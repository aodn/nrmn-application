--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.17
-- Dumped by pg_dump version 10.14 (Ubuntu 10.14-0ubuntu0.18.04.1)
drop schema if exists nrmn cascade;

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: nrmn; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nrmn;


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: aphia_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.aphia_ref (
    aphia_id integer NOT NULL,
    authority character varying(255),
    is_brackish boolean,
    citation character varying(255),
    is_extinct boolean,
    is_freshwater boolean,
    lsid character varying(255),
    is_marine boolean,
    match_type character varying(255),
    modified timestamp without time zone,
    parent_name_usage_id integer,
    rank character varying(255),
    rank_class character varying(255),
    rank_family character varying(255),
    rank_genus character varying(255),
    rank_kingdom character varying(255),
    rank_order character varying(255),
    rank_phylum character varying(255),
    scientificname character varying(255),
    status character varying(255),
    taxon_rank_id integer,
    is_terrestrial boolean,
    unacceptreason character varying(255),
    url character varying(255),
    valid_aphia_id integer,
    valid_authority character varying(255),
    valid_name character varying(255)
);


--
-- Name: aphia_rel_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.aphia_rel_type_ref (
    aphia_rel_type_id integer NOT NULL,
    aphia_rel_type_name character varying(255)
);


--
-- Name: diver_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.diver_ref (
    diver_id integer NOT NULL,
    full_name character varying(255),
    initials character varying(255)
);


--
-- Name: diver_ref_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.diver_ref_aud (
    diver_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    full_name character varying(255),
    full_name_mod boolean,
    initials character varying(255),
    initials_mod boolean
);


--
-- Name: error_check; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.error_check (
    job_id character varying(255) NOT NULL,
    message character varying(255) NOT NULL,
    column_target character varying(255),
    error_level character varying(255),
    row_id bigint NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: location_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.location_ref (
    location_id integer NOT NULL,
    is_active boolean,
    location_name character varying(255)
);


--
-- Name: location_ref_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.location_ref_aud (
    location_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    is_active boolean,
    is_active_mod boolean,
    location_name character varying(255),
    location_name_mod boolean
);


--
-- Name: measure_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.measure_ref (
    measure_id integer NOT NULL,
    is_active boolean,
    measure_name character varying(255),
    seq_no integer
);


--
-- Name: measure_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.measure_type_ref (
    measure_type_id integer NOT NULL,
    is_active boolean,
    measure_type_name character varying(255)
);


--
-- Name: method_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.method_ref (
    method_id integer NOT NULL,
    is_active boolean,
    method_name character varying(255)
);


--
-- Name: obs_item_type_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.obs_item_type_ref (
    obs_item_type_id integer NOT NULL,
    is_active boolean,
    obs_item_type_name character varying(255)
);


--
-- Name: observable_item_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observable_item_ref (
    observable_item_id integer NOT NULL,
    obs_item_attribute jsonb,
    observable_item_name character varying(255)
);


--
-- Name: observable_item_ref_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observable_item_ref_aud (
    observable_item_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    obs_item_attribute jsonb,
    obs_item_attribute_mod boolean,
    observable_item_name character varying(255),
    observable_item_name_mod boolean
);


--
-- Name: observation; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observation (
    observation_id integer NOT NULL,
    measure_value integer,
    observation_attribute jsonb
);


--
-- Name: observation_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.observation_aud (
    observation_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    measure_value integer,
    measure_value_mod boolean,
    observation_attribute jsonb,
    observation_attribute_mod boolean
);


--
-- Name: program_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.program_ref (
    program_id integer NOT NULL,
    is_active boolean,
    program_name character varying(255)
);


--
-- Name: public_data_exclusion; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.public_data_exclusion (
    program_program_id integer NOT NULL,
    site_site_id integer NOT NULL
);


--
-- Name: revinfo; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.revinfo (
    id integer NOT NULL,
    "timestamp" bigint NOT NULL,
    api_request_id character varying(255),
    username character varying(255)
);


--
-- Name: sec_role; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.sec_role (
    name character varying(255) NOT NULL,
    version integer NOT NULL
);


--
-- Name: sec_user; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.sec_user (
    id bigint NOT NULL,
    email_address character varying(255) NOT NULL,
    full_name character varying(255),
    hashed_password character varying(255),
    status character varying(255) NOT NULL,
    version integer NOT NULL
);


--
-- Name: sec_user_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.sec_user_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    email_address character varying(255),
    email_mod boolean,
    full_name character varying(255),
    full_name_mod boolean,
    hashed_password character varying(255),
    hashed_password_mod boolean,
    status character varying(255),
    status_mod boolean
);


--
-- Name: sec_user_sec_role; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.sec_user_sec_role (
    sec_user_id bigint NOT NULL,
    sec_role_id character varying(255) NOT NULL
);


--
-- Name: site_ref; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.site_ref (
    site_id integer NOT NULL,
    is_active boolean,
    latitude double precision,
    longitude double precision,
    site_attribute jsonb,
    site_code character varying(255),
    site_name character varying(255)
);


--
-- Name: site_ref_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.site_ref_aud (
    site_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    is_active boolean,
    is_active_mod boolean,
    latitude double precision,
    latitude_mod boolean,
    longitude double precision,
    longitude_mod boolean,
    site_attribute jsonb,
    site_attribute_mod boolean,
    site_code character varying(255),
    site_code_mod boolean,
    site_name character varying(255),
    site_name_mod boolean
);


--
-- Name: staged_job; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.staged_job (
    file_id character varying(255) NOT NULL,
    job_attributes jsonb,
    source character varying(255),
    status character varying(255)
);


--
-- Name: staged_survey; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.staged_survey (
    id bigint NOT NULL,
    common_name character varying(255),
    l5 integer,
    l95 integer,
    pqs integer,
    block integer,
    buddy character varying(255),
    code character varying(255),
    date date,
    depth double precision,
    direction character varying(255),
    diver character varying(255),
    inverts integer,
    is_invert_sizing boolean,
    latitude double precision,
    longitude double precision,
    m2_invert_sizing_species boolean,
    measure_value json,
    method integer,
    site_name character varying(255),
    site_no character varying(255),
    species character varying(255),
    "time" double precision,
    total integer,
    vis integer,
    staged_job_file_id character varying(255)
);


--
-- Name: staged_survey_id_seq; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.staged_survey_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: staged_survey_id_seq; Type: SEQUENCE OWNED BY; Schema: nrmn; Owner: -
--

ALTER SEQUENCE nrmn.staged_survey_id_seq OWNED BY nrmn.staged_survey.id;


--
-- Name: survey; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey (
    survey_id integer NOT NULL,
    depth integer,
    direction character varying(255),
    survey_attribute jsonb,
    survey_date date,
    survey_num integer,
    survey_time time without time zone,
    visibility integer
);


--
-- Name: survey_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey_aud (
    survey_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    depth integer,
    depth_mod boolean,
    direction character varying(255),
    direction_mod boolean,
    survey_attribute jsonb,
    survey_attribute_mod boolean,
    survey_date date,
    survey_date_mod boolean,
    survey_num integer,
    survey_num_mod boolean,
    survey_time time without time zone,
    survey_time_mod boolean,
    visibility integer,
    visibility_mod boolean
);


--
-- Name: survey_method; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey_method (
    survey_method_id integer NOT NULL,
    block_num integer,
    survey_not_done boolean
);


--
-- Name: survey_method_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.survey_method_aud (
    survey_method_id integer NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    block_num integer,
    block_num_mod boolean,
    survey_not_done boolean,
    survey_not_done_mod boolean
);


--
-- Name: user_action_aud; Type: TABLE; Schema: nrmn; Owner: -
--

CREATE TABLE nrmn.user_action_aud (
    id bigint NOT NULL,
    audit_time timestamp with time zone,
    details text,
    operation character varying(255),
    request_id character varying(255),
    username character varying(255)
);


--
-- Name: user_id_seq; Type: SEQUENCE; Schema: nrmn; Owner: -
--

CREATE SEQUENCE nrmn.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: staged_survey id; Type: DEFAULT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.staged_survey ALTER COLUMN id SET DEFAULT nextval('nrmn.staged_survey_id_seq'::regclass);


--
-- Data for Name: aphia_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: aphia_rel_type_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: diver_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: diver_ref_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: error_check; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: location_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: location_ref_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: measure_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: measure_type_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: method_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: obs_item_type_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: observable_item_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: observable_item_ref_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: observation; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: observation_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: program_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: public_data_exclusion; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: revinfo; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: sec_role; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: sec_user; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: sec_user_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: sec_user_sec_role; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: site_ref; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: site_ref_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: staged_job; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: staged_survey; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: survey; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: survey_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: survey_method; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: survey_method_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Data for Name: user_action_aud; Type: TABLE DATA; Schema: nrmn; Owner: -
--



--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: nrmn; Owner: -
--

SELECT pg_catalog.setval('nrmn.hibernate_sequence', 1, false);


--
-- Name: staged_survey_id_seq; Type: SEQUENCE SET; Schema: nrmn; Owner: -
--

SELECT pg_catalog.setval('nrmn.staged_survey_id_seq', 1, false);


--
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: nrmn; Owner: -
--

SELECT pg_catalog.setval('nrmn.user_id_seq', 1, false);


--
-- Name: aphia_ref aphia_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_ref
    ADD CONSTRAINT aphia_ref_pkey PRIMARY KEY (aphia_id);


--
-- Name: aphia_rel_type_ref aphia_rel_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.aphia_rel_type_ref
    ADD CONSTRAINT aphia_rel_type_ref_pkey PRIMARY KEY (aphia_rel_type_id);


--
-- Name: diver_ref_aud diver_ref_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref_aud
    ADD CONSTRAINT diver_ref_aud_pkey PRIMARY KEY (diver_id, rev);


--
-- Name: diver_ref diver_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref
    ADD CONSTRAINT diver_ref_pkey PRIMARY KEY (diver_id);


--
-- Name: error_check error_check_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.error_check
    ADD CONSTRAINT error_check_pkey PRIMARY KEY (job_id, message, row_id);


--
-- Name: location_ref_aud location_ref_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref_aud
    ADD CONSTRAINT location_ref_aud_pkey PRIMARY KEY (location_id, rev);


--
-- Name: location_ref location_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref
    ADD CONSTRAINT location_ref_pkey PRIMARY KEY (location_id);


--
-- Name: measure_ref measure_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_ref
    ADD CONSTRAINT measure_ref_pkey PRIMARY KEY (measure_id);


--
-- Name: measure_type_ref measure_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.measure_type_ref
    ADD CONSTRAINT measure_type_ref_pkey PRIMARY KEY (measure_type_id);


--
-- Name: method_ref method_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.method_ref
    ADD CONSTRAINT method_ref_pkey PRIMARY KEY (method_id);


--
-- Name: obs_item_type_ref obs_item_type_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.obs_item_type_ref
    ADD CONSTRAINT obs_item_type_ref_pkey PRIMARY KEY (obs_item_type_id);


--
-- Name: observable_item_ref_aud observable_item_ref_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref_aud
    ADD CONSTRAINT observable_item_ref_aud_pkey PRIMARY KEY (observable_item_id, rev);


--
-- Name: observable_item_ref observable_item_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref
    ADD CONSTRAINT observable_item_ref_pkey PRIMARY KEY (observable_item_id);


--
-- Name: observation_aud observation_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation_aud
    ADD CONSTRAINT observation_aud_pkey PRIMARY KEY (observation_id, rev);


--
-- Name: observation observation_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation
    ADD CONSTRAINT observation_pkey PRIMARY KEY (observation_id);


--
-- Name: program_ref program_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.program_ref
    ADD CONSTRAINT program_ref_pkey PRIMARY KEY (program_id);


--
-- Name: public_data_exclusion public_data_exclusion_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT public_data_exclusion_pkey PRIMARY KEY (program_program_id, site_site_id);


--
-- Name: revinfo revinfo_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.revinfo
    ADD CONSTRAINT revinfo_pkey PRIMARY KEY (id);


--
-- Name: sec_role sec_role_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_role
    ADD CONSTRAINT sec_role_pkey PRIMARY KEY (name);


--
-- Name: sec_user_aud sec_user_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user_aud
    ADD CONSTRAINT sec_user_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: sec_user sec_user_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user
    ADD CONSTRAINT sec_user_pkey PRIMARY KEY (id);


--
-- Name: sec_user_sec_role sec_user_sec_role_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user_sec_role
    ADD CONSTRAINT sec_user_sec_role_pkey PRIMARY KEY (sec_user_id, sec_role_id);


--
-- Name: site_ref_aud site_ref_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref_aud
    ADD CONSTRAINT site_ref_aud_pkey PRIMARY KEY (site_id, rev);


--
-- Name: site_ref site_ref_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref
    ADD CONSTRAINT site_ref_pkey PRIMARY KEY (site_id);


--
-- Name: staged_job staged_job_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.staged_job
    ADD CONSTRAINT staged_job_pkey PRIMARY KEY (file_id);


--
-- Name: staged_survey staged_survey_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.staged_survey
    ADD CONSTRAINT staged_survey_pkey PRIMARY KEY (id);


--
-- Name: survey_aud survey_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_aud
    ADD CONSTRAINT survey_aud_pkey PRIMARY KEY (survey_id, rev);


--
-- Name: survey_method_aud survey_method_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method_aud
    ADD CONSTRAINT survey_method_aud_pkey PRIMARY KEY (survey_method_id, rev);


--
-- Name: survey_method survey_method_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method
    ADD CONSTRAINT survey_method_pkey PRIMARY KEY (survey_method_id);


--
-- Name: survey survey_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey
    ADD CONSTRAINT survey_pkey PRIMARY KEY (survey_id);


--
-- Name: sec_user unique_email; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user
    ADD CONSTRAINT unique_email UNIQUE (email_address);


--
-- Name: user_action_aud user_action_aud_pkey; Type: CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.user_action_aud
    ADD CONSTRAINT user_action_aud_pkey PRIMARY KEY (id);


--
-- Name: diver_ref_aud fk1nahs3dov9lbpxnmeafoyl82i; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.diver_ref_aud
    ADD CONSTRAINT fk1nahs3dov9lbpxnmeafoyl82i FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: sec_user_aud fk1tqqojx2q75iy64166aehon7p; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user_aud
    ADD CONSTRAINT fk1tqqojx2q75iy64166aehon7p FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: sec_user_sec_role fk_role_user_sec; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user_sec_role
    ADD CONSTRAINT fk_role_user_sec FOREIGN KEY (sec_role_id) REFERENCES nrmn.sec_role(name);


--
-- Name: sec_user_sec_role fk_user_sec_role; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.sec_user_sec_role
    ADD CONSTRAINT fk_user_sec_role FOREIGN KEY (sec_user_id) REFERENCES nrmn.sec_user(id);


--
-- Name: staged_survey fkc53smpawan288nu3oqugn8b06; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.staged_survey
    ADD CONSTRAINT fkc53smpawan288nu3oqugn8b06 FOREIGN KEY (staged_job_file_id) REFERENCES nrmn.staged_job(file_id);


--
-- Name: observation_aud fkctpj5torreec5ut7jcsxjwxtd; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observation_aud
    ADD CONSTRAINT fkctpj5torreec5ut7jcsxjwxtd FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: public_data_exclusion fkh51awlt0f0jld3bb4hmh0ykrs; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT fkh51awlt0f0jld3bb4hmh0ykrs FOREIGN KEY (program_program_id) REFERENCES nrmn.program_ref(program_id);


--
-- Name: error_check fkhmycainhljtnhm0ywwutb308w; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.error_check
    ADD CONSTRAINT fkhmycainhljtnhm0ywwutb308w FOREIGN KEY (row_id) REFERENCES nrmn.staged_survey(id);


--
-- Name: survey_method_aud fkk0pl3e2pnxqsx8schxcqakf4p; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_method_aud
    ADD CONSTRAINT fkk0pl3e2pnxqsx8schxcqakf4p FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: survey_aud fklqcbssyix1l4orhbnrvd9khta; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.survey_aud
    ADD CONSTRAINT fklqcbssyix1l4orhbnrvd9khta FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: site_ref_aud fkoj8hgo02f1vvoas72bogiv97t; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.site_ref_aud
    ADD CONSTRAINT fkoj8hgo02f1vvoas72bogiv97t FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: public_data_exclusion fkq3j7bouanigjlvfim20yvd6qr; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.public_data_exclusion
    ADD CONSTRAINT fkq3j7bouanigjlvfim20yvd6qr FOREIGN KEY (site_site_id) REFERENCES nrmn.site_ref(site_id);


--
-- Name: location_ref_aud fkqcdhb4kma1glcjulq39i8hofn; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.location_ref_aud
    ADD CONSTRAINT fkqcdhb4kma1glcjulq39i8hofn FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- Name: observable_item_ref_aud fksehkdmw8opm6n0ytxsmtcjx9l; Type: FK CONSTRAINT; Schema: nrmn; Owner: -
--

ALTER TABLE ONLY nrmn.observable_item_ref_aud
    ADD CONSTRAINT fksehkdmw8opm6n0ytxsmtcjx9l FOREIGN KEY (rev) REFERENCES nrmn.revinfo(id);


--
-- PostgreSQL database dump complete
--

