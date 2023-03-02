#!/usr/bin/env bash
# Script to setup an empty test database
# requires nrmn_dev to already exist and be owned by user `nrmn_dev`

set -euo pipefail

echo [START: "$(date)"]

REPO=~/projects/nrmn-application

export PGDATABASE=nrmn_dev
export PGUSER=nrmn_dev
export PGHOST=localhost

psql -c "DROP SCHEMA IF EXISTS nrmn CASCADE;"

psql -f $REPO/db/schema/CreateTables.sql
psql -f $REPO/db/schema/CreateMiscAncillaryObjects.sql
psql -f $REPO/db/schema/ApplyConstraints.sql
psql -f $REPO/db/schema/BasicIndexing.sql

psql -f $REPO/api/src/main/resources/sql/application.sql

psql -f $REPO/api/src/test/resources/testdata/FILL_DATA.sql
psql -f $REPO/api/src/test/resources/testdata/FILL_ROLES.sql
psql -f $REPO/api/src/test/resources/testdata/TEST_USER.sql

psql -f $REPO/db/endpoints/CreatePrivateEndpoints.sql
psql -f $REPO/db/endpoints/CreatePublicEndpoints.sql
psql -f $REPO/db/endpoints/EndpointIndexes.sql

echo [END  : "$(date)"]

# EOF
