import React from 'react';
import {useSelector} from 'react-redux';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';
import clsx from 'clsx';
import {ThemeProvider, createMuiTheme, responsiveFontSizes, makeStyles} from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import {blueGrey, deepPurple} from '@material-ui/core/colors';
import Alert from '@material-ui/lab/Alert';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';
import Login from './components/auth/login';
import XlxsUpload from './components/import/XlxsUpload';
import ValidationPage from './components/import/ValidationJob';
import EntityEdit from './components/data-entities/EntityEdit';
import EntityList from './components/data-entities/EntityList';
import EntityView from './components/data-entities/EntityView';
import Homepage from './components/layout/Homepage';
import FourOFour from './components/layout/FourOFour';
import JobList from './components/job/JobList';
import JobView from './components/job/JobView';
import LocationTemplate from './components/data-entities/LocationTemplate';
import DiverTemplate from './components/data-entities/DiverTemplate';
import SiteEditTemplate from './components/data-entities/SiteEditTemplate';
import SiteAddTemplate from './components/data-entities/SiteAddTemplate';
import SiteViewTemplate from './components/data-entities/SiteViewTemplate';
import ObservableItemTemplate from './components/data-entities/ObservableItemTemplate';

const drawerWidth = process.env.REACT_APP_LEFT_DRAWER_WIDTH ? process.env.REACT_APP_LEFT_DRAWER_WIDTH : 180;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex'
  },
  mainContent: {
    marginTop: 60,
    flexGrow: 1,
    paddingTop: theme.spacing(3),
    paddingLeft: theme.spacing(3),
    paddingRight: theme.spacing(3),
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

const referenceData = [
  {
    name: 'Location',
    idKey: 'locationId',
    can: {delete: false, clone: false},
    flexField: 'locationName',
    route: {base: '/reference/location', view: '/reference/location/:id?/:success?', edit: '/reference/location/:id?/edit'},
    schemaKey: 'Location',
    endpoint: 'locations',
    template: {add: LocationTemplate, edit: LocationTemplate, view: LocationTemplate},
    list: {
      schemaKey: 'Location',
      name: 'locations',
      route: '/reference/locations',
      endpoint: 'locations'
    }
  },
  {
    name: 'Diver',
    idKey: 'diverId',
    can: {delete: false, clone: false},
    flexField: 'fullName',
    route: {base: '/reference/diver', view: '/reference/diver/:id?/:success?', edit: '/reference/diver/:id?/edit'},
    schemaKey: 'Diver',
    endpoint: 'divers',
    template: {add: DiverTemplate, edit: DiverTemplate, view: DiverTemplate},
    list: {
      schemaKey: 'Diver',
      name: 'divers',
      route: '/reference/divers',
      endpoint: 'divers'
    }
  },
  {
    name: 'Site',
    idKey: 'siteId',
    can: {delete: true, clone: true},
    flexField: null,
    route: {base: '/reference/site', view: '/reference/site/:id?/:success?', edit: '/reference/site/:id?/edit'},
    schemaKey: 'SiteGetDto',
    endpoint: 'sites',
    template: {add: SiteAddTemplate, edit: SiteEditTemplate, view: SiteViewTemplate},
    list: {
      name: 'siteListItems',
      schemaKey: 'SiteListItem',
      route: '/reference/sites',
      endpoint: 'siteListItems'
    }
  },
  {
    name: 'Observable Item',
    idKey: 'id',
    can: {edit: false},
    showSpeciesSeach: true,
    flexField: null,
    route: {
      base: '/reference/observableItem',
      view: '/reference/observableItem/:id?/:success?',
      edit: '/reference/observableItem/:id?/edit'
    },
    schemaKey: 'ObservableItemDto',
    endpoint: 'reference/observableItem',
    template: {add: ObservableItemTemplate, edit: false, view: false},
    list: {
      name: 'tupleBackedMaps',
      schemaKey: 'ObservableItemRow',
      route: '/reference/observableItems',
      endpoint: 'reference/observableItems',
      sort: ['obsItemTypeName', 'name']
    }
  }
];

const App = () => {
  const classes = useStyles();
  const leftSideMenuIsOpen = useSelector((state) => state.toggle.leftSideMenuIsOpen);
  const loggedIn = useSelector((state) => state.auth.success);

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
          },
          '.ag-header-cell:hover': {
            '&:hover .menu-icon': {
              opacity: '1 !important'
            }
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
          <SideMenu entities={referenceData}></SideMenu>
          <main
            className={clsx(classes.mainContent, {
              [classes.contentShift]: leftSideMenuIsOpen
            })}
          >
            <Switch>
              <Route
                path="/wip"
                render={() => (
                  <Alert severity="info" variant="filled">
                    This feature is under construction.
                  </Alert>
                )}
              />

              <Route path="/login" component={Login} />
              <Route exact path="/home" component={Homepage} />
              <Route exact path="/404" component={FourOFour}></Route>

              <Redirect exact from="/" to="/home" />
              {!loggedIn ? <Redirect to={`/login?redirect=${window.location.pathname}`} /> : null}

              {/** Authenticated Pages */}
              <Route exact path="/jobs" component={JobList} />
              <Route exact path="/jobs/:id/view" component={JobView} />
              <Route exact path="/validation/:jobId" component={ValidationPage} />
              <Route exact path="/upload" component={XlxsUpload} />
              <Redirect exact from="/list/stagedJob" to="/jobs" />
              {referenceData.map((e) => (
                <Route exact key={e.route.base} path={e.route.base} render={() => <EntityEdit entity={e} template={e.template.add} />} />
              ))}
              {referenceData.map((e) => (
                <Route exact key={e.route.edit} path={e.route.edit} render={() => <EntityEdit entity={e} template={e.template.edit} />} />
              ))}
              {referenceData
                .filter((e) => e.can.clone)
                .map((e) => (
                  <Route
                    exact
                    key={`${e.route.base}/:id?/clone`}
                    path={`${e.route.base}/:id?/clone`}
                    render={() => <EntityEdit entity={e} template={e.template.add} clone />}
                  />
                ))}
              {referenceData.map((e) => (
                <Route exact key={e.route.view} path={e.route.view} render={() => <EntityView entity={e} template={e.template.view} />} />
              ))}
              {referenceData.map((e) => (
                <Route exact key={e.list.route} path={e.list.route} render={() => <EntityList entity={e} />} />
              ))}
              <Route path="*" component={FourOFour}></Route>
            </Switch>
          </main>
        </Router>
      </ThemeProvider>
    </div>
  );
};

export default App;
