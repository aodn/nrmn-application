
    alter table if exists nrmn.public_data_exclusion
       drop constraint if exists FK_DATA_EXCLUSION_PROGRAM;

    alter table if exists nrmn.public_data_exclusion
       drop constraint if exists FK_DATA_EXCLUSION_SITE;

    drop table if exists nrmn.public_data_exclusion cascade;

    create table nrmn.public_data_exclusion (
       program_program_id int4 not null,
        site_site_id int4 not null,
        primary key (program_program_id, site_site_id)
    );

    alter table if exists nrmn.public_data_exclusion
       add constraint FK_DATA_EXCLUSION_PROGRAM
       foreign key (program_program_id)
       references nrmn.program_ref;

    alter table if exists nrmn.public_data_exclusion
       add constraint FK_DATA_EXCLUSION_SITE
       foreign key (site_site_id)
       references nrmn.site_ref;

