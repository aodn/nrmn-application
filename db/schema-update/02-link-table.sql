BEGIN TRANSACTION;

-- DROP INDEX IF EXISTS idx_shared_link_public_id;
-- DROP TABLE IF EXISTS nrmn.shared_link;

CREATE TABLE nrmn.shared_link (
  id SERIAL PRIMARY KEY,
  sec_user_id INTEGER REFERENCES nrmn.sec_user (id),
  link_type TEXT,
  description TEXT,
  expires TIMESTAMP NOT NULL,
  expired TIMESTAMP,
  created TIMESTAMP NOT NULL,
  updated TIMESTAMP,
  public_id UUID NOT NULL,
  target_url TEXT
);

CREATE INDEX idx_shared_link_public_id ON nrmn.shared_link (public_id);

END TRANSACTION;

-- insert into nrmn.shared_link (sec_user_id, link_type, description, expires, expired, created, public_id, target_url) values (321460, 'ENDPOINT', 'ep_m0_off_transect_sighting', '2021-01-01', null, '2020-01-01', '00000000-0000-0000-0000-000000000001', 'https://www.google.com');
