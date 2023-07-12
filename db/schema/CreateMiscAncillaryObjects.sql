\echo 'You must run this sql using psql, e.g. psql -h localhost -d nrmn_dev -U postgres -f CreateMiscAncillaryObjects.sql'

create or replace function nrmn.obs_biomass(
	a float,
	b float,
	cf float,
	sizeclass float,
	num int,
	use_sizeclass_correction boolean default false
)
returns float as $$

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

end; $$
LANGUAGE PLPGSQL
SET search_path = nrmn,public;


create or replace function nrmn.taxonomic_name(
	obs_item_name varchar(100),
	allow_square_brackets bool default true
)
returns varchar(100) as $$
declare taxonomic_name   varchar(100);
begin

	select
		case
			when oi.phylum='Substrate'
				 then oi.observable_item_name
			when observable_item_name ~ '[\[\]]' and allow_square_brackets
				 then oi.observable_item_name
			when oi.species_epithet is not null and oi.genus is not null
				 then concat(oi.genus, ' ', oi.species_epithet)
			when oi.species_epithet is null and oi.genus is not null
				 then concat(oi.genus, ' spp.')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is not null
				 then concat(replace(oi.family, 'idae', 'id'), ' spp.')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is not null
				 then concat(oi."order", ' spp.')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is null
				 and oi.class is not null
				 then concat(oi.class, ' spp.')
			when oi.species_epithet is null and oi.genus is null
				 and oi.family is null and oi."order" is null
				 and oi.class is null and oi.phylum is not null
				 then concat(oi.phylum, ' spp.')
		end	taxonomic_name
		into taxonomic_name
from observable_item_ref oi
where oi.observable_item_name = obs_item_name;

return taxonomic_name;

end; $$
LANGUAGE PLPGSQL
SET search_path = nrmn,public;

-- l5/l95 percentile aggregation support

CREATE OR REPLACE FUNCTION nrmn.accum_counts (accum_counts numeric[], size_class numeric, total_count numeric)
    RETURNS numeric[]
    LANGUAGE 'plpgsql'
    COST 100 VOLATILE
    SET search_path = nrmn, public
    AS $BODY$
BEGIN
    IF size_class IS null THEN
        RETURN accum_counts;
    ELSE
        RETURN accum_counts || ARRAY[[size_class, total_count]];
    END IF;
END;
$BODY$;

CREATE OR REPLACE FUNCTION nrmn.calc_percentile (measure_counts numeric[][], percentile integer)
    RETURNS numeric
    LANGUAGE 'plpgsql'
    COST 100 VOLATILE
    SET search_path = nrmn, public
    AS $BODY$
DECLARE
    total_count numeric;
    accum_count numeric;
    percentile_count integer;
    cumulative_count integer;
    prev_cumulative_count integer;
    prev_measure_count numeric[2];
    measure_count numeric[2];
BEGIN
    -- handle edge cases
    IF array_length(measure_counts, 1) IS NULL THEN
        -- return null if array is empty
        RETURN NULL;
    ELSIF array_length(measure_counts, 1) = 1 THEN
        -- only one measure count - return that measure
        RETURN measure_counts[1][1];
    END IF;
    -- calculate percentile count
    SELECT
        sum(ct) INTO total_count
    FROM
        unnest(measure_counts[1:array_length(measure_counts, 1)][2:2]) AS ct;
    percentile_count := (total_count * percentile / 100) AS Integer;
    -- check for a measure count higher than the percentile count and return that,
    -- or the previous measure, if there is one, and its closer
    cumulative_count := 0;
    FOREACH measure_count SLICE 1 IN ARRAY measure_counts LOOP
        cumulative_count := cumulative_count + measure_count[2];
        IF cumulative_count < percentile_count THEN
            prev_measure_count = measure_count;
            prev_cumulative_count = cumulative_count;
            CONTINUE;
        ELSIF prev_cumulative_count IS NULL THEN
            RETURN measure_count[1];
        ELSIF abs(percentile_count - prev_cumulative_count) <= abs(percentile_count - cumulative_count) THEN
            RETURN prev_measure_count[1];
        ELSE
            RETURN measure_count[1];
        END IF;
    END LOOP;
    -- no measure with a count higher than the percentile count,
    -- return the last measure - its the closest
    RETURN measure_counts[array_length(measure_counts, 1)][1];
END;
$BODY$;

CREATE OR REPLACE FUNCTION nrmn.calc_l5 (measure_counts numeric[][])
    RETURNS numeric
    LANGUAGE 'plpgsql'
    COST 100 VOLATILE
    SET search_path = nrmn, public
    AS $BODY$
BEGIN
    RETURN calc_percentile (measure_counts, 5);
END;
$BODY$;

CREATE OR REPLACE FUNCTION nrmn.calc_l95 (measure_counts numeric[][])
    RETURNS numeric
    LANGUAGE 'plpgsql'
    COST 100 VOLATILE
    SET search_path = nrmn, public
    AS $BODY$
BEGIN
    RETURN calc_percentile (measure_counts, 95);
END;
$BODY$;

DROP AGGREGATE IF EXISTS nrmn.l5(numeric, numeric) CASCADE;
CREATE AGGREGATE nrmn.l5 (size_class numeric, total_count numeric) (
    STYPE = numeric[][2],
    SFUNC = nrmn.accum_counts,
    FINALFUNC = nrmn.calc_l5,
    INITCOND = '{}'
);

DROP AGGREGATE IF EXISTS nrmn.l95(numeric, numeric) CASCADE;
CREATE AGGREGATE nrmn.l95 (size_class numeric, total_count numeric) (
    STYPE = numeric[][],
    SFUNC = nrmn.accum_counts,
    FINALFUNC = nrmn.calc_l95,
    INITCOND = '{}'
);

CREATE OR REPLACE FUNCTION nrmn.abbreviated_species_code(species_name varchar, code_length integer)
RETURNS varchar as $$
BEGIN
    return CASE
               WHEN species_name LIKE '% spp.'
                   THEN substring(species_name from 1 for (position(' ' in species_name) - 1))
               ELSE substring(species_name from 1 for 1)
                   || substring(species_name from (1 + position(' ' in species_name)) for (code_length - 1))
        END;
END; $$
LANGUAGE PLPGSQL
SET search_path = nrmn,public;

CREATE OR REPLACE FUNCTION nrmn.assign_species_to_method()
    RETURNS integer
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    SET search_path=nrmn, public
    AS $BODY$
BEGIN
TRUNCATE TABLE nrmn.methods_species;
-- M1
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'1'::integer from nrmn.observable_item_ref  obs
WHERE obs."class" IN ('Actinopterygii','Actinopteri','Teleostei','Reptilia','Elasmobranchii','Mammalia',
'Cephalopoda','Aves')
EXCEPT
SELECT observable_item_id,'1'::integer from nrmn.observable_item_ref
WHERE observable_item_name IN ('Unidentified cryptic fish','Unidentified fish (cryptic)','Unidentified eel');
--M2 inverts
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'2'::integer from nrmn.observable_item_ref  obs
WHERE obs."class" IN ('Asteroidea','Anopla','Bivalvia','Cephalopoda','Crinoidea','Cubozoa','Echinoidea','Gastropoda',
'Hexanauplia','Holothuroidea','Hydrozoa','Ophiuroidea','Malacostraca','Maxillopoda','Merostomata','Polychaeta',
'Polyplacophora','Pycnogonida','Rhabditophora','Tentaculata','Turbellaria') or
coalesce(superseded_by,observable_item_name) ='Phlyctenactis tuberculosa';
--M2 cryptic
--Update has to be applied after updating M1, as it targets Fishes(Actinopterygii,Elasmobranchii) at family level
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'2'::integer from nrmn.observable_item_ref
WHERE (family IN ('Agonidae','Anarhichadidae','Anguillidae','Antennariidae','Aploactinidae','Apogonidae','Ariidae',
'Bathymasteridae','Batrachoididae','Blenniidae','Bothidae','Bovichtidae','Brachaeluridae','Brachionichthyidae',
'Bythitidae','Callionymidae','Caracanthidae','Carapidae','Chaenopsidae','Chironemidae','Cirrhitidae','Clinidae',
'Congiopodidae','Congridae','Congrogadidae','Cottidae','Creediidae','Cryptacanthodidae','Cyclopteridae','Cynoglossidae',
'Dasyatidae','Diodontidae','Eleotridae','Gnathanacanthidae','Gobiesocidae','Gobiidae','Grammistidae','Harpagiferidae',
'Hemiscylliidae','Heterodontidae','Hexagrammidae','Holocentridae','Hypnidae','Labrisomidae','Latidae','Leptoscopidae',
'Liparidae','Lotidae','Monocentridae','Moridae','Muraenidae','Nototheniidae','Ophichthidae','Ophidiidae',
'Opistognathidae','Orectolobidae','Paralichthyidae','Parascylliidae','Pataecidae','Pegasidae','Pempheridae',
'Percophidae','Phycidae','Pholidae','Pholidichthyidae','Pinguipedidae','Platycephalidae','Plesiopidae','Pleuronectidae',
'Plotosidae','Priacanthidae','Pseudochromidae','Psychrolutidae','Rajidae','Rhinobatidae','Scorpaenidae','Serranidae',
'Scyliorhinidae','Soleidae','Solenostomidae','Stichaeidae','Synanceiidae','Syngnathidae','Synodontidae',
'Tetrabrachiidae','Tetrarogidae','Torpedinidae','Trachichthyidae','Trachinidae','Tripterygiidae','Uranoscopidae',
'Urolophidae','Urotrygonidae','Zaproridae','Zoarcidae')
OR observable_item_name IN ('Unidentified cryptic fish','Unidentified fish (cryptic)','Actinopterygii spp.',
'Teleostei spp.','Elasmobranchii spp.','Unidentified eel') OR ("class"='Reptilia' AND "order"='Squamata'))
 EXCEPT
 SELECT observable_item_id,'2'::integer from nrmn.observable_item_ref
 WHERE genus IN ('Trachinops','Anthias','Caesioperca','Lepidoperca');
-- M3
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'3'::integer from nrmn.observable_item_ref  obs
WHERE "class" in ('Anthozoa','Maxillopoda','Polychaeta','Ascidiacea','Hydrozoa','Staurozoa','Bivalvia')
OR phylum IN('Porifera','Bryozoa','Algae','Brachiopoda','Substrate','Magnoliophyta','Chlorophyta','Dinophyta',
'Heterokontophyta','Ochrophyta','Rhodophyta','Tracheophyta','Animalia','Cyanophyceae') or family='Vermetidae' and not
coalesce(superseded_by,observable_item_name) ='Phlyctenactis tuberculosa';
--M4
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'4'::integer from nrmn.observable_item_ref  obs
WHERE genus ='Macrocystis';
--M5
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'5'::integer from nrmn.observable_item_ref  obs
WHERE family IN('Siphonariidae','Turbinidae','Lottiidae','Nacellidae','Patellidae','Acmaeidae');

--M7
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'7'::integer from nrmn.observable_item_ref  obs
WHERE family ='Palinuridae';

--Debris
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'2'::integer from nrmn.observable_item_ref  obs
WHERE obs_item_type_id = 5;

-- No Species Found -> observable in M1 and M2
INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'1'::integer from nrmn.observable_item_ref  obs
WHERE obs_item_type_id = 6;

INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'2'::integer from nrmn.observable_item_ref  obs
WHERE obs_item_type_id = 6;

INSERT INTO nrmn.methods_species(observable_item_id,method_id)
SELECT observable_item_id,'0'::integer from nrmn.observable_item_ref  obs
WHERE obs_item_type_id IN (1,2,5);

IF (select count(*) from nrmn.methods_species)>0
THEN RETURN 1;
END IF;
RETURN 0;
END;
$BODY$;


CREATE OR REPLACE FUNCTION nrmn.set_is_invert_species()
returns integer as $$
BEGIN
UPDATE nrmn.observable_item_ref SET is_invert_sized = true
WHERE "class" IN ('Anopla','Aplacophora','Ascidiacea','Asteroidea','Bivalvia','Crinoidea','Cubozoa','Dinophyceae',
'Echinoidea','Gastropoda','Malacostraca','Maxillopoda','Nuda','Ophiuroidea','Polychaeta','Polyplacophora',
'Pycnogonida','Rhabditophora','Scyphozoa','Staurozoa','Tentaculata','Turbellaria');
IF (select count(*) from nrmn.observable_item_ref where is_invert_sized = true)>0
THEN RETURN 1;
END IF;
RETURN 0;
END; $$
LANGUAGE PLPGSQL
SET search_path = nrmn,public;


CREATE OR REPLACE FUNCTION nrmn.refresh_materialized_views ()
    RETURNS VOID
    SECURITY DEFINER
    LANGUAGE plpgsql
    AS $$
BEGIN
    REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes;
    REFRESH MATERIALIZED VIEW nrmn.ep_site_list;
    REFRESH MATERIALIZED VIEW nrmn.ep_survey_list;
    REFRESH MATERIALIZED VIEW nrmn.ep_rarity_abundance;
    REFRESH MATERIALIZED VIEW nrmn.ep_rarity_extents;
    REFRESH MATERIALIZED VIEW nrmn.ep_rarity_frequency;
    REFRESH MATERIALIZED VIEW nrmn.ep_rarity_range;
    REFRESH MATERIALIZED VIEW nrmn.ep_observable_items;
    REFRESH MATERIALIZED VIEW nrmn.ep_m1;
    REFRESH MATERIALIZED VIEW nrmn.ep_m2_cryptic_fish;
    REFRESH MATERIALIZED VIEW nrmn.ep_m2_inverts;
    RETURN;
END
$$;

CREATE OR REPLACE FUNCTION nrmn.set_species_attributes()
    RETURNS VOID
    SECURITY DEFINER
    LANGUAGE plpgsql
    AS $$
BEGIN
    PERFORM nrmn.assign_species_to_method();
    PERFORM nrmn.set_is_invert_species();
END
$$;
SET search_path = nrmn,public;

-- Execute function after update
DO $$
BEGIN
  PERFORM nrmn.set_species_attributes();
END;
$$;

-- The above script drop these views and hence need to execute those sql too.
\i '../endpoints/CreatePublicEndpoints.sql';
\i '../endpoints/CreatePrivateEndpoints.sql';
\i '../endpoints/EndpointIndexes.sql';