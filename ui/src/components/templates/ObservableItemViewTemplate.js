import React from 'react';
import {PropTypes} from 'prop-types';
import {Box, Divider, Grid, Typography} from '@material-ui/core';

const ObservableItemTemplate = ({properties}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Box pt={2} pb={2}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['observableItemId']}
          </Grid>
          <Grid item xs={6}>
            {el['observableItemName']}
          </Grid>
          <Grid item xs={6}>
            {el['obsItemTypeName']}
          </Grid>
          <Grid item xs={6}>
            {el['commonName']}
          </Grid>
          <Grid item xs={6}>
            {el['aphiaId']}
          </Grid>
          <Grid item xs={6}>
            {el['aphiaRelTypeName']}
          </Grid>
          <Grid item xs={6}>
            {el['supersededBy']}
          </Grid>
          <Grid item xs={6}>
            {el['supersededNames']}
          </Grid>
          <Grid item xs={6}>
            {el['supersededIds']}
          </Grid>
          <Grid item xs={6}>
            {el['letterCode']}
          </Grid>
        </Grid>
      </Box>
      <Divider />
      <Box pt={2} pb={2}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['phylum']}
          </Grid>
          <Grid item xs={6}>
            {el['class']}
          </Grid>
          <Grid item xs={6}>
            {el['order']}
          </Grid>
          <Grid item xs={6}>
            {el['family']}
          </Grid>
          <Grid item xs={6}>
            {el['genus']}
          </Grid>
          <Grid item xs={6}>
            {el['reportGroup']}
          </Grid>
          <Grid item xs={6}>
            {el['habitatGroups']}
          </Grid>
          <Grid item xs={6}>
            {el['speciesEpithet']}
          </Grid>
        </Grid>
      </Box>
      <Divider />
      <Box pt={2} pb={2}>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {el['lengthWeightA']}
          </Grid>
          <Grid item xs={6}>
            {el['lengthWeightB']}
          </Grid>
          <Grid item xs={6}>
            {el['lengthWeightCf']}
          </Grid>
        </Grid>
      </Box>
      <Box pt={2} pb={2}>
        {el['obsItemAttribute'].props.formData ? (
          el['obsItemAttribute']
        ) : (
          <Typography variant="subtitle2" component="i">
            No Other Attributes
          </Typography>
        )}
      </Box>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;
