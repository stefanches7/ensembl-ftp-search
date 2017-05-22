ADR 4: Use REST API for fetching the data to own search database
================================================================
Context
-------
The new REST API is being developed for some of the Ensembl databases including ensembl_metadata and ncbi_taxonomy. @prem (Slack) is responsible/contactable.
Since only those are of interest for the project and using this API is some kind of adapter between the project and the data, I take the
current decision.

Decision
--------
Use REST API to fetch the data from the metadata databases (accept JSON as a response and parse it properly).
