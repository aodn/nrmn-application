import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Grid from '@material-ui/core/Grid';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import {makeStyles} from '@material-ui/core/styles';
import clsx from 'clsx';
import { toggleLeftSideMenu } from './layout-reducer';
import { connect } from 'react-redux';
import store from '../store';
import AuthState from './AuthState';
import SettingsMenu from './SettingsMenu';
import Button from '@material-ui/core/Button';
import Link from '@material-ui/core/Link';
import {blueGrey} from '@material-ui/core/colors';
import {PropTypes} from 'prop-types';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  header: {
    fontFamily: [
      'Lora'
    ].join(','),
    color: '#FFF',
    fontSize: 'x-large',
    textTransform: 'initial',
    paddingRight: 15,
    paddingLeft: 15,
    '&:hover': {
      color: '#FFF',
      backgroundColor: blueGrey[400]
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
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
  },
  appBarShift: {
    width: `calc(100% - ${drawerWidth}px)`,
    marginLeft: drawerWidth,
    transition: theme.transitions.create(['margin', 'width'], {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    })
  },
  menuButton: {
    marginRight: theme.spacing(2),
  },
  hide: {
    display: 'none',
  },
}));

const mapStateToProps = state => {
  return { leftSideMenuIsOpen: state.toggle.leftSideMenuIsOpen };
};

const handleClick = () => {
  store.dispatch(toggleLeftSideMenu());
};

const ReduxTopBar = ({ leftSideMenuIsOpen }) => {
  const classes = useStyles();

  return (
  <AppBar
    position="fixed"
    className={clsx(classes.appBar, {
      [classes.appBarShift]: leftSideMenuIsOpen,
    })}
  >
    <Toolbar position="static">
      <Grid container alignItems={'center'} justify="space-between" >
        <Grid item >
          <Grid container alignItems={'center'} justify="space-between" >
            <Grid item >
              <IconButton
                color="inherit"
                aria-label="open drawer"
                onClick={handleClick}
                edge="start"
                className={clsx(classes.menuButton, leftSideMenuIsOpen && classes.hide)} >
                <MenuIcon />
              </IconButton>
            </Grid>
            <Grid item>
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
                  >{process.env.REACT_APP_SITE_TITLE}
                  </Button>
                </Grid>
              </Grid>

            </Grid>
          </Grid>
        </Grid>
        <Grid item >
          <AuthState /> |
          <SettingsMenu />
        </Grid>
      </Grid>
    </Toolbar>
  </AppBar>
  );
};
ReduxTopBar.propTypes = {
  leftSideMenuIsOpen : PropTypes.bool
};
const TopBar = connect(mapStateToProps)(ReduxTopBar);
export default TopBar;