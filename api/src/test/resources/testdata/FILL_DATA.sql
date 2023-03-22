INSERT INTO nrmn.location_ref(location_id, location_name, is_active)
VALUES (29, 'SA - Western', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (0, 'NONE', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (55, 'RLS', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (56, 'ATRC', True);

INSERT INTO nrmn.location_ref (location_id, location_name, is_active) VALUES (0, 'NONE', true);

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
/*
 This survey have pq_catalogued true
 */
INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated, pq_catalogued)
VALUES (55, 551, 812331347, '2017-11-29', '00:00:00', 25, 1, 10, 'N',
        'Survey locked for edit', 'Not able to correct this', 'Unsure', true, true);


INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (51, 'JEP', 'Juan Espanol Pagina');
INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (70, 'AZS', 'Alex Zum Smith');
INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (80, 'EVP', 'Eve valerie Piotr');

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
VALUES (109, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'jobid-rls', 'INGEST', 'STAGED', 55, 123456, false);

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
VALUES (1, 'is');

INSERT INTO nrmn.obs_item_type_ref (obs_item_type_id, obs_item_type_name, is_active)
VALUES (1, 'Species', true), (5, 'Debris', true);

INSERT INTO nrmn.observable_item_ref (observable_item_id, obs_item_type_id, aphia_id, aphia_rel_type_id,
                                      observable_item_name, letter_code)
VALUES (333, 1, 102, 1, 'Species 56', 'S56'),
       (331, 1, 103, 1, 'Species 57', 'S57'),
       (332, 5, null, null, 'Debris', 'deb'),
       (334, 1, null, null, 'Haliotis rubra', 'hal'),
       (330, 1, null, null, 'Duplicate rubra', 'dup');

INSERT INTO nrmn.method_ref(method_id, method_name, is_active)
VALUES (1, 'Standard fish', true), (2, 'Standard invertebrates & cryptic fish', true), (3, 'Standard quadrat', true), (11, 'Off transect', true), (12, 'Debris', true);

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
VALUES ('JEP', 1, 'AZS', 'AAA', '09/09/2009', '03/03/2003', 3.3, 'NW', 'JEP',
        0, false, '01/01/2001', -5, 35, 
        '{
          "1": "5"
        }', 1, 'Castillo', 'CEU4', 'Species 56', '11:11', 5, 11, 109);

REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes;
