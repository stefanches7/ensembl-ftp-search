import React, {Component} from "react";

/**
 * Output component in the application.
 */
class OutputArea extends Component {
    render() {
        return <LinkTable fileLinks={this.props.fileLinks} />;
    }
}

class LinkTable extends Component {
    render() {
        return <table>
            <tr>Links satisfying your filters:</tr>
            {this.props.fileLinks.map((link) => <tr>{link}</tr>)}
        </table>
    }
}

export default OutputArea;