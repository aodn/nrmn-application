import {Alert, Box, Button, Chip, IconButton, LinearProgress, TextField, Typography} from '@mui/material';
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
  const [selected, setSelected] = useState(null);
  const [correction, setCorrection] = useState({newObservableItemName: null});
  const [locationChips, setLocationChips] = useState([]);
  const [searchResults, setSearchResults] = useState(null);

  useEffect(() => {
    setCorrection({newObservableItemName: null});
  }, [selected]);

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
        const surveyIds = searchResults.find((r) => r.observableItemId === selected.result).surveyIds;
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
            setSearchResults(res.data.map((p) => ({...p, surveyIds: JSON.parse(p.surveyIds)})));
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
  }, [request.request]);

  const detail = searchResults?.find((r) => r.observableItemId === selected?.result);
  const req = request.request;
  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <SpeciesCorrectFilter
        onSearch={(filter, chips) => {
          setSearchResults([]);
          setSelected(null);
          dispatch({type: 'getRequest', payload: filter});
          setLocationChips(chips);
        }}
      />
      {request.loading && <LinearProgress />}
      {request.error && <Alert severity="error">{request.error}</Alert>}
      {!request.loading && searchResults && (
        <Box display="flex" flex={2} overflow="hidden" flexDirection="row">
          <Box width="50%" style={{overflowX: 'hidden', overflowY: 'auto'}}>
            <SpeciesCorrectResults results={searchResults} onClick={(result) => setSelected({result})} />
          </Box>
          {selected?.jobId && (
            <Box m={1} width="30%">
              <Alert>Species Corrected</Alert>
              <Box m={1}>
                <Button
                  variant="outlined"
                  endIcon={<LaunchIcon />}
                  onClick={() => window.open(`/data/job/${selected.jobId}/view`, '_blank').focus()}
                >
                  Open Job
                </Button>
              </Box>
            </Box>
          )}
          {detail && (
            <Box width="50%" m={2} style={{overflowX: 'hidden', overflowY: 'auto'}}>
              <Box m={1}>
                <Typography variant="subtitle2">Current species name</Typography>
                <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
                  <TextField fullWidth color="primary" size="small" value={detail.observableItemName} spellCheck={false} readOnly />
                  <IconButton
                    style={{marginLeft: 5, marginRight: 15}}
                    onClick={() => window.open(`/reference/observableItem/${detail.observableItemId}`, '_blank').focus()}
                  >
                    <LaunchIcon />
                  </IconButton>
                </Box>
              </Box>
              <Box m={1}>
                <Typography variant="subtitle2">Correct to</Typography>
                <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
                  <CustomSearchInput
                    fullWidth
                    formData={correction?.newObservableItemName}
                    exclude={detail.observableItemName}
                    onChange={(t) => {
                      if (t) {
                        setCorrection({...req, newObservableItemId: t.id, newObservableItemName: t.species});
                      } else {
                        setCorrection({...req, newObservableItemId: null, newObservableItemName: null});
                      }
                    }}
                  />
                  <IconButton
                    style={{marginLeft: 5, marginRight: 15}}
                    disabled={!correction?.newObservableItemId}
                    onClick={() => window.open(`/reference/observableItem/${correction.newObservableItemId}`, '_blank').focus()}
                  >
                    <LaunchIcon />
                  </IconButton>
                </Box>
              </Box>
              <Box m={1}>
                <Button
                  variant="contained"
                  disabled={!correction?.newObservableItemName}
                  onClick={() => dispatch({type: 'postCorrection'})}
                >
                  Submit Correction
                </Button>
              </Box>
              <Box m={1} key={detail.observableItemId}>
                <Box m={1}>
                  {locationChips?.map((c) => (
                    <Chip key={`location-${c.id}`} label={c.locationName} style={{margin: 5}} />
                  ))}
                </Box>
                <Typography variant="subtitle2">Surveys to correct</Typography>
                <Box m={1}>
                  {detail?.surveyIds.map((id) => (
                    <Chip
                      key={`survey-${detail.observableItemId}-${id}`}
                      label={id}
                      style={{margin: 5}}
                      onClick={() => window.open(`/data/survey/${id}`, '_blank').focus()}
                      clickable
                    />
                  ))}
                </Box>
              </Box>
            </Box>
          )}
        </Box>
      )}
    </>
  );
};

export default SpeciesCorrect;
