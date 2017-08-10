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
    handleValueChange = (e, key, taxaId) => {
        let currElemData = this.state.currentElementsData;
        if (taxaId) {
            //put taxonomy id as a value of the filter
            currElemData[key].value = taxaId;
        } else {currElemData[key].value = e.target.value;}
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
                    onChange={(e, key) => this.handleValueChange(e, key)}
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
        let values = Object.values(this.state.currentElementsData);
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
    loadSuggestions = (e) => {
        let value = e.target.value;
        console.log("Value was " + value);
        this.setState({value: value});
        if (this.state.reference == "Organism name" || this.state.reference == "File type") {
            this.loadLocalSuggestions(value);
        }
        else if (this.state.reference == "Taxonomy branch") {
            this.loadOLSValueSuggestions(value);
        }
    };
    loadLocalSuggestions = (value) => {
        //FIXME
    };
    loadOLSValueSuggestions = (value) => {
        if (value === "") {
            this.setState({suggestions: []});
            return;
        } //nothing is entered
        let url = "https://www.ebi.ac.uk/ols/api/select?q=" + value + "&ontology=ncbitaxon&fieldList=obo_id,label";
        let xhr = SearchHelper.createCORSRequest('GET', url);
        if (!xhr) {
            console.log("Was unable to create taxa suggestion CORS. Perhaps, it is not supported by the browser.");
            return;
        }
        let filterObj = this;
        xhr.onload = function () {
            let suggJSON = JSON.parse(xhr.responseText);
            let newTaxaSugg = [];
            for (let autosuggObj of suggJSON.response.docs) {
                newTaxaSugg.push(autosuggObj);
            }
            filterObj.setState({suggestions: newTaxaSugg});
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
        let taxaId = 0;
        let taxaIdSingleton = this.state.suggestions.map((suggObj) => {
            if (suggObj.label === value) {
                return suggObj.obo_id.substring(11) //-"NCBITaxon:"
            }});
        if (taxaIdSingleton.length < 1) {
            taxaId = this.state.suggestions[0].obo_id.substring(11) //-"NCBITaxon:"
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
            <FilterValue onChange={(e, key) => {
                this.loadSuggestions(e);
                this.props.onChange(e, key, getSuggestedTaxaId(e.target.value))}}
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
                this.props.onChange(e, this.props.listId)
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