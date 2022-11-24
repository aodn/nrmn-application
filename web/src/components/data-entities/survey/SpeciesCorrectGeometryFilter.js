import {Autocomplete, TextField, Typography} from '@mui/material';
import React, {useEffect, useState} from 'react';
import {PropTypes} from 'prop-types';
import {Box} from '@mui/system';

const SpeciesCorrectGeometryFilter = ({onChange, filter}) => {
  const [file, setFile] = useState();
  const [label, setLabel] = useState('Upload KML file ..');
  const [places, setPlaces] = useState();

  useEffect(() => {
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        const parser = new DOMParser();
        const xml = parser.parseFromString(reader.result, 'application/xml');
        const placeMarks = xml.getElementsByTagName('Placemark');
        if (placeMarks.length < 1) return setLabel('No placemarks found in the file');
        const items = {};
        for (const p of placeMarks) {
          const placeName = p.children[0].textContent;
          const kml = p.children[2].outerHTML;
          items[placeName] = kml;
        }
        setPlaces(items);
      };
      reader.readAsText(file);
      setLabel('Upload KML file ..');
    }
  }, [file]);

  useEffect(() => {
    if (!filter.geometry) {
      setPlaces();
      setFile();
    }
  }, [filter.geometry]);

  return (
    <Box style={{height: '35px', width: '300px'}}>
      {places ? (
        <Autocomplete
          options={Object.keys(places)}
          onChange={(e) => {
            onChange(places[e.target.textContent]);
          }}
          renderInput={(params) => <TextField {...params} />}
          size="small"
        />
      ) : (
        <Box p={1} border={1} borderColor="grey.400" borderRadius={1}>
          <input
            id="kmlFileInput"
            accept=".kml"
            type="file"
            style={{display: 'none'}}
            onChange={(p) => {
              setFile(p.target.files[0]);
            }}
          />
          <label style={{cursor: 'pointer'}} htmlFor="kmlFileInput">
            <Typography variant="subtitle2">{label}</Typography>
          </label>
        </Box>
      )}
    </Box>
  );
};

SpeciesCorrectGeometryFilter.propTypes = {
  onChange: PropTypes.func,
  filter: PropTypes.object
};

export default SpeciesCorrectGeometryFilter;
