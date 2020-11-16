INSERT INTO nrmn.location_ref(location_id, location_name, is_active)
VALUES (29, 'SA - Western', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (55, 'RLS', True);

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
VALUES (56, 'ATRC', True);

INSERT INTO nrmn.site_ref(site_id, site_code, site_name, longitude, latitude, location_id, site_attribute, is_active)
VALUES (551, 'EYR71', 'South East Slade Point', 154, -35, 29,
        '{"MPA": "West Coast Bays MP", "Zone": "Habitat Protection Zone 1", "State": "South Australia", "Country": "Australia", "ProxCountry": "Australia", "OldSiteCodes": "4117", "Effectiveness2": "High", "ProtectionStatus": "Restricted take multizoned", "year_of_protection": "2012"}',
        True);

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, survey_attribute)
VALUES (55, 551, 812300132, '2006-12-04', '00:00:00', 5, 2, 7, 'SW',
        '{"Notes": "Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.", "ProjectTitle": "Baseline survey of Batemans Marine park prior to protection", "InsideMarinePark": "No", "BlockAbundanceSimulated": true}');

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, survey_attribute)
VALUES (55, 551, 812300134, '2006-12-04', '00:00:00', 10, 2, 7, 'SW',
        '{"Notes": "Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.", "ProjectTitle": "Baseline survey of Batemans Marine park prior to protection", "InsideMarinePark": "No", "BlockAbundanceSimulated": true}');

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, survey_attribute)
VALUES (55, 551, 812300133, '2006-12-04', '00:00:00', 15, 3, 7, 'SW',
        '{"Notes": "Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.", "ProjectTitle": "Baseline survey of Batemans Marine park prior to protection", "InsideMarinePark": "No", "BlockAbundanceSimulated": true}');

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, survey_attribute)
VALUES (55, 551, 812300131, '2006-12-04', '00:00:00', 20, 1, 7, 'E',
        '{"Notes": "Surveyed with a BACI design as baseline for a proposed Marine Park with at least a draft zoning plan-As opposed to general biodiversity surveys of general areas, potential MPAs or proposed MPA without draft zoning plans.", "ProjectTitle": "Baseline survey of Batemans Marine park prior to protection", "InsideMarinePark": "No", "BlockAbundanceSimulated": true}');

INSERT INTO nrmn.survey (program_id, site_id, survey_id, survey_date, survey_time, depth, survey_num, visibility,
                         direction, survey_attribute)
VALUES (55, 551, 812331345, '2017-11-28', '00:00:00', 25, 1, 10, 'N',
        '{"InsideMarinePark": "Unsure", "BlockAbundanceSimulated": true}');



INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (51, 'TJR', 'Tanjona Julien Rafidison');
INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (70, 'AZS', 'Alex Zum Smith');
INSERT INTO nrmn.diver_ref(diver_id, initials, full_name)
VALUES (80, 'EVP', 'Eve valerie Piotr');


INSERT INTO nrmn.sec_role
    (NAME, version)
VALUES ('ROLE_USER', 1),
       ('ROLE_ADMIN', 1),
       ('ROLE_AODN_ADMIN', 1);

INSERT INTO nrmn.sec_user (id, email_address, full_name, hashed_password, status, version)
VALUES (123456, 'test@gmail.com', 'Tanjona R', '$2a$10$URwhRiv5533pail2XSHzA.VEyFViKZHBy4VSIpZ7woTwfZ7X2U8DS', 'ACTIVE',
        1);

INSERT INTO nrmn.sec_user_roles (sec_user_id, sec_role_id)
VALUES (123456, 'ROLE_USER');

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
                             program_id)
VALUES (109, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'jobid-rls', 'FILE', 'PENDING', 55);

INSERT INTO nrmn.staged_job (id,
                             created,
                             last_updated,
                             reference,
                             source,
                             status,
                             program_id)
VALUES (119, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP , 'jobid-atrc', 'FILE', 'PENDING', 56);


INSERT INTO  nrmn.aphia_ref (
    aphia_id,
    valid_name,
    is_extinct
) VALUES ( 102, 'Specie 56', false)