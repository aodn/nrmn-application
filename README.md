# National Reef Monitoring Network Data Portal

The National Reef Monitoring Network (NRMN) Data Portal is a web application for collation, validation, and storage of all data obtained during shallow reef surveys conducted by the NRMN. NRMN is a sub-facility of the Integrated Marine Observing System [IMOS](https://imos.org.au/).

The application consists of the following components:

* A Spring Boot based backend with a REST API
* A React user interface
* A Postgres database

The NRMN Data Portal is deployed at [NRMN - Data Portal](https://nrmn.aodn.org.au/).

This repository contains the source code for the NRMN application backend, the user interface and DDL code for initialisation of the database.

## Licensing

This project is licensed under the terms of the GNU GPLv3 license.

## Structure

This repository contains a multi-module maven project used to build both the front-end react application, the backend end 
spring boot application and the war. The maven project is structured as follows:

Project | Description
--- | ---
. | multi-module maven project containing all sub-modules used to build the project and containing common settings
api | maven sub-module used to build the spring boot backend
web | maven sub-module project used to build the react front-end
app | maven project to assemble the api and web artifacts in a single application WAR
db | SQL scripts containing the data and application DDL and scripts to insert data for testing

## Building

Build requires Java 11 with Maven and Node v16 with Yarn v1.x. A Dockerfile is also provided with the necessary prerequisites. 

    docker build --tag nrmn-builder --build-arg BUILDER_UID=$(id -u) .
    docker run -it -v $PWD:/home/builder/src nrmn-builder yarn --cwd src/web
    docker run -it -v $PWD:/home/builder/src nrmn-builder mvn -f src clean package

## Developing

### Creating an empty database

This application required PostreSQL 11 + the PostGIS extension. Database connection details specified in [application.properties](api/src/main/resources/application.properties).

Create an empty development database:

    sudo su - postgres
    createdb nrmn_dev
    psql -c "CREATE ROLE nrmn_dev PASSWORD 'nrmn_dev' LOGIN;"
    psql -d nrmn_dev -c "CREATE SCHEMA nrmn AUTHORIZATION nrmn_dev;"
    psql -d nrmn_dev -c "CREATE EXTENSION postgis;"
    exit

Apply the DDL against this database using the following scripts in order:

    db/schema/CreateTables.sql
    db/schema/CreateMiscAncillaryObjects.sql
    db/schema/ApplyConstraints.sql
    db/schema/BasicIndexing.sql
    db/endpoints/CreatePrivateEndpoints.sql
    db/endpoints/CreatePublicEndpoints.sql
    db/endpoints/EndpointIndexes.sql

Then add the application tables:
    
    api/src/main/resources/sql/application.sql

Also add any sample data if required:

    api/src/test/resources/testdata/FILL_DATA.sql
    api/src/test/resources/testdata/FILL_ROLES.sql

Finally run all of the scripts in `db/schema-update/` in order.

### Developing with Codium / Visual Studio Code

Instructions are based on VSCode v1.66 running on Linux. and assume that Chrome / Chromium is installed.

1. Install the extension [https://marketplace.visualstudio.com/items?itemName=GabrielBB.vscode-lombok](Lombok Annotations Support) and [https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack](Extension Pack for Java).
2. Open the nrmn-application repository as a folder
3. Go to Debug & Run and choose App from the list of configurations and start debugging

- The first time the application is run may take some time as Yarn and Maven will download build dependencies.
- To verify the debugger is working correctly place breakpoints in appropriate places in `LoginForm.js` and `AuthController.java` and Log In.

### Updating Database Schema

Scripts to modify the database schema are stored in scripts/db-update once applied. Do not modify application.sql. Script names should have the form `00-Script_Description.sql` where 00 is incremented for each script and consist of a single update.
