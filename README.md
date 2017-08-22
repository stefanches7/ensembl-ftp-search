# Ensembl/Ensembl Genomes FTP search API ![alt link](https://travis-ci.org/stefanches7/ensembl-ftp-search.svg?branch=master)
HTTP and JS interfaces for searching the FTP sites of Ensembl/Ensembl Genomes

Summary
-------
This API is a comfortable way to get the links to the files needed from [Ensembl FTP site] and 
[EnsemblGenomes FTP site]. I.e., having entered e.g. organism release name/taxonomy branch 
and data type you are interested in (say, "Sophophora" and "vep") you'll get all the file links to the files matching 
your query (in this case all the links leading to "vep" files for all species that are Sophophora).

[Ensembl FTP site]: ftp.ensembl.org
[EnsemblGenomes FTP site]: ftp.ensemblgenomes.org

Usage
-----
### HTTP interface

API can be accessed through HTTP requests to the various endpoints:

* `/hello` - "Hello World!" healthcheck
* `/search` - the main end-point for searching.
* `/organismNameSuggestion`/`/fileTypeSuggestion` - pattern search for distinct organism_names/file_types in the local 
database (links to organism names and file types database).

#### Searching

`/search` endpoint provides the interface to search the database applying all the filters specified. As of now, it will 
_intersect_ all the filters you have specified and return the _Java-like_ list of links that match your whole query (i.e.,
list will look like \[\<link1\>, \<link2\>...\]).

The available filters are:

* `organismName` - organism _release_ name (lowercase, with underscores, e.g. "drosophila_melanogaster")
* `fileType` - dataType, e.g. "vep" or "fasta_cdna" (fasta subtype after underscore)
* `taxaBranch` - taxonomy _id_ (_number_) as of NCBI Taxonomy database (e.g. "Drosophila melanogaster" has id 7227)

Please note that `page` and `size` parameters are available for **paging** (they should also be _numbers_)

_Examples_: 

`<ip>:<port>/search?taxaBranch=7227&file_type=vep&page=1&size=20` - vep files for Drosophila melanogaster, second page
of 20 links (links 20-39 from the result)

`curl <ip> <port> -d organism_name='drosophila_melanogaster' -d fileType='vep'` - same, but the whole result set this time

### Javascript interface

There is also a JS interface available, see `web\src\js\search-webui`.

## Setup & configuration
### Requirements

Following software is required to run the HTTP server:
* **JDK1.8**
* **MySQL**5.5+
* *Gradle* \*

\* Gradle can be automatically installed via Gradle Wrapper, that is:

Linux: 
```bash
ensembl-ftp-search~$ ./gradlew
```

Windows:
```cmd
ensembl-ftp-search> exec gradlew.bat

```

Javascript user interface was built using *React* libraries and is therefore dependent on **Node** and `npm`.

Update job requires *Perl 5.24+* to run.

### Configuration

Spring Boot application is configured using `main/resources/application.properties` file in the Java sources root **or** passing the command line arguments, e. g. `java -jar build/libs/ensembl-ftp-search-XXXXX.jar --server.port 9988`. See [Spring documentation] for a comprehensive list of Spring Boot options available.
Please _pay special attention_ to the `spring.datasource` group values, as they should point to **running and accessible
MySQL database**. Without it, the application won't be able to start.

[Spring documentation]: https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

### Startup

HTTP server is started with `./gradlew bootRun` command (don't worry about having Gradle already installed, Gradle Wrapper
won't download any other version then).

### Logging 

HTTP server logs into the command line from which it was started.
