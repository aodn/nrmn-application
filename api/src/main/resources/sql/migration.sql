--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.15
-- Dumped by pg_dump version 13.2 (Ubuntu 13.2-1.pgdg20.04+1)

-- Started on 2021-03-03 07:32:19 AEDT

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
-- TOC entry 770 (class 1255 OID 110081)
-- Name: abbreviated_species_code(character varying, integer); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.abbreviated_species_code(species_name character varying, code_length integer) RETURNS character varying
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
BEGIN
    return CASE
               WHEN species_name LIKE ''% spp.''
                   THEN substring(species_name from 1 for (position('' '' in species_name) - 1))
               ELSE substring(species_name from 1 for 1)
                   || substring(species_name from (1 + position('' '' in species_name)) for (code_length - 1))
        END;
END; ';


--
-- TOC entry 785 (class 1255 OID 110085)
-- Name: assign_species_to_method(); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.assign_species_to_method() RETURNS void
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
BEGIN
-- M1
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M1}'', to_jsonb(true))
WHERE "class" IN (''Actinopterygii'',''Reptilia'',''Elasmobranchii'',''Mammalia'',''Cephalopoda'',''Aves'');
--M2 inverts
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M2_invert}'', to_jsonb(true))
WHERE "class" IN (''Polyplacophora'',''Turbellaria'',''Cephalopoda'',''Rhabditophora'',''Polychaeta'',''Merostomata'',
''Tentaculata'',''Anopla'');
--M2 cryptic
--Update has to be applied after updating M1, as it targets Fishes(Actinopterygii,Elasmobranchii) at family level
UPDATE nrmn.observable_item_ref SET obs_item_attribute = obs_item_attribute ||
jsonb_set(obs_item_attribute, ''{is_M2_cryptic}'', to_jsonb(true))
WHERE family IN (''Agonidae'',''Ambassidae'',''Anarhichadidae'',''Antennariidae'',''Aploactinidae'',''Apogonidae'',''Ariidae'',
''Aulopidae'',''Bathymasteridae'',''Batrachoididae'',''Blenniidae'',''Bothidae'',''Bovichtidae'',''Brachaeluridae'',
''Brachionichthyidae'',''Bythitidae'',''Callionymidae'',''Caracanthidae'',''Carapidae'',''Centriscidae'',''Chaenopsidae'',
''Chironemidae'',''Cirrhitidae'',''Clinidae'',''Congridae'',''Congrogadidae'',''Cottidae'',''Creediidae'',''Cryptacanthodidae'',
''Cyclopteridae'',''Cynoglossidae'',''Dasyatidae'',''Diodontidae'',''Eleotridae'',''Gnathanacanthidae'',''Gobiesocidae'',''Gobiidae'',
''Grammistidae'',''Hemiscylliidae'',''Heterodontidae'',''Holocentridae'',''Hypnidae'',''Labrisomidae'',''Leptoscopidae'',''Liparidae'',
''Lotidae'',''Monocentridae'',''Moridae'',''Muraenidae'',''Nototheniidae'',''Ophichthidae'',''Ophidiidae'',''Opistognathidae'',
''Orectolobidae'',''Paralichthyidae'',''Parascylliidae'',''Pataecidae'',''Pegasidae'',''Pempheridae'',''Pholidae'',''Pinguipedidae'',
''Platycephalidae'',''Plesiopidae'',''Pleuronectidae'',''Plotosidae'',''Priacanthidae'',''Pseudochromidae'',
''Psychrolutidae'',''Rajidae'',''Rhinobatidae'',''Scorpaenidae'',''*Serranidae - excluding “Anthias”, "Caesioperca", and "Lepidoperca"
'',''Scyliorhinidae'',''Soleidae'',''Solenostomidae'',''Stichaeidae'',''Synanceiidae'',''Syngnathidae'',''Synodontidae'',
''Tetrabrachiidae'',''Tetrarogidae'',''Torpedinidae'',''Trachichthyidae'',''Tripterygiidae'',''Uranoscopidae'',''Urolophidae'',
''Zaproridae'',''Zoarcidae'') and not genus IN (''Trachinops'',''Anthias'',''Caesioperca'',''Lepidoperca'');
-- M3
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M3}'', to_jsonb(true))
WHERE "class" in (''Anthozoa'',''Maxillopoda'',''Polychaeta'',''Ascidiacea'',''Hydrozoa'',''Anthozoa'',''Staurozoa'',''
Bivalvia'') OR phylum IN(''Porifera'',''Bryozoa'',''Algae'',''Brachipoda'',''Substrate'',''Magnoliophyta'',''Chlorophyta'',''Dinophyta'',
''Heterokontophyta'',''Ochrophyta'',''Rhodophyta'',''Tracheophyta'',''Animalia'',''Cyanophyceae'') or family=''Vermetidae'' and not
coalesce(superseded_by,observable_item_name) =''Plyctenactis tuberculosa'';
--M4
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M4}'', to_jsonb(true))
WHERE genus =''Macrocystis'';
--M5
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M5}'', to_jsonb(true))
WHERE family IN(''Siphonariidae'',''Turbinidae'',''Lottiidae'',''Nacellidae'',''Patellidae'',''Acmaeidae'');

--M7
UPDATE nrmn.observable_item_ref
SET obs_item_attribute = jsonb_set(obs_item_attribute, ''{is_M7}'', to_jsonb(true))
WHERE family =''Palinuridae'';
END; ';


--
-- TOC entry 771 (class 1255 OID 110082)
-- Name: length_percentile(integer, integer); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.length_percentile(species_id integer, percentile integer) RETURNS double precision
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
declare length_percentile float;
BEGIN
with stg1 as (
  select
    measure_id,
    sum(measure_value) sum_value
  from nrmn.observation obs
  join nrmn.observable_item_ref ON observable_item_ref.observable_item_id = obs.observable_item_id
  where coalesce(superseded_by,observable_item_name) in (
	    select observable_item_name
	    from nrmn.observable_item_ref
	    where obs.observable_item_id = species_id)
  group by 1
),
stg2 as(
	select
  measure_id,
  sum(sum_value) over (order by measure_id asc rows between unbounded preceding and current row) as cumulvalue
from stg1),
stg3 as (
	select cast(percentile *max(cumulvalue)/100 as integer) perc  from stg2),

stg4 as  (select
stg2.cumulvalue,
stg2.measure_id
from stg2
order by abs(cumulvalue -(select perc from stg3)) limit 1)

select
case
	when meas.measure_name in (''Unsized'', ''No specimen found'') then 0
	when meas.measure_name similar to ''(B|Q)%'' then NULL
	when meas.measure_name=''Item'' then NULL
	else (replace(meas.measure_name, ''cm'', ''''))::numeric
	end size_class into length_percentile
from stg4
join nrmn.measure_ref meas on meas.measure_id=stg4.measure_id;

return length_percentile;
END; ';


--
-- TOC entry 784 (class 1255 OID 110084)
-- Name: maximum_abundance(integer); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.maximum_abundance(species_id integer) RETURNS numeric
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
declare maximum_abundance numeric;
BEGIN
select
    max(measure_value) into maximum_abundance
    from nrmn.observation obs
    join nrmn.observable_item_ref ON observable_item_ref.observable_item_id = obs.observable_item_id
	where coalesce(superseded_by,observable_item_name) in  (
	    select observable_item_name
	    from nrmn.observable_item_ref
	    where obs.observable_item_id = species_id);

return maximum_abundance;

END; ';


--
-- TOC entry 772 (class 1255 OID 110083)
-- Name: maximum_length(integer); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.maximum_length(species_id integer) RETURNS numeric
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
declare maximum_length numeric;
BEGIN
with stg1 as (select
    max(measure_id) lmax
    from nrmn.observation obs
    join nrmn.observable_item_ref ON observable_item_ref.observable_item_id = obs.observable_item_id
	where coalesce(superseded_by,observable_item_name) in  (
	    select observable_item_name
	    from nrmn.observable_item_ref
	    where obs.observable_item_id = species_id))
select
case
   when meas.measure_name in (''Unsized'', ''No specimen found'') then 0
   when meas.measure_name similar to ''(B|Q)%'' then NULL
   when meas.measure_name=''Item'' then NULL
   else (replace(meas.measure_name, ''cm'', ''''))::numeric
end maxlength into maximum_length
from stg1
join nrmn.measure_ref meas on meas.measure_id = stg1.lmax;

return maximum_length;

END; ';


--
-- TOC entry 733 (class 1255 OID 110079)
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
-- TOC entry 798 (class 1255 OID 110086)
-- Name: set_is_invert_species(); Type: FUNCTION; Schema: nrmn; Owner: -
--

CREATE FUNCTION nrmn.set_is_invert_species() RETURNS void
    LANGUAGE plpgsql
    SET search_path TO 'nrmn', 'public'
    AS '
BEGIN
UPDATE nrmn.observable_item_ref SET obs_item_attribute =
jsonb_set(obs_item_attribute, ''{is_invert_sized}'', to_jsonb(true))
WHERE "class" IN (''Anopla'',''Aplacophora'',''Ascidiacea'',''Asteroidea'',''Bivalvia'',''Crinoidea'',''Cubozoa'',''Dinophyceae'',
''Echinoidea'',''Gastropoda'',''Malacostraca'',''Maxillopoda'',''Nuda'',''Ophiuroidea'',''Polychaeta'',''Polyplacophora'',
''Pycnogonida'',''Rhabditophora'',''Scyphozoa'',''Staurozoa'',''Tentaculata'',''Turbellaria'');
END; ';


--
-- TOC entry 752 (class 1255 OID 110080)
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
			when oi.phylum=''Substrate''
				 then oi.observable_item_name
			when observable_item_name ~ ''[\[\]]'' and allow_square_brackets
				 then oi.observable_item_name
			when oi.species_epithet is not null and oi.genus is not null
				 then concat(oi.genus, '' '', oi.species_epithet)
			when oi.species_epithet is null and oi.genus is not null
				 then concat(oi.genus, '' spp.'')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is not null
				 then concat(replace(oi.family, ''idae'', ''id''), '' spp.'')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is not null
				 then concat(oi."order", '' spp.'')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is null
				 and oi.class is not null
				 then concat(oi.class, '' spp.'')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is null
				 and oi.class is null and oi.phylum is not null
				 then concat(oi.phylum, '' spp.'')
		end	taxonomic_name
		into taxonomic_name
from observable_item_ref oi
where oi.observable_item_name = obs_item_name;

return taxonomic_name;

end; ';


SET default_tablespace = '';

--
-- TOC entry 226 (class 1259 OID 109843)
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
-- TOC entry 220 (class 1259 OID 109804)
-- Name: aphia_rel_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.aphia_rel_type_ref (
    aphia_rel_type_id integer NOT NULL,
    aphia_rel_type_name character varying(50)
);


--
-- TOC entry 228 (class 1259 OID 109856)
-- Name: atrc_rugosity; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.atrc_rugosity (
    survey_id integer,
    rugosity double precision
);


--
-- TOC entry 207 (class 1259 OID 109733)
-- Name: diver_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.diver_ref (
    diver_id integer NOT NULL,
    initials character varying(10) NOT NULL,
    full_name character varying(100)
);


--
-- TOC entry 235 (class 1259 OID 109897)
-- Name: diver_ref_diver_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.diver_ref_diver_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4130 (class 0 OID 0)
-- Dependencies: 235
-- Name: diver_ref_diver_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.diver_ref_diver_id OWNED BY nrmn.diver_ref.diver_id;


--
-- TOC entry 219 (class 1259 OID 109799)
-- Name: obs_item_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.obs_item_type_ref (
    obs_item_type_id integer NOT NULL,
    obs_item_type_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 221 (class 1259 OID 109809)
-- Name: observable_item_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observable_item_ref (
    observable_item_id integer NOT NULL,
    observable_item_name character varying(100) NOT NULL,
    obs_item_type_id integer NOT NULL,
    aphia_id integer,
    aphia_rel_type_id integer,
    common_name character varying(100),
    superseded_by character varying(100),
    phylum character varying(50),
    class character varying(50),
    "order" character varying(50),
    family character varying(50),
    genus character varying(50),
    species_epithet character varying(100),
    report_group character varying(50),
    habitat_groups character varying(50),
    letter_code character varying(20),
    obs_item_attribute jsonb
);


--
-- TOC entry 210 (class 1259 OID 109748)
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
-- TOC entry 217 (class 1259 OID 109786)
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
    longitude double precision,
    latitude double precision,
    protection_status character varying(100),
    inside_marine_park character varying(50),
    notes character varying(1000),
    pq_catalogued boolean,
    block_abundance_simulated boolean,
    project_title character varying(100)
);


--
-- TOC entry 223 (class 1259 OID 109825)
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
-- TOC entry 251 (class 1259 OID 110103)
-- Name: ep_rarity_abundance; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_abundance AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = 'Species'::text) AND (NOT ((nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)))::text ~ 'spp.$'::text)))
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
-- TOC entry 222 (class 1259 OID 109817)
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
    state character varying(100),
    country character varying(100),
    old_site_code character varying(50)[],
    mpa character varying(200),
    protection_status character varying(100),
    relief integer,
    currents integer,
    wave_exposure integer,
    slope integer,
    site_attribute jsonb,
    is_active boolean
);


--
-- TOC entry 252 (class 1259 OID 110111)
-- Name: ep_rarity_extents; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_extents AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = ANY ((ARRAY['Species'::character varying, 'Undescribed Species'::character varying])::text[])) AND (NOT ((nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)))::text ~ 'spp.$'::text)))
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
            public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT  ((', taxon_min_max_coords.minlongitude, ' ', taxon_min_max_coords.minlatitude, '), (', taxon_min_max_coords.maxlongitude, ' ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)) AS one,
                CASE
                    WHEN ((taxon_min_max_coords.maxwestlongitude IS NOT NULL) AND (taxon_min_max_coords.mineastlongitude IS NOT NULL)) THEN public.st_union(public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT  ((', taxon_min_max_coords.mineastlongitude, ' ', taxon_min_max_coords.minlatitude, '), ( 180.0 ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)), public.st_envelope(public.st_buffer(public.st_geomfromtext(concat('MULTIPOINT  ((-180.0 ', taxon_min_max_coords.minlatitude, '), ( ', taxon_min_max_coords.maxwestlongitude, ' ', taxon_min_max_coords.maxlatitude, '))'), 4326), (0.005)::double precision)))
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
-- TOC entry 254 (class 1259 OID 110127)
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
-- TOC entry 253 (class 1259 OID 110119)
-- Name: ep_rarity_range; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_rarity_range AS
 WITH taxa AS (
         SELECT oi.observable_item_id,
            nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)) AS taxon
           FROM (nrmn.observable_item_ref oi
             JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
          WHERE (((oit.obs_item_type_name)::text = ANY ((ARRAY['Species'::character varying, 'Undescribed Species'::character varying])::text[])) AND (NOT ((nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)))::text ~ 'spp.$'::text)))
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
-- TOC entry 229 (class 1259 OID 109859)
-- Name: legacy_common_names; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.legacy_common_names (
    rank character varying(6),
    name character varying(18) NOT NULL,
    common_name character varying(32)
);


--
-- TOC entry 227 (class 1259 OID 109851)
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
-- TOC entry 255 (class 1259 OID 110135)
-- Name: ep_observable_items; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_observable_items AS
 WITH supersedings AS (
         SELECT oi_1.superseded_by AS currentname,
            string_agg((oi_1.observable_item_name)::text, ', '::text ORDER BY (oi_1.observable_item_name)::text) AS names,
            string_agg(((oi_1.observable_item_id)::character varying(5))::text, ', '::text ORDER BY oi_1.observable_item_name) AS ids
           FROM nrmn.observable_item_ref oi_1
          WHERE (oi_1.superseded_by IS NOT NULL)
          GROUP BY oi_1.superseded_by
        )
 SELECT oi.observable_item_id,
    oi.observable_item_name,
    oit.obs_item_type_name,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.genus,
    replace((oi.common_name)::text, ''''::text, ''::text) AS common_name,
    rran.range,
    rfreq.frequency,
    rabu.abundance,
    ((oi.obs_item_attribute ->> 'MaxLength'::text))::double precision AS max_length,
    cfam.common_name AS common_family_name,
    ccla.common_name AS common_class_name,
    cphy.common_name AS common_phylum_name,
    oi.superseded_by,
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
    nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name)) AS taxon,
    nrmn.taxonomic_name(COALESCE(oi.superseded_by, oi.observable_item_name), false) AS reporting_name,
    oi.report_group,
    oi.habitat_groups,
    (oi.obs_item_attribute ->> 'OtherGroups'::text) AS other_groups
   FROM (((((((((((nrmn.observable_item_ref oi
     JOIN nrmn.obs_item_type_ref oit ON ((oit.obs_item_type_id = oi.obs_item_type_id)))
     LEFT JOIN supersedings ON (((oi.observable_item_name)::text = (supersedings.currentname)::text)))
     LEFT JOIN nrmn.legacy_common_names cfam ON ((((oi.family)::text = (cfam.name)::text) AND ((cfam.rank)::text = 'Family'::text))))
     LEFT JOIN nrmn.legacy_common_names ccla ON ((((oi.class)::text = (ccla.name)::text) AND ((ccla.rank)::text = 'Class'::text))))
     LEFT JOIN nrmn.legacy_common_names cphy ON ((((oi.phylum)::text = (cphy.name)::text) AND ((cphy.rank)::text = 'Phylum'::text))))
     LEFT JOIN nrmn.lengthweight_ref lw ON ((lw.observable_item_id = oi.observable_item_id)))
     LEFT JOIN nrmn.aphia_ref aph ON ((aph.aphia_id = oi.aphia_id)))
     LEFT JOIN nrmn.aphia_rel_type_ref art ON ((art.aphia_rel_type_id = oi.aphia_rel_type_id)))
     LEFT JOIN nrmn.ep_rarity_abundance rabu ON (((rabu.taxon)::text = (oi.observable_item_name)::text)))
     LEFT JOIN nrmn.ep_rarity_range rran ON (((rran.taxon)::text = (oi.observable_item_name)::text)))
     LEFT JOIN nrmn.ep_rarity_frequency rfreq ON (((rfreq.taxon)::text = (oi.observable_item_name)::text)))
  WITH NO DATA;


--
-- TOC entry 208 (class 1259 OID 109738)
-- Name: location_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.location_ref (
    location_id integer NOT NULL,
    location_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 230 (class 1259 OID 109864)
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
-- TOC entry 212 (class 1259 OID 109761)
-- Name: program_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.program_ref (
    program_id integer NOT NULL,
    program_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 249 (class 1259 OID 110087)
-- Name: ep_site_list; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_site_list AS
 SELECT sit.country,
    sit.state AS area,
    loc.location_name AS location,
    sit.mpa,
    sit.site_code,
    sit.site_name,
    array_to_string(sit.old_site_code, ','::text) AS old_site_codes,
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
    string_agg(DISTINCT (pro.program_name)::text, ', '::text ORDER BY (pro.program_name)::text) AS programs,
    sit.protection_status
   FROM ((((nrmn.site_ref sit
     JOIN nrmn.location_ref loc ON ((loc.location_id = sit.location_id)))
     JOIN nrmn.meow_ecoregions meo ON (public.st_contains(meo.geom, sit.geom)))
     LEFT JOIN nrmn.survey sur ON ((sur.site_id = sit.site_id)))
     LEFT JOIN nrmn.program_ref pro ON ((sur.program_id = pro.program_id)))
  GROUP BY sit.country, sit.state, loc.location_name, sit.mpa, sit.site_code, sit.site_name, (array_to_string(sit.old_site_code, ','::text)), sit.latitude, sit.longitude, sit.wave_exposure, sit.relief, sit.slope, sit.currents, meo.realm, meo.province, meo.ecoregion, meo.lat_zone, sit.geom, sit.protection_status
  WITH NO DATA;


--
-- TOC entry 218 (class 1259 OID 109794)
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
-- TOC entry 209 (class 1259 OID 109743)
-- Name: surface_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.surface_type_ref (
    surface_type_id integer NOT NULL,
    surface_type_name character varying(50) NOT NULL
);


--
-- TOC entry 250 (class 1259 OID 110095)
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
    (sur.pq_catalogued IS NOT NULL) AS "has pqs catalogued in db",
    div.full_names AS divers,
    sur.visibility,
    sur.survey_time AS hour,
    sur.direction,
    sur.latitude AS survey_latitude,
    sur.longitude AS survey_longitude,
    rug.avg_rugosity,
    rug.max_rugosity,
    st.surface_type_name AS surface,
    sit.geom,
    pro.program_name AS program,
        CASE
            WHEN (sur.pq_catalogued IS NOT NULL) THEN concat('http://rls.tpac.org.au/pq/', (sur.survey_id)::character varying(10), '/zip')
            ELSE NULL::text
        END AS pq_zip_url,
    sur.protection_status,
    sit.old_site_codes,
    ( SELECT string_agg(DISTINCT ((sm1.method_id)::character varying(3))::text, ', '::text ORDER BY ((sm1.method_id)::character varying(3))::text) AS string_agg
           FROM nrmn.survey_method sm1
          WHERE (sm1.survey_id = sur.survey_id)
          GROUP BY sm1.survey_id) AS methods,
    sur.notes AS survey_notes
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
-- TOC entry 224 (class 1259 OID 109833)
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
-- TOC entry 270 (class 1259 OID 110222)
-- Name: ep_lobster_haliotis; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_lobster_haliotis AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
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
            div.full_name AS diver,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi."order",
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
          WHERE ((sm.method_id = 2) AND (((oi.family)::text = 'Palinuridae'::text) OR ((oi.family)::text = 'Haliotidae'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.location,
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
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first."order",
    cte_to_force_joins_evaluated_first.family,
    cte_to_force_joins_evaluated_first.recorded_species_name,
    cte_to_force_joins_evaluated_first.species_name,
    cte_to_force_joins_evaluated_first.taxon,
    cte_to_force_joins_evaluated_first.reporting_name,
    cte_to_force_joins_evaluated_first.size_class,
    cte_to_force_joins_evaluated_first.total
   FROM cte_to_force_joins_evaluated_first;


--
-- TOC entry 260 (class 1259 OID 110172)
-- Name: ep_m0_off_transect_sighting; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m0_off_transect_sighting AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
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
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.site_code, sur.location, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name;


--
-- TOC entry 231 (class 1259 OID 109872)
-- Name: public_data_exclusion; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.public_data_exclusion (
    program_id integer NOT NULL,
    site_id integer NOT NULL
);


--
-- TOC entry 282 (class 1259 OID 110277)
-- Name: ep_m0_off_transect_sighting_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m0_off_transect_sighting_public AS
 SELECT epm0.survey_id,
    epm0.country,
    epm0.area,
    epm0.ecoregion,
    epm0.realm,
    epm0.location,
    epm0.site_code,
    epm0.site_name,
    round((epm0.latitude)::numeric, 2) AS latitude,
    round((epm0.longitude)::numeric, 2) AS longitude,
    epm0.survey_date,
    epm0.depth,
    public.st_setsrid(public.st_makepoint((round((epm0.latitude)::numeric, 2))::double precision, (round((epm0.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm0.program,
    epm0.visibility,
    epm0.hour,
    round((epm0.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm0.survey_longitude)::numeric, 2) AS survey_longitude,
    epm0.block,
    epm0.phylum,
    epm0.class,
    epm0."order",
    epm0.family,
    epm0.species_name,
    epm0.reporting_name,
    epm0.total
   FROM nrmn.ep_m0_off_transect_sighting epm0
  WHERE (NOT ((epm0.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 256 (class 1259 OID 110143)
-- Name: ep_m1; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m1 AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
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
            div.full_name AS diver,
            sm.method_id AS method,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi."order",
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sur.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END, sm.method_id
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.location,
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
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.method,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first."order",
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
-- TOC entry 266 (class 1259 OID 110202)
-- Name: ep_m11_off_transect_measurement; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m11_off_transect_measurement AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
    oi.taxon,
    oi.reporting_name,
        CASE
            WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
            ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
        END AS size_class,
    obs.measure_value AS total
   FROM ((((((nrmn.observation obs
     JOIN nrmn.survey_method sm ON ((sm.survey_method_id = obs.survey_method_id)))
     JOIN nrmn.ep_survey_list sur ON ((sur.survey_id = sm.survey_id)))
     JOIN nrmn.ep_site_list sit ON (((sit.site_code)::text = (sur.site_code)::text)))
     JOIN nrmn.diver_ref div ON ((div.diver_id = obs.diver_id)))
     JOIN nrmn.ep_observable_items oi ON ((oi.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.measure_ref meas ON ((meas.measure_id = obs.measure_id)))
  WHERE (sm.method_id = 11);


--
-- TOC entry 286 (class 1259 OID 110297)
-- Name: ep_m11_off_transect_measurement_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m11_off_transect_measurement_public AS
 SELECT epm11.survey_id,
    epm11.country,
    epm11.area,
    epm11.ecoregion,
    epm11.realm,
    epm11.location AS site_code,
    epm11.site_name,
    round((epm11.latitude)::numeric, 2) AS latitude,
    round((epm11.longitude)::numeric, 2) AS longitude,
    epm11.survey_date,
    epm11.depth,
    public.st_setsrid(public.st_makepoint((round((epm11.latitude)::numeric, 2))::double precision, (round((epm11.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm11.program,
    epm11.visibility,
    epm11.hour,
    round((epm11.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm11.survey_longitude)::numeric, 2) AS survey_longitude,
    epm11.phylum,
    epm11.class,
    epm11."order",
    epm11.family,
    epm11.species_name,
    epm11.reporting_name,
    epm11.size_class,
    epm11.total
   FROM nrmn.ep_m11_off_transect_measurement epm11
  WHERE (NOT ((epm11.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 259 (class 1259 OID 110167)
-- Name: ep_m12_debris; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m12_debris AS
 SELECT sm.survey_id,
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
-- TOC entry 214 (class 1259 OID 109771)
-- Name: pq_cat_res_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_cat_res_ref (
    cat_res_id integer NOT NULL,
    resolution_id integer NOT NULL,
    category_id integer NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 109838)
-- Name: pq_category_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_category_ref (
    category_id integer NOT NULL,
    major_category_name character varying(100) NOT NULL,
    description character varying(100) NOT NULL
);


--
-- TOC entry 213 (class 1259 OID 109766)
-- Name: pq_resolution_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.pq_resolution_ref (
    resolution_id integer NOT NULL,
    resolution_name character varying(100) NOT NULL
);


--
-- TOC entry 216 (class 1259 OID 109781)
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
-- TOC entry 265 (class 1259 OID 110197)
-- Name: ep_m13_pq_scores; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m13_pq_scores AS
 SELECT sur.survey_id,
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
-- TOC entry 287 (class 1259 OID 110302)
-- Name: ep_m13_pq_scores_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m13_pq_scores_public AS
 SELECT epm13.survey_id,
    epm13.country,
    epm13.area,
    epm13.ecoregion,
    epm13.realm,
    epm13.location AS site_code,
    epm13.site_name,
    round((epm13.latitude)::numeric, 2) AS latitude,
    round((epm13.longitude)::numeric, 2) AS longitude,
    epm13.survey_date,
    epm13.depth,
    public.st_setsrid(public.st_makepoint((round((epm13.latitude)::numeric, 2))::double precision, (round((epm13.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm13.program,
    epm13.visibility,
    epm13.hour,
    round((epm13.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm13.survey_longitude)::numeric, 2) AS survey_longitude,
    epm13.resolution,
    epm13.category,
    epm13.major_category,
    epm13.num_points,
    epm13.total_points,
    epm13.percent_cover
   FROM nrmn.ep_m13_pq_scores epm13
  WHERE (((epm13.category)::text <> 'Tape'::text) AND (NOT ((epm13.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id)))))));


--
-- TOC entry 279 (class 1259 OID 110262)
-- Name: ep_m1_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m1_public AS
 SELECT epm1.survey_id,
    epm1.country,
    epm1.area,
    epm1.ecoregion,
    epm1.realm,
    epm1.location,
    epm1.site_code,
    epm1.site_name,
    round((epm1.latitude)::numeric, 2) AS latitude,
    round((epm1.longitude)::numeric, 2) AS longitude,
    epm1.survey_date,
    epm1.depth,
    public.st_setsrid(public.st_makepoint((round((epm1.latitude)::numeric, 2))::double precision, (round((epm1.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm1.program,
    epm1.visibility,
    epm1.hour,
    round((epm1.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm1.survey_longitude)::numeric, 2) AS survey_longitude,
    epm1.method,
    epm1.block,
    epm1.phylum,
    epm1.class,
    epm1."order",
    epm1.family,
    epm1.species_name,
    epm1.reporting_name,
    epm1.size_class,
    epm1.total,
    epm1.biomass
   FROM nrmn.ep_m1 epm1
  WHERE (((epm1.phylum)::text = ANY ((ARRAY['Actinopterygii'::character varying, 'Chondrichthyes'::character varying, 'Elasmobranchii'::character varying, 'Aves'::character varying, 'Mammalia'::character varying, 'Reptilia'::character varying, 'Cephalopoda'::character varying, 'Cnidaria'::character varying, 'Ctenophora'::character varying])::text[])) AND (NOT ((epm1.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id)))))));


--
-- TOC entry 257 (class 1259 OID 110151)
-- Name: ep_m2_cryptic_fish; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish AS
 WITH cte_to_force_joins_evaluated_first AS (
         SELECT sm.survey_id,
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
            div.full_name AS diver,
            sm.method_id AS method,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi."order",
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
          WHERE ((sm.method_id = 2) AND (((oi.class)::text = ANY ((ARRAY['Actinopterygii'::character varying, 'Elasmobranchii'::character varying])::text[])) OR ((oi.observable_item_name)::text = 'No species found'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END, sm.method_id
        )
 SELECT cte_to_force_joins_evaluated_first.survey_id,
    cte_to_force_joins_evaluated_first.country,
    cte_to_force_joins_evaluated_first.area,
    cte_to_force_joins_evaluated_first.ecoregion,
    cte_to_force_joins_evaluated_first.realm,
    cte_to_force_joins_evaluated_first.location,
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
    cte_to_force_joins_evaluated_first.survey_latitude,
    cte_to_force_joins_evaluated_first.survey_longitude,
    cte_to_force_joins_evaluated_first.diver,
    cte_to_force_joins_evaluated_first.method,
    cte_to_force_joins_evaluated_first.block,
    cte_to_force_joins_evaluated_first.phylum,
    cte_to_force_joins_evaluated_first.class,
    cte_to_force_joins_evaluated_first."order",
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
-- TOC entry 281 (class 1259 OID 110272)
-- Name: ep_m2_cryptic_fish_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m2_cryptic_fish_public AS
 SELECT epm2cf.survey_id,
    epm2cf.country,
    epm2cf.area,
    epm2cf.ecoregion,
    epm2cf.realm,
    epm2cf.location,
    epm2cf.site_code,
    epm2cf.site_name,
    round((epm2cf.latitude)::numeric, 2) AS latitude,
    round((epm2cf.longitude)::numeric, 2) AS longitude,
    epm2cf.survey_date,
    epm2cf.depth,
    public.st_setsrid(public.st_makepoint((round((epm2cf.latitude)::numeric, 2))::double precision, (round((epm2cf.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm2cf.program,
    epm2cf.visibility,
    epm2cf.hour,
    round((epm2cf.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm2cf.survey_longitude)::numeric, 2) AS survey_longitude,
    epm2cf.method,
    epm2cf.block,
    epm2cf.phylum,
        CASE
            WHEN ((epm2cf.class)::text = 'Elasmobranchii'::text) THEN 'Chondrichthyes'::character varying
            ELSE epm2cf.class
        END AS class,
    epm2cf."order",
    epm2cf.family,
    epm2cf.species_name,
    epm2cf.reporting_name,
    epm2cf.size_class,
    epm2cf.total,
    epm2cf.biomass
   FROM nrmn.ep_m2_cryptic_fish epm2cf
  WHERE (((epm2cf.family)::text = ANY ((ARRAY['Agonidae'::character varying, 'Ambassidae'::character varying, 'Anarhichadidae'::character varying, 'Antennariidae'::character varying, 'Aploactinidae'::character varying, 'Apogonidae'::character varying, 'Ariidae'::character varying, 'Aulopidae'::character varying, 'Bathymasteridae'::character varying, 'Batrachoididae'::character varying, 'Blenniidae'::character varying, 'Bothidae'::character varying, 'Bovichtidae'::character varying, 'Brachaeluridae'::character varying, 'Brachionichthyidae'::character varying, 'Bythitidae'::character varying, 'Callionymidae'::character varying, 'Caracanthidae'::character varying, 'Carapidae'::character varying, 'Centriscidae'::character varying, 'Chaenopsidae'::character varying, 'Chironemidae'::character varying, 'Cirrhitidae'::character varying, 'Clinidae'::character varying, 'Congridae'::character varying, 'Congrogadidae'::character varying, 'Cottidae'::character varying, 'Creediidae'::character varying, 'Cryptacanthodidae'::character varying, 'Cyclopteridae'::character varying, 'Cynoglossidae'::character varying, 'Dasyatidae'::character varying, 'Diodontidae'::character varying, 'Eleotridae'::character varying, 'Gnathanacanthidae'::character varying, 'Gobiesocidae'::character varying, 'Gobiidae'::character varying, 'Grammistidae'::character varying, 'Hemiscylliidae'::character varying, 'Heterodontidae'::character varying, 'Holocentridae'::character varying, 'Hypnidae'::character varying, 'Labrisomidae'::character varying, 'Leptoscopidae'::character varying, 'Liparidae'::character varying, 'Lotidae'::character varying, 'Monocentridae'::character varying, 'Moridae'::character varying, 'Muraenidae'::character varying, 'Nototheniidae'::character varying, 'Ophichthidae'::character varying, 'Ophidiidae'::character varying, 'Opistognathidae'::character varying, 'Orectolobidae'::character varying, 'Paralichthyidae'::character varying, 'Parascylliidae'::character varying, 'Pataecidae'::character varying, 'Pegasidae'::character varying, 'Pempheridae'::character varying, 'Pholidae'::character varying, 'Pinguipedidae'::character varying, 'Platycephalidae'::character varying, 'Plesiopidae'::character varying, 'Pleuronectidae'::character varying, 'Plotosidae'::character varying, 'Priacanthidae'::character varying, 'Pseudochromidae'::character varying, 'Psychrolutidae'::character varying, 'Rajidae'::character varying, 'Rhinobatidae'::character varying, 'Scorpaenidae'::character varying, 'Serranidae'::character varying, 'Scyliorhinidae'::character varying, 'Soleidae'::character varying, 'Solenostomidae'::character varying, 'Stichaeidae'::character varying, 'Synanceiidae'::character varying, 'Syngnathidae'::character varying, 'Synodontidae'::character varying, 'Tetrabrachiidae'::character varying, 'Tetrarogidae'::character varying, 'Torpedinidae'::character varying, 'Trachichthyidae'::character varying, 'Tripterygiidae'::character varying, 'Uranoscopidae'::character varying, 'Urolophidae'::character varying, 'Zaproridae'::character varying, 'Zoarcidae'::character varying])::text[])) OR (((epm2cf.species_name)::text !~ similar_escape('(Trachinops|Anthias|Caesioperca|Lepidoperca)%'::text, NULL::text)) AND (NOT ((epm2cf.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))))));


--
-- TOC entry 215 (class 1259 OID 109776)
-- Name: measure_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.measure_type_ref (
    measure_type_id integer NOT NULL,
    measure_type_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 258 (class 1259 OID 110159)
-- Name: ep_m2_inverts; Type: MATERIALIZED VIEW; Schema: nrmn; Owner: -
--

CREATE MATERIALIZED VIEW nrmn.ep_m2_inverts AS
 WITH fish_classes AS (
         SELECT
                CASE
                    WHEN ((mea.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
            div.full_name AS diver,
            sm.method_id AS method,
            sm.block_num AS block,
            oi.phylum,
            oi.class,
            oi."order",
            oi.family,
            oi.observable_item_name AS recorded_species_name,
            COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
            oi.taxon,
            oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
          WHERE ((sm.method_id = 2) AND ((((oi.phylum)::text = 'Arthropoda'::text) AND ((oi.class)::text = 'Malacostraca'::text) AND ((oi.family)::text <> 'Palaemonidae'::text)) OR (((oi.phylum)::text = 'Mollusca'::text) AND ((oi.class)::text = ANY ((ARRAY['Gastropoda'::character varying, 'Cephalopoda'::character varying])::text[]))) OR (((oi.phylum)::text = 'Echinodermata'::text) AND (((oi.class)::text = ANY ((ARRAY['Asteroidea'::character varying, 'Holuthuria'::character varying, 'Echinoidea'::character varying])::text[])) OR (((oi.class)::text = 'Ophiuroidea'::text) AND ((oi."order")::text = 'Phrynophiurida'::text)))) OR ((oi.phylum)::text = 'Platyhelminthes'::text) OR (((oi.phylum)::text = 'Cnidaria'::text) AND ((COALESCE(oi.superseded_by, oi.observable_item_name))::text = 'Phlyctenactis tuberculosa'::text)) OR (((oi.phylum)::text = 'Chordata'::text) AND ((oi.family)::text <> 'Pyuridae'::text)) OR ((oi.observable_item_name)::text = 'No species found'::text)))
          GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name,
                CASE
                    WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
                    ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
                END, sm.method_id
        )
 SELECT m2.survey_id,
    m2.country,
    m2.area,
    m2.ecoregion,
    m2.realm,
    m2.location,
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
    m2.survey_latitude,
    m2.survey_longitude,
    m2.diver,
    m2.method,
    m2.block,
    m2.phylum,
    m2.class,
    m2."order",
    m2.family,
    m2.recorded_species_name,
    m2.species_name,
    m2.taxon,
    m2.reporting_name,
    bfc.nominal AS size_class,
    m2.total,
    m2.biomass
   FROM (invert_sized m2
     JOIN bounded_fish_classes bfc ON (((m2.size_class > bfc.lower_bound) AND (m2.size_class <= bfc.upper_bound))))
  WITH NO DATA;


--
-- TOC entry 280 (class 1259 OID 110267)
-- Name: ep_m2_inverts_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m2_inverts_public AS
 SELECT epm2i.survey_id,
    epm2i.country,
    epm2i.area,
    epm2i.ecoregion,
    epm2i.realm,
    epm2i.location,
    epm2i.site_code,
    epm2i.site_name,
    round((epm2i.latitude)::numeric, 2) AS latitude,
    round((epm2i.longitude)::numeric, 2) AS longitude,
    epm2i.survey_date,
    epm2i.depth,
    public.st_setsrid(public.st_makepoint((round((epm2i.latitude)::numeric, 2))::double precision, (round((epm2i.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm2i.program,
    epm2i.visibility,
    epm2i.hour,
    round((epm2i.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm2i.survey_longitude)::numeric, 2) AS survey_longitude,
    epm2i.method,
    epm2i.block,
    epm2i.phylum,
    epm2i.class,
    epm2i."order",
    epm2i.family,
    epm2i.species_name,
    epm2i.reporting_name,
    epm2i.size_class,
    epm2i.total,
    epm2i.biomass
   FROM nrmn.ep_m2_inverts epm2i
  WHERE (NOT ((epm2i.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 261 (class 1259 OID 110177)
-- Name: ep_m3_isq; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m3_isq AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
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
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name;


--
-- TOC entry 283 (class 1259 OID 110282)
-- Name: ep_m3_isq_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m3_isq_public AS
 SELECT epm3.survey_id,
    epm3.country,
    epm3.area,
    epm3.ecoregion,
    epm3.realm,
    epm3.location,
    epm3.site_code,
    epm3.site_name,
    round((epm3.latitude)::numeric, 2) AS latitude,
    round((epm3.longitude)::numeric, 2) AS longitude,
    epm3.survey_date,
    epm3.depth,
    public.st_setsrid(public.st_makepoint((round((epm3.latitude)::numeric, 2))::double precision, (round((epm3.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm3.program,
    epm3.visibility,
    epm3.hour,
    round((epm3.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm3.survey_longitude)::numeric, 2) AS survey_longitude,
    epm3.phylum,
    epm3.class,
    epm3."order",
    epm3.family,
    epm3.species_name,
    epm3.reporting_name,
    epm3.quadrat,
    epm3.total
   FROM nrmn.ep_m3_isq epm3
  WHERE (NOT ((epm3.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 267 (class 1259 OID 110207)
-- Name: ep_m4_macrocystis_count; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m4_macrocystis_count AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
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
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name;


--
-- TOC entry 284 (class 1259 OID 110287)
-- Name: ep_m4_macrocystis_count_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m4_macrocystis_count_public AS
 SELECT epm4.survey_id,
    epm4.country,
    epm4.area,
    epm4.ecoregion,
    epm4.realm,
    epm4.location AS site_code,
    epm4.site_name,
    round((epm4.latitude)::numeric, 2) AS latitude,
    round((epm4.longitude)::numeric, 2) AS longitude,
    epm4.survey_date,
    epm4.depth,
    public.st_setsrid(public.st_makepoint((round((epm4.latitude)::numeric, 2))::double precision, (round((epm4.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm4.program,
    epm4.visibility,
    epm4.hour,
    round((epm4.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm4.survey_longitude)::numeric, 2) AS survey_longitude,
    epm4.phylum,
    epm4.class,
    epm4."order",
    epm4.family,
    epm4.species_name,
    epm4.reporting_name,
    epm4.block,
    epm4.total
   FROM nrmn.ep_m4_macrocystis_count epm4
  WHERE (NOT ((epm4.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 268 (class 1259 OID 110212)
-- Name: ep_m5_limpet_quadrats; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m5_limpet_quadrats AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
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
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, mr.measure_name, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name;


--
-- TOC entry 285 (class 1259 OID 110292)
-- Name: ep_m5_limpet_quadrats_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m5_limpet_quadrats_public AS
 SELECT epm5.survey_id,
    epm5.country,
    epm5.area,
    epm5.ecoregion,
    epm5.realm,
    epm5.location AS site_code,
    epm5.site_name,
    round((epm5.latitude)::numeric, 2) AS latitude,
    round((epm5.longitude)::numeric, 2) AS longitude,
    epm5.survey_date,
    epm5.depth,
    public.st_setsrid(public.st_makepoint((round((epm5.latitude)::numeric, 2))::double precision, (round((epm5.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm5.program,
    epm5.visibility,
    epm5.hour,
    round((epm5.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm5.survey_longitude)::numeric, 2) AS survey_longitude,
    epm5.phylum,
    epm5.class,
    epm5."order",
    epm5.family,
    epm5.species_name,
    epm5.reporting_name,
    epm5.quadrat,
    epm5.total
   FROM nrmn.ep_m5_limpet_quadrats epm5
  WHERE (NOT ((epm5.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 269 (class 1259 OID 110217)
-- Name: ep_m7_lobster_count; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_m7_lobster_count AS
 SELECT sm.survey_id,
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
    div.full_name AS diver,
    sm.block_num AS block,
    oi.phylum,
    oi.class,
    oi."order",
    oi.family,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
    oi.taxon,
    oi.reporting_name,
        CASE
            WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
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
  GROUP BY sm.survey_id, sur.country, sur.area, sit.ecoregion, sit.realm, sur.location, sur.site_code, sur.site_name, sur.latitude, sur.longitude, sur.survey_date, sur.depth, sit.geom, sur.program, sur.visibility, sur.hour, sur.survey_latitude, sur.survey_longitude, div.full_name, sm.block_num, oi.phylum, oi.class, oi."order", oi.family, oi.observable_item_name, COALESCE(oi.superseded_by, oi.observable_item_name), oi.taxon, oi.reporting_name,
        CASE
            WHEN ((meas.measure_name)::text = ANY ((ARRAY['Unsized'::character varying, 'No specimen found'::character varying])::text[])) THEN (0)::numeric
            ELSE (replace((meas.measure_name)::text, 'cm'::text, ''::text))::numeric
        END;


--
-- TOC entry 276 (class 1259 OID 110248)
-- Name: ep_site_list_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_site_list_public AS
 SELECT sr.country,
    epl.area,
    epl.location,
    epl.mpa,
    sr.site_code,
    epl.site_name,
    epl.old_site_codes,
    round((epl.latitude)::numeric, 2) AS latitude,
    round((epl.longitude)::numeric, 2) AS longitude,
    epl.wave_exposure,
    epl.relief,
    epl.slope,
    epl.currents,
    epl.realm,
    epl.province,
    epl.ecoregion,
    epl.lat_zone,
    public.st_setsrid(public.st_makepoint((round((epl.longitude)::numeric, 2))::double precision, (round((epl.latitude)::numeric, 2))::double precision), 4326) AS geom,
    epl.programs,
    epl.protection_status
   FROM (nrmn.ep_site_list epl
     JOIN nrmn.site_ref sr ON (((sr.site_code)::text = (epl.site_code)::text)))
  WHERE ((EXISTS ( SELECT 1
           FROM nrmn.survey
          WHERE (survey.site_id = sr.site_id))) AND (NOT ((epl.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id)))))));


--
-- TOC entry 262 (class 1259 OID 110182)
-- Name: ep_species_list; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_species_list AS
 SELECT oi.observable_item_id AS species_id,
    oi.observable_item_name AS recorded_species_name,
    COALESCE(oi.superseded_by, oi.observable_item_name) AS species_name,
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
    oi.common_family_name,
    oi.common_class_name,
    oi.common_phylum_name,
    NULL::unknown AS geom,
    oi.superseded_ids,
    oi.superseded_names
   FROM nrmn.ep_observable_items oi
  WHERE (((oi.obs_item_type_name)::text = ANY ((ARRAY['Species'::character varying, 'Undescribed Species'::character varying])::text[])) AND (EXISTS ( SELECT 1
           FROM nrmn.observation obs
          WHERE (obs.observable_item_id = oi.observable_item_id))) AND (oi.superseded_by IS NULL) AND (NOT ((oi.phylum)::text = ANY ((ARRAY['Cnidaria'::character varying, 'Echiura'::character varying, 'Heterokontophyta'::character varying])::text[]))) AND (NOT ((oi.class)::text = ANY ((ARRAY['Anthozoa'::character varying, 'Ascidiacea'::character varying, 'Echiuroidea'::character varying, 'Phaeophyceae'::character varying, 'Aves'::character varying])::text[]))) AND (NOT ((oi.observable_item_name)::text ~ 'spp.$'::text)));


--
-- TOC entry 278 (class 1259 OID 110258)
-- Name: ep_species_list_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_species_list_public AS
 SELECT ep_species_list.species_id,
    ep_species_list.recorded_species_name,
    ep_species_list.species_name,
    ep_species_list.taxon,
    ep_species_list.reporting_name,
    ep_species_list.phylum,
    ep_species_list.class,
    ep_species_list."order",
    ep_species_list.family,
    ep_species_list.genus,
    ep_species_list.common_name,
    ep_species_list.range,
    ep_species_list.frequency,
    ep_species_list.abundance,
    ep_species_list.max_length,
    ep_species_list.common_family_name,
    ep_species_list.common_class_name,
    ep_species_list.common_phylum_name,
    ep_species_list.geom,
    ep_species_list.superseded_ids,
    ep_species_list.superseded_names
   FROM nrmn.ep_species_list;


--
-- TOC entry 263 (class 1259 OID 110187)
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
  WHERE ((oi.obs_item_type_name)::text = ANY ((ARRAY['Species'::character varying, 'Undescribed Species'::character varying])::text[]));


--
-- TOC entry 264 (class 1259 OID 110192)
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
  WHERE ((sm.method_id = ANY (ARRAY[0, 1, 2, 7, 8, 9, 10])) AND (NOT ((oi.observable_item_name)::text ~ 'spp.$'::text)) AND (NOT ((oi.phylum)::text = ANY ((ARRAY['Cnidaria'::character varying, 'Echiura'::character varying, 'Heterokontophyta'::character varying])::text[]))) AND (NOT ((oi.class)::text = ANY ((ARRAY['Anthozoa'::character varying, 'Ascidiacea'::character varying, 'Echiuroidea'::character varying, 'Phaeophyceae'::character varying, 'Aves'::character varying])::text[]))));


--
-- TOC entry 277 (class 1259 OID 110253)
-- Name: ep_survey_list_public; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ep_survey_list_public AS
 SELECT epsl.survey_id,
    epsl.country,
    epsl.area,
    epsl.location,
    epsl.mpa,
    epsl.site_code,
    epsl.site_name,
    epsl.latitude,
    epsl.longitude,
    epsl.depth,
    epsl.survey_date,
    epsl."latest surveydate for site",
    epsl."has pq scores in db",
    epsl."has rugosity scores in db",
    epsl."has pqs catalogued in db",
    epsl.visibility,
    epsl.hour,
        CASE
            WHEN ((epsl.direction)::text ~* '(N|S|W|E|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW)'::text) THEN epsl.direction
            WHEN ((epsl.direction)::text ~* '(east|west|north|south)'::text) THEN epsl.direction
            ELSE NULL::character varying
        END AS direction,
    round((epsl.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epsl.survey_longitude)::numeric, 2) AS survey_longitude,
    epsl.avg_rugosity,
    epsl.max_rugosity,
    epsl.surface,
    public.st_setsrid(public.st_makepoint((round((epsl.longitude)::numeric, 2))::double precision, (round((epsl.latitude)::numeric, 2))::double precision), 4326) AS geom,
    epsl.program,
    epsl.pq_zip_url,
    epsl.protection_status,
    epsl.old_site_codes,
    epsl.methods,
    epsl.survey_notes
   FROM nrmn.ep_survey_list epsl
  WHERE (NOT ((epsl.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));


--
-- TOC entry 232 (class 1259 OID 109877)
-- Name: location_ref_location_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.location_ref_location_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4131 (class 0 OID 0)
-- Dependencies: 232
-- Name: location_ref_location_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.location_ref_location_id OWNED BY nrmn.location_ref.location_id;


--
-- TOC entry 240 (class 1259 OID 109964)
-- Name: measure_ref_measure_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.measure_ref_measure_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4132 (class 0 OID 0)
-- Dependencies: 240
-- Name: measure_ref_measure_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.measure_ref_measure_id OWNED BY nrmn.measure_ref.measure_id;


--
-- TOC entry 239 (class 1259 OID 109959)
-- Name: measure_type_ref_measure_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.measure_type_ref_measure_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4133 (class 0 OID 0)
-- Dependencies: 239
-- Name: measure_type_ref_measure_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.measure_type_ref_measure_id OWNED BY nrmn.measure_type_ref.measure_type_id;


--
-- TOC entry 211 (class 1259 OID 109756)
-- Name: method_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.method_ref (
    method_id integer NOT NULL,
    method_name character varying(100) NOT NULL,
    is_active boolean
);


--
-- TOC entry 241 (class 1259 OID 109974)
-- Name: obs_item_type_ref_obs_item_type_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.obs_item_type_ref_obs_item_type_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4134 (class 0 OID 0)
-- Dependencies: 241
-- Name: obs_item_type_ref_obs_item_type_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.obs_item_type_ref_obs_item_type_id OWNED BY nrmn.obs_item_type_ref.obs_item_type_id;


--
-- TOC entry 242 (class 1259 OID 109979)
-- Name: observable_item_ref_observable_item_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.observable_item_ref_observable_item_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4135 (class 0 OID 0)
-- Dependencies: 242
-- Name: observable_item_ref_observable_item_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.observable_item_ref_observable_item_id OWNED BY nrmn.observable_item_ref.observable_item_id;


--
-- TOC entry 238 (class 1259 OID 109932)
-- Name: observation_observation_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.observation_observation_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4136 (class 0 OID 0)
-- Dependencies: 238
-- Name: observation_observation_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.observation_observation_id OWNED BY nrmn.observation.observation_id;


--
-- TOC entry 245 (class 1259 OID 110009)
-- Name: pq_cat_res_ref_cat_res_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_cat_res_ref_cat_res_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4137 (class 0 OID 0)
-- Dependencies: 245
-- Name: pq_cat_res_ref_cat_res_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_cat_res_ref_cat_res_id OWNED BY nrmn.pq_cat_res_ref.cat_res_id;


--
-- TOC entry 244 (class 1259 OID 110004)
-- Name: pq_category_ref_category_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_category_ref_category_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4138 (class 0 OID 0)
-- Dependencies: 244
-- Name: pq_category_ref_category_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_category_ref_category_id OWNED BY nrmn.pq_category_ref.category_id;


--
-- TOC entry 243 (class 1259 OID 109999)
-- Name: pq_resolution_ref_resolution_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_resolution_ref_resolution_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4139 (class 0 OID 0)
-- Dependencies: 243
-- Name: pq_resolution_ref_resolution_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_resolution_ref_resolution_id OWNED BY nrmn.pq_resolution_ref.resolution_id;


--
-- TOC entry 246 (class 1259 OID 110024)
-- Name: pq_score_score_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.pq_score_score_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4140 (class 0 OID 0)
-- Dependencies: 246
-- Name: pq_score_score_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.pq_score_score_id OWNED BY nrmn.pq_score.score_id;


--
-- TOC entry 234 (class 1259 OID 109892)
-- Name: program_ref_program_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.program_ref_program_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4141 (class 0 OID 0)
-- Dependencies: 234
-- Name: program_ref_program_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.program_ref_program_id OWNED BY nrmn.program_ref.program_id;


--
-- TOC entry 293 (class 1259 OID 110819)
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
-- TOC entry 291 (class 1259 OID 110807)
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
-- TOC entry 288 (class 1259 OID 110789)
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
-- TOC entry 290 (class 1259 OID 110801)
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
-- TOC entry 292 (class 1259 OID 110813)
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
-- TOC entry 289 (class 1259 OID 110795)
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
-- TOC entry 294 (class 1259 OID 110825)
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
-- TOC entry 248 (class 1259 OID 110044)
-- Name: rugosity_rugosity_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.rugosity_rugosity_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4142 (class 0 OID 0)
-- Dependencies: 248
-- Name: rugosity_rugosity_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.rugosity_rugosity_id OWNED BY nrmn.rugosity.rugosity_id;


--
-- TOC entry 233 (class 1259 OID 109882)
-- Name: site_ref_site_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.site_ref_site_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4143 (class 0 OID 0)
-- Dependencies: 233
-- Name: site_ref_site_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.site_ref_site_id OWNED BY nrmn.site_ref.site_id;


--
-- TOC entry 247 (class 1259 OID 110039)
-- Name: surface_type_ref_surface_type_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.surface_type_ref_surface_type_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4144 (class 0 OID 0)
-- Dependencies: 247
-- Name: surface_type_ref_surface_type_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.surface_type_ref_surface_type_id OWNED BY nrmn.surface_type_ref.surface_type_id;


--
-- TOC entry 237 (class 1259 OID 109917)
-- Name: survey_method_survey_method_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.survey_method_survey_method_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4145 (class 0 OID 0)
-- Dependencies: 237
-- Name: survey_method_survey_method_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.survey_method_survey_method_id OWNED BY nrmn.survey_method.survey_method_id;


--
-- TOC entry 236 (class 1259 OID 109902)
-- Name: survey_survey_id; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.survey_survey_id
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 4146 (class 0 OID 0)
-- Dependencies: 236
-- Name: survey_survey_id; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.survey_survey_id OWNED BY nrmn.survey.survey_id;


--
-- TOC entry 275 (class 1259 OID 110244)
-- Name: ui_habitat_groups; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ui_habitat_groups AS
 SELECT DISTINCT observable_item_ref.habitat_groups
   FROM nrmn.observable_item_ref;


--
-- TOC entry 272 (class 1259 OID 110232)
-- Name: ui_mpa; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ui_mpa AS
 SELECT DISTINCT site_ref.mpa
   FROM nrmn.site_ref
  WHERE site_ref.mpa IS NOT NULL;

--
-- TOC entry 273 (class 1259 OID 110236)
-- Name: ui_protection_status; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ui_protection_status AS
 SELECT DISTINCT initcap(btrim(lower(site_ref.protection_status::text))) AS initcap
   FROM nrmn.site_ref
  WHERE site_ref.protection_status IS NOT NULL;


--
-- TOC entry 274 (class 1259 OID 110240)
-- Name: ui_report_group; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ui_report_group AS
 SELECT DISTINCT observable_item_ref.report_group
   FROM nrmn.observable_item_ref;


--
-- TOC entry 271 (class 1259 OID 110227)
-- Name: ui_species_attributes; Type: VIEW; Schema: nrmn; Owner: -
--

CREATE VIEW nrmn.ui_species_attributes AS
 SELECT obs.observable_item_id,
    COALESCE(oir.superseded_by, oir.observable_item_name) AS species_name,
    oir.common_name,
    ((oir.obs_item_attribute ->> 'is_invert_sized'::text))::boolean AS is_invert_sized,
    nrmn.length_percentile(obs.observable_item_id, 5) AS l5,
    nrmn.length_percentile(obs.observable_item_id, 95) AS l95,
    nrmn.maximum_abundance(obs.observable_item_id) AS maxabundance,
    nrmn.maximum_length(obs.observable_item_id) AS lmax
   FROM ((nrmn.observation obs
     JOIN nrmn.observable_item_ref oir ON ((oir.observable_item_id = obs.observable_item_id)))
     JOIN nrmn.obs_item_type_ref oitr ON ((oitr.obs_item_type_id = oir.obs_item_type_id)))
  WHERE (oitr.obs_item_type_id = ANY (ARRAY[1, 2]))
  GROUP BY obs.observable_item_id, oir.common_name, COALESCE(oir.superseded_by, oir.observable_item_name), (oir.obs_item_attribute ->> 'is_invert_sized'::text);


--
-- TOC entry 3828 (class 2604 OID 109901)
-- Name: diver_ref diver_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref ALTER COLUMN diver_id SET DEFAULT nextval('nrmn.diver_ref_diver_id'::regclass);


--
-- TOC entry 3829 (class 2604 OID 109881)
-- Name: location_ref location_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref ALTER COLUMN location_id SET DEFAULT nextval('nrmn.location_ref_location_id'::regclass);


--
-- TOC entry 3843 (class 2604 OID 109968)
-- Name: measure_ref measure_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref ALTER COLUMN measure_id SET DEFAULT nextval('nrmn.measure_ref_measure_id'::regclass);


--
-- TOC entry 3835 (class 2604 OID 109963)
-- Name: measure_type_ref measure_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref ALTER COLUMN measure_type_id SET DEFAULT nextval('nrmn.measure_type_ref_measure_id'::regclass);


--
-- TOC entry 3839 (class 2604 OID 109978)
-- Name: obs_item_type_ref obs_item_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref ALTER COLUMN obs_item_type_id SET DEFAULT nextval('nrmn.obs_item_type_ref_obs_item_type_id'::regclass);


--
-- TOC entry 3840 (class 2604 OID 109983)
-- Name: observable_item_ref observable_item_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref ALTER COLUMN observable_item_id SET DEFAULT nextval('nrmn.observable_item_ref_observable_item_id'::regclass);


--
-- TOC entry 3831 (class 2604 OID 109936)
-- Name: observation observation_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation ALTER COLUMN observation_id SET DEFAULT nextval('nrmn.observation_observation_id'::regclass);


--
-- TOC entry 3834 (class 2604 OID 110013)
-- Name: pq_cat_res_ref cat_res_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref ALTER COLUMN cat_res_id SET DEFAULT nextval('nrmn.pq_cat_res_ref_cat_res_id'::regclass);


--
-- TOC entry 3844 (class 2604 OID 110008)
-- Name: pq_category_ref category_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref ALTER COLUMN category_id SET DEFAULT nextval('nrmn.pq_category_ref_category_id'::regclass);


--
-- TOC entry 3833 (class 2604 OID 110003)
-- Name: pq_resolution_ref resolution_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref ALTER COLUMN resolution_id SET DEFAULT nextval('nrmn.pq_resolution_ref_resolution_id'::regclass);


--
-- TOC entry 3836 (class 2604 OID 110028)
-- Name: pq_score score_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score ALTER COLUMN score_id SET DEFAULT nextval('nrmn.pq_score_score_id'::regclass);


--
-- TOC entry 3832 (class 2604 OID 109896)
-- Name: program_ref program_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref ALTER COLUMN program_id SET DEFAULT nextval('nrmn.program_ref_program_id'::regclass);


--
-- TOC entry 3838 (class 2604 OID 110048)
-- Name: rugosity rugosity_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity ALTER COLUMN rugosity_id SET DEFAULT nextval('nrmn.rugosity_rugosity_id'::regclass);


--
-- TOC entry 3841 (class 2604 OID 109886)
-- Name: site_ref site_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref ALTER COLUMN site_id SET DEFAULT nextval('nrmn.site_ref_site_id'::regclass);


--
-- TOC entry 3830 (class 2604 OID 110043)
-- Name: surface_type_ref surface_type_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref ALTER COLUMN surface_type_id SET DEFAULT nextval('nrmn.surface_type_ref_surface_type_id'::regclass);


--
-- TOC entry 3837 (class 2604 OID 109906)
-- Name: survey survey_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey ALTER COLUMN survey_id SET DEFAULT nextval('nrmn.survey_survey_id'::regclass);


--
-- TOC entry 3842 (class 2604 OID 109921)
-- Name: survey_method survey_method_id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method ALTER COLUMN survey_method_id SET DEFAULT nextval('nrmn.survey_method_survey_method_id'::regclass);


--
-- TOC entry 3930 (class 2606 OID 109850)
-- Name: aphia_ref aphia_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_ref
    ADD CONSTRAINT aphia_ref_pkey PRIMARY KEY (aphia_id);


--
-- TOC entry 3906 (class 2606 OID 109808)
-- Name: aphia_rel_type_ref aphia_rel_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_rel_type_ref
    ADD CONSTRAINT aphia_rel_type_ref_pkey PRIMARY KEY (aphia_rel_type_id);


--
-- TOC entry 3846 (class 2606 OID 109737)
-- Name: diver_ref diver_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref
    ADD CONSTRAINT diver_ref_pkey PRIMARY KEY (diver_id);


--
-- TOC entry 3848 (class 2606 OID 109900)
-- Name: diver_ref diver_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref
    ADD CONSTRAINT diver_unique UNIQUE (initials);


--
-- TOC entry 3934 (class 2606 OID 109863)
-- Name: legacy_common_names legacy_common_names_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.legacy_common_names
    ADD CONSTRAINT legacy_common_names_pkey PRIMARY KEY (name);


--
-- TOC entry 3932 (class 2606 OID 109855)
-- Name: lengthweight_ref lengthweight_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.lengthweight_ref
    ADD CONSTRAINT lengthweight_ref_pkey PRIMARY KEY (observable_item_id);


--
-- TOC entry 3850 (class 2606 OID 109742)
-- Name: location_ref location_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref
    ADD CONSTRAINT location_ref_pkey PRIMARY KEY (location_id);


--
-- TOC entry 3852 (class 2606 OID 109880)
-- Name: location_ref location_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref
    ADD CONSTRAINT location_unique UNIQUE (location_name);


--
-- TOC entry 3922 (class 2606 OID 109837)
-- Name: measure_ref measure_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_ref_pkey PRIMARY KEY (measure_id);


--
-- TOC entry 3883 (class 2606 OID 109780)
-- Name: measure_type_ref measure_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref
    ADD CONSTRAINT measure_type_ref_pkey PRIMARY KEY (measure_type_id);


--
-- TOC entry 3885 (class 2606 OID 109962)
-- Name: measure_type_ref measure_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref
    ADD CONSTRAINT measure_type_unique UNIQUE (measure_type_name);


--
-- TOC entry 3924 (class 2606 OID 109967)
-- Name: measure_ref measure_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_unique UNIQUE (measure_name, measure_type_id);


--
-- TOC entry 3936 (class 2606 OID 109871)
-- Name: meow_ecoregions meow_ecoregions_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.meow_ecoregions
    ADD CONSTRAINT meow_ecoregions_pkey PRIMARY KEY (id);


--
-- TOC entry 3866 (class 2606 OID 109760)
-- Name: method_ref method_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.method_ref
    ADD CONSTRAINT method_ref_pkey PRIMARY KEY (method_id);


--
-- TOC entry 3868 (class 2606 OID 109958)
-- Name: method_ref method_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.method_ref
    ADD CONSTRAINT method_unique UNIQUE (method_name);


--
-- TOC entry 3902 (class 2606 OID 109803)
-- Name: obs_item_type_ref obs_item_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref
    ADD CONSTRAINT obs_item_type_ref_pkey PRIMARY KEY (obs_item_type_id);


--
-- TOC entry 3904 (class 2606 OID 109977)
-- Name: obs_item_type_ref obs_item_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref
    ADD CONSTRAINT obs_item_type_unique UNIQUE (obs_item_type_name);


--
-- TOC entry 3908 (class 2606 OID 109816)
-- Name: observable_item_ref observable_item_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT observable_item_ref_pkey PRIMARY KEY (observable_item_id);


--
-- TOC entry 3910 (class 2606 OID 109982)
-- Name: observable_item_ref observable_item_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT observable_item_unique UNIQUE (observable_item_name);


--
-- TOC entry 3862 (class 2606 OID 109755)
-- Name: observation observation_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_pkey PRIMARY KEY (observation_id);


--
-- TOC entry 3864 (class 2606 OID 109935)
-- Name: observation observation_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_unique UNIQUE (survey_method_id, observable_item_id, measure_id, diver_id, observation_attribute);


--
-- TOC entry 3879 (class 2606 OID 109775)
-- Name: pq_cat_res_ref pq_cat_res_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT pq_cat_res_ref_pkey PRIMARY KEY (cat_res_id);


--
-- TOC entry 3881 (class 2606 OID 110012)
-- Name: pq_cat_res_ref pq_cat_res_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT pq_cat_res_unique UNIQUE (resolution_id, category_id);


--
-- TOC entry 3926 (class 2606 OID 109842)
-- Name: pq_category_ref pq_category_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref
    ADD CONSTRAINT pq_category_ref_pkey PRIMARY KEY (category_id);


--
-- TOC entry 3928 (class 2606 OID 110007)
-- Name: pq_category_ref pq_category_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_category_ref
    ADD CONSTRAINT pq_category_unique UNIQUE (description);


--
-- TOC entry 3874 (class 2606 OID 109770)
-- Name: pq_resolution_ref pq_resolution_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref
    ADD CONSTRAINT pq_resolution_ref_pkey PRIMARY KEY (resolution_id);


--
-- TOC entry 3876 (class 2606 OID 110002)
-- Name: pq_resolution_ref pq_resolution_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_resolution_ref
    ADD CONSTRAINT pq_resolution_unique UNIQUE (resolution_name);


--
-- TOC entry 3889 (class 2606 OID 109785)
-- Name: pq_score pq_score_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT pq_score_pkey PRIMARY KEY (score_id);


--
-- TOC entry 3891 (class 2606 OID 110027)
-- Name: pq_score pq_score_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT pq_score_unique UNIQUE (survey_method_id, cat_res_id);


--
-- TOC entry 3870 (class 2606 OID 109765)
-- Name: program_ref program_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref
    ADD CONSTRAINT program_ref_pkey PRIMARY KEY (program_id);


--
-- TOC entry 3872 (class 2606 OID 109895)
-- Name: program_ref program_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref
    ADD CONSTRAINT program_unique UNIQUE (program_name);


--
-- TOC entry 3938 (class 2606 OID 109876)
-- Name: public_data_exclusion public_data_exclusion_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT public_data_exclusion_pkey PRIMARY KEY (program_id, site_id);


--
-- TOC entry 3898 (class 2606 OID 109798)
-- Name: rugosity rugosity_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_pkey PRIMARY KEY (rugosity_id);


--
-- TOC entry 3900 (class 2606 OID 110047)
-- Name: rugosity rugosity_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_unique UNIQUE (survey_method_id);


--
-- TOC entry 3913 (class 2606 OID 109824)
-- Name: site_ref site_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_ref_pkey PRIMARY KEY (site_id);


--
-- TOC entry 3915 (class 2606 OID 109885)
-- Name: site_ref site_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_unique UNIQUE (site_code, site_name);


--
-- TOC entry 3854 (class 2606 OID 109747)
-- Name: surface_type_ref surface_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref
    ADD CONSTRAINT surface_type_ref_pkey PRIMARY KEY (surface_type_id);


--
-- TOC entry 3856 (class 2606 OID 110042)
-- Name: surface_type_ref surface_type_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.surface_type_ref
    ADD CONSTRAINT surface_type_unique UNIQUE (surface_type_name);


--
-- TOC entry 3918 (class 2606 OID 109832)
-- Name: survey_method survey_method_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_pkey PRIMARY KEY (survey_method_id);


--
-- TOC entry 3920 (class 2606 OID 109920)
-- Name: survey_method survey_method_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_unique UNIQUE (survey_id, method_id, block_num);


--
-- TOC entry 3894 (class 2606 OID 109793)
-- Name: survey survey_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey_id);


--
-- TOC entry 3896 (class 2606 OID 109905)
-- Name: survey survey_unique; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_unique UNIQUE (site_id, survey_date, survey_time, depth, survey_num, program_id);


--
-- TOC entry 3857 (class 1259 OID 111237)
-- Name: ix_obs_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_1 ON nrmn.observation USING btree (survey_method_id);


--
-- TOC entry 3858 (class 1259 OID 111238)
-- Name: ix_obs_2; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_2 ON nrmn.observation USING btree (diver_id);


--
-- TOC entry 3859 (class 1259 OID 111240)
-- Name: ix_obs_3; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_3 ON nrmn.observation USING btree (observable_item_id);


--
-- TOC entry 3860 (class 1259 OID 111241)
-- Name: ix_obs_4; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_obs_4 ON nrmn.observation USING btree (measure_id);


--
-- TOC entry 3877 (class 1259 OID 111245)
-- Name: ix_pqcr_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqcr_1 ON nrmn.pq_cat_res_ref USING btree (resolution_id, category_id);


--
-- TOC entry 3886 (class 1259 OID 111243)
-- Name: ix_pqs_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqs_1 ON nrmn.pq_score USING btree (cat_res_id);


--
-- TOC entry 3887 (class 1259 OID 111244)
-- Name: ix_pqs_2; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_pqs_2 ON nrmn.pq_score USING btree (survey_method_id);


--
-- TOC entry 3911 (class 1259 OID 111236)
-- Name: ix_sit_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sit_1 ON nrmn.site_ref USING btree (location_id);


--
-- TOC entry 3916 (class 1259 OID 111242)
-- Name: ix_sm_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sm_1 ON nrmn.survey_method USING btree (survey_id);


--
-- TOC entry 3892 (class 1259 OID 111239)
-- Name: ix_sur_1; Type: INDEX; Schema: nrmn; Owner: -
--

CREATE INDEX ix_sur_1 ON nrmn.survey USING btree (site_id, survey_date, depth);


--
-- TOC entry 3943 (class 2606 OID 110014)
-- Name: pq_cat_res_ref cat_res_category_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT cat_res_category_fk FOREIGN KEY (category_id) REFERENCES nrmn.pq_category_ref(category_id);


--
-- TOC entry 3944 (class 2606 OID 110019)
-- Name: pq_cat_res_ref cat_res_resolution_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_cat_res_ref
    ADD CONSTRAINT cat_res_resolution_fk FOREIGN KEY (resolution_id) REFERENCES nrmn.pq_resolution_ref(resolution_id);


--
-- TOC entry 3958 (class 2606 OID 110059)
-- Name: lengthweight_ref lengthweight_observable_item_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.lengthweight_ref
    ADD CONSTRAINT lengthweight_observable_item_fk FOREIGN KEY (observable_item_id) REFERENCES nrmn.observable_item_ref(observable_item_id);


--
-- TOC entry 3957 (class 2606 OID 109969)
-- Name: measure_ref measure_measure_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_measure_type_fk FOREIGN KEY (measure_type_id) REFERENCES nrmn.measure_type_ref(measure_type_id);


--
-- TOC entry 3952 (class 2606 OID 109989)
-- Name: observable_item_ref obs_item_aphia_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_aphia_fk FOREIGN KEY (aphia_id) REFERENCES nrmn.aphia_ref(aphia_id);


--
-- TOC entry 3953 (class 2606 OID 109994)
-- Name: observable_item_ref obs_item_aphia_rel_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_aphia_rel_type_fk FOREIGN KEY (aphia_rel_type_id) REFERENCES nrmn.aphia_rel_type_ref(aphia_rel_type_id);


--
-- TOC entry 3951 (class 2606 OID 109984)
-- Name: observable_item_ref obs_item_obs_item_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT obs_item_obs_item_type_fk FOREIGN KEY (obs_item_type_id) REFERENCES nrmn.obs_item_type_ref(obs_item_type_id);


--
-- TOC entry 3942 (class 2606 OID 109952)
-- Name: observation observation_diver_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_diver_fk FOREIGN KEY (diver_id) REFERENCES nrmn.diver_ref(diver_id);


--
-- TOC entry 3941 (class 2606 OID 109947)
-- Name: observation observation_measure_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_measure_fk FOREIGN KEY (measure_id) REFERENCES nrmn.measure_ref(measure_id);


--
-- TOC entry 3940 (class 2606 OID 109942)
-- Name: observation observation_observable_item_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_observable_item_fk FOREIGN KEY (observable_item_id) REFERENCES nrmn.observable_item_ref(observable_item_id);


--
-- TOC entry 3939 (class 2606 OID 109937)
-- Name: observation observation_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id) ON DELETE CASCADE;


--
-- TOC entry 3960 (class 2606 OID 110069)
-- Name: public_data_exclusion public_data_exclusion_program_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT public_data_exclusion_program_fk FOREIGN KEY (program_id) REFERENCES nrmn.program_ref(program_id);


--
-- TOC entry 3961 (class 2606 OID 110074)
-- Name: public_data_exclusion public_data_exclusion_site_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT public_data_exclusion_site_fk FOREIGN KEY (site_id) REFERENCES nrmn.site_ref(site_id);


--
-- TOC entry 3950 (class 2606 OID 110054)
-- Name: rugosity rugosity_surface_type_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_surface_type_fk FOREIGN KEY (surface_type_id) REFERENCES nrmn.surface_type_ref(surface_type_id);


--
-- TOC entry 3949 (class 2606 OID 110049)
-- Name: rugosity rugosity_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.rugosity
    ADD CONSTRAINT rugosity_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id);


--
-- TOC entry 3946 (class 2606 OID 110034)
-- Name: pq_score score_cat_res_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT score_cat_res_fk FOREIGN KEY (cat_res_id) REFERENCES nrmn.pq_cat_res_ref(cat_res_id);


--
-- TOC entry 3945 (class 2606 OID 110029)
-- Name: pq_score score_survey_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.pq_score
    ADD CONSTRAINT score_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method(survey_method_id);


--
-- TOC entry 3954 (class 2606 OID 109887)
-- Name: site_ref site_location_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_location_fk FOREIGN KEY (location_id) REFERENCES nrmn.location_ref(location_id);


--
-- TOC entry 3959 (class 2606 OID 110064)
-- Name: atrc_rugosity survey_atrc_rugosity_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.atrc_rugosity
    ADD CONSTRAINT survey_atrc_rugosity_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey(survey_id);


--
-- TOC entry 3956 (class 2606 OID 109927)
-- Name: survey_method survey_method_method_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_method_fk FOREIGN KEY (method_id) REFERENCES nrmn.method_ref(method_id);


--
-- TOC entry 3955 (class 2606 OID 109922)
-- Name: survey_method survey_method_survey_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_survey_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey(survey_id) ON DELETE CASCADE;


--
-- TOC entry 3948 (class 2606 OID 109912)
-- Name: survey survey_program_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_program_fk FOREIGN KEY (program_id) REFERENCES nrmn.program_ref(program_id);


--
-- TOC entry 3947 (class 2606 OID 109907)
-- Name: survey survey_site_fk; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_site_fk FOREIGN KEY (site_id) REFERENCES nrmn.site_ref(site_id);


-- Completed on 2021-03-03 07:32:27 AEDT

--
-- PostgreSQL database dump complete
--

