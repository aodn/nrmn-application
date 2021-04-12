import React from 'react';
import {useSelector} from 'react-redux';
import {BrowserRouter as Router, Switch, Route, Redirect} from 'react-router-dom';
import clsx from 'clsx';
import {ThemeProvider, createMuiTheme, responsiveFontSizes, makeStyles} from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import {blueGrey, deepPurple} from '@material-ui/core/colors';
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
import DiverTemplate from './components/data-entities/DiverTemplate';
import LocationTemplate from './components/templates/LocationTemplate';
import SiteEditTemplate from './components/templates/SiteEditTemplate';
import SiteAddTemplate from './components/templates/SiteAddTemplate';
import SiteViewTemplate from './components/templates/SiteViewTemplate';
import ObservableItemTemplate from './components/templates/ObservableItemTemplate';
import ObservableItemViewTemplate from './components/templates/ObservableItemViewTemplate';
import ObservableItemEditTemplate from './components/templates/ObservableItemEditTemplate';
import SurveyViewTemplate from './components/templates/SurveyViewTemplate';
import SurveyEditTemplate from './components/templates/SurveyEditTemplate';
import ExtractTemplateData from './components/datasheets/ExtractTemplateData';

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
    endpoint: 'locations',
    route: {base: '/reference/location', view: '/reference/location/:id?/:success?', edit: '/reference/location/:id?/edit'},
    schemaKey: {add: 'Location', edit: 'Location', view: 'Location'},
    template: {add: LocationTemplate, edit: LocationTemplate, view: LocationTemplate},
    list: {
      name: 'Locations',
      showNew: true,
      schemaKey: 'Location',
      key: 'locations',
      route: '/reference/locations',
      endpoint: 'locations'
    }
  },
  {
    name: 'Diver',
    idKey: 'diverId',
    can: {delete: false, clone: false},
    flexField: 'fullName',
    endpoint: 'divers',
    route: {base: '/reference/diver', view: '/reference/diver/:id?/:success?', edit: '/reference/diver/:id?/edit'},
    schemaKey: {add: 'Diver', edit: 'Diver', view: 'Diver'},
    template: {add: DiverTemplate, edit: DiverTemplate, view: DiverTemplate},
    list: {
      name: 'Divers',
      showNew: true,
      schemaKey: 'Diver',
      key: 'divers',
      route: '/reference/divers',
      endpoint: 'divers'
    }
  },
  {
    name: 'Site',
    idKey: 'siteId',
    can: {delete: true, clone: true},
    flexField: null,
    endpoint: 'sites',
    route: {base: '/reference/site', view: '/reference/site/:id?/:success?', edit: '/reference/site/:id?/edit'},
    schemaKey: {add: 'SiteGetDto', edit: 'SiteGetDto', view: 'SiteGetDto'},
    template: {add: SiteAddTemplate, edit: SiteEditTemplate, view: SiteViewTemplate},
    list: {
      name: 'Sites',
      showNew: true,
      key: 'siteListItems',
      schemaKey: 'SiteListItem',
      route: '/reference/sites',
      endpoint: 'siteListItems'
    }
  },
  {
    name: 'Observable Item',
    idKey: 'observableItemId',
    can: {edit: true},
    showSpeciesSearch: true,
    flexField: null,
    endpoint: 'reference/observableItem',
    route: {
      base: '/reference/observableItem',
      view: '/reference/observableItem/:id?/:success?',
      edit: '/reference/observableItem/:id?/edit'
    },
    schemaKey: {add: 'ObservableItemDto', edit: 'ObservableItemPutDto', view: 'ObservableItemGetDto'},
    template: {add: ObservableItemTemplate, edit: ObservableItemEditTemplate, view: ObservableItemViewTemplate},
    list: {
      name: 'Observable Items',
      showNew: true,
      key: 'tupleBackedMaps',
      schemaKey: 'ObservableItemRow',
      route: '/reference/observableItems',
      endpoint: 'reference/observableItems',
      headers: [
        'observableItemId',
        'typeName',
        'name',
        'commonName',
        'supersededBy',
        'supersededNames',
        'supersededIDs',
        'phylum',
        'class',
        'order',
        'family',
        'genus'
      ],
      sort: ['obsItemTypeName', 'name']
    }
  },
  {
    hide: true,
    name: 'Survey',
    idKey: 'surveyId',
    can: {edit: true},
    flexField: null,
    endpoint: 'data/survey',
    route: {
      base: '/data/survey',
      view: '/data/survey/:id?/:success?',
      edit: '/data/survey/:id?/edit'
    },
    schemaKey: {edit: 'SurveyDto', view: 'SurveyDto'},
    template: {edit: SurveyEditTemplate, view: SurveyViewTemplate},
    list: {
      name: 'Surveys',
      // key: 'surveys',
      showNew: false,
      schemaKey: 'SurveyRow',
      route: '/data/surveys',
      endpoint: 'data/surveys',
      headers: ['surveyId', 'siteName', 'programName', 'surveyDate', 'surveyTime', 'depth', 'surveyNum'],
      sort: ['siteName']
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
          <SideMenu entities={referenceData.filter((e) => !e.hide)}></SideMenu>
          <main
            className={clsx(classes.mainContent, {
              [classes.contentShift]: leftSideMenuIsOpen
            })}
          >
            <Switch>
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
              <Route exact path="/data/extract" component={ExtractTemplateData} />
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
