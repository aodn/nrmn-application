CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_ep_m1 ON nrmn.ep_m1(survey_id, recorded_species_name, size_class, block, "method", diver);
CREATE UNIQUE INDEX IF NOT EXISTS idx_unique_ep_rarity_frequency_taxon ON nrmn.ep_rarity_frequency(taxon);
