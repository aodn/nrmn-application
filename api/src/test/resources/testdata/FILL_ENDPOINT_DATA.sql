insert into nrmn.diver_ref (diver_id, initials, full_name, created)
values  (155, 'DH', 'Dave Henke', null),
        (188, 'GJE', 'Graham Edgar', null),
        (208, 'IVS', 'Ian Shaw', null),
        (248, 'JT', 'John Turnbull', null),
        (268, 'KW', 'Kirsty Whitman', null),
        (276, 'LH', 'Leah Harper', null),
        (315, 'NB', 'Nacor Balanos', null),
        (327, 'NSB', 'Nev Barrett', null),
        (366, 'RSS', 'Rick Stuart-Smith', null),
        (378, 'SDL', 'Scott Ling', null),
        (1, '0', 'Unknown diver', null),
        (115, 'ATC', 'Antonia Cooper', null),
        (437, 'OJ', 'Olivia Johnson', null);

insert into nrmn.location_ref (location_id, location_name, is_active)
values  (10, 'Sydney', true),
        (43, 'Port Davey', true),
        (55, 'Port Phillip Heads', true),
        (67, 'Ashmore/Cartier', true),
        (156, 'Bonaire', true),
        (162, 'Coral Sea - Central', true),
        (165, 'Coral Sea - Far North', true),
        (171, 'Capricorn-Bunker', true),
        (70, 'Rottnest Island', true),
        (21, 'Torres Strait', true),
        (196, 'Solitary Islands', true),
        (195, 'Seaflower', true);

insert into nrmn.method_ref (method_id, method_name, is_active)
values  (0, 'Off transect sightings or observations', true);

insert into nrmn.site_ref (site_id, site_code, site_name, longitude, latitude, geom, location_id, state, country, old_site_code, mpa, protection_status, relief, currents, wave_exposure, slope, site_attribute, is_active)
values  (4250, 'GBR287', 'Westari', 151.8633270263672, -23.460689544677734, '0101000020E610000000000060A0FB6240000000C0EF7537C0', 171, 'Queensland', 'Australia', null, 'Great Barrier Reef MP', 'No Take', null, null, null, null, null, true),
        (4254, 'GBR285', 'Tenements 2', 151.93174743652344, -23.433340072631836, '0101000020E6100000000000E0D0FD624000000060EF6E37C0', 171, 'Queensland', 'Australia', null, 'Great Barrier Reef MP', 'No Take', null, null, null, null, null, true),
        (4251, 'GBR288', 'Fourth Point', 151.98126220703125, -23.437339782714844, '0101000020E61000000000008066FF624000000080F56F37C0', 171, 'Queensland', 'Australia', null, 'Great Barrier Reef MP', 'Restricted Take', null, null, null, null, null, true),
        (4263, 'GBR298', 'OTI10 - Bendy reef', 152.08180236816406, -23.503429412841797, '0101000020E6100000000000209E026340000000C0E08037C0', 171, 'Queensland', 'Australia', null, null, null, null, null, null, null, null, true),
        (1148, 'PPH-S12', 'Annulus (Popes Eye)', 144.69762, -38.2767, '0101000020E610000023A12DE752166240CF66D5E76A2343C0', 55, 'Victoria', 'Australia', '{16PPB, PPB3, ESO6_166,2812}', 'Port Phillip Heads Marine National Park', 'No take multizoned', 4, 4, 2, 3, '{"Age": "0", "Zone": "Popes Eye", "area": "0", "No_take": "1", "Isolation": 3.0, "Zone_name": "Popes Eye", "NEOLI_Total": "2", "ProxCountry": "Australia", "area_in_km2": "0.02", "Effectiveness": "High", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "0", "year_of_protection": "1981", "Effectiveness_NEOLI": "1", "Distance_to_boat_ramp": 2.48, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 0.15, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 0.6, "Distance_to_boundary_in_km": 0.04, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (1441, 'NWS189', 'Cartier Garden', 123.57215, -12.525269999999999, '0101000020E6100000029A081B9EE45E40CC237F30F00C29C0', 67, 'Western Australia', 'Australia', null, 'Ashmore AMP', 'No take multizoned', 3, 4, 3, 3, '{"Isolation": 4.0, "Zone_name": "Sanctuary Zone", "ProxCountry": "Indonesia", "area_in_km2": "172.37", "Effectiveness": "High", "year_of_protection": "2000", "Distance_to_boundary_in_km": 5.5}', true),
        (3010, 'CS134', 'Chilcott Islet inshore reef', 150.00092, -16.9366, '0101000020E6100000323D618907C06240006F8104C5EF30C0', 162, 'Queensland', 'Australia', null, 'Coral Sea AMP', 'No take multizoned', 2, 2, 2, 3, '{"Age": "1", "Zone": "Coringa-Herald Nature Reserve", "area": "1", "No_take": "1", "Comments": "Based on the pdf maps from 2018 and the kmz from the GBR this big no-take area seems to be much larger. Original value you had was 8852", "Isolation": 5.0, "Zone_name": "Coringa-Herald Nature Reserve", "NEOLI_Total": "4", "ProxCountry": "Australia", "area_in_km2": "60489", "Effectiveness": "Medium", "Isolation_NEOLI": "1", "year_of_protection": "1982", "Effectiveness_NEOLI": "0", "Distance_to_boat_ramp": 424.68, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "Is_rec_fishing_allowed": "N", "Distance_to_boundary_in_km": 42.0, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (3153, 'CS191', 'Ashmore Rf Lagoon East Bommie', 144.53391000000002, -10.37448, '0101000020E61000003DA06CCA151162401D03B2D7BBBF24C0', 165, 'Queensland', 'Australia', null, 'Coral Sea AMP', 'Restricted take multizoned', 3, 3, 2, 4, '{"Zone": "Habitat Protection Zone", "Zone_name": "Habitat Protection Zone", "ProxCountry": "Papua New Guinea", "area_in_km2": "11963", "Effectiveness": "Medium", "Gears_allowed": "MT", "year_of_protection": "2018", "Distance_to_boat_ramp": 200.0, "Rec_methods_permitted": "ALL", "com_methods_permitted": "Ls, D, Ns, TR, MT", "Is_rec_fishing_allowed": "A", "Is_commercial_fishing_allowed": "S", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (3402, 'GBR12', 'Wistari Reef south west', 151.85275, -23.46521, '0101000020E61000003F355EBA49FB6240ACC5A700187737C0', 171, 'Queensland', 'Australia', null, 'Great Barrier Reef MP', 'Restricted take multizoned', 3, 3, 3, 3, '{"Zone": "CP-23-4106", "Zone_name": "CP-23-4106", "ProxCountry": "Australia", "area_in_km2": "191.96", "Effectiveness": "High", "Gears_allowed": "N", "Isolation_NEOLI": "1", "year_of_protection": "2004", "Distance_to_boat_ramp": 73.0, "Rec_methods_permitted": "N, T, D, S, L, TR", "com_methods_permitted": "N, T, D, S, L, TR", "offshore_extent_in_km": 6.32, "Is_rec_fishing_allowed": "S", "perimeter_length_in_km": 62.5, "Distance_to_boundary_in_km": -3.0, "Is_commercial_fishing_allowed": "S", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (4021, 'CAR17', 'Montanita', -81.71718, 12.58681, '0101000020E61000001956F146E66D54C078EE3D5C722C2940', 195, 'Archipelago of San Andres, Providencia and Santa Catalina', 'Colombia', null, 'Seaflower Area Marina Protegida', 'No take multizoned', 3, 2, 3, 1, '{"Age": "0", "Zone": "San Andres N", "area": "1", "No_take": "0", "Isolation": 1.0, "Zone_name": "San Andres N", "NEOLI_Total": "2", "ProxCountry": "Colombia", "area_in_km2": "25", "Effectiveness": "Low", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "1", "year_of_protection": "2005", "Effectiveness_NEOLI": "0", "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 2.0, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 40.0, "Distance_to_boundary_in_km": 0.5, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (4022, 'CAR16', 'Wild Life', -81.73215, 12.518839999999999, '0101000020E61000000D71AC8BDB6E54C099B67F65A5092940', 195, 'Archipelago of San Andres, Providencia and Santa Catalina', 'Colombia', null, 'Seaflower Area Marina Protegida', 'No take multizoned', 2, 2, 3, 1, '{"Age": "0", "Zone": "San Andres S", "area": "1", "No_take": "0", "Isolation": 1.0, "Zone_name": "San Andres S", "NEOLI_Total": "2", "ProxCountry": "Colombia", "area_in_km2": "7", "Effectiveness": "Low", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "1", "year_of_protection": "2005", "Effectiveness_NEOLI": "0", "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 0.7, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 27.0, "Distance_to_boundary_in_km": 0.03, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (145, 'SYD15', 'Gunamatta Baths', 151.14834, -34.05903, '0101000020E610000009168733BFE46240CFBD874B8E0741C0', 10, 'New South Wales', 'Australia', '{1PH}', null, 'Fishing', 2, 1, 1, 2, '{"ProxCountry": "Australia", "Gears_allowed": "Allperm", "Shore_fishing_index": 4.0, "Distance_to_boat_ramp": 0.3, "Rec_methods_permitted": "ALL", "com_methods_permitted": "ALL", "Is_rec_fishing_allowed": "A", "Is_commercial_fishing_allowed": "A", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (3163, 'CS189', 'Ashmore Rf Lagoon SW', 144.43347, -10.44697, '0101000020E610000082397AFCDE0D624014967840D9E424C0', 165, 'Queensland', 'Australia', null, 'Coral Sea AMP', 'Restricted take multizoned', 3, 3, 2, 1, '{"Zone": "Habitat Protection Zone", "Zone_name": "Habitat Protection Zone", "ProxCountry": "Australia", "area_in_km2": "11963", "Effectiveness": "Medium", "Gears_allowed": "MT", "year_of_protection": "2018", "Distance_to_boat_ramp": 200.0, "Rec_methods_permitted": "ALL", "com_methods_permitted": "Ls, D, Ns, TR, MT", "Is_rec_fishing_allowed": "A", "Is_commercial_fishing_allowed": "S", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (958, 'PD-S29', 'Sarah Island', 145.99501, -43.33056, '0101000020E61000002D26361FD73F6240D1AE42CA4FAA45C0', 43, 'Tasmania', 'Australia', '{1501,BH-S1}', 'Port Davey National Park', 'No take multizoned', 3, 4, 1, 3, '{"Isolation": 1.0, "Zone_name": "Bathurst Harbour", "ProxCountry": "Australia", "area_in_km2": "70", "Effectiveness": "High", "year_of_protection": "2001", "Distance_to_boundary_in_km": 2.3}', true),
        (2801, 'BON1', 'Vista Blue', -68.25981999999999, 12.03208, '0101000020E610000030B610E4A01051C01CB62DCA6C102840', 156, 'Bonaire', 'Netherlands Antilles', null, 'Bonaire', 'Restricted take', 3, 1, 2, 3, '{"Age": "1", "Zone": "Marine Park", "area": "0", "No_take": "0", "Zone_name": "Marine Park", "NEOLI_Total": "3", "ProxCountry": "Netherlands Antilles", "area_in_km2": "27", "Effectiveness": "High", "Gears_allowed": "H", "Isolation_NEOLI": "1", "year_of_protection": "1979", "Effectiveness_NEOLI": "1", "com_methods_permitted": "not clear. Spearfishing not allowed.", "offshore_extent_in_km": 0.5, "perimeter_length_in_km": 110.0, "Distance_to_boundary_in_km": -0.4}', true),
        (2805, 'BON7', 'Windsock', -68.28501, 12.13128, '0101000020E61000001C42959A3D1251C0DE3CD52137432840', 156, 'Bonaire', 'Netherlands Antilles', null, 'Bonaire', 'Restricted take', 2, 1, 2, 3, '{"Age": "1", "Zone": "Marine Park", "area": "0", "No_take": "0", "Zone_name": "Marine Park", "NEOLI_Total": "3", "ProxCountry": "Netherlands Antilles", "area_in_km2": "27", "Effectiveness": "High", "Gears_allowed": "H", "Isolation_NEOLI": "1", "year_of_protection": "1979", "Effectiveness_NEOLI": "1", "com_methods_permitted": "not clear. Spearfishing not allowed.", "offshore_extent_in_km": 0.5, "perimeter_length_in_km": 110.0, "Distance_to_boundary_in_km": -0.4}', true),
        (1509, 'RI5', 'Crystal palace', 115.54513999999999, -32.02498, '0101000020E610000073EFE192E3E25C40EC866D8B320340C0', 70, 'Western Australia', 'Australia', '{ DL13,5RI}', 'Rottnest Island Marine Reserve', 'Restricted take multizoned', 4, 1, 3, 2, '{"Age": "0", "Zone": "Reserve (Rottnest Island)", "area": "0", "No_take": "1", "Zone_name": "Reserve (Rottnest Island)", "NEOLI_Total": "2", "ProxCountry": "Australia", "area_in_km2": "0.89", "Effectiveness": "High", "Gears_allowed": "BT", "Isolation_NEOLI": "0", "year_of_protection": "2007", "Effectiveness_NEOLI": "1", "Distance_to_boat_ramp": 22.35, "Rec_methods_permitted": "ALL", "com_methods_permitted": "ALL", "offshore_extent_in_km": 0.45, "Is_rec_fishing_allowed": "A", "perimeter_length_in_km": 5.5, "Distance_to_boundary_in_km": -1.2, "Is_commercial_fishing_allowed": "A", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (4026, 'SI2', 'Anemone Bay N Solitary Is', 153.38808999999998, -29.923140000000004, '0101000020E6100000F23CB83B6B2C634024A12DE752EC3DC0', 196, 'New South Wales', 'Australia', '{2SI}', 'Solitary Islands Marine Park', 'No take multizoned', 3, 2, 3, 2, '{"Age": "0", "Zone": "North Solitary", "area": "0", "No_take": "1", "Isolation": 1.0, "Zone_name": "North Solitary", "NEOLI_Total": "2", "ProxCountry": "Australia", "area_in_km2": "0.5", "Effectiveness": "High", "Gears_allowed": "Allbanned", "Isolation_NEOLI": "0", "year_of_protection": "2002", "Effectiveness_NEOLI": "1", "Distance_to_boat_ramp": 11.9, "Rec_methods_permitted": "-", "com_methods_permitted": "-", "offshore_extent_in_km": 0.25, "Is_rec_fishing_allowed": "N", "perimeter_length_in_km": 4.0, "Distance_to_boundary_in_km": 0.12, "Is_commercial_fishing_allowed": "N", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true),
        (343, 'TS8', 'Seven Reefs Anchorage', 143.70978, -10.2875, '0101000020E61000005BEB8B84B6F6614033333333339324C0', 21, 'Queensland', 'Australia', '{GBR52}', null, 'Fishing', 4, 3, 2, 2, '{"ProxCountry": "Australia", "Distance_to_boat_ramp": 147.0, "Rec_methods_permitted": "ALL", "com_methods_permitted": "ALL", "Is_rec_fishing_allowed": "A", "Is_commercial_fishing_allowed": "A", "Is_it_a_shore_rec_fishing_zone_within_SZ": "N"}', true);

insert into nrmn.observable_item_ref (observable_item_id, observable_item_name, obs_item_type_id, aphia_id, aphia_rel_type_id, common_name, superseded_by, phylum, class, "order", family, genus, species_epithet, report_group, habitat_groups, letter_code, is_invert_sized, obs_item_attribute, created, updated, mapped_id)
values  (810, 'Apogon doederleini', 1, 273008, 1, 'Four-line cardinalfish', 'Ostorhinchus doederleini', 'Chordata', 'Actinopterygii', 'Perciformes', 'Apogonidae', 'Apogon', 'doederleini', null, null, 'ADOE', false, '{"MaxLength": "14.0", "OtherGroups": "Benthic,Planktivore"}', null, '2023-04-21 00:03:09.149887', 1438),
        (1526, 'Asperaxis karenae', 1, 289460, 1, 'Bramble coral', null, 'Cnidaria', 'Anthozoa', 'Alcyonacea', 'Melithaeidae', 'Asperaxis', 'karenae', null, null, 'AKA', false, '{}', null, null, 2267),
        (2367, 'Arenigobius frenatus', 1, 313758, 1, 'Halfbridled Goby', null, 'Chordata', 'Actinopterygii', 'Perciformes', 'Gobiidae', 'Arenigobius', 'frenatus', null, null, null, false, '{"MaxLength": 18.0}', null, null, 3182),
        (3069, 'Acanthurus spp.', 1, 125908, 1, '[A surgeonfish]', null, 'Chordata', 'Actinopterygii', 'Perciformes', 'Acanthuridae', 'Acanthurus', null, null, null, null, false, '{"MaxLength": 36.33333333}', null, null, 3888),
        (3762, 'Acanthostracion polygonius', 1, 158919, 1, 'Honeycomb cowfish', null, 'Chordata', 'Actinopterygii', 'Tetraodontiformes', 'Ostraciidae', 'Acanthostracion', 'polygonius', null, null, null, false, '{"MaxLength": 50.0}', null, null, 4584),
        (4935, 'Cypraea annulus', 1, 216777, 1, null, 'Monetaria annulus', 'Mollusca', 'Gastropoda', 'Littorinimorpha', 'Cypraeidae', null, null, null, null, null, true, '{}', null, null, 5861),
        (6285, 'Phalacrocorax varius', 1, 225940, 1, 'Australian pied cormorant', null, 'Chordata', 'Aves', 'Ciconiiformes', 'Phalacrocoracidae', 'Phalacrocorax', 'varius', null, null, null, false, '{}', null, null, 7629),
        (6820, 'Acanthurus sp. [pyroferus]', 2, 125908, 2, '[A surgeonfish]', null, 'Chordata', 'Actinopterygii', 'Perciformes', 'Acanthuridae', 'Acanthurus', null, null, null, null, false, '{"MaxLength": 25.0}', null, null, 5206),
        (8027, 'Tripneustes kermadecensis', 1, 1075052, null, '', null, 'Echinodermata', 'Echinoidea', 'Camarodonta', 'Toxopneustidae', 'Tripneustes', 'kermadecensis', '', '', '', true, null, '2022-07-04 05:28:55.950040', '2022-07-04 05:28:55.950046', 99908027),
        (8114, 'Ostorhinchus doederleini', 1, 713303, null, 'Four lined cardinalfish', null, 'Chordata', 'Actinopterygii', 'Perciformes', 'Apogonidae', 'Ostorhinchus', 'doederleini', '', '', '', null, null, '2023-04-21 00:02:04.687858', '2023-06-03 03:39:16.943911', 99908114);

INSERT INTO nrmn.lengthweight_ref (observable_item_id, a, b, cf, sgfgu)
VALUES
    (8114,0.009,3.46,0.9416196,null),
    (810,0.009,3.46,0.9416196,null),
    (3762,0.0517,2.679,0.812677773,'S'),
    (2367,0.013,2.882,1,'F'),
    (6820,0.0251,3.032,0.919399325,'G'),
    (3069,0.0251,3.032,0.919399325,'G'),
    (8027,0.0213,3.152,0.859845228,'G'),
    (4935,0.0063,3.217,1,'G');

insert into nrmn.aphia_ref (aphia_id, url, scientificname, authority, status, unacceptreason, taxon_rank_id, rank, valid_aphia_id, valid_name, valid_authority, parent_name_usage_id, rank_kingdom, rank_phylum, rank_class, rank_order, rank_family, rank_genus, citation, lsid, is_marine, is_brackish, is_freshwater, is_terrestrial, is_extinct, match_type, modified)
values  (158919, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=158919', 'Acanthostracion polygonius', 'Poey, 1876', 'accepted', null, 220, 'Species', 158919, 'Acanthostracion polygonius', 'Poey, 1876', 126237, 'Animalia', 'Chordata', 'Actinopteri', 'Tetraodontiformes', 'Ostraciidae', 'Acanthostracion', 'Froese, R. and D. Pauly. Editors. (2021). FishBase. Acanthostracion polygonius Poey, 1876. Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=158919 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:158919', true, false, false, false, null, 'exact', '2008-01-15 17:27:08.177000'),
        (125908, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=125908', 'Acanthurus', 'Forsskål, 1775', 'accepted', null, 180, 'Genus', 125908, 'Acanthurus', 'Forsskål, 1775', 125515, 'Animalia', 'Chordata', 'Actinopteri', 'Acanthuriformes', 'Acanthuridae', 'Acanthurus', 'Froese, R. and D. Pauly. Editors. (2021). FishBase. Acanthurus Forsskål, 1775. Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=125908 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:125908', true, true, false, false, null, 'exact', '2014-12-02 12:48:54.943000'),
        (273008, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=273008', 'Apogon doederleini', 'Jordan & Snyder, 1901', 'unaccepted', null, 220, 'Species', 713303, 'Ostorhinchus doederleini', '(Jordan & Snyder, 1901)', 125913, 'Animalia', 'Chordata', 'Actinopteri', 'Kurtiformes', 'Apogonidae', 'Apogon', 'Froese, R. and D. Pauly. Editors. (2021). FishBase. Apogon doederleini Jordan & Snyder, 1901. Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=273008 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:273008', true, false, false, false, null, 'exact', '2020-04-23 08:42:40.887000'),
        (313758, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=313758', 'Arenigobius frenatus', '(Günther, 1861)', 'accepted', null, 220, 'Species', 313758, 'Arenigobius frenatus', '(Günther, 1861)', 268378, 'Animalia', 'Chordata', 'Actinopteri', 'Gobiiformes', 'Gobiidae', 'Arenigobius', 'Froese, R. and D. Pauly. Editors. (2021). FishBase. Arenigobius frenatus (Günther, 1861). Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=313758 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:313758', true, true, false, false, null, 'exact', '2009-06-15 13:59:21.393000'),
        (289460, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=289460', 'Asperaxis karenae', 'Alderslade, 2006', 'accepted', null, 220, 'Species', 289460, 'Asperaxis karenae', 'Alderslade, 2006', 267239, 'Animalia', 'Cnidaria', 'Anthozoa', 'Alcyonacea', 'Melithaeidae', 'Asperaxis', 'Cordeiro, R.; McFadden, C.; van Ofwegen, L.; Williams, G. (2021). World List of Octocorallia. Asperaxis karenae Alderslade, 2006. Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=289460 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:289460', true, null, false, false, null, 'exact', '2016-10-04 13:25:44.113000'),
        (216777, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=216777', 'Cypraea annulus', 'Linnaeus, 1758', 'unaccepted', 'original combination', 220, 'Species', 216875, 'Monetaria annulus', '(Linnaeus, 1758)', 205978, 'Animalia', 'Mollusca', 'Gastropoda', 'Littorinimorpha', 'Cypraeidae', 'Cypraea', 'MolluscaBase eds. (2021). MolluscaBase. Cypraea annulus Linnaeus, 1758. Accessed through: World Register of Marine Species at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=216777 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:216777', true, null, null, null, null, 'exact', '2011-09-25 13:40:55.867000'),
        (225940, 'http://www.marinespecies.org/aphia.php?p=taxdetails&id=225940', 'Phalacrocorax varius', '(Gmelin, 1789)', 'accepted', null, 220, 'Species', 225940, 'Phalacrocorax varius', '(Gmelin, 1789)', 137054, 'Animalia', 'Chordata', 'Aves', 'Pelecaniformes', 'Phalacrocoracidae', 'Phalacrocorax', 'WoRMS (2021). Phalacrocorax varius (Gmelin, 1789). Accessed at: http://www.marinespecies.org/aphia.php?p=taxdetails&id=225940 on 2021-08-18', 'urn:lsid:marinespecies.org:taxname:225940', true, null, null, null, null, 'exact', '2011-04-27 09:01:37.467000');

insert into nrmn.survey (survey_id, site_id, program_id, survey_date, survey_time, depth, survey_num, visibility, direction, longitude, latitude, protection_status, inside_marine_park, notes, pq_catalogued, pq_zip_url, pq_diver_id, block_abundance_simulated, project_title, created, updated, locked)
values  (923401736, 4263, 55, '2023-05-08', '10:30:00', 2, 0, 10, 'SE', 152.0818, -23.50343, null, 'No', null, true, 'http://rls.tpac.org.au/pq/923401736/zip/', 248, null, null, '2023-05-17 22:57:41.366000', '2023-05-19 14:00:10.634000', false),
        (923401762, 4250, 55, '2022-06-03', '10:00:00', 6, 5, 18, 'E', 151.86332, -23.46069, 'No Take', 'Yes', null, null, null, 378, null, null, '2023-06-15 07:56:35.971000', '2023-06-15 07:56:35.971000', false),
        (923401763, 4254, 55, '2022-06-01', '14:27:00', 6, 0, 15, 'W', 151.93174, -23.43334, 'No Take', 'Yes', null, null, null, 437, null, null, '2023-06-15 07:57:35.344000', '2023-06-15 07:57:35.344000', false),
        (923401764, 3402, 55, '2022-06-02', '14:00:00', 8, 0, 30, 'SW', 151.85275, -23.46521, 'Restricted take multizoned', 'Yes', null, null, null, 437, null, null, '2023-06-15 07:57:58.723000', '2023-06-15 07:57:58.723000', false),
        (923401769, 4251, 55, '2022-06-04', '12:30:00', 8, 5, 15, 'N', 151.98126, -23.43734, 'Restricted Take', 'Yes', null, null, null, 437, null, null, '2023-06-15 08:01:23.986000', '2023-06-15 08:01:23.986000', false),
        (923400975, 2805, 55, '2022-06-12', '09:00:00', 13, 5, 25, 'N', -68.28501, 12.13128, 'Restricted take', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400975/zip/', 276, null, null, null, '2023-04-19 14:00:04.660000', false),
        (812321941, 958, 56, '2009-04-27', null, 5, 1, null, null, null, null, 'No Take', 'Yes', null, false, 'None', null, true, 'ARC rare species project- ARC', null, null, false),
        (812321942, 958, 56, '2009-04-27', null, 5, 2, null, null, null, null, 'No Take', 'Yes', null, false, 'None', null, true, 'ARC rare species project- ARC', null, null, false),
        (912350072, 1148, 55, '2018-02-04', '14:00:00', 5, 0, 9, 'S', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912350072/zip', null, false, null, null, null, false),
        (1003674, 4022, 55, '2011-03-21', null, 12, 2, null, 'S', null, null, null, null, null, false, 'None', null, false, null, null, null, false),
        (2001039, 145, 55, '2009-08-01', null, 5, 0, null, null, null, null, null, null, null, false, 'None', null, false, null, null, null, false),
        (1003675, 4021, 55, '2011-03-21', null, 12, 2, null, 'S', null, null, null, null, null, false, 'None', null, false, null, null, null, false),
        (912340394, 2801, 55, '2012-06-11', '08:00:00', 8, 0, 22, 'NW', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912340394/zip', null, false, null, null, null, false),
        (923400539, 1441, 55, '2021-10-15', '08:00:00', 5, 0, 20, 'W', 123.57215, -12.52527, 'No take multizoned', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400539/zip/', 188, null, null, null, null, false),
        (923400619, 3163, 55, '2021-08-18', '12:00:00', 6, 0, 25, 'W', 144.43347, -10.44697, 'Restricted take multizoned', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400619/zip/', 268, null, null, null, null, false),
        (923400649, 3010, 55, '2021-08-10', '11:00:00', 4, 0, 15, 'NW', 150.00092, -16.9366, 'No take multizoned', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400649/zip/', 268, null, null, null, null, false),
        (923400632, 3153, 55, '2021-08-19', '13:00:00', 7, 0, 25, 'W', 144.53391, -10.37448, 'Restricted take multizoned', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400632/zip/', 208, null, null, null, null, false),
        (912352253, 1509, 55, '2019-05-12', '12:00:00', 10, 0, 15, 'SE', null, null, null, null, null, true, 'http://rls.tpac.org.au/pq/912352253/zip', null, false, null, null, null, false),
        (923400669, 343, 55, '2021-08-21', '10:55:00', 6, 5, 10, 'S', 143.70978, -10.2875, 'Fishing', 'No', null, true, 'http://rls.tpac.org.au/pq/923400669/zip/', 268, null, null, null, null, false),
        (923401776, 4026, 55, '2023-04-12', '09:30:00', 12, 0, 15, 'E', 153.38809, -29.92314, 'No take multizoned', 'Yes', null, null, null, 208, null, null, '2023-06-16 06:25:38.233000', '2023-06-16 06:25:38.233000', false),
        (923401635, 958, 56, '2023-03-29', '16:30:00', 5, 1, 6, null, 145.99501, -43.33056, 'No take multizoned', 'Yes', null, null, null, 1, null, null, '2023-04-18 03:12:22.528000', '2023-04-18 03:12:22.528000', false),
        (923400982, 2805, 55, '2022-06-12', '09:00:00', 9, 0, 18, 'S', -68.28501, 12.13128, 'Restricted take', 'Yes', null, true, 'http://rls.tpac.org.au/pq/923400982/zip/', 366, null, null, null, '2022-11-11 13:00:03.216000', false);

insert into nrmn.survey_method (survey_method_id, survey_id, method_id, block_num, survey_not_done, survey_method_attribute)
values
    (150300, 923401762, 2, 2, false, null),
    (150330, 923401769, 2, 1, false, null),
    (150306, 923401764, 1, 1, false, null),
    (150303, 923401763, 2, 1, false, null),
    (150004, 923401736, 2, 1, false, null),
    (146469, 923400982, 1, 2, false, null),
    (146437, 923400975, 1, 1, false, null),
    (16550, 1003674, 1, 2, false, '{}'),
    (30279, 2001039, 1, 1, false, '{}'),
    (41730, 912340394, 1, 1, false, '{}'),
    (62950, 812321941, 3, null, false, '{}'),
    (65975, 812321942, 3, null, false, '{}'),
    (72001, 912350072, 1, 2, false, '{}'),
    (119888, 1003675, 0, null, false, '{}'),
    (144560, 923400539, 1, 2, false, null),
    (144918, 923400619, 1, 2, false, null),
    (144973, 923400632, 1, 1, false, null),
    (145044, 923400649, 1, 2, false, null),
    (145126, 923400669, 1, 1, false, null),
    (150364, 923401776, 2, 2, false, null),
    (721, 912352253, 1, 1, false, '{}'),
    (149529, 923401635, 3, 0, false, null),
    (150362, 923401776, 2, 1, false, null);

INSERT INTO nrmn.observation
(observation_id, measure_value,observation_attribute, diver_id, measure_id,observable_item_id, survey_method_id)
VALUES (700, 1, '{}', 51, 4, 3762, 721),
       (701, 5, '{}', 51, 1, 810, 721),
       (702, 5, '{}', 51, 1, 2367, 721),
       (704, 2, '{}', 51, 4, 6820, 721),
       (705, 1, '{}', 51, 52, 1526,721),
       (706, 1, '{}', 51, 6, 6285, 721);

insert into nrmn.observation (observation_id, survey_method_id, diver_id, observable_item_id, measure_id, measure_value, observation_attribute)
values
       (2493327, 150330, 437, 810, 3, 3, null),
       (2492188, 150306, 437, 810, 3, 40, null),
       (2491994, 150303, 437, 810, 3, 2, null),
       (2486101, 150004, 248, 810, 5, 3, null),
       (2486100, 150004, 248, 810, 4, 4, null),
       (2417391, 146469, 366, 3762, 8, 1, null),
       (2416391, 146437, 276, 3762, 9, 1, null),
       (1345059, 16550, 315, 3762, 4, 1, '{"Biomass": 10.6801553731}'),
       (1460936, 119888, 315, 3762, 4, 1, '{"Biomass": 10.6801553731}'),
       (1472991, 41730, 366, 3762, 4, 1, '{"Biomass": 10.6801553731}'),
       (1607034, 30279, 366, 2367, 5, 1, '{"Biomass": 16.7549718848}'),
       (2389868, 145126, 268, 3069, 12, 10, null),
       (2387487, 145044, 268, 3069, 11, 3, null),
       (2385075, 144973, 208, 3069, 10, 4, null),
       (2382961, 144918, 268, 3069, 8, 1, null),
       (2369909, 144560, 188, 3069, 8, 2, null),
       (2478644, 149529, 327, 1526, 44, 4, null),
       (2478643, 149529, 327, 1526, 43, 2, null),
       (2478642, 149529, 327, 1526, 42, 2, null),
       (539549, 62950, 327, 1526, 42, 18, '{}'),
       (539557, 65975, 327, 1526, 42, 9, '{}'),
       (2194300, 72001, 115, 6285, 14, 1, '{}');

