create table revinfo
(
    id             integer not null
        constraint revinfo_pkey
            primary key,
    timestamp      bigint  not null,
    api_request_id varchar(255),
    username       varchar(255)
);
