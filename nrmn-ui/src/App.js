import React from 'react';
import clsx from 'clsx';
import {
  ThemeProvider ,
  createMuiTheme,
  responsiveFontSizes,
  makeStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';

import {blueGrey, grey} from '@material-ui/core/colors';

import {
  BrowserRouter as Router,
  Switch,
  Route
} from "react-router-dom";

import FileList from './components/import/FileList';
import ImportPage from './components/import/Index';
import UserForm from './components/forms/UserForm';


const drawerWidth = 240;

let theme = createMuiTheme({
  palette: {
    primary: blueGrey,
    secondary: grey
  },
});

theme = responsiveFontSizes(theme);

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  content: {
    marginTop: 50,
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth,
  },
  contentShift: {
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  },
}));

export default function PersistentDrawerLeft() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <ThemeProvider theme={theme}>
        <Router>
          <CssBaseline />
          <TopBar></TopBar>
          <SideMenu></SideMenu>
          <main
            className={clsx(classes.content, {
              [classes.contentShift]: false,
            })}
          >
            <div className={classes.drawerHeader} />
            <Switch>
              <Route path="/import-file/:fileID?" >
              </Route>
              <Route path="/list-file"  >
              </Route>
              <Route path="/form/:entity?" component={UserForm}>
              </Route>
            </Switch>
        </main>
        </Router>
      </ThemeProvider>
    </div>
  );
}
