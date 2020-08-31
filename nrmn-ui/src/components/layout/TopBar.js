
import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Switch from "@material-ui/core/Switch";
import Grid from '@material-ui/core/Grid';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import { toggleMenu } from './redux-layout';
import { toggleTheme } from '../import/reducers/theme-reducer';
import { connect } from 'react-redux';
import store from '../store';
import AuthState from "./AuthState";
import { TopbarButton } from './TopbarButton'

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
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
  return { menuIsOpen: state.toggle.menuIsOpen };
};

const handleClick = () => {
  store.dispatch(toggleMenu())
}

const handleToggleThemeChange = event => {
  store.dispatch(toggleTheme(event.target.checked))
}

const ReduxTopBar = ({ menuIsOpen }) => {
  const classes = useStyles();

  return (
  <AppBar
    position="fixed"
    className={clsx(classes.appBar, {
      [classes.appBarShift]: menuIsOpen,
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
                className={clsx(classes.menuButton, menuIsOpen && classes.hide)} >
                <MenuIcon />
              </IconButton>
            </Grid>
            <Grid item >
              <Typography
                  variant="h5"
                  className={clsx(classes.header)}
                  noWrap>
                {process.env.REACT_APP_SITE_TITLE}
              </Typography>
            </Grid>
          </Grid>
        </Grid>
        <Grid item >
          <AuthState /> |
          <TopbarButton
              variant="text"
              color="secondary"
              size="small"
              >Notifications</TopbarButton>
          <Switch title="Dark/Light theme toggle" onChange={handleToggleThemeChange} size="small" checked={store.getState().theme.themeType}/>
        </Grid>
      </Grid>
    </Toolbar>
  </AppBar>
  )
};

const TopBar = connect(mapStateToProps)(ReduxTopBar);
export default TopBar;