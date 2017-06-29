# Ensembl project FTP search API ![alt link](https://travis-ci.org/stefanches7/ensembl-ftp-search.svg?branch=master)
Various end-user tools for searching the FTP sites of the Ensembl project.

Setup & requirements
--------------------
Following software is required to run the server:
* JDK1.8
* MySQL
* Gradle

Gradle can be automatically installed via Gradle Wrapper, that is:

Linux: 
```bash
ensembl-ftp-search~$ ./gradlew
```

Windows:
```cmd
ensembl-ftp-search> exec gradlew.bat

```

Please refer to the Gradle documentation for more instructions.

Configuration
-------------

All the important configuration values are specified in the `application.properties` file located in the src/main/resources folder.

Usage
=====
Starting the server
-------------------
The application requires a running MySQL database to work with.
Please refer to the `spring.datasource` group values in `src\main\resorces\application.properties` file
to see and/or specify the database connection credentials.

Since application is built with Spring boot framework, it's possible
to start it with a simple gradle task:

`gradle bootRun`

However it's also possible to start it vanilla gradle core way:

```sbtshell
gradle build
java -jar build/libs/ensembl-ftp-search-XXXXX.jar <options>
```

The server starts at `localhost:8080` by default, which, however, can be changed in `application.properties`.

HTTP requests
-------------

API can be accessed through HTTP requests to the various endpoints:

* `/search` - the end-point for searching, exact description down the page.
* `/addNew` - adds organism to the specified database. Please note that this is a debug feature and will be removed on the release.
* `/findAll` - sends all the database records in form of JSON objects. This feature is also debug.

Searching
---------
`/search` endpoint provides the interface to search the database applying all the filters specified. You are to specify the filters either in camelCase form or with_underscores_between_words, i.e. both `organismName` and `organism_name` are accepted. The API warns about the filters that were not applied (because they were invalid) in the HTTP response and intersects all the filters that were valid. 
Your response contains all the links that are relevant to the specified search filters' combination.

Right now the available filters are:

* `organismName`
* `fileType`

_Examples_: 

`<ip>:<port>/search?organismName=Bushbaby&file_type=.vep`

`curl <ip> <port> -d organism_name='Bushbaby' -d fileType='.vep'`

Add new
-------
For adding new organism, please specify all the parameters: `link_url`, `file_type` and `organism_name`. Malformed URLs are not accepted.

