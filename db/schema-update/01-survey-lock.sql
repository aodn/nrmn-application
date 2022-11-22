-- add boolean column 'locked' to survey used to determine if a survey can be used in the corrections process
BEGIN TRANSACTION;
ALTER TABLE nrmn.survey ADD COLUMN locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE nrmn.survey_aud ADD COLUMN locked BOOLEAN;
ALTER TABLE nrmn.survey_aud ADD COLUMN locked_mod BOOLEAN;
END TRANSACTION;
