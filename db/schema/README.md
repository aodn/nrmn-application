# db/schema

This folder contains scripts to create the database schema.

Warning: 
When CreateMiscAncillaryObjects.sql runs 2 
materialized view get dropped, which implies that 
endpoints/CreatePrivateEndpoints.sql 
and
endpoints/CreatePubicEndpoints.sql 
need to be run too.

