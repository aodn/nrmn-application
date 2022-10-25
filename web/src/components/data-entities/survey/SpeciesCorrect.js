import {Box, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';
import {getSurveySpecies} from '../../../api/api';

const SpeciesCorrect = () => {
  const [data, setData] = useState();

  const [loading, setLoading] = useState(true);

  const [request, setRequest] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, request: action.payload};
      case 'showResults':
        return {loading: false, results: action.payload};
      default:
        return state;
    }
  }, {});

  useEffect(async () => {
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

  useEffect(() => {
    const fetchSurveySpecies = async () => {
      const result = await getSurveySpecies(filter);
      setRequest({type: 'showResults', payload: result.data});
    };
    if (request.request) fetchSurveySpecies();
  }, [request.request]);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      const updated = {...filter};
      updated[action.field] = action.value;
      return updated;
    },
    {startDate: '2021-01-01', endDate: '2022-01-01', locationId: 38}
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
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <hr></hr>
      {data ? (
        <>
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
                onClick={() => setRequest({type: 'getRequest', payload: filter})}
                fullWidth
                variant="contained"
              >
                Search
              </LoadingButton>
            </Box>
          </Box>
          <Box mx={5}>
            {request.loading ? (
              <LinearProgress />
            ) : (
              <Box>
                {request.results && (
                  <Box>
                    <Typography variant="subtitle2">Results</Typography>
                    <Box>
                      {request.results.map((r) => (
                        <Box key={r.observableItemId} display="flex" flexDirection="row">
                          <Box width="150px">{r.locationName}</Box><Box width="100px">{r.surveyId}</Box><Box width="100px">{r.surveyDate}</Box><Box width="200px">{r.observableItemName}</Box><Box width="200px">{r.commonName}</Box>
                        </Box>
                      ))}
                    </Box>
                  </Box>
                )}
              </Box>
            )}
          </Box>
        </>
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

export default SpeciesCorrect;
