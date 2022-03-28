/* constraint format templates */
/* foreign key constraint format */
/*
  ALTER TABLE child_table
  ADD CONSTRAINT constraint_name
  FOREIGN KEY (child_fk_column)
  REFERENCES parent_table (parent_pk_column);
*/

/* not null constraint format */
/*
  ALTER TABLE table_name
  ALTER COLUMN column_name SET NOT NULL;
*/

/* unique constraint format */
/*
  ALTER TABLE table_name
  ADD CONSTRAINT constraint_name
  UNIQUE (column_1, column_2, etc);
*/

/* check constraint format */
/*
  ALTER TABLE table_name
  ADD CONSTRAINT constraint_name CHECK (boolean_expression); -- eg. column_1 >= column_2.  FALSE result will produce error.
*/

/* location_ref */
CREATE SEQUENCE nrmn.location_ref_location_id  OWNED BY nrmn.location_ref.location_id;

ALTER TABLE nrmn.location_ref
ALTER COLUMN location_id SET DEFAULT nextval('nrmn.location_ref_location_id'),
ALTER COLUMN location_name SET NOT NULL,
ADD CONSTRAINT location_unique UNIQUE (location_name);

/* site_ref */
CREATE SEQUENCE nrmn.site_ref_site_id  OWNED BY nrmn.site_ref.site_id;

ALTER TABLE nrmn.site_ref
ALTER COLUMN site_id SET DEFAULT nextval('nrmn.site_ref_site_id'),
ALTER COLUMN location_id SET NOT NULL,
ALTER COLUMN site_code SET NOT NULL,
ALTER COLUMN site_name SET NOT NULL,
ADD CONSTRAINT site_location_fk FOREIGN KEY (location_id) REFERENCES nrmn.location_ref (location_id),
ADD CONSTRAINT site_code_unique UNIQUE (site_code);

/* program_ref */
CREATE SEQUENCE nrmn.program_ref_program_id  OWNED BY nrmn.program_ref.program_id;

ALTER TABLE nrmn.program_ref
ALTER COLUMN program_id SET DEFAULT nextval('nrmn.program_ref_program_id'),
ALTER COLUMN program_name SET NOT NULL,
ADD CONSTRAINT program_unique UNIQUE (program_name);

/* diver_ref */
CREATE SEQUENCE nrmn.diver_ref_diver_id  OWNED BY nrmn.diver_ref.diver_id;

ALTER TABLE nrmn.diver_ref
ALTER COLUMN diver_id SET DEFAULT nextval('nrmn.diver_ref_diver_id'),
ALTER COLUMN initials SET NOT NULL,
ADD CONSTRAINT diver_unique UNIQUE (initials)
;

/* survey */
CREATE SEQUENCE nrmn.survey_survey_id  OWNED BY nrmn.survey.survey_id;

ALTER TABLE nrmn.survey
ALTER COLUMN survey_id SET DEFAULT nextval('nrmn.survey_survey_id'),
ALTER COLUMN site_id SET NOT NULL,
ALTER COLUMN survey_date SET NOT NULL,
ALTER COLUMN depth SET NOT NULL,
ALTER COLUMN survey_num SET NOT NULL,
ALTER COLUMN program_id SET NOT NULL,
ADD CONSTRAINT survey_site_fk FOREIGN KEY (site_id) REFERENCES nrmn.site_ref (site_id),
ADD CONSTRAINT survey_program_fk FOREIGN KEY (program_id) REFERENCES nrmn.program_ref (program_id),
ADD CONSTRAINT survey_unique UNIQUE (site_id, survey_date, survey_time, depth, survey_num, program_id),
ADD CONSTRAINT pq_diver_fk FOREIGN KEY (pq_diver_id) REFERENCES nrmn.diver_ref (diver_id)
;

/* survey_method */
CREATE SEQUENCE nrmn.survey_method_survey_method_id  OWNED BY nrmn.survey_method.survey_method_id;

ALTER TABLE nrmn.survey_method
ALTER COLUMN survey_method_id SET DEFAULT nextval('nrmn.survey_method_survey_method_id'),
ALTER COLUMN survey_id SET NOT NULL,
ALTER COLUMN method_id SET NOT NULL,
ADD CONSTRAINT survey_method_survey_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey (survey_id) ON DELETE CASCADE,
ADD CONSTRAINT survey_method_method_fk FOREIGN KEY (method_id) REFERENCES nrmn.method_ref (method_id),
ADD CONSTRAINT survey_method_unique UNIQUE (survey_id, method_id, block_num)
;

/* observation */
CREATE SEQUENCE nrmn.observation_observation_id  OWNED BY nrmn.observation.observation_id;

ALTER TABLE nrmn.observation
ALTER COLUMN observation_id SET DEFAULT nextval('nrmn.observation_observation_id'),
ALTER COLUMN survey_method_id SET NOT NULL,
ALTER COLUMN observable_item_id SET NOT NULL,
ALTER COLUMN measure_id SET NOT NULL,
ADD CONSTRAINT observation_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method (survey_method_id) ON DELETE CASCADE,
ADD CONSTRAINT observation_observable_item_fk FOREIGN KEY (observable_item_id) REFERENCES nrmn.observable_item_ref (observable_item_id),
ADD CONSTRAINT observation_measure_fk FOREIGN KEY (measure_id) REFERENCES nrmn.measure_ref (measure_id),
ADD CONSTRAINT observation_diver_fk FOREIGN KEY (diver_id) REFERENCES nrmn.diver_ref (diver_id),
ADD CONSTRAINT observation_unique UNIQUE (survey_method_id, observable_item_id, measure_id, diver_id, observation_attribute)
;

/* method_ref */
ALTER TABLE nrmn.method_ref
ALTER COLUMN method_name SET NOT NULL,
ADD CONSTRAINT method_unique UNIQUE (method_name)
;

/* measure_type_ref */
CREATE SEQUENCE nrmn.measure_type_ref_measure_id  OWNED BY nrmn.measure_type_ref.measure_type_id;

ALTER TABLE nrmn.measure_type_ref
ALTER COLUMN measure_type_id SET DEFAULT nextval('nrmn.measure_type_ref_measure_id'),
ALTER COLUMN measure_type_name SET NOT NULL,
ADD CONSTRAINT measure_type_unique UNIQUE (measure_type_name)
;


/* measure_ref */
CREATE SEQUENCE nrmn.measure_ref_measure_id  OWNED BY nrmn.measure_ref.measure_id;

ALTER TABLE nrmn.measure_ref
ALTER COLUMN measure_id SET DEFAULT nextval('nrmn.measure_ref_measure_id'),
ALTER COLUMN measure_name SET NOT NULL,
ALTER COLUMN measure_type_id SET NOT NULL,
ADD CONSTRAINT measure_measure_type_fk FOREIGN KEY (measure_type_id) REFERENCES nrmn.measure_type_ref (measure_type_id),
ADD CONSTRAINT measure_unique UNIQUE (measure_name, measure_type_id)
;

/* obs_item_type_ref */
CREATE SEQUENCE nrmn.obs_item_type_ref_obs_item_type_id  OWNED BY nrmn.obs_item_type_ref.obs_item_type_id;

ALTER TABLE nrmn.obs_item_type_ref
ALTER COLUMN obs_item_type_id SET DEFAULT nextval('nrmn.obs_item_type_ref_obs_item_type_id'),
ALTER COLUMN obs_item_type_name SET NOT NULL,
ADD CONSTRAINT obs_item_type_unique UNIQUE (obs_item_type_name)
;

/* observable_item_ref */
CREATE SEQUENCE nrmn.observable_item_ref_observable_item_id  OWNED BY nrmn.observable_item_ref.observable_item_id;

ALTER TABLE nrmn.observable_item_ref
ALTER COLUMN observable_item_id SET DEFAULT nextval('nrmn.observable_item_ref_observable_item_id'),
ALTER COLUMN observable_item_name SET NOT NULL,
ALTER COLUMN obs_item_type_id SET NOT NULL,
ADD CONSTRAINT obs_item_obs_item_type_fk FOREIGN KEY (obs_item_type_id) REFERENCES nrmn.obs_item_type_ref (obs_item_type_id),
ADD CONSTRAINT observable_item_unique UNIQUE (observable_item_name),
ADD CONSTRAINT obs_item_aphia_fk FOREIGN KEY (aphia_id) REFERENCES nrmn.aphia_ref (aphia_id),
ADD CONSTRAINT obs_item_aphia_rel_type_fk FOREIGN KEY (aphia_rel_type_id) REFERENCES nrmn.aphia_rel_type_ref (aphia_rel_type_id)
;

/* pq_resolution_ref */
CREATE SEQUENCE nrmn.pq_resolution_ref_resolution_id  OWNED BY nrmn.pq_resolution_ref.resolution_id;

ALTER TABLE nrmn.pq_resolution_ref
ALTER COLUMN resolution_id SET DEFAULT nextval('nrmn.pq_resolution_ref_resolution_id'),
ALTER COLUMN resolution_name SET NOT NULL,
ADD CONSTRAINT pq_resolution_unique UNIQUE (resolution_name)
;

/* pq_category_ref */
CREATE SEQUENCE nrmn.pq_category_ref_category_id  OWNED BY nrmn.pq_category_ref.category_id;

ALTER TABLE nrmn.pq_category_ref
ALTER COLUMN category_id SET DEFAULT nextval('nrmn.pq_category_ref_category_id'),
ALTER COLUMN major_category_name SET NOT NULL,
ALTER COLUMN description SET NOT NULL,
ADD CONSTRAINT pq_category_unique UNIQUE (description)
;

/* pq_cat_res_ref */
CREATE SEQUENCE nrmn.pq_cat_res_ref_cat_res_id  OWNED BY nrmn.pq_cat_res_ref.cat_res_id;

ALTER TABLE nrmn.pq_cat_res_ref
ALTER COLUMN cat_res_id SET DEFAULT nextval('nrmn.pq_cat_res_ref_cat_res_id'),
ALTER COLUMN resolution_id SET NOT NULL,
ALTER COLUMN category_id SET NOT NULL,
ADD CONSTRAINT cat_res_category_fk FOREIGN KEY (category_id) REFERENCES nrmn.pq_category_ref (category_id),
ADD CONSTRAINT cat_res_resolution_fk FOREIGN KEY (resolution_id) REFERENCES nrmn.pq_resolution_ref (resolution_id),
ADD CONSTRAINT pq_cat_res_unique UNIQUE (resolution_id, category_id)
;

/* pq_score */
CREATE SEQUENCE nrmn.pq_score_score_id  OWNED BY nrmn.pq_score.score_id;

ALTER TABLE nrmn.pq_score
ALTER COLUMN score_id SET DEFAULT nextval('nrmn.pq_score_score_id'),
ALTER COLUMN survey_method_id SET NOT NULL,
ALTER COLUMN fraction_coverage SET NOT NULL,
ALTER COLUMN cat_res_id SET NOT NULL,
ADD CONSTRAINT score_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method (survey_method_id),
ADD CONSTRAINT score_cat_res_fk FOREIGN KEY (cat_res_id) REFERENCES nrmn.pq_cat_res_ref (cat_res_id),
ADD CONSTRAINT pq_score_unique UNIQUE (survey_method_id, cat_res_id)
;

/* surface_type_ref */
CREATE SEQUENCE nrmn.surface_type_ref_surface_type_id  OWNED BY nrmn.surface_type_ref.surface_type_id;

ALTER TABLE nrmn.surface_type_ref
ALTER COLUMN surface_type_id SET DEFAULT nextval('nrmn.surface_type_ref_surface_type_id'),
ALTER COLUMN surface_type_name SET NOT NULL,
ADD CONSTRAINT surface_type_unique UNIQUE (surface_type_name)
;

/* rugosity */
CREATE SEQUENCE nrmn.rugosity_rugosity_id  OWNED BY nrmn.rugosity.rugosity_id;

ALTER TABLE nrmn.rugosity
ALTER COLUMN rugosity_id SET DEFAULT nextval('nrmn.rugosity_rugosity_id'),
ALTER COLUMN survey_method_id SET NOT NULL,
ADD CONSTRAINT rugosity_survey_method_fk FOREIGN KEY (survey_method_id) REFERENCES nrmn.survey_method (survey_method_id),
ADD CONSTRAINT rugosity_surface_type_fk FOREIGN KEY (surface_type_id) REFERENCES nrmn.surface_type_ref (surface_type_id),
ADD CONSTRAINT rugosity_unique UNIQUE (survey_method_id)
;

/* lengthweigth */
ALTER TABLE nrmn.lengthweight_ref
ADD CONSTRAINT lengthweight_observable_item_fk FOREIGN KEY (observable_item_id)
    REFERENCES nrmn.observable_item_ref (observable_item_id)
;

/*atrc_rugosity*/
ALTER TABLE nrmn.atrc_rugosity
ADD CONSTRAINT survey_atrc_rugosity_fk FOREIGN KEY (survey_id) REFERENCES nrmn.survey (survey_id)
;

/* public_data_exclusion */
ALTER TABLE nrmn.public_data_exclusion
ALTER COLUMN program_id SET NOT NULL,
ALTER COLUMN site_id SET NOT NULL,
ADD CONSTRAINT public_data_exclusion_program_fk FOREIGN KEY (program_id) REFERENCES nrmn.program_ref (program_id),
ADD CONSTRAINT public_data_exclusion_site_fk FOREIGN KEY (site_id) REFERENCES nrmn.site_ref (site_id)
;

/* method_species */
ALTER TABLE nrmn.methods_species
ADD CONSTRAINT observable_item_ref_fk FOREIGN KEY (observable_item_id)
REFERENCES nrmn.observable_item_ref (observable_item_id),
ADD CONSTRAINT method_ref_fk FOREIGN KEY (method_id) REFERENCES nrmn.method_ref (method_id)

