import {Alert, Box, Button, IconButton, LinearProgress, TextField, Typography} from '@mui/material';
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
  const [selected, setSelected] = useState(null);
  const [correctionLocations, setCorrectionLocations] = useState([]);

  useEffect(() => {
    if (!selected || selected.jobId) return;
    const resultLocations = searchResults.find((r) => r.observableItemId === selected.result).locations;
    setCorrection({newObservableItemName: null});
    setCorrectionLocations([...resultLocations]);
  }, [selected, setCorrectionLocations, searchResults]);

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
        // TODO: flatten `correctionLocations` into surveyIds
        const surveyIds = correctionLocations.reduce((p,v) => ([...p, ...v.surveyIds]), []);
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
            const rowData = res.data.map((p) => {
              const surveyJson = JSON.parse(p.surveyJson);

              const locations = Array.from(new Set(Object.values(surveyJson))).map((l) => {
                const surveyIds = Object.entries(surveyJson)
                  .filter((s) => s[1] === l)
                  .map((e) => e[0]);
                return {locationId: l, locationName: locationData[l], surveyIds};
              });

              return {...p, locations};
            });

            setSearchResults(rowData);
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
  }, [request.request, locationData]);

  const detail = searchResults?.find((r) => r.observableItemId === selected?.result);
  const req = request.request;
  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <SpeciesCorrectFilter
        onLoadLocations={(locations) => setLocationData(locations)}
        onSearch={(filter) => {
          setSearchResults([]);
          setSelected(null);
          dispatch({type: 'getRequest', payload: filter});
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
            <Box width="50%" borderLeft={1} borderColor="divider" mr={2} style={{overflowX: 'hidden', overflowY: 'auto'}}>
              <Box mx={1}>
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
                {correctionLocations?.map((l) => {
                  return (
                    <p key={l.locationId} onClick={() => {
                      setCorrectionLocations([...correctionLocations.filter(c => c.locationName !== l.locationName)]);
                    }}>
                      {l.locationName} {l.surveyIds.join(',')}
                    </p>
                  );
                })}
              </Box>
            </Box>
          )}
        </Box>
      )}
    </>
  );
};

export default SpeciesCorrect;
