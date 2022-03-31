National Reef Monitoring Network Data Portal
=

The National Reef Monitoring Network (NRMN) Data Portal is a web application for collation, validation, and storage of all data obtained during shallow reef surveys conducted by the NRMN. NRMN is a sub-facility of the Integrated Marine Observing System [IMOS](https://imos.org.au/).

The application consists of the following components:

* A Spring Boot based backend with a REST API
* A React + MUI user interface
* A Postgres database

The NRMN Data Portal is deployed at [NRMN - Data Portal](https://nrmn.aodn.org.au/).

This repository contains the source code for the NRMN application backend, the user interface and DDL code for initialisation of the database.

### Web Application

This repository contains a multi-module Maven project used to build both the front-end React application, the backend end 
Spring Boot application and the WAR containing both that can be deployed on Tomcat. The project is structured as follows:

project | descriptiuon
--- | ---
. | multi-module Maven project containing all sub-modules used to build the project and containing common settings
api | Maven submodule used to build the Spring Boot backend (the api)
web | Maven submodule project used to build the React front-end
app | Maven submodule to assemble the api and web artifacts built by the api and web submodules into the application WAR containing both

### Database Schema

The complete database schema consists of the following scripts applied in order:

* [db/schema](db/schema) - amalgamated ATRC/RLS database ddl/migration code
* [application.sql](api/src/main/resources/sql/application.sql) - ddl to support nrmn-application functionality
* [db/schema-updates](db/schema-updates) - additional update scripts applied since going live.

### Updating Database Schema

Scripts to modify the database schema are stored in scripts/db-update once applied. Do not modify application.sql. Script names should have the form `00-Script_Description.sql` where 00 is incremented for each script and consist of a single update.
