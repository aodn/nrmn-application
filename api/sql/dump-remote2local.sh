#!/bin/bash
HOST_SOURCE="17-nec-hob.emii.org.au"
USER_SOURCE="nrmn_dev"
HOST_TARGET="localhost"
USER_TARGET="docker"
BACKUP_FILE="backup.dump"
SQL="DROP DATABASE nrmn;CREATE DATABASE nrmn TEMPLATE template_postgis OWNER ${USER_SOURCE}"

echo "starting remote dump from: ${HOST_SOURCE} --> ${HOST_TARGET}"
echo "dumping from source..."

echo "pg_dump -h ${HOST_SOURCE} -p 5432 -U ${USER_SOURCE} --no-acl --no-owner nrmn > dump.sql nrmn > ${BACKUP_FILE}"
pg_dump -h ${HOST_SOURCE} -p 5432 -U ${USER_SOURCE}  --no-acl --no-owner  nrmn > ${BACKUP_FILE}
echo "dumping done"

if test -f ${BACKUP_FILE}; then
    echo "${BACKUP_FILE} exists."
else
    echo "${BACKUP_FILE} does not found."
    exit 2
fi


echo "droping and creating database"
echo "psql -h ${HOST_TARGET} -p 5433 -U ${USER_TARGET} nrmn -c ${SQL}
psql -h ${HOST_TARGET} -p 5433 -U ${USER_TARGET} nrmn -c ${SQL}"

echo "restoring"
echo "pg_restore -h ${HOST_TARGET} -U ${USER_TARGET}  -p 5433 -n public -d nrmn ${BACKUP_FILE}"
psql -h ${HOST_TARGET} -U ${USER_TARGET}  -p 5433  -n public -d nrmn ${BACKUP_FILE}
echo "Done."
