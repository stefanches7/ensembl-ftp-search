import React, {Component} from "react";
import "./App.css";
import InputArea from "./InputArea";
import OutputArea from "./OutputArea";
import {appconfig} from "./config";

class App extends Component {
    /**
     * speak to HTTP interface's /search endpoint
     */
    initiateRemoteAsyncSearch = (e, currentElementsData) => {
        let searchQuery = SearchHelper.buildSearchQuery(...currentElementsData);
        let successCallback = (responseText) => {
            console.debug("Success! Response looked like " + responseText);
            this.setState({renderedLinks: SearchHelper.parseLinksList(responseText)})
        };
        let failureCallback = (statusText) => {
            console.debug("Error in request: " + statusText);
            this.setState({renderedLinks: [statusText]})
        };
        SearchHelper.asyncSearchGet(searchQuery, successCallback, failureCallback);
    };

    constructor() {
        super();
        this.state = {renderedLinks: ["You haven't searched for anything yet."]}
    }
    
    render() {
        return (<p>
                <InputArea onSearchClicked={(e, currentElementsData) => this.initiateRemoteAsyncSearch(e,
                    currentElementsData)} />
                <hr />
                <OutputArea fileLinks={this.state.renderedLinks} />
            </p>
        );
    }
}

//map human-readable param names to HTTP interface convention
const paramNameDict = {"Organism name": "organismName", "File type": "fileType", "Taxonomy branch": "taxaBranch",
    "Page size": "size", "Page number": "page"};

export class SearchHelper {
    static get paramNameDict() {
        return paramNameDict;
    }

    /**
     * Convert to a search term in query
     * @param paramName
     * @param paramValue
     * @returns {string} "paramName=paramValue"
     */
    static convertToReqFormat(paramName, paramValue) {
        return this.paramNameDict[paramName] + "=" + paramValue;
    }

    /**
     * Convert current DOM data to HTTP search query
     * @param dataElements objects artis {reference: <>, value: <>} holding infos about current app state
     * @returns {string} HTTP search query artis "param1=value1&param2=value2..."
     */
    static buildSearchQuery(...dataElements) {
        let searchQuery = "";
        for (let dataElement of dataElements) {
            console.debug("Seeing filter data element:" + dataElement.reference + ", value: " + dataElement.value);
            if (!dataElement.value) {
                continue;
            }
            searchQuery += SearchHelper.convertToReqFormat(dataElement.reference, dataElement.value);
            if (dataElements.indexOf(dataElement) !== dataElements.length - 1) {
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

    /**
     * Launch search request to the HTTP server
     * 
     * @param searchQuery from .buildSearchQuery()
     * @param successCallback called on successful request
     * @param failureCallback called on request's failure
     */
    static asyncSearchGet(searchQuery, successCallback, failureCallback) {
        const url = appconfig.httpSearchInterfaceUrl + appconfig.httpSISearchEndp + searchQuery;
        console.debug("Search query was " + searchQuery);
        let req = SearchHelper.createCORSRequest('GET', url);
        if (!req) {
            failureCallback("CORS, an XMLHTTPRequest cross-origin extension, is not supported by your browser." +
                " Please" +
                " try another user-agent");
        }
        req.onload = function () {
            successCallback(req.responseText)
        };
        req.onerror = function () {
            console.debug("Async search has failed!");
            failureCallback(req.statusText)
        };
        req.send();
    }

    /**
     * Convert Java-style list of links to array of pure links
     * @param responseText list artis [<link1>, <link2, ...]
     * @returns {Array} array of separate links s
     */
    static parseLinksList(responseText) {
        let responseStrip = responseText.toString().replace(/[\[\]'"]+/g, '');
        return responseStrip.split(",");
    }
}

export default App;
