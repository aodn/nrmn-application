create table observable_item_ref_aud
(
    observable_item_id       integer not null,
    rev                      integer not null
        constraint fksehkdmw8opm6n0ytxsmtcjx9l
            references revinfo,
    revtype                  smallint,
    obs_item_attribute       varchar(255),
    obs_item_attribute_mod   boolean,
    observable_item_name     varchar(255),
    observable_item_name_mod boolean,
    constraint observable_item_ref_aud_pkey
        primary key (observable_item_id, rev)
);