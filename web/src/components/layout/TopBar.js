import React from 'react';
import {AppBar, Box, Toolbar, IconButton} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import AuthState from './AuthState';
import {Typography} from '@mui/material';
import {PropTypes} from 'prop-types';

const TopBar = ({onMenuClick, children}) => {
  return (
    <AppBar>
      <Toolbar style={{minHeight: '10px'}}>
        <IconButton color="inherit" onClick={onMenuClick} edge="start">
          <MenuIcon />
        </IconButton>
        <Box flexGrow={1} display="flex" alignItems="center" justifyContent="center" spacing={1}>
          <img style={{paddingRight: '10px'}} src="/logos/imos.png" alt="IMOS Logo" />
          <Typography variant="button">{children}</Typography>
        </Box>
        <AuthState />
      </Toolbar>
    </AppBar>
  );
};

TopBar.propTypes = {
  onMenuClick: PropTypes.func,
  children: PropTypes.any
};

export default TopBar;
