import React from 'react';
import {makeStyles, useTheme} from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import List from '@material-ui/core/List';
import ListSubheader from '@material-ui/core/ListSubheader';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import {useDispatch, useSelector} from 'react-redux';
import {toggleLeftSideMenu} from './layout-reducer';
import {NavLink} from 'react-router-dom';
import store from '../store';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';
import {toggleTheme} from './theme-reducer';

const drawerWidth = process.env.REACT_APP_LEFT_DRAWER_WIDTH ? process.env.REACT_APP_LEFT_DRAWER_WIDTH : 180;

const useStyles = makeStyles((theme) => ({
  drawer: {
    width: `${drawerWidth}px`,
    flexShrink: 0
  },
  drawerPaper: {
    width: `${drawerWidth}px`
  },
  drawerHeader: {
    display: 'flex',
    alignItems: 'center',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar,
    justifyContent: 'flex-end'
  }
}));

const SideMenu = () => {
  const classes = useStyles();
  const theme = useTheme();
  const leftSideMenuIsOpen = useSelector((state) => state.toggle.leftSideMenuIsOpen);
  const dispatch = useDispatch();
  const auth = store.getState().auth;

  const handleMainMenu = () => {
    dispatch(toggleLeftSideMenu());
  };

  return (
    <Drawer
      className={classes.drawer}
      variant="persistent"
      anchor="left"
      open={leftSideMenuIsOpen}
      classes={{
        paper: classes.drawerPaper
      }}
    >
      <div className={classes.drawerHeader}>
        <IconButton onClick={handleMainMenu}>{theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}</IconButton>
      </div>
      {auth.success && (
        <>
          <Divider />
          <List>
            <ListSubheader>DATA</ListSubheader>
            <List component="div" disablePadding>
              <ListItem button onClick={handleMainMenu} className={classes.nested} component={NavLink} aweirdprop="true" to="/jobs">
                <ListItemText primary="List Jobs" />
              </ListItem>
              <ListItem button onClick={handleMainMenu} className={classes.nested} component={NavLink} to="/upload">
                <ListItemText primary="Add Job" />
              </ListItem>
            </List>
          </List>
          <Divider />
          <List>
            <ListSubheader>REFERENCE DATA</ListSubheader>
            {['Diver', 'Location', 'ObservableItem', 'Program', 'Site'].map((text) => (
              <ListItem button key={text} component={NavLink} to={'/list/' + text}>
                <ListItemText primary={text} />
              </ListItem>
            ))}
          </List>
        </>
      )}
      <Divider />
      <List>
        <FormControlLabel
          label="Theme"
          labelPlacement="start"
          control={
            <Switch
              title="Dark/Light theme toggle"
              onChange={(e) => {
                store.dispatch(toggleTheme(e.target.checked));
              }}
              checked={store.getState().theme.themeType}
            />
          }
        />
      </List>
    </Drawer>
  );
};

export default SideMenu;
