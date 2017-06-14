ADR 9: use Specifications API for querying
==========================================
Context
-------
We need to intersect the search filters in a blackbox to run from possible big refactorings in the future (when filters set will be changed)
and/or big paperwork in the now.

Decision
--------
Create SearchFilter objects and validate them; then, use JpaSpecifications to add filters to search query in a loop. Execute it and get the result.
