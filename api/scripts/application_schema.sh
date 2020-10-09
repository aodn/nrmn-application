#!/bin/bash

# Script to generate ddl for application specific objects from the migration.sql file and a database 
# where the nrmn_migration schema has been updated by hibernate (nrmn_update defined in application-update.properties)
#
# Requires liquibase (https://github.com/liquibase/liquibase/releases/download/v4.1.0/liquibase-4.1.0.zip
# or snap install liquibase) to be downloaded, the postgres driver configured for it and added to the path and
# pg_format to be installed (apt install pgformatter)
#

# Get port to use for docker image if supplied

usage() { echo "Usage: $0 [-d <docker port>] [-p <local postgres port> ] " 1>&2; exit 1; }

while getopts "d:p:" OPTION; do
    case "$OPTION" in
        p)
            POSTGRES_PORT=${OPTARG}
            ;;
        d)
            DOCKER_PORT=${OPTARG}
            ;;
        ?)
            usage
            ;;
    esac
done
shift $((OPTIND-1))

# Defaults

if [[ -z ${POSTGRES_PORT} ]] ; then
    POSTGRES_PORT=5432
fi

if [[ -z ${DOCKER_PORT} ]] ; then
    DOCKER_PORT=5434
fi

# Determine script/project locations 

SCRIPT_DIR="$(cd "$(dirname "$0")"; pwd -P)"
PROJECT_DIR="$(cd ${SCRIPT_DIR}/..; pwd -P)"

# Load docker postgres instance with migration schema

echo "Starting docker postgres instance..."

export PGPASSWORD=postgres

docker run -p ${DOCKER_PORT}:5432 --name migration_db -e POSTGRES_PASSWORD=${PGPASSWORD} -d postgis/postgis:9.6-2.5 > /dev/null

# wait for postgres to start listening on port 5434

RETRIES=20

until pg_isready -h localhost -U postgres -p ${DOCKER_PORT} > /dev/null 2>&1 || [[ ${RETRIES} -eq 0 ]]; do
  echo "Waiting for postgres server, $((RETRIES--)) remaining attempts..."
  sleep 1
done

# create nrmn_migration database and create schema from nrmn_migration.sql

echo "Loading migration schema..."

psql -h localhost -U postgres -p ${DOCKER_PORT} -c "create database nrmn_migration template template_postgis" > /dev/null
psql -h localhost -U postgres -p ${DOCKER_PORT} -d nrmn_migration -f "$PROJECT_DIR/src/main/resources/sql/migration.sql" > /dev/null 2>&1

# create a temporary directory for liquibase working files so no state is stored (changelog.xml/databasechangelog.csv)
 
CHANGE_LOG_DIR=$(mktemp --directory --tmpdir --suffix '.xml' changelog_XXXXXX)

# Create changelog required to go from nrmn_migration to nrmn_update (nrmn_migration + hibernate updates)

echo "Creating application object changelog..."

liquibase --changeLogFile=changelog.xml \
          --classpath="$CHANGE_LOG_DIR" \
          --includeSchema=true \
          --driver=org.postgresql.Driver \
          --url=jdbc:postgresql://localhost:${DOCKER_PORT}/nrmn_migration \
          --username=postgres \
          --password=${PGPASSWORD} \
          --defaultSchemaName=nrmn \
          diffChangeLog \
          --referenceUrl=jdbc:postgresql://localhost:${POSTGRES_PORT}/nrmn_update \
          --referenceUsername=postgres \
          --referencePassword=postgres \
          --referenceDefaultSchemaName=nrmn > /dev/null

# Convert changelog to sql using liquibase updateSql and then 
# format removing liquibase comments using pg_format

echo "Updating application.sql ..."

liquibase --changeLogFile=changelog.xml \
          --classpath="$CHANGE_LOG_DIR" \
          --driver=org.postgresql.Driver \
          --url='offline:postgresql?version=9.6&outputLiquibaseSql=none' \
          updateSql \
 | pg_format -n - > "$PROJECT_DIR/src/main/resources/sql/application.sql"

# cleanup

echo "Cleaning up..."

docker stop migration_db  > /dev/null
docker rm migration_db > /dev/null

rm -rf "$CHANGE_LOG_DIR"
