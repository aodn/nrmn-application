import React from 'react';
import {Box, Divider, Drawer, IconButton, List, ListSubheader, Typography, ListItem, ListItemText} from '@material-ui/core';
import MenuIcon from '@material-ui/icons/Menu';
import {NavLink} from 'react-router-dom';
import {PropTypes} from 'prop-types';

const SideMenu = ({entities, open, onClose}) => {
  const version = process.env.REACT_APP_VERSION && process.env.REACT_APP_VERSION.split('.');
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
          <ListItem button onClick={onClose} component={NavLink} to="/jobs">
            <ListItemText primary="List Jobs" />
          </ListItem>
          <ListItem button onClick={onClose} component={NavLink} to="/upload">
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
        {entities.map((e) => (
          <ListItem button onClick={onClose} key={e.list.name} component={NavLink} to={e.list.route}>
            <ListItemText primary={e.list.name} />
          </ListItem>
        ))}
      </List>
      <List>
        <Divider />
        {version && <ListSubheader>{`Version ${version[0]}.${version[1]} (${version[2]})`}</ListSubheader>}
      </List>
    </Drawer>
  );
};

SideMenu.propTypes = {
  entities: PropTypes.array,
  open: PropTypes.bool,
  onClose: PropTypes.func
};

export default SideMenu;
