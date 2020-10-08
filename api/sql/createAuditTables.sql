ALTER TABLE IF EXISTS nrmn.diver_ref_aud
    DROP CONSTRAINT IF EXISTS FK_DIVER_AUD_REV;

ALTER TABLE IF EXISTS nrmn.location_ref_aud
    DROP CONSTRAINT IF EXISTS FK_LOCATION_AUD_REV;

ALTER TABLE IF EXISTS nrmn.observable_item_ref_aud
    DROP CONSTRAINT IF EXISTS FK_OBS_ITEM_AUD_REV;

ALTER TABLE IF EXISTS nrmn.observation_aud
    DROP CONSTRAINT IF EXISTS FK_OBS_AUD_REV;

ALTER TABLE IF EXISTS nrmn.site_ref_aud
    DROP CONSTRAINT IF EXISTS FK_SITE_AUD_REV;

ALTER TABLE IF EXISTS nrmn.survey_aud
    DROP CONSTRAINT IF EXISTS FK_SURVEY_AUD_REV;

ALTER TABLE IF EXISTS nrmn.survey_method_aud
    DROP CONSTRAINT IF EXISTS FK_SURVEY_METHOD_AUD_REV;

DROP TABLE IF EXISTS nrmn.diver_ref_aud CASCADE;

DROP TABLE IF EXISTS nrmn.location_ref CASCADE;

DROP TABLE IF EXISTS nrmn.location_ref_aud CASCADE;

DROP TABLE IF EXISTS nrmn.observable_item_ref_aud CASCADE;

DROP TABLE IF EXISTS nrmn.observation_aud CASCADE;

DROP TABLE IF EXISTS nrmn.REVINFO CASCADE;

DROP TABLE IF EXISTS nrmn.site_ref_aud CASCADE;

DROP TABLE IF EXISTS nrmn.survey_aud CASCADE;

DROP TABLE IF EXISTS nrmn.survey_method_aud CASCADE;

DROP TABLE IF EXISTS nrmn.user_action_aud CASCADE;

CREATE TABLE nrmn.diver_ref_aud (
    diver_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    full_name varchar(255),
    fullName_MOD boolean,
    initials varchar(255),
    initials_MOD boolean,
    PRIMARY KEY (diver_id, REV)
);

CREATE TABLE nrmn.location_ref_aud (
    location_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    is_active boolean,
    isActive_MOD boolean,
    location_name varchar(255),
    locationName_MOD boolean,
    PRIMARY KEY (location_id, REV)
);

CREATE TABLE nrmn.observable_item_ref_aud (
    observable_item_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    obs_item_attribute uuid,
    obsItemAttribute_MOD boolean,
    observable_item_name varchar(255),
    observableItemName_MOD boolean,
    PRIMARY KEY (observable_item_id, REV)
);

CREATE TABLE nrmn.observation_aud (
    observation_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    measure_value int4,
    measureValue_MOD boolean,
    observation_attribute uuid,
    observationAttribute_MOD boolean,
    PRIMARY KEY (observation_id, REV)
);

CREATE TABLE nrmn.REVINFO (
    REV int4 NOT NULL,
    REVTSTMP int8,
    PRIMARY KEY (REV)
);

CREATE TABLE nrmn.site_ref_aud (
    site_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    is_active boolean,
    isActive_MOD boolean,
    latitude float8,
    latitude_MOD boolean,
    longitude float8,
    longitude_MOD boolean,
    site_attribute jsonb,
    siteAttribute_MOD boolean,
    site_code varchar(255),
    siteCode_MOD boolean,
    site_name varchar(255),
    siteName_MOD boolean,
    PRIMARY KEY (site_id, REV)
);

CREATE TABLE nrmn.survey_aud (
    survey_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    depth int4,
    depth_MOD boolean,
    direction varchar(255),
    direction_MOD boolean,
    survey_attribute uuid,
    surveyAttribute_MOD boolean,
    survey_date date,
    surveyDate_MOD boolean,
    survey_num int4,
    surveyNum_MOD boolean,
    survey_time time,
    surveyTime_MOD boolean,
    visibility int4,
    visibility_MOD boolean,
    PRIMARY KEY (survey_id, REV)
);

CREATE TABLE nrmn.survey_method_aud (
    survey_method_id int4 NOT NULL,
    REV int4 NOT NULL,
    REVTYPE int2,
    block_num int4,
    blockNum_MOD boolean,
    survey_not_done boolean,
    surveyNotDone_MOD boolean,
    PRIMARY KEY (survey_method_id, REV)
);

CREATE TABLE nrmn.user_action_aud (
    id int8 NOT NULL,
    audit_time timestamp with time zone,
    details text,
    operation varchar(255),
    request_id varchar(255),
    username varchar(255),
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS nrmn.diver_ref_aud
    ADD CONSTRAINT FK_DIVER_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.location_ref_aud
    ADD CONSTRAINT FK_LOCATION_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.observable_item_ref_aud
    ADD CONSTRAINT FK_OBS_ITEM_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.observation_aud
    ADD CONSTRAINT FK_OBS_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.site_ref_aud
    ADD CONSTRAINT FK_SITE_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.survey_aud
    ADD CONSTRAINT FK_SURVEY_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

ALTER TABLE IF EXISTS nrmn.survey_method_aud
    ADD CONSTRAINT FK_SURVEY_METHOD_AUD_REV FOREIGN KEY (REV) REFERENCES nrmn.REVINFO;

