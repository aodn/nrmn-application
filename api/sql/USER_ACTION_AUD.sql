create table user_action_aud
(
    id         bigint not null
        constraint user_action_aud_pkey
            primary key,
    audit_time timestamp with time zone,
    details    text,
    operation  varchar(255),
    request_id varchar(255),
    username   varchar(255)
);