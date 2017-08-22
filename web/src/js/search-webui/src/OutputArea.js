import React, {Component} from "react";

/**
 * Results output.
 */
class OutputArea extends Component {
    render() {
        return <div>
        <LinkTable fileLinks={this.props.fileLinks} />
        </div>;
    }
}

/**
 * Table containing all the link results of the latter search.
 */
class LinkTable extends Component {
    renderListLine = (line) => {
        if (line.indexOf("ftp.ensembl") !== -1 ) { //line is actually an ftp site link
            return <tr><a className="ftpFileLink" href={"ftp://" + line}>{line}</a></tr>
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

/** FIXME: implement buttons that trigger download of the whole page/whole set at once!
class DownloadPageButton extends Component {

    downloadPageClicked = () =>{
        let fileAnchors = document.getElementsByClassName("ftpFileLink");
        for (let fileAnchor of fileAnchors) {
            this.downloadEnsFTPFile(fileAnchor.innerHTML.textContent);      
        }
    };
    downloadEnsFTPFile = (fileurl) => {
        let destinationDir = "C:\\Users\\xxx-m\\Downloads";
        if (fileurl.indexOf("ensemblgenomes") !== -1){
           this.EGFtp.get(fileurl.substring(fileurl.indexOf("/")), destinationDir + "\\" +
               fileurl.substring(fileurl.lastIndexOf("/")), (hadErr)=> {
               if (hadErr) {
                   console.log("Error retrieving FTP file.");
               }
               else {
                   console.log("FTP file retrieved successfully!");
               }
           })
        }
    };

    constructor() {
        super();
        var JSFtp = require("jsftp");
        var EFtp = new JSFtp({host:"ftp.ensembl.org"});
        this.EGFtp = new JSFtp({host: "ftp.ensemblgenomes.org"});
    }

    render() {
        return <button onClick={this.downloadPageClicked()}>Download files on page</button>
    }
}*/

export default OutputArea;