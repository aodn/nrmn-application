-- script to identify which survey_method can be deleted once the 'RLS/Squidle', and
-- Australian Coral Species pqscore have been deleted for the database.
-- if all pqscores are from these 2 resolution( i.e. stg3: diff = null) then delete. the result has been saved in the file SurveyMethodToDelee.csv
-- the deletion is done in a separate script

WITH stg1 AS(SELECT count(score_id) as scores,survey_method.survey_id,pq_score.survey_method_id FROM nrmn.pq_score
JOIN nrmn.survey_method ON survey_method.survey_method_id = pq_score.survey_method_id 
JOIN nrmn.pq_cat_res_ref ON pq_cat_res_ref.cat_res_id = pq_score.cat_res_id
JOIN nrmn.pq_resolution_ref ON pq_resolution_ref.resolution_id = pq_cat_res_ref.resolution_id
WHERE pq_resolution_ref.resolution_name IN ('RLS Basic')
  AND survey_id in(2000866, 2000906, 2000936, 2000959, 2000961, 2000973, 2000986, 2000996, 2001046, 2001048, 2001182, 2001183, 2001221, 2001222, 2001223, 2001245, 2001263, 2001285, 2001286, 2001299, 2001300, 2001351, 2001352, 2001353, 2001795, 2001797, 2001909, 2001910, 2001913, 2001914, 2001919, 2001920, 2001921, 2001922, 2001923, 2001930, 2001932)
GROUP BY survey_method.survey_id,pq_score.survey_method_id 
union 
SELECT count(score_id) as scores,survey_method.survey_id,pq_score.survey_method_id FROM nrmn.pq_score
JOIN nrmn.survey_method ON survey_method.survey_method_id = pq_score.survey_method_id 
JOIN nrmn.pq_cat_res_ref ON pq_cat_res_ref.cat_res_id = pq_score.cat_res_id
JOIN nrmn.pq_resolution_ref ON pq_resolution_ref.resolution_id = pq_cat_res_ref.resolution_id
WHERE pq_resolution_ref.resolution_name IN ('RLS Basic','Full Res (Amelia NSW)')
   AND survey_id in(2000925, 2001047, 2001262, 2001911, 2001912, 2001915, 2001916, 2001917, 2001918, 2001927) GROUP BY survey_method.survey_id,pq_score.survey_method_id ),



			  stg2 AS(SELECT count(score_id)as total ,survey_method.survey_id,pq_score.survey_method_id FROM nrmn.pq_score
JOIN nrmn.survey_method ON survey_method.survey_method_id = pq_score.survey_method_id 
WHERE survey_id in(2000925, 2001047, 2001262, 2001911, 2001912, 2001915, 2001916, 2001917, 2001918, 2001927, 2000866, 2000906, 2000936, 2000959, 2000961, 2000973, 2000986, 2000996, 2001046, 2001048, 2001182, 2001183, 2001221, 2001222, 2001223, 2001245, 2001263, 2001285, 2001286, 2001299, 2001300, 2001351, 2001352, 2001353, 2001795, 2001797, 2001909, 2001910, 2001913, 2001914, 2001919, 2001920, 2001921, 2001922, 2001923, 2001930, 2001932) GROUP BY survey_method.survey_id,pq_score.survey_method_id),

			  stg3 AS (SELECT stg1.scores,stg2.total, CASE 
					  WHEN stg2.total-stg1.scores=0 THEN null
					  ELSE stg2.total-stg1.scores
					  END AS diff,
					  stg1.survey_id,stg1.survey_method_id FROM stg1
					  JOIN stg2 on stg2.survey_method_id=stg1.survey_method_id)
 SELECT stg3.survey_id,stg3.survey_method_id FROM stg3
					  WHERE diff IS NULL