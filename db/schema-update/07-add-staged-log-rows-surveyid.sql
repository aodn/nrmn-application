BEGIN TRANSACTION;

ALTER TABLE nrmn.staged_job_log ADD COLUMN survey_id INT;
ALTER TABLE nrmn.staged_job_log ADD COLUMN row_summary TEXT;

END TRANSACTION
