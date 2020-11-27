INSERT INTO
    nrmn.sec_role
(NAME, version)
VALUES ('ROLE_ADMIN', 1), ('ROLE_DATA_OFFICER', 1), ('ROLE_POWER_USER', 1);

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 123456, 'test@gmail.com', 'Tanjona R','$2a$10$URwhRiv5533pail2XSHzA.VEyFViKZHBy4VSIpZ7woTwfZ7X2U8DS', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (123456, 'ROLE_DATA_OFFICER');

INSERT INTO
    nrmn.sec_user (id, email_address, full_name,hashed_password, status, version)
    VALUES( 654321, 'power_user@gmail.com', 'Power User','$2a$10$URwhRiv5533pail2XSHzA.VEyFViKZHBy4VSIpZ7woTwfZ7X2U8DS', 'ACTIVE', 1);

INSERT INTO
    nrmn.sec_user_roles (sec_user_id, sec_role_id)
    VALUES (654321, 'ROLE_POWER_USER');

