begin transaction;

delete from nrmn.site_ref where site_id = 0;

delete from nrmn.location_ref where location_id = 0;

drop SEQUENCE if EXISTS nrmn.survey_survey_id_m0;

insert into nrmn.location_ref (location_id, location_name, is_active) values (0, 'NONE', true);

insert into nrmn.site_ref (site_id, site_name, site_code, location_id, is_active) values (0, 'NONE', 'NONE', 0, true);

CREATE SEQUENCE nrmn.survey_survey_id_m0 START WITH 903400000 INCREMENT BY 1 NO MINVALUE MAXVALUE 923399999 CACHE 1;

end;
