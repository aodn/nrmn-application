INSERT INTO nrmn.survey

INSERT INTO nrmn.program_ref(program_id, program_name, is_active)
    VALUES (1, 'RLS', True);

INSERT INTO nrmn.location_ref (location_id, location_name, is_active)
    VALUES (184, '"Lord Howe Island"', true);

INSERT INTO nrmn.site_ref(site_id,site_code,site_name,longitude,latitude,geom,location_id,state,
    country,old_site_code,mpa,protection_status,relief,currents,wave_exposure,slope,site_attribute,is_active)
VALUES (3834,'LHI37','Malabar 2',159.05615,-31.5113,'0101000020E610000074B515FBCBE16340DE718A8EE4823FC0',
        184,'New South Wales','Australia',null,'Lord Howe Island Marine Park','No take multizoned',4,3,3,2,
        '{
            "Age": "1",
            "Zone":"Neds Beach and Admiralty Islands Sanctuary Zone",
            "area":"0",
            "No_take":"1",
            "Isolation":1.0,
            "Zone_name":"Neds Beach and Admiralty Islands Sanctuary Zone",
            "NEOLI_Total":"3",
            "ProxCountry":"Australia",
            "area_in_km2":"4.8",
            "Effectiveness":"High",
            "Gears_allowed":"Allbanned",
            "Isolation_NEOLI":"0",
            "year_of_protection":"2004",
            "Effectiveness_NEOLI":"1",
            "Shore_fishing_index":"1.0",
            "Distance_to_boat_ramp": "5.5",
            "Rec_methods_permitted":"-",
            "com_methods_permitted":"-",
            "offshore_extent_in_km":"1.5",
            "Is_rec_fishing_allowed":
            "N","perimeter_length_in_km":"9.1",
            "Distance_to_boundary_in_km":"0.46",
            "Is_commercial_fishing_allowed":"N",
            "Is_it_a_shore_rec_fishing_zone_within_SZ":"N"
          }',
          true),
        (3844,'LHI38','North Bay 2',159.04688000000002,-31.52113,'0101000020E6100000C55A7C0A80E16340E8F692C668853FC0',
         184,'New South Wales','Australia',null,'Lord Howe Island Marine Park','No take multizoned',3,1,1,1,
         '{"Age": "1", "Zone": "North Bay Sanctuary Zone", "area": "0", "No_take": "1", "Isolation": 1.0, "Zone_name": "North Bay Sanctuary Zone", "NEOLI_Total": "3", "ProxCountry": "Australia", "area_in_km2": "0.5", "Effectiveness": "High", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "0", "year_of_protection": "2004", "Effectiveness_NEOLI": "1", "Shore_fishing_index": 1.0, "Distance_to_boat_ramp": 1.2, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 0.5, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 3.1, "Distance_to_boundary_in_km": 0.2, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}',
         true);

INSERT INTO nrmn.survey (survey_id,site_id,program_id,survey_date,survey_time,depth,survey_num,
    visibility,direction,longitude,latitude,protection_status,inside_marine_park,notes,pq_catalogued,pq_zip_url,pq_diver_id,
    block_abundance_simulated,project_title,created,updated,locked)
VALUES (912351270,3834,1,'2008-02-27',NULL,10,0,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False),
    (912351271,3844,1,'2008-02-27',NULL,1,3,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False),
    (912351272,3844,1,'2008-02-27',NULL,1,5,NULL,'',NULL,NULL,NULL,NULL,NULL,False,'None',NULL,False,NULL,NULL,NULL,False);