import React from 'react';
import {PropTypes} from 'prop-types';
import {Grid} from '@material-ui/core';

const ObservableItemTemplate = ({properties}) => {
  const el = {};
  properties.map((e) => {
    el[e.name] = e.content;
  });

  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={6}>
          {el['aphiaId']}
        </Grid>
        <Grid item xs={6}>
          {el['observableItemName']}
        </Grid>
        <Grid item xs={6}>
          {el['obsItemTypeId']}
        </Grid>
        <Grid item xs={6}>
          {el['commonName']}
        </Grid>
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
          {el['speciesEpithet']}
        </Grid>
        <Grid item xs={6}>
          {el['letterCode']}
        </Grid>
        <Grid item xs={6}>
          {el['reportGroup']}
        </Grid>
        <Grid item xs={6}>
          {el['habitatGroups']}
        </Grid>
        <Grid item xs={6}>
          {el['supersededBy']}
        </Grid>
      </Grid>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;
