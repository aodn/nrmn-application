-- UI was setting the value of inside_marine_park to uppercase Yes, No, or Unsure.
UPDATE nrmn.survey SET inside_marine_park=LOWER(inside_marine_park) WHERE inside_marine_park <> LOWER(inside_marine_park);
