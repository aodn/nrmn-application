import {Grid} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const SiteEditTemplate = (props) => {
  const {properties} = props;
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          {el['siteCode']}
        </Grid>
        <Grid item xs={6}>
          {el['siteName']}
        </Grid>
        <Grid item xs={6}>
          {el['locationId']}
        </Grid>
      </Grid>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          {el['state']}
        </Grid>
        <Grid item xs={6}>
          {el['country']}
        </Grid>
        <Grid item xs={6}>
          {el['latitude']}
        </Grid>
        <Grid item xs={6}>
          {el['longitude']}
        </Grid>
        <Grid item xs={6}>
          {el['mpa']}
        </Grid>
        <Grid item xs={6}>
          {el['protectionStatus']}
        </Grid>
        <Grid item xs={6}>
          {el['relief']}
        </Grid>
        <Grid item xs={6}>
          {el['slope']}
        </Grid>
        <Grid item xs={6}>
          {el['waveExposure']}
        </Grid>
        <Grid item xs={6}>
          {el['currents']}
        </Grid>
        <Grid item xs={12}>
          {el['oldSiteCodes']}
        </Grid>
      </Grid>
    </>
  );
};

SiteEditTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default SiteEditTemplate;
