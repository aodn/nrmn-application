### Background

The ddl for the new NRMN database is currently maintained in two places

* [NRMN/db-migrate/dd](https://github.com/aodn/NRMN/tree/master/db-migrate) - amalgamated ATRC/RLS
  database ddl
* [application.sql](api/src/main/resources/sql/application.sql) - ddl
  to support nrmn-application functionality

This made it simpler when developing application related table changes but should probably be
revisited now those table definitions have stabilised (also refer https://github.com/aodn/backlog/issues/2479)

### Updating/refreshing databases

database | server | current usage | updating/refreshing
--- | --- | --- | ---
nrmn_migration | 17-nec-hob | contains the output of running db-migrate | @bpasquer runs migration
nrmn_dev | 17-nec-hob | nrmn_migration + application.sql + TEST_USER.sql for reference | run /var/lib/postgresql/refresh_nrmn_dev.sh on 17-nec-hob as postgres user
nrmn_edge | 17-nec-hob | nrmn_migration + application.sql + TEST_USER.sql for nrmn-edge | run /var/lib/postgresql/refresh_nrmn_edge.sh on 17-nec-hob as postgres user
nrmn_systest | 13-aws-syd | nrmn_migration + application.sql + TEST_USER.sql for nrmn-systest | copy nrmn_migration.dump generated above from /var/lib/postgresql/ on 17-nec-hob to /var/lib/postgresql/ on 13-aws-syd <br><br> run /var/lib/postgresql/refresh_nrmn_systest.sh on 13-aws-syd as postgres user

### Updating nrmn-application migration.sql

To allow databases to be created from scratch when running tests we maintain a copy of the nrmn_migration database ddl
in [nrmn-application application.sql](api/src/main/resources/sql/migration.sql).  To update this script
we currently run run [migration_schema.sh](api/scripts/migration_schema.sh) to dump the nrmn_migration
after rerunning the migration.
