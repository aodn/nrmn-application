import {Box, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';

const SpeciesCorrect = () => {
  const [data, setData] = useState();

  const [loading, setLoading] = useState(true);
  const [canSearch, setCanSearch] = useState(false);

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations').then((res) => {
        const locations = [];
        res.data.items.forEach((d) => {
          locations[d.id] = d.locationName;
        });
        locations.sort((a, b) => a.localeCompare(b));
        const locationIds = Object.keys(locations).sort();
        setData({locations, locationIds});
      });
    }
    fetchLocations();
    setLoading(false);
  }, []);

  const [filter, updateFilter] = useReducer((filter, action) => {
    const updated = {...filter};
    updated[action.field] = action.value;
    setCanSearch(updated.startDate && updated.endDate && updated.locationId);
    return updated;
  }, {});

  const updateStartDate = (e) => {
    updateFilter({field: 'startDate', value: e.target.value});
  };

  const updateEndDate = (e) => {
    updateFilter({field: 'endDate', value: e.target.value});
  };

  const updateLocation = (e, value) => {
    updateFilter({field: 'locationId', value: value});
  };

  const onSearch = () => {
    console.log(filter);
  };

  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Bulk Species Update Feature</Typography>
      </Box>
      <hr></hr>
      {data ? (
        <Box ml={5} display="flex" flexDirection="row">
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
              onChange={updateLocation}
              options={data.locationIds}
              renderInput={(params) => <TextField {...params} />}
              size="small"
              value={filter.location}
            />
          </Box>
          <Box my={4} width={200}>
            <LoadingButton disabled={!canSearch} fullWidth onClick={onSearch} variant="contained">
              Search
            </LoadingButton>
          </Box>
        </Box>
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

export default SpeciesCorrect;
