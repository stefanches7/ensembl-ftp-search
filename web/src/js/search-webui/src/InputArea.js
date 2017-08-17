import React, {Component} from "react";
import "./InputArea.css";
import {SearchHelper} from "./App";

/**
 * Component of the user input in the application
 */
class InputArea extends Component {
    handleReferenceSelection = (e, key) => {
        let currElemData = this.state.currentElementsData;
        currElemData[key].reference = e.target.value;
        this.setState({currentElementsData: currElemData});
    };
    handleValueChange = (newValue, key, taxaId) => {
        let currElemData = this.state.currentElementsData;
        if (taxaId && currElemData[key].reference === "Taxonomy branch") {
            //put taxonomy id as a value of the filter
            currElemData[key].value = taxaId;
        } else {
            currElemData[key].value = newValue;
        }
        this.setState({currentElementsData: currElemData});
    };
    removeFilter = (key) => {
        if (key == 0) {
            return;
        }
        console.debug("Removing filter with the key: " + key);
        let activeFiltersNow = this.state.activeFilterElements;
        delete activeFiltersNow[key];
        let currElemData = this.state.currentElementsData;
        delete currElemData[key];
        this.setState({activeFilterElements: activeFiltersNow, currentElementsData: currElemData});
    };
    addFilter = () => {
        console.debug("Adding filter with initial data.");
        let activeFiltersNow = this.state.activeFilterElements;
        activeFiltersNow[this.state.filterCounter] =
            <Filter key={this.state.filterCounter} listId={this.state.filterCounter}
                    onSelect={(e, key) => this.handleReferenceSelection(e, key)}
                    onChange={(newValue, key, taxaId) => this.handleValueChange(newValue, key, taxaId )}
                    onClickDelete={(key) => this.removeFilter(key)} />;
        let currElemData = this.state.currentElementsData;
        currElemData[this.state.filterCounter] = InitialHelper.getInitialFilterData();
        this.setState({
            activeFilterElements: activeFiltersNow,
            currentElementsData: currElemData,
            filterCounter: this.state.filterCounter + 1
        });
    };

    constructor(props) {
        super(props);
        let initialListId = 0;
        this.state = {
            filterCounter: 1, activeFilterElements:
                [<Filter isFirst="true" listId={initialListId} onSelect={this.handleReferenceSelection}
                         onChange={this.handleValueChange} onClickDelete={(key) => this.removeFilter(key)} />],
            currentElementsData: {0: InitialHelper.getInitialFilterData(initialListId)}
        };
    }

    render() {
        return (<div>
            <FilterList activeFilterElements={this.state.activeFilterElements} />
            <AddFilterButton onclick={this.addFilter} />
            <SearchButton
                onclick={(e) => this.props.onSearchClicked(e, Object.values(this.state.currentElementsData))} />
        </div>);
    }
}

class FilterList extends Component {
    render() {
        return (<table>
            {this.props.activeFilterElements}
        </table>);
    }
}

class Filter extends Component {
    loadSuggestionsAndUpstreamEvent = (newValue, key) => {
        if (this.state.reference == "Organism name" || this.state.reference == "File type") {
            this.loadLocalSuggestions(newValue, key);
        }
        else if (this.state.reference == "Taxonomy branch") {
            this.loadOLSValueSuggestions(newValue, key);
        }
    };
    loadLocalSuggestions = (newValue, key) => {
        this.props.onChange(newValue, key, null);
    };
    loadOLSValueSuggestions = (newValue, key) => {
        if (newValue === "") {
            this.setState({suggestions: []});
            return;
        } //nothing is entered
        let url = "https://www.ebi.ac.uk/ols/api/select?q=" + newValue + "&ontology=ncbitaxon&fieldList=obo_id,label";
        let xhr = SearchHelper.createCORSRequest('GET', url);
        if (!xhr) {
            console.log("Was unable to create taxa suggestion CORS. Perhaps, it is not supported by the browser.");
            return;
        }
        let filterObj = this; //use Filter object in onload callback
        xhr.onload = function () {
            let suggJSON = JSON.parse(xhr.responseText);
            let newTaxaSugg = [];
            for (let autosuggObj of suggJSON.response.docs) {
                newTaxaSugg.push(autosuggObj);
            }
            filterObj.setState({suggestions: newTaxaSugg});
            filterObj.props.onChange(newValue, key, filterObj.getSuggestedTaxaId(newValue))
        };
        xhr.send();
    };
    updateReference = (e) => {
        let reference = e.target.value;
        if (reference !== this.state.reference) { //clean suggestions on new selection
            this.setState({reference: reference, suggestions: []});
        }
    };
    getSuggestedTaxaId = (value) => {
        if (this.state.reference != "Taxonomy branch") { return; }
        let taxaId = null;
        let taxaIdSingleton = this.state.suggestions.map((suggObj) => {
            if (suggObj.label === value) {
                return suggObj.obo_id.substring(10) //-"NCBITaxon:"
            }});
        if (taxaIdSingleton[0] === undefined && this.state.suggestions.length > 0) {
            console.debug("Didn't match any suggestion, taking first as the right one.");
            let firstSugg = this.state.suggestions[0];
            taxaId = firstSugg.obo_id.substring(10); //-"NCBITaxon:"
        } else {taxaId = taxaIdSingleton[0]}
        return taxaId;
    };

    constructor(props) {
        super(props);
        this.state = InitialHelper.getInitialFilterData();
    }

    render() {
        return <div className="filterContainer">
            {this.props.isFirst ? '' : <FilterPrefix />}
            <FilterReference onSelect={(e, key) => {
                this.props.onSelect(e, key);
                this.updateReference(e)}} listId={this.props.listId} />
            <FilterAssignment />
            <FilterValue onChange={(newValue, key) => {
                this.loadSuggestionsAndUpstreamEvent(newValue, key)}}
                         listId={this.props.listId}
                         suggestions={this.state.suggestions.map((suggObj) => suggObj.label)} />
            {this.props.isFirst ? '' :
                <DeleteFilterButton onClick={() => this.props.onClickDelete(this.props.listId)} />}
        </div>
    }
}

function FilterPrefix() {
    return <div>and</div>;
}

function FilterReference(props) {
    const availableFilters = ["Organism name", "File type", "Taxonomy branch"];
    return <select required={true} onChange={(e) => {
        props.onSelect(e, props.listId)
    }}>
        {availableFilters.map((filterName) => <option>{filterName}</option>)}
    </select>;
}

function FilterAssignment() {
    return <div>eq</div>;
}

class FilterValue extends Component {
    render() {
        let i = 0;
        return <div>
            <input onChange={(e) => {
                this.props.onChange(e.target.value, this.props.listId)
            }}
                   list={`suggestions-${this.props.listId}`} />
            <datalist id={`suggestions-${this.props.listId}`}>
                {this.props.suggestions.map((suggestion) => {
                    i++;
                    return <option key={i}>{suggestion}</option>
                })}
            </datalist>
        </div>
    }
}

class DeleteFilterButton extends Component {
    render() {
        return <button className="deleteFilterButton" onClick={(e) => this.props.onClick()}>X delete</button>;
    }
}

class AddFilterButton extends Component {
    render() {
        return <button id="addFilterButton" onClick={this.props.onclick}><b>+ Add filter</b></button>;
    }
}

class SearchButton extends Component {
    render() {
        return <button id="searchButton" onClick={this.props.onclick}>
            <b>0-Search!</b>
        </button>;
    }
}

class InitialHelper {
    static getInitialFilterData() {
        return {reference: "Organism name", value: "", suggestions: []};
    }
}

export default InputArea;