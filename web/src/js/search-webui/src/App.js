import React, {Component} from "react";
import "./App.css";
import InputArea from "./InputArea";
import OutputArea from "./OutputArea";

class App extends Component {
    constructor() {
        super();
        this.state = {renderedLinks: ["http://youspitfirefool.com"]}
    }
  initiateRemoteAsyncSearch = (e, currentElementsData) => {
      let searchQuery = SearchHelper.buildSearchQuery(...currentElementsData);
      let successCallback = (responseText) => {console.log("Response looked like " + responseText);
      this.setState({renderedLinks: SearchHelper.parseLinksList(responseText)})};
      let failureCallback = (statusText) => {console.log("Error in request: " + statusText);
      this.setState({renderedLinks: [statusText]})};
      SearchHelper.asyncSearchGet(searchQuery, successCallback, failureCallback);
  };
  render() {
    return (<p>
            <InputArea onSearchClicked={(e, currentElementsData) => this.initiateRemoteAsyncSearch(e, currentElementsData)}/>
            <hr/>
            <OutputArea fileLinks={this.state.renderedLinks}/>
        </p>
    );
  }
}

const paramNameDict = {"Organism name": "organismName", "File type": "fileType"};
class SearchHelper {
    static get paramNameDict() {
        return paramNameDict;
    }
    static convertToReqFormat(paramName, paramValue) {
        return this.paramNameDict[paramName] + "=" + paramValue;
    }

    static buildSearchQuery(...dataElements) {
        let searchQuery = "";
        for (let dataElement of dataElements) {
            if (!dataElement.value) {
                continue;
            }
            searchQuery += SearchHelper.convertToReqFormat(dataElement.reference, dataElement.value);
            if (dataElements.indexOf(dataElement) !== dataElements.length -1 ) {
                searchQuery += "&";
            }
        }
        return searchQuery;
    }

    /**
     * Creates CORS API request that extends XMLHTTPRequest to be cross-origin.
     *
     * @param method HTTP method of the request
     * @param url request url
     * @returns {XMLHttpRequest} ready CORS request. Null if CORS is not supported.
     */
    static createCORSRequest(method, url) {
        let xhr = new XMLHttpRequest();
        if ("withCredentials" in xhr) {

            // Check if the XMLHttpRequest object has a "withCredentials" property.
            // "withCredentials" only exists on XMLHTTPRequest2 objects.
            xhr.open(method, url, true);

        } else if (typeof XDomainRequest != "undefined") {

            // Otherwise, check if XDomainRequest.
            // XDomainRequest only exists in IE, and is IE's way of making CORS requests.
            xhr = new XDomainRequest();
            xhr.open(method, url);

        } else {

            // Otherwise, CORS is not supported by the browser.
            xhr = null;

        }
        return xhr;
    }

    static asyncSearchGet(searchQuery, successCallback, failureCallback) {
        const url = "http://localhost:8080/search?" + searchQuery;
        let req = SearchHelper.createCORSRequest('GET',url);
        if (!req) {
            failureCallback("CORS, an XMLHTTPRequest cross-origin extension, is not supported by your browser." +
                " Please" +
                " try another user-agent");
        }
        console.log(req);
        req.onload = function () {
            console.log("Success!");
            successCallback(req.responseText)
        };
        req.onerror = function () {
            console.log("Failure!");
            failureCallback(req.statusText)
        };
        req.send();
    }

    static parseLinksList(responseText) {
        let responseStrip = responseText.toString().replace(/[\[\]'"]+/g, '');
        return responseStrip.split(",");
    }
}

export default App;
