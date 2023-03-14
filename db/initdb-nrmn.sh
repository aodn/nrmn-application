#!/bin/bash

set -e

"${psql[@]}" <<- 'EOSQL'
    CREATE DATABASE nrmn_dev;
    CREATE USER nrmn_dev WITH ENCRYPTED PASSWORD 'nrmn_dev';
    GRANT ALL PRIVILEGES ON DATABASE nrmn_dev TO nrmn_dev;
EOSQL

"${psql[@]}" --dbname='nrmn_dev' --command="CREATE EXTENSION postgis;"

export PGUSER=nrmn_dev
export PGPASSWORD=nrmn_dev
export PGDATABASE=nrmn_dev

psql --file="/scripts/CreateTables.sql"
psql --file="/scripts/CreateMiscAncillaryObjects.sql"
psql --file="/scripts/ApplyConstraints.sql"
psql --file="/scripts/BasicIndexing.sql"
psql --file="/scripts/CreatePrivateEndpoints.sql"
psql --file="/scripts/CreatePublicEndpoints.sql"
psql --file="/scripts/EndpointIndexes.sql"
psql --file="/scripts/application.sql"
psql --file="/scripts/FILL_ROLES.sql"
psql --file="/scripts/TEST_USER.sql"
psql --file="/scripts/FILL_DATA.sql"
