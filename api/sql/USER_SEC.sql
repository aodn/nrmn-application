create table user_sec
(
    user_id         integer not null
        constraint user_sec_pkey
            primary key,
    created_at      timestamp,
    last_updated    timestamp,
    is_active       boolean,
    email           varchar(255),
    full_name       varchar(255),
    hashed_password varchar(255),
    is_superuser    boolean
);