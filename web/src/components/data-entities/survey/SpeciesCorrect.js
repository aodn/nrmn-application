import {Alert, Box, Button, IconButton, LinearProgress, Link, Tab, Tabs, TextField, Typography} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Close';
import React, {useEffect, useReducer, useState} from 'react';
import {searchSpeciesSummary, postSpeciesCorrection} from '../../../api/api';
import CustomSearchInput from '../../input/CustomSearchInput';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import SpeciesCorrectResults from './SpeciesCorrectResults';
import LaunchIcon from '@mui/icons-material/Launch';
import SpeciesCorrectEdit from './SpeciesCorrectEdit';

const removeNullProperties = (obj) => {
  return obj ? Object.fromEntries(Object.entries(obj).filter((v) => v[1] && v[1] !== '')) : null;
};

const SpeciesCorrect = () => {
  const [correction, setCorrection] = useState({newObservableItemName: null});
  const [locationData, setLocationData] = useState([]);
  const [searchResults, setSearchResults] = useState(null);
  const [searchResultData, setSearchResultData] = useState(null);
  const [selected, setSelected] = useState(null);
  const [correctionLocations, setCorrectionLocations] = useState([]);
  const [tabIndex, setTabIndex] = useState(0);

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
      case 'postCorrection': {
        const surveyIds = correctionLocations.reduce((p, v) => [...p, ...v.surveyIds], []);
        const payload = {prevObservableItemId: selected.result, newObservableItemId: correction.newObservableItemId, surveyIds};
        return {loading: true, results: null, request: {search: state.search, type: 'post', payload}};
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
        case 'post':
          postSpeciesCorrection(request.request.payload).then((res) => {
            setSelected({jobId: res.data});
            dispatch({type: 'getRequest', payload: request.request.search.payload});
          });
          break;
      }
  }, [request.request, request.loading, locationData]);

  useEffect(() => {
    setTabIndex(selected ? 1 : 0);
  }, [selected]);

  return (
    <>
      <Box p={1}>
        <Typography variant="h6">Correct Species</Typography>
      </Box>
      <Tabs value={tabIndex} onChange={(e, v) => setTabIndex(v)}>
        <Tab label="Search Results" />
        <Tab label="Confirm Correction" disabled={!selected} />
      </Tabs>
      {tabIndex == 1 && <SpeciesCorrectEdit selected={selected} />}
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
            <SpeciesCorrectResults results={searchResults} onClick={(id) => setSelected({filter: request.search.payload, result: id})} />
          </Box>
        )}
      </Box>
    </>
  );
};

export default SpeciesCorrect;
