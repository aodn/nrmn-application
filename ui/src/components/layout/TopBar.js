import React from 'react';
import {AppBar, Box, Toolbar, IconButton} from '@material-ui/core';
import MenuIcon from '@material-ui/icons/Menu';
import AuthState from './AuthState';
import {Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const TopBar = ({onMenuClick}) => {
  return (
    <AppBar position="relative">
      <Toolbar style={{minHeight: '48px'}}>
        <IconButton color="inherit" onClick={onMenuClick} edge="start">
          <MenuIcon />
        </IconButton>
        <Box flexGrow={1} display="flex" alignItems="center" justifyContent="center" spacing={2}>
          <img style={{paddingRight: '10px'}} src="/logos/imos.png" alt="IMOS Logo" />
          <Typography variant="button">National Reef Monitoring Network</Typography>
        </Box>
        <AuthState />
      </Toolbar>
    </AppBar>
  );
};

TopBar.propTypes = {
  onMenuClick: PropTypes.func
};

export default TopBar;
