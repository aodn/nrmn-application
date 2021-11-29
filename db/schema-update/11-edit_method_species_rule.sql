DROP FUNCTION nrmn.assign_species_to_method();

CREATE OR REPLACE FUNCTION nrmn.assign_species_to_method(
	)
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
WHERE obs."class" IN ('Actinopterygii','Reptilia','Elasmobranchii','Mammalia','Cephalopoda','Aves')
EXCEPT
SELECT observable_item_id,'1'::integer from nrmn.observable_item_ref
WHERE observable_item_name IN ('Unidentified cryptic fish','Unidentified fish (cryptic)');
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
WHERE family IN ('Agonidae','Ambassidae','Anarhichadidae','Antennariidae','Aploactinidae','Apogonidae','Ariidae',
'Aulopidae','Bathymasteridae','Batrachoididae','Blenniidae','Bothidae','Bovichtidae','Brachaeluridae',
'Brachionichthyidae','Bythitidae','Callionymidae','Caracanthidae','Carapidae','Centriscidae','Chaenopsidae',
'Chironemidae','Cirrhitidae','Clinidae','Congridae','Congrogadidae','Cottidae','Creediidae','Cryptacanthodidae',
'Cyclopteridae','Cynoglossidae','Dasyatidae','Diodontidae','Eleotridae','Gnathanacanthidae','Gobiesocidae','Gobiidae',
'Grammistidae','Hemiscylliidae','Heterodontidae','Hexagrammidae','Holocentridae','Hypnidae','Labrisomidae','Leptoscopidae','Liparidae',
'Lotidae','Monocentridae','Moridae','Muraenidae','Nototheniidae','Ophichthidae','Ophidiidae','Opistognathidae',
'Orectolobidae','Paralichthyidae','Parascylliidae','Pataecidae','Pegasidae','Pempheridae','Pholidae','Pinguipedidae',
'Platycephalidae','Plesiopidae','Pleuronectidae','Plotosidae','Priacanthidae','Pseudochromidae',
'Psychrolutidae','Rajidae','Rhinobatidae','Scorpaenidae','Serranidae','Scyliorhinidae','Soleidae','Solenostomidae',
'Stichaeidae','Synanceiidae','Syngnathidae','Synodontidae',
'Tetrabrachiidae','Tetrarogidae','Torpedinidae','Trachichthyidae','Tripterygiidae','Uranoscopidae','Urolophidae',
'Zaproridae','Zoarcidae')
OR observable_item_name IN ('Unidentified cryptic fish','Unidentified fish (cryptic)')
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
END;
$BODY$;
