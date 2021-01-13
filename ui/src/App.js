import React from 'react';
import clsx from 'clsx';
import {
  ThemeProvider,
  createMuiTheme,
  responsiveFontSizes,
  makeStyles
} from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';
import Login from './components/auth/login';
import { blueGrey, deepPurple } from '@material-ui/core/colors';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Redirect
} from 'react-router-dom';
import XlxsUpload from './components/import/XlxsUpload';
import ValidationPage from './components/import/ValidationJob';
import GenericForm from './components/data-entities/GenericForm';
import EntityList from './components/data-entities/EntityList';
import { getFullPath } from './components/utils/helpers';
import AlertTitle from '@material-ui/lab/AlertTitle';
import {useSelector} from 'react-redux';
import Alert from '@material-ui/lab/Alert';
import GenericDetailsView from './components/data-entities/GenericDetailsView';
import Homepage from './components/layout/Homepage';

const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },
  mainContent: {
    marginTop: 50,
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth
  },
  contentShift: {
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  }
}));

const App = () => {
  const classes = useStyles();
  const themeState = useSelector(state => state.theme);
  const leftSideMenuIsOpen = useSelector(state => state.toggle.leftSideMenuIsOpen);

  let theme = createMuiTheme({
    palette: {
      primary: blueGrey,
      secondary: {
        main: deepPurple[300]
      },
      type: themeState.themeType ? 'dark' : 'light',
    },
    props: {
      MuiTextField: {
        variant: 'outlined',
        margin: 'dense',
        notched: 'true',
      }
    },
    overrides: {
      MuiCssBaseline: {
        '@global': {
          '.ag-root-wrapper-body': {
            minHeight: 400
          }

        },
      },
    },
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
            className={clsx(classes.mainContent, {
              [classes.contentShift]: leftSideMenuIsOpen
            })}
          >
            <Switch>
              {/* <Route exact path='/' render={() => (
                <Redirect to='/upload' />
              )} /> */}
              <Route path='/validation/:jobId' component={ValidationPage} />
              <Route path='/upload'component={XlxsUpload} />
              <Route path='/login' component={Login} />
              <Route path='/form/:entityName/:id?' component={GenericForm} />
              <Route path="/view/:entityName/:id?" component={GenericDetailsView} />
              <Route path='/list/:entityName' component={EntityList} />
              <Route path='/notfound' render={(props) =>
                <Alert severity='error'  >
                  <AlertTitle>API Resource Not Found</AlertTitle>
                  {`The requested resource ${getFullPath(props.location)} is not available`}
                </Alert>}
              />
              <Route path={'/'} component={Homepage} />
              <Route render={(props) =>
                <Alert severity='error'  >
                  <AlertTitle>Path Not Found</AlertTitle>
                    The requested resource <strong>{getFullPath(props.location)}</strong> is not available
                  </Alert>}
              />
            </Switch>
          </main>
        </Router>
      </ThemeProvider>
    </div>
  );
};

export default App;

