import {Box, Chip, LinearProgress, Typography} from '@mui/material';
import React, {useEffect, useReducer} from 'react';
import {getSurveySpecies} from '../../../api/api';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import {PropTypes} from 'prop-types';

const SpeciesCorrectResults = (results) => {
  return (
    <Box>
      {results.results.map((r) => (
        <Box key={`${r.observableItemId}-${r.surveyId}`} display="flex" flexDirection="row">
          <div width="150px">{r.locationName}</div>
          <div width="100px">{r.surveyId}</div>
          <div width="100px">{r.surveyDate}</div>
          <div width="100px">{r.observableItemName}</div>
          <div width="200px">{r.commonName}</div>
        </Box>
      ))}
    </Box>
  );
};

SpeciesCorrectResults.propsTypes = {
  results: PropTypes.object
};

const SpeciesCorrect = () => {
  const [request, setRequest] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, results: null, request: action.payload};
      case 'showResults':
        return {loading: false, request: null, results: action.payload};
      default:
        return state;
    }
  }, {});

  useEffect(() => {
    const fetchSurveySpecies = async () => {
      const result = await getSurveySpecies(request.request);
      setRequest({type: 'showResults', payload: result.data});
    };
    if (request.request) fetchSurveySpecies();
  }, [request.request]);

  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <SpeciesCorrectFilter onSearch={(filter) => setRequest({type: 'getRequest', payload: filter})} />
      {request.loading && <LinearProgress />}
      {request.results && (
        <Box display="flex" flex={2} overflow="hidden" flexDirection="row">
          <Box width="50%" overflow="scroll">
            <SpeciesCorrectResults results={request.results} />
          </Box>
          <Box>Thing</Box>
        </Box>
      )}
    </>
  );
};

export default SpeciesCorrect;
