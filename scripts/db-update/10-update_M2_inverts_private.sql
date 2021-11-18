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
	or (oi.phylum = 'Cnidaria' and (oi.class IN ('Hydrozoa','Cubozoa')
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

