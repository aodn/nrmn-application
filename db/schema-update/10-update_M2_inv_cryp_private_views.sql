/* Endpoint "M2 Inverts" #171 */
DROP MATERIALIZED VIEW nrmn.ep_m2_inverts CASCADE;
create materialized view nrmn.ep_m2_inverts as
with fish_classes as (
	select case
			when mea.measure_name in ('Unsized', 'No specimen found') then 0
			else (replace(mea.measure_name, 'cm', ''))::numeric
		   end size_class
	from nrmn.measure_ref mea
		 inner join nrmn.measure_type_ref mt on mea.measure_type_id = mt.measure_type_id
	where mt.measure_type_name = 'Fish Size Class'
),
bounded_fish_classes as (
	select size_class nominal,
		   case
			when size_class = 0 then -.01
			else coalesce((lag (size_class) over (order by size_class)), 0)
		   end lower_bound,
		   size_class as upper_bound
	from fish_classes
),
invert_sized as (
select
	sm.survey_id,
	sur.country,
	sur.area,
	sit.ecoregion,
	sit.realm,
	sur.location,
	sur.site_code,
	sur.site_name,
    sur.latitude,
    sur.longitude,
	sur.survey_date,
	sur.depth,
	sit.geom,
	sur.program,
	sur.visibility,
	sur.hour,
	sur.survey_latitude,
	sur.survey_longitude,
	div.full_name as diver,
	sm.method_id "method",
	sm.block_num as block,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'No specimen found') then 0
		else (replace(meas.measure_name, 'cm', ''))::numeric
	end size_class,
	sum(obs.measure_value) as total,
	sum(
		nrmn.obs_biomass(
			oi.a,
			oi.b,
			oi.cf, (
			case when meas.measure_name ~ 'cm$' then replace(meas.measure_name, 'cm', '') else '0' end )::float,
			(obs.measure_value)::int,true
		)
	) as biomass
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref meas ON meas.measure_id = obs.measure_id
where sm.method_id = 2
and (
	(oi.phylum = 'Arthropoda' and oi.class ='Malacostraca' and oi.family <>'Palaemonidae')
	or (oi.phylum = 'Arthropoda' and oi.class='Pycnogonida' )
	or (oi.phylum = 'Mollusca' and oi.class IN ('Gastropoda', 'Cephalopoda'))
	or (oi.phylum = 'Mollusca' and oi.family IN ('Tridacnidae','Cardiidae','Pectinidae'))
	or (oi.phylum = 'Echinodermata' and (oi.class IN ('Asteroidea', 'Holothuroidea', 'Echinoidea','Crinoidea')
	                                     or (oi.class = 'Ophiuroidea' and oi."order" ='Phrynophiurida')))
	or (oi.phylum = 'Platyhelminthes')
	or (oi.phylum = 'Cnidaria' and (coalesce(oi.superseded_by, oi.observable_item_name)='Phlyctenactis tuberculosa'))
	or (oi.phylum = 'Cnidaria' and (oi.class IN ('Hydrozoa','Cubozoa')))
	or (phylum = 'Chordata' and oi.family = 'Pyuridae')
	or (phylum = ' Echiura')
	or (phylum = 'Ctenophora')
	or (oi.observable_item_name = 'No species found')
)
group by sm.survey_id,
	sur.country,
	sur.area,
	sit.ecoregion,
	sit.realm,
	sur.location,
	sur.site_code,
	sur.site_name,
    sur.latitude,
    sur.longitude,
	sur.survey_date,
	sur.depth,
	sit.geom,
	sur.program,
	sur.visibility,
	sur.hour,
	sur.survey_latitude,
	sur.survey_longitude,
	div.full_name,
	sm.block_num,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'No specimen found') then 0
		else (replace(meas.measure_name, 'cm', ''))::numeric
	end,
	sm.method_id
)
select survey_id,
country,
area,
ecoregion,
realm,
location,
site_code,
site_name,
latitude,
longitude,
survey_date,
depth,
geom,
program,
visibility,
hour,
survey_latitude,
survey_longitude,
diver,
"method",
block,
phylum,
"class",
"order",
family,
recorded_species_name,
species_name,
taxon,
reporting_name,
bfc.nominal size_class,
total,
biomass
from invert_sized m2
	 inner join bounded_fish_classes bfc on m2.size_class > bfc.lower_bound and m2.size_class <= bfc.upper_bound;

	 /* Endpoint "M2 Cryptic Fish" #170 */
DROP MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish CASCADE;
create materialized view nrmn.ep_m2_cryptic_fish as
with cte_to_force_joins_evaluated_first as(
select
	sm.survey_id,
	sur.country,
	sur.area,
	sit.ecoregion,
	sit.realm,
	sur.location,
	sur.site_code,
	sur.site_name,
    sur.latitude,
    sur.longitude,
	sur.survey_date,
	sur.depth,
	sit.geom,
	sur.program,
	sur.visibility,
	sur.hour,
	sur.survey_latitude,
	sur.survey_longitude,
	div.full_name as diver,
	sm.method_id "method",
	sm.block_num as block,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'No specimen found') then 0
		else (replace(meas.measure_name, 'cm', ''))::numeric
	end size_class,
	sum(obs.measure_value) as total,
	sum(
		nrmn.obs_biomass(
			oi.a,
			oi.b,
			oi.cf, (
			case when meas.measure_name ~ 'cm$' then replace(meas.measure_name, 'cm', '') else '0' end )::float,
			(obs.measure_value)::int,true
		)
	) as biomass
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref meas ON meas.measure_id = obs.measure_id
	inner join nrmn.methods_species ms ON ms.observable_item_id = obs.observable_item_id
where sm.method_id = 2 and ms.method_id = 2
and (
	(oi.class in ('Actinopterygii', 'Elasmobranchii'))
	or (oi.observable_item_name = 'No species found')
)
group by sm.survey_id,
	sur.country,
	sur.area,
	sit.ecoregion,
	sit.realm,
	sur.location,
	sur.site_code,
	sur.site_name,
    sur.latitude,
    sur.longitude,
	sur.survey_date,
	sur.depth,
	sit.geom,
	sur.program,
	sur.visibility,
	sur.hour,
	sur.survey_latitude,
	sur.survey_longitude,
	div.full_name,
	sm.block_num,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'No specimen found') then 0
		else (replace(meas.measure_name, 'cm', ''))::numeric
	end,
	sm.method_id
) select * from cte_to_force_joins_evaluated_first;

-- M2 Inverts
-- 1) 2-decimal coordinate precision
-- 2) remove name of Divers
-- 3) Exclude Recorded Species name and Taxon KEEP Species Name (ie ()bracket and []bracket) and Reporting Species name)
DROP VIEW IF EXISTS nrmn.ep_m2_inverts_public;
CREATE OR REPLACE VIEW  nrmn.ep_m2_inverts_public AS
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
FROM nrmn.ep_m2_inverts epm2i
WHERE epm2i.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id
	WHERE pde.program_id=2);

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
WHERE epm2cf.survey_id NOT IN (
	SELECT survey_id FROM nrmn.ep_survey_list esl
	JOIN nrmn.program_ref pr ON esl.program=pr.program_name
	JOIN nrmn.site_ref sr ON esl.site_code =sr.site_code
	JOIN nrmn.public_data_exclusion pde ON sr.site_id =pde.site_id AND pr.program_id =pde.program_id
	WHERE pde.program_id=2);