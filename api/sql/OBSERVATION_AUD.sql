create table observation_aud
(
    observation_id            integer not null,
    rev                       integer not null
        constraint fkctpj5torreec5ut7jcsxjwxtd
            references revinfo,
    revtype                   smallint,
    measure_value             integer,
    measure_value_mod         boolean,
    observation_attribute     varchar(255),
    observation_attribute_mod boolean,
    constraint observation_aud_pkey
        primary key (observation_id, rev)
);