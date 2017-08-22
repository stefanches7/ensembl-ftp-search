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
    renderListLine = (line) => {
        if (line.indexOf("ftp.ensembl") !== -1 ) { //line is actually an ftp site link
            return <tr><a href={"ftp://" + line}>{line}</a></tr>
        } else {
            return <tr>{line}</tr>;
        }
    };
    render() {
        return <table>
            <tr>Links satisfying your filters:</tr>
            {this.props.fileLinks.map((line) => this.renderListLine(line))}
        </table>
    }
}

export default OutputArea;