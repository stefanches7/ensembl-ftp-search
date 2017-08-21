import React, {Component} from "react";
import "./InputArea.css";
import {SearchHelper} from "./App";
import {appconfig} from "./config"

/**
 * Everything that has to do with specifying or activating the search.
 */
class InputArea extends Component {
    /**
     * Filter reference changed callback.
     * @param e change event
     * @param key Filter's unique id
     */
    handleReferenceSelection = (e, key) => {
        let currElemData = this.state.currentElementsData;
        currElemData[key].reference = e.target.value;
        this.setState({currentElementsData: currElemData});
    };
    /**
     * Filter value changed callback.
     * @param newValue updated value of the fiter
     * @param key filter's unique id
     * @param taxaId taxonomy id of selected taxa branch, null for N/A references
     */
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
    onPageSizeChanged = (newValue) => {
        this.setState({pageSize: newValue});
    };
    onPageNoChanged = (newValue) => {
        this.setState({pageNo: newValue - 1});
    };
    /**
     * Hit when filter is deleted.
     * @param key filter's unique id
     */
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
    /**
     * Hit when new filter is added.
     */
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
            currentElementsData: {0: InitialHelper.getInitialFilterData(initialListId)}, pageSize: 5, pageNo: 0
        };
    }

    render() {
        return (<div>
            <FilterList activeFilterElements={this.state.activeFilterElements} />
            <AddFilterButton onclick={this.addFilter} />
            <SearchButton
                onclick={(e) => {
                    let currData = Object.values(this.state.currentElementsData);
                    currData.push({reference: "Page number", value: this.state.pageNo}, {reference: "Page size", value:
                    this.state.pageSize}); //append paging data
                    this.props.onSearchClicked(e, currData);
                }} />
                <div>
                    Page number:
                    <PageNo onChange={(e) => this.onPageNoChanged(e.target.value)} />
                    Page size:
                    <PageSize onChange={(e) => this.onPageSizeChanged(e.target.value)} />
                </div>
            </div>);
    }
}

/**
 * Filters wrapper.
 */
class FilterList extends Component {
    render() {
        return (<table>
            {this.props.activeFilterElements}
        </table>);
    }
}

/**
 * Filter component.
 */
class Filter extends Component {
    /**
     * Hit the right method of loading suggestions according to filter's reference
     * @param newValue updated filter's value
     * @param key filter's unique id
     */
    loadSuggestionsAndUpstreamEvent = (newValue, key) => {
        if (this.state.reference == "Organism name" || this.state.reference == "File type") {
            this.loadLocalSuggestions(newValue, key);
        }
        else if (this.state.reference == "Taxonomy branch") {
            this.loadOLSValueSuggestions(newValue, key);
        }
    };
    /**
     * Load suggestions using OLS API. Applicable for taxonomy branch filter.
     * @param newValue updated taxonomy branch filter's value
     * @param key filter's unique id
     */
    loadOLSValueSuggestions = (newValue, key) => {
        if (newValue === "") {
            this.setState({suggestions: []});
            return;
        } //nothing is entered
        let url = appconfig.olsApiUrlSelect + "q=" + newValue + "&ontology=ncbitaxon&fieldList=obo_id,label";
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
    /**
     * Load suggestions using own HTTP interface's search database (applicable for organism name and file type).
     * @param newValue updated filter's value
     * @param key filter's unique id
     */
    loadLocalSuggestions = (newValue, key) => {
        if (newValue === "") {
            this.setState({suggestions: []});
            return;
        } //nothing is entered
        let url = appconfig.httpSearchInterfaceUrl + "/" + SearchHelper.paramNameDict[this.state.reference]+"Suggestion?value=" + newValue;
                                                                //construct endpoint with current filter reference,
        // e.g. <serverurl>/organismNameSuggestion?value=<value>
        let xhr = SearchHelper.createCORSRequest('GET', url);
        if (!xhr) {
            console.log("Was unable to create local suggestion CORS. Perhaps, it is not supported by the browser.");
            return;
        }
        let filterObj = this; //act upon this Filter object in onload callback
        xhr.onload = function () {
            let responseStrip = xhr.responseText.toString().replace(/[\[\]'"]+/g, '');
            let newSugg = [];
            for (let suggestion of responseStrip.split(",")) {
                newSugg.push({label: suggestion, obo_id: ""}); //adhere to taxa suggestions format
            }
            filterObj.setState({suggestions: newSugg});
            filterObj.props.onChange(newValue, key, null);
        };
        xhr.send();
    };
    /**
     * Update current state on filter reference's update.
     * @param e update event
     */
    updateReference = (e) => {
        let reference = e.target.value;
        if (reference !== this.state.reference) { //clean suggestions on new selection
            this.setState({reference: reference, suggestions: []});
        }
    };
    /**
     * Upstream current taxa id by filter's value specified. *Takes the first suggestion in list if specified value
     * itself doesn't correspond to any taxa id.
     * 
     * @param value
     * @returns {*}
     */
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

/**
 * Filter prefix. Fixme: change to accept other logical functions (e.g. or, not).
 * @returns {XML} 
 * @constructor
 */
function FilterPrefix() {
    return <div>and</div>;
}

/**
 * Filter's type.
 * @param props HTML props & upstream callbacks.
 * @returns {XML} render
 * @constructor
 */
function FilterReference(props) {
    const availableFilters = ["Organism name", "File type", "Taxonomy branch"];
    return <select required={true} onChange={(e) => {
        props.onSelect(e, props.listId)
    }}>
        {availableFilters.map((filterName) => <option>{filterName}</option>)}
    </select>;
}

/**
 * FIXME: implement other assignments, e.g. "not equals"
 * @returns {XML}
 * @constructor
 */
function FilterAssignment() {
    return <div>eq</div>;
}

/**
 * Filter's value input component. Turns into not-required list when something is typed (suggestions are loaded).
 */
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

/**
 * Deletes the filter in the corresponding row and removes its current state record.
 */
class DeleteFilterButton extends Component {
    render() {
        return <button className="deleteFilterButton" onClick={(e) => this.props.onClick()}>X delete</button>;
    }
}

/**
 * Appends new filter to the end of the list and creates new current state data record.
 */
class AddFilterButton extends Component {
    render() {
        return <button id="addFilterButton" onClick={this.props.onclick}><b>+ Add filter</b></button>;
    }
}

/**
 * Activates interface search when hit, see `./App.js`.
 */
class SearchButton extends Component {
    render() {
        return <button id="searchButton" onClick={this.props.onclick}>
            <b>0-Search!</b>
        </button>;
    }
}

/**
 * Used for result paging. Specify size of the result page.
 */
class PageSize extends Component {
    render() {
        return <select required={true} onChange={(e) => this.props.onChange(e)}>
            <option>5</option>
            <option>10</option>
            <option>20</option>
            <option>50</option>
            <option>100</option>
        </select>
    }
}

/**
 * Used for result paging. Specify number of the result page.
 */
class PageNo extends Component {
    render() {
        return <input type="number" onChange={(e) => this.props.onChange(e)}>
        </input>
    }
}

/**
 * Helps with defaults.
 */
class InitialHelper {
    static getInitialFilterData() {
        return {reference: "Organism name", value: "", suggestions: []};
    }
}

export default InputArea;