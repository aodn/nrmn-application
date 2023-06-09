INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 123456, 'test@example.com', 'General Test User','$2a$10$2WByZ2x9phDATumnKHVgruMb5W2n/RfQGc2Em3.dCgSi2UAKxEHNu', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (123456, 'ROLE_DATA_OFFICER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 888888, 'auth@example.com', 'Authentication Testing','$2a$10$GCFwNETRjfuFY103u6/FJOwrTX4KpkFQKHcxs63IL7X2nMP7hn9Ya', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version, expires)
    VALUES( 888889, 'expired@example.com', 'Expired Auth Testing','$2a$10$GCFwNETRjfuFY103u6/FJOwrTX4KpkFQKHcxs63IL7X2nMP7hn9Ya', 'ACTIVE', 1, '2020-01-01');

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (888888, 'ROLE_DATA_OFFICER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 321456, 'data1@example.com', 'Data Officer 1','$2a$10$tcLqckl5sN7mMqPBV6ND0.lJonHOCFx6NBMGuT9W9vLtq1wFHZUoW', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (321456, 'ROLE_DATA_OFFICER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 321654, 'data2@example.com', 'Data Officer 2','$2a$10$dxhRev.HYg/tRu5E2HVaWOra0YwcOKwlRY090U0AoxQLUdMXVE3nS', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (321654, 'ROLE_DATA_OFFICER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 654321, 'power_user@example.com', 'Power User','$2a$10$nlGPV/fr5nbbUm/XGQ94cuoN.6TAwW2IqEanB1RVIzUBTifPrdR4q', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (654321, 'ROLE_POWER_USER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
VALUES( 654322, 'survey_editor@example.com', 'Survey Editor','$2a$10$nlGPV/fr5nbbUm/XGQ94cuoN.6TAwW2IqEanB1RVIzUBTifPrdR4q', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
VALUES (654322, 'ROLE_SURVEY_EDITOR');
