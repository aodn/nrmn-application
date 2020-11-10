INSERT INTO
    nrmn.sec_role
(NAME, version)
VALUES ('ROLE_USER',1), ('ROLE_ADMIN', 1), ('ROLE_AODN_ADMIN', 1);

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 123456, 'test@gmail.com', 'Tanjona R','$2a$10$URwhRiv5533pail2XSHzA.VEyFViKZHBy4VSIpZ7woTwfZ7X2U8DS', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (123456, 'ROLE_USER');

