import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Grid from '@material-ui/core/Grid';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import { makeStyles } from '@material-ui/core/styles';
import clsx from 'clsx';
import { toggleLeftSideMenu } from './layout-reducer';
import { useDispatch, useSelector } from 'react-redux';
import AuthState from './AuthState';
import SettingsMenu from './SettingsMenu';

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




const TopBar = () => {
  const classes = useStyles();
  const leftSideMenuIsOpen = useSelector(state => state.toggle.leftSideMenuIsOpen);
  const dispatch = useDispatch();

  const handleClick = (dis) => {
    dis(toggleLeftSideMenu());
  };

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
                  onClick={() => handleClick(dispatch)}
                  edge="start"
                  className={clsx(classes.menuButton, leftSideMenuIsOpen && classes.hide)} >
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
            <AuthState />
            <SettingsMenu />
          </Grid>
        </Grid>
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;