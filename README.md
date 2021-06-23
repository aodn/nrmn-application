NRMN Application
================

This repository contains the source code for the NRMN application.  

ddl and migration code for the core NRMN reference data and surveys can be found in the 
[NRMN repository](https://github.com/aodn/NRMN/tree/master/db-migrate).

ddl for additional tables required to support application functionality can be found in [application.sql](api/src/main/resources/sql/application.sql)

### Structure

This repository contains a multi-module maven project used to build both the front-end react application, the backend end 
spring boot application and the war containing both that can be deployed on tomcat. The maven project is structured as follows:

project | descriptiuon
--- | ---
. | multi-module maven project containing all sub-modules used to build the project and containing common settings
api | maven sub-module used to build the spring boot backend (the api)
ui | maven sub-module project used to build the react front-end
app | maven submodule to assemble the api and ui artifacts built by the api and ui sub-modules into the application war containing both

### To build

```
mvn clean package 
```

### To run

Restore a copy of the nrmn_dev.nrmn schema on 17-nec-hob to a local database as follows:

    sudo su - postgres
    createdb nrmn_dev
    psql -c "CREATE ROLE nrmn_dev PASSWORD 'nrmn_dev' LOGIN;"
    psql -d nrmn_dev -c "CREATE SCHEMA nrmn AUTHORIZATION nrmn_dev;"
    psql -d nrmn_dev -c "CREATE EXTENSION postgis;"
    exit

Find the latest backup for nrmn_dev e.g.

    aws --profile production-developer s3 ls imos-backups/backups/17-nec-hob.emii.org.au/pgsql/

Retrieve it e.g. for the latest backup when this page was written:

    aws --profile production-developer s3 cp s3://imos-backups/backups/17-nec-hob.emii.org.au/pgsql/2021.06.23.00.00.43/nrmn_dev/nrmn.dump /tmp

(or you could just create a backup directly from the database using pg_dump)

Then restore it to your empty database:

    pg_restore --host "localhost" --port "5432" --username "nrmn_dev" --no-password --dbname "nrmn_dev" --no-owner --no-privileges --no-tablespaces --verbose "/tmp/nrmn.dump"

And then deploy the application to a tomcat instance e.g. in intellij 

![image](https://user-images.githubusercontent.com/1860215/123058197-ec7a3600-d44b-11eb-957a-965542d581aa.png)

![image](https://user-images.githubusercontent.com/1860215/123058279-ff8d0600-d44b-11eb-9a7d-efd216fd41f4.png)


Note:

This uses the spring.datasource database connection details specified in [application.properties](api/src/main/resources/application.properties)

You can override these on the command line to point to a different database by adding the following arguments to the startup options for tomcat 

    -Dspring.datasource.url=... -Dspring.datasource.username=... -Dspring.datasource.password=...

### Updating test databases

This is currently a manual process as described in [Updating_Databases.md](Updating_Databases.md)
