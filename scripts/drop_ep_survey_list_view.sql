DROP MATERIALIZED VIEW nrmn.ep_survey_list CASCADE;
CREATE MATERIALIZED VIEW nrmn.ep_survey_list AS
with divers AS (
	SELECT DISTINCT sm.survey_id, STRING_AGG ( DISTINCT div.full_name, ', ' ORDER BY div.full_name) AS full_names
	FROM nrmn.observation obs
		 INNER JOIN nrmn.survey_method sm ON sm.survey_method_id = obs.survey_method_id
	     INNER JOIN  nrmn.diver_ref div ON div.diver_id = obs.diver_id
	group by sm.survey_id
	)
SELECT
	sur.survey_id,
	sit.country,
	sit.area,
	sit.location,
	sit.mpa,
	sit.site_code,
	sit.site_name,
	sit.latitude,
	sit.longitude,
	sur.depth + 0.1*survey_num AS depth,
	sur.survey_date AS survey_date,
	sur.survey_date = MAX(sur.survey_date) OVER (PARTITION BY sit.site_code) AS "latest surveydate for site",
	(sm13.survey_id IS NOT null)::boolean AS  "has pq scores in db",
	(sm6.survey_id IS NOT null)::boolean AS "has rugosity scores in db",
	(COALESCE(sur.pq_catalogued,false))::boolean AS "has pqs catalogued in db",
	div.full_names AS divers,
	sur.visibility AS visibility,
	sur.survey_time AS hour,
	sur.direction AS direction,
	sur.latitude AS survey_latitude,
	sur.longitude AS survey_longitude,
	rug.avg_rugosity,
	rug.max_rugosity,
	st.surface_type_name surface,
	sit.geom,
	pro.program_name AS program,
	sur.pq_zip_url,
	sur.protection_status,
	sit.old_site_codes,
	(SELECT string_agg(DISTINCT sm1.method_id::varchar(3), ', ' ORDER BY sm1.method_id::varchar(3)) FROM nrmn.survey_method sm1 WHERE sm1.survey_id = sur.survey_id GROUP BY sm1.survey_id) methods,
	sur.notes AS survey_notes
FROM nrmn.survey sur
	 INNER JOIN nrmn.site_ref sit1 ON sit1.site_id = sur.site_id
	 INNER JOIN nrmn.ep_site_list sit ON sit.site_code = sit1.site_code
	 LEFT JOIN divers div ON div.survey_id = sur.survey_id
	 INNER JOIN nrmn.program_ref pro ON sur.program_id = pro.program_id
	 LEFT JOIN nrmn.survey_method sm6 ON sur.survey_id = sm6.survey_id AND sm6.method_id = 6
	 LEFT JOIN nrmn.rugosity rug ON rug.survey_method_id = sm6.survey_method_id
	 LEFT JOIN nrmn.survey_method sm13 ON sur.survey_id = sm13.survey_id AND sm13.method_id = 13
     LEFT JOIN nrmn.surface_type_ref st ON st.surface_type_id = rug.surface_type_id;
	 
/* Endpoint "M1 Fish all" #164 */
create materialized view nrmn.ep_m1 as
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
	sur.geom,
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
where sm.method_id in (1,8,9,10)
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
	sur.geom,
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

/* Endpoint "M2 Cryptic Fish" #170 */
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
where sm.method_id = 2
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


/* Endpoint "M2 Inverts" #171 */
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
	or (oi.phylum = 'Mollusca' and oi.class IN ('Gastropoda', 'Cephalopoda'))
	or (oi.phylum = 'Echinodermata' and (oi.class IN ('Asteroidea', 'Holuthuria', 'Echinoidea')
	                                     or (oi.class = 'Ophiuroidea' and oi."order" ='Phrynophiurida')))
	or (oi.phylum = 'Platyhelminthes')
	or (oi.phylum = 'Cnidaria' and (coalesce(oi.superseded_by, oi.observable_item_name)='Phlyctenactis tuberculosa'))
	or (phylum = 'Chordata' and oi.family <> 'Pyuridae')
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
method,
block,
phylum,
class,
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

/* Endpoint "M12 Debris" #172 */
create or replace view nrmn.ep_m12_debris as
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
	sm.block_num as block,
	oi.observable_item_name as debris,
	obs.measure_value as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
where sm.method_id = 12;

/* Endpoint "M0 off transect sightings" #173 */
create or replace view nrmn.ep_m0_off_transect_sighting as
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
	sm.block_num as block,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	sum(obs.measure_value) as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
where sm.method_id = 0
group by sm.survey_id,
	sur.country,
	sur.area,
	sit.ecoregion,
	sit.realm,
	sur.site_code,
	sur.location,
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
	coalesce(oi.superseded_by, oi.observable_item_name) ,
	oi.taxon,
	oi.reporting_name;

/* Endpoint "M3 in-situ quadrats" #175 */
create or replace view nrmn.ep_m3_isq as
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
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	mr.measure_name quadrat,
	sum(obs.measure_value) as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref mr ON mr.measure_id = obs.measure_id
where sm.method_id = 3
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
	mr.measure_name,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name;

/* Endpoint "species list" #178 */
create or replace view nrmn.ep_species_list as
select
	oi.observable_item_id as species_id,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.genus,
	oi.common_name,
	oi.range,
	oi.frequency,
	oi.abundance,
	oi.max_length,
	common_family_name,
	common_class_name,
	common_phylum_name,
	null as geom,
	oi.superseded_ids,
	oi.superseded_names
from nrmn.ep_observable_items oi
where oi.obs_item_type_name in ('Species', 'Undescribed Species')
and exists (select 1 from nrmn.observation obs where obs.observable_item_id = oi.observable_item_id)
and oi.superseded_by is null
except
select epoi.observable_item_id as species_id,
	epoi.observable_item_name as recorded_species_name,
	coalesce(epoi.superseded_by, epoi.observable_item_name) as species_name,
	epoi.taxon,
	epoi.reporting_name,
	epoi.phylum,
	epoi.class,
	epoi."order",
	epoi.family,
	epoi.genus,
	epoi.common_name,
	epoi.range,
	epoi.frequency,
	epoi.abundance,
	epoi.max_length,
	common_family_name,
	common_class_name,
	common_phylum_name,
	null as geom,
	epoi.superseded_ids,
	epoi.superseded_names from nrmn.ep_observable_items epoi
where  epoi.obs_item_type_name in ('Species', 'Undescribed Species')
and exists (select 1 from nrmn.observation obs where obs.observable_item_id = epoi.observable_item_id)
and epoi.superseded_by is null
and (epoi.phylum in ('Cnidaria','Echiura','Heterokontophyta')
or epoi.class in ('Anthozoa','Ascidiacea','Echiuroidea','Phaeophyceae', 'Aves')
or epoi.observable_item_name ~ 'spp.$');

/* Endpoint: species survey #185 */
create or replace view nrmn.ep_species_survey as
select obs.observable_item_id as species_id
	  ,sur.survey_id
	  ,sur.latitude
	  ,sur.longitude
	  ,sit.realm
	  ,sur.country
	  ,sur.area
	  ,sur.geom
	  ,sur.program
from nrmn.ep_survey_list sur
	 inner join nrmn.survey_method sm on sur.survey_id = sm.survey_id
	 inner join nrmn.observation obs on obs.survey_method_id = sm.survey_method_id
	 inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	 inner join nrmn.ep_site_list sit on sur.site_code = sit.site_code
where oi.obs_item_type_name in ('Species', 'Undescribed Species');


/*endpoint: species survey observation #186*/
create or replace view nrmn.ep_species_survey_observation as
select
	oi.observable_item_id species_id
	,sur.survey_id
	,sur.site_code
	,sur.latitude
	,sur.longitude
	,sur.site_name
	,sit.ecoregion
	,sit.province
	,sit.realm
	,sur.country
	,sur.area
	,sit.location
	,sur.survey_date
	,sur.depth
	,sur.geom
	,sur.program
	,div.full_name diver
	,obs.measure_value total
	,sm.block_num as block
	,sm.method_id
	,meas.measure_name size_class
	,nrmn.obs_biomass(
			oi.a,
			oi.b,
			oi.cf, (
			case when meas.measure_name ~ 'cm$' then replace(meas.measure_name, 'cm', '') else '0' end )::float,
			(obs.measure_value)::int,true
		) biomass
from nrmn.ep_site_list sit
	inner join nrmn.ep_survey_list sur on sit.site_code = sur.site_code
	inner join nrmn.survey_method sm on sur.survey_id = sm.survey_id
	inner join nrmn.observation obs on obs.survey_method_id = sm.survey_method_id
	inner join nrmn.measure_ref meas on obs.measure_id = meas.measure_id
	inner join nrmn.diver_ref div on div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on obs.observable_item_id = oi.observable_item_id
where sm.method_id in (0,1,2,7,8,9,10)
and not oi.observable_item_name ~ 'spp.$'
and not oi.phylum in ('Cnidaria','Echiura','Heterokontophyta')
and not oi.class in ('Anthozoa','Ascidiacea','Echiuroidea','Phaeophyceae', 'Aves');

/* Endpoint pq scores #188 */
create or replace view nrmn.ep_m13_pq_scores as
select
    sur.survey_id,
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
	res.resolution_name as resolution,
	cat.description as category,
	cat.major_category_name as major_category,
	pq.num_points as num_points,
	sum(pq.num_points) over (partition by sm.survey_id, res.resolution_name) as total_points,
	pq.fraction_coverage as percent_cover
from nrmn.pq_score pq
	 inner join nrmn.pq_cat_res_ref cr on pq.cat_res_id = cr.cat_res_id
	 inner join nrmn.pq_resolution_ref res on res.resolution_id = cr.resolution_id
	 inner join nrmn.pq_category_ref cat on cat.category_id = cr.category_id
	 inner join nrmn.survey_method sm on pq.survey_method_id = sm.survey_method_id
	 inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	 inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code;


/* Endpoint: Off transect measurements #189 */
create or replace view nrmn.ep_m11_off_transect_measurement as
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
	obs.measure_value as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref meas ON meas.measure_id = obs.measure_id
where sm.method_id = 11;

/* Endpoint: M4 Macrocystis count #190 */
create or replace view nrmn.ep_m4_macrocystis_count as
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
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	mr.measure_name block,
	sum(obs.measure_value) as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref mr ON mr.measure_id = obs.measure_id
where sm.method_id = 4
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
	mr.measure_name,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name;

/* Endpoint: M5 Limpet Quadrats #191 */
create or replace view nrmn.ep_m5_limpet_quadrats as
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
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	mr.measure_name quadrat,
	sum(obs.measure_value) as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref mr ON mr.measure_id = obs.measure_id
where sm.method_id = 5
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
	mr.measure_name,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name;

/* endpoint M7 Lobster counts jurien bay #192 */
create or replace view nrmn.ep_m7_lobster_count as
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
	sum(obs.measure_value) as total
from nrmn.observation obs
	inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
	inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
	inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
	inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
	inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	inner join nrmn.measure_ref meas ON meas.measure_id = obs.measure_id
where sm.method_id = 7
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
	end;

/* Endpoint "Lobster Haliotis" #302 */
create or replace view nrmn.ep_lobster_haliotis as
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
        sum(obs.measure_value) as total
    from nrmn.observation obs
        inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
        inner join nrmn.ep_survey_list sur on sur.survey_id = sm.survey_id
        inner join nrmn.ep_site_list sit on sit.site_code = sur.site_code
        inner join nrmn.diver_ref div ON div.diver_id = obs.diver_id
        inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
        inner join nrmn.measure_ref meas ON meas.measure_id = obs.measure_id
    where sm.method_id = 2
    and (
        (oi.family = 'Palinuridae')
        or (oi.family = 'Haliotidae')
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
        end
) select * from cte_to_force_joins_evaluated_first;
