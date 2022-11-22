-- add boolean column 'locked' to survey used to determine if a survey can be used in the corrections process
BEGIN TRANSACTION;
ALTER TABLE nrmn.survey ADD COLUMN locked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE nrmn.survey_aud ADD COLUMN locked BOOLEAN;
ALTER TABLE nrmn.survey_aud ADD COLUMN locked_mod BOOLEAN;

UPDATE nrmn.survey s
SET locked = true
FROM (SELECT DISTINCT sm.survey_id
      FROM nrmn.observation o RIGHT JOIN nrmn.survey_method sm on o.survey_method_id = sm.survey_method_id
      WHERE o.observation_attribute -> 'Notes' IS NOT NULL
         OR o.observation_attribute -> 'SizeRaw' IS NOT NULL
         OR o.observation_attribute -> 'DescriptiveName' IS NOT NULL
         OR o.observation_attribute -> 'SizeClassRaw' IS NOT NULL
         OR o.observation_attribute -> 'LegalSize' IS NOT NULL
         OR o.observation_attribute -> 'SizeEstimated' IS NOT NULL
         OR o.observation_attribute -> 'SpeciesSex' IS NOT NULL
         OR o.observation_attribute -> 'SimulatedAbsence' IS NOT NULL
         OR o.observation_attribute -> 'SizeClassEstimated' IS NOT NULL
         OR o.observation_attribute -> 'NonStandardData' IS NOT NULL
         OR sm.survey_method_attribute -> 'LegacyMethod' IS NOT NULL) su
where su.survey_id = s.survey_id;

END TRANSACTION;
