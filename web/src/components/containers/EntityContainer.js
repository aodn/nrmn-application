import React from 'react';
import {Box, Grid, Typography} from '@mui/material';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';

const EntityContainer = (props) => (
  <>
    <Box m={1}>
      <NavLink to={props.goBackTo}>
        <Typography>{'<< Back to ' + props.name}</Typography>
      </NavLink>
    </Box>
    <Grid container justifyContent="center">
      {props.header}
      <Box style={{background: 'white', width: props.containerWidth}} boxShadow={1} margin={3} padding={3}>
        <Grid container alignItems="flex-start" direction="row">
          {props.children}
        </Grid>
      </Box>
    </Grid>
  </>
);

EntityContainer.propTypes = {
  name: PropTypes.string.isRequired,
  containerWidth: PropTypes.string,
  goBackTo: PropTypes.string.isRequired,
  children: PropTypes.any,
  header: PropTypes.any
};

EntityContainer.defaultProps = {
  containerWidth: '50%',
};

export default EntityContainer;
