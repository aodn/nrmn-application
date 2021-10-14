TRUNCATE TABLE nrmn.public_data_exclusion;
WITH stg1 AS (
SELECT 2 AS program_id ,site_id FROM nrmn.site_ref
WHERE site_code LIKE 'NCMP-S%' or site_code LIKE 'JMP-S%'
ORDER BY site_id)
INSERT INTO nrmn.public_data_exclusion (program_id,site_id)
SELECT * FROM stg1