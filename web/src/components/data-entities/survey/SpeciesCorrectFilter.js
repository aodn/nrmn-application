import {Box, Button, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';
import {PropTypes} from 'prop-types';
import CustomSearchInput from '../../input/CustomSearchInput';

const SpeciesCorrectFilter = ({onSearch}) => {
  const [data, setData] = useState();
  const [countries, setCountries] = useState();
  const [states, setState] = useState();
  const [ecoRegions, setEcoRegions] = useState();

  const [dataResponse, setDataResponse] = useState();

  const [loading, setLoading] = useState(true);

  const initialFilter = {
    startDate: '2021-01-01',
    endDate: '2022-01-01',
    country: '',
    state: '',
    locationId: null,
    observableItemId: null,
    coord1: '',
    coord2: '',
    species: '',
    locationIds: []
  };

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations').then((res) => {
        const activeLocations = res.data.items.filter(i => i.status === 'Active');
        setDataResponse(activeLocations);
        const locations = [];
        const locationIds = [];
        res.data.items
          .sort((a, b) => a.locationName.localeCompare(b.locationName))
          .forEach((d) => {
            locations[d.id] = d.locationName;
            locationIds.push(d.id);
          });
        setData({locations, locationIds});

        const groups = {ecoRegions: [], countries: [], areas: [], siteCodes: []};
        res.data.items.forEach((d) => {
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
        setEcoRegions(groups.ecoRegions);
        setCountries(groups.countries);
        setState(groups.areas);

        const labels = res.data.items.reduce(
          (acc, cur) => {
            if (cur.countries && !acc.countries.includes(cur.countries)) acc.countries.push(cur.countries);

            if (cur.areas && !acc.areas.includes(cur.areas)) acc.areas.push(cur.areas);

            if (cur.ecoRegions && !acc.ecoRegions.includes(cur.ecoRegions)) acc.ecoRegions.push(cur.ecoRegions);

            return acc;
          },
          {countries: [], areas: [], ecoRegions: []}
        );
        labels.countries.sort();
        setCountries(labels.countries);
        labels.areas.sort();
        setState(labels.areas);
        labels.ecoRegions.sort();
        setEcoRegions(labels.ecoRegions);
      });
      setLoading(false);
    }
    fetchLocations();
  }, []);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      if (!action) return {...initialFilter};
      const updated = {...filter};
      if (!action.value) {
        delete updated[action.field];
      } else {
        updated[action.field] = action.value;
      }

      var locationIds = [];
      locationIds.push(updated['locationId']);
      locationIds.push(dataResponse.find((d) => d.areas === updated['state'])?.id);
      locationIds.push(dataResponse.find((d) => d.countries === updated['country'])?.id);
      locationIds.push(dataResponse.find((d) => d.ecoRegions === updated['ecoRegion'])?.id);
      locationIds = [...new Set(locationIds.filter((d) => d))];

      updated['locationIds'] = locationIds.join(',');
      return updated;
    },
    {...initialFilter}
  );

  const updateStartDate = (e) => {
    updateFilter({field: 'startDate', value: e.target.value});
  };

  const updateEndDate = (e) => {
    updateFilter({field: 'endDate', value: e.target.value});
  };

  const updateLocation = (e, value) => {
    updateFilter({field: 'locationId', value: value});
  };

  const updateCountry = (e, value) => {
    updateFilter({field: 'country', value: value});
  };

  const updateState = (e, value) => {
    updateFilter({field: 'state', value: value});
  };

  const updateEcoRegion = (e, value) => {
    updateFilter({field: 'ecoRegion', value: value});
  };

  const updateObservableItem = (e) => {
    updateFilter({field: 'observableItemId', value: e ? e.id : null});
    updateFilter({field: 'species', value: e ? e.species : null});
  };

  const updateCoord1 = (e) => {
    updateFilter({field: 'coord1', value: e.target.value});
  };

  const updateCoord2 = (e) => {
    updateFilter({field: 'coord2', value: e.target.value});
  };

  const canSearch =
    filter.startDate &&
    filter.endDate &&
    (filter.locationId ||
      filter.country ||
      filter.ecoRegion ||
      filter.state ||
      filter.observableItemId ||
      (filter.coord1 && filter.coord2));

  return (
    <>
      {data?.locationIds && states && countries ? (
        <>
          <Box ml={1} display="flex" flexDirection="row">
            <Box m={1} width={150}>
              <Typography variant="subtitle2">Start Date</Typography>
              <input
                disabled={loading}
                max={filter.endDate}
                onChange={updateStartDate}
                style={{height: '35px', width: '150px'}}
                type="date"
                value={filter.startDate}
              />
            </Box>
            <Box m={1} width={150}>
              <Typography variant="subtitle2">End Date</Typography>
              <input
                disabled={loading}
                min={filter.startDate}
                onChange={updateEndDate}
                style={{height: '35px', width: '150px'}}
                type="date"
                value={filter.endDate}
              />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">Location</Typography>
              <Autocomplete
                disabled={loading}
                filterSelectedOptions
                getOptionLabel={(id) => data.locations[id]}
                value={filter.locationId}
                onChange={updateLocation}
                options={data.locationIds}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">EcoRegion</Typography>
              <Autocomplete
                disabled={loading}
                filterSelectedOptions
                onChange={updateEcoRegion}
                options={ecoRegions}
                value={filter.ecoRegion}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} width={300}>
              <CustomSearchInput fullWidth label="Species" formData={filter.species} onChange={updateObservableItem} />
            </Box>
          </Box>
          <Box ml={1} display="flex" flexDirection="row">
            <Box m={1} width={150}>
              <Typography variant="subtitle2">BBox Min</Typography>
              <input onChange={updateCoord1} value={filter.coord1} style={{height: '35px', width: '150px'}} />
            </Box>
            <Box m={1} width={150}>
              <Typography variant="subtitle2">BBox Max</Typography>
              <input onChange={updateCoord2} value={filter.coord2} style={{height: '35px', width: '150px'}} />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">Country</Typography>
              <Autocomplete
                disabled={loading}
                filterSelectedOptions
                onChange={updateCountry}
                options={countries}
                value={filter.country}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">Area/State</Typography>
              <Autocomplete
                disabled={loading}
                filterSelectedOptions
                onChange={updateState}
                options={states}
                value={filter.state}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            {/* <Box mx={1} mt={4} width={50}>
              <Button onClick={() => updateFilter()} fullWidth variant="outlined">
                Reset
              </Button>
            </Box> */}
            <Box m={1} my={4} width={300}>
              <LoadingButton disabled={!canSearch} onClick={() => onSearch(filter)} fullWidth variant="contained">
                Search
              </LoadingButton>
            </Box>
          </Box>
        </>
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

SpeciesCorrectFilter.propTypes = {
  onSearch: PropTypes.func.isRequired
};

export default SpeciesCorrectFilter;
