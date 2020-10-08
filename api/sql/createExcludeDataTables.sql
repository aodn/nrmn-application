ALTER TABLE IF EXISTS nrmn.public_data_exclusion
    DROP CONSTRAINT IF EXISTS FK_DATA_EXCLUSION_PROGRAM;

ALTER TABLE IF EXISTS nrmn.public_data_exclusion
    DROP CONSTRAINT IF EXISTS FK_DATA_EXCLUSION_SITE;

DROP TABLE IF EXISTS nrmn.public_data_exclusion CASCADE;

CREATE TABLE nrmn.public_data_exclusion (
    program_program_id int4 NOT NULL,
    site_site_id int4 NOT NULL,
    PRIMARY KEY (program_program_id, site_site_id)
);

ALTER TABLE IF EXISTS nrmn.public_data_exclusion
    ADD CONSTRAINT FK_DATA_EXCLUSION_PROGRAM FOREIGN KEY (program_program_id) REFERENCES nrmn.program_ref;

ALTER TABLE IF EXISTS nrmn.public_data_exclusion
    ADD CONSTRAINT FK_DATA_EXCLUSION_SITE FOREIGN KEY (site_site_id) REFERENCES nrmn.site_ref;

