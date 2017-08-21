import React, {Component} from "react";

/**
 * Results output.
 */
class OutputArea extends Component {
    render() {
        return <LinkTable fileLinks={this.props.fileLinks} />;
    }
}

/**
 * Table containing all the link results of the latter search.
 */
class LinkTable extends Component {
    render() {
        return <table>
            <tr>Links satisfying your filters:</tr>
            {this.props.fileLinks.map((link) => <tr>{link}</tr>)}
        </table>
    }
}

export default OutputArea;