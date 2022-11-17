import {Autocomplete, TextField} from '@mui/material';
import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box} from '@mui/system';

const SpeciesCorrectGeometryFilter = ({onChange}) => {
  const [file, setFile] = useState();
  const [places, setPlaces] = useState();

  useEffect(() => {
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(reader.result, 'application/xml');
        const placeMarks = xml.getElementsByTagName('Placemark');
        const items = {};
        for (const p of placeMarks) {
          const placeName = p.children[0].textContent;
          const kml = p.children[2].outerHTML;
          items[placeName] = kml;
        }
        setPlaces(items);
      };
      reader.readAsText(file);
    }
  }, [file]);

  return (
    <>
      {places ? (
        <Autocomplete options={Object.keys(places)} onChange={(e) => {
          onChange(places[e.target.textContent]);
        }} renderInput={(params) => <TextField {...params} />} size="small" />
      ) : (
        <Box p={1}>
          <input
            type="file"
            onChange={(p) => {
              setFile(p.target.files[0]);
            }}
          />
        </Box>
      )}
    </>
  );
};

SpeciesCorrectGeometryFilter.propTypes = {
  onChange: PropTypes.func.isRequired
};

export default SpeciesCorrectGeometryFilter;
