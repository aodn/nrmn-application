import {Box, LinearProgress, TextField, Typography} from '@mui/material';
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

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations').then((res) => {
        const locations = [];
        const locationIds = [];
        res.data.items
          .sort((a, b) => a.locationName.localeCompare(b.locationName))
          .forEach((d) => {
            locations[d.id] = d.locationName;
            locationIds.push(d.id);
          });
        setData({locations, locationIds});
        const labels = res.data.items.reduce(
          (acc, cur) => {
            if (cur.countries && !acc.countries.includes(cur.countries)) {
              acc.countries.push(cur.countries);
            }
            if (cur.areas && !acc.areas.includes(cur.areas)) {
              acc.areas.push(cur.areas);
            }
            return acc;
          },
          {countries: [], areas: []}
        );
        labels.countries.sort();
        labels.areas.sort();
        setCountries(labels.countries);
        setState(labels.areas);
      });
      setLoading(false);
    }
    fetchLocations();
  }, []);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      const updated = {...filter};
      if (!action.value) {
        delete updated[action.field];
      } else {
        updated[action.field] = action.value;
      }
      return updated;
    },
    {startDate: '2021-01-01', endDate: '2022-01-01'}
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

  const updateObservableItem = (e) => {
    updateFilter({field: 'observableItemId', value: e ? e.id : null});
  };

  const updateCoord1 = (e) => {
    updateFilter({field: 'coord1', value: e.target.value});
  };

  const updateCoord2 = (e) => {
    updateFilter({field: 'coord2', value: e.target.value});
  };

  const canSearch = filter.startDate && filter.endDate;

  return (
    <>
      {data ? (
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
              <Typography variant="subtitle2">Species</Typography>
              <CustomSearchInput fullWidth onChange={updateObservableItem} />
            </Box>
            <Box m={1} my={4} width={200}></Box>
          </Box>
          <Box ml={1} display="flex" flexDirection="row">
            <Box m={1} width={150}>
              <Typography variant="subtitle2">BBox Min</Typography>
              <input onInput={updateCoord1} value={filter.coord1} style={{height: '35px', width: '150px'}} />
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
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} my={4} width={200}>
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
