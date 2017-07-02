import React, {Component} from "react";

/**
 * Output component in the application.
 */
class OutputArea extends Component{
    render() {
        return <LinkTable />;
    }
}

class LinkTable extends Component {
    constructor() {
        super();
        this.state = {links: ["http://youspitfirefool.com"]};
    }
    render() {
        let links = [];
        for (let link of this.state.links) {
            links.push(<Link link={link}/>);
        }
        return <table>
            <tr>Links satisfying your filters:</tr>
            {links}
        </table>
    }
}

function Link(props) {
    return <tr>{props.link}</tr>;
}

export default OutputArea;