ALTER TABLE IF EXISTS nrmn.sec_user_aud
    DROP CONSTRAINT IF EXISTS FK_SEC_USER_AUD_REV;

ALTER TABLE IF EXISTS nrmn.sec_user_sec_role
    DROP CONSTRAINT IF EXISTS FK_ROLE_USER_SEC;

ALTER TABLE IF EXISTS nrmn.sec_user
    DROP CONSTRAINT IF EXISTS UNIQUE_EMAIL;

ALTER TABLE IF EXISTS nrmn.sec_user_sec_role
    DROP CONSTRAINT IF EXISTS FK_USER_SEC_ROLE;

DROP TABLE IF EXISTS nrmn.REVINFO CASCADE;

DROP TABLE IF EXISTS nrmn.sec_role CASCADE;

DROP TABLE IF EXISTS nrmn.sec_user CASCADE;

DROP TABLE IF EXISTS nrmn.sec_user_aud CASCADE;

DROP TABLE IF EXISTS nrmn.sec_user_sec_role CASCADE;

DROP SEQUENCE IF EXISTS nrmn.hibernate_sequence;

DROP SEQUENCE IF EXISTS nrmn.role_id_seq;

DROP SEQUENCE IF EXISTS nrmn.user_id_seq;

CREATE SEQUENCE nrmn.hibernate_sequence
    START 1
    INCREMENT 1;

CREATE SEQUENCE nrmn.role_id_seq
    START 1
    INCREMENT 1;

CREATE SEQUENCE nrmn.user_id_seq
    START 1
    INCREMENT 1;

CREATE TABLE nrmn.REVINFO (
    REV int4 NOT NULL,
    REVTSTMP int8,
    PRIMARY KEY (REV)
);

CREATE TABLE nrmn.sec_role (
    id int8 NOT NULL,
    name varchar(255) NOT NULL,
    version int4 NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE nrmn.sec_user (
    id int4 NOT NULL,
    email_address varchar(255) NOT NULL,
    full_name varchar(255),
    hashed_password varchar(255),
    status varchar(255) NOT NULL,
    version int4 NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE nrmn.sec_user_aud (
    id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    email_address varchar(255),
    email_MOD boolean,
    full_name varchar(255),
    fullName_MOD boolean,
    hashed_password varchar(255),
    hashedPassword_MOD boolean,
    status varchar(255),
    status_MOD boolean,
    PRIMARY KEY (id, REV)
);

CREATE TABLE nrmn.sec_user_sec_role (
    sec_user_id int4 NOT NULL,
    sec_role_id int8 NOT NULL,
    PRIMARY KEY (sec_user_id, sec_role_id)
);

ALTER TABLE IF EXISTS nrmn.sec_user
    ADD CONSTRAINT UNIQUE_EMAIL UNIQUE (email_address);

ALTER TABLE IF EXISTS nrmn.sec_user_aud
    ADD CONSTRAINT FK_SEC_USER_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.sec_user_sec_role
    ADD CONSTRAINT FK_ROLE_USER_SEC FOREIGN KEY (sec_role_id) REFERENCES nrmn.sec_role;

ALTER TABLE IF EXISTS nrmn.sec_user_sec_role
    ADD CONSTRAINT FK_USER_SEC_ROLE FOREIGN KEY (sec_user_id) REFERENCES nrmn.sec_user;

