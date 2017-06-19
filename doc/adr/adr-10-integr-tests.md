ADR 10: Make special expression on the integration tests
========================================================
Context
-------
Current project is comparably small API with the special focus on providing the most comfortable
and usable way to the users to get the data they need, and this data to be accurate.

Decision
--------
Focus especially on the integration tests with the goal that everything works fine from the user's point of view,
since should there be some bug not covered by tests one can relatively easy locate it. NB! That doesn't mean there will be no unit 
tests at all, that just means there'll be much more integration tests!
