CREATE TABLE nrmn.diver_ref (
  diver_id int,
  initials varchar(10),
  full_name varchar(100),
  PRIMARY KEY (diver_id)
);

CREATE TABLE nrmn.location_ref (
  location_id int,
  location_name varchar(100),
  is_active boolean NOT NULL,
  PRIMARY KEY (location_id)
);

CREATE TABLE nrmn.surface_type_ref (
  surface_type_id int,
  surface_type_name varchar(50),
  PRIMARY KEY (surface_type_id)
);

CREATE TABLE nrmn.observation (
  observation_id int,
  survey_method_id int,
  diver_id int,
  observable_item_id int,
  measure_id int,
  measure_value int,
  observation_attribute jsonb,
  PRIMARY KEY (observation_id)
);

CREATE TABLE nrmn.method_ref (
  method_id int,
  method_name varchar(100),
  is_active boolean NOT NULL,
  PRIMARY KEY (method_id)
);

CREATE TABLE nrmn.program_ref (
  program_id int,
  program_name varchar(100),
  is_active boolean NOT NULL,
  PRIMARY KEY (program_id)
);

CREATE TABLE nrmn.pq_resolution_ref (
  resolution_id int,
  resolution_name varchar(100),
  PRIMARY KEY (resolution_id)
);

CREATE TABLE nrmn.pq_cat_res_ref (
  cat_res_id int,
  resolution_id int,
  category_id int,
  PRIMARY KEY (cat_res_id)
);

CREATE TABLE nrmn.measure_type_ref (
  measure_type_id int,
  measure_type_name varchar(100),
  is_active boolean NOT NULL,
  PRIMARY KEY (measure_type_id)
);

CREATE TABLE nrmn.pq_score (
  score_id int,
  survey_method_id int,
  num_points int,
  fraction_coverage float,
  cat_res_id int,
  PRIMARY KEY (score_id)
);

CREATE TABLE nrmn.survey (
  survey_id int,
  site_id int,
  program_id int,
  survey_date date,
  survey_time time,
  depth int,
  survey_num int,
  visibility float,
  direction varchar(10),
  longitude float,
  latitude float,
  protection_status varchar(100),
  inside_marine_park varchar(50),
  notes varchar(1000),
  pq_catalogued boolean,
  pq_zip_url varchar(255),
  pq_diver_id int,
  block_abundance_simulated boolean,
  project_title varchar(100),
  PRIMARY KEY (survey_id)
);

CREATE TABLE nrmn.rugosity (
  rugosity_id int,
  survey_method_id int,
  avg_rugosity float,
  max_rugosity float,
  surface_type_id int,
  PRIMARY KEY (rugosity_id)
);

CREATE TABLE nrmn.obs_item_type_ref (
  obs_item_type_id int,
  obs_item_type_name varchar(100),
  is_active boolean NOT NULL,
  PRIMARY KEY (obs_item_type_id)
);

CREATE TABLE nrmn.aphia_rel_type_ref (
  aphia_rel_type_id int,
  aphia_rel_type_name varchar(50),
  PRIMARY KEY (aphia_rel_type_id)
);

CREATE TABLE nrmn.observable_item_ref (
  observable_item_id int,
  observable_item_name varchar(100),
  obs_item_type_id int,
  aphia_id int,
  aphia_rel_type_id int,
  common_name varchar(100),
  superseded_by varchar(100),
  phylum varchar(50),
  class varchar(50),
  "order" varchar(50),
  family varchar(50),
  genus varchar(50),
  species_epithet varchar(100),
  report_group varchar(50),
  habitat_groups varchar(50),
  letter_code varchar(20),
  is_invert_sized boolean default false,
  obs_item_attribute jsonb,
  PRIMARY KEY (observable_item_id)
);

CREATE TABLE nrmn.site_ref (
  site_id int,
  site_code varchar(10),
  site_name varchar(100),
  longitude float,
  latitude float,
  geom geometry,
  location_id int,
  state varchar(100),
  country varchar(100),
  old_site_code varchar(50)[],
  mpa varchar(200),
  protection_status varchar(100),
  relief int,
  currents int,
  wave_exposure int,
  slope int,
  site_attribute jsonb,
  is_active boolean NOT NULL DEFAULT false,
  PRIMARY KEY (site_id)
);

CREATE TABLE nrmn.survey_method (
  survey_method_id int,
  survey_id int,
  method_id int,
  block_num int,
  survey_not_done boolean,
  survey_method_attribute jsonb,
  PRIMARY KEY (survey_method_id)
);

CREATE TABLE nrmn.measure_ref (
  measure_id int,
  measure_type_id int,
  measure_name varchar(20),
  seq_no int,
  is_active boolean NOT NULL,
  PRIMARY KEY (measure_id)
);

CREATE TABLE nrmn.pq_category_ref (
  category_id int,
  major_category_name varchar(100),
  description varchar(100),
  PRIMARY KEY (category_id)
);

CREATE TABLE nrmn.aphia_ref (
  aphia_id int,
  url varchar(100),
  scientificname varchar(100),
  authority varchar(100),
  status varchar(100),
  unacceptreason varchar(200),
  taxon_rank_id int,
  rank varchar(50),
  valid_aphia_id int,
  valid_name varchar(100),
  valid_authority varchar(100),
  parent_name_usage_id int,
  rank_kingdom varchar(50),
  rank_phylum varchar(50),
  rank_class varchar(50),
  rank_order varchar(50),
  rank_family varchar(50),
  rank_genus varchar(50),
  citation text,
  lsid varchar(50),
  is_marine boolean,
  is_brackish boolean,
  is_freshwater boolean,
  is_terrestrial boolean,
  is_extinct boolean,
  match_type varchar(50),
  modified timestamp ,
  PRIMARY KEY (aphia_id)
);

CREATE TABLE nrmn.lengthweight_ref (
  observable_item_id int,
  a float,
  b float,
  cf float,
  sgfgu varchar(2),
  PRIMARY KEY (observable_item_id)
);

CREATE TABLE nrmn.atrc_rugosity (
  survey_id int,
  rugosity float
);

CREATE TABLE nrmn.legacy_common_names (
  rank varchar(6),
  name varchar(18),
  common_name varchar(32),
  PRIMARY KEY (name)
);

CREATE TABLE nrmn.meow_ecoregions (
  id int,
  ecoregion varchar(50),
  province varchar(40),
  realm varchar(40),
  lat_zone varchar(10),
  geom geometry(MultiPolygon,4326),
  PRIMARY KEY (id)
);

CREATE TABLE nrmn.public_data_exclusion (
  program_id integer NOT NULL,
  site_id integer NOT NULL,
  PRIMARY KEY (program_id, site_id)
);

CREATE TABLE nrmn.methods_species (
  observable_item_id integer NOT NULL,
  method_id integer NOT NULL,
  PRIMARY KEY(observable_item_id,method_id)
 );

ALTER TABLE nrmn.observable_item_ref ADD COLUMN created TIMESTAMP;
ALTER TABLE nrmn.observable_item_ref ADD COLUMN updated TIMESTAMP;