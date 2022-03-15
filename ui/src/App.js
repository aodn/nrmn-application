import React, {useState} from 'react';
import {useSelector} from 'react-redux';
import {BrowserRouter as Router, Routes, Route, Navigate} from 'react-router-dom';
import Alert from '@material-ui/lab/Alert';
import {ThemeProvider, unstable_createMuiStrictModeTheme as createMuiTheme, responsiveFontSizes} from '@material-ui/core/styles';
import Box from '@material-ui/core/Box';
import {blueGrey, deepPurple} from '@material-ui/core/colors';
import {LicenseManager} from 'ag-grid-enterprise';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';
import Login from './components/auth/login';
import JobUpload from './components/import/JobUpload';
import ValidationPage from './components/import/ValidationJob';
import EntityEdit from './components/data-entities/EntityEdit';
import EntityList from './components/data-entities/EntityList';
import EntityView from './components/data-entities/EntityView';
import Homepage from './components/layout/Homepage';
import JobList from './components/job/JobList';
import JobView from './components/job/JobView';
import AppContent from './components/containers/AppContent';
import SiteViewTemplate from './components/templates/SiteViewTemplate';
import LocationList from './components/data-entities/location/LocationList';
import SurveyList from './components/data-entities/survey/SurveyList';
import DiverList from './components/data-entities/diver/DiverList';
import ObservableItemEdit from './components/data-entities/ObservableItemEdit';
import ObservableItemTemplate from './components/templates/ObservableItemTemplate';
import ObservableItemViewTemplate from './components/templates/ObservableItemViewTemplate';
import LocationTemplate from './components/templates/LocationTemplate';
import ExtractTemplateData from './components/datasheets/ExtractTemplateData';
import SurveyViewTemplate from './components/templates/SurveyViewTemplate';
import SurveyEditTemplate from './components/templates/SurveyEditTemplate';
import DiverTemplate from './components/data-entities/DiverTemplate';
import SiteEdit from './components/data-entities/SiteEdit';
import ObservableItemAdd from './components/data-entities/ObservableItemAdd';
import LocationView from './components/data-entities/location/LocationView';
import LocationAdd from './components/data-entities/location/LocationAdd';

const referenceData = [
  {
    name: 'Location',
    idKey: 'locationId',
    can: {delete: false, clone: false},
    flexField: 'locationName',
    endpoint: 'locations',
    route: {base: '/reference/location', view: '/reference/location/:id', edit: '/reference/location/:id/edit'},
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
    endpoint: 'diver',
    route: {base: '/reference/diver', view: '/reference/diver/:id', edit: '/reference/diver/:id/edit'},
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
    route: {base: '/reference/site', view: '/reference/site/:id', edit: '/reference/site/:id/edit'},
    schemaKey: {add: 'SiteGetDto', edit: 'SiteGetDto', view: 'SiteGetDto'},
    template: {add: SiteViewTemplate, edit: SiteViewTemplate, view: SiteViewTemplate},
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
      view: '/reference/observableItem/:id',
      edit: '/reference/observableItem/:id/edit'
    },
    schemaKey: {add: 'ObservableItemDto', edit: 'ObservableItemPutDto', view: 'ObservableItemGetDto'},
    template: {add: ObservableItemTemplate, edit: ObservableItemEdit, view: ObservableItemViewTemplate},
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
      view: '/data/survey/:id',
      edit: '/data/survey/:id/edit'
    },
    schemaKey: {edit: 'SurveyDto', view: 'SurveyDto'},
    template: {edit: SurveyEditTemplate, view: SurveyViewTemplate},
    list: {
      name: 'Surveys',
      showNew: false,
      schemaKey: 'SurveyRow',
      route: '/data/surveys',
      endpoint: 'data/surveys',
      headers: [
        'surveyId',
        'siteCode',
        'siteName',
        'programName',
        'locationName',
        'hasPQs',
        'mpa',
        'country',
        'diverName',
        'surveyDate',
        'surveyTime',
        'depth',
        'surveyNum'
      ],
      sort: ['surveyId', 'siteCode', 'siteName', 'programName', 'locationName', 'hasPQs', 'mpa', 'country', 'diverName', 'surveyDate']
    }
  }
];

const App = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [filterModel, setFilterModel] = React.useState({});
  const expires = useSelector((state) => state.auth.expires);
  const [applicationError, setApplicationError] = useState(null);
  window.setApplicationError = setApplicationError;

  const loggedIn = Date.now() < expires;

  LicenseManager.setLicenseKey(JSON.parse(localStorage.getItem('gridLicense')));

  let theme = createMuiTheme({
    palette: {
      primary: blueGrey,
      secondary: {
        main: deepPurple[300]
      },
      text: {
        primary: '#607d8b',
        secondary: '#999'
      },
      warning: {main: '#d32f2f'}
    },
    typography: {
      body2: {
        fontSize: '12px'
      }
    },
    props: {
      MuiTextField: {
        variant: 'outlined',
        margin: 'dense',
        notched: 'true'
      },
      MuiButton: {
        disableRipple: true,
        variant: 'contained',
        color: 'primary',
        ml: '4px'
      }
    },
    overrides: {
      MuiCssBaseline: {
        '@global': {
          '.ag-root-wrapper-body': {
            height: '100%'
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
    <ThemeProvider theme={theme}>
      <Router>
        <AppContent>
          <SideMenu open={menuOpen} onClose={() => setMenuOpen(false)} entities={referenceData.filter((e) => !e.hide)}></SideMenu>
          <TopBar onMenuClick={() => setMenuOpen(true)}></TopBar>
          {applicationError && (
            <Box m={2}>
              <Alert severity="error" variant="filled">
                {applicationError}
                <br />
                The server may be experiencing problems. Please wait a moment and try again.
                <br />
                If this problem persists, please contact info@aodn.org.au.
              </Alert>
            </Box>
          )}
          <Routes>
            <Route path="/home" element={<Homepage />} />

            <Route path="/" element={<Navigate to="/home" />} />

            {/** Authenticated Pages */}
            {loggedIn ? <>
              <Route path="/login" element={<Navigate to="/home" />} />
              <Route path="/jobs" element={<JobList />} />
              <Route path="/jobs/:id/view" element={<JobView />} />
              <Route
                exact
                path="/reference/locations"
                element={<LocationList setFilterModel={setFilterModel} filterModel={filterModel} />}
              />
              <Route
                exact
                path="/reference/divers"
                element={<DiverList setFilterModel={setFilterModel} filterModel={filterModel} />}
              />
              <Route path="/upload" element={<JobUpload />} />
              <Route path="/data/surveys" element={<SurveyList />} />
              <Route path="/data/extract" element={<ExtractTemplateData />} />
              <Route path="/validation/:jobId" element={<ValidationPage />} />
              <Route path="/reference/observableItem" element={<ObservableItemAdd />} />
              <Route path="/reference/observableItem/:id/edit" element={<ObservableItemEdit />} />
              <Route path="/reference/site/:id/edit" element={<SiteEdit />} />
              <Route path="/reference/site/:id/clone" element={<SiteEdit clone />} />
              <Route path="/reference/site" element={<SiteEdit />} />
              <Route path="/reference/location" element={<LocationAdd />} />
              <Route path="/reference/location/:id" element={<LocationView />} />
              <Route path="/reference/location/:id/edit" element={<LocationAdd />} />
              <Route path="/reference/location/:id/:verb" element={<LocationView />} />
              {referenceData.map((e) => (
                <Route key={e.route.base} path={e.route.base} element={<EntityEdit entity={e} template={e.template.add} />} />
              ))}
              {referenceData.map((e) => (
                <Route key={e.route.edit} path={e.route.edit} element={<EntityEdit entity={e} template={e.template.edit} />} />
              ))}
              {referenceData
                .filter((e) => e.can.clone)
                .map((e) => (
                  <Route
                    exact
                    key={`${e.route.base}/:id/clone`}
                    path={`${e.route.base}/:id/clone`}
                    element={<EntityEdit entity={e} template={e.template.add} clone />}
                  />
                ))}
              {referenceData.map((e) => (
                <Route key={e.route.view} path={e.route.view} element={<EntityView entity={e} template={e.template.view} />} />
              ))}
              {referenceData.map((e) => (
                <Route key={e.list.route} path={e.list.route} element={<EntityList entity={e} />} />
              ))}
              <Route path="*" element={<Navigate to="/home" />} />
            </> : <Route path="*" element={<Login/>} />}
          </Routes>
        </AppContent>
      </Router>
    </ThemeProvider>
  );
};

export default App;
