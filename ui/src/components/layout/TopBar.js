import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Grid from '@material-ui/core/Grid';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import { toggleLeftSideMenu } from './layout-reducer';
import Button from '@material-ui/core/Button';
import { useDispatch, useSelector } from 'react-redux';
import AuthState from './AuthState';
import SettingsMenu from './SettingsMenu';
import { useMediaQuery } from '@material-ui/core';

const drawerWidth = process.env.REACT_APP_LEFT_DRAWER_WIDTH ?
  process.env.REACT_APP_LEFT_DRAWER_WIDTH : 180;

const useStyles = makeStyles((theme) => ({
  header: {
    fontFamily: [
      'Roboto'
    ].join(','),
    color: '#FFF',
    fontSize: 'x-large',
    textTransform: 'initial',
    paddingRight: 15,
    paddingLeft: 15,
    '&:hover': {
      color: '#FFF',
      backgroundColor: 'rgba(0, 0, 0, 0.04)'
    }
  },
  spacer: {
    '& > *': {
      marginLeft: 10,
      marginRight: 10,
      padding: 10
    }
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: `${drawerWidth}px`,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    })
  },
  menuButton: {
  },
  hide: {
    display: 'none',
  },
}));




const TopBar = () => {
  const classes = useStyles();
  const leftSideMenuIsOpen = useSelector(state => state.toggle.leftSideMenuIsOpen);
  const dispatch = useDispatch();
  const matches = useMediaQuery(theme => theme.breakpoints.up('lg'));


  const handleClick = () => {
    dispatch(toggleLeftSideMenu());
  };

  return (
    <AppBar
      position="fixed"
      className={clsx(classes.appBar, {
        [classes.appBarShift]: leftSideMenuIsOpen,
      })}
    >
      <Toolbar position="static">
        <IconButton
          color="inherit"
          aria-label="open drawer"
          onClick={handleClick}
          edge="start"
          className={clsx(classes.menuButton, leftSideMenuIsOpen && classes.hide)} >
          <MenuIcon />
        </IconButton>
        <Grid container
          justify="flex-start"
          alignItems="center">
          <Grid item className={classes.spacer}>
            <img
              width={100}
              src={'https://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png'}
              alt={'IMOS Logo'}
            />
          </Grid>
          <Grid item>
            <Button
              className={clsx(classes.header)}
              nowrap="true"
              href="/"
            >{ (matches) ? process.env.REACT_APP_SITE_TITLE : 'NRMN'}
            </Button>
          </Grid>
        </Grid>
        <Grid container justify="flex-end"
          alignItems="center">
          <Grid item>
            <AuthState />
          </Grid>
          <Grid item>

            <SettingsMenu />
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;