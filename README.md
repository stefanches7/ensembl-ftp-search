# Ensembl project FTP search API ![alt link](https://travis-ci.org/stefanches7/ensembl-ftp-search.svg?branch=master)
Various end-user tools for searching the FTP sites of the Ensembl project.

Setup & requirements
--------------------
Following software is required to run the server:
* JDK1.8
* MySQL
* Gradle

Gradle can be automatically installed via Gradle Wrapper, that is:
Linux 
```bash
ensembl-ftp-search $ ./gradlew
```
Windows
````cmd
ensembl-ftp-search> exec gradlew.bat
```

Please refer to gradle documentation for more instructions.

Configuration
-------------

All the important configuration values are specified in the `application.properties` file located in the src/main/resources folder.
