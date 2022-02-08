BEGIN TRANSACTION;

CREATE OR REPLACE FUNCTION trg_mapped_id_func() RETURNS trigger 
LANGUAGE plpgsql AS
$func$
BEGIN
   NEW.mapped_id := NEW.observable_item_id + 99900000;
   RETURN NEW;
END
$func$;

DROP TRIGGER IF EXISTS trg_mapped_id ON nrmn.observable_item_ref;
CREATE TRIGGER trg_mapped_id BEFORE INSERT ON nrmn.observable_item_ref FOR EACH ROW EXECUTE PROCEDURE trg_mapped_id_func();

BEGIN TRANSACTION;