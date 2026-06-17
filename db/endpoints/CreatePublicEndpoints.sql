-- SITE_LIST:
-- 1) 2-decimal coordinate precision
-- 2) only site with at least one survey done
DROP VIEW IF EXISTS nrmn.ep_site_list_public;
CREATE OR REPLACE VIEW nrmn.ep_site_list_public AS
SELECT
    epl.programs,
    sr.country,
	epl.area,
	epl.location,
	sr.site_code,
    epl.site_name,
	old_site_codes,
    round(epl.latitude::numeric, 2) AS latitude,
	round(epl.longitude::numeric, 2) AS longitude,
	epl.realm,
	epl.province,
	epl.ecoregion,
	epl.lat_zone,
	ST_SetSrid(ST_MakePoint(round (epl.longitude::numeric, 2), round (epl.latitude::numeric, 2)),4326)::geometry AS geom
FROM nrmn.ep_site_list epl
JOIN nrmn.site_ref sr ON sr.site_code = epl.site_code
WHERE EXISTS (SELECT 1 FROM nrmn.survey WHERE survey.site_id = sr.site_id);


-- SURVEY_LIST :
-- 1) 2-decimal coordinate precision
-- 2) remove name of diver
-- 3) exclude invalid directions
DROP VIEW IF EXISTS nrmn.ep_survey_list_public;
CREATE OR REPLACE VIEW nrmn.ep_survey_list_public AS
SELECT
    "program",
	survey_id,
	country,
	area,
	location,
	site_code,
	site_name,
	round(latitude::numeric, 2) AS latitude,
	round(longitude::numeric, 2) AS longitude,
	depth,
	survey_date,
	latest_surveydate_for_site,
	has_rugosity_scores_in_db,
	has_pqs_catalogued_in_db,
    visibility,
    hour,
	CASE WHEN direction ~* '(N|S|W|E|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW)'THEN direction
             WHEN direction ~* '(east|west|north|south)' THEN direction
             ELSE NULL
        END AS direction,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	ST_SetSrid(ST_MakePoint(round (longitude::numeric, 2), round (latitude::numeric, 2)),4326)::geometry AS geom,
	pq_zip_url,
	old_site_codes,
	methods
FROM nrmn.ep_survey_list epsl
WHERE epsl.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id
	WHERE pde.program_id in (2,5));

-- Species list
-- 1) 2-decimal coordinate precision
-- 2) exclude species with no current taxonomic name
-- **Criteria already met in ep_species_list**
CREATE OR REPLACE VIEW nrmn.ep_species_list_public AS
SELECT * FROM nrmn.ep_species_list;


-- M1 Fish
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
-- 4) include Actinopterygii, Chondrichthyes, Elasmobranchii, Aves, Mammalia, Reptilia, Cephalopoda, Cnidaria, Ctenophora
-- 5) include "No species found"
DROP VIEW IF EXISTS nrmn.ep_m1_public;
CREATE OR REPLACE VIEW nrmn.ep_m1_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location,
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
	round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	"method",
    "block",
	phylum,
	"class",
	"order",
	family,
    species_name,
	reporting_name,
	size_class,
    total,
	biomass
FROM nrmn.ep_m1 epm1
WHERE ("class" IN ('Actinopterygii','Actinopteri','Teleostei','Chondrichthyes','Elasmobranchii',
'Mammalia','Reptilia','Cephalopoda') OR phylum IN ('Cnidaria', 'Ctenophora'))
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm1.survey_id
    )
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    WHERE esl.survey_id = epm1.survey_id
      AND pr.program_id = 1
      AND sr.site_code in ('GSV26','GSV116','GSV93','GSV183','GSV182','GSV118','COO20','GSV125','GSV21','GSV1','GSV3',
                    'GSV45','GSV46','GSV47','SSG62','GSV119','GSV9','GSV191','GSV121','GSV39','GSV15','GLSR_SHI',
                    'GSV57','GSV185','GSV140','GSV2','GSV19','GSV124','GSV131','GSV95','GSV105','GSV53','GSV138',
                    'GSV18','GSV7','GSV34','GSV14','GSV136','GSV112','OSSR_SHI','GSV107','GSV11','GSV55','GSV58',
                    'SSG64','SSG46','GSV30','COO19','GSV115','GSV194','GSV134','GSV135','GSV59','GSV4','GSV41','GSV5',
                    'GSV43','GSV42','GSV24','GSV127','GSV20','GSV128','GSV56','GSV17','GSV190','GSV189','GSV40',
                    'GSV129','GSV134','GSV109','GSV137','GSV139','GSV110','GSV111','GSV106','GSV114','GSV113')
        AND esl.survey_date BETWEEN '2026-01-01' AND '2026-06-01');


-- M2 Inverts
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m2_inverts_public;
CREATE OR REPLACE VIEW  nrmn.ep_m2_inverts_public AS
SELECT
       program,
       survey_id,
       country,
       area,
       ecoregion,
       realm,
       location,
       site_code,
       site_name,
       round(latitude::numeric, 2) AS latitude,
       round(longitude::numeric, 2) AS longitude,
       survey_date,
       depth,
       ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
       visibility,
       hour,
       round(survey_latitude::numeric, 2) AS survey_latitude,
       round(survey_longitude::numeric, 2) AS survey_longitude,
       "method",
       "block",
       phylum,
       "class",
       "order",
       family,
       species_name,
       reporting_name,
       size_class,
       total,
       biomass
FROM nrmn.ep_m2_inverts epm2i
WHERE NOT EXISTS (
        SELECT 1
        FROM nrmn.ep_survey_list esl
        JOIN nrmn.program_ref pr ON esl.program = pr.program_name
        JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
        JOIN nrmn.public_data_exclusion pde
            ON sr.site_id = pde.site_id
            AND pr.program_id = pde.program_id
        WHERE esl.survey_id = epm2i.survey_id
    )
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    WHERE esl.survey_id = epm2i.survey_id
      AND pr.program_id = 1
      AND sr.site_code in ('GSV26','GSV116','GSV93','GSV183','GSV182','GSV118','COO20','GSV125','GSV21','GSV1','GSV3',
                    'GSV45','GSV46','GSV47','SSG62','GSV119','GSV9','GSV191','GSV121','GSV39','GSV15','GLSR_SHI',
                    'GSV57','GSV185','GSV140','GSV2','GSV19','GSV124','GSV131','GSV95','GSV105','GSV53','GSV138',
                    'GSV18','GSV7','GSV34','GSV14','GSV136','GSV112','OSSR_SHI','GSV107','GSV11','GSV55','GSV58',
                    'SSG64','SSG46','GSV30','COO19','GSV115','GSV194','GSV134','GSV135','GSV59','GSV4','GSV41','GSV5',
                    'GSV43','GSV42','GSV24','GSV127','GSV20','GSV128','GSV56','GSV17','GSV190','GSV189','GSV40',
                    'GSV129','GSV134','GSV109','GSV137','GSV139','GSV110','GSV111','GSV106','GSV114','GSV113')
      AND esl.survey_date BETWEEN '2026-01-01' AND '2026-06-01');


-- M2 Cryptic fish
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
-- 4) Elasmobranchii rolled up to Chondrychthyes
DROP VIEW IF EXISTS nrmn.ep_m2_cryptic_fish_public;
CREATE OR REPLACE VIEW nrmn.ep_m2_cryptic_fish_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location,
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
    "method",
    "block",
	phylum,
	CASE WHEN "class" = 'Elasmobranchii' THEN 'Chondrichthyes'
	     ELSE "class"
	END "class",
	"order",
	family,
	species_name,
	reporting_name,
	size_class,
    total,
	biomass
FROM nrmn.ep_m2_cryptic_fish epm2cf
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm2cf.survey_id
    )
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    WHERE esl.survey_id = epm2cf.survey_id
      AND pr.program_id = 1
      AND sr.site_code in ('GSV26','GSV116','GSV93','GSV183','GSV182','GSV118','COO20','GSV125','GSV21','GSV1','GSV3',
                    'GSV45','GSV46','GSV47','SSG62','GSV119','GSV9','GSV191','GSV121','GSV39','GSV15','GLSR_SHI',
                    'GSV57','GSV185','GSV140','GSV2','GSV19','GSV124','GSV131','GSV95','GSV105','GSV53','GSV138',
                    'GSV18','GSV7','GSV34','GSV14','GSV136','GSV112','OSSR_SHI','GSV107','GSV11','GSV55','GSV58',
                    'SSG64','SSG46','GSV30','COO19','GSV115','GSV194','GSV134','GSV135','GSV59','GSV4','GSV41','GSV5',
                    'GSV43','GSV42','GSV24','GSV127','GSV20','GSV128','GSV56','GSV17','GSV190','GSV189','GSV40',
                    'GSV129','GSV134','GSV109','GSV137','GSV139','GSV110','GSV111','GSV106','GSV114','GSV113')
      AND esl.survey_date BETWEEN '2026-01-01' AND '2026-06-01');

-- M0 Off transect sightings
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m0_off_transect_sighting_public;
CREATE OR REPLACE VIEW nrmn.ep_m0_off_transect_sighting_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location,
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	"method",
    "block",
	phylum,
	"class",
	"order",
	family,
	species_name,
	reporting_name,
    size_class,
    total,
    biomass
FROM nrmn.ep_m0_off_transect_sighting epm0
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm0.survey_id
    )
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    WHERE esl.survey_id = epm0.survey_id
      AND pr.program_id = 1
      AND sr.site_code in ('GSV26','GSV116','GSV93','GSV183','GSV182','GSV118','COO20','GSV125','GSV21','GSV1','GSV3',
                    'GSV45','GSV46','GSV47','SSG62','GSV119','GSV9','GSV191','GSV121','GSV39','GSV15',
                    'GSV57','GSV185','GSV140','GSV2','GSV19','GSV124','GSV131','GSV95','GSV105','GSV53','GSV138',
                    'GSV18','GSV7','GSV34','GSV14','GSV136','GSV112','GSV107','GSV11','GSV55','GSV58',
                    'SSG64','SSG46','GSV30','COO19','GSV115','GSV194','GSV134','GSV135','GSV59','GSV4','GSV41','GSV5',
                    'GSV43','GSV42','GSV24','GSV127','GSV20','GSV128','GSV56','GSV17','GSV190','GSV189','GSV40',
                    'GSV129','GSV134','GSV109','GSV137','GSV139','GSV110','GSV111','GSV106','GSV114','GSV113')
      AND esl.survey_date BETWEEN '2026-01-01' AND '2026-06-01');


-- M3 In situ quadrats
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m3_isq_public;
CREATE OR REPLACE VIEW nrmn.ep_m3_isq_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location,
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	phylum,
	"class",
	"order",
	family,
	species_name,
	reporting_name,
	report_group,
	habitat_groups,
	quadrat,
	total
FROM nrmn.ep_m3_isq epm3
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm3.survey_id
    );

-- M4 Macrocystis counts
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m4_macrocystis_count_public;
CREATE OR REPLACE VIEW nrmn.ep_m4_macrocystis_count_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	phylum,
	"class",
	"order",
	family,
    species_name,
	reporting_name,
    block,
    total
from nrmn.ep_m4_macrocystis_count epm4
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm4.survey_id
    );

--M5 Limpet quadrats
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m5_limpet_quadrats_public;
CREATE OR REPLACE VIEW nrmn.ep_m5_limpet_quadrats_public as
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	phylum,
	"class",
	"order",
	family,
    species_name,
	reporting_name,
	quadrat,
	total
FROM nrmn.ep_m5_limpet_quadrats epm5
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm5.survey_id
    );

-- M11 Off-transect measurements
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m11_off_transect_measurement_public;
CREATE OR REPLACE VIEW nrmn.ep_m11_off_transect_measurement_public AS
SELECT
    program,
	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	phylum,
	"class",
	"order",
	family,
    species_name,
    reporting_name,
	size_class,
    total
FROM nrmn.ep_m11_off_transect_measurement epm11
WHERE NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm11.survey_id
    );

-- M13 Photo Quadrat scores
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) remove name of PQ scorer (Currently not implemented in private endpoint)
DROP VIEW IF EXISTS nrmn.ep_m13_pq_scores_public;
CREATE OR REPLACE VIEW nrmn.ep_m13_pq_scores_public AS
SELECT
    program,
 	survey_id,
	country,
	area,
	ecoregion,
	realm,
	location,
	site_code,
	site_name,
    round(latitude::numeric, 2) AS latitude,
    round(longitude::numeric, 2) AS longitude,
	survey_date,
	depth,
	ST_SetSrid(ST_MakePoint(round (latitude::numeric, 2), round (longitude::numeric, 2)),4326)::geometry AS geom,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	resolution,
	category,
	major_category,
	num_points,
    total_points,
    percent_cover
FROM nrmn.ep_m13_pq_scores epm13
WHERE category <> 'Tape'
AND NOT EXISTS (
    SELECT 1
    FROM nrmn.ep_survey_list esl
    JOIN nrmn.program_ref pr ON esl.program = pr.program_name
    JOIN nrmn.site_ref sr ON esl.site_code = sr.site_code
    JOIN nrmn.public_data_exclusion pde
        ON sr.site_id = pde.site_id
        AND pr.program_id = pde.program_id
    WHERE esl.survey_id = epm13.survey_id
    );


-- TPAC specific SURVEY_LIST for cataloguing purposes- limited fields:
-- 1) full coordinate precision
-- 2) full survey list(no exclusions)

DROP VIEW IF EXISTS nrmn.ep_tpac;
CREATE OR REPLACE VIEW nrmn.ep_tpac AS
SELECT
    survey_id,
    site_code,
    location,
    survey_date,
    depth,
    latitude,
    longitude,
    survey_latitude,
    survey_longitude,
    has_pqs_catalogued_in_db,
    CASE WHEN direction ~* '(N|S|W|E|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW)'THEN direction
         WHEN direction ~* '(east|west|north|south)' THEN direction
         ELSE NULL
    END AS direction
FROM nrmn.ep_survey_list
