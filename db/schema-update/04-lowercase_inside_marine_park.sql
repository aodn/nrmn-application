-- UI was setting the value of inside_marine_park to lowercase instead of uppercase values
UPDATE nrmn.survey SET inside_marine_park='Yes' where inside_marine_park = 'yes';
UPDATE nrmn.survey SET inside_marine_park='No' where inside_marine_park = 'no';
UPDATE nrmn.survey SET inside_marine_park='Unsure' where inside_marine_park = 'unsure';