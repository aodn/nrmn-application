
INSERT INTO
    nrmn.sec_user (id, email_address, full_name, status, version)
    VALUES( 123456, 'tj@gmail.com', 'Tanjona R', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_sec_role (sec_user_id, sec_role_id)
    VALUES (123456, 'ROLE_USER');


