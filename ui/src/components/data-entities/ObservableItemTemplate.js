import {Box, Button, Checkbox, Grid, Typography} from '@material-ui/core';
import {useDispatch} from 'react-redux';
import {wormsSearchRequested} from './form-reducer';

import React from 'react';
import {PropTypes} from 'prop-types';

const ObservableItemTemplate = ({properties, title}) => {
  const elCommonName = properties.find((p) => p.name === 'commonName').content;
  const elAphiaRef = properties.find((p) => p.name === 'aphiaRef').content;
  const elAphiaRelType = properties.find((p) => p.name === 'aphiaRelType').content;

  const elPhylum = properties.find((p) => p.name === 'phylum').content;
  const elClass = properties.find((p) => p.name === 'clazz').content;
  const elOrder = properties.find((p) => p.name === 'order').content;
  const elFamily = properties.find((p) => p.name === 'family').content;
  const elGenus = properties.find((p) => p.name === 'genus').content;

  const dispatch = useDispatch();

  return (
    <>
      <Box component="div" width={600}>
        <h1>{title}</h1>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {elCommonName}
          </Grid>
        </Grid>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            {elAphiaRef}
          </Grid>
          <Grid item xs={6}>
            {elAphiaRelType}
          </Grid>
          <Button
            variant="contained"
            style={{minWidth: '100%', textTransform: 'none'}}
            onClick={() => {
              const s = elAphiaRef.props.formData?.split('/');
              if (s.length > 0) {
                const id = s[s.length - 1];
                dispatch(wormsSearchRequested({aphia_id: id}));
              }
            }}
            color="primary"
          >
            Search WoRMS
          </Button>
          <Grid container direction="row" justify="center" alignItems="center" alignContent="center" xs={12} m={20}>
            <Typography h6>Overrides</Typography>
          </Grid>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              {elPhylum}
            </Grid>
            <Grid item xs={6}>
              {elClass}
            </Grid>
            <Grid item xs={6}>
              {elOrder}
            </Grid>
            <Grid item xs={6}>
              {elFamily}
            </Grid>
            <Grid item xs={6}>
              {elGenus}
            </Grid>
            <Grid item xs={6}>
              <Checkbox></Checkbox>
            </Grid>
          </Grid>
        </Grid>
      </Box>
    </>
  );
};

ObservableItemTemplate.propTypes = {
  properties: PropTypes.any,
  title: PropTypes.string
};

export default ObservableItemTemplate;
