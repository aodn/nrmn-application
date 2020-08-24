create table survey_method_aud
(
    survey_method_id    integer not null,
    rev                 integer not null
        constraint fkk0pl3e2pnxqsx8schxcqakf4p
            references revinfo,
    revtype             smallint,
    block_num           integer,
    block_num_mod       boolean,
    survey_not_done     boolean,
    survey_not_done_mod boolean,
    constraint survey_method_aud_pkey
        primary key (survey_method_id, rev)
);