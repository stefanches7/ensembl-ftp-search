import React, {Component} from "react";
import "./InputArea.css";

/**
 * Component of the user input in the application
 */
class InputArea extends Component {
    constructor(props) {
        super(props);
        let initialListId = 0;
        this.state = { filterCounter:1, activeFilterElements :
            [<Filter isFirst="true" listId={initialListId} onSelect={this.handleReferenceSelection} onChange={this.handleValueChange} onClickDelete={(key) => this.removeFilter(key)}/>],
            currentElementsData : {0: InitialHelper.getInitialFilterData(initialListId)}};
    }
    handleReferenceSelection = (e,key) => {
        let currElemData = this.state.currentElementsData;
        currElemData[key].reference = e.target.value;
        this.setState({currentElementsData: currElemData});
    };
    handleValueChange = (e, key) => {
        let currElemData = this.state.currentElementsData;
        currElemData[key].value  = e.target.value;
        this.setState({currentElementsData: currElemData});
    };
    removeFilter = (key) => {
        if (key==0) {
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
        activeFiltersNow[this.state.filterCounter] = <Filter listId={this.state.filterCounter} onSelect={(e, key) => this.handleReferenceSelection(e, key)}
                                      onChange={(e,key) =>this.handleValueChange(e, key)} onClickDelete={(key)=>this.removeFilter(key)}/>;
        let currElemData = this.state.currentElementsData;
        currElemData[this.state.filterCounter] = InitialHelper.getInitialFilterData();
        this.setState({activeFilterElements: activeFiltersNow, currentElementsData: currElemData, filterCounter: this.state.filterCounter + 1});
    };
    render() {
        let values = Object.values(this.state.currentElementsData);
        return (<div>
            <FilterList activeFilterElements={this.state.activeFilterElements}/>
            <AddFilterButton onclick={this.addFilter}/>
            <SearchButton onclick={(e) => this.props.onSearchClicked(e, Object.values(this.state.currentElementsData))} />
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
    render() {
        return <div className="filterContainer">
            {this.props.isFirst? '':<FilterPrefix />}
            <FilterReference onSelect={(e,key) => this.props.onSelect(e,key)} listId={this.props.listId}/>
            <FilterAssignment />
            <FilterValue onChange={(e,key) => this.props.onChange(e,key)} listId={this.props.listId}/>
            {this.props.isFirst?'':<DeleteFilterButton onClick={() => this.props.onClickDelete(this.props.listId)}/>}
        </div>
    }
}

function FilterPrefix() {
    return <div>and</div>;
}

function FilterReference(props) {
    const availableFilters = ["Organism name", "File type"];
    return <select required={true} onChange={(e) => { props.onSelect(e,props.listId)}} key={props.listId}>
        {availableFilters.map((filterName) => <option>{filterName}</option>)}
    </select>;
}

function FilterAssignment() {
    return <div>eq</div>;
}

class FilterValue extends Component{
    render() {
        return <input onChange={(e) => this.props.onChange(e,this.props.listId)} key={this.props.listId}/>
    }
    loadSuggestions(reference) {
    }
}
class DeleteFilterButton extends Component {
    render() {
        return <button className="deleteFilterButton" onClick={(e)=>this.props.onClick()}>X delete</button>;
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
        return {reference: "Organism name", value: ""};
    }
}

export default InputArea;