INSERT INTO nrmn.observable_item_ref
(observable_item_id,observable_item_name,obs_item_type_id,aphia_id,aphia_rel_type_id,common_name,superseded_by,
 phylum,class,order,family,genus,species_epithet,report_group,habitat_group, letter_code,is_invert_sized,
 obs_item_attribute,created,updated,mapped_id)
VALUES
       (8114,'Ostorhinchus doederleini',1,713303,1,'Four lined cardinalfish',null,'Chordata','Actinopterygii','Perciformes','Apogonidae','Ostorhinchus','doederleini',null,null,null,false,null,'2023-04-21 00:02:04.687858','2023-06-03 03:39:16.943911',99908114),
       (810,'Apogon doederleini',1,273008,1,'Four-line cardinalfish','Ostorhinchus doederleini','Chordata','Actinopterygii','Perciformes','Apogonidae','Apogon','doederleini',null,null,'ADOE',false,'{"MaxLength": 14.0, "OtherGroups": "Benthic,Planktivore"}',null,null,1438),
       (3762,'Acanthostracion polygonius',1,158919,1,'Honeycomb cowfish',null,'Chordata','Actinopterygii','Tetraodontiformes','Ostraciidae','Acanthostracion','polygonius',null,null,null,false,'{"MaxLength": 50.0}',null,null,4584),
       (8124,'Morwong fuscus',1,1526241,1,null,'Red morwong',null,'Chordata,Teleostei','Centrarchiformes','Latridae','Morwong','fuscus',null,null,null,false,null,'2023-06-13 03:17:15.823116','2023-06-13 03:18:12.186339',99908124),
       (2367,'Arenigobius frenatus',1,313758,1,'Halfbridled Goby',null,'Chordata','Actinopterygii','Perciformes','Gobiidae','Arenigobius','frenatus',null,null,null,false,'{"MaxLength": 18.0}',null,null,3182),
       (788,'Acanthuridae spp.',1,125515,1,'Unidentified surgeonfish',null,'Chordata','Actinopterygii','Perciformes','Acanthuridae',null,null,null,null,'ACAN',false,'{"MaxLength": 40.44545455}',null,null,1415),
       (6820,'Acanthurus sp. [pyroferus]',2,125908,2,'[A surgeonfish]',null,'Chordata','Actinopterygii','Perciformes','Acanthuridae','Acanthurus',null,null,null,null,false,'{"MaxLength": 25.0}',null,null,5206),
       (3069,'Acanthurus spp.',1,125908,1,'[A surgeonfish]',null,'Chordata','Actinopterygii','Perciformes','Acanthuridae','Acanthurus',null,null,null,null,false,'{"MaxLength": 36.33333333}',null,null,3888),
       (6849,'Actinopterygii sp. [Epiuris annularis]',2,10194,2,null,null,'Chordata','Actinopterygii',null,null,null,null,null,null,null,false,'{"MaxLength": 15.0}',null,null,5265),
       (8027,'Tripneustes kermadecensis',1,1075052,1,null,null,'Echinodermata','Echinoidea','Camarodonta','Toxopneustidae','Tripneustes','kermadecensis',null,null,null,true,null,'2022-07-04 05:28:55.950040','2022-07-04 05:28:55.950046',99908027),
       (4935,'Cypraea annulus',1,216777,1,null,'Monetaria annulus','Mollusca','Gastropoda','Littorinimorpha','Cypraeidae',null,null,null,null,null,true,null,null,null,5861);

INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf, sgfgu)
VALUES
    (8114,0.009,3.46,0.9416196,null),
    (810,0.009,3.46,0.9416196,null),
    (3762,0.0517,2.679,0.812677773,'S'),
    (8124,0.016,2.989,null,null),
    (2367,0.013,2.882,1,'F'),
    (788,0.0426,2.8683,0.9149131,null),
    (6820,0.0251,3.032,0.919399325,'G'),
    (3069,0.0251,3.032,0.919399325,'G'),
    (6849,0.014850099,3.048667987,1,'Gu'),
    (8027,0.0213,3.152,0.859845228,'G'),
    (4935,0.0063,3.217,1,'G');
