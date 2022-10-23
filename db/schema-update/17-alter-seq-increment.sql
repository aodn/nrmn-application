BEGIN TRANSACTION;
ALTER SEQUENCE nrmn.observation_observation_id INCREMENT BY 100;
ALTER SEQUENCE nrmn.survey_method_survey_method_id INCREMENT BY 100;
END TRANSACTION;
