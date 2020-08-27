
    alter table if exists nrmn.sec_user_sec_role 
       drop constraint if exists FK_ROLE_USER_SEC;

    alter table if exists nrmn.sec_user_sec_role 
       drop constraint if exists FK_USER_SEC_ROLE;

    drop table if exists nrmn.sec_role cascade;

    drop table if exists nrmn.sec_user cascade;

    drop table if exists nrmn.sec_user_sec_role cascade;

    drop sequence if exists role_id_seq;

    drop sequence if exists user_id_seq;

    create table nrmn.sec_role (
       id int8 not null,
        name varchar(255) not null,
        version int4 not null,
        primary key (id)
    );

    create table nrmn.sec_user (
       id int4 not null,
        email_address varchar(255) not null,
        full_name varchar(255),
        hashed_password varchar(255),
        status varchar(255) not null,
        version int4 not null,
        primary key (id)
    );

    create table nrmn.sec_user_sec_role (
       sec_user_id int4 not null,
        sec_role_id int8 not null,
        primary key (sec_user_id, sec_role_id)
    );

    alter table if exists nrmn.sec_user 
       add constraint UNIQUE_EMAIL unique (email_address);
create sequence role_id_seq start 1 increment 1;
create sequence user_id_seq start 1 increment 1;

    alter table if exists nrmn.sec_user_sec_role 
       add constraint FK_ROLE_USER_SEC 
       foreign key (sec_role_id) 
       references nrmn.sec_role;

    alter table if exists nrmn.sec_user_sec_role 
       add constraint FK_USER_SEC_ROLE 
       foreign key (sec_user_id) 
       references nrmn.sec_user;
