import {Alert, Box, Button, IconButton, LinearProgress, Link, TextField, Typography} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Close';
import React, {useEffect, useReducer, useState} from 'react';
import {getSurveySpecies, postSpeciesCorrection} from '../../../api/api';
import CustomSearchInput from '../../input/CustomSearchInput';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import SpeciesCorrectResults from './SpeciesCorrectResults';
import LaunchIcon from '@mui/icons-material/Launch';

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

  useEffect(() => {
    if (!selected || selected.jobId) return;
    setCorrection({newObservableItemName: null});
    setCorrectionLocations([...selected.locations]);
  }, [selected, setCorrectionLocations, searchResultData]);

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
          getSurveySpecies(payload).then((res) => {
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


  return (
    <>
      <Box p={1}>
        <Typography variant="h6">Correct Species</Typography>
      </Box>
      <Box>
        <Box border={1} borderRadius={1} m={1} borderColor="divider" style={{overflow: 'hidden'}} >
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
              onClick={(id) => {
                const res = searchResultData.find((r) => r.observableItemId === id);
                setSelected(res);
              }}
            />
          </Box>
        )}
      </Box>
    </>
  );
};

export default SpeciesCorrect;
