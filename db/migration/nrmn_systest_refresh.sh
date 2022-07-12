#!/usr/bin/env bash

# Drop existing schema
echo "SELECT drop_objects_in_schema('nrmn');" | psql -d nrmn_systest

# Dump and restore `nrmn` schema from `nrmn_migration` database to `nrmn_dev` database
pg_restore --no-privileges --no-owner --role=nrmn_systest -v -d nrmn_systest nrmn_migration.dump

# Apply application.sql and test_user.sql

wget -O application.sql 'https://raw.githubusercontent.com/aodn/nrmn-application/main/api/src/main/resources/sql/application.sql'
wget -O test_user.sql 'https://raw.githubusercontent.com/aodn/nrmn-application/main/api/src/test/resources/testdata/TEST_USER.sql'

psql -c 'set role nrmn_systest' -f application.sql -f test_user.sql nrmn_systest

