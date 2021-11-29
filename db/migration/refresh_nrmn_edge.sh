#!/usr/bin/env bash

# Drop existing schema
echo "SELECT drop_objects_in_schema('nrmn');" | psql -d nrmn_edge

# Dump and restore `nrmn` schema from `nrmn_migration` database to `nrmn_edge` database
pg_dump -d nrmn_migration -n nrmn -Fc -v > nrmn_migration.dump
pg_restore --no-privileges --no-owner --role=nrmn_edge -v -d nrmn_edge nrmn_migration.dump

# Apply application.sql and test_user.sql

wget -O application.sql 'https://raw.githubusercontent.com/aodn/nrmn-application/master/api/src/main/resources/sql/application.sql'
wget -O test_user.sql 'https://raw.githubusercontent.com/aodn/nrmn-application/master/api/src/test/resources/testdata/TEST_USER.sql'

psql -c 'set role nrmn_edge'  -f application.sql -f test_user.sql nrmn_edge


