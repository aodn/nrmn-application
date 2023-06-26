INSERT INTO nrmn.observable_item_ref
(observable_item_id,observable_item_name,obs_item_type_id,aphia_id,aphia_rel_type_id,common_name,superseded_by,
 phylum,class,order,family,genus,species_epithet,report_group,habitat_group, letter_code,is_invert_sized,
 created,updated,mapped_id)
VALUES
       (8047,'Pseudorhombus arsius',1,220051,,'Largetooth flounder',,'Chordata','Actinopteri','Pleuronectiformes','Paralichthyidae','Pseudorhombus,arsius','','','',,,'2022-08-25 02:48:29.411357','2022-08-25 02:48:29.411382',99908047),
       (8114,'Ostorhinchus doederleini',1,713303,,'Four lined cardinalfish',,'Chordata','Actinopterygii','Perciformes','Apogonidae','Ostorhinchus','doederleini','','','',,,'2023-04-21 00:02:04.687858','2023-06-03 03:39:16.943911',99908114),
       (8124,'Morwong fuscus',1,1526241,,'Red morwong',,'Chordata,Teleostei','Centrarchiformes','Latridae','Morwong','fuscus','','','',,,'2023-06-13 03:17:15.823116','2023-06-13 03:18:12.186339',99908124),
       (3751,'Abudefduf luridus',1,126999,1,'Canary damsel',,'Chordata','Actinopterygii','Perciformes','Pomacentridae','Abudefduf','luridus',,,,false,'{"MaxLength": 15.0}',,,4573),
       (3053,'Abudefduf spp.',1,126044,1,,,'Chordata','Actinopterygii','Perciformes','Pomacentridae','Abudefduf',,,,,false,'{"MaxLength": 19.0}',,,3872),
       (4799,'Acanthemblemaria spp.',1,254882,1,,,'Chordata','Actinopterygii','Perciformes','Chaenopsidae','Acanthemblemaria',,,,,false,'{"MaxLength": 4.7875}',,,5686),
       (2461,'Acanthistius spp.',1,206247,1,,,'Chordata','Actinopterygii','Perciformes','Serranidae','Acanthistius',,,,,false,'{}',,,3276),
       (788,'Acanthuridae spp.',1,125515,1,'Unidentified surgeonfish',,'Chordata','Actinopterygii','Perciformes','Acanthuridae',,,,,'ACAN',false,'{"MaxLength": 40.44545455}',,,1415),
       (7065,'Acanthurus sp. [achilles]',2,125908,2,'[A surgeonfish]',,'Chordata,Actinopterygii','Perciformes','Acanthuridae','Acanthurus',,,,,false,'{}',,,6916),
       (6819,'Acanthurus sp. [fowleri]',2,125908,2,'[A surgeonfish]',,'Chordata','Actinopterygii','Perciformes','Acanthuridae','Acanthurus',,,,,false,'{"MaxLength": 45.0}',,,5205),
       (6820,'Acanthurus sp. [pyroferus]',2,125908,2,'[A surgeonfish]',,'Chordata','Actinopterygii','Perciformes','Acanthuridae','Acanthurus',,,,,false,'{"MaxLength": 25.0}',,,5206),
       (3069,'Acanthurus spp.',1,125908,1,'[A surgeonfish]',,'Chordata','Actinopterygii','Perciformes','Acanthuridae','Acanthurus',,,,,false,'{"MaxLength": 36.33333333}',,,3888),
       (6849,'Actinopterygii sp. [Epiuris annularis]',2,10194,2,,,'Chordata','Actinopterygii',,,,,,,,false,'{"MaxLength": 15.0}',,,5265),
       (8024,'Eviota cf. teresae',1,889535,,'Whitelined Eviota',,'Chordata','Actinopterygii','Gobiiformes','Gobiidae,Eviota','','','','',,,'2022-06-20 05:39:36.363578','2022-06-20 05:39:36.363601',99908024),
       (8101,'Plectropomus marisrubri',1,1577317,,'Roving coral grouper',,'Chordata','Teleostei','Perciformes','Serranidae','Plectropomus','marisrubri','','','',,,'2023-03-07 02:16:25.998813','2023-03-07 02:16:25.998824',99908101);

INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf, sgfgu)
VALUES
    (8047,0.006025599781423807,3.1500000953674316,1,null),
    (8114,0.009,3.46,0.9416196,null),
    (8124,0.016,2.989,null,null),
    (3751,0.0213,3.152,0.859845228,'G'),
    (3053,0.0213,3.152,0.859845228,'G'),
    (4799,0.0063,3.217,1,'G'),
    (2461,0.0048,3.175,1,'F'),
    (788,0.0426,2.8683,0.9149131,null),
    (7065,0.00245,3,1,'S'),
    (6819,0.0251,3.032,0.919399325,'G'),
    (6820,0.0251,3.032,0.919399325,'G'),
    (3069,0.0251,3.032,0.919399325,'G'),
    (6849,0.014850099,3.048667987,1,'Gu');