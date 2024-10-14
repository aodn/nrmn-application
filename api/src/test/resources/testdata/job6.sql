INSERT INTO nrmn.staged_job (id,
                            created,
                            is_extended_size,
                            last_updated,
                            reference,
                            source,
                            status,
                            survey_ids,
                            program_id,
                            sec_user_id)
VALUES(6,'2021-10-28 03:27:12.923+00',true,'2021-10-28 05:32:34.037+00','FRDC131 2019-2021 Reef Biodiversity Database.xlsx','INGEST','STAGED',null,55,123456);

-- Have not insert "Notolabrus tetricus" to test some missing case
INSERT INTO nrmn.staged_row (id,block,buddy,code,common_name,created,"date",depth,direction,diver,inverts,is_invert_sizing,last_updated,
                            latitude,longitude,measure_value,method,pqs,site_name,site_no,species,"time",total,vis,"position",staged_job_id)
VALUES(16615,2,'ESO','snd','Survey not Done','2021-10-28 03:27:16.241+00','7/02/2020',5.4,'W','SK',1,'No','2021-10-28 03:27:16.241+00',
        -43.10084,147.69253,'{}',2,0,'Creeses','SBIR20','Survey not Done','14:00',1,3,2772000,6),
      (13844,1,'SK','nfu','Purple wrasse','2021-10-28 14:27:16.039+11','6/03/2019',5.1,'NE','ATC',0,'No','2021-10-28 14:27:16.039+11',
        -43.07570409,147.3668048,'{"5":"1","6":"1","7":"2","8":"2","9":"1","10":"1"}',1,0,'Bull Bay North','TAS174','Notolabrus fucicola','9:00',8,5,1000,6),
      (13847,2,'SK','nte','Blue-throat wrasse','2021-10-28 14:27:16.039+11','6/03/2019',5.1,'NE','ATC',0,'No','2021-10-28 14:27:16.039+11',
        -43.07570409,147.3668048,'{"6":"1","7":"1","12":"1"}',1,0,'Bull Bay North','TAS174','Notolabrus tetricus','9:00',3,5,4000,6),
      (13852,1,'SK','lfo','Bastard trumpeter','2021-10-28 14:27:16.04+11','6/03/2019',5.1,'NE','ATC',0,'No','2021-10-28 14:27:16.04+11',
        -43.07570409,147.3668048,'{"12":"1"}',1,0,'Bull Bay North','TAS174','Latridopsis forsteri','9:00',1,5,9000,6);

INSERT INTO nrmn.observable_item_ref(observable_item_id,observable_item_name,obs_item_type_id,aphia_id,aphia_rel_type_id,common_name,superseded_by,
                                    phylum,class,"order",family,genus,species_epithet,report_group,habitat_groups,letter_code,is_invert_sized,obs_item_attribute,created,updated,mapped_id)
VALUES
        (68,'Notolabrus fucicola',1,281788,1,'Purple wrasse',null,'Chordata','Actinopterygii','Perciformes','Labridae','Notolabrus','fucicola',null,null,'NFU',false,'{"MaxLength": 38.0, "OtherGroups": "Benthic lower carnivore"}',null,null,58),
        (63,'Latridopsis forsteri',1,281278,1,'Bastard trumpeter',null,'Chordata','Actinopterygii','Perciformes','Latridae','Latridopsis','forsteri',null,null,'LFOR',false,'{"MaxLength": 65.0, "OtherGroups": "Benthic lower carnivore"}',null,null,53);

INSERT INTO nrmn.location_ref (location_id,location_name,is_active)
VALUES(34,'Tasmania - South East',true);

INSERT INTO nrmn.site_ref(site_id,site_code,site_name,longitude,latitude,geom,location_id,state,country,old_site_code,mpa,
                protection_status,relief,currents,wave_exposure,slope,site_attribute,is_active)
VALUES(4137,'SBIR20','Creeses',147.69253540039062,-43.1008415222168,'0101000020E6100000000000402976624000000060E88C45C0',34,'Tasmania',
        'Australia','{CR}',null,'Fishing',2,2,3,2,'{}',true),
      (711,'TAS174','Bull Bay North',147.3673095703125,-43.07366180419922,'0101000020E610000000000000C16B6240000000C06D8945C0',34,'Tasmania',
        'Australia','{1806,SBIR04,BBN}',null,'Fishing',3,1,3,2,'{"ProxCountry": "Australia"}',true);

-- Do not forget to commit
COMMIT;