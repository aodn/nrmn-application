CREATE TABLE nrmn.diver_ref_aud (
    diver_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    full_name varchar(255),
    full_name_mod boolean,
    initials varchar(255),
    initials_mod boolean,
    CONSTRAINT diver_ref_aud_pkey PRIMARY KEY (diver_id, rev)
);

CREATE TABLE nrmn.revinfo (
    id integer NOT NULL,
    timestamp bigint NOT NULL,
    api_request_id varchar(255),
    username varchar(255),
    CONSTRAINT revinfo_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.sec_user_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    email_address varchar(255),
    email_mod boolean,
    full_name varchar(255),
    full_name_mod boolean,
    hashed_password varchar(255),
    hashed_password_mod boolean,
    status varchar(255),
    status_mod boolean,
    CONSTRAINT sec_user_aud_pkey PRIMARY KEY (id, rev)
);

CREATE TABLE nrmn.observation_aud (
    observation_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    measure_value integer,
    measure_value_mod boolean,
    observation_attribute jsonb,
    observation_attribute_mod boolean,
    diver_id integer,
    diver_mod boolean,
    measure_id integer,
    measure_mod boolean,
    observable_item_id integer,
    observable_item_mod boolean,
    survey_method_id integer,
    survey_method_mod boolean,
    CONSTRAINT observation_aud_pkey PRIMARY KEY (observation_id, rev)
);

CREATE TABLE nrmn.lengthweight_ref_aud (
    observable_item_id integer NOT NULL,
    rev integer NOT NULL,
    a float8,
    b float8,
    cf float8,
    sgfgu varchar(255),
    CONSTRAINT lengthweight_ref_aud_pkey PRIMARY KEY (observable_item_id, rev)
);

CREATE TABLE nrmn.observable_item_ref_aud (
    observable_item_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    class varchar(255),
    class_mod boolean,
    common_name varchar(255),
    common_name_mod boolean,
    family varchar(255),
    family_mod boolean,
    genus varchar(255),
    genus_mod boolean,
    habitat_groups varchar(255),
    habitat_groups_mod boolean,
    letter_code varchar(255),
    letter_code_mod boolean,
    obs_item_attribute jsonb,
    obs_item_attribute_mod boolean,
    observable_item_name varchar(255),
    observable_item_name_mod boolean,
    "order" varchar(255),
    order_mod boolean,
    phylum varchar(255),
    phylum_mod boolean,
    report_group varchar(255),
    report_group_mod boolean,
    superseded_by varchar(255),
    superseded_by_mod boolean,
    aphia_id integer,
    aphia_ref_mod boolean,
    aphia_rel_type_id integer,
    aphia_rel_type_mod boolean,
    obs_item_type_id integer,
    obs_item_type_mod boolean,
    species_epithet varchar(255),
    species_epithet_mod boolean,
    CONSTRAINT observable_item_ref_aud_pkey PRIMARY KEY (observable_item_id, rev)
);

CREATE TABLE nrmn.survey_method_aud (
    survey_method_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    block_num integer,
    block_num_mod boolean,
    survey_method_attribute jsonb,
    survey_method_attribute_mod boolean,
    survey_not_done boolean,
    survey_not_done_mod boolean,
    method_id integer,
    method_mod boolean,
    survey_id integer,
    survey_mod boolean,
    CONSTRAINT survey_method_aud_pkey PRIMARY KEY (survey_method_id, rev)
);

CREATE TABLE nrmn.survey_aud (
    survey_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    block_abundance_simulated boolean,
    block_abundance_simulated_mod boolean,
    depth integer,
    depth_mod boolean,
    direction varchar(255),
    direction_mod boolean,
    inside_marine_park varchar(255),
    inside_marine_park_mod boolean,
    latitude float8,
    latitude_mod boolean,
    longitude float8,
    longitude_mod boolean,
    notes varchar(255),
    notes_mod boolean,
    pq_catalogued boolean,
    pq_catalogued_mod boolean,
    project_title varchar(255),
    project_title_mod boolean,
    protection_status varchar(255),
    protection_status_mod boolean,
    survey_date date,
    survey_date_mod boolean,
    survey_num integer,
    survey_num_mod boolean,
    survey_time time without time zone,
    survey_time_mod boolean,
    visibility integer,
    visibility_mod boolean,
    program_id integer,
    program_mod boolean,
    site_id integer,
    site_mod boolean,
    CONSTRAINT survey_aud_pkey PRIMARY KEY (survey_id, rev)
);

CREATE TABLE nrmn.location_ref_aud (
    location_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    is_active boolean,
    is_active_mod boolean,
    location_name varchar(255),
    location_name_mod boolean,
    CONSTRAINT location_ref_aud_pkey PRIMARY KEY (location_id, rev)
);

CREATE TABLE nrmn.sec_role (
    name varchar(255) NOT NULL,
    version integer NOT NULL,
    CONSTRAINT sec_role_pkey PRIMARY KEY (name)
);

CREATE TABLE nrmn.sec_user_roles (
    sec_user_id bigint NOT NULL,
    sec_role_id varchar(255) NOT NULL,
    CONSTRAINT sec_user_roles_pkey PRIMARY KEY (sec_user_id, sec_role_id)
);

CREATE TABLE nrmn.staged_job (
    id bigint NOT NULL,
    created timestamp with time zone,
    is_extended_size boolean,
    last_updated timestamp with time zone,
    reference varchar(255),
    source varchar(255),
    status varchar(255),
    program_id integer NOT NULL,
    sec_user_id bigint NOT NULL,
    CONSTRAINT staged_job_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.sec_user (
    id bigint NOT NULL,
    email_address varchar(255) NOT NULL,
    full_name varchar(255),
    hashed_password varchar(255),
    status varchar(255) NOT NULL,
    version integer NOT NULL,
    CONSTRAINT sec_user_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.staged_row (
    id bigserial NOT NULL,
    block varchar(255),
    buddy varchar(255),
    code varchar(255),
    common_name varchar(255),
    created timestamp with time zone NOT NULL,
    date varchar(255),
    depth varchar(255),
    direction varchar(255),
    diver varchar(255),
    inverts varchar(255),
    is_invert_sizing varchar(255),
    l5 varchar(255),
    l95 varchar(255),
    lmax varchar(255),
    last_updated timestamp with time zone NOT NULL,
    latitude varchar(255),
    longitude varchar(255),
    m2_invert_sizing_species varchar(255),
    measure_value json,
    method varchar(255),
    pqs varchar(255),
    site_name varchar(255),
    site_no varchar(255),
    species varchar(255),
    time varchar(255),
    total varchar(255),
    vis varchar(255),
    position  bigint,
    staged_job_id bigint,
    CONSTRAINT staged_row_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.staged_job_log (
    id bigint NOT NULL,
    details text,
    event_time timestamp with time zone NOT NULL,
    event_type varchar(255) NOT NULL,
    staged_job_id bigint NOT NULL,
    CONSTRAINT staged_job_log_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.staged_row_error (
    job_id bigint NOT NULL,
    message varchar(255) NOT NULL,
    column_target varchar(255),
    error_type varchar(255),
    error_level varchar(255),
    row_id bigint NOT NULL,
    CONSTRAINT staged_row_error_pkey PRIMARY KEY (job_id, message, row_id)
);

CREATE TABLE nrmn.user_action_aud (
    id bigint NOT NULL,
    audit_time timestamp with time zone,
    details text,
    operation varchar(255),
    request_id varchar(255),
    username varchar(255),
    CONSTRAINT user_action_aud_pkey PRIMARY KEY (id)
);

CREATE TABLE nrmn.site_ref_aud (
    site_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    country varchar(255),
    country_mod boolean,
    currents integer,
    currents_mod boolean,
    geom PUBLIC.GEOMETRY,
    geom_mod boolean,
    is_active boolean,
    is_active_mod boolean,
    latitude float8,
    latitude_mod boolean,
    longitude float8,
    longitude_mod boolean,
    mpa varchar(255),
    mpa_mod boolean,
    old_site_code varchar[],
    old_site_codes_mod boolean,
    protection_status varchar(255),
    protection_status_mod boolean,
    relief integer,
    relief_mod boolean,
    slope integer,
    slope_mod boolean,
    wave_exposure integer,
    wave_exposure_mod boolean,
    site_attribute jsonb,
    site_attribute_mod boolean,
    site_code varchar(255),
    site_code_mod boolean,
    site_name varchar(255),
    site_name_mod boolean,
    state varchar(255),
    state_mod boolean,
    location_id integer,
    location_mod boolean,
    CONSTRAINT site_ref_aud_pkey PRIMARY KEY (site_id, rev)
);

ALTER TABLE nrmn.sec_user_aud
    ADD CONSTRAINT fk1tqqojx2q75iy64166aehon7p FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.observation_aud
    ADD CONSTRAINT fkctpj5torreec5ut7jcsxjwxtd FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.observable_item_ref_aud
    ADD CONSTRAINT fksehkdmw8opm6n0ytxsmtcjx9l FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.survey_method_aud
    ADD CONSTRAINT fkk0pl3e2pnxqsx8schxcqakf4p FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.survey_aud
    ADD CONSTRAINT fklqcbssyix1l4orhbnrvd9khta FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.location_ref_aud
    ADD CONSTRAINT fkqcdhb4kma1glcjulq39i8hofn FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.sec_user_roles
    ADD CONSTRAINT fk_role_user_sec FOREIGN KEY (sec_role_id) REFERENCES nrmn.sec_role (name) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.staged_job
    ADD CONSTRAINT fkstvpeikdu4c83xc9tmpfb4spq FOREIGN KEY (program_id) REFERENCES nrmn.program_ref (program_id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.staged_job
    ADD CONSTRAINT fk_staged_job_sec_user FOREIGN KEY (sec_user_id) REFERENCES nrmn.sec_user (id) ON UPDATE NO ACTION ON DELETE NO ACTION;


ALTER TABLE nrmn.staged_row
    ADD CONSTRAINT staged_row_staged_job_id_fkey FOREIGN KEY (staged_job_id) REFERENCES nrmn.staged_job (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE UNIQUE INDEX unique_email ON nrmn.sec_user (email_address);

ALTER TABLE nrmn.staged_job_log
    ADD CONSTRAINT staged_job_log_staged_job_id_fkey FOREIGN KEY (staged_job_id) REFERENCES nrmn.staged_job (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.staged_row_error
    ADD CONSTRAINT staged_row_error_staged_row_id_fkey FOREIGN KEY (row_id) REFERENCES nrmn.staged_row (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.diver_ref_aud
    ADD CONSTRAINT fk1nahs3dov9lbpxnmeafoyl82i FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.site_ref_aud
    ADD CONSTRAINT fkoj8hgo02f1vvoas72bogiv97t FOREIGN KEY (rev) REFERENCES nrmn.revinfo (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.lengthweight_ref_aud
    ADD CONSTRAINT fkirex80hveqn0hwv6j7pj7ftot FOREIGN KEY (observable_item_id, rev) REFERENCES nrmn.observable_item_ref_aud (observable_item_id, rev) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE nrmn.sec_user_roles
    ADD CONSTRAINT fk_user_sec_role FOREIGN KEY (sec_user_id) REFERENCES nrmn.sec_user (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE SEQUENCE IF NOT EXISTS nrmn.hibernate_sequence;

CREATE SEQUENCE IF NOT EXISTS nrmn.staged_job_id_seq;

CREATE SEQUENCE IF NOT EXISTS nrmn.staged_job_log_id_seq;

CREATE SEQUENCE IF NOT EXISTS nrmn.user_id_seq;

