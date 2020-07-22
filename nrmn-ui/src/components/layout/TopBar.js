
import React from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import MenuIcon from '@material-ui/icons/Menu';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import clsx from 'clsx';
import { toggleMenu } from './redux-layout';
import { connect } from 'react-redux';
import store from '../store';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
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
  return { menuIsOpen: state.toggle.menuIsOpen };
};

const handleClick = () => {
  store.dispatch(toggleMenu())
}

const ReduxTopBar = ({ menuIsOpen }) => {
  const classes = useStyles();
  const theme = useTheme();
  return (<AppBar
    position="fixed"
    className={clsx(classes.appBar, {
      [classes.appBarShift]: menuIsOpen,
    })}
  >
    <Toolbar>
      <IconButton
        color="inherit"
        aria-label="open drawer"
        onClick={handleClick}
        edge="start"
        className={clsx(classes.menuButton, menuIsOpen && classes.hide)}
      >
        <MenuIcon />
      </IconButton>
      <Typography variant="h6" noWrap>
        NRMN
      </Typography>
    </Toolbar>
  </AppBar>
  )
};

const TopBar = connect(mapStateToProps)(ReduxTopBar);
export default TopBar;