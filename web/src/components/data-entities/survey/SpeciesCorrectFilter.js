import {Box, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';
import {PropTypes} from 'prop-types';

const SpeciesCorrectFilter = ({onSearch}) => {

  const [data, setData] = useState();

  const [loading, setLoading] = useState(true);

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
      setLoading(false);
    }
    fetchLocations();
  }, []);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      const updated = {...filter};
      updated[action.field] = action.value;
      return updated;
    },
    {startDate: '2021-01-01', endDate: '2022-01-01', locationId: null}
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

  const canSearch = filter.startDate && filter.endDate && filter.locationId;

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
            <Box my={4} width={200}>
              <LoadingButton
                disabled={!canSearch}
                onClick={() => onSearch(filter)}
                fullWidth
                variant="contained"
              >
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
    onSearch: PropTypes.func.isRequired,
};

export default SpeciesCorrectFilter;
