/*
basic indexes 

.sit.location_id
meo.geom, sit.geom?
.obs.survey_method_id
.obs.diver_id
.sur.survey_date sit.SiteCode, sur.depth

nrmn.ep_site_list_private sitecode primary materialised? 
nrmn.ep_survey_list_private matereialise? with surveyID as PK? site_code
obs.observable_item_id
obs.measure_id

pq.cat_res_id
cr.category_id, cr.resolution_id

pq.survey_method_id */

CREATE index ix_sit_1 ON nrmn.site_ref(location_id);

CREATE index ix_obs_1 ON nrmn.observation (survey_method_id);
CREATE index ix_obs_2 ON nrmn.observation(diver_id);
CREATE index ix_sur_1 ON nrmn.survey(site_id, survey_date, depth);


CREATE index ix_obs_3 ON nrmn.observation(observable_item_id);
CREATE index ix_obs_4 ON nrmn.observation(measure_id);
CREATE index ix_sm_1 ON nrmn.survey_method(survey_id);

CREATE index ix_pqs_1 ON nrmn.pq_score(cat_res_id);
CREATE index ix_pqs_2 ON nrmn.pq_score(survey_method_id);
CREATE index ix_pqcr_1 ON nrmn.pq_cat_res_ref(resolution_id, category_id);

CREATE index ix_obsitem_1 ON nrmn.observable_item_ref(superseded_by);
