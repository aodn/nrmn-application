CREATE
OR REPLACE FUNCTION safe_geom_from_text_with_details(wkt text, srid int, minlongitude text, minlatitude text, maxlongitude text, maxlatitude text, taxon text) RETURNS geometry AS $$
DECLARE
geom geometry;
BEGIN
    -- Attempt to create the geometry
    geom
:= ST_GeomFromText(wkt, srid);
RETURN geom;
EXCEPTION WHEN OTHERS THEN
    -- Log detailed error information, including NULL checks on inputs
    RAISE NOTICE 'Failed to parse WKT for taxon %: WKT=%, SRID=%, minlongitude=%, minlatitude=%, maxlongitude=%, maxlatitude=% (NULL checks: minlongitude IS NULL=%, minlatitude IS NULL=%, maxlongitude IS NULL=%, maxlatitude IS NULL=%)',
        taxon, wkt, srid, minlongitude, minlatitude, maxlongitude, maxlatitude,
        minlongitude IS NULL, minlatitude IS NULL, maxlongitude IS NULL, maxlatitude IS NULL;
RETURN NULL;
END;
$$
LANGUAGE plpgsql;
