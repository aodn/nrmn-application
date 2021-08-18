import {Grid} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const SurveyEditTemplate = (props) => {
  const {properties} = props;
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12}>
          {el['surveyId']}
        </Grid>
        <Grid item xs={6}>
          {el['visibility']}
        </Grid>
        <Grid item xs={6}>
          {el['direction']}
        </Grid>
        <Grid item xs={6}>
          {el['latitude']}
        </Grid>
        <Grid item xs={6}>
          {el['longitude']}
        </Grid>
        <Grid item xs={6}>
          {el['siteName']}
        </Grid>
        <Grid item xs={6}>
          {el['siteCode']}
        </Grid>
        <Grid item xs={6}>
          {el['program']}
        </Grid>
        <Grid item xs={6}>
          {el['blockAbundanceSimulated']}
        </Grid>
        <Grid item xs={6}>
          {el['surveyDate']}
        </Grid>
        <Grid item xs={6}>
          {el['surveyTime']}
        </Grid>
        <Grid item xs={6}>
          {el['depth']}
        </Grid>
        <Grid item xs={6}>
          {el['surveyNum']}
        </Grid>
        <Grid item xs={6}>
          {el['pqDiverInitials']}
        </Grid>
        <Grid item xs={12}>
          {el['projectTitle']}
        </Grid>
        <Grid item xs={6}>
          {el['protectionStatus']}
        </Grid>
        <Grid item xs={6}>
          {el['insideMarinePark']}
        </Grid>
        <Grid item xs={12}>
          {el['notes']}
        </Grid>
      </Grid>
    </>
  );
};

SurveyEditTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string,
  formData: PropTypes.object
};

export default SurveyEditTemplate;
