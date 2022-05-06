SELECT score_id,
       survey_method.survey_id,
       pq_score.survey_method_id,
       resolution_name
FROM nrmn.pq_score
JOIN nrmn.survey_method ON survey_method.survey_method_id = pq_score.survey_method_id
JOIN nrmn.pq_cat_res_ref ON pq_cat_res_ref.cat_res_id = pq_score.cat_res_id
JOIN nrmn.pq_resolution_ref ON pq_resolution_ref.resolution_id = pq_cat_res_ref.resolution_id
WHERE pq_resolution_ref.resolution_name IN ('RLS Basic')
  AND survey_id in(2000925, 2001047, 2001262, 2001911, 2001912, 2001915, 2001916, 2001917, 2001918, 2001927, 2000866, 2000906, 2000936, 2000959, 2000961, 2000973, 2000986, 2000996, 2001046, 2001048, 2001182, 2001183, 2001221, 2001222, 2001223, 2001245, 2001263, 2001285, 2001286, 2001299, 2001300, 2001351, 2001352, 2001353, 2001795, 2001797, 2001909, 2001910, 2001913, 2001914, 2001919, 2001920, 2001921, 2001922, 2001923, 2001930, 2001932)
UNION
SELECT score_id,
       survey_method.survey_id,
       pq_score.survey_method_id,
       resolution_name
FROM nrmn.pq_score
JOIN nrmn.survey_method ON survey_method.survey_method_id = pq_score.survey_method_id
JOIN nrmn.pq_cat_res_ref ON pq_cat_res_ref.cat_res_id = pq_score.cat_res_id
JOIN nrmn.pq_resolution_ref ON pq_resolution_ref.resolution_id = pq_cat_res_ref.resolution_id
WHERE pq_resolution_ref.resolution_name IN ('Full Res (Amelia NSW)')
  AND survey_id in(2000925, 2001047, 2001262, 2001911, 2001912, 2001915, 2001916, 2001917, 2001918, 2001927)