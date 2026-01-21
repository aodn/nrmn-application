INSERT INTO nrmn.location_ref(location_id, location_name, is_active)
VALUES (29, 'SA - Western', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (0, 'NONE', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (55, 'RLS', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (56, 'ATRC', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (57, 'RRH', True);

INSERT INTO nrmn.location_ref (location_id, location_name, is_active)
    VALUES (0, 'NONE', true),
           (184, 'Lord Howe Island', true);

INSERT INTO nrmn.site_ref (site_id, site_name, site_code, location_id, is_active) VALUES (0, 'NONE', 'NONE', 0, true);

INSERT INTO nrmn.site_ref(site_id, site_code, site_name, longitude, latitude, location_id, state, country,
                          old_site_code, mpa, protection_status, site_attribute, is_active)
VALUES (551, 'EYR71', 'South East Slade Point', 154, -35, 29, 'South Australia', 'Australia', '{"4117"}',
        'West Coast Bays MP', 'Restricted take multizoned',
        '{
          "Zone": "Habitat Protection Zone 1",
          "ProxCountry": "Australia",
          "Effectiveness2": "High",
          "year_of_protection": "2012"
        }',
        True);

INSERT INTO nrmn.site_ref(site_id, site_code, site_name, longitude, latitude, location_id, state, country,
                          protection_status, site_attribute, is_active)
VALUES (1783, 'CEU4', 'Castillo', -5, 35, 29, 'Ceuta', 'Spain', 'Fishing',
        '{
          "ProxCountry": "Spain"
        }', true);

insert into nrmn.site_ref (site_id, site_code, site_name, longitude, latitude, geom, location_id, state, country, old_site_code, mpa, protection_status, relief, currents, wave_exposure, slope, site_attribute, is_active)
values  (3807, 'LHI52', 'Sunken Rock', 159.2853, -31.81209, '0101000020E61000009031772D21E963409D685721E5CF3FC0', 184, 'New South Wales', 'Australia', null, 'Lord Howe Island Marine Park', 'No take multizoned', 2, 4, 4, 3, '{"Age": "1", "Zone": "Sanctuary Zone (Balls Pyramid)", "area": "0", "No_take": "1", "Isolation": 5.0, "Zone_name": "Sanctuary Zone (Balls Pyramid)", "NEOLI_Total": "3", "ProxCountry": "Australia", "area_in_km2": "53", "Effectiveness": "High", "Isolation_NEOLI": "0", "year_of_protection": "2004", "Effectiveness_NEOLI": "1", "Rec_methods_permitted": "-", "com_methods_permitted": "-", "Is_rec_fishing_allowed": "N", "Distance_to_boundary_in_km": 2.8, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (3808, 'LHI43', 'South East Rock', 159.28145, -31.7875, '0101000020E6100000B22E6EA301E963409A99999999C93FC0', 184, 'New South Wales', 'Australia', null, 'Lord Howe Island Marine Park', 'No take multizoned', 3, 3, 4, 4, '{"Age": "1", "Zone": "Balls Pyramid Sanctuary Zone", "area": "0", "No_take": "1", "Isolation": 5.0, "Zone_name": "Balls Pyramid Sanctuary Zone", "NEOLI_Total": "3", "ProxCountry": "Australia", "area_in_km2": "33", "Effectiveness": "High", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "0", "year_of_protection": "2004", "Effectiveness_NEOLI": "1", "Distance_to_boat_ramp": 38.7, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 6.0, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 24.0, "Distance_to_boundary_in_km": 1.0, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300132, '2006-12-04', '00:00:00', 5, 2, 7, 'SW',
        'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300134, '2006-12-04', '00:00:00', 10, 2, 7, 'SW',
        'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300133, '2006-12-04', '00:00:01', 15, 3, 7, 'SW',
        'Almost identical -> Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300130, '2006-12-04', '00:00:00', 15, 3, 7, 'SW',
    'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
    'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300131, '2006-12-04', '00:00:00', 20, 1, 7, 'E',
        'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812331345, '2017-11-28', '00:00:00', 25, 1, 10, 'N', null, null, 'Unsure', true);
/*
 This survey is locked for correction
 */
INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated, locked)
VALUES (56, 551, 812331346, '2017-11-28', '00:00:00', 25, 1, 10, 'N',
        'Survey locked for edit', 'Not able to correct this', 'Unsure', true, true);

insert into nrmn.survey (survey_id, site_id, program_id, survey_date, survey_time, depth, survey_num, visibility, direction, longitude, latitude, protection_status, inside_marine_park, notes, pq_catalogued, pq_zip_url, pq_diver_id, block_abundance_simulated, project_title, created, updated, locked)
values  (912350289, 3807, 55, '2018-03-04', '10:00:00', 10, 0, 20, 'W', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912350289/zip', null, false, null, null, null, false),
        (912350275, 3807, 55, '2018-03-04', '10:00:00', 15, 0, 30, 'O', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912350275/zip', null, false, null, null, null, false),
        (2001744, 3808, 55, '2010-02-11', null, 12, 0, null, null, null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/2001744/zip', null, false, null, null, null, false),
        (912350272, 3808, 55, '2018-03-04', '11:00:00', 8, 0, 30, 'O', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912350272/zip', null, false, null, null, null, false),
        (912340551, 3808, 55, '2012-03-01', '10:00:00', 7, 0, 40, 'NE', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912340551/zip', null, false, null, null, null, false),
        (2001745, 3808, 55, '2010-02-11', null, 14, 0, null, null, null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/2001745/zip', null, false, null, null, null, false),
        (912340553, 3808, 55, '2012-03-01', '10:00:00', 14, 0, 40, 'NW', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912340553/zip', null, false, null, null, null, false),
        (912340552, 3808, 55, '2012-03-01', '10:00:00', 12, 0, 40, 'S', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912340552/zip', null, false, null, null, null, false),
        (912350271, 3808, 55, '2018-03-04', '11:00:00', 14, 0, 25, 'E', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912350271/zip', null, false, null, null, null, false);
/*
 This survey have pq_catalogued true
 */
INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated, pq_catalogued)
VALUES (55, 551, 812331347, '2017-11-29', '00:00:00', 25, 1, 10, 'N',
        'Survey locked for edit', 'Not able to correct this', 'Unsure', true, true);

INSERT INTO nrmn.diver_ref(diver_id, initials, full_name, created)
    VALUES (51, 'JEP', 'Juan Espanol Pagina', null),
           (70, 'AZS', 'Alex Zum Smith', null),
           (80, 'EVP', 'Eve valerie Piotr', null),
           (419, 'TPC', 'Tim Crawford', null),
           (373, 'SAG', 'Sallyann Gudge', null),
           (366, 'RSS', 'Rick Stuart-Smith', null),
           (208, 'IVS', 'Ian Shaw', null),
           (421, 'TRD', 'Tom Davis', null),
           (188, 'GJE', 'Graham Edgar', null),
           (425, 'WCB', 'Bill Barker', null),
           (148, 'CTH', 'Christo Haseldon', null),
           (115, 'ATC', 'Antonia Cooper', null),
           (97, 'AJG', 'Andrew Green', null),
           (187, 'GER', 'Germán Soler', null),
           (378, 'SDL', 'Scott Ling', null);
/*
   id bigint NOT NULL,
    created timestamp with time zone,
    program_id integer,
    last_updated timestamp with time zone,
 */

INSERT INTO nrmn.staged_job (id,
                             created,
                             last_updated,
                             reference,
                             source,
                             status,
                             program_id,
                             sec_user_id,
                             is_extended_size)
VALUES (109, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'jobid-rls', 'INGEST', 'STAGED', 55, 123456, false),
       (110, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'jobid-als', 'INGEST', 'STAGED', 55, 123456, false);

INSERT INTO nrmn.staged_job (id,
                             created,
                             last_updated,
                             reference,
                             source,
                             status,
                             program_id,
                             sec_user_id,
                             is_extended_size)

VALUES (119, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP , 'jobid-atrc', 'INGEST', 'STAGED', 56, 123456, false);

INSERT INTO nrmn.staged_job (id,
                             created,
                             last_updated,
                             reference,
                             source,
                             status,
                             program_id,
                             sec_user_id,
                             is_extended_size)

VALUES (120, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP , 'jobid-unvalidated', 'INGEST', 'PENDING', 56, 123456, false);

INSERT INTO nrmn.aphia_ref (aphia_id,
                            valid_name,
                            is_extinct)
VALUES (102, 'Species 56', false),(103, 'Species 57', false);

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

INSERT INTO nrmn.observable_item_ref (observable_item_id, obs_item_type_id, aphia_id, aphia_rel_type_id,
                                      observable_item_name, letter_code, superseded_by)
VALUES (333, 1, 102, 1, 'Species 56', 'S56', 'Debris'),
       (331, 1, 103, 1, 'Species 57', 'S57', null),
       (332, 5, null, null, 'Debris', 'deb', 'Duplicate rubra'),
       (334, 1, null, null, 'Haliotis rubra', 'hal', 'Debris'),
       (330, 1, null, null, 'Duplicate rubra', 'dup', null);


INSERT INTO nrmn.method_ref(method_id, method_name, is_active)
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

INSERT INTO nrmn.methods_species
VALUES (331, 1), (333, 1), (331, 2), (333, 2), (331, 3), (333, 3), (332, 12), (334, 11);

INSERT INTO nrmn.survey_method(survey_method_id, block_num,
                               survey_method_attribute, survey_not_done,
                               method_id, survey_id)
                            VALUES (121,1,'{}',false,1,812300133),
                                   (221,2,'{}',false,1,812300133),
                                   (321,1,'{}',false,2,812300133),
                                   (421,2,'{}',false,2,812300131);

INSERT INTO nrmn.measure_type_ref(measure_type_id, measure_type_name, is_active)
VALUES (1,'Fish Size Class',true),
(2,'In Situ Quadrat',true),
(3,'Macrocystis Block',true),
(4,'Invert Size Class',true),
(5,'Single Item',true),
(6,'Absence',true),
(7,'Limpet Quadrat',true);

INSERT INTO nrmn.measure_ref(measure_id, measure_type_id, measure_name, seq_no, is_active)
VALUES (1,1,'Unsized',0,true),
(2,1,'2.5cm',1,true),
(3,1,'5cm',2,true),
(4,1,'7.5cm',3,true),
(5,1,'10cm',4,true),
(6,1,'12.5cm',5,true),
(7,1,'15cm',6,true),
(8,1,'20cm',7,true),
(9,1,'25cm',8,true),
(10,1,'30cm',9,true),
(11,1,'35cm',10,true),
(12,1,'40cm',11,true),
(13,1,'50cm',12,true),
(14,1,'62.5cm',13,true),
(15,1,'75cm',14,true),
(16,1,'87.5cm',15,true),
(17,1,'100cm',16,true),
(18,1,'112.5cm',17,true),
(19,1,'125cm',18,true),
(20,1,'137.5cm',19,true),
(21,1,'150cm',20,true),
(22,1,'162.5cm',21,true),
(23,1,'175cm',22,true),
(24,1,'187.5cm',23,true),
(25,1,'200cm',24,true),
(26,1,'250cm',25,true),
(27,1,'300cm',26,true),
(28,1,'350cm',27,true),
(29,1,'400cm',28,true),
(30,1,'450cm',29,true),
(31,1,'500cm',30,true),
(32,1,'550cm',31,true),
(33,1,'600cm',32,true),
(34,1,'650cm',33,true),
(35,1,'700cm',34,true),
(36,1,'750cm',35,true),
(37,1,'800cm',36,true),
(38,1,'850cm',37,true),
(39,1,'900cm',38,true),
(40,1,'950cm',39,true),
(41,1,'1000cm',40,true),
(42,2,'Q1',1,true),
(43,2,'Q2',2,true),
(44,2,'Q3',3,true),
(45,2,'Q4',4,true),
(46,2,'Q5',5,true),
(47,3,'B1',1,true),
(48,3,'B2',2,true),
(49,3,'B3',3,true),
(50,3,'B4',4,true),
(51,3,'B5',5,true),
(52,4,'Unsized',0,true),
(53,4,'0.5cm',1,true),
(54,4,'1cm',2,true),
(55,4,'1.5cm',3,true),
(56,4,'2cm',4,true),
(57,4,'2.5cm',5,true),
(58,4,'3cm',6,true),
(59,4,'3.5cm',7,true),
(60,4,'4cm',8,true),
(61,4,'4.5cm',9,true),
(62,4,'5cm',10,true),
(63,4,'5.5cm',11,true),
(64,4,'6cm',12,true),
(65,4,'6.5cm',13,true),
(66,4,'7cm',14,true),
(67,4,'7.5cm',15,true),
(68,4,'8cm',16,true),
(69,4,'8.5cm',17,true),
(70,4,'9cm',18,true),
(71,4,'9.5cm',19,true),
(72,4,'10cm',20,true),
(73,4,'10.5cm',21,true),
(74,4,'11cm',22,true),
(75,4,'11.5cm',23,true),
(76,4,'12cm',24,true),
(77,4,'12.5cm',25,true),
(78,4,'13cm',26,true),
(79,4,'13.5cm',27,true),
(80,4,'14cm',28,true),
(81,4,'14.5cm',29,true),
(82,4,'15cm',30,true),
(83,4,'16cm',31,true),
(84,4,'17cm',32,true),
(85,4,'18cm',33,true),
(86,4,'19cm',34,true),
(87,4,'20cm',35,true),
(88,4,'22cm',36,true),
(89,4,'24cm',37,true),
(90,4,'26cm',38,true),
(91,4,'28cm',39,true),
(92,4,'30cm',40,true),
(93,5,'Item',0,true),
(94,6,'No specimen found',0,true),
(95,7,'Q1',1,true),
(96,7,'Q2',2,true),
(97,7,'Q3',3,true),
(98,7,'Q4',4,true),
(99,7,'Q5',5,true);

INSERT INTO nrmn.observation(observation_id, measure_value,
                             observation_attribute, diver_id, measure_id,
                             observable_item_id, survey_method_id)
VALUES (551, 1, '{}',51, 2, 333, 121),
       (552, 500, '{}',51, 7, 333, 221),
       (553, 600, '{}',51, 7, 331, 421),
       (554, 40, '{}',51, 2, 331, 321),
       (651, 1, '{}',51, 2, 330, 121),
       (652, 500, '{}',51, 7, 330, 221),
       (653, 600, '{}',51, 3, 332, 421),
       (654, 40, '{}',51, 2, 332, 321);

INSERT INTO nrmn.staged_row(pqs, block, buddy, code, created, date, depth, direction, diver,
                            inverts, is_invert_sizing, last_updated, latitude, longitude,
                            measure_value, method, site_name, site_no, species, time, total, vis, staged_job_id)
VALUES ('JEP', 1, 'AZS', 'AAA', '09/09/2009', '03/03/2003', 3.3, 'NW', 'JEP', 0, false, '01/01/2001', -5, 35, '{"1": "5"}', 1, 'Castillo', 'CEU4', 'Species 56', '11:11', 5, 11, 109),
       ('JEP', 1, 'AZS', 'ZZZ', '09/09/2009', '03/03/2003', 3.3, 'NW', 'JEP', 0, false, '01/01/2001', -5, 35,'{"1": "5"}', 1, 'Castillo1', 'CEU41', 'Species 56', '11:11', 5, 11, 110);

INSERT INTO nrmn.legacy_common_names (rank, name, common_name)
VALUES ('Phylum','Annelida','Segmented worms'),
       ('Phylum','Arthropoda','Arthropods'),
       ('Phylum','Chordata','Chordates'),
       ('Phylum','Ctenophora','Comb jellies'),
       ('Phylum','Echinodermata','Echinoderms'),
       ('Phylum','Mollusca','Molluscs'),
       ('Phylum','Platyhelminthes','Flat worms'),
       ('Family','Berycidae','alfonsinos'),
       ('Family','Urotrygonidae','American round stingrays'),
       ('Family','Engraulidae','anchovies'),
       ('Family','Squatinidae','angel sharks'),
       ('Family','Pomacanthidae','Angelfishes'),
       ('Family','Antennariidae','Anglerfishes'),
       ('Family','Toxotidae','Archerfishes'),
       ('Family','Pentacerotidae','armorheads'),
       ('Family','Ambassidae','Asiatic glassfishes'),
       ('Family','Pataecidae','Australian prowfishes'),
       ('Family','Arripidae','Australian salmon (Kahawai)'),
       ('Family','Sphyraenidae','Barracudas'),
       ('Family','Latidae','Barramundi'),
       ('Family','Grammatidae','basslets'),
       ('Family','Ephippidae','Batfishes'),
       ('Family','Ogcocephalidae','batfishes'),
       ('Family','Priacanthidae','Big-eyes'),
       ('Family','Blenniidae','Blennies'),
       ('Family','Brachaeluridae','blind sharks'),
       ('Family','Pomatomidae','bluefishes'),
       ('Family','Inermiidae','bonnetmouths'),
       ('Family','Ostraciidae','Boxfishes'),
       ('Family','Sparidae','Breams'),
       ('Family','Heterodontidae','bullhead sharks'),
       ('Family','Pempheridae','Bullseyes'),
       ('Family','Chaetodontidae','Butterflyfishes'),
       ('Family','Odacidae','cales'),
       ('Family','Apogonidae','Cardinalfishes'),
       ('Family','Scyliorhinidae','cat sharks'),
       ('Family','Gobiesocidae','Clingfishes'),
       ('Family','Rachycentridae','cobias'),
       ('Family','Nototheniidae','cod icefishes'),
       ('Family','Gadidae','cods'),
       ('Family','Hypnidae','Coffin rays'),
       ('Family','Congridae','Conger eels'),
       ('Family','Pholidichthyidae','convict blenny'),
       ('Family','Channichthyidae','crocodile icefishes'),
       ('Family','Ophidiidae','cusk-eels'),
       ('Family','Pomacentridae','Damselfishes'),
       ('Family','Microdesmidae','Dartfishes'),
       ('Family','Ptereleotridae','dartfishes'),
       ('Family','Moridae','deep-sea cods'),
       ('Family','Mobulidae','Devil rays'),
       ('Family','Zeidae','dories'),
       ('Family','Pseudochromidae','Dottybacks'),
       ('Family','Callionymidae','Dragonets'),
       ('Family','Kyphosidae','Drummers'),
       ('Family','Sciaenidae','drums or croakers'),
       ('Family','Percophidae','duckbills'),
       ('Family','Myliobatidae','Eagle rays'),
       ('Family','Zoarcidae','eelpouts'),
       ('Family','Plotosidae','Eeltail catfishes'),
       ('Family','Lethrinidae','Emperors'),
       ('Family','Aulopidae','flagfins'),
       ('Family','Kuhliidae','Flagtails'),
       ('Family','Platycephalidae','Flatheads'),
       ('Family','Fistulariidae','Flutemouths'),
       ('Family','Batrachoididae','Frogfishes'),
       ('Family','Caesionidae','Fusiliers'),
       ('Family','Dichistiidae','galjoen fishes'),
       ('Family','Solenostomidae','Ghost pipefishes'),
       ('Family','Scombropidae','gnomefishes'),
       ('Family','Mullidae','Goatfishes'),
       ('Family','Gobiidae','Gobies'),
       ('Family','Hexagrammidae','greenlings'),
       ('Family','Odontaspididae','Grey nurse sharks'),
       ('Family','Callanthiidae','groppos'),
       ('Family','Serranidae','Groupers and rockcods'),
       ('Family','Pinguipedidae','Grubfishes'),
       ('Family','Haemulidae','Grunter breams (sweetlips)'),
       ('Family','Terapontidae','grunters or tigerperches'),
       ('Family','Rhinobatidae','Guitarfishes'),
       ('Family','Pholidae','gunnels'),
       ('Family','Hemiramphidae','halfbeaks'),
       ('Family','Sphyrnidae','Hammerhead sharks'),
       ('Family','Brachionichthyidae','handfishes'),
       ('Family','Cirrhitidae','Hawkfishes'),
       ('Family','Clupeidae','herrings'),
       ('Family','Clinidae','kelp blennies'),
       ('Family','Chironemidae','kelpfishes'),
       ('Family','Oplegnathidae','knifejaws'),
       ('Family','Labrisomidae','labrisomid blennies'),
       ('Family','Myctophidae','Lanternfishes'),
       ('Family','Monacanthidae','Leatherjackets'),
       ('Family','Bothidae','Lefteye flounders'),
       ('Family','Synodontidae','Lizardfishes'),
       ('Family','Dinolestidae','long-finned pikes'),
       ('Family','Hemiscylliidae','Longtail carpet sharks'),
       ('Family','Lamnidae','mackerel sharks'),
       ('Family','Aplodactylidae','Marblefishes'),
       ('Family','Centrolophidae','medusafishes'),
       ('Family','Chanidae','Milkfish'),
       ('Family','Molidae','molas'),
       ('Family','Zanclidae','Moorish idol'),
       ('Family','Muraenidae','Moray eels'),
       ('Family','Cheilodactylidae','Morwongs'),
       ('Family','Mugilidae','Mullets'),
       ('Family','Belonidae','Needlefishes'),
       ('Family','Ginglymostomatidae','Nurse sharks'),
       ('Family','Atherinidae','Old World silversides'),
       ('Family','Enoplosidae','oldwives'),
       ('Family','Caracanthidae','orbicular velvetfishes'),
       ('Family','Scaridae','parrotfishes'),
       ('Family','Glaucosomatidae','Pearl perches'),
       ('Family','Centracanthidae','picarel porgies'),
       ('Family','Monocentridae','Pineapplefishes'),
       ('Family','Callorhinchidae','plownose chimaeras'),
       ('Family','Agonidae','poachers'),
       ('Family','Diodontidae','Porcupinefishes'),
       ('Family','Plesiopidae','Prettyfins (devilfishes)'),
       ('Family','Stichaeidae','pricklebacks'),
       ('Family','Tetraodontidae','Pufferfishes'),
       ('Family','Siganidae','Rabbitfishes'),
       ('Family','Congiopodidae','racehorses or pigfishes'),
       ('Family','Centriscidae','Razorfishes'),
       ('Family','Gnathanacanthidae','red velvetfishes'),
       ('Family','Echeneidae','Remoras'),
       ('Family','Pleuronectidae','righteye flounders'),
       ('Family','Bathymasteridae','ronquils'),
       ('Family','Trachichthyidae','roughies'),
       ('Family','Urolophidae','round stingrays'),
       ('Family','Salmonidae','salmonids'),
       ('Family','Creediidae','Sand burrowers'),
       ('Family','Paralichthyidae','sand flounders'),
       ('Family','Ammodytidae','sand lances'),
       ('Family','Scatophagidae','scats'),
       ('Family','Scorpaenidae','Scorpionfishes'),
       ('Family','Cottidae','sculpins'),
       ('Family','Syngnathidae','Seahorses and pipefishes'),
       ('Family','Triglidae','searobins or gurnards'),
       ('Family','Monodactylidae','Silver batfishes'),
       ('Family','Gerreidae','Silverbiddies'),
       ('Family','Rajidae','skates'),
       ('Family','Liparidae','snailfishes'),
       ('Family','Ophichthidae','Snake eels'),
       ('Family','Gempylidae','snake mackerels'),
       ('Family','Soleidae','soles'),
       ('Family','Holocentridae','Squirrelfishes and soldierfishes'),
       ('Family','Uranoscopidae','stargazers'),
       ('Family','Gasterosteidae','sticklebacks'),
       ('Family','Dasyatidae','Stingrays'),
       ('Family','Synanceiidae','Stonefishes'),
       ('Family','Embiotocidae','surfperches'),
       ('Family','Acanthuridae','Surgeonfishes'),
       ('Family','Pempherididae','sweepers'),
       ('Family','Megalopidae','tarpons'),
       ('Family','Moronidae','temperate basses'),
       ('Family','Elopidae','tenpounders'),
       ('Family','Nemipteridae','Threadfin breams'),
       ('Family','Malacanthidae','Tilefishes'),
       ('Family','Carangidae','Trevallies'),
       ('Family','Balistidae','Triggerfishes'),
       ('Family','Tripterygiidae','Triplefins'),
       ('Family','Lutjanidae','Tropical snappers'),
       ('Family','Latridae','trumpeters'),
       ('Family','Aulostomidae','Trumpetfishes'),
       ('Family','Chaenopsidae','tube blennies'),
       ('Family','Aulorhynchidae','tubesnouts'),
       ('Family','Scombridae','Tunas, mackerels'),
       ('Family','Scophthalmidae','turbots'),
       ('Family','Aploactinidae','velvetfishes'),
       ('Family','Bythitidae','viviparous brotulas'),
       ('Family','Tetrarogidae','Wasp fishes'),
       ('Family','Trachinidae','weeverfishes'),
       ('Family','Rhincodontidae','Whale sharks'),
       ('Family','Carcharhinidae','Whaler sharks'),
       ('Family','Sillaginidae','whitings'),
       ('Family','Orectolobidae','Wobbegongs'),
       ('Family','Anarhichadidae','wolffishes'),
       ('Family','Labridae','Wrasses'),
       ('Family','Albulidae','Bonefishes   '),
       ('Family','Harpagiferidae','Spiny Plunderfishes  '),
       ('Class','Actinopterygii','Ray-finned fishes'),
       ('Class','Asteroidea','Sea stars'),
       ('Class','Aves','Birds'),
       ('Class','Bivalvia','Bivalves'),
       ('Class','Cephalopoda','Cephalopods'),
       ('Class','Crinoidea','Featherstars'),
       ('Class','Echinoidea','Sea urchins'),
       ('Class','Elasmobranchii','Cartilaginous fishes'),
       ('Class','Gastropoda','Sea snails and slugs'),
       ('Class','Holothuroidea','Sea cucumbers'),
       ('Class','Hydrozoa','Jellyfish'),
       ('Class','Malacostraca','Higher crustaceans'),
       ('Class','Mammalia','Mammals'),
       ('Class','Merostomata','Horseshoe crabs'),
       ('Class','Ophiuroidea','Basketstars and brittlestars'),
       ('Class','Polychaeta','Fireworms'),
       ('Class','Polyplacophora','Chitons'),
       ('Class','Pycnogonida','Sea spiders'),
       ('Class','Reptilia','Reptiles'),
       ('Class','Rhabditophora','Flatworms'),
       ('Class','Scyphozoa','Jellyfish'),
       ('Class','Tentaculata','Comb jellies'),
       ('Class','Turbellaria','Flatworms');


REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes;
