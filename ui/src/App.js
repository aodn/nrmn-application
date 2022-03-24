import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import {responsiveFontSizes, ThemeProvider, createTheme} from '@mui/material/styles';
import Alert from '@mui/material/Alert';
import {LicenseManager} from 'ag-grid-enterprise';
import React, {useState, useMemo} from 'react';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';
import {AuthContext} from './auth-context';
import LoginForm from './components/auth/LoginForm';
import AppContent from './components/containers/AppContent';
import DiverAdd from './components/data-entities/diver/DiverAdd';
import DiverList from './components/data-entities/diver/DiverList';
import LocationAdd from './components/data-entities/location/LocationAdd';
import LocationList from './components/data-entities/location/LocationList';
import LocationView from './components/data-entities/location/LocationView';
import ObservableItemAdd from './components/data-entities/ObservableItemAdd';
import ObservableItemEdit from './components/data-entities/ObservableItemEdit';
import ObservableItemList from './components/data-entities/ObservableItemList';
import ObservableItemView from './components/data-entities/ObservableItemView';
import SiteEdit from './components/data-entities/SiteEdit';
import SiteList from './components/data-entities/SiteList';
import SiteView from './components/data-entities/SiteView';
import SurveyEdit from './components/data-entities/survey/SurveyEdit';
import SurveyList from './components/data-entities/survey/SurveyList';
import SurveyView from './components/data-entities/survey/SurveyView';
import ExtractTemplateData from './components/datasheets/ExtractTemplateData';
import JobUpload from './components/import/JobUpload';
import ValidationPage from './components/import/ValidationJob';
import JobList from './components/job/JobList';
import JobView from './components/job/JobView';
import Homepage from './components/layout/Homepage';
import SideMenu from './components/layout/SideMenu';
import TopBar from './components/layout/TopBar';

const App = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [auth, setAuth] = useState(JSON.parse(localStorage.getItem('auth')) || {expires: 0, username: null});

  const [applicationError, setApplicationError] = useState(null);
  window.setApplicationError = setApplicationError;

  const loggedIn = Date.now() < auth.expires;

  LicenseManager.setLicenseKey(JSON.parse(localStorage.getItem('gridLicense')));

  const theme = useMemo(
    () =>
      responsiveFontSizes(
        createTheme({
          palette: {
            mode: 'light',
            primary: {main: '#546E7B', light: '#AADFFA', dark: '546E7B'},
            secondary: {main: '#563FF2', light: '#7D69FF', dark: '5844DB'}
          }
        })
      ),
    []
  );

  return (
    <ThemeProvider theme={theme}>
      <AuthContext.Provider value={{auth: auth, setAuth: setAuth}}>
        <Router>
          <SideMenu open={menuOpen} onClose={() => setMenuOpen(false)}></SideMenu>
          <TopBar onMenuClick={() => setMenuOpen(true)}></TopBar>
          <AppContent>
            {applicationError ? (
              <Box m={10}>
                <Box py={3}>
                  <Alert severity="error" variant="filled">
                    {applicationError}
                    <br />
                    The server may be experiencing problems. Please wait a moment and try again.
                    <br />
                    If this problem persists, please contact info@aodn.org.au.
                  </Alert>
                </Box>
                <Button onClick={() => window.location.reload()}>Refresh Page</Button>
              </Box>
            ) : (
              <Routes>
                <Route path="/home" element={<Homepage />} />

                <Route path="/" element={<Navigate to="/home" />} />

                {/** Authenticated Pages */}
                {loggedIn && (
                  <>
                    <Route path="/login" element={<Navigate to="/home" />} />

                    <Route path="/data/surveys" element={<SurveyList />} />
                    <Route path="/data/survey/:id" element={<SurveyView />} />
                    <Route path="/data/survey/:id/edit" element={<SurveyEdit />} />

                    <Route path="/jobs" element={<JobList />} />
                    <Route path="/jobs/:id/view" element={<JobView />} />
                    <Route path="/validation/:jobId" element={<ValidationPage />} />

                    <Route path="/upload" element={<JobUpload />} />

                    <Route path="/data/extract" element={<ExtractTemplateData />} />

                    <Route path="/reference/locations" element={<LocationList />} />
                    <Route path="/reference/location" element={<LocationAdd />} />
                    <Route path="/reference/location/:id" element={<LocationView />} />
                    <Route path="/reference/location/:id/edit" element={<LocationAdd />} />
                    <Route path="/reference/location/:id/:verb" element={<LocationView />} />

                    <Route path="/reference/divers" element={<DiverList />} />
                    <Route path="/reference/diver" element={<DiverAdd />} />

                    <Route path="/reference/sites" element={<SiteList />} />
                    <Route path="/reference/site" element={<SiteEdit />} />
                    <Route path="/reference/site/:id" element={<SiteView />} />
                    <Route path="/reference/site/:id/edit" element={<SiteEdit />} />
                    <Route path="/reference/site/:id/clone" element={<SiteEdit clone />} />

                    <Route path="/reference/observableItems" element={<ObservableItemList />} />
                    <Route path="/reference/observableItem" element={<ObservableItemAdd />} />
                    <Route path="/reference/observableItem/:id" element={<ObservableItemView />} />
                    <Route path="/reference/observableItem/:id/edit" element={<ObservableItemEdit />} />

                    <Route path="*" element={<Navigate to="/home" />} />
                  </>
                )}
                <Route path="*" element={<LoginForm />} />
              </Routes>
            )}
          </AppContent>
        </Router>
      </AuthContext.Provider>
    </ThemeProvider>
  );
};

export default App;
