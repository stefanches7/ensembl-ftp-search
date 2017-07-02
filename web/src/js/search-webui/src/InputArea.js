import React, {Component} from "react";

/**
 * Component of the user input in the application
 */
class InputArea extends Component {
    render() {
        return (<p>
            <FilterList />
            <AddFilter />
        </p>);
    }
}

class FilterList extends Component {
    constructor() {
        super();
        this.state ={};
    }
    render() {
        return (<table>
                <Filter isFirst="true"/>
                {this.state.filters}
            </table>);
    }
}

class Filter extends Component {
    render() {
        return <tr>
            {this.props.isFirst? '':<FilterPrefix />}
            <FilterReference />
            <FilterAssignment />
            <FilterValue />
        </tr>
    }
}

function FilterPrefix() {
    return <div>or</div>;
}

function FilterReference() {
    return <select>
        <option>OrganismName</option>
    </select>;
}

function FilterAssignment() {
    return <div>eq</div>;
}

class FilterValue extends Component{
    render() {
        return <input/>
    }
    loadSuggestions(reference) {
    }
}

class AddFilter extends Component {
    render() {
        return <button><b>+ Add filter</b></button>;
    }
}

export default InputArea;