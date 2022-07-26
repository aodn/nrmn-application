BEGIN TRANSACTION;
ALTER TABLE nrmn.survey DROP IF EXISTS created;
ALTER TABLE nrmn.survey DROP IF EXISTS modified;
ALTER TABLE nrmn.survey_aud DROP IF EXISTS created;
ALTER TABLE nrmn.survey_aud DROP IF EXISTS created_mod;
ALTER TABLE nrmn.survey_aud DROP IF EXISTS modified;
ALTER TABLE nrmn.survey_aud DROP IF EXISTS modified_mod;
ALTER TABLE nrmn.survey ADD COLUMN created TIMESTAMP;
ALTER TABLE nrmn.survey ADD COLUMN modified TIMESTAMP;
ALTER TABLE nrmn.survey_aud ADD COLUMN modified TIMESTAMP;
ALTER TABLE nrmn.survey_aud ADD COLUMN modified_mod BOOLEAN;
ALTER TABLE nrmn.survey_aud ADD COLUMN created TIMESTAMP;
ALTER TABLE nrmn.survey_aud ADD COLUMN created_mod BOOLEAN;
END;
