import {Alert, Box, Button, LinearProgress, Tab, Tabs, Typography} from '@mui/material';
import React, {useEffect, useReducer, useState} from 'react';
import {searchSpeciesSummary} from '../../../api/api';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import SpeciesCorrectResults from './SpeciesCorrectResults';
import SpeciesCorrectEdit from './SpeciesCorrectEdit';
import JobView from '../../job/JobView';

const removeNullProperties = (obj) => {
  return obj ? Object.fromEntries(Object.entries(obj).filter((v) => v[1] && v[1] !== '')) : null;
};

const SpeciesCorrect = () => {
  const [locationData, setLocationData] = useState([]);
  const [searchResults, setSearchResults] = useState();
  const [selected, setSelected] = useState();
  const [tabIndex, setTabIndex] = useState(0);
  const [jobId, setJobId] = useState();

  useEffect(() => {
    document.title = 'Species Correction';
  }, []);

  const [request, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, results: null, request: {type: 'search', payload: action.payload}};
      case 'showResults': {
        return {loading: false, search: state.request, request: null};
      }
      case 'showError': {
        return {loading: false, search: null, request: null, error: action.payload};
      }
      default:
        return state;
    }
  }, {});

  useEffect(() => {
    if (request.request)
      switch (request.request.type) {
        case 'search': {
          var payload = removeNullProperties(request.request.payload);
          searchSpeciesSummary(payload).then((res) => {
            setSearchResults(res.data);
            dispatch({type: 'showResults'});
          });
          break;
        }
      }
  }, [request.request, request.loading, locationData]);

  useEffect(() => {
    if (tabIndex === 0) {
      setSelected(null);
      setJobId(null);
    }
  }, [tabIndex]);

  useEffect(() => {
    setTabIndex(selected ? 1 : 0);
  }, [selected]);

  useEffect(() => {
    setTabIndex(jobId ? 2 : 0);
  }, [jobId]);

  return (
    <>
      <Box p={1}>
        <Typography variant="h6">Species Correction</Typography>
      </Box>
      <Tabs value={tabIndex} onChange={(e, v) => setTabIndex(v)}>
        <Tab style={{minWidth: '33%'}} label="Search" />
        <Tab style={{minWidth: '33%'}} label="Correcting" disabled={!selected || jobId} />
        <Tab style={{minWidth: '33%'}} label="Corrected" disabled={!jobId} />
      </Tabs>
      {tabIndex === 2 && jobId && (
        <Box border={1} borderRadius={1} m={1} borderColor="divider">
          <JobView jobId={jobId} />
          <Box width={200} m={2}>
          <Button variant="contained" onClick={() => setTabIndex(0)}>Return to Search</Button>
          </Box>
        </Box>
      )}
      {tabIndex === 1 && <SpeciesCorrectEdit selected={selected} onSubmit={(newJobId) => setJobId(newJobId)} />}
      {
        <Box display={tabIndex == 0 ? 'initial' : 'none'}>
          <Box border={1} borderRadius={1} m={1} borderColor="divider" style={{overflow: 'hidden'}}>
            <SpeciesCorrectFilter
              onLoadLocations={(locations) => setLocationData(locations)}
              onSearch={(filter) => {
                setSearchResults([]);
                setSelected(null);
                const payload = {...filter, locationIds: filter.locationId ? [filter.locationId] : filter.locationIds};
                delete payload.locationId;
                dispatch({type: 'getRequest', payload});
              }}
            />
          </Box>
          {request.loading && <LinearProgress />}
          {request.error && <Alert severity="error">{request.error}</Alert>}
          {!request.loading && searchResults && (
            <Box m={1}>
              <SpeciesCorrectResults
                results={searchResults}
                onClick={(id) => setSelected({filter: {...request.search.payload, observableItemId: id.observableItemId}, result: id})}
              />
            </Box>
          )}
        </Box>
      }
    </>
  );
};

export default SpeciesCorrect;
