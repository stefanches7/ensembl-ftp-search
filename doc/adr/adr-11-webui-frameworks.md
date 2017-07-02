ADR 11: Start coding WebUI and pick WebUI frameworks
=============================
Context
-------
There should be at least a simple UI created, that allows the end-user unwillingly to specify the HTTP requests manually a bit more glance.
Even though it's apparently an "optional" feature in thoughts of project manager (the main API functionality does not depend on whether it
is implemented or not), it's pretty for the end-user and we have time to dedicate to this, therefore it's on the roadmap.

Decision
--------
Code off a (simple) WebUI interface that allows intersecting various filters, loads suggestions for the values and responds the list of links on
the query (which is also output in some kind of table, see later decisions). The main framework for building the interface is going
to be the ReactJS.
