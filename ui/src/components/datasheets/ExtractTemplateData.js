import {Box, Button, Chip, CircularProgress, TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import React, {useEffect, useState} from 'react';
import {getEntity, templateZip} from '../../axios/api';
import qs from 'qs';
import FileDownload from 'js-file-download';

const ExtractTemplateData = () => {
  const [locations, setLocations] = useState([]);
  const [ecoRegions, setEcoRegions] = useState([]);
  const [countries, setCountries] = useState([]);
  const [areas, setAreas] = useState([]);

  const [siteCodes, setSiteCodes] = useState([]);
  const [siteLocation, setSiteLocation] = useState(null);

  const [ecoRegion, setEcoRegion] = useState([]);
  const [country, setCountry] = useState([]);
  const [area, setArea] = useState([]);

  const [download, setDownload] = useState(false);
  const [stagedLocations, setStagedLocations] = useState([]);
  const [templateLocations, setTemplateLocations] = useState([]);

  useEffect(() => {
    getEntity('locations').then((res) => {
      const locations = [];
      const groups = {ecoRegions: [], countries: [], areas: [], siteCodes: []};
      res.data.forEach((d) => {
        locations[d.id] = d.locationName;
        ['locations', 'ecoRegions', 'countries', 'areas', 'siteCodes'].forEach((prop) => {
          d[prop]
            ?.split(',')
            .map((a) => a.trim())
            .forEach((a) => {
              groups[prop][a] = groups[prop][a] ? [...groups[prop][a], d.id] : [d.id];
            });
        });
      });
      setLocations(locations);
      setEcoRegions(groups.ecoRegions);
      setCountries(groups.countries);
      setAreas(groups.areas);
      setSiteCodes(groups.siteCodes);
    });
  }, []);

  useEffect(() => {
    const set_1 = new Set(ecoRegion);
    const set_2 = new Set(country);
    const set_3 = new Set(area);

    var intersect = new Set([...set_1, ...set_2, ...set_3]);
    if (set_1.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_1.has(i)));
    if (set_2.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_2.has(i)));
    if (set_3.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_3.has(i)));

    var intersect_arr = Array.from(intersect);
    intersect_arr = siteLocation && !intersect_arr.includes(siteLocation) ? [...intersect_arr, siteLocation] : intersect_arr;
    setStagedLocations(intersect_arr.filter((l) => !templateLocations.includes(l)));
  }, [siteLocation, ecoRegion, country, area, templateLocations]);

  useEffect(() => download && downloadZip({locations: templateLocations}), [download, templateLocations]);

  const downloadZip = (params) => {
    templateZip(qs.stringify(params, {indices: false})).then((result) => {
      FileDownload(result.data, `template.zip`);
      setDownload(false);
    });
  };

  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Template Data</Typography>
      </Box>
      <Box m={5}>
        <Typography variant="subtitle2">Site Code</Typography>
        <Autocomplete
          options={Object.keys(siteCodes).sort()}
          getOptionLabel={(e) => `${e} - ${locations[siteCodes[e][0]]}`}
          onChange={(_, e) => setSiteLocation(e ? siteCodes[e][0] : null)}
          renderInput={(params) => <TextField {...params} variant="outlined" />}
        />
        <hr></hr>
        <Typography variant="subtitle2">Eco Region</Typography>
        <Autocomplete
          filterSelectedOptions
          options={Object.keys(ecoRegions).sort()}
          onChange={(e) => (e.target.textContent !== '' ? setEcoRegion(ecoRegions[e.target.textContent]) : setEcoRegion([]))}
          renderInput={(params) => <TextField {...params} variant="outlined" />}
        />
        <Typography variant="subtitle2">Country</Typography>
        <Autocomplete
          filterSelectedOptions
          options={Object.keys(countries).sort()}
          onChange={(e) => (e.target.textContent !== '' ? setCountry(countries[e.target.textContent]) : setCountry([]))}
          renderInput={(params) => <TextField {...params} variant="outlined" />}
        />
        <Typography variant="subtitle2">Area/State</Typography>
        <Autocomplete
          filterSelectedOptions
          options={Object.keys(areas).sort()}
          onChange={(e) => (e.target.textContent !== '' ? setArea(areas[e.target.textContent]) : setArea([]))}
          renderInput={(params) => <TextField {...params} variant="outlined" />}
        />
        <Box p={1}>
          {stagedLocations.map((l) => (
            <Chip
              key={l}
              style={{margin: 5}}
              label={locations[l]}
              onClick={() => setTemplateLocations(Array.from(new Set([...templateLocations, l])))}
            />
          ))}
        </Box>
        <Button
          disabled={stagedLocations.length < 1}
          onClick={() => setTemplateLocations(Array.from(new Set([...templateLocations, ...stagedLocations])))}
        >
          Add All Locations
        </Button>
        <hr></hr>
        <Typography variant="subtitle2">Generate Template for Locations</Typography>
        <Box p={1}>
          {templateLocations &&
            templateLocations.map((l) => (
              <Chip
                key={l}
                style={{margin: 5}}
                label={locations[l]}
                onDelete={() => setTemplateLocations((state) => [...state.filter((id) => id !== l)])}
              />
            ))}
        </Box>
        <Button style={{width: '100%'}} onClick={() => setDownload(true)} disabled={download || templateLocations.length < 1}>
          {download ? <CircularProgress size={25} /> : 'Download Sheets'}
        </Button>
      </Box>
    </>
  );
};

export default ExtractTemplateData;
