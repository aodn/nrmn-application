
    drop table if exists nrmn.user_sec cascade;

    drop table if exists nrmn.user_sec_role cascade;

    alter table if exists user_sec_roles 
       drop constraint if exists FK66b1mjokf7kr304482svj793e;

    alter table if exists user_sec_roles 
       drop constraint if exists FKcsp4dn4ir904edwa6o68fnry2;

    drop table if exists user_sec_roles cascade;

    drop sequence if exists hibernate_sequence;

    create table nrmn.user_sec (
       id int4 not null,
        email_address varchar(255) not null,
        full_name varchar(255),
        hashed_password varchar(255),
        status varchar(255) not null,
        version int4 not null,
        primary key (id)
    );

    create table nrmn.user_sec_role (
       id int8 not null,
        name varchar(255) not null,
        version int4 not null,
        primary key (id)
    );

    alter table if exists nrmn.user_sec 
       add constraint UK_lw7syc93lfq5iatr1xj7s73yl unique (email_address);
create sequence hibernate_sequence start 1 increment 1;

    create table user_sec_roles (
       user_sec_id int4 not null,
        user_sec_role_id int8 not null,
        primary key (user_sec_id, user_sec_role_id)
    );

    alter table if exists user_sec_roles 
       add constraint FK66b1mjokf7kr304482svj793e 
       foreign key (user_sec_role_id) 
       references nrmn.user_sec_role;

    alter table if exists user_sec_roles 
       add constraint FKcsp4dn4ir904edwa6o68fnry2 
       foreign key (user_sec_id) 
       references nrmn.user_sec;
