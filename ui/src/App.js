import React from 'react';
import clsx from 'clsx';
import {ThemeProvider, createMuiTheme, responsiveFontSizes, makeStyles} from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';
import Login from './components/auth/login';
import {blueGrey, deepPurple} from '@material-ui/core/colors';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';
import XlxsUpload from './components/import/XlxsUpload';
import ValidationPage from './components/import/ValidationJob';
import GenericForm from './components/data-entities/GenericForm';
import {useSelector} from 'react-redux';
import EntityList from './components/data-entities/EntityList';
import GenericDetailsView from './components/data-entities/GenericDetailsView';
import Homepage from './components/layout/Homepage';
import FourOFour from './components/layout/FourOFour';
import JobList from './components/job/JobList';
import JobView from './components/job/JobView';
const drawerWidth = process.env.REACT_APP_LEFT_DRAWER_WIDTH ? process.env.REACT_APP_LEFT_DRAWER_WIDTH : 180;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex'
  },
  mainContent: {
    marginTop: 50,
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen
    }),
    marginLeft: `-${drawerWidth}px`
  },
  contentShift: {
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen
    }),
    marginLeft: 0
  }
}));

const entityDefinitions = [
  {
    name: 'Locations',
    entityName: 'Location',
    entityListName: 'locations',
    createButtonPath: '/edit/locations',
    entityLinkPath: 'view/location/{}'
  },
  {
    name: 'Divers',
    entityName: 'Diver',
    entityListName: 'divers',
    createButtonPath: '/edit/divers',
    entityLinkPath: 'view/diver/{}'
  },
  {
    name: 'Sites',
    entityName: 'Site',
    entityListName: 'sites',
    createButtonPath: '/edit/sites',
    entityLinkPath: 'view/sites/{}'
  },
  {
    name: 'Observable Items',
    entityName: 'ObservableItem',
    entityListName: 'observableItems',
    createButtonPath: '/edit/observableItems',
    entityLinkPath: 'view/observableItems/{}'
  }
];

const App = () => {
  const classes = useStyles();
  const leftSideMenuIsOpen = useSelector((state) => state.toggle.leftSideMenuIsOpen);

  let theme = createMuiTheme({
    palette: {
      text: {
        primary: '#607d8b',
        secondary: '#555'
      },
      primary: blueGrey,
      secondary: {
        main: deepPurple[300]
      }
    },
    props: {
      MuiTextField: {
        variant: 'outlined',
        margin: 'dense',
        notched: 'true'
      }
    },
    overrides: {
      MuiCssBaseline: {
        '@global': {
          '.ag-root-wrapper-body': {
            minHeight: 400
          }
        }
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
          <SideMenu entities={entityDefinitions}></SideMenu>
          <main
            className={clsx(classes.mainContent, {
              [classes.contentShift]: leftSideMenuIsOpen
            })}
          >
            <Switch>
              <Route exact path="/home" component={Homepage} />
              <Route exact path="/jobs" component={JobList} />
              <Route exact path="/jobs/:id/view" component={JobView} />
              <Route exact path="/validation/:jobId" component={ValidationPage} />
              <Route exact path="/upload" component={XlxsUpload} />
              <Route exact path="/login" component={Login} />
              <Redirect exact from="/list/stagedJob" to="/jobs" />
              {entityDefinitions.map((e, i) => (
                <Route exact key={`E${i}`} path={`/edit/${e.name}/:id?`} render={() => <GenericForm entity={e} />} />
              ))}
              {entityDefinitions.map((e, i) => (
                <Route exact key={`V${i}`} path={`/view/${e.name}/:id?`} render={() => <GenericDetailsView entity={e} />} />
              ))}
              {entityDefinitions.map((e, i) => (
                <Route exact key={`L${i}`} path={`/list/${e.name}`} render={() => <EntityList entity={e} />} />
              ))}
              <Route path="/404" component={FourOFour}></Route>
              <Redirect exact from="/" to="/home" />
              <Route path="*" component={FourOFour}></Route>
            </Switch>
          </main>
        </Router>
      </ThemeProvider>
    </div>
  );
};

export default App;
