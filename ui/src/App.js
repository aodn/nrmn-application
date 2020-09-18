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
import { useSelector} from "react-redux";
import green from "@material-ui/core/colors/green";


const drawerWidth = 240;

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
  }
}));

export default function App()  {
  const classes = useStyles();

  const themeState = useSelector(state =>  state.theme);

  let theme = createMuiTheme({
    palette: {
      primary: blueGrey,
      secondary: grey,
      type: themeState.themeType ? "dark" : "light",
    },
    props: {
      MuiTable: {
        size: themeState.tableSize
      }
    }
  });
  theme = responsiveFontSizes(theme);

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
            <Route path={["/import-file/:fileID?"]} component={ImportPage}>
              </Route>
            <Route path="/list-file" component={FileList} >
            <Route path="/form/:entity?" component={UserForm}>
              </Route>
            </Switch>
        </main>
        </Router>
      </ThemeProvider>
    </div>
  );
}

