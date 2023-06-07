import {Box, Button, LinearProgress, Tab, Tabs, Typography} from '@mui/material';
import React, {useEffect, useReducer, useState} from 'react';
import {searchSpeciesSummary} from '../../../api/api';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import SpeciesCorrectResults from './SpeciesCorrectResults';
import SpeciesCorrectEdit from './SpeciesCorrectEdit';
import JobView from '../../job/JobView';
import SpeciesCorrectErrorResults from './SpeciesCorrectErrorResults';
import {AuthContext} from '../../../contexts/auth-context';
import {AppConstants} from '../../../common/constants';
import Alert from '@mui/material/Alert';

const removeNullProperties = (obj) => {
  return obj ? Object.fromEntries(Object.entries(obj).filter((v) => v[1] && v[1] !== '')) : null;
};

const SpeciesCorrect = () => {
  const [locationData, setLocationData] = useState([]);
  const [searchResults, setSearchResults] = useState();
  const [correctionErrors, setCorrectionErrors] = useState();
  const [selected, setSelected] = useState();
  const [tabIndex, setTabIndex] = useState(0);
  const [jobId, setJobId] = useState();

  useEffect(() => {
    document.title = 'Species Correction';
  }, []);

  const [request, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, results: null, request: {type: 'search', payload: action.payload}};
      case 'showResults': {
        return {loading: false, search: state.request, request: null};
      }
      case 'showError': {
        return {loading: false, search: null, request: {type: 'error', payload: action.payload}};
      }
      default:
        return state;
    }
  }, {});

  useEffect(() => {
    if (request.request)
      switch (request.request.type) {
        case 'search': {
          let payload = removeNullProperties(request.request.payload);
          searchSpeciesSummary(payload).then((res) => {
            setSearchResults(res.data);
            dispatch({type: 'showResults'});
          });
          break;
        }
        case 'error': {
          setCorrectionErrors({errors: request.request.payload});
          break;
        }
      }
  }, [request.request, request.loading, locationData]);

  useEffect(() => {
    if (tabIndex === 0) {
      setSelected(null);
      setSearchResults(null);
      setCorrectionErrors(null);
      setJobId(null);
    }
  }, [tabIndex]);

  useEffect(() => {
    setTabIndex(selected ? 1 : 0);
  }, [selected]);

  useEffect(() => {
    setTabIndex(jobId ? 2 : 0);
  }, [jobId]);

  const submitHandler = (val) => {
    setJobId(+val.jobId);
  };

  const errorHandler = (error) => {
    setTabIndex(2);
    dispatch({type: 'showError', payload: error?.response?.data});
  };

  const tabContent = () =>
    <>
      <Tabs value={tabIndex} onChange={(e, v) => setTabIndex(v)}>
        <Tab style={{minWidth: '33%'}} label="Search" />
        <Tab style={{minWidth: '33%'}} label="Correcting" disabled={!selected || jobId} />
        <Tab style={{minWidth: '33%'}} label="Corrected" disabled={!jobId || !correctionErrors} />
      </Tabs>
      {tabIndex === 2 && jobId && (
        <Box border={1} borderRadius={1} m={1} borderColor="divider">
          <JobView jobId={jobId} />
          <Box width={200} m={2}>
            <Button variant="contained" onClick={() => setTabIndex(0)}>Return to Search</Button>
          </Box>
        </Box>
      )}
      {tabIndex === 2 && correctionErrors && (
        <Box sx={{m: 1, border: '1px red solid', borderRadius: '1px' }}>
          <SpeciesCorrectErrorResults correctionErrors={correctionErrors.errors} />
          <Box width={200} m={2}>
            <Button variant="contained" onClick={() => setTabIndex(0)} data-testid='return-to-search-after-error'>Return to Search</Button>
          </Box>
        </Box>
      )}
      {tabIndex === 1 && <SpeciesCorrectEdit selected={selected} onSubmit={submitHandler} onError={errorHandler}/>}
      {
        <Box display={tabIndex == 0 ? 'initial' : 'none'}>
          <Box border={1} borderRadius={1} m={1} borderColor="divider" style={{overflow: 'hidden'}}>
            <SpeciesCorrectFilter
              onLoadLocations={(locations) => setLocationData(locations)}
              onSearch={(filter) => {
                setSearchResults([]);
                setSelected(null);
                const payload = {...filter, locationIds: filter.locationId ? [filter.locationId] : filter.locationIds};
                delete payload.locationId;
                dispatch({type: 'getRequest', payload});
              }}
            />
          </Box>
          {request.loading && <LinearProgress />}
          {!request.loading && searchResults && (
            <Box m={1}>
              <SpeciesCorrectResults
                results={searchResults}
                onClick={(id) => setSelected({filter: {...request.search.payload, observableItemId: id.observableItemId}, result: id})}
              />
            </Box>
          )}
        </Box>
      }
    </>;

  return (
    <>
      <Box p={1}>
        <Typography variant="h6">Species Correction</Typography>
      </Box>
      <AuthContext.Consumer>
        {({auth}) => {
          if(auth.roles.includes(AppConstants.ROLES.DATA_OFFICER)) {
            return tabContent();
          }
          else {
            return(
              <Alert severity="error" variant="outlined">
                <p>Permission Denied</p>
              </Alert>
            );
          }
        }}
      </AuthContext.Consumer>
    </>
  );
};

export default SpeciesCorrect;
