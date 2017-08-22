# Javascript User Interface for searching the Ensembl FTP sites

## Description

This interface acts merely like a step-between the HTTP interface and user to make searching
the FTP sites even more comfortable. Some users may prefer JS iinterface over HTTP interface because of *value suggestions*
using the OLS API of EMBL-EBI, what helps to get rid of many misspelling errors and mistakes. 
Users can end unlimited amount of the same search filters that are used in the essential HTTP interface (i.e., "Organism
name", "Taxonomy branch", "File type"), but are warned that not any taxonomy branch that is typed and even suggested is going
to deliver any results - you will be however warned, if there are no organisms in the API database that correspond
to the specified taxonomy branch. In this interface paging is also available.

## Configuration 

`config.js` contains the most important values needed to run the application.

## Start & access
JS webserver (standalone) is started with `npm start` _from the root directory_ (currently `web\src\js\search_webui`). *Please note* that this application is written using the newest Javascript language standards and it is therefore _highly recommended to update your user-agent_ to the newest version possible before accessing the application.

### Logging

The application logs most important values in user-agent's console (e.g. Dev Tools in Chrome). There are also some more infos logged using `console.debug()`.
