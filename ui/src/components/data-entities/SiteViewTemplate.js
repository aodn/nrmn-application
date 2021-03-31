import {Box, Grid, Typography} from '@material-ui/core';

import React from 'react';
import {PropTypes} from 'prop-types';

const SiteViewTemplate = (props) => {
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
            {el['siteCode']}
          </Grid>
          <Grid item xs={6}>
            {el['siteName']}
          </Grid>
          <Grid item xs={6}>
            {el['locationName']}
          </Grid>
          <Grid item xs={6}>
            {props.formData['isActive'] ? el['isActive'] : <Typography variant="subtitle2">Not Active</Typography>}
          </Grid>
          <Grid item xs={6}>
            {el['state']}
          </Grid>
          <Grid item xs={6}>
            {el['country']}
          </Grid>
          <Grid item xs={6}>
            {el['mpa']}
          </Grid>
          <Grid item xs={6}>
            {el['protectionStatus']}
          </Grid>
          <Grid item xs={6}>
            {el['latitude']}
          </Grid>
          <Grid item xs={6}>
            {el['longitude']}
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
          <Grid item xs={12}>
            {el['siteAttribute']}
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

SiteViewTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string,
  formData: PropTypes.object
};

export default SiteViewTemplate;
