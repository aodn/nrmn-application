#!/bin/bash

# Script to copy the nrmn_migration nrmn schema definition to nrmn_migration.sql
#
# Uses external configuration to connect i.e. .pgpass or PGUSER/PGPASSWORD environment 
# variables - will prompt for password if not supplied through config
# 
#   PGUSER=craigj ./migration_ddl.sql

PROJECT_DIR="$(cd "$(dirname "$0")"/..; pwd -P)"

echo "Updating migration.sql from 17-nec-hob..."

pg  --schema-only nrmn_migration --disable-dollar-quoting > "$PROJECT_DIR/src/main/resources/sql/migration.sql"

# remove line setting search_path to '' so that we can find postgis functions/tables/types
# after running this script
sed -i 's/SELECT pg_catalog.set_config.*//' "$PROJECT_DIR/src/main/resources/sql/migration.sql"
