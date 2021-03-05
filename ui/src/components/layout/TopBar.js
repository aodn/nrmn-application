import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import {makeStyles} from '@material-ui/core/styles';
import clsx from 'clsx';
import {toggleLeftSideMenu} from './layout-reducer';
import {useDispatch, useSelector} from 'react-redux';
import AuthState from './AuthState';
import {Typography, useMediaQuery} from '@material-ui/core';

const drawerWidth = process.env.REACT_APP_LEFT_DRAWER_WIDTH ? process.env.REACT_APP_LEFT_DRAWER_WIDTH : 180;

const useStyles = makeStyles((theme) => ({
  toolbar: {
    flexGrow: 1
  },
  header: {
    fontFamily: ['Roboto'].join(','),
    color: '#FFF',
    flexGrow: 1,
    fontSize: 'x-large',
    fontWeight: 500,
    textAlign: 'left',
    textTransform: 'initial'
  },
  logo: {
    maxWidth: 100,
    height: 'auto',
    marginLeft: 25,
    marginRight: 25
  },
  appBar: {
    zIndex: theme.zIndex.drawer + 1,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen
    })
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: `${drawerWidth}px`,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen
    })
  },
  menuButton: {},
  hide: {
    display: 'none'
  }
}));

const TopBar = () => {
  const classes = useStyles();
  const leftSideMenuIsOpen = useSelector((state) => state.toggle.leftSideMenuIsOpen);
  const dispatch = useDispatch();
  const matches = useMediaQuery((theme) => theme.breakpoints.up('md'));

  const handleClick = () => {
    dispatch(toggleLeftSideMenu());
  };

  return (
    <AppBar
      position="fixed"
      className={clsx(classes.appBar, {
        [classes.appBarShift]: leftSideMenuIsOpen
      })}
    >
      <Toolbar position="static">
        <IconButton
          color="inherit"
          aria-label="open drawer"
          onClick={handleClick}
          edge="start"
          className={clsx(classes.menuButton, leftSideMenuIsOpen && classes.hide)}
        >
          <MenuIcon />
        </IconButton>
        <img className={clsx(classes.logo)} src={'https://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png'} alt={'IMOS Logo'} />
        <Typography className={clsx(classes.header)}>{matches ? process.env.REACT_APP_SITE_TITLE : 'NRMN'}</Typography>
        <AuthState />
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;
