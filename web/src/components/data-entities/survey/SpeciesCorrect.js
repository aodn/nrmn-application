import {Box, Chip, LinearProgress, Typography} from '@mui/material';
import React, {useEffect, useState, useReducer} from 'react';
import {getSurveySpecies} from '../../../api/api';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import {PropTypes} from 'prop-types';

const SpeciesCorrectResults = ({results, onClick}) => {
  return (
    <Box>
      {results.map((r) => (
        <Box key={`${r.observableItemId}-${r.surveyId}`} display="flex" flexDirection="row">
          <div width="150px">{r.locationName}</div>
          <div width="100px">{r.surveyId}</div>
          <div width="100px">{r.surveyDate}</div>
          <div width="100px">{r.observableItemName}</div>
          <div width="200px">{r.commonName}</div>
          <Chip label="View" onClick={() => onClick(r)} />
        </Box>
      ))}
    </Box>
  );
};

SpeciesCorrectResults.propTypes = {
  results: PropTypes.array,
  onClick: PropTypes.func
};

const SpeciesCorrect = () => {
  const [request, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, results: null, detail: null, request: action.payload};
      case 'showResults':
        return {loading: false, request: null, detail: null, results: action.payload};
      default:
        return state;
    }
  }, {});

  useEffect(() => {
    const fetchSurveySpecies = async () => {
      const result = await getSurveySpecies(request.request);
      dispatch({type: 'showResults', payload: result.data});
    };
    if (request.request) fetchSurveySpecies();
  }, [request.request]);

  const [detail, setDetail] = useState(null);

  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <SpeciesCorrectFilter onSearch={(filter) => dispatch({type: 'getRequest', payload: filter})} />
      {request.loading && <LinearProgress />}
      {request.results && (
        <Box display="flex" flex={2} overflow="hidden" flexDirection="row">
          <Box width="50%" overflow="scroll">
            <SpeciesCorrectResults results={request.results} onClick={(r) => setDetail(r)} />
          </Box>
          {detail && (
            <Box>
              <div width="150px">{detail.locationName}</div>
              <div width="100px">{detail.surveyId}</div>
              <div width="100px">{detail.surveyDate}</div>
              <div width="100px">{detail.observableItemName}</div>
              <div width="200px">{detail.commonName}</div>
            </Box>
          )}
        </Box>
      )}
    </>
  );
};

export default SpeciesCorrect;
