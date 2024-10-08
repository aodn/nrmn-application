import { responsiveFontSizes, ThemeProvider, createTheme } from '@mui/material/styles';
import { LicenseManager } from 'ag-grid-enterprise';
import React, { useState, useMemo, ReactNode } from 'react';
import { BrowserRouter as Router, Navigate, Route, Routes } from 'react-router-dom';
import { AuthContext } from './contexts/auth-context';
import LoginForm from './components/auth/LoginForm';
import AppContent from './components/containers/AppContent';
import DiverAdd from './components/data-entities/diver/DiverAdd';
import DiverList from './components/data-entities/diver/DiverList';
import LocationAdd from './components/data-entities/location/LocationAdd';
import LocationList from './components/data-entities/location/LocationList';
import LocationView from './components/data-entities/location/LocationView';
import ObservableItemAdd from './components/data-entities/observable-item/ObservableItemAdd';
import ObservableItemEdit from './components/data-entities/observable-item/ObservableItemEdit';
import ObservableItemList from './components/data-entities/observable-item/ObservableItemList';
import ObservableItemView from './components/data-entities/observable-item/ObservableItemView';
import SharedLinkList from './components/data-entities/shared-link/SharedLinkList';
import SiteEdit from './components/data-entities/site/SiteEdit';
import SiteList from './components/data-entities/site/SiteList';
import SiteView from './components/data-entities/site/SiteView';
import SurveyEdit from './components/data-entities/survey/SurveyEdit';
import SurveyList from './components/data-entities/survey/SurveyList';
import SurveyView from './components/data-entities/survey/SurveyView';
import SurveyCorrect from './components/data-entities/survey/SurveyCorrect';
import ExtractTemplateData from './components/datasheets/ExtractTemplateData';
import JobUpload from './components/import/JobUpload';
import ValidationPage from './components/import/ValidationJob';
import JobList from './components/job/JobList';
import JobView from './components/job/JobView';
import Homepage from './components/layout/Homepage';
import SideMenu from './components/layout/SideMenu';
import TopBar from './components/layout/TopBar';
import EntityContainer from './components/containers/EntityContainer';
import '@ag-grid-community/styles/ag-grid.css';
import '@ag-grid-community/styles/ag-theme-material.css';
import ApplicationError from './components/ui/ApplicationError';
import SpeciesCorrect from './components/data-entities/survey/SpeciesCorrect';
import PropTypes from 'prop-types';
import ChangePasswordForm from './components/auth/ChangePasswordForm';
import UserList from './components/admin/UserList';

interface ErrorBoundaryProps {
  children: ReactNode;
}

interface ErrorBoundaryState {
  hasError: boolean;
  message?: string;
  error?: Error | null;
}

class ErrorBoundary extends React.Component<ErrorBoundaryProps, ErrorBoundaryState> {
  static propTypes = {
    children: PropTypes.node
  };

  constructor(props: ErrorBoundaryProps) {
    super(props);
    this.state = { hasError: false };
  }

  static getDerivedStateFromError(error: ErrorBoundaryState) {
    return { hasError: true, message: error.message };
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="error">
          <b>Page Error</b>
          <div>{this.state.message}</div>
          <button onClick={() => window.location.reload()}>Refresh Page</button>
        </div>
      );
    }
    return this.props.children;
  }
}

const App: React.FC = () => {
  const [menuOpen, setMenuOpen] = useState(false);
  const [auth, setAuth] = useState(JSON.parse(localStorage.getItem('auth') + '') || { expires: 0, username: null, features: [] });

  const [applicationError, setApplicationError] = useState();

  (window as Window & typeof globalThis & { setApplicationError: React.Dispatch<React.SetStateAction<undefined>> }).setApplicationError = setApplicationError;

  const loggedIn = Date.now() < auth.expires;

  const licenceKey = JSON.parse(localStorage.getItem('gridLicense') + '');
  if (licenceKey) LicenseManager.setLicenseKey(licenceKey);

  const productionTheme = useMemo(
    () =>
      responsiveFontSizes(
        createTheme({
          typography: {
            body2: {
              fontSize: 12
            },
            // table: {
            //   fontSize: 12,
            //   padding: 6
            // }
          },
          palette: {
            mode: 'light',
            primary: {
              main: '#546E7B',
              light: '#AADFFA',
              dark: '#546E7B',
              //rowHeader: '#E4EAED',
              //rowHighlight: '#F2F6F7'
            },
            secondary: {
              main: '#563FF2',
              light: '#7D69FF',
              dark: '#5844DB',
              //rowHeader: '#E4EAED',
              //rowHighlight: '#F2F6F7'
            }
          }
        })
      ),
    []
  );

  const verificationTheme = useMemo(
    () =>
      responsiveFontSizes(
        createTheme({
          typography: {
            // table: {
            //   fontSize: 12
            // }
          },
          palette: {
            mode: 'light',
            primary: {
              main: '#7B6154',
              light: '#AADFFA',
              dark: '#546E7B',
              //rowHeader: '#E4EAED',
              //rowHighlight: '#F2F6F7'
            },
            secondary: {
              main: '#563FF2',
              light: '#7D69FF',
              dark: '#5844DB',
              //rowHeader: '#E4EAED',
              //rowHighlight: '#F2F6F7'
            }
          }
        })
      ),
    []
  );

  return (
    <AuthContext.Provider value={{ auth: auth, setAuth: setAuth }}>
      <ThemeProvider theme={auth?.features?.includes('verification') ? verificationTheme : productionTheme}>
        <Router>
          <SideMenu open={menuOpen} onClose={() => setMenuOpen(false)}></SideMenu>
          <TopBar onMenuClick={() => setMenuOpen(true)}>
            {auth?.features?.includes('verification') ? 'NRMN SysTest' : 'National Reef Monitoring Network'}
          </TopBar>
          <AppContent>
            {applicationError ? (
              <ApplicationError error={applicationError} />
            ) : (
              <ErrorBoundary>
                <Routes>
                  <Route path="/home" element={<Homepage />} />

                  <Route path="/" element={<Navigate to="/home" />} />

                  {/** Authenticated Pages */}
                  {loggedIn && (
                    <>
                      <Route path="/login" element={<Navigate to="/home" />} />

                      <Route path="/admin/users" element={<UserList />} />

                      <Route path="/data/surveys" element={<SurveyList />} />
                      <Route path="/data/survey/:id" element={<SurveyView />} />
                      <Route path="/data/survey/:id/edit" element={<SurveyEdit />} />
                      {auth?.features?.includes('corrections') && <Route path="/data/survey/:id/correct" element={<SurveyCorrect />} />}
                      {auth?.features?.includes('corrections') && <Route path="/data/species" element={<SpeciesCorrect />} />}

                      <Route path="/data/jobs" element={<JobList />} />
                      <Route
                        path="/data/job/:id/view"
                        element={
                          <EntityContainer name="Jobs" goBackTo="/data/jobs">
                            <JobView />
                          </EntityContainer>
                        }
                      />
                      <Route path="/data/job/:id/edit" element={<ValidationPage />} />

                      <Route path="/data/upload" element={<JobUpload />} />
                      <Route
                        path="/data/share"
                        element={
                          <ErrorBoundary>
                            <SharedLinkList />
                          </ErrorBoundary>
                        }
                      />
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
                  <Route path="/changePassword" element={<ChangePasswordForm />} />
                  <Route path="*" element={<LoginForm />} />
                </Routes>
              </ErrorBoundary>
            )}
          </AppContent>
        </Router>
      </ThemeProvider>
    </AuthContext.Provider>
  );
};

export default App;
