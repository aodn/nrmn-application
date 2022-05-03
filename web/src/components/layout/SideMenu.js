import React from 'react';
import {Box, Divider, Drawer, IconButton, List, ListSubheader, Typography, ListItem, ListItemText} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';
import {AuthContext} from '../../contexts/auth-context';

const SideMenu = ({open, onClose}) => {
  const version = process.env.REACT_APP_VERSION ? process.env.REACT_APP_VERSION.split('.') : [0, 0, 0];

  return (
    <Drawer variant="temporary" anchor="left" open={open} onClose={onClose}>
      <Box ml={3}>
        <IconButton color="inherit" onClick={onClose} edge="start">
          <MenuIcon />
        </IconButton>
      </Box>
      <Divider />
      <List>
        <ListItem button onClick={onClose} component={NavLink} to="/home">
          <ListItemText primary="Home" />
        </ListItem>
      </List>
      <Divider />
      <List>
        <ListSubheader>
          <Typography variant="button">Data</Typography>
        </ListSubheader>
        <List component="div" disablePadding>
          <ListItem button onClick={onClose} component={NavLink} to="/data/surveys">
            <ListItemText primary="List Surveys" />
          </ListItem>
          <ListItem button onClick={onClose} component={NavLink} to="/data/jobs">
            <ListItemText primary="List Jobs" />
          </ListItem>
          <ListItem button onClick={onClose} component={NavLink} to="/data/upload">
            <ListItemText primary="Add Job" />
          </ListItem>
          <ListItem button onClick={onClose} component={NavLink} to="/data/extract">
            <ListItemText primary="Template Data" />
          </ListItem>
        </List>
      </List>
      <Divider />
      <List>
        <ListSubheader>
          <Typography variant="button">Reference Data</Typography>
        </ListSubheader>
        <ListItem button onClick={onClose} component={NavLink} to={'/reference/locations'}>
          <ListItemText primary="Locations" />
        </ListItem>
        <ListItem button onClick={onClose} component={NavLink} to={'/reference/divers'}>
          <ListItemText primary="Divers" />
        </ListItem>
        <ListItem button onClick={onClose} component={NavLink} to={'/reference/sites'}>
          <ListItemText primary="Sites" />
        </ListItem>
        <ListItem button onClick={onClose} component={NavLink} to={'/reference/observableItems'}>
          <ListItemText primary="Observable Items" />
        </ListItem>
      </List>
      <List>
        <Divider />
        <ListSubheader>{`Version ${version[0]}.${version[1]} (${version[2]})`}</ListSubheader>
      </List>
      <AuthContext.Consumer>
        {({auth}) => (
          <Box ml={2}>
            {auth.features.map((label, i) => (
              <small key={i}>{label}</small>
            ))}
          </Box>
        )}
      </AuthContext.Consumer>
    </Drawer>
  );
};

SideMenu.propTypes = {
  open: PropTypes.bool,
  onClose: PropTypes.func
};

export default SideMenu;
