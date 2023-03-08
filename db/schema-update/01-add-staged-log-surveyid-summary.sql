BEGIN TRANSACTION;

ALTER TABLE nrmn.staged_job_log DROP COLUMN IF EXISTS survey_id;
ALTER TABLE nrmn.staged_job_log DROP COLUMN IF EXISTS summary;

ALTER TABLE nrmn.staged_job_log ADD COLUMN survey_id INT;
ALTER TABLE nrmn.staged_job_log ADD COLUMN summary JSONB;

END TRANSACTION