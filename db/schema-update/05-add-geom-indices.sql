CREATE INDEX meow_ecoregion_geom_idx ON nrmn.meow_ecoregions USING GIST (geom);
CREATE INDEX site_geom_idx ON nrmn.site_ref USING GIST (geom);