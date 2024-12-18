-- nrmn.rugosity is empty for (85720, 85721, 85722, 85723)
INSERT INTO nrmn.aphia_rel_type_ref (aphia_rel_type_id, aphia_rel_type_name)
VALUES (1, 'is'),
       (2, 'rolls up to');

INSERT INTO nrmn.obs_item_type_ref (obs_item_type_id, obs_item_type_name, is_active)
VALUES (1, 'Species', true),
       (2, 'Undescribed Species', true),
       (3, 'Algae', true),
       (4, 'Substrate', true),
       (5, 'Debris', true),
       (6, 'Absence', true);

INSERT INTO nrmn.method_ref(method_id,method_name,is_active)
    VALUES (0, 'Off transect sightings or observations', true),
           (1, 'Standard fish', true),
           (2, 'Standard invertebrates & cryptic fish', true),
           (3, 'Standard quadrat', true),
           (4, 'Macrocystis count', true),
           (5, 'Limpet quadrat', true),
           (6, 'Rugosity', true),
           (7, 'Additional lobster counts (Jurien Bay)', true),
           (10, 'Seagrass fish survey', false),
           (11, 'Off transect', true),
           (12, 'Debris', true),
           (13, 'Photo quadrat scores', true);

INSERT INTO nrmn.measure_type_ref(measure_type_id, measure_type_name, is_active)
    VALUES (1,'Fish Size Class',true),
        (2,'In Situ Quadrat',true),
        (3,'Macrocystis Block',true),
        (4,'Invert Size Class',true),
        (5,'Single Item',true),
        (6,'Absence',true),
        (7,'Limpet Quadrat',true);

INSERT INTO nrmn.measure_ref(measure_id,measure_type_id,measure_name,seq_no,is_active)
    VALUES (64,4,'6cm',12,true),
            (66,4,'7cm',14,true),
            (67,4,'7.5cm',15,true),
            (68,4,'8cm',16,true),
            (70,4,'9cm',18,true),
            (72,4,'10cm',20,true),
            (86,4,'19cm',34,true),
            (5,1,'10cm',4,true),
            (3,1,'5cm',2,true),
            (4,1,'7.5cm',3,true),
            (6,1,'12.5cm',5,true),
            (7,1,'15cm',6,true),
            (8,1,'20cm',7,true),
            (9,1,'25cm',8,true),
            (10,1,'30cm',9,true),
            (11,1,'35cm',10,true),
            (12,1,'40cm',11,true),
            (42,2,'Q1',1,true),
            (43,2,'Q2',2,true),
            (44,2,'Q3',3,true),
            (45,2,'Q4',4,true),
            (46,2,'Q5',5,true),
            (13,1,'50cm',12,true);

INSERT INTO nrmn.program_ref (program_id, program_name, is_active)
    VALUES (1, 'RLS', True),
            (2, 'ATRC',	true);

INSERT INTO nrmn.location_ref (location_id, location_name, is_active)
    VALUES (184, '"Lord Howe Island"',true),
            (41,'Kent Group',true);

INSERT INTO nrmn.diver_ref (diver_id,initials,full_name,created)
    VALUES (21,'134','Jan Jansen', null),
            (180,'ESO','Liz Oh', null);

INSERT INTO nrmn.site_ref(site_id,site_code,site_name,longitude,latitude,geom,location_id,state,
    country,old_site_code,mpa,protection_status,relief,currents,wave_exposure,slope,site_attribute,is_active)
VALUES  (914,'KG-S11','Deal Island (Murray Pass)',147.31422,-39.46125,'0101000020E6100000F9F719170E6A6240D7A3703D0ABB43C0',
        41,'Tasmania','Australia','{1111}','Kent Group Marine Park','No take multizoned',3,4,1,3,
        '{"Isolation": 1.0, "Zone_name": "Deal Island No take", "ProxCountry": "Australia", "area_in_km2": "138", "Effectiveness": "High", "year_of_protection": "2001", "Distance_to_boundary_in_km": 1.26}',
        true),
        (3834,'LHI37','Malabar 2',159.05615,-31.5113,'0101000020E610000074B515FBCBE16340DE718A8EE4823FC0',
        184,'New South Wales','Australia',null,'Lord Howe Island Marine Park','No take multizoned',4,3,3,2,
        '{
            "Age": "1",
            "Zone":"Neds Beach and Admiralty Islands Sanctuary Zone",
            "area":"0",
            "No_take":"1",
            "Isolation":1.0,
            "Zone_name":"Neds Beach and Admiralty Islands Sanctuary Zone",
            "NEOLI_Total":"3",
            "ProxCountry":"Australia",
            "area_in_km2":"4.8",
            "Effectiveness":"High",
            "Gears_allowed":"Allbanned",
            "Isolation_NEOLI":"0",
            "year_of_protection":"2004",
            "Effectiveness_NEOLI":"1",
            "Shore_fishing_index":"1.0",
            "Distance_to_boat_ramp": "5.5",
            "Rec_methods_permitted":"-",
            "com_methods_permitted":"-",
            "offshore_extent_in_km":"1.5",
            "Is_rec_fishing_allowed":
            "N","perimeter_length_in_km":"9.1",
            "Distance_to_boundary_in_km":"0.46",
            "Is_commercial_fishing_allowed":"N",
            "Is_it_a_shore_rec_fishing_zone_within_SZ":"N"
          }',
          true),
        (3844,'LHI38','North Bay 2',159.04688000000002,-31.52113,'0101000020E6100000C55A7C0A80E16340E8F692C668853FC0',
         184,'New South Wales','Australia',null,'Lord Howe Island Marine Park','No take multizoned',3,1,1,1,
         '{"Age": "1", "Zone": "North Bay Sanctuary Zone", "area": "0", "No_take": "1", "Isolation": 1.0, "Zone_name": "North Bay Sanctuary Zone", "NEOLI_Total": "3", "ProxCountry": "Australia", "area_in_km2": "0.5", "Effectiveness": "High", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "0", "year_of_protection": "2004", "Effectiveness_NEOLI": "1", "Shore_fishing_index": 1.0, "Distance_to_boat_ramp": 1.2, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 0.5, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 3.1, "Distance_to_boundary_in_km": 0.2, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}',
         true);

INSERT INTO nrmn.observable_item_ref (
        observable_item_id,observable_item_name,obs_item_type_id,aphia_id,aphia_rel_type_id,common_name,superseded_by,
        phylum,class,"order",family,genus,species_epithet,report_group,habitat_groups,letter_code,is_invert_sized,
        obs_item_attribute,created,updated,mapped_id)
    VALUES
            (35,'Caesioperca rasor',1,280012,1,'Barber perch',null,'Chordata','Actinopterygii','Perciformes','Serranidae','Caesioperca','rasor',null,null,'CRA',false,'{"MaxLength": 25.0, "OtherGroups": "Planktivore"}',null,null,25),
            (44,'Upeneichthys vlamingii',1,283083,1,'Southern goatfish',null,'Chordata','Actinopterygii','Perciformes','Mullidae','Upeneichthys','vlamingii',null,null,'UVL',false,'{"MaxLength": 35.0, "OtherGroups": "Benthic lower carnivore"}',null,null,34),
            (45,'Pempheris multiradiata',1,277062,1,'Common bullseye',null,'Chordata','Actinopterygii','Perciformes','Pempheridae','Pempheris','multiradiata',null,null,'PMU',false,'{"MaxLength": 28.0, "OtherGroups": "Planktivore"}',null,null,35),
            (50,'Scorpis aequipinnis',1,282728,1,'Sea sweep',null,'Chordata','Actinopterygii','Perciformes','Kyphosidae','Scorpis','aequipinnis',null,null,'SAE',false,'{"MaxLength": 40.0, "OtherGroups": "Planktivore"}',null,null,40),
            (52,'Atypichthys strigatus',1,279827,1,'Mado sweep',null,'Chordata','Actinopterygii','Perciformes','Kyphosidae','Atypichthys','strigatus',null,null,'ASTR',false,'{"MaxLength": 25.0, "OtherGroups": "Planktivore,REDMAP"}',null,null,42),
            (53,'Enoplosus armatus',1,280685,1,'Old wife',null,'Chordata','Actinopterygii','Perciformes','Enoplosidae','Enoplosus','armatus',null,null,'EAR',false,'{"MaxLength": 50.0, "OtherGroups": "Benthic lower carnivore,REDMAP"}',null,null,43),
            (56,'Parma microlepis',1,282156,1,'White-ear',null,'Chordata','Actinopterygii','Perciformes','Pomacentridae','Parma','microlepis',null,null,'PMI',false,'{"MaxLength": 14.0, "OtherGroups": "Herbivore,REDMAP"}',null,null,46),
            (58,'Aplodactylus arctidens',1,279656,1,'Marblefish',null,'Chordata','Actinopterygii','Perciformes','Aplodactylidae','Aplodactylus','arctidens',null,null,'AAR',false,'{"MaxLength": 65.0, "OtherGroups": "Benthic,Herbivore"}',null,null,48),
            (59,'Cheilodactylus nigripes',1,278154,1,'Magpie perch','Pseudogoniistius nigripes','Chordata','Actinopterygii','Perciformes','Cheilodactylidae','Cheilodactylus','nigripes',null,null,'CNI',false,'{"MaxLength": 41.0, "OtherGroups": "Benthic lower carnivore"}',null,null,49),
            (61,'Dactylophora nigricans',1,280476,1,'Dusky morwong',null,'Chordata','Actinopterygii','Perciformes','Cheilodactylidae','Dactylophora','nigricans',null,null,'DNIG',false,'{"MaxLength": 120.0, "OtherGroups": "Herbivore,REDMAP"}',null,null,51),
            (66,'Dotalabrus aurantiacus',1,280578,1,'Castelnaus wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Dotalabrus','aurantiacus',null,null,'DAU',false,'{"MaxLength": 11.1, "OtherGroups": "Benthic lower carnivore"}',null,null,56),
            (67,'Pictilabrus laticlavius',1,282258,1,'Senator wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Pictilabrus','laticlavius',null,null,'PLA',false,'{"MaxLength": 23.0, "OtherGroups": "Benthic lower carnivore"}',null,null,57),
            (68,'Notolabrus fucicola',1,281788,1,'Purple wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Notolabrus','fucicola',null,null,'NFU',false,'{"MaxLength": 38.0, "OtherGroups": "Benthic lower carnivore"}',null,null,58),
            (70,'Notolabrus tetricus',1,281792,1,'Blue-throat wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Notolabrus','tetricus',null,null,'NTE',false,'{"MaxLength": 50.0, "OtherGroups": "Benthic lower carnivore"}',null,null,60),
            (102,'Acanthaluteres vittiger',1,279464,1,'Toothbrush leatherjacket',null,'Chordata','Actinopterygii','Tetraodontiformes','Monacanthidae','Acanthaluteres','vittiger',null,null,'AVI',false,'{"MaxLength": 35.0, "OtherGroups": "Benthic lower carnivore"}',null,null,93),
            (175,'Notolabrus gymnogenis',1,281789,1,'Crimson-banded wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Notolabrus','gymnogenis',null,null,'NGY',false,'{"MaxLength": 49.0, "OtherGroups": "Benthic lower carnivore,REDMAP"}',null,null,171),
            (205,'Heliocidaris erythrogramma',1,513313,1,'Short-spined urchin',null,'Echinodermata','Echinoidea','Echinoida','Echinometridae','Heliocidaris','erythrogramma',null, null, 'HER',true,'{"MaxLength": 9.0}', null, null, 202),
            (206,'Centrostephanus rodgersii',1,414212,1,'Long-spine urchin',null,'Echinodermata','Echinoidea','Diadematoida','Diadematidae','Centrostephanus','rodgersii',null,null,'CRO',true,'{"MaxLength": 10.0, "OtherGroups": "REDMAP"}',null,null,203),
            (248,'Charonia lampas rubicunda',1,141101,2,'Triton shell','Charonia lampas','Mollusca','Gastropoda','Littorinimorpha','Ranellidae','Charonia','lampas rubicunda',null,null,'CLR',true,'{}',null,null,246),
            (252,'Dicathais orbita',1,396664,1,'Dog whelk',null,'Mollusca','Gastropoda','Neogastropoda','Muricidae','Dicathais','orbita',null, null, 'DOR',true,'{"MaxLength": 8.0}', null, null, 250),
            (273,'Plagusia chabrus',1,246189,1,'Red bait crab',null,'Arthropoda','Malacostraca','Decapoda','Plagusiidae','Plagusia','chabrus',null, null, 'PCHAB', true,'{"MaxLength": 7.0}', null, null, 272),
            (1455,'Unidentified sponge (encrusting)',1,558,2,'Encrusting sponge',null,'Porifera',null,null,null,null,null,null,'Encrusting','USEN',false,'{}',null,null,2116),
            (1456,'Unidentified sponges',1,558,2,'Sponges',null,'Porifera',null,null,null,null,null,'Sponges',null,'USP',false,'{}',null,null,2117),
            (1469,'Unidentified bryozoans (soft)',1,146142,2,'Soft bryozoans',null,'Bryozoa',null,null,null,null,null,'Soft bryozoa',null,'UBRS',false,'{}',null,null,2131),
            (1300,'Comanthus trichoptera',1,393495,1,'Orange feather star','Cenolia trichoptera','Echinodermata','Crinoidea','Comatulida','Comasteridae','Comanthus','trichoptera',null,null,'CTRIC',true,'{"MaxLength": 46.0}',null,null,1957),
            (1354,'Ophthalmolepis lineolatus',1,402371,1,'Maori wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Ophthalmolepis','lineolatus',null,null,'OLI',false,'{"MaxLength": 41.0, "OtherGroups": "Benthic lower carnivore,REDMAP"}',null,null,2011),
            (1528,'Olisthops cyanomelas',1,319734,1,'Herring cale',null,'Chordata','Actinopterygii','Perciformes','Odacidae','Olisthops','cyanomelas',null,null,'OCY',false,'{"MaxLength": 51.0, "OtherGroups": "Herbivore,REDMAP"}',null,null,2271),
            (1555,'Herdmania grandis',1,250691,1,'Red-throat ascidian',null,'Chordata','Ascidiacea','Pleurogona','Pyuridae','Herdmania','grandis','Ascidians','Sessile invertebrate','HGR',true,'{}',null,null,2303),
            (7163,'Acrocarpia paniculata',3,374837,1,'Bushy tangleweed',null,'Heterokontophyta','Phaeophyceae','Fucales','Sargassaceae','Acrocarpia','paniculata',null,'Canopy','APAN',false,'{}',null,null,300),
            (7172,'Cystophora monilifera',3,239171,1,'Three-branched cystophora',null,'Heterokontophyta','Phaeophyceae','Fucales','Sargassaceae','Cystophora','monilifera',null,'Canopy','CMO',false,'{}',null,null,309),
            (7173,'Cystophora moniliformis',3,239172,1,'Zigzag cystophora',null,'Heterokontophyta','Phaeophyceae','Fucales','Sargassaceae','Cystophora','moniliformis',null,'Canopy','CMON',false,'{}',null,null,310),
            (7185,'Ecklonia radiata',3,214344,1,'Common kelp',null,'Heterokontophyta','Phaeophyceae','Laminariales','Lessoniaceae','Ecklonia','radiata',null,'Canopy','ERA',false,'{}',null,null,322),
            (7196,'Phyllospora comosa',3,374788,1,'Crayweed',null,'Heterokontophyta','Phaeophyceae','Fucales','Seirococcaceae','Phyllospora','comosa',null,'Canopy','PCOM',false,'{}',null,null,333),
            (7199,'Sargassum verruculosum',3,494928,1,'Common sargassum',null,'Heterokontophyta','Phaeophyceae','Fucales','Sargassaceae','Sargassum','verruculosum',null,'Canopy','SVERR',false,'{}',null,null,336),
            (7203,'Xiphophora chondrophylla',3,373472,1,'Branched daggerweed',null,'Heterokontophyta','Phaeophyceae','Fucales','Fucaceae','Xiphophora','chondrophylla',null,'Understorey','XCH',false,'{}',null,null,340),
            (7247,'Plocamium angustum',3,371408,1,'Common plocamium',null,'Rhodophyta','Florideophyceae','Plocamiales','Plocamiaceae','Plocamium','angustum',null,'Understorey','PANGU',false,'{}',null,null,384),
            (7258,'Phacelocarpus alatus',3,239346,1,null,null,'Rhodophyta','Florideophyceae','Gigartinales','Phacelocarpaceae','Phacelocarpus','alatus','Phacelocarpus/Delisea spp.','Understorey','PALA',false,'{}',null,null,395),
            (7259,'Pterocladia capillacea',3,182734,1,'Pinnate agarweed','Pterocladiella capillacea','Rhodophyta','Florideophyceae','Gelidiales','Pterocladiaceae','Pterocladia','capillacea',null,'Understorey','PCAPI',false,'{}',null,null,396),
            (7346,'Gelidium australe',3,239268,1,'Southern agarweed',null,'Rhodophyta','Florideophyceae','Nemaliales','Gelidiaceae','Gelidium','australe','Gelidium spp.','Understorey','GAUS',false,'{}',null,null,515),
            (7533,'Pachydictyon paniculatum',3,499919,1,'Stringy forkweed',null,'Heterokontophyta','Phaeophyceae','Dictyotales','Dictyotaceae','Pachydictyon','paniculatum',null,'Understorey','PPAN',false,'{}',null,null,862),
            (7578,'Halopteris paniculata',3,496299,1,null,null,'Heterokontophyta','Phaeophyceae','Sphacelariales','Stypocaulaceae','Halopteris','paniculata',null,'Understorey','HPA',false,'{}',null,null,1055),
            (7721,'Unidentified algae (turf)',3,null,null,'Turf',null,'Algae',null,null,null,null,null,'Turf algae','Encrusting','UALT',false,'{}',null,null,2146),
            (7727,'Zonaria turneriana/angustata',3,144093,2,null,null,'Heterokontophyta','Phaeophyceae','Dictyotales','Dictyotaceae','Zonaria',null,'Zonaria turneriana/angustata','Understorey','ZTA',false,'{}',null,null,2152),
            (7731,'Unidentified algae (encrusting brown)',3,830,2,'Encrusting brown algae',null,'Heterokontophyta','Phaeophyceae',null,null,null,null,'Brown algae (encrusting)','Encrusting','UALEB',false,'{}',null,null,2156),
            (7769,'Unidentified algae (crustose coralline)',3,15308,2,'Crustose coralline algae',null,'Rhodophyta','Florideophyceae','Corallinales',null,null,null,null,'Encrusting','UALCC',false,'{}',null,null,2194),
            (7785,'Peyssonnelia spp. (encrusting)',3,144051,2,'Peyssonnelia spp. (encrusting)',null,'Rhodophyta','Florideophyceae','Peyssonneliales','Peyssonneliaceae','Peyssonnelia',null,'Peyssonnelia spp.','Encrusting','PEYE',false,'{}',null,null,2219),
            (7792,'Phacelocarpus peperocarpos',3,213963,1,'Serrated red seaweed',null,'Rhodophyta','Florideophyceae','Gigartinales','Phacelocarpaceae','Phacelocarpus','peperocarpos','Phacelocarpus/Delisea spp.','Understorey','PPEP',false,'{}',null,null,2256),
            (7933,'Sand',4,null,null,'Sand',null,'Substrate',null,null,null,'Sand',null,null,'Substrate','SAND',false,'{}',null,null,461),
            (7935,'Bare rock',4,null,null,'Bare rock',null,'Substrate',null,null,null,'Bare rock (non - barrens)',null,null,'Substrate','BRN',false,'{}',null,null,633),
            (7795,'Jania micrarthrodia',3,145128,1,'Ball coralline',null,'Rhodophyta','Florideophyceae','Corallinales','Corallinaceae','Jania','micrarthrodia',null,null,'JMI',false,'{}',null,null,2269);

INSERT INTO nrmn.survey (survey_id,site_id,program_id,survey_date,survey_time,depth,survey_num,
    visibility,direction,longitude,latitude,protection_status,inside_marine_park,notes,pq_catalogued,pq_zip_url,pq_diver_id,
    block_abundance_simulated,project_title,created,updated,locked)
VALUES (912351270,3834,1,'2008-02-27',NULL,10,0,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False),
    (912351271,3844,1,'2008-02-27',NULL,1,3,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False),
    (912351272,3844,1,'2008-02-27',NULL,1,5,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False),
    (812331754,914,2,'2018-06-03',null,5,4,15,NULL,NULL,NULL,NULL,'Unsure',NULL,false,'None',NULL,true,NULL,NULL,NULL,false);

INSERT INTO nrmn.survey_method(survey_method_id,survey_id,method_id,block_num,survey_not_done,survey_method_attribute)
    VALUES (85720,912351270,2,2,false,'{}'),
        (85721,912351270,1,1,false,'{}'),
        (85722,912351270,1,2,false,'{}'),
        (85723,912351270,2,1,false,'{}'),
        (12485,912351271,2,1,true,'{}'),
        (12486,912351271,1,1,false,'{}'),
        (12487,912351271,1,2,false,'{}'),
        (12488,912351271,2,2,true,'{}'),
        (56987,912351272,2,2,true,'{}'),
        (56988,912351272,1,1,false,'{}'),
        (56989,912351272,2,1,true,'{}'),
        (56990,912351272,1,2,false,'{}'),
        (3733,812331754,2,1,false,'{}'),
        (3734,812331754,1,1,false,'{}'),
        (3735,812331754,3,null,false,'{}'),
        (3736,812331754,5,null,false,'{}'),
        (3737,812331754,1,2,false,'{}');

INSERT INTO nrmn.observation (observation_id,survey_method_id,diver_id,observable_item_id,measure_id,measure_value,observation_attribute)
VALUES (428107,3733,21,205,64,1,'{"SizeRaw": 60.0, "SizeEstimated": "Yes"}'),
        (431828,3733,21,252,66,1,'{"SizeRaw": 70.0, "SizeEstimated": "No"}'),
        (433024,3733,21,273,67,1,'{"SizeRaw": 75.0, "SizeEstimated": "Yes"}'),
        (435253,3733,21,206,68,1,'{"SizeRaw": 80.0, "SizeEstimated": "Yes"}'),
        (435254,3733,21,205,68,5,'{"SizeRaw": 80.0, "SizeEstimated": "Yes"}'),
        (438106,3733,21,206,70,8,'{"SizeRaw": 90.0, "SizeEstimated": "Yes"}'),
        (438870,3733,21,1300,5,2,'{"SizeRaw": 100.0, "SizeEstimated": "Yes"}'),
        (441090,3733,21,206,72,4,'{"SizeRaw": 100.0, "SizeEstimated": "Yes"}'),
        (463011,3733,21,248,86,1,'{"SizeRaw": 195.0, "SizeEstimated": "No"}'),
        (7314,3734,180,50,6,1,'{}'),
        (7315,3734,180,44,6,1,'{}'),
        (7316,3734,180,45,6,3,'{}'),
        (7317,3734,180,102,6,2,'{}'),
        (7318,3734,180,66,6,1,'{}'),
        (55302,3734,180,45,4,3,'{}'),
        (95815,3734,180,175,5,1,'{}'),
        (142644,3734,180,50,7,1,'{}'),
        (142645,3734,180,67,7,1,'{}'),
        (142646,3734,180,70,7,5,'{}'),
        (142647,3734,180,102,7,3,'{}'),
        (187307,3734,180,59,10,2,'{}'),
        (187308,3734,180,70,10,1,'{"SpeciesSex": "Male"}'),
        (223698,3734,180,59,8,2,'{}'),
        (223699,3734,180,68,8,4,'{}'),
        (223700,3734,180,70,8,8,'{}'),
        (223701,3734,180,53,8,2,'{}'),
        (223702,3734,180,35,8,1,'{}'),
        (223703,3734,180,1528,8,2,'{}'),
        (223704,3734,180,1354,8,1,'{}'),
        (265865,3734,180,52,3,20,'{}'),
        (265866,3734,180,45,3,43,'{}'),
        (294007,3734,180,70,9,3,'{}'),
        (349970,3734,180,58,12,1,'{}'),
        (356266,3734,180,61,13,1,'{}'),
        (479941,3735,180,7185,42,12,'{}'),
        (479942,3735,180,1555,42,2,'{}'),
        (479943,3735,180,7203,42,6,'{}'),
        (479944,3735,180,7258,42,3,'{}'),
        (479945,3735,180,7259,42,12,'{}'),
        (479946,3735,180,7933,42,10,'{}'),
        (479947,3735,180,7769,42,14,'{}'),
        (479948,3735,180,1469,42,6,'{}'),
        (479949,3735,180,1455,42,5,'{}'),
        (479950,3735,180,1456,42,1,'{}'),
        (479951,3735,180,7785,42,12,'{}'),
        (479952,3735,180,7721,42,2,'{}'),
        (479953,3735,180,7795,42,1,'{}'),
        (479954,3735,180,7792,42,3,'{}'),
        (558349,3735,180,7172,44,10,'{}'),
        (558350,3735,180,7185,44,14,'{}'),
        (558351,3735,180,1555,44,3,'{}'),
        (558352,3735,180,7196,44,16,'{}'),
        (558353,3735,180,7933,44,8,'{}'),
        (558354,3735,180,7346,44,6,'{}'),
        (558355,3735,180,7935,44,5,'{}'),
        (558356,3735,180,7769,44,22,'{}'),
        (558357,3735,180,1455,44,2,'{}'),
        (558358,3735,180,7785,44,9,'{}'),
        (558359,3735,180,7721,44,2,'{}'),
        (558360,3735,180,7792,44,4,'{}'),
        (635422,3735,180,7172,45,12,'{}'),
        (635423,3735,180,7578,45,8,'{}'),
        (635424,3735,180,7173,45,11,'{}'),
        (635425,3735,180,7199,45,5,'{}'),
        (635426,3735,180,7259,45,18,'{}'),
        (635427,3735,180,7533,45,4,'{}'),
        (635428,3735,180,1455,45,1,'{}'),
        (635429,3735,180,7785,45,2,'{}'),
        (635430,3735,180,7727,45,4,'{}'),
        (635431,3735,180,7721,45,32,'{}'),
        (712223,3735,180,7172,46,16,'{}'),
        (712224,3735,180,7185,46,10,'{}'),
        (712225,3735,180,7578,46,3,'{}'),
        (712226,3735,180,7196,46,5,'{}'),
        (712227,3735,180,7247,46,3,'{}'),
        (712228,3735,180,7933,46,30,'{}'),
        (712229,3735,180,7721,46,16,'{}'),
        (712230,3735,180,7792,46,6,'{}'),
        (789258,3735,180,7172,43,18,'{}'),
        (789259,3735,180,7185,43,20,'{}'),
        (789260,3735,180,7163,43,2,'{}'),
        (789261,3735,180,7173,43,3,'{}'),
        (789262,3735,180,7346,43,4,'{}'),
        (789263,3735,180,7935,43,2,'{}'),
        (789264,3735,180,7769,43,30,'{}'),
        (789265,3735,180,7785,43,1,'{}'),
        (789266,3735,180,7721,43,2,'{}'),
        (789267,3735,180,7731,43,10,'{}'),
        (7319,3737,180,50,6,1,'{}'),
        (7320,3737,180,70,6,3,'{}'),
        (7321,3737,180,56,6,1,'{}'),
        (7322,3737,180,45,6,5,'{}'),
        (7323,3737,180,102,6,1,'{}'),
        (7324,3737,180,35,6,1,'{}'),
        (55303,3737,180,45,4,7,'{}'),
        (142648,3737,180,50,7,3,'{}'),
        (142649,3737,180,59,7,1,'{}'),
        (142650,3737,180,68,7,1,'{}'),
        (142651,3737,180,1528,7,1,'{}'),
        (142652,3737,180,1354,7,5,'{}'),
        (187309,3737,180,59,10,1,'{}'),
        (187310,3737,180,1528,10,1,'{"SpeciesSex": "Male"}'),
        (223705,3737,180,68,8,1,'{}'),
        (223706,3737,180,70,8,2,'{}'),
        (223707,3737,180,175,8,2,'{}'),
        (223708,3737,180,44,8,1,'{}'),
        (223709,3737,180,53,8,1,'{}'),
        (223710,3737,180,35,8,1,'{}'),
        (265867,3737,180,45,3,2,'{}'),
        (294008,3737,180,50,9,1,'{}'),
        (294009,3737,180,59,9,2,'{}'),
        (294010,3737,180,68,9,1,'{}'),
        (294011,3737,180,70,9,1,'{}'),
        (294012,3737,180,70,9,1,'{"SpeciesSex": "Male"}'),
        (335672,3737,180,1528,11,1,'{"SpeciesSex": "Male"}'),
        (349971,3737,180,58,12,1,'{}');
