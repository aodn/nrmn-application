INSERT INTO nrmn.location_ref(location_id, location_name, is_active)
VALUES (29, 'SA - Western', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (55, 'RLS', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (56, 'ATRC', True);

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
VALUES (55, 551, 812300133, '2006-12-04', '00:00:00', 15, 3, 7, 'SW',
        'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812300131, '2006-12-04', '00:00:00', 20, 1, 7, 'E',
        'Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.',
        'Baseline survey of Batemans Marine park prior to protection', 'No', true);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, notes, project_title, inside_marine_park, block_abundance_simulated)
VALUES (55, 551, 812331345, '2017-11-28', '00:00:00', 25, 1, 10, 'N',
        null, null, 'Unsure', true);




INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (51, 'TJR', 'Tanjona Julien Rafidison');
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
VALUES (102, 'Specie 56', false),(103, 'Specie 57', false);

INSERT INTO nrmn.aphia_rel_type_ref (aphia_rel_type_id, aphia_rel_type_name)
VALUES (1, 'is');

INSERT INTO nrmn.obs_item_type_ref (obs_item_type_id, obs_item_type_name, is_active)
VALUES (1, 'Species', true);

INSERT INTO nrmn.observable_item_ref (observable_item_id, obs_item_type_id, aphia_id, aphia_rel_type_id,
                                      observable_item_name)
VALUES (333, 1, 102, 1, 'Specie 56'),
       (331, 1, 103, 1, 'Specie 57');

INSERT INTO nrmn.method_ref(method_id, method_name, is_active)
VALUES (1, 'Standard fish', true), (2, 'Big fish', true);

INSERT INTO nrmn.survey_method(survey_method_id, block_num,
                               survey_method_attribute, survey_not_done,
                               method_id, survey_id)
                            VALUES (121,1,'{}',false,1,812300133),
                                   (221,2,'{}',false,1,812300133),
                                   (321,1,'{}',false,2,812300133),
                                   (421,2,'{}',false,2,812300133);

INSERT INTO nrmn.measure_type_ref(measure_type_id, measure_type_name, is_active)
VALUES (1, 'Fish Size Class', true),
       (2, 'Big Size Class', true),
       (3, 'Giga Size Class', true),
       (4, 'Mega Size Class', true);

INSERT INTO nrmn.measure_ref(measure_id, measure_type_id, measure_name, seq_no, is_active)
VALUES  (1, 1, '2.5cm', 1, true),
       (2, 2, '5cm', 1, true),
       (3, 3, '7.5cm', 1, true),
       (4, 4, '10cm', 1, true);;


INSERT INTO nrmn.observation(observation_id, measure_value,
                             observation_attribute, diver_id, measure_id,
                             observable_item_id, survey_method_id)
VALUES (551, 1, '{}',51, 3, 333,121),
       (552, 1, '{}',51, 2, 333,121),
       (554, 4, '{}',51, 3, 331,  121),
       (553, 5, '{}',51, 2, 331,  121);

INSERT INTO nrmn.staged_row(pqs, block, buddy, code, created, date, depth, direction, diver,
                            inverts, is_invert_sizing, last_updated, latitude, longitude, 
                            measure_value, method, site_name, site_no, species, time, total, vis, staged_job_id)
VALUES ('TJR', 1, 'AZS', 'AAA', '09/09/2009', '03/03/2003', 3.3, 'NW', 'TJR',
        0, false, '01/01/2001', -5, 35, 
        '{
          "1": "5"
        }', 1, 'Castillo', 'CEU4', 'Specie 56', '11:11', 5, 11, 109);

REFRESH MATERIALIZED VIEW nrmn.ui_species_attributes;
