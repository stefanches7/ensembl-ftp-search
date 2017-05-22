ADR 5: Treat the project as a separate kind of thing for the time being
=======================================================================
Context
-------
Since embedding the application in the other Ensembl code can become a hard, buggy and distracting thing, there was a 

Decision
--------
To write the app per se first and only then embed it in Ensembl. Here job of updating the db and WebUI will be also treated separately as standalones.
