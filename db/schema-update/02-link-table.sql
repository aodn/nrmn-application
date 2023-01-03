BEGIN TRANSACTION;

DROP INDEX IF EXISTS idx_shared_link_public_id;
DROP TABLE IF EXISTS nrmn.shared_link;

CREATE TABLE nrmn.shared_link (
  id SERIAL PRIMARY KEY,
  sec_user_id INTEGER REFERENCES nrmn.sec_user (id),
  link_type TEXT,
  receipient TEXT,
  expires TIMESTAMP NOT NULL,
  expired TIMESTAMP,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP,
  target_url TEXT
);

END TRANSACTION;
