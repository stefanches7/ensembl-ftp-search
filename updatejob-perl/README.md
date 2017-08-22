# Perl update job of the file search database

## Description

This job updates indices of the database icluding:

* `link` table (main table connecting all the files on FTP site with corresponding organism names, file types etc.)
* suggestions table like `organism_name_suggestion` etc.

It is run as a simple Perl script using UpdateSearchDB.pl as an entry point (i.e., `~$ perl UpdateSearchDB.pl <parameters>`). 
Parameters are:

* dbname - name of database _being updated_.
* dbport - port of this database.
* dbhost - IP on which this database is hosted.
* dbuser, dbpssw - this database's access credentials.
* entrypoint - entrypoint(s) to start recursive crawling from. Separate with comma ",".
* entrypointsfile - file that contains crawling entrypoints either separated with comma or with newline. _Overwrites_
entrypoints specified with the "entrypoint" parameter.
* truncate - specify to rewrite the previous information in the database, otherwise the new information will be appended to the
old one.

## Synopsis

`perl UpdateSearchDB.pl --dbname='ensembl_api_test' --dbport=8080 --dbhost='localhost' --truncatetables --dbuser='springtest' --dbpssw='ensemblrules'
--entrypoint='ftp://ftp.ensembl.org/pub/release-89/bed','ftp://ftp.ensembl.org/pub/release-89/bamcov' --entrypointsfile=./entrypoints.list`
