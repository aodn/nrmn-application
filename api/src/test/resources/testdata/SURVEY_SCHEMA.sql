create table survey
(
    survey_id        serial  not NULL
        constraint survey_pkey
            primary key,
    site_id          integer not NULL,
    program_id       integer not NULL,
    survey_date      date    not NULL,
    survey_time      time,
    depth            integer not null,
    survey_num       integer not null,
    visibility       integer,
    direction        varchar(10),
    survey_attribute jsonb,
    constraint survey_unique
        unique (site_id, survey_date, survey_time, depth, survey_num, program_id)
);
