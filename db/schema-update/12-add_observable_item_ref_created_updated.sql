BEGIN TRANSACTION;

ALTER TABLE nrmn.observable_item_ref ADD COLUMN created TIMESTAMP;
ALTER TABLE nrmn.observable_item_ref ADD COLUMN updated TIMESTAMP;

END TRANSACTION;
