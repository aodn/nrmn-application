GRANT USAGE ON SCHEMA cron TO nrmn_prod;

--select * from pg_settings where name like 'cron.%';

SELECT * FROM cron.job;

SELECT cron.schedule('set_species_attributes', '0 18 * * *', 'SELECT nrmn.set_species_attributes()');
SELECT cron.schedule('refresh_materialized_views', '05 18 * * *', 'SELECT nrmn.refresh_materialized_views()');
--SELECT nrmn.set_species_attributes();

SELECT * FROM cron.job order by 1;

--update cron.job set schedule = '0 18 * * *' where jobid = 1;
--update cron.job set schedule = '05 18 * * *' where jobid = 2;

update cron.job set database = 'nrmn_prod' where command like 'SELECT nrmn%';

SELECT * FROM cron.job order by 1;
