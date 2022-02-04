BEGIN TRANSACTION;

ALTER TABLE nrmn.observable_item_ref ADD COLUMN mapped_id integer;

END TRANSACTION;
