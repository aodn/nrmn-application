import {Typography} from '@mui/material';
import {NavLink} from 'react-router-dom';
import React from 'react';
import PropTypes from 'prop-types';

const BackButton = (props) => {
  return (
    <NavLink to={props.goBackTo} onClick={props.onClick}>
      <Typography
        variant="button"
        sx={{textTransform: 'none', fontSize: '1rem', fontWeight: 400 }}
      >
        {'<< Back to ' + props.name}
      </Typography>
    </NavLink>
  );
};

BackButton.propTypes = {
  goBackTo: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  onClick: PropTypes.func
};

export default BackButton;