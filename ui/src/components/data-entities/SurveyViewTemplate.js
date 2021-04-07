import {Box, Grid} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const SurveyViewTemplate = (props) => {
  const {properties, title} = props;
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Box width={600}>
        <h1>{title}</h1>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['surveyId']}
          </Grid>
          <Grid item xs={6}>
            {el['siteName']}
          </Grid>
          <Grid item xs={6}>
            {el['program']}
          </Grid>
          <Grid item xs={6}>
            {el['surveyDate']}
          </Grid>
          <Grid item xs={6}>
            {el['surveyTime']}
          </Grid>
          <Grid item xs={12}>
            {el['projectTitle']}
          </Grid>
          <Grid item xs={6}>
            {el['depth']}
          </Grid>
          <Grid item xs={6}>
            {el['surveyNum']}
          </Grid>
          <Grid item xs={6}>
            {el['visibility']}
          </Grid>
          <Grid item xs={6}>
            {el['direction']}
          </Grid>
          <Grid item xs={6}>
            {el['method']}
          </Grid>
          <Grid item xs={6}>
            {el['block']}
          </Grid>
          <Grid item xs={6}>
            {el['longitude']}
          </Grid>
          <Grid item xs={6}>
            {el['latitude']}
          </Grid>
          <Grid item xs={6}>
            {el['protectionStatus']}
          </Grid>
          <Grid item xs={6}>
            {el['insideMarinePark']}
          </Grid>
          <Grid item xs={6}>
            {el['pqCatalogued']}
          </Grid>
          <Grid item xs={6}>
            {el['pqDiver']}
          </Grid>
          <Grid item xs={6}>
            {el['blockAbundanceSimulated']}
          </Grid>
          <Grid item xs={12}>
            {el['surveyNotDone']}
          </Grid>
          <Grid item xs={12}>
            {el['notes']}
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

SurveyViewTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string,
  formData: PropTypes.object
};

export default SurveyViewTemplate;
