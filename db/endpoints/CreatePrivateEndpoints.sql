/* site list */
DROP MATERIALIZED VIEW IF EXISTS nrmn.ep_site_list CASCADE;
CREATE MATERIALIZED VIEW nrmn.ep_site_list AS
SELECT
	sit.country,
	sit.state AS area,
	loc.location_name AS location,
	sit.mpa,
	sit.site_code,
	sit.site_name,
	array_to_string(sit.old_site_code, ',') AS old_site_codes,
	sit.latitude,
	sit.longitude,
	sit.wave_exposure,
	sit.relief,
	sit.slope,
	sit.currents,
	meo.realm,
	meo.province,
	meo.ecoregion,
	meo.lat_zone,
	sit.geom,
	string_agg(DISTINCT pro.program_name, ', ' ORDER BY pro.program_name) AS programs,
	sit.protection_status
FROM nrmn.site_ref sit
		 INNER JOIN nrmn.location_ref loc ON loc.location_id = sit.location_id
		 INNER JOIN nrmn.meow_ecoregions meo ON st_contains(meo.geom, sit.geom)
		 LEFT JOIN nrmn.survey sur ON sur.site_id = sit.site_id
		 LEFT JOIN nrmn.program_ref pro ON sur.program_id = pro.program_id
GROUP BY
	sit.country,
	sit.state,
	loc.location_name,
	sit.mpa,
	sit.site_code,
	sit.site_name,
	array_to_string(sit.old_site_code, ','),
	sit.latitude,
	sit.longitude,
	sit.wave_exposure,
	sit.relief,
	sit.slope,
	sit.currents,
	meo.realm,
	meo.province,
	meo.ecoregion,
	meo.lat_zone,
	sit.geom,
	sit.protection_status;

/* survey list */
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
	sur.survey_date = MAX(sur.survey_date) OVER (PARTITION BY sit.site_code) AS latest_surveydate_for_site,
	(sm13.survey_id IS NOT null)::boolean AS  has_pq_scores_in_db,
	(sm6.survey_id IS NOT null)::boolean AS has_rugosity_scores_in_db,
	(COALESCE(sur.pq_catalogued,false))::boolean AS has_pqs_catalogued_in_db,
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
	(SELECT string_agg(DISTINCT sm1.method_id::character varying(3)::text, ', '::text ORDER BY (sm1.method_id::character varying(3)::text)) AS string_agg
	 FROM nrmn.survey_method sm1 WHERE sm1.survey_id = sur.survey_id AND sm1.survey_not_done=false GROUP BY sm1.survey_id) methods,
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

/* rarity indices "abundance" */
DROP MATERIALIZED VIEW if exists nrmn.ep_rarity_abundance CASCADE;
CREATE MATERIALIZED VIEW nrmn.ep_rarity_abundance AS
with taxa as (
	select
	    oi.observable_item_id,
	    nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) taxon
	from
	    nrmn.observable_item_ref oi
		inner join nrmn.obs_item_type_ref oit on oit.obs_item_type_id = oi.obs_item_type_id
	where
	    oit.obs_item_type_name = 'Species'
		and not nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) ~ 'spp.$'
),
stage2 as (
	select
	    oi.taxon,
	    sm.survey_id,
	    sur.site_id,
	    sm.block_num,
	    sum(obs.measure_value) sumnum
	from
	    nrmn.observation obs
		inner join nrmn.survey_method sm on sm.survey_method_id = obs.survey_method_id
		inner join taxa oi on oi.observable_item_id = obs.observable_item_id
		inner join nrmn.survey sur on sur.survey_id = sm.survey_id
	group by oi.taxon, sm.survey_id, sur.site_id, sm.block_num
)
select
	taxon,
    count(distinct site_id) n_sites,
    count(distinct survey_id) n_surveys,
    count(*) n_blocks,
    avg(sumnum) abundance
from stage2
group by  taxon;

/* Rarity extents (intemediary table) */
DROP MATERIALIZED VIEW if exists nrmn.ep_rarity_extents CASCADE;
CREATE MATERIALIZED VIEW nrmn.ep_rarity_extents AS
with taxa as (
/*
select all taxa as observable_items of (undescribed) species
but no spp. */
	select
	    oi.observable_item_id,
	    nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) taxon
	from
	    nrmn.observable_item_ref oi
		inner join nrmn.obs_item_type_ref oit on oit.obs_item_type_id = oi.obs_item_type_id
	where
		oit.obs_item_type_name in ('Species', 'Undescribed Species')
		and not nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) ~ 'spp.$'
),
taxon_at_site as (
/* determine all distinct species-at-site, i.e. join all taxa with the sites where
 they have ever been observed. */
	select distinct
		taxa.taxon,
		sit.site_id,
		sit.Longitude,
		sit.Latitude
	from
		nrmn.observation obs
		inner join taxa on obs.observable_item_id = taxa.observable_item_id
		inner join nrmn.survey_method sm on obs.survey_method_id = sm.survey_method_id
		inner join nrmn.survey sur on sm.survey_id = sur.survey_id
		inner join nrmn.site_ref sit on  sur.site_id = sit.site_id
)
,taxon_min_max_coords as (
/*
DETERMINE THE minimum and maximum longitudes and latitutes per species
Also destermine the minimum (westernmost) longitude on the eastern hemisphere
("mineastlongitude") and the maximum (easternmost) longitude on the western
hemisphere ("maxwestlongitude"). Also collect points into a multipoint geometry
per species record
*/
	select
		taxon,
		count(distinct site_id) numsites,
		cast (min(latitude) as varchar(25)) minlatitude,
		cast (max(latitude) as varchar(25)) maxlatitude,
		cast (min(longitude) as varchar(25)) minlongitude,
		cast (max(longitude) as varchar(25)) maxlongitude,
		cast (max(case when longitude > 0 then null else longitude end) as varchar(25)) maxwestlongitude,
		cast (min(case when longitude < 0 then null else longitude end) as varchar(25)) mineastlongitude,
		st_setsrid(st_collect((st_point(longitude, latitude))::geometry),4326) points
	from
	    taxon_at_site
	group by
	    taxon
),
taxon_extents_with_2_geoms as (
/*
create a minimum bounding box from the Min/Max Lat/Long
create a pair of minimum bounding boxes (as multipolygon)
for the records that have both a MinEastLongitude and
MaxWestLongitude, effectively spanning the dateline
 */
	select
		taxon,
		numsites,
		st_envelope(
		    st_buffer(
		        (st_geomfromtext(concat('MULTIPOINT  ((', minLongitude, ' ', minLatitude, '), (',
		            maxLongitude, ' ', maxLatitude, '))'), 4326))::geometry, 0.005)) one,
		case
			when MaxWestLongitude is not null and MinEastLongitude is not null then
				st_union(
					st_envelope(
					    st_buffer(
					        (st_geomfromtext(concat('MULTIPOINT  ((', MinEastLongitude, ' ',
					            minlatitude, '), ( 180.0 ', maxlatitude, '))'), 4326))::geometry, 0.005)),
					st_envelope(
					    st_buffer(
					        (st_geomfromtext(concat('MULTIPOINT  ((-180.0 ', MinLatitude,
					            '), ( ', MaxWestLongitude, ' ', maxlatitude, '))'), 4326))::geometry, 0.005))
				)
			else
				null::geometry
		end two
	from
	    taxon_min_max_coords
),
taxon_extents as (
/*
select smallest extent for each species
join with cte to get multipoints
 */
	select
		abc.taxon,
		case when st_area(two) < st_area(one) then two else one end geom,
		def.points
	from
		taxon_extents_with_2_geoms abc
		inner join taxon_min_max_coords def on abc.taxon = def.taxon
),
shift_360 as (
	select tas.taxon,
		   tas.latitude,
		   case
				when st_numgeometries(tex.geom) = 2 and tas.longitude < 0 then tas.longitude + 360
				else tas.longitude
		    end longitude /* Corrected with 360 degress correction*/
	from taxon_extents tex
			inner join taxon_at_site tas on tex.taxon = tas.taxon
),
mean_coords as (
	select taxon,
		   avg(latitude) mean_latitude,
		   avg(longitude) mean_longitude
	from shift_360
	group by taxon
),
unshifted_meancoords as (
	select taxon,
		   mean_latitude,
		   case
				when mean_longitude > 180 Then mean_longitude - 360
				else mean_longitude
			end mean_longitude
	from mean_coords
)
select
	A.*,
	b.mean_latitude,
    b.mean_longitude,
	st_setsrid(st_point(mean_longitude, mean_latitude), 4326) mean_point,
	0.001 * st_length(
		st_geographyfromtext(
			concat('LINESTRING(', mean_longitude::varchar(200), ' ', mean_latitude::varchar(200), ', ', mean_longitude::varchar(200), ' ', (mean_latitude+1.0)::varchar(200), ')')
		)
	) as km_degr_vertical,
	0.001 * st_length(
		st_geographyfromtext(
			concat('LINESTRING(', mean_longitude::varchar(200), ' ', mean_latitude::varchar(200), ', ', (1.0+mean_longitude)::varchar(200), ' ', mean_latitude::varchar(200), ')')
		)
	) as km_degr_horizontal
from taxon_extents a
	 inner join unshifted_meancoords b ON b.taxon = a.taxon;

/* rarity_range */
CREATE MATERIALIZED VIEW nrmn.ep_rarity_range AS
with taxa as (
/*
select all taxa as observable_items of (undescribed) species
but no spp.
 */
	select
	    oi.observable_item_id,
	    nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) taxon
	from
	    nrmn.observable_item_ref oi
		inner join nrmn.obs_item_type_ref oit on oit.obs_item_type_id = oi.obs_item_type_id
	where
		oit.obs_item_type_name in ('Species', 'Undescribed Species')
		and not nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) ~ 'spp.$'
),
taxon_at_site_with_ext as (
/*
determine all distinct species-at-site, i.e. join all taxa with the sites where
they have ever been observed.
join to extent for access to km/degree approximation
apply 360 degree correction, for extents straddling the dateline
 */
	select distinct
		taxa.taxon,
		sit.site_id,
	    case
			when st_numgeometries(ext.geom) = 2  and sit.longitude < 0
			then sit.longitude + 360
			else sit.longitude
		end longitude,
		sit.latitude,
		ext.km_degr_horizontal,
		ext.km_degr_vertical
	from
		nrmn.observation obs
		inner join taxa on obs.observable_item_id = taxa.observable_item_id
		inner join nrmn.survey_method sm on obs.survey_method_id = sm.survey_method_id
		inner join nrmn.survey sur on sm.survey_id = sur.survey_id
		inner join nrmn.site_ref sit on  sur.site_id = sit.site_id
		inner join nrmn.ep_rarity_extents ext on ext.taxon = taxa.taxon
),
stddev_coords_by_taxon as (
	select
		taxon,
		km_degr_vertical,
		km_degr_horizontal,
		count(*) num_sites,
		stddev_pop(latitude) stdev_latitude,
		stddev_pop(longitude) stdev_longitude
	from
		taxon_at_site_with_ext
	group by
		taxon,
		km_degr_vertical,
		km_degr_horizontal
)
select
	taxon,
	num_sites,
	greatest(10, sqrt(power(km_degr_vertical * stdev_latitude, 2.0) + power(km_degr_horizontal * stdev_longitude, 2.0))) "range"
from
	stddev_coords_by_taxon;

/* rarity frequency */
CREATE MATERIALIZED VIEW nrmn.ep_rarity_frequency AS
select
	taxon,
	st_numgeometries(points) FoundAtSitesWithinExtent,
	count(sit.site_id) TotalSitesWithinExtent,
	100.0*st_numgeometries(points)::float / (count(sit.site_id))::float Frequency
from
	nrmn.ep_rarity_extents ext
	inner join nrmn.site_ref sit on st_contains(ext.geom, sit.geom)
group by
	taxon,
	st_numgeometries(points);

/* Endpoint "observable items" */
CREATE MATERIALIZED VIEW nrmn.ep_observable_items AS
with supersedings as (
	select oi.superseded_by currentname,
		STRING_AGG ( oi.observable_item_name, ', ' order by oi.observable_item_name) as names,
		STRING_AGG ( cast(oi.observable_item_id as varchar(5)), ', ' order by oi.observable_item_name) as ids,
		STRING_AGG ( cast(oi.mapped_id as varchar(5)), ', ' order by oi.observable_item_name) as mapped_ids
	from nrmn.observable_item_ref oi
	where oi.superseded_by is not null
	group by oi.superseded_by
)
select
	oi.observable_item_id,
	oi.observable_item_name,
	oit.obs_item_type_name,
	oi.phylum,
	oi.class,
	oi."order",
	oi.family,
	oi.genus,
	replace(oi.common_name, '''', '') as common_name,
	rran.range,
	rfreq.frequency,
	rabu.abundance,
	(oi.obs_item_attribute::jsonb ->> 'MaxLength')::float as max_length,
	cfam.common_name as common_family_name,
	ccla.common_name as common_class_name,
	cphy.common_name as common_phylum_name,
	oi.superseded_by,
	supersedings.ids as superseded_ids,
	supersedings.names as superseded_names,
	lw.a,
	lw.b,
	lw.cf,
	art.aphia_rel_type_name aphia_relation,
	oi.aphia_id,
	aph.scientificname,
	aph.status,
	aph.unacceptreason,
	nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name)) as taxon,
	nrmn.taxonomic_name (coalesce(oi.superseded_by, observable_item_name), false) as reporting_name,
    oi.report_group,
    oi.habitat_groups,
    oi.obs_item_attribute::jsonb ->> 'OtherGroups' other_groups,
    oi.mapped_id,
    supersedings.mapped_ids as mapped_superseded_ids
from nrmn.observable_item_ref oi
	 inner join nrmn.obs_item_type_ref oit ON oit.obs_item_type_id = oi.obs_item_type_id
	 left join supersedings on oi.observable_item_name = supersedings.CurrentName
	 left join nrmn.legacy_common_names cfam on oi.family = cfam.name and cfam.rank = 'Family'
	 left join nrmn.legacy_common_names ccla on oi.class = ccla.name and ccla.rank = 'Class'
	 left join nrmn.legacy_common_names cphy on oi.phylum = cphy.name and cphy.rank = 'Phylum'
     left join nrmn.lengthweight_ref lw on lw.observable_item_id = oi.observable_item_id
	 left join nrmn.aphia_ref aph on aph.aphia_id = oi.aphia_id
	 left join nrmn.aphia_rel_type_ref art on art.aphia_rel_type_id = oi.aphia_rel_type_id
	 left join nrmn.ep_rarity_abundance rabu on rabu.taxon = oi.observable_item_name
	 left join nrmn.ep_rarity_range rran on rran.taxon = oi.observable_item_name
	 left join nrmn.ep_rarity_frequency rfreq on rfreq.taxon = oi.observable_item_name;

/* Endpoint "M1 Fish all" #164 */
CREATE MATERIALIZED VIEW  nrmn.ep_m1 AS
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
CREATE MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish AS
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
	(oi.class in ('Actinopterygii','Actinopteri','Teleostei', 'Elasmobranchii'))
	or (oi.observable_item_name = 'No species found') or (oi.class='Reptilia' AND oi."order"='Squamata'))
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
CREATE MATERIALIZED VIEW nrmn.ep_m2_inverts AS
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
	(oi.phylum = 'Arthropoda' and oi.class ='Malacostraca')
	or (oi.phylum = 'Arthropoda' and oi.class='Pycnogonida' )
	or (oi.phylum = 'Mollusca' and oi.class IN ('Gastropoda', 'Cephalopoda'))
	or (oi.phylum = 'Mollusca' and oi.family IN ('Tridacnidae','Cardiidae','Pectinidae','Pteriidae','Pinnidae'))
	or (oi.phylum = 'Echinodermata' and (oi.class IN ('Asteroidea', 'Holothuroidea', 'Echinoidea','Crinoidea')
	                                     or (oi.class = 'Ophiuroidea' and oi."order" ='Phrynophiurida')))
	or (oi.phylum = 'Platyhelminthes')
	or (oi.phylum = 'Cnidaria' and (coalesce(oi.superseded_by, oi.observable_item_name)='Phlyctenactis tuberculosa'))
	or (oi.phylum = 'Cnidaria' and (oi.class IN ('Hydrozoa','Cubozoa','Scyphozoa','Anthozoa')))
	or (phylum = 'Chordata' and oi.family = 'Pyuridae')
	or (phylum = 'Echiura')
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
	EXCEPT
	SELECT
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
where sm.method_id = 2 and  (oi.phylum = 'Arthropoda' and oi.class ='Malacostraca' and oi.family='Palaemonidae')

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
	sm.method_id)
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
sum(total)::bigint as total,
sum(biomass) as biomass
from invert_sized m2
	 inner join bounded_fish_classes bfc on m2.size_class > bfc.lower_bound and m2.size_class <= bfc.upper_bound
group by survey_id,country,area,ecoregion,realm,location,site_code,site_name,latitude,longitude,survey_date,depth,geom,
program,visibility,hour,survey_latitude,survey_longitude,diver,"method",block,phylum,"class","order",family,
recorded_species_name,species_name,taxon,reporting_name,bfc.nominal;

/* Endpoint "M12 Debris" #172 */
CREATE OR REPLACE VIEW nrmn.ep_m12_debris AS
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
CREATE OR REPLACE VIEW nrmn.ep_m0_off_transect_sighting AS
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
CREATE OR REPLACE VIEW nrmn.ep_m3_isq AS
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

/* Endpoint "species list" #178 */
CREATE OR REPLACE VIEW nrmn.ep_species_list AS
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
	oi.superseded_names,
	oi.mapped_id,
	oi.mapped_superseded_ids
from nrmn.ep_observable_items oi
where oi.obs_item_type_name in ('Species', 'Undescribed Species')
and exists (select 1 from nrmn.observation obs
where obs.observable_item_id = ANY((string_to_array(oi.superseded_ids,',')::integer[] || ARRAY[oi.observable_item_id])))
and oi.superseded_by is NULL
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
	epoi.superseded_names,
	epoi.mapped_id,
	epoi.mapped_superseded_ids
	from nrmn.ep_observable_items epoi
where  epoi.obs_item_type_name in ('Species', 'Undescribed Species')
and exists (select 1 from nrmn.observation obs
where obs.observable_item_id = ANY((string_to_array(epoi.superseded_ids,',')::integer[] || ARRAY[epoi.observable_item_id])))
and epoi.superseded_by is NULL
and (epoi.phylum in ('Cnidaria','Echiura','Heterokontophyta')
or epoi.class in ('Anthozoa','Ascidiacea','Echiuroidea','Phaeophyceae', 'Aves')
or epoi.observable_item_name ~ 'spp.$');

/* Endpoint: species survey #185 */
CREATE OR REPLACE VIEW nrmn.ep_species_survey AS
select obs.observable_item_id as species_id
	  ,sur.survey_id
	  ,round(sur.latitude::numeric, 2) AS latitude
      ,round(sur.longitude::numeric, 2) AS longitude
	  ,sit.realm
	  ,sur.country
	  ,sur.area
	  ,sur.geom
	  ,sur.program
	  ,mapped_id
from nrmn.ep_survey_list sur
	 inner join nrmn.survey_method sm on sur.survey_id = sm.survey_id
	 inner join nrmn.observation obs on obs.survey_method_id = sm.survey_method_id
	 inner join nrmn.ep_observable_items oi on oi.observable_item_id = obs.observable_item_id
	 inner join nrmn.ep_site_list sit on sur.site_code = sit.site_code
where oi.obs_item_type_name in ('Species', 'Undescribed Species');


/*endpoint: species survey observation #186*/
CREATE OR REPLACE VIEW nrmn.ep_species_survey_observation AS
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
	,mapped_id
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
CREATE OR REPLACE VIEW nrmn.ep_m13_pq_scores AS
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
CREATE OR REPLACE VIEW nrmn.ep_m11_off_transect_measurement AS
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
CREATE OR REPLACE VIEW nrmn.ep_m4_macrocystis_count AS
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
CREATE OR REPLACE VIEW nrmn.ep_m5_limpet_quadrats AS
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
CREATE OR REPLACE VIEW nrmn.ep_m7_lobster_count AS
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
CREATE OR REPLACE VIEW nrmn.ep_lobster_haliotis AS
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

/* l5/l95/maxabundance/maxlength stats for observable items - with biais from unsized obs removed */
CREATE OR REPLACE VIEW nrmn.ui_observable_item_stats AS
WITH measure_count AS (
    SELECT
        oir.observable_item_id,
        obs.measure_id,
        max(obs.measure_value) as max_abundance_measure_id,
        sum(obs.measure_value) total_count
    FROM
        nrmn.observable_item_ref oir
        LEFT JOIN nrmn.observable_item_ref xref ON (coalesce(xref.superseded_by, xref.observable_item_name) = coalesce(oir.superseded_by, oir.observable_item_name))
        LEFT JOIN nrmn.observation obs ON (obs.observable_item_id = xref.observable_item_id)
    GROUP BY
        oir.observable_item_id,
        obs.measure_id
),
size_class_count AS (
    SELECT
        measure_count.observable_item_id,
        measure_count.measure_id,
        CASE WHEN meas.measure_name IN ('Unsized', 'No specimen found', 'Item') THEN
            NULL
        WHEN meas.measure_name SIMILAR TO '(B|Q)%' THEN
            NULL
        ELSE
            (replace(meas.measure_name, 'cm', ''))::numeric
        END size_class,
        total_count,
        max_abundance_measure_id
    FROM
        measure_count
        LEFT JOIN nrmn.measure_ref meas ON (meas.measure_id = measure_count.measure_id))
SELECT
    observable_item_id,
    max(max_abundance_measure_id) AS maximum_abundance,
    max(size_class) AS maximum_length,
    nrmn.l5 (size_class, total_count ORDER BY size_class),
    nrmn.l95 (size_class, total_count ORDER BY size_class)
FROM
    size_class_count
GROUP BY
    observable_item_id;

/* Endpoint species_attributes for template data and validation checks */
DROP MATERIALIZED VIEW IF EXISTS nrmn.ui_species_attributes CASCADE;
CREATE MATERIALIZED VIEW nrmn.ui_species_attributes AS
SELECT
    oir.observable_item_id,
    coalesce(oir.superseded_by, oir.observable_item_name) AS species_name,
    oir.common_name,
    CASE
    WHEN oir.is_invert_sized=True THEN 'Yes'
	ELSE 'No'
	END AS is_invert_sized,
    ois.l5,
    ois.l95,
    ois.maximum_abundance AS maxabundance,
    ois.maximum_length AS lmax
FROM
    nrmn.observable_item_ref oir
    JOIN nrmn.obs_item_type_ref oitr ON oitr.obs_item_type_id = oir.obs_item_type_id
    JOIN nrmn.ui_observable_item_stats ois ON ois.observable_item_id = oir.observable_item_id
WHERE
    oitr.obs_item_type_id IN (1, 2);

/* Endpoint ui_client_attributes for template data and validation checks */
CREATE OR replace VIEW nrmn.ui_mpa AS
SELECT DISTINCT mpa FROM nrmn.site_ref WHERE mpa IS NOT NULL;
CREATE OR replace VIEW nrmn.ui_protection_status AS
SELECT DISTINCT initcap(trim(lower(protection_status))) AS protection_status FROM nrmn.site_ref
WHERE protection_status IS NOT NULL;
CREATE OR replace VIEW nrmn.ui_report_group AS
SELECT DISTINCT report_group from nrmn.observable_item_ref WHERE report_group IS NOT NULL;
CREATE OR replace VIEW nrmn.ui_habitat_groups AS
SELECT DISTINCT habitat_groups FROM nrmn.observable_item_ref WHERE habitat_groups IS NOT NULL;


SELECT nrmn.refresh_materialized_views();


