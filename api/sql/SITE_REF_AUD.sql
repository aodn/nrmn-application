create table site_ref_aud
(
    site_id            integer not null,
    rev                integer not null
        constraint fkoj8hgo02f1vvoas72bogiv97t
            references revinfo,
    revtype            smallint,
    is_active          boolean,
    active_mod         boolean,
    latitude           double precision,
    latitude_mod       boolean,
    longitude          double precision,
    longitude_mod      boolean,
    site_attribute     jsonb,
    site_attribute_mod boolean,
    site_code          varchar(255),
    site_code_mod      boolean,
    site_name          varchar(255),
    site_name_mod      boolean,
    constraint site_ref_aud_pkey
        primary key (site_id, rev)
);