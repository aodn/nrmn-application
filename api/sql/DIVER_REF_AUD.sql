create table diver_ref_aud
(
    diver_id      integer not null,
    rev           integer not null
        constraint fk1nahs3dov9lbpxnmeafoyl82i
            references revinfo,
    revtype       smallint,
    full_name     varchar(255),
    full_name_mod boolean,
    initials      varchar(255),
    initials_mod  boolean,
    constraint diver_ref_aud_pkey
        primary key (diver_id, rev)
);