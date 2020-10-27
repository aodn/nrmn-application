--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 13.0 (Ubuntu 13.0-1.pgdg20.04+1)

-- Started on 2020-10-14 16:03:48 AEDT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;

SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 13 (class 2615 OID 17868)
-- Name: nrmn; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nrmn;


--
-- TOC entry 430 (class 1255 OID 78212)
-- Name: obs_biomass(double precision, double precision, double precision, double precision, integer, boolean); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.obs_biomass(a double precision, b double precision, cf double precision, sizeclass double precision, num integer, use_sizeclass_correction boolean DEFAULT false) RETURNS double precision
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '

begin

sizeclass = case
 when sizeclass = 0 then null
 when not use_sizeclass_correction then sizeclass
 when sizeclass <= 15.0 then 1.2 * sizeclass
 when sizeclass <= 35.0 then 0.712 * sizeclass  + 9.02
 when sizeclass > 35.0 then 0.9244 * sizeclass - 0.2624
end;

sizeclass = cast(sizeclass as numeric(5,1));

return (a * (cf * sizeclass)^b * num);

end; ';


--
-- TOC entry 433 (class 1255 OID 78213)
-- Name: taxonomic_name(character varying, boolean); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.taxonomic_name(obs_item_name character varying, allow_square_brackets boolean DEFAULT true) RETURNS character varying
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
declare taxonomic_name   varchar(100);
begin

 select
  case
   when oi.obs_item_attribute::jsonb ->> ''Phylum''=''Substrate''
     then oi.observable_item_name
   when observable_item_name ~ ''[\[\]]'' and allow_square_brackets
     then oi.observable_item_name
   when oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and oi.obs_item_attribute::jsonb ? ''Genus''
     then concat(oi.obs_item_attribute::jsonb ->> ''Genus'', '' '', oi.obs_item_attribute::jsonb ->> ''SpeciesEpithet'')
   when not oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and oi.obs_item_attribute::jsonb ? ''Genus''
     then concat(oi.obs_item_attribute::jsonb ->> ''Genus'', '' spp.'')
   when not oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and not oi.obs_item_attribute::jsonb ? ''Genus''
     and oi.obs_item_attribute::jsonb ? ''Family''
     then concat(replace(oi.obs_item_attribute::jsonb ->> ''Family'', ''idae'', ''id''), '' spp.'')
   when not oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and not oi.obs_item_attribute::jsonb ? ''Genus''
     and not oi.obs_item_attribute::jsonb ? ''Family'' and oi.obs_item_attribute::jsonb ? ''Order''
     then concat(oi.obs_item_attribute::jsonb ->> ''Order'', '' spp.'')
   when not oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and not oi.obs_item_attribute::jsonb ? ''Genus''
     and not oi.obs_item_attribute::jsonb ? ''Family'' and not oi.obs_item_attribute::jsonb ? ''Order''
     and oi.obs_item_attribute::jsonb ? ''Class''
     then concat(oi.obs_item_attribute::jsonb ->> ''Class'', '' spp.'')
   when not oi.obs_item_attribute::jsonb ? ''SpeciesEpithet'' and not oi.obs_item_attribute::jsonb ? ''Genus''
     and not oi.obs_item_attribute::jsonb ? ''Family'' and not oi.obs_item_attribute::jsonb ? ''Order''
     and not oi.obs_item_attribute::jsonb ? ''Class'' and oi.obs_item_attribute::jsonb ? ''Phylum''
     then concat(oi.obs_item_attribute::jsonb ->> ''Phylum'', '' spp.'')
  end taxonomic_name
  into taxonomic_name
from observable_item_ref oi
where oi.observable_item_name = obs_item_name;

return taxonomic_name;

end; ';


SET default_tablespace = '';

--
-- TOC entry 207 (class 1259 OID 78214)
-- Name: aphia_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.aphia_ref (
    aphia_id integer NOT NULL,
    url character varying(100),
    scientificname character varying(100),
    authority character varying(100),
    status character varying(100),
    unacceptreason character varying(200),
    taxon_rank_id integer,
    rank character varying(50),
    valid_aphia_id integer,
    valid_name character varying(100),
    valid_authority character varying(100),
    parent_name_usage_id integer,
    rank_kingdom character varying(50),
    rank_phylum character varying(50),
    rank_class character varying(50),
    rank_order character varying(50),
    rank_family character varying(50),
    rank_genus character varying(50),
    citation text,
    lsid character varying(50),
    is_marine boolean,
    is_brackish boolean,
    is_freshwater boolean,
    is_terrestrial boolean,
    is_extinct boolean,
    match_type character varying(50),
    modified timestamp without time zone
);


--
-- TOC entry 208 (class 1259 OID 78220)
-- Name: aphia_rel_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.aphia_rel_type_ref (
    aphia_rel_type_id integer NOT NULL,
    aphia_rel_type_name character varying(50)
);


--
-- TOC entry 209 (class 1259 OID 78223)
-- Name: atrc_rugosity; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.atrc_rugosity (
    survey_id integer,
    rugosity double precision
);


--
-- TOC entry 210 (class 1259 OID 78226)
-- Name: diver_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.diver_ref (
    diver_id integer NOT NULL,
    initials character varying(10) NOT NULL,
    full_name character varying(100)
);


--
-- TOC entry 211 (class 1259 OID 78229)
-- Name: diver_ref_diver_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.diver_ref_diver_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4031 (class 0 OID 0)
-- Dependencies: 211
-- Name: diver_ref_diver_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.diver_ref_diver_id OWNED BY nrmn.diver_ref.diver_id;


--
-- TOC entry 212 (class 1259 OID 78231)
-- Name: obs_item_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.obs_item_type_ref (
    obs_item_type_id integer NOT NULL,
    obs_item_type_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 213 (class 1259 OID 78234)
-- Name: observable_item_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observable_item_ref (
    observable_item_id integer NOT NULL,
    observable_item_name character varying(100) NOT NULL,
    obs_item_type_id integer NOT NULL,
    aphia_id integer,
    aphia_rel_type_id integer,
    obs_item_attribute jsonb
);


--
-- TOC entry 214 (class 1259 OID 78240)
-- Name: observation; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observation (
    observation_id integer NOT NULL,
    survey_method_id integer NOT NULL,
    diver_id integer,
    observable_item_id integer NOT NULL,
    measure_id integer NOT NULL,
    measure_value integer,
    observation_attribute jsonb
);


--
-- TOC entry 215 (class 1259 OID 78246)
-- Name: survey; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey (
    survey_id integer NOT NULL,
    site_id integer NOT NULL,
    program_id integer NOT NULL,
    survey_date date NOT NULL,
    survey_time time without time zone,
    depth integer NOT NULL,
    survey_num integer NOT NULL,
    visibility integer,
    direction character varying(10),
    survey_attribute jsonb
);


--
-- TOC entry 216 (class 1259 OID 78252)
-- Name: survey_method; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey_method (
    survey_method_id integer NOT NULL,
    survey_id integer NOT NULL,
    method_id integer NOT NULL,
    block_num integer,
    survey_not_done boolean,
    survey_method_attribute jsonb
);


--
-- TOC entry 217 (class 1259 OID 78258)
-- Name: ep_rarity_abundance; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_abundance AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = 'Species'::text) AND (NOT ((nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying))::text ~ 'spp.$'::text)))
        ), stage2 AS (
         SELECT oi.taxon,
            sm.survey_id,
            sur.site_id,
            sm.block_num,
            sum(obs.measure_value) AS sumnum
           FROM (((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN taxa oi ON ((oi.observable_item_id = obs.observable_item_id)))
             JOIN nrmn.survey sur ON ((sur.survey_id = sm.survey_id)))
          GROUP BY oi.taxon, sm.survey_id, sur.site_id, sm.block_num
        )
 SELECT stage2.taxon,
    count(DISTINCT stage2.site_id) AS n_sites,
    count(DISTINCT stage2.survey_id) AS n_surveys,
    count(*) AS n_blocks,
    avg(stage2.sumnum) AS abundance
   FROM stage2
  GROUP BY stage2.taxon
  WITH NO DATA;


--
-- TOC entry 218 (class 1259 OID 78266)
-- Name: site_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.site_ref (
    site_id integer NOT NULL,
    site_code character varying(10) NOT NULL,
    site_name character varying(100) NOT NULL,
    longitude double precision,
    latitude double precision,
    geom public.geometry,
    location_id integer NOT NULL,
    site_attribute jsonb,
    is_active boolean
);


--
-- TOC entry 219 (class 1259 OID 78272)
-- Name: ep_rarity_extents; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_extents AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = ANY (ARRAY[('Species'::character varying)::text, ('Undescribed Species'::character varying)::text])) AND (NOT ((nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying))::text ~ 'spp.$'::text)))
        ), taxon_at_site AS (
         SELECT DISTINCT taxa.taxon,
            sit.site_id,
            sit.longitude,
            sit.latitude
           FROM ((((nrmn.observation obs
             JOIN taxa ON ((obs.observable_item_id = taxa.observable_item_id)))
             JOIN nrmn.survey_method sm ON ((obs.survey_method_id = sm.survey_method_id)))
             JOIN nrmn.survey sur ON ((sm.survey_id = sur.survey_id)))
             JOIN nrmn.site_ref sit ON ((sur.site_id = sit.site_id)))
        ), taxon_min_max_coords AS (
         SELECT taxon_at_site.taxon,
            count(DISTINCT taxon_at_site.site_id) AS numsites,
            (min(taxon_at_site.latitude))::character varying(25) AS minlatitude,
            (max(taxon_at_site.latitude))::character varying(25) AS maxlatitude,
            (min(taxon_at_site.longitude))::character varying(25) AS minlongitude,
            (max(taxon_at_site.longitude))::character varying(25) AS maxlongitude,
            (max(
                CASE
                    WHEN (taxon_at_site.longitude > (0)::double precision) THEN NULL::double precision
                    ELSE taxon_at_site.longitude
                END))::character varying(25) AS maxwestlongitude,
            (min(
                CASE
                    WHEN (taxon_at_site.longitude < (0)::double precision) THEN NULL::double precision
                    ELSE taxon_at_site.longitude
                END))::character varying(25) AS mineastlongitude,
            public.st_setsrid(public.st_collect(public.st_point(taxon_at_site.longitude, taxon_at_site.latitude)), 4326) AS points
           FROM taxon_at_site
          GROUP BY taxon_at_site.taxon
        ), taxon_extents_with_2_geoms AS (
         SELECT taxon_min_max_coords.taxon,
            taxon_min_max_coords.numsites,
            public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT ((', taxon_min_max_coords.minlongitude, ' ', taxon_min_max_coords.minlatitude, '), (', taxon_min_max_coords.maxlongitude, ' ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)) AS one,
                CASE
                    WHEN ((taxon_min_max_coords.maxwestlongitude IS NOT NULL) AND (taxon_min_max_coords.mineastlongitude IS NOT NULL)) THEN public.st_union(public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT ((', taxon_min_max_coords.mineastlongitude, ' ', taxon_min_max_coords.minlatitude, '), ( 180.0 ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)), public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT ((-180.0 ', taxon_min_max_coords.minlatitude, '), ( ', taxon_min_max_coords.maxwestlongitude, ' ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)))
                    ELSE NULL::public.geometry
                END AS two
           FROM taxon_min_max_coords
        ), taxon_extents AS (
         SELECT abc.taxon,
                CASE
                    WHEN (public.st_area(abc.two) < public.st_area(abc.one)) THEN abc.two
                    ELSE abc.one
                END AS geom,
            def.points
           FROM (taxon_extents_with_2_geoms abc
             JOIN taxon_min_max_coords def ON (((abc.taxon)::text = (def.taxon)::text)))
        ), shift_360 AS (
         SELECT tas.taxon,
            tas.latitude,
                CASE
                    WHEN ((public.st_numgeometries(tex.geom) = 2) AND (tas.longitude < (0)::double precision)) THEN (tas.longitude + (360)::double precision)
                    ELSE tas.longitude
                END AS longitude
           FROM (taxon_extents tex
             JOIN taxon_at_site tas ON (((tex.taxon)::text = (tas.taxon)::text)))
        ), mean_coords AS (
         SELECT shift_360.taxon,
            avg(shift_360.latitude) AS mean_latitude,
            avg(shift_360.longitude) AS mean_longitude
           FROM shift_360
          GROUP BY shift_360.taxon
        ), unshifted_meancoords AS (
         SELECT mean_coords.taxon,
            mean_coords.mean_latitude,
                CASE
                    WHEN (mean_coords.mean_longitude > (180)::double precision) THEN (mean_coords.mean_longitude - (360)::double precision)
                    ELSE mean_coords.mean_longitude
                END AS mean_longitude
           FROM mean_coords
        )
 SELECT a.taxon,
    a.geom,
    a.points,
    b.mean_latitude,
    b.mean_longitude,
    public.st_setsrid(public.st_point(b.mean_longitude, b.mean_latitude), 4326) AS mean_point,
    ((0.001)::double precision * public.st_length(public.st_geographyfromtext(concat('LINESTRING(', (b.mean_longitude)::character varying(200), ' ', (b.mean_latitude)::character varying(200), ', ', (b.mean_longitude)::character varying(200), ' ', ((b.mean_latitude + (1.0)::double precision))::character varying(200), ')')))) AS km_degr_vertical,
    ((0.001)::double precision * public.st_length(public.st_geographyfromtext(concat('LINESTRING(', (b.mean_longitude)::character varying(200), ' ', (b.mean_latitude)::character varying(200), ', ', (((1.0)::double precision + b.mean_longitude))::character varying(200), ' ', (b.mean_latitude)::character varying(200), ')')))) AS km_degr_horizontal
   FROM (taxon_extents a
     JOIN unshifted_meancoords b ON (((b.taxon)::text = (a.taxon)::text)))
  WITH NO DATA;


--
-- TOC entry 220 (class 1259 OID 78280)
-- Name: ep_rarity_frequency; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_frequency AS
 SELECT ext.taxon,
    public.st_numgeometries(ext.points) AS foundatsiteswithinextent,
    count(sit.site_id) AS totalsiteswithinextent,
    (((100.0)::double precision * (public.st_numgeometries(ext.points))::double precision) / (count(sit.site_id))::double precision) AS frequency
   FROM (nrmn.ep_rarity_extents ext
     JOIN nrmn.site_ref sit ON (public.st_contains(ext.geom, sit.geom)))
  GROUP BY ext.taxon, (public.st_numgeometries(ext.points))
  WITH NO DATA;


--
-- TOC entry 221 (class 1259 OID 78288)
-- Name: ep_rarity_range; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_range AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = ANY (ARRAY[('Species'::character varying)::text, ('Undescribed Species'::character varying)::text])) AND (NOT ((nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying))::text ~ 'spp.$'::text)))
        ), taxon_at_site_with_ext AS (
         SELECT DISTINCT taxa.taxon,
            sit.site_id,
                CASE
                    WHEN ((public.st_numgeometries(ext.geom) = 2) AND (sit.longitude < (0)::double precision)) THEN (sit.longitude + (360)::double precision)
                    ELSE sit.longitude
                END AS longitude,
            sit.latitude,
            ext.km_degr_horizontal,
            ext.km_degr_vertical
           FROM (((((nrmn.observation obs
             JOIN taxa ON ((obs.observable_item_id = taxa.observable_item_id)))
             JOIN nrmn.survey_method sm ON ((obs.survey_method_id = sm.survey_method_id)))
             JOIN nrmn.survey sur ON ((sm.survey_id = sur.survey_id)))
             JOIN nrmn.site_ref sit ON ((sur.site_id = sit.site_id)))
             JOIN nrmn.ep_rarity_extents ext ON (((ext.taxon)::text = (taxa.taxon)::text)))
        ), stddev_coords_by_taxon AS (
         SELECT taxon_at_site_with_ext.taxon,
            taxon_at_site_with_ext.km_degr_vertical,
            taxon_at_site_with_ext.km_degr_horizontal,
            count(*) AS num_sites,
            stddev_pop(taxon_at_site_with_ext.latitude) AS stdev_latitude,
            stddev_pop(taxon_at_site_with_ext.longitude) AS stdev_longitude
           FROM taxon_at_site_with_ext
          GROUP BY taxon_at_site_with_ext.taxon, taxon_at_site_with_ext.km_degr_vertical, taxon_at_site_with_ext.km_degr_horizontal
        )
 SELECT stddev_coords_by_taxon.taxon,
    stddev_coords_by_taxon.num_sites,
    GREATEST((10)::double precision, sqrt((power((stddev_coords_by_taxon.km_degr_vertical * stddev_coords_by_taxon.stdev_latitude), (2.0)::double precision) + power((stddev_coords_by_taxon.km_degr_horizontal * stddev_coords_by_taxon.stdev_longitude), (2.0)::double precision)))) AS range
   FROM stddev_coords_by_taxon
  WITH NO DATA;


--
-- TOC entry 222 (class 1259 OID 78296)
-- Name: legacy_common_names; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.legacy_common_names (
    rank character varying(6),
    name character varying(18) NOT NULL,
    common_name character varying(32)
);


--
-- TOC entry 223 (class 1259 OID 78299)
-- Name: lengthweight_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.lengthweight_ref (
    observable_item_id integer NOT NULL,
    a double precision,
    b double precision,
    cf double precision,
    sgfgu character varying(2)
);


--
-- TOC entry 224 (class 1259 OID 78302)
-- Name: ep_observable_items; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_observable_items AS
 WITH supersedings AS (
         SELECT (oi_1.obs_item_attribute ->> 'SupersededBy'::text) AS currentname,
            string_agg((oi_1.observable_item_name)::text, ', '::text ORDER BY (oi_1.observable_item_name)::text) AS names,
            string_agg(((oi_1.observable_item_id)::character varying(5))::text, ', '::text ORDER BY oi_1.observable_item_name) AS ids
           FROM nrmn.observable_item_ref oi_1
          WHERE (oi_1.obs_item_attribute ? 'SupersededBy'::text)
          GROUP BY (oi_1.obs_item_attribute ->> 'SupersededBy'::text)
        )
 SELECT oi.observable_item_id,
    oi.observable_item_name,
    oit.obs_item_type_name,
    (oi.obs_item_attribute ->> 'Phylum'::text) AS phylum,
    (oi.obs_item_attribute ->> 'Class'::text) AS class,
    (oi.obs_item_attribute ->> 'Order'::text) AS "order",
    (oi.obs_item_attribute ->> 'Family'::text) AS family,
    (oi.obs_item_attribute ->> 'Genus'::text) AS genus,
    replace((oi.obs_item_attribute ->> 'CommonName'::text), ''''::text, ''::text) AS common_name,
    rran.range,
    rfreq.frequency,
    rabu.abundance,
    ((oi.obs_item_attribute ->> 'MaxLength'::text))::double precision AS max_length,
    cfam.common_name AS common_family_name,
    ccla.common_name AS common_class_name,
    cphy.common_name AS common_phylum_name,
    (oi.obs_item_attribute ->> 'SupersededBy'::text) AS superseded_by,
    supersedings.ids AS superseded_ids,
    supersedings.names AS superseded_names,
    lw.a,
    lw.b,
    lw.cf,
    art.aphia_rel_type_name AS aphia_relation,
    oi.aphia_id,
    aph.scientificname,
    aph.status,
    aph.unacceptreason,
    nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying) AS taxon,
    nrmn.taxonomic_name((COALESCE((oi.obs_item_attribute ->> 'SupersededBy'::text), (oi.observable_item_name)::text))::character varying, false) AS reporting_name,
    (oi.obs_item_attribute ->> 'ReportGroup'::text) AS report_group,
    (oi.obs_item_attribute ->> 'HabitatGroups'::text) AS habitat_groups,
    (oi.obs_item_attribute ->> 'OtherGroups'::text) AS other_groups
   FROM (((((((((((nrmn.observable_item_ref oi
     JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
     LEFT JOIN supersedings ON (((oi.observable_item_name)::text = supersedings.currentname)))
     LEFT JOIN nrmn.legacy_common_names cfam ON ((((oi.obs_item_attribute ->> 'Family'::text) = (cfam.name)::text) AND ((cfam.rank)::text = 'Family'::text))))
     LEFT JOIN nrmn.legacy_common_names ccla ON ((((oi.obs_item_attribute ->> 'Class'::text) = (ccla.name)::text) AND ((ccla.rank)::text = 'Class'::text))))
     LEFT JOIN nrmn.legacy_common_names cphy ON ((((oi.obs_item_attribute ->> 'Phylum'::text) = (cphy.name)::text) AND ((cphy.rank)::text = 'Phylum'::text))))
     LEFT JOIN nrmn.lengthweight_ref lw ON ((lw.observable_item_id = oi.observable_item_id)))
     LEFT JOIN nrmn.aphia_ref aph ON ((aph.aphia_id = oi.aphia_id)))
     LEFT JOIN nrmn.aphia_rel_type_ref art ON ((art.aphia_rel_type_id = oi.aphia_rel_type_id)))
     LEFT JOIN nrmn.ep_rarity_abundance rabu ON (((rabu.taxon)::text = (oi.observable_item_name)::text)))
     LEFT JOIN nrmn.ep_rarity_range rran ON (((rran.taxon)::text = (oi.observable_item_name)::text)))
     LEFT JOIN nrmn.ep_rarity_frequency rfreq ON (((rfreq.taxon)::text = (oi.observable_item_name)::text)))
  WITH NO DATA;


--
-- TOC entry 225 (class 1259 OID 78310)
-- Name: location_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.location_ref (
    location_id integer NOT NULL,
    location_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 226 (class 1259 OID 78313)
-- Name: meow_ecoregions; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.meow_ecoregions (
    id integer NOT NULL,
    ecoregion character varying(50),
    province character varying(40),
    realm character varying(40),
    lat_zone character varying(10),
    geom public.geometry(MultiPolygon,4326)
);


--
-- TOC entry 227 (class 1259 OID 78319)
-- Name: program_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.program_ref (
    program_id integer NOT NULL,
    program_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 228 (class 1259 OID 78322)
-- Name: ep_site_list; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_site_list AS
 SELECT (sit.site_attribute ->> 'Country'::text) AS country,
    (sit.site_attribute ->> 'State'::text) AS area,
    loc.location_name AS location,
    (sit.site_attribute ->> 'MPA'::text) AS mpa,
    sit.site_code,
    sit.site_name,
    regexp_replace((sit.site_attribute ->> 'OldSiteCodes'::text), '["\[\]]'::text, ''::text, 'gi'::text) AS old_site_codes,
    sit.latitude,
    sit.longitude,
    ((sit.site_attribute ->> 'WaveExposure1'::text))::integer AS wave_exposure,
    ((sit.site_attribute ->> 'Relief'::text))::integer AS relief,
    ((sit.site_attribute ->> 'Slope'::text))::integer AS slope,
    ((sit.site_attribute ->> 'Currents'::text))::integer AS currents,
    meo.realm,
    meo.province,
    meo.ecoregion,
    meo.lat_zone,
    sit.geom,
    string_agg(DISTINCT (pro.program_name)::text, ', '::text ORDER BY (pro.program_name)::text) AS programs,
    (sit.site_attribute ->> 'ProtectionStatus'::text) AS protection_status
   FROM ((((nrmn.site_ref sit
     JOIN nrmn.location_ref loc ON ((loc.location_id = sit.location_id)))
     JOIN nrmn.meow_ecoregions meo ON (public.st_contains(meo.geom, sit.geom)))
     LEFT JOIN nrmn.survey sur ON ((sur.site_id = sit.site_id)))
     LEFT JOIN nrmn.program_ref pro ON ((sur.program_id = pro.program_id)))
  GROUP BY (sit.site_attribute ->> 'Country'::text), (sit.site_attribute ->> 'State'::text), loc.location_name, (sit.site_attribute ->> 'MPA'::text), sit.site_code, sit.site_name, (regexp_replace((sit.site_attribute ->> 'OldSiteCodes'::text), '["\[\]]'::text, ''::text, 'gi'::text)), sit.latitude, sit.longitude, ((sit.site_attribute ->> 'WaveExposure1'::text))::integer, ((sit.site_attribute ->> 'Relief'::text))::integer, ((sit.site_attribute ->> 'Slope'::text))::integer, ((sit.site_attribute ->> 'Currents'::text))::integer, meo.realm, meo.province, meo.ecoregion, meo.lat_zone, sit.geom, (sit.site_attribute ->> 'ProtectionStatus'::text)
  WITH NO DATA;


--
-- TOC entry 229 (class 1259 OID 78330)
-- Name: rugosity; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.rugosity (
    rugosity_id integer NOT NULL,
    survey_method_id integer NOT NULL,
    avg_rugosity double precision,
    max_rugosity double precision,
    surface_type_id integer
);


--
-- TOC entry 230 (class 1259 OID 78333)
-- Name: surface_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.surface_type_ref (
    surface_type_id integer NOT NULL,
    surface_type_name character varying(50) NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 78336)
-- Name: ep_survey_list; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_survey_list AS
 WITH divers AS (
         SELECT DISTINCT sm.survey_id,
            string_agg(DISTINCT (div_1.full_name)::text, ', '::text ORDER BY (div_1.full_name)::text) AS full_names
           FROM ((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN nrmn.diver_ref div_1 ON ((div_1.diver_id = obs.diver_id)))
          GROUP BY sm.survey_id
        )
 SELECT sur.survey_id,
    sit.country,
    sit.area,
    sit.location,
    sit.mpa,
    sit.site_code,
    sit.site_name,
    sit.latitude,
    sit.longitude,
    ((sur.depth)::numeric + (0.1 * (sur.survey_num)::numeric)) AS depth,
    sur.survey_date,
    (sur.survey_date = max(sur.survey_date) OVER (PARTITION BY sit.site_code)) AS "latest surveydate for site",
    (sm13.survey_id IS NOT NULL) AS "has pq scores in db",
    (sm6.survey_id IS NOT NULL) AS "has rugosity scores in db",
    (sur.survey_attribute ? 'pq_catalogued'::text) AS "has pqs catalogued in db",
    div.full_names AS divers,
    sur.visibility,
    sur.survey_time AS hour,
    sur.direction,
    (sur.survey_attribute ->> 'SurveyLatitude'::text) AS survey_latitude,
    (sur.survey_attribute ->> 'SurveyLongitude'::text) AS survey_longitude,
    rug.avg_rugosity,
    rug.max_rugosity,
    st.surface_type_name AS surface,
    sit.geom,
    pro.program_name AS program,
        CASE
            WHEN (sur.survey_attribute ? 'pq_catalogued'::text) THEN concat('http://rls.tpac.org.au/pq/', (sur.survey_id)::character varying(10), '/zip')
            ELSE NULL::text
        END AS pq_zip_url,
    (sur.survey_attribute ->> 'SurveyProtectionStatus'::text) AS protection_status,
    sit.old_site_codes,
    ( SELECT string_agg(DISTINCT ((sm1.method_id)::character varying(3))::text, ', '::text ORDER BY ((sm1.method_id)::character varying(3))::text) AS string_agg
           FROM nrmn.survey_method sm1
          WHERE (sm1.survey_id = sur.survey_id)
          GROUP BY sm1.survey_id) AS methods,
    (sur.survey_attribute ->> 'Notes'::text) AS survey_notes
   FROM ((((((((nrmn.survey sur
     JOIN nrmn.site_ref sit1 ON ((sit1.site_id = sur.site_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sit1.site_code)::text)))
     LEFT JOIN divers div ON ((div.survey_id = sur.survey_id)))
     JOIN nrmn.program_ref pro ON ((sur.program_id = pro.program_id)))
     LEFT JOIN nrmn.survey_method sm6 ON (((sur.survey_id = sm6.survey_id) AND (sm6.method_id = 6))))
     LEFT JOIN nrmn.rugosity rug ON ((rug.survey_method_id = sm6.survey_method_id)))
     LEFT JOIN nrmn.survey_method sm13 ON (((sur.survey_id = sm13.survey_id) AND (sm13.method_id = 13))))
     LEFT JOIN nrmn.surface_type_ref st ON ((st.surface_type_id = rug.surface_type_id)))
  WITH NO DATA;


--
-- TOC entry 232 (class 1259 OID 78344)
-- Name: measure_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.measure_ref (
    measure_id integer NOT NULL,
    measure_type_id integer NOT NULL,
    measure_name character varying(20) NOT NULL,
    seq_no integer,
    is_active boolean
);


--
-- TOC entry 233 (class 1259 OID 78347)
-- Name: ep_lobster_haliotis; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_lobster_haliotis AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
            sur.country,
            sur.area,
            sit.ecoregion,
            sit.realm,
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
            sur.location,
            sur.survey_latitude,
            sur.survey_longitude,
            div.full_name AS diver,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END AS size_class,
            sum(obs.measure_value) AS total
           FROM ((((((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
             JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
             JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
             JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
             JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
          WHERE ((sm.method_id = 2) AND ((oi.family = 'Palinuridae'::text) OR (oi.family = 'Haliotidae'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.site_code,
    cte_to_force_joins_evaluated_first.site_name,
    cte_to_force_joins_evaluated_first.latitude,
    cte_to_force_joins_evaluated_first.longitude,
    cte_to_force_joins_evaluated_first.survey_date,
    cte_to_force_joins_evaluated_first.depth,
    cte_to_force_joins_evaluated_first.geom,
    cte_to_force_joins_evaluated_first.program,
    cte_to_force_joins_evaluated_first.visibility,
    cte_to_force_joins_evaluated_first.hour,
    cte_to_force_joins_evaluated_first.location,
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first.family,
    cte_to_force_joins_evaluated_first.recorded_species_name,
    cte_to_force_joins_evaluated_first.species_name,
    cte_to_force_joins_evaluated_first.taxon,
    cte_to_force_joins_evaluated_first.reporting_name,
    cte_to_force_joins_evaluated_first.size_class,
    cte_to_force_joins_evaluated_first.total
   FROM cte_to_force_joins_evaluated_first;


--
-- TOC entry 234 (class 1259 OID 78352)
-- Name: ep_m0_off_transect_sighting; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m0_off_transect_sighting AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    sum(obs.measure_value) AS total
   FROM (((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
  WHERE (sm.method_id = 0)
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name;


--
-- TOC entry 235 (class 1259 OID 78357)
-- Name: ep_m1; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m1 AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
            sur.country,
            sur.area,
            sit.ecoregion,
            sit.realm,
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
            sur.location,
            sur.survey_latitude,
            sur.survey_longitude,
            div.full_name AS diver,
            sm.method_id AS method,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END AS size_class,
            sum(obs.measure_value) AS total,
            sum(nrmn.obs_biomass(oi.a, oi.b, oi.cf, (
                CASE
                    WHEN ((meas.measure_name)::text ~ 'cm$'::text) THEN replace((meas.measure_name)::text, 'cm'::text, ''::text)
                    ELSE '0'::text
                END)::double precision, obs.measure_value)) AS biomass
           FROM ((((((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
             JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
             JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
             JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
             JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
          WHERE (sm.method_id = ANY (ARRAY[1, 8, 9, 10]))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sur.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END, sm.method_id
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.site_code,
    cte_to_force_joins_evaluated_first.site_name,
    cte_to_force_joins_evaluated_first.latitude,
    cte_to_force_joins_evaluated_first.longitude,
    cte_to_force_joins_evaluated_first.survey_date,
    cte_to_force_joins_evaluated_first.depth,
    cte_to_force_joins_evaluated_first.geom,
    cte_to_force_joins_evaluated_first.program,
    cte_to_force_joins_evaluated_first.visibility,
    cte_to_force_joins_evaluated_first.hour,
    cte_to_force_joins_evaluated_first.location,
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.method,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first.family,
    cte_to_force_joins_evaluated_first.recorded_species_name,
    cte_to_force_joins_evaluated_first.species_name,
    cte_to_force_joins_evaluated_first.taxon,
    cte_to_force_joins_evaluated_first.reporting_name,
    cte_to_force_joins_evaluated_first.size_class,
    cte_to_force_joins_evaluated_first.total,
    cte_to_force_joins_evaluated_first.biomass
   FROM cte_to_force_joins_evaluated_first
  WITH NO DATA;


--
-- TOC entry 236 (class 1259 OID 78365)
-- Name: ep_m11_off_transect_measurement; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m11_off_transect_measurement AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    obs.measure_value AS total
   FROM (((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
  WHERE (sm.method_id = 11);


--
-- TOC entry 237 (class 1259 OID 78370)
-- Name: ep_m12_debris; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m12_debris AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    sm.block_num AS block,
    oi.observable_item_name AS debris,
    obs.measure_value AS total
   FROM (((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
  WHERE (sm.method_id = 12);


--
-- TOC entry 238 (class 1259 OID 78375)
-- Name: pq_cat_res_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_cat_res_ref (
    cat_res_id integer NOT NULL,
    resolution_id integer NOT NULL,
    category_id integer NOT NULL
);


--
-- TOC entry 239 (class 1259 OID 78378)
-- Name: pq_category_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_category_ref (
    category_id integer NOT NULL,
    major_category_name character varying(100) NOT NULL,
    description character varying(100) NOT NULL
);


--
-- TOC entry 240 (class 1259 OID 78381)
-- Name: pq_resolution_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_resolution_ref (
    resolution_id integer NOT NULL,
    resolution_name character varying(100) NOT NULL
);


--
-- TOC entry 241 (class 1259 OID 78384)
-- Name: pq_score; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_score (
    score_id integer NOT NULL,
    survey_method_id integer NOT NULL,
    num_points integer,
    fraction_coverage double precision NOT NULL,
    cat_res_id integer NOT NULL
);


--
-- TOC entry 242 (class 1259 OID 78387)
-- Name: ep_m13_pq_scores; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m13_pq_scores AS
 SELECT sur.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    res.resolution_name AS resolution,
    cat.description AS category,
    cat.major_category_name AS major_category,
    pq.num_points,
    sum(pq.num_points) OVER (PARTITION BY sm.survey_id, res.resolution_name) AS total_points,
    pq.fraction_coverage AS percent_cover
   FROM ((((((nrmn.pq_score pq
     JOIN nrmn.pq_cat_res_ref cr ON ((pq.cat_res_id = cr.cat_res_id)))
     JOIN nrmn.pq_resolution_ref res ON ((res.resolution_id = cr.resolution_id)))
     JOIN nrmn.pq_category_ref cat ON ((cat.category_id = cr.category_id)))
     JOIN nrmn.survey_method sm ON ((pq.survey_method_id = sm.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)));


--
-- TOC entry 243 (class 1259 OID 78392)
-- Name: ep_m2_cryptic_fish; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
            sur.country,
            sur.area,
            sit.ecoregion,
            sit.realm,
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
            sur.location,
            sur.survey_latitude,
            sur.survey_longitude,
            div.full_name AS diver,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END AS size_class,
            sum(obs.measure_value) AS total,
            sum(nrmn.obs_biomass(oi.a, oi.b, oi.cf, (
                CASE
                    WHEN ((meas.measure_name)::text ~ 'cm$'::text) THEN replace((meas.measure_name)::text, 'cm'::text, ''::text)
                    ELSE '0'::text
                END)::double precision, obs.measure_value)) AS biomass
           FROM ((((((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
             JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
             JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
             JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
             JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
          WHERE ((sm.method_id = 2) AND ((oi.class = ANY (ARRAY['Actinopterygii'::text, 'Elasmobranchii'::text])) OR ((oi.observable_item_name)::text = 'No species found'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.site_code,
    cte_to_force_joins_evaluated_first.site_name,
    cte_to_force_joins_evaluated_first.latitude,
    cte_to_force_joins_evaluated_first.longitude,
    cte_to_force_joins_evaluated_first.survey_date,
    cte_to_force_joins_evaluated_first.depth,
    cte_to_force_joins_evaluated_first.geom,
    cte_to_force_joins_evaluated_first.program,
    cte_to_force_joins_evaluated_first.visibility,
    cte_to_force_joins_evaluated_first.hour,
    cte_to_force_joins_evaluated_first.location,
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first.family,
    cte_to_force_joins_evaluated_first.recorded_species_name,
    cte_to_force_joins_evaluated_first.species_name,
    cte_to_force_joins_evaluated_first.taxon,
    cte_to_force_joins_evaluated_first.reporting_name,
    cte_to_force_joins_evaluated_first.size_class,
    cte_to_force_joins_evaluated_first.total,
    cte_to_force_joins_evaluated_first.biomass
   FROM cte_to_force_joins_evaluated_first
  WITH NO DATA;


--
-- TOC entry 244 (class 1259 OID 78400)
-- Name: measure_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.measure_type_ref (
    measure_type_id integer NOT NULL,
    measure_type_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 245 (class 1259 OID 78403)
-- Name: ep_m2_inverts; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m2_inverts AS
 WITH fish_classes AS (
         SELECT
                CASE
                    WHEN ((mea.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((mea.measure_name)::text, 'cm'::text, ''::text))::numeric
                END AS size_class
           FROM (nrmn.measure_ref mea
             JOIN nrmn.measure_type_ref mt ON ((mea.measure_type_id = mt.measure_type_id)))
          WHERE ((mt.measure_type_name)::text = 'Fish Size Class'::text)
        ), bounded_fish_classes AS (
         SELECT fish_classes.size_class AS nominal,
                CASE
                    WHEN (fish_classes.size_class = (0)::numeric) THEN '-0.01'::numeric
                    ELSE COALESCE(lag(fish_classes.size_class) OVER (ORDER BY fish_classes.size_class), (0)::numeric)
                END AS lower_bound,
            fish_classes.size_class AS upper_bound
           FROM fish_classes
        ), invert_sized AS (
         SELECT sm.survey_id,
            sur.country,
            sur.area,
            sit.ecoregion,
            sit.realm,
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
            sur.location,
            sur.survey_latitude,
            sur.survey_longitude,
            div.full_name AS diver,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END AS size_class,
            sum(obs.measure_value) AS total
           FROM ((((((nrmn.observation obs
             JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
             JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
             JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
             JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
             JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
             JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
          WHERE ((sm.method_id = 2) AND (((oi.phylum = 'Arthropoda'::text) AND (oi.class = 'Malacostraca'::text)) OR ((oi.phylum = 'Mollusca'::text) AND (oi.class = ANY (ARRAY['Gastropoda'::text, 'Cephalopoda'::text]))) OR ((oi.phylum = 'Echinodermata'::text) AND (COALESCE(oi.class, ''::text) <> 'Ophiuroidea'::text)) OR (oi.phylum = 'Platyhelminthes'::text) OR ((oi.observable_item_name)::text = 'No species found'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END
        )
 SELECT m2.survey_id,
    m2.country,
    m2.area,
    m2.ecoregion,
    m2.realm,
    m2.site_code,
    m2.site_name,
    m2.latitude,
    m2.longitude,
    m2.survey_date,
    m2.depth,
    m2.geom,
    m2.program,
    m2.visibility,
    m2.hour,
    m2.location,
    m2.survey_latitude,
    m2.survey_longitude,
    m2.diver,
    m2.block,
    m2.phylum,
    m2.class,
    m2.family,
    m2.recorded_species_name,
    m2.species_name,
    m2.taxon,
    m2.reporting_name,
    bfc.nominal AS size_class,
    m2.total
   FROM (invert_sized m2
     JOIN bounded_fish_classes bfc ON (((m2.size_class > bfc.lower_bound) AND (m2.size_class <= bfc.upper_bound))))
  WITH NO DATA;


--
-- TOC entry 246 (class 1259 OID 78411)
-- Name: ep_m3_isq; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m3_isq AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    mr.measure_name AS quadrat,
    sum(obs.measure_value) AS total
   FROM ((((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.measure_ref mr ON ((mr.measure_id = obs.measure_id)))
  WHERE (sm.method_id = 3)
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name;


--
-- TOC entry 247 (class 1259 OID 78416)
-- Name: ep_m4_macrocystis_count; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m4_macrocystis_count AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    mr.measure_name AS block,
    sum(obs.measure_value) AS total
   FROM ((((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.measure_ref mr ON ((mr.measure_id = obs.measure_id)))
  WHERE (sm.method_id = 4)
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name;


--
-- TOC entry 248 (class 1259 OID 78421)
-- Name: ep_m5_limpet_quadrats; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m5_limpet_quadrats AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    mr.measure_name AS quadrat,
    sum(obs.measure_value) AS total
   FROM ((((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.measure_ref mr ON ((mr.measure_id = obs.measure_id)))
  WHERE (sm.method_id = 5)
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name;


--
-- TOC entry 249 (class 1259 OID 78426)
-- Name: ep_m7_lobster_count; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m7_lobster_count AS
 SELECT sm.survey_id,
    sur.country,
    sur.area,
    sit.ecoregion,
    sit.realm,
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
    sur.location,
    sur.survey_latitude,
    sur.survey_longitude,
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
        CASE
            WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
            ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
        END AS size_class,
    sum(obs.measure_value) AS total
   FROM ((((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
  WHERE (sm.method_id = 7)
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.location, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, (oi.observable_item_name)::text), oi.taxon, oi.reporting_name,
        CASE
            WHEN ((meas.measure_name)::text = ANY (ARRAY[('Unsized'::character varying)::text, ('No specimen found'::character varying)::text])) THEN (0)::numeric
            ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
        END;


--
-- TOC entry 250 (class 1259 OID 78431)
-- Name: ep_species_list; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_species_list AS
 SELECT oi.observable_item_id AS species_id,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, (oi.observable_item_name)::text) AS species_name,
    oi.taxon,
    oi.reporting_name,
    oi.phylum,
    oi.class,
    oi.family,
    oi.genus,
    oi.common_name,
    oi.range,
    oi.frequency,
    oi.abundance,
    oi.max_length,
    oi.common_family_name,
    oi.common_class_name,
    oi.common_phylum_name,
    NULL::unknown AS geom,
    oi.superseded_ids,
    oi.superseded_names
   FROM nrmn.ep_observable_items oi
  WHERE (((oi.obs_item_type_name)::text = ANY (ARRAY[('Species'::character varying)::text, ('Undescribed Species'::character varying)::text])) AND (EXISTS ( SELECT 1
           FROM nrmn.observation obs
          WHERE (obs.observable_item_id = oi.observable_item_id))) AND (oi.superseded_by IS NULL) AND (NOT (oi.phylum = ANY (ARRAY['Cnidaria'::text, 'Echiura'::text, 'Heterokontophyta'::text]))) AND (NOT (oi.class = ANY (ARRAY['Anthozoa'::text, 'Ascidiacea'::text, 'Echiuroidea'::text, 'Phaeophyceae'::text, 'Aves'::text]))) AND (NOT ((oi.observable_item_name)::text ~ 'spp.$'::text)));


--
-- TOC entry 251 (class 1259 OID 78436)
-- Name: ep_species_survey; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_species_survey AS
 SELECT obs.observable_item_id AS species_id,
    sur.survey_id,
    sur.latitude,
    sur.longitude,
    sit.realm,
    sur.country,
    sur.area,
    sur.geom,
    sur.program
   FROM ((((nrmn.ep_survey_list sur
     JOIN nrmn.survey_method sm ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.observation obs ON ((obs.survey_method_id = sm.survey_method_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.ep_site_list sit ON (((sur.site_code)::text = (sit.site_code)::text)))
  WHERE ((oi.obs_item_type_name)::text = ANY (ARRAY[('Species'::character varying)::text, ('Undescribed Species'::character varying)::text]));


--
-- TOC entry 252 (class 1259 OID 78441)
-- Name: ep_species_survey_observation; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_species_survey_observation AS
 SELECT oi.observable_item_id AS species_id,
    sur.survey_id,
    sur.site_code,
    sur.latitude,
    sur.longitude,
    sur.site_name,
    sit.ecoregion,
    sit.province,
    sit.realm,
    sur.country,
    sur.area,
    sit.location,
    sur.survey_date,
    sur.depth,
    sur.geom,
    sur.program,
    div.full_name AS diver,
    obs.measure_value AS total,
    sm.block_num AS block,
    sm.method_id,
    meas.measure_name AS size_class,
    nrmn.obs_biomass(oi.a, oi.b, oi.cf, (
        CASE
            WHEN ((meas.measure_name)::text ~ 'cm$'::text) THEN replace((meas.measure_name)::text, 'cm'::text, ''::text)
            ELSE '0'::text
        END)::double precision, obs.measure_value) AS biomass
   FROM ((((((nrmn.ep_site_list sit
     JOIN nrmn.ep_survey_list sur ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.survey_method sm ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.observation obs ON ((obs.survey_method_id = sm.survey_method_id)))
     JOIN nrmn.measure_ref meas ON ((obs.measure_id = meas.measure_id)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((obs.observable_item_id = oi.observable_item_id)))
  WHERE ((sm.method_id = ANY (ARRAY[0, 1, 2, 7, 8, 9, 10])) AND (NOT ((oi.observable_item_name)::text ~ 'spp.$'::text)) AND (NOT (oi.phylum = ANY (ARRAY['Cnidaria'::text, 'Echiura'::text, 'Heterokontophyta'::text]))) AND (NOT (oi.class = ANY (ARRAY['Anthozoa'::text, 'Ascidiacea'::text, 'Echiuroidea'::text, 'Phaeophyceae'::text, 'Aves'::text]))));


--
-- TOC entry 253 (class 1259 OID 78446)
-- Name: location_ref_location_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.location_ref_location_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4032 (class 0 OID 0)
-- Dependencies: 253
-- Name: location_ref_location_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.location_ref_location_id OWNED BY nrmn.location_ref.location_id;


--
-- TOC entry 254 (class 1259 OID 78448)
-- Name: measure_ref_measure_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.measure_ref_measure_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4033 (class 0 OID 0)
-- Dependencies: 254
-- Name: measure_ref_measure_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.measure_ref_measure_id OWNED BY nrmn.measure_ref.measure_id;


--
-- TOC entry 255 (class 1259 OID 78450)
-- Name: measure_type_ref_measure_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.measure_type_ref_measure_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4034 (class 0 OID 0)
-- Dependencies: 255
-- Name: measure_type_ref_measure_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.measure_type_ref_measure_id OWNED BY nrmn.measure_type_ref.measure_type_id;


--
-- TOC entry 256 (class 1259 OID 78452)
-- Name: method_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.method_ref (
    method_id integer NOT NULL,
    method_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 257 (class 1259 OID 78455)
-- Name: obs_item_type_ref_obs_item_type_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.obs_item_type_ref_obs_item_type_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4035 (class 0 OID 0)
-- Dependencies: 257
-- Name: obs_item_type_ref_obs_item_type_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.obs_item_type_ref_obs_item_type_id OWNED BY nrmn.obs_item_type_ref.obs_item_type_id;


--
-- TOC entry 258 (class 1259 OID 78457)
-- Name: observable_item_ref_observable_item_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.observable_item_ref_observable_item_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4036 (class 0 OID 0)
-- Dependencies: 258
-- Name: observable_item_ref_observable_item_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.observable_item_ref_observable_item_id OWNED BY nrmn.observable_item_ref.observable_item_id;


--
-- TOC entry 259 (class 1259 OID 78459)
-- Name: observation_observation_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.observation_observation_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4037 (class 0 OID 0)
-- Dependencies: 259
-- Name: observation_observation_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.observation_observation_id OWNED BY nrmn.observation.observation_id;


--
-- TOC entry 260 (class 1259 OID 78461)
-- Name: pq_cat_res_ref_cat_res_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_cat_res_ref_cat_res_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4038 (class 0 OID 0)
-- Dependencies: 260
-- Name: pq_cat_res_ref_cat_res_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_cat_res_ref_cat_res_id OWNED BY nrmn.pq_cat_res_ref.cat_res_id;


--
-- TOC entry 261 (class 1259 OID 78463)
-- Name: pq_category_ref_category_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_category_ref_category_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4039 (class 0 OID 0)
-- Dependencies: 261
-- Name: pq_category_ref_category_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_category_ref_category_id OWNED BY nrmn.pq_category_ref.category_id;


--
-- TOC entry 262 (class 1259 OID 78465)
-- Name: pq_resolution_ref_resolution_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_resolution_ref_resolution_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4040 (class 0 OID 0)
-- Dependencies: 262
-- Name: pq_resolution_ref_resolution_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_resolution_ref_resolution_id OWNED BY nrmn.pq_resolution_ref.resolution_id;


--
-- TOC entry 263 (class 1259 OID 78467)
-- Name: pq_score_score_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_score_score_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4041 (class 0 OID 0)
-- Dependencies: 263
-- Name: pq_score_score_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_score_score_id OWNED BY nrmn.pq_score.score_id;


--
-- TOC entry 264 (class 1259 OID 78469)
-- Name: program_ref_program_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.program_ref_program_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4042 (class 0 OID 0)
-- Dependencies: 264
-- Name: program_ref_program_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.program_ref_program_id OWNED BY nrmn.program_ref.program_id;


--
-- TOC entry 265 (class 1259 OID 78471)
-- Name: pv_debris; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_debris (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "24a Debristype" character varying,
    "24b Count" character varying,
    "24c Isremoved" character varying
);


--
-- TOC entry 266 (class 1259 OID 78477)
-- Name: pv_fishes; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_fishes (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "11a Species" character varying,
    "11b Sex" character varying,
    "11c Sizeclass" character varying,
    "11d Count" character varying
);


--
-- TOC entry 267 (class 1259 OID 78483)
-- Name: pv_inverts1; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_inverts1 (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "23a Species" character varying,
    "23b Sex" character varying,
    "23c Sizeclass" character varying,
    "23d Count" character varying
);


--
-- TOC entry 268 (class 1259 OID 78489)
-- Name: pv_inverts2; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_inverts2 (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "21a Species" character varying,
    "21b Sex" character varying,
    "21c Count" character varying
);


--
-- TOC entry 269 (class 1259 OID 78495)
-- Name: pv_inverts3; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_inverts3 (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "22a Species" character varying,
    "22b Sex" character varying,
    "22c Size" character varying
);


--
-- TOC entry 270 (class 1259 OID 78501)
-- Name: pv_macrocystis; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_macrocystis (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "41a Species" character varying,
    "41b Count" character varying
);


--
-- TOC entry 271 (class 1259 OID 78507)
-- Name: pv_macrophytes; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pv_macrophytes (
    project character varying,
    location character varying,
    survey_no character varying,
    survey_ref character varying,
    treatment character varying,
    site_status character varying,
    site character varying,
    "site-event" character varying,
    date_time character varying,
    time_offset character varying,
    sample character varying,
    block character varying,
    replicate character varying,
    output character varying,
    record character varying,
    sample_id character varying,
    output_id character varying,
    record_id character varying,
    "31a Species" character varying,
    "31b Pointscount" character varying
);


--
-- TOC entry 272 (class 1259 OID 78513)
-- Name: rugosity_rugosity_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.rugosity_rugosity_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4043 (class 0 OID 0)
-- Dependencies: 272
-- Name: rugosity_rugosity_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.rugosity_rugosity_id OWNED BY nrmn.rugosity.rugosity_id;


--
-- TOC entry 273 (class 1259 OID 78515)
-- Name: site_ref_site_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.site_ref_site_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4044 (class 0 OID 0)
-- Dependencies: 273
-- Name: site_ref_site_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.site_ref_site_id OWNED BY nrmn.site_ref.site_id;


--
-- TOC entry 274 (class 1259 OID 78517)
-- Name: surface_type_ref_surface_type_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.surface_type_ref_surface_type_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4045 (class 0 OID 0)
-- Dependencies: 274
-- Name: surface_type_ref_surface_type_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.surface_type_ref_surface_type_id OWNED BY nrmn.surface_type_ref.surface_type_id;


--
-- TOC entry 275 (class 1259 OID 78519)
-- Name: survey_method_survey_method_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.survey_method_survey_method_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4046 (class 0 OID 0)
-- Dependencies: 275
-- Name: survey_method_survey_method_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.survey_method_survey_method_id OWNED BY nrmn.survey_method.survey_method_id;


--
-- TOC entry 276 (class 1259 OID 78521)
-- Name: survey_survey_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.survey_survey_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4047 (class 0 OID 0)
-- Dependencies: 276
-- Name: survey_survey_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.survey_survey_id OWNED BY nrmn.survey.survey_id;


--
-- TOC entry 3750 (class 2604 OID 78523)
-- Name: diver_ref diver_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref ALTER COLUMN diver_id SET DEFAULT nextval('nrmn.diver_ref_diver_id'::regclass);


--
-- TOC entry 3757 (class 2604 OID 78524)
-- Name: location_ref location_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref ALTER COLUMN location_id SET DEFAULT nextval('nrmn.location_ref_location_id'::regclass);


--
-- TOC entry 3761 (class 2604 OID 78525)
-- Name: measure_ref measure_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref ALTER COLUMN measure_id SET DEFAULT nextval('nrmn.measure_ref_measure_id'::regclass);


--
-- TOC entry 3766 (class 2604 OID 78526)
-- Name: measure_type_ref measure_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref ALTER COLUMN measure_type_id SET DEFAULT nextval('nrmn.measure_type_ref_measure_id'::regclass);


--
-- TOC entry 3751 (class 2604 OID 78527)
-- Name: obs_item_type_ref obs_item_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref ALTER COLUMN obs_item_type_id SET DEFAULT nextval('nrmn.obs_item_type_ref_obs_item_type_id'::regclass);


--
-- TOC entry 3752 (class 2604 OID 78528)
-- Name: observable_item_ref observable_item_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref ALTER COLUMN observable_item_id SET DEFAULT nextval('nrmn.observable_item_ref_observable_item_id'::regclass);


--
-- TOC entry 3753 (class 2604 OID 78529)
-- Name: observation observation_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation ALTER COLUMN observation_id SET DEFAULT nextval('nrmn.observation_observation_id'::regclass);


--
-- TOC entry 3762 (class 2604 OID 78530)
-- Name: pq_cat_res_ref cat_res_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref ALTER COLUMN cat_res_id SET DEFAULT nextval('nrmn.pq_cat_res_ref_cat_res_id'::regclass);


--
-- TOC entry 3763 (class 2604 OID 78531)
-- Name: pq_category_ref category_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref ALTER COLUMN category_id SET DEFAULT nextval('nrmn.pq_category_ref_category_id'::regclass);


--
-- TOC entry 3764 (class 2604 OID 78532)
-- Name: pq_resolution_ref resolution_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref ALTER COLUMN resolution_id SET DEFAULT nextval('nrmn.pq_resolution_ref_resolution_id'::regclass);


--
-- TOC entry 3765 (class 2604 OID 78533)
-- Name: pq_score score_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score ALTER COLUMN score_id SET DEFAULT nextval('nrmn.pq_score_score_id'::regclass);


--
-- TOC entry 3758 (class 2604 OID 78534)
-- Name: program_ref program_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref ALTER COLUMN program_id SET DEFAULT nextval('nrmn.program_ref_program_id'::regclass);


--
-- TOC entry 3759 (class 2604 OID 78535)
-- Name: rugosity rugosity_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity ALTER COLUMN rugosity_id SET DEFAULT nextval('nrmn.rugosity_rugosity_id'::regclass);


--
-- TOC entry 3756 (class 2604 OID 78536)
-- Name: site_ref site_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref ALTER COLUMN site_id SET DEFAULT nextval('nrmn.site_ref_site_id'::regclass);


--
-- TOC entry 3760 (class 2604 OID 78537)
-- Name: surface_type_ref surface_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref ALTER COLUMN surface_type_id SET DEFAULT nextval('nrmn.surface_type_ref_surface_type_id'::regclass);


--
-- TOC entry 3754 (class 2604 OID 78538)
-- Name: survey survey_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey ALTER COLUMN survey_id SET DEFAULT nextval('nrmn.survey_survey_id'::regclass);


--
-- TOC entry 3755 (class 2604 OID 78539)
-- Name: survey_method survey_method_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method ALTER COLUMN survey_method_id SET DEFAULT nextval('nrmn.survey_method_survey_method_id'::regclass);


--
-- TOC entry 3768 (class 2606 OID 78617)
-- Name: aphia_ref aphia_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_ref
    ADD CONSTRAINT aphia_ref_pkey PRIMARY KEY (aphia_id);


--
-- TOC entry 3770 (class 2606 OID 78619)
-- Name: aphia_rel_type_ref aphia_rel_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_rel_type_ref
    ADD CONSTRAINT aphia_rel_type_ref_pkey PRIMARY KEY (aphia_rel_type_id);


--
-- TOC entry 3772 (class 2606 OID 78621)
-- Name: diver_ref diver_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref
    ADD CONSTRAINT diver_ref_pkey PRIMARY KEY (diver_id);


--
-- TOC entry 3774 (class 2606 OID 78623)
-- Name: diver_ref diver_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref
    ADD CONSTRAINT diver_unique UNIQUE (initials);


--
-- TOC entry 3807 (class 2606 OID 78625)
-- Name: legacy_common_names legacy_common_names_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.legacy_common_names
    ADD CONSTRAINT legacy_common_names_pkey PRIMARY KEY (name);


--
-- TOC entry 3809 (class 2606 OID 78627)
-- Name: lengthweight_ref lengthweight_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.lengthweight_ref
    ADD CONSTRAINT lengthweight_ref_pkey PRIMARY KEY (observable_item_id);


--
-- TOC entry 3811 (class 2606 OID 78629)
-- Name: location_ref location_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref
    ADD CONSTRAINT location_ref_pkey PRIMARY KEY (location_id);


--
-- TOC entry 3813 (class 2606 OID 78631)
-- Name: location_ref location_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref
    ADD CONSTRAINT location_unique UNIQUE (location_name);


--
-- TOC entry 3829 (class 2606 OID 78633)
-- Name: measure_ref measure_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_ref_pkey PRIMARY KEY (measure_id);


--
-- TOC entry 3852 (class 2606 OID 78635)
-- Name: measure_type_ref measure_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref
    ADD CONSTRAINT measure_type_ref_pkey PRIMARY KEY (measure_type_id);


--
-- TOC entry 3854 (class 2606 OID 78637)
-- Name: measure_type_ref measure_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref
    ADD CONSTRAINT measure_type_unique UNIQUE (measure_type_name);


--
-- TOC entry 3831 (class 2606 OID 78639)
-- Name: measure_ref measure_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_unique UNIQUE (measure_name, measure_type_id);


--
-- TOC entry 3815 (class 2606 OID 78641)
-- Name: meow_ecoregions meow_ecoregions_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.meow_ecoregions
    ADD CONSTRAINT meow_ecoregions_pkey PRIMARY KEY (id);


--
-- TOC entry 3856 (class 2606 OID 78643)
-- Name: method_ref method_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.method_ref
    ADD CONSTRAINT method_ref_pkey PRIMARY KEY (method_id);


--
-- TOC entry 3858 (class 2606 OID 78645)
-- Name: method_ref method_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.method_ref
    ADD CONSTRAINT method_unique UNIQUE (method_name);


--
-- TOC entry 3776 (class 2606 OID 78647)
-- Name: obs_item_type_ref obs_item_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref
    ADD CONSTRAINT obs_item_type_ref_pkey PRIMARY KEY (obs_item_type_id);


--
-- TOC entry 3778 (class 2606 OID 78649)
-- Name: obs_item_type_ref obs_item_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref
    ADD CONSTRAINT obs_item_type_unique UNIQUE (obs_item_type_name);


--
-- TOC entry 3780 (class 2606 OID 78651)
-- Name: observable_item_ref observable_item_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT observable_item_ref_pkey PRIMARY KEY (observable_item_id);


--
-- TOC entry 3782 (class 2606 OID 78653)
-- Name: observable_item_ref observable_item_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT observable_item_unique UNIQUE (observable_item_name);


--
-- TOC entry 3788 (class 2606 OID 78655)
-- Name: observation observation_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_pkey PRIMARY KEY (observation_id);


--
-- TOC entry 3790 (class 2606 OID 78657)
-- Name: observation observation_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_unique UNIQUE (survey_method_id, observable_item_id, measure_id, diver_id, observation_attribute);


--
-- TOC entry 3834 (class 2606 OID 78659)
-- Name: pq_cat_res_ref pq_cat_res_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT pq_cat_res_ref_pkey PRIMARY KEY (cat_res_id);


--
-- TOC entry 3836 (class 2606 OID 78661)
-- Name: pq_cat_res_ref pq_cat_res_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT pq_cat_res_unique UNIQUE (resolution_id, category_id);


--
-- TOC entry 3838 (class 2606 OID 78663)
-- Name: pq_category_ref pq_category_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref
    ADD CONSTRAINT pq_category_ref_pkey PRIMARY KEY (category_id);


--
-- TOC entry 3840 (class 2606 OID 78665)
-- Name: pq_category_ref pq_category_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref
    ADD CONSTRAINT pq_category_unique UNIQUE (description);


--
-- TOC entry 3842 (class 2606 OID 78667)
-- Name: pq_resolution_ref pq_resolution_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref
    ADD CONSTRAINT pq_resolution_ref_pkey PRIMARY KEY (resolution_id);


--
-- TOC entry 3844 (class 2606 OID 78669)
-- Name: pq_resolution_ref pq_resolution_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref
    ADD CONSTRAINT pq_resolution_unique UNIQUE (resolution_name);


--
-- TOC entry 3848 (class 2606 OID 78671)
-- Name: pq_score pq_score_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT pq_score_pkey PRIMARY KEY (score_id);


--
-- TOC entry 3850 (class 2606 OID 78673)
-- Name: pq_score pq_score_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT pq_score_unique UNIQUE (survey_method_id, cat_res_id);


--
-- TOC entry 3817 (class 2606 OID 78675)
-- Name: program_ref program_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref
    ADD CONSTRAINT program_ref_pkey PRIMARY KEY (program_id);


--
-- TOC entry 3819 (class 2606 OID 78677)
-- Name: program_ref program_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref
    ADD CONSTRAINT program_unique UNIQUE (program_name);


--
-- TOC entry 3821 (class 2606 OID 78679)
-- Name: rugosity rugosity_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_pkey PRIMARY KEY (rugosity_id);


--
-- TOC entry 3823 (class 2606 OID 78681)
-- Name: rugosity rugosity_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_unique UNIQUE (survey_method_id);


--
-- TOC entry 3803 (class 2606 OID 78683)
-- Name: site_ref site_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_ref_pkey PRIMARY KEY (site_id);


--
-- TOC entry 3805 (class 2606 OID 78685)
-- Name: site_ref site_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_unique UNIQUE (site_code, site_name);


--
-- TOC entry 3825 (class 2606 OID 78687)
-- Name: surface_type_ref surface_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref
    ADD CONSTRAINT surface_type_ref_pkey PRIMARY KEY (surface_type_id);


--
-- TOC entry 3827 (class 2606 OID 78689)
-- Name: surface_type_ref surface_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref
    ADD CONSTRAINT surface_type_unique UNIQUE (surface_type_name);


--
-- TOC entry 3798 (class 2606 OID 78691)
-- Name: survey_method survey_method_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_pkey PRIMARY KEY (survey_method_id);


--
-- TOC entry 3800 (class 2606 OID 78693)
-- Name: survey_method survey_method_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_unique UNIQUE (survey_id, method_id, block_num);


--
-- TOC entry 3793 (class 2606 OID 78695)
-- Name: survey survey_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey_id);


--
-- TOC entry 3795 (class 2606 OID 78697)
-- Name: survey survey_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_unique UNIQUE (site_id, survey_date, survey_time, depth, survey_num, program_id);


--
-- TOC entry 3783 (class 1259 OID 78698)
-- Name: ix_obs_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_1 ON nrmn.observation USING btree (survey_method_id);


--
-- TOC entry 3784 (class 1259 OID 78699)
-- Name: ix_obs_2; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_2 ON nrmn.observation USING btree (diver_id);


--
-- TOC entry 3785 (class 1259 OID 78700)
-- Name: ix_obs_3; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_3 ON nrmn.observation USING btree (observable_item_id);


--
-- TOC entry 3786 (class 1259 OID 78701)
-- Name: ix_obs_4; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_4 ON nrmn.observation USING btree (measure_id);


--
-- TOC entry 3832 (class 1259 OID 78702)
-- Name: ix_pqcr_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqcr_1 ON nrmn.pq_cat_res_ref USING btree (resolution_id, category_id);


--
-- TOC entry 3845 (class 1259 OID 78703)
-- Name: ix_pqs_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqs_1 ON nrmn.pq_score USING btree (cat_res_id);


--
-- TOC entry 3846 (class 1259 OID 78704)
-- Name: ix_pqs_2; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqs_2 ON nrmn.pq_score USING btree (survey_method_id);


--
-- TOC entry 3801 (class 1259 OID 78705)
-- Name: ix_sit_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sit_1 ON nrmn.site_ref USING btree (location_id);


--
-- TOC entry 3796 (class 1259 OID 78706)
-- Name: ix_sm_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sm_1 ON nrmn.survey_method USING btree (survey_id);


--
-- TOC entry 3791 (class 1259 OID 78707)
-- Name: ix_sur_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sur_1 ON nrmn.survey USING btree (site_id, survey_date, depth);


--
-- TOC entry 3876 (class 2606 OID 78708)
-- Name: pq_cat_res_ref cat_res_category_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT cat_res_category_fk FOREIGN KEY (category_id) REFERENCES nrmn.pq_category_ref(category_id);


--
-- TOC entry 3877 (class 2606 OID 78713)
-- Name: pq_cat_res_ref cat_res_resolution_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT cat_res_resolution_fk FOREIGN KEY (resolution_id) REFERENCES nrmn.pq_resolution_ref(resolution_id);


--
-- TOC entry 3872 (class 2606 OID 78718)
-- Name: lengthweight_ref lengthweight_observable_item_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.lengthweight_ref
    ADD CONSTRAINT lengthweight_observable_item_fk FOREIGN KEY (observable_item_id) REFERENCES nrmn.observable_item_ref(observable_item_id);


--
-- TOC entry 3875 (class 2606 OID 78723)
-- Name: measure_ref measure_measure_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_measure_type_fk FOREIGN KEY (measure_type_id) REFERENCES nrmn.measure_type_ref(measure_type_id);


--
-- TOC entry 3860 (class 2606 OID 78728)
-- Name: observable_item_ref obs_item_aphia_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_aphia_fk FOREIGN KEY (aphia_id) REFERENCES nrmn.aphia_ref(aphia_id);


--
-- TOC entry 3861 (class 2606 OID 78733)
-- Name: observable_item_ref obs_item_aphia_rel_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_aphia_rel_type_fk FOREIGN KEY (aphia_rel_type_id) REFERENCES nrmn.aphia_rel_type_ref(aphia_rel_type_id);


--
-- TOC entry 3862 (class 2606 OID 78738)
-- Name: observable_item_ref obs_item_obs_item_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_obs_item_type_fk FOREIGN KEY (obs_item_type_id) REFERENCES nrmn.obs_item_type_ref(obs_item_type_id);


--
-- TOC entry 3863 (class 2606 OID 78743)
-- Name: observation observation_diver_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_diver_fk FOREIGN KEY (diver_id) REFERENCES nrmn.diver_ref(diver_id);


--
-- TOC entry 3864 (class 2606 OID 78749)
-- Name: observation observation_measure_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_measure_fk FOREIGN KEY (measure_id) REFERENCES nrmn.measure_ref(measure_id);


--
-- TOC entry 3865 (class 2606 OID 78764)
-- Name: observation observation_observable_item_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_observable_item_fk FOREIGN KEY (observable_item_id) REFERENCES nrmn.observable_item_ref(observable_item_id);


--
-- TOC entry 3866 (class 2606 OID 78776)
-- Name: observation observation_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id) ON DELETE CASCADE;


--
-- TOC entry 3873 (class 2606 OID 78781)
-- Name: rugosity rugosity_surface_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_surface_type_fk FOREIGN KEY (surface_type_id) REFERENCES nrmn.surface_type_ref(surface_type_id);


--
-- TOC entry 3874 (class 2606 OID 78786)
-- Name: rugosity rugosity_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id);


--
-- TOC entry 3878 (class 2606 OID 78791)
-- Name: pq_score score_cat_res_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT score_cat_res_fk FOREIGN KEY (cat_res_id) REFERENCES nrmn.pq_cat_res_ref(cat_res_id);


--
-- TOC entry 3879 (class 2606 OID 78796)
-- Name: pq_score score_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT score_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id);


--
-- TOC entry 3871 (class 2606 OID 78801)
-- Name: site_ref site_location_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_location_fk FOREIGN KEY (location_id) REFERENCES nrmn.location_ref(location_id);


--
-- TOC entry 3859 (class 2606 OID 78806)
-- Name: atrc_rugosity survey_atrc_rugosity_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.atrc_rugosity
    ADD CONSTRAINT survey_atrc_rugosity_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey(survey_id);


--
-- TOC entry 3869 (class 2606 OID 78811)
-- Name: survey_method survey_method_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_method_fk FOREIGN KEY (method_id) REFERENCES nrmn.method_ref(method_id);


--
-- TOC entry 3870 (class 2606 OID 78816)
-- Name: survey_method survey_method_survey_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_survey_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey(survey_id) ON DELETE CASCADE;


--
-- TOC entry 3867 (class 2606 OID 78821)
-- Name: survey survey_program_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_program_fk FOREIGN KEY (program_id) REFERENCES nrmn.program_ref(program_id);


--
-- TOC entry 3868 (class 2606 OID 78826)
-- Name: survey survey_site_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_site_fk FOREIGN KEY (site_id) REFERENCES nrmn.site_ref(site_id);


-- Completed on 2020-10-14 16:03:52 AEDT

--
-- PostgreSQL database dump complete
--

