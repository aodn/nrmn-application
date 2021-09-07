-- username is `power@example.com` and password is `password`
INSERT INTO nrmn.sec_user (id, email_address, full_name,hashed_password, status, version) VALUES( 654323, 'power@example.com', 'Power User','$2a$10$WZTfdnGYlr620KL4I824S.5u.u7j/LO6t4rxn91Dt44tBRFRJ4kuW', 'ACTIVE', 1);
INSERT INTO nrmn.sec_user_roles (sec_user_id, sec_role_id) VALUES (654323, 'ROLE_POWER_USER');

-- username is `data@example.com` and password is `password`
INSERT INTO nrmn.sec_user (id, email_address, full_name,hashed_password, status, version) VALUES( 654325, 'data@example.com', 'Data User','$2a$10$WZTfdnGYlr620KL4I824S.5u.u7j/LO6t4rxn91Dt44tBRFRJ4kuW', 'ACTIVE', 1);
INSERT INTO nrmn.sec_user_roles (sec_user_id, sec_role_id) VALUES (654325, 'ROLE_DATA_OFFICER');
