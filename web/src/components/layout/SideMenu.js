import React from 'react';
import {Box, Divider, Drawer, IconButton, List, ListItemButton, ListItemText, ListSubheader, Stack, Typography} from '@mui/material';
import Chip from '@mui/material/Chip';
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
        <ListItemButton onClick={onClose} component={NavLink} to="/home">
          <ListItemText primary="Home" />
        </ListItemButton>
      </List>
      <Divider />
      <List>
        <ListSubheader>
          <Typography variant="button">Data</Typography>
        </ListSubheader>
        <List component="div" disablePadding>
          <ListItemButton onClick={onClose} component={NavLink} to="/data/surveys" state={{resetFilters: true}}>
            <ListItemText primary="List Surveys" />
          </ListItemButton>
          <ListItemButton onClick={onClose} component={NavLink} to="/data/jobs" state={{resetFilters: true}}>
            <ListItemText primary="List Jobs" />
          </ListItemButton>
          <ListItemButton onClick={onClose} component={NavLink} to="/data/upload">
            <ListItemText primary="Add Job" />
          </ListItemButton>
          <AuthContext.Consumer>
            {({auth}) =>
              !auth?.features?.includes('corrections') && (
                <ListItemButton onClick={onClose} component={NavLink} to="/data/species">
                  <ListItemText primary="Species Correction" />
                </ListItemButton>
              )
            }
          </AuthContext.Consumer>
        </List>
      </List>
      <Divider />
      <List>
        <ListSubheader>
          <Typography variant="button">Reference Data</Typography>
        </ListSubheader>
        <ListItemButton onClick={onClose} component={NavLink} to={'/reference/locations'} state={{resetFilters: true}}>
          <ListItemText primary="Locations" />
        </ListItemButton>
        <ListItemButton onClick={onClose} component={NavLink} to={'/reference/divers'} state={{resetFilters: true}}>
          <ListItemText primary="Divers" />
        </ListItemButton>
        <ListItemButton onClick={onClose} component={NavLink} to={'/reference/sites'} state={{resetFilters: true}}>
          <ListItemText primary="Sites" />
        </ListItemButton>
        <ListItemButton onClick={onClose} component={NavLink} to={'/reference/observableItems'} state={{resetFilters: true}}>
          <ListItemText primary="Observable Items" />
        </ListItemButton>
      </List>
      <Divider />
      <List>
        <ListSubheader>
          <Typography variant="button">Extract</Typography>
        </ListSubheader>
        <ListItemButton onClick={onClose} component={NavLink} to="/data/share">
          <ListItemText primary="Endpoint Links" />
        </ListItemButton>
        <ListItemButton onClick={onClose} component={NavLink} to="/data/extract">
          <ListItemText primary="Template Data" />
        </ListItemButton>
      </List>
      <Divider />
      <AuthContext.Consumer>
        {({auth}) => (
          <Stack direction="column" spacing={1} m={2}>
            <Chip color="primary" size="small" label={`Version ${version[0]}.${version[1]} (${version[2]})`} />
            {auth?.features?.map((label, i) => (
              <Chip key={i} color="primary" variant="outlined" size="small" label={label} />
            ))}
          </Stack>
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
