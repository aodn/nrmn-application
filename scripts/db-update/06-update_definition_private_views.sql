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
	or (oi.phylum = 'Mollusca' and oi.class IN ('Gastropoda', 'Cephalopoda'))
	or (oi.phylum = 'Mollusca' and oi.family IN ('Tridacnidae','Cardiidae'))
	or (oi.phylum = 'Echinodermata' and (oi.class IN ('Asteroidea', 'Holuthuria', 'Echinoidea')
	                                     or (oi.class = 'Ophiuroidea' and oi."order" ='Phrynophiurida')))
	or (oi.phylum = 'Platyhelminthes')
	or (oi.phylum = 'Cnidaria' and (coalesce(oi.superseded_by, oi.observable_item_name)='Phlyctenactis tuberculosa'))
	or (phylum = 'Chordata' and oi.family = 'Pyuridae')
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


/* Endpoint "M0 off transect sightings" #173 */
drop view if exists  nrmn.ep_m0_off_transect_sighting cascade;
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
	sm.method_id "method",
	sm.block_num as block,
	oi.phylum,
	oi."class",
	oi."order",
	oi.family,
	oi.observable_item_name as recorded_species_name,
	coalesce(oi.superseded_by, oi.observable_item_name) as species_name,
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'Item', 'No specimen found') then 0
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
	sm.method_id,
	sm.block_num,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name) ,
	oi.taxon,
	oi.reporting_name,
	case
		when meas.measure_name in ('Unsized', 'Item', 'No specimen found') then 0
		else (replace(meas.measure_name, 'cm', ''))::numeric
	end;

/* Endpoint "M3 in-situ quadrats" #175 */
drop view if exists nrmn.ep_m3_isq cascade;
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
	oi.report_group,
	oi.habitat_groups,
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
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.observable_item_name,
	coalesce(oi.superseded_by, oi.observable_item_name),
	oi.taxon,
	oi.reporting_name,
	oi.report_group,
	oi.habitat_groups,
	mr.measure_name;

/* Endpoint: species survey #185 */
create or replace view nrmn.ep_species_survey as
select obs.observable_item_id as species_id
	  ,sur.survey_id
	  ,round(sur.latitude::numeric, 2) AS latitude
      ,round(sur.longitude::numeric, 2) AS longitude
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
	,round(sur.latitude::numeric, 2) AS latitude
    ,round(sur.longitude::numeric, 2) AS longitude
	,sur.site_name
	,sit.ecoregion
	,sit.province
	,sit.realm
	,sur.country
	,sur.area
	,sit.location
	,sur.survey_date
	,sur.depth
	,ST_SetSrid(ST_MakePoint(round (sur.latitude::numeric, 2), round (sur.longitude::numeric, 2)),4326)::geometry AS geom
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

