WITH stg1 AS (
SELECT 2 AS program_id ,site_id FROM nrmn.site_ref
WHERE site_code LIKE 'NCMP-S%'
ORDER BY site_code)
INSERT INTO nrmn.public_data_exclusion (program_id,site_id)
SELECT * FROM stg1
