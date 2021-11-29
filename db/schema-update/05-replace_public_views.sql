-- SITE_LIST:
-- 1) 2-decimal coordinate precision
-- 2) only site with at least one survey done
DROP VIEW IF EXISTS nrmn.ep_site_list_public;
CREATE OR REPLACE VIEW nrmn.ep_site_list_public AS
SELECT
    sr.country,
	epl.area,
	epl.location,
	epl.mpa,
	sr.site_code,
    epl.site_name,
	old_site_codes,
    round(epl.latitude::numeric, 2) AS latitude,
	round(epl.longitude::numeric, 2) AS longitude,
	epl.relief,
	epl.slope,
	epl.currents,
	epl.realm,
	epl.province,
	epl.ecoregion,
	epl.lat_zone,
	ST_SetSrid(ST_MakePoint(round (epl.longitude::numeric, 2), round (epl.latitude::numeric, 2)),4326)::geometry AS geom,
	epl.programs
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
	survey_id,
	country,
	area,
	location,
	mpa,
	site_code,
	site_name,
	round(latitude::numeric, 2) AS latitude,
	round(longitude::numeric, 2) AS longitude,
	depth,
	survey_date,
	"latest surveydate for site",
	"has pq scores in db",
	"has rugosity scores in db",
	"has pqs catalogued in db",
    visibility,
    hour,
	CASE WHEN direction ~* '(N|S|W|E|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW)'THEN direction
         WHEN direction ~* '(east|west|north|south)' THEN direction
         ELSE NULL
    END AS direction,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
	avg_rugosity,
	max_rugosity,
	surface,
	ST_SetSrid(ST_MakePoint(round (longitude::numeric, 2), round (latitude::numeric, 2)),4326)::geometry AS geom,
	program,
	pq_zip_url,
	old_site_codes,
	methods,
	survey_notes
FROM nrmn.ep_survey_list epsl
WHERE epsl.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

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
	program,
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
WHERE phylum IN ('Actinopterygii','Chondrichthyes','Elasmobranchii','Aves',
'Mammalia','Reptilia','Cephalopoda', 'Cnidaria', 'Ctenophora')
AND epm1.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M2 Inverts
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m2_inverts_public;
CREATE OR REPLACE VIEW  nrmn.ep_m2_inverts_public AS
SELECT survey_id,
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
    program,
    visibility,
    hour,
    round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
    method,
    block,
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
WHERE epm2i.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M2 Cryptic fish
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
-- 4) Elasmobranchii rolled up to Chondrychthyes
DROP VIEW IF EXISTS nrmn.ep_m2_cryptic_fish_public;
CREATE OR REPLACE VIEW nrmn.ep_m2_cryptic_fish_public AS
SELECT
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
	program,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
    "method",
    block,
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
WHERE family IN('Agonidae','Ambassidae','Anarhichadidae','Antennariidae','Aploactinidae','Apogonidae','Ariidae',
'Aulopidae','Bathymasteridae','Batrachoididae','Blenniidae','Bothidae','Bovichtidae','Brachaeluridae',
'Brachionichthyidae','Bythitidae','Callionymidae','Caracanthidae','Carapidae','Centriscidae','Chaenopsidae',
'Chironemidae','Cirrhitidae','Clinidae','Congridae','Congrogadidae','Cottidae','Creediidae','Cryptacanthodidae',
'Cyclopteridae','Cynoglossidae','Dasyatidae','Diodontidae','Eleotridae','Gnathanacanthidae','Gobiesocidae','Gobiidae',
'Grammistidae','Hemiscylliidae','Heterodontidae','Holocentridae','Hypnidae','Labrisomidae','Leptoscopidae','Liparidae',
'Lotidae','Monocentridae','Moridae','Muraenidae','Nototheniidae','Ophichthidae','Ophidiidae','Opistognathidae',
'Orectolobidae','Paralichthyidae','Parascylliidae','Pataecidae','Pegasidae','Pempheridae','Pholidae','Pinguipedidae',
'Platycephalidae','Plesiopidae','Pleuronectidae','Plotosidae','Priacanthidae','Pseudochromidae','Psychrolutidae',
'Rajidae','Rhinobatidae','Scorpaenidae','Serranidae','Scyliorhinidae','Soleidae','Solenostomidae','Stichaeidae',
'Synanceiidae','Syngnathidae','Synodontidae','Tetrabrachiidae','Tetrarogidae','Torpedinidae','Trachichthyidae',
'Tripterygiidae','Uranoscopidae','Urolophidae','Zaproridae','Zoarcidae')
OR species_name NOT SIMILAR TO '(Trachinops|Anthias|Caesioperca|Lepidoperca)%'
AND epm2cf.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M0 Off transect sightings
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m0_off_transect_sighting_public;
CREATE OR REPLACE VIEW nrmn.ep_m0_off_transect_sighting_public AS
SELECT
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
	program,
	visibility,
	hour,
	round(survey_latitude::numeric, 2) AS survey_latitude,
	round(survey_longitude::numeric, 2) AS survey_longitude,
    block,
	phylum,
	"class",
	"order",
	family,
	species_name,
	reporting_name,
    total
FROM nrmn.ep_m0_off_transect_sighting epm0
WHERE epm0.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M3 In situ quadrats
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m3_isq_public;
CREATE OR REPLACE VIEW nrmn.ep_m3_isq_public AS
SELECT
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
	program,
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
FROM nrmn.ep_m3_isq epm3
WHERE epm3.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M4 Macrocystis counts
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m4_macrocystis_count_public;
CREATE OR REPLACE VIEW nrmn.ep_m4_macrocystis_count_public AS
SELECT
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
	program,
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
WHERE epm4.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

--M5 Limpet quadrats
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m5_limpet_quadrats_public;
CREATE OR REPLACE VIEW nrmn.ep_m5_limpet_quadrats_public as
SELECT
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
	program,
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
WHERE epm5.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M11 Off-transect measurements
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m11_off_transect_measurement_public;
CREATE OR REPLACE VIEW nrmn.ep_m11_off_transect_measurement_public AS
SELECT
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
	program,
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
WHERE epm11.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');

-- M13 Photo Quadrat scores
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) remove name of PQ scorer (Currently not implemented in private endpoint)
DROP VIEW IF EXISTS nrmn.ep_m13_pq_scores_public;
CREATE OR REPLACE VIEW nrmn.ep_m13_pq_scores_public AS
SELECT
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
	program,
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
AND epm13.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id 
	WHERE pde.program_id='2');