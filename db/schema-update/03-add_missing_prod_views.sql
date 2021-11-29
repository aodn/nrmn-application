CREATE OR REPLACE VIEW nrmn.ep_m0_off_transect_sighting_public AS
 SELECT epm0.survey_id,
    epm0.country,
    epm0.area,
    epm0.ecoregion,
    epm0.realm,
    epm0.location,
    epm0.site_code,
    epm0.site_name,
    round((epm0.latitude)::numeric, 2) AS latitude,
    round((epm0.longitude)::numeric, 2) AS longitude,
    epm0.survey_date,
    epm0.depth,
    public.st_setsrid(public.st_makepoint((round((epm0.latitude)::numeric, 2))::double precision, (round((epm0.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm0.program,
    epm0.visibility,
    epm0.hour,
    round((epm0.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm0.survey_longitude)::numeric, 2) AS survey_longitude,
    epm0.block,
    epm0.phylum,
    epm0.class,
    epm0."order",
    epm0.family,
    epm0.species_name,
    epm0.reporting_name,
    epm0.total
   FROM nrmn.ep_m0_off_transect_sighting epm0
  WHERE (NOT ((epm0.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m1_public AS
 SELECT epm1.survey_id,
    epm1.country,
    epm1.area,
    epm1.ecoregion,
    epm1.realm,
    epm1.location,
    epm1.site_code,
    epm1.site_name,
    round((epm1.latitude)::numeric, 2) AS latitude,
    round((epm1.longitude)::numeric, 2) AS longitude,
    epm1.survey_date,
    epm1.depth,
    public.st_setsrid(public.st_makepoint((round((epm1.latitude)::numeric, 2))::double precision, (round((epm1.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm1.program,
    epm1.visibility,
    epm1.hour,
    round((epm1.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm1.survey_longitude)::numeric, 2) AS survey_longitude,
    epm1.method,
    epm1.block,
    epm1.phylum,
    epm1.class,
    epm1."order",
    epm1.family,
    epm1.species_name,
    epm1.reporting_name,
    epm1.size_class,
    epm1.total,
    epm1.biomass
   FROM nrmn.ep_m1 epm1
  WHERE (((epm1.class)::text = ANY ((ARRAY['Actinopterygii'::character varying, 'Chondrichthyes'::character varying, 'Elasmobranchii'::character varying, 'Aves'::character varying, 'Mammalia'::character varying, 'Reptilia'::character varying, 'Cephalopoda'::character varying])::text[])) OR (((epm1.phylum)::text = ANY ((ARRAY['Cnidaria'::character varying, 'Ctenophora'::character varying])::text[])) AND (NOT ((epm1.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m2_cryptic_fish_public AS
 SELECT epm2cf.survey_id,
    epm2cf.country,
    epm2cf.area,
    epm2cf.ecoregion,
    epm2cf.realm,
    epm2cf.location,
    epm2cf.site_code,
    epm2cf.site_name,
    round((epm2cf.latitude)::numeric, 2) AS latitude,
    round((epm2cf.longitude)::numeric, 2) AS longitude,
    epm2cf.survey_date,
    epm2cf.depth,
    public.st_setsrid(public.st_makepoint((round((epm2cf.latitude)::numeric, 2))::double precision, (round((epm2cf.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm2cf.program,
    epm2cf.visibility,
    epm2cf.hour,
    round((epm2cf.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm2cf.survey_longitude)::numeric, 2) AS survey_longitude,
    epm2cf.method,
    epm2cf.block,
    epm2cf.phylum,
        CASE
            WHEN ((epm2cf.class)::text = 'Elasmobranchii'::text) THEN 'Chondrichthyes'::character varying
            ELSE epm2cf.class
        END AS class,
    epm2cf."order",
    epm2cf.family,
    epm2cf.species_name,
    epm2cf.reporting_name,
    epm2cf.size_class,
    epm2cf.total,
    epm2cf.biomass
   FROM nrmn.ep_m2_cryptic_fish epm2cf
  WHERE (((epm2cf.family)::text = ANY ((ARRAY['Agonidae'::character varying, 'Ambassidae'::character varying, 'Anarhichadidae'::character varying, 'Antennariidae'::character varying, 'Aploactinidae'::character varying, 'Apogonidae'::character varying, 'Ariidae'::character varying, 'Aulopidae'::character varying, 'Bathymasteridae'::character varying, 'Batrachoididae'::character varying, 'Blenniidae'::character varying, 'Bothidae'::character varying, 'Bovichtidae'::character varying, 'Brachaeluridae'::character varying, 'Brachionichthyidae'::character varying, 'Bythitidae'::character varying, 'Callionymidae'::character varying, 'Caracanthidae'::character varying, 'Carapidae'::character varying, 'Centriscidae'::character varying, 'Chaenopsidae'::character varying, 'Chironemidae'::character varying, 'Cirrhitidae'::character varying, 'Clinidae'::character varying, 'Congridae'::character varying, 'Congrogadidae'::character varying, 'Cottidae'::character varying, 'Creediidae'::character varying, 'Cryptacanthodidae'::character varying, 'Cyclopteridae'::character varying, 'Cynoglossidae'::character varying, 'Dasyatidae'::character varying, 'Diodontidae'::character varying, 'Eleotridae'::character varying, 'Gnathanacanthidae'::character varying, 'Gobiesocidae'::character varying, 'Gobiidae'::character varying, 'Grammistidae'::character varying, 'Hemiscylliidae'::character varying, 'Heterodontidae'::character varying, 'Holocentridae'::character varying, 'Hypnidae'::character varying, 'Labrisomidae'::character varying, 'Leptoscopidae'::character varying, 'Liparidae'::character varying, 'Lotidae'::character varying, 'Monocentridae'::character varying, 'Moridae'::character varying, 'Muraenidae'::character varying, 'Nototheniidae'::character varying, 'Ophichthidae'::character varying, 'Ophidiidae'::character varying, 'Opistognathidae'::character varying, 'Orectolobidae'::character varying, 'Paralichthyidae'::character varying, 'Parascylliidae'::character varying, 'Pataecidae'::character varying, 'Pegasidae'::character varying, 'Pempheridae'::character varying, 'Pholidae'::character varying, 'Pinguipedidae'::character varying, 'Platycephalidae'::character varying, 'Plesiopidae'::character varying, 'Pleuronectidae'::character varying, 'Plotosidae'::character varying, 'Priacanthidae'::character varying, 'Pseudochromidae'::character varying, 'Psychrolutidae'::character varying, 'Rajidae'::character varying, 'Rhinobatidae'::character varying, 'Scorpaenidae'::character varying, 'Serranidae'::character varying, 'Scyliorhinidae'::character varying, 'Soleidae'::character varying, 'Solenostomidae'::character varying, 'Stichaeidae'::character varying, 'Synanceiidae'::character varying, 'Syngnathidae'::character varying, 'Synodontidae'::character varying, 'Tetrabrachiidae'::character varying, 'Tetrarogidae'::character varying, 'Torpedinidae'::character varying, 'Trachichthyidae'::character varying, 'Tripterygiidae'::character varying, 'Uranoscopidae'::character varying, 'Urolophidae'::character varying, 'Zaproridae'::character varying, 'Zoarcidae'::character varying])::text[])) OR (((epm2cf.species_name)::text !~ similar_escape('(Trachinops|Anthias|Caesioperca|Lepidoperca)%'::text, NULL::text)) AND (NOT ((epm2cf.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m2_inverts_public AS
 SELECT epm2i.survey_id,
    epm2i.country,
    epm2i.area,
    epm2i.ecoregion,
    epm2i.realm,
    epm2i.location,
    epm2i.site_code,
    epm2i.site_name,
    round((epm2i.latitude)::numeric, 2) AS latitude,
    round((epm2i.longitude)::numeric, 2) AS longitude,
    epm2i.survey_date,
    epm2i.depth,
    public.st_setsrid(public.st_makepoint((round((epm2i.latitude)::numeric, 2))::double precision, (round((epm2i.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm2i.program,
    epm2i.visibility,
    epm2i.hour,
    round((epm2i.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm2i.survey_longitude)::numeric, 2) AS survey_longitude,
    epm2i.method,
    epm2i.block,
    epm2i.phylum,
    epm2i.class,
    epm2i."order",
    epm2i.family,
    epm2i.species_name,
    epm2i.reporting_name,
    epm2i.size_class,
    epm2i.total,
    epm2i.biomass
   FROM nrmn.ep_m2_inverts epm2i
  WHERE (NOT ((epm2i.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m3_isq_public AS
 SELECT epm3.survey_id,
    epm3.country,
    epm3.area,
    epm3.ecoregion,
    epm3.realm,
    epm3.location,
    epm3.site_code,
    epm3.site_name,
    round((epm3.latitude)::numeric, 2) AS latitude,
    round((epm3.longitude)::numeric, 2) AS longitude,
    epm3.survey_date,
    epm3.depth,
    public.st_setsrid(public.st_makepoint((round((epm3.latitude)::numeric, 2))::double precision, (round((epm3.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm3.program,
    epm3.visibility,
    epm3.hour,
    round((epm3.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm3.survey_longitude)::numeric, 2) AS survey_longitude,
    epm3.phylum,
    epm3.class,
    epm3."order",
    epm3.family,
    epm3.species_name,
    epm3.reporting_name,
    epm3.quadrat,
    epm3.total
   FROM nrmn.ep_m3_isq epm3
  WHERE (NOT ((epm3.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m4_macrocystis_count_public AS
 SELECT epm4.survey_id,
    epm4.country,
    epm4.area,
    epm4.ecoregion,
    epm4.realm,
    epm4.location AS site_code,
    epm4.site_name,
    round((epm4.latitude)::numeric, 2) AS latitude,
    round((epm4.longitude)::numeric, 2) AS longitude,
    epm4.survey_date,
    epm4.depth,
    public.st_setsrid(public.st_makepoint((round((epm4.latitude)::numeric, 2))::double precision, (round((epm4.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm4.program,
    epm4.visibility,
    epm4.hour,
    round((epm4.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm4.survey_longitude)::numeric, 2) AS survey_longitude,
    epm4.phylum,
    epm4.class,
    epm4."order",
    epm4.family,
    epm4.species_name,
    epm4.reporting_name,
    epm4.block,
    epm4.total
   FROM nrmn.ep_m4_macrocystis_count epm4
  WHERE (NOT ((epm4.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));

CREATE OR REPLACE VIEW nrmn.ep_m5_limpet_quadrats_public AS
 SELECT epm5.survey_id,
    epm5.country,
    epm5.area,
    epm5.ecoregion,
    epm5.realm,
    epm5.location AS site_code,
    epm5.site_name,
    round((epm5.latitude)::numeric, 2) AS latitude,
    round((epm5.longitude)::numeric, 2) AS longitude,
    epm5.survey_date,
    epm5.depth,
    public.st_setsrid(public.st_makepoint((round((epm5.latitude)::numeric, 2))::double precision, (round((epm5.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm5.program,
    epm5.visibility,
    epm5.hour,
    round((epm5.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm5.survey_longitude)::numeric, 2) AS survey_longitude,
    epm5.phylum,
    epm5.class,
    epm5."order",
    epm5.family,
    epm5.species_name,
    epm5.reporting_name,
    epm5.quadrat,
    epm5.total
   FROM nrmn.ep_m5_limpet_quadrats epm5
  WHERE (NOT ((epm5.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));

CREATE OR REPLACE VIEW nrmn.ep_m11_off_transect_measurement_public AS
 SELECT epm11.survey_id,
    epm11.country,
    epm11.area,
    epm11.ecoregion,
    epm11.realm,
    epm11.location AS site_code,
    epm11.site_name,
    round((epm11.latitude)::numeric, 2) AS latitude,
    round((epm11.longitude)::numeric, 2) AS longitude,
    epm11.survey_date,
    epm11.depth,
    public.st_setsrid(public.st_makepoint((round((epm11.latitude)::numeric, 2))::double precision, (round((epm11.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm11.program,
    epm11.visibility,
    epm11.hour,
    round((epm11.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm11.survey_longitude)::numeric, 2) AS survey_longitude,
    epm11.phylum,
    epm11.class,
    epm11."order",
    epm11.family,
    epm11.species_name,
    epm11.reporting_name,
    epm11.size_class,
    epm11.total
   FROM nrmn.ep_m11_off_transect_measurement epm11
  WHERE (NOT ((epm11.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_m13_pq_scores_public AS
 SELECT epm13.survey_id,
    epm13.country,
    epm13.area,
    epm13.ecoregion,
    epm13.realm,
    epm13.location AS site_code,
    epm13.site_name,
    round((epm13.latitude)::numeric, 2) AS latitude,
    round((epm13.longitude)::numeric, 2) AS longitude,
    epm13.survey_date,
    epm13.depth,
    public.st_setsrid(public.st_makepoint((round((epm13.latitude)::numeric, 2))::double precision, (round((epm13.longitude)::numeric, 2))::double precision), 4326) AS geom,
    epm13.program,
    epm13.visibility,
    epm13.hour,
    round((epm13.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epm13.survey_longitude)::numeric, 2) AS survey_longitude,
    epm13.resolution,
    epm13.category,
    epm13.major_category,
    epm13.num_points,
    epm13.total_points,
    epm13.percent_cover
   FROM nrmn.ep_m13_pq_scores epm13
  WHERE (((epm13.category)::text <> 'Tape'::text) AND (NOT ((epm13.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id)))))));
			 
CREATE OR REPLACE VIEW nrmn.ep_survey_list_public AS
 SELECT epsl.survey_id,
    epsl.country,
    epsl.area,
    epsl.location,
    epsl.mpa,
    epsl.site_code,
    epsl.site_name,
    round((epsl.latitude)::numeric, 2) AS latitude,
    round((epsl.longitude)::numeric, 2) AS longitude,
    epsl.depth,
    epsl.survey_date,
    epsl."latest surveydate for site",
    epsl."has pq scores in db",
    epsl."has rugosity scores in db",
    epsl."has pqs catalogued in db",
    epsl.visibility,
    epsl.hour,
        CASE
            WHEN ((epsl.direction)::text ~* '(N|S|W|E|NE|SE|SW|NW|NNE|ENE|ESE|SSE|SSW|WSW|WNW|NNW)'::text) THEN epsl.direction
            WHEN ((epsl.direction)::text ~* '(east|west|north|south)'::text) THEN epsl.direction
            ELSE NULL::character varying
        END AS direction,
    round((epsl.survey_latitude)::numeric, 2) AS survey_latitude,
    round((epsl.survey_longitude)::numeric, 2) AS survey_longitude,
    epsl.avg_rugosity,
    epsl.max_rugosity,
    epsl.surface,
    public.st_setsrid(public.st_makepoint((round((epsl.longitude)::numeric, 2))::double precision, (round((epsl.latitude)::numeric, 2))::double precision), 4326) AS geom,
    epsl.program,
    epsl.pq_zip_url,
    epsl.protection_status,
    epsl.old_site_codes,
    epsl.methods,
    epsl.survey_notes
   FROM nrmn.ep_survey_list epsl
  WHERE (NOT ((epsl.site_code)::text IN ( SELECT site_ref.site_code
           FROM (nrmn.public_data_exclusion
             JOIN nrmn.site_ref ON ((site_ref.site_id = public_data_exclusion.site_id))))));
