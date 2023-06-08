import React, {useEffect, useReducer, useState} from 'react';
import {useParams, NavLink, Navigate} from 'react-router-dom';
import {Alert, Box, Button, CircularProgress, Grid, Typography} from '@mui/material';
import {Save} from '@mui/icons-material';
import PropTypes from 'prop-types';
import {AuthContext} from '../../../contexts/auth-context';
import {AppConstants} from '../../../common/constants';
import EntityContainer from '../../containers/EntityContainer';

import CustomArrayInput from '../../input/CustomArrayInput';
import CustomTextInput from '../../input/CustomTextInput';
import CustomDropDownInput from '../../input/CustomDropDownInput';
import CustomAutoCompleteInput from '../../input/CustomAutoCompleteInput';

import {getResult, entityEdit, entitySave} from '../../../api/api';

const numericOptions = [
  {id: 1, label: '1'},
  {id: 2, label: '2'},
  {id: 3, label: '3'},
  {id: 4, label: '4'}
];

const SiteEdit = ({clone}) => {
  const siteId = useParams()?.id;

  const [saved, setSaved] = useState(false);
  const [errors, setErrors] = useState([]);
  const [options, setOptions] = useState({});

  const [checkCoords, setCheckCoords] = useState();
  const [coordWarning, setCoordWarning] = useState();

  const edit = !clone && typeof siteId !== 'undefined';

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [site, dispatch] = useReducer(formReducer, {});

  useEffect(() => {
    document.title = edit ? 'Edit Site' : 'New Site';
    async function fetchSiteOptions() {
      await getResult('siteOptions').then((res) => setOptions(res.data));
    }
    fetchSiteOptions();
  }, [edit]);

  useEffect(() => {
    async function fetchSite() {
      getResult(`site/${siteId}`).then((res) => {
        if (clone) {
          delete res.data.siteAttribute;
          delete res.data.siteId;
        }
        dispatch({form: res.data});
      });
    }
    if (siteId) fetchSite();
  }, [siteId, clone, edit]);

  const latLongBlur = () => {
    if (site.latitude && site.longitude) {
      setCheckCoords({latitude: site.latitude, longitude: site.longitude});
    }
  };

  useEffect(latLongBlur, [site.latitude, site.longitude]);

  useEffect(() => {
    async function fetchSiteNear() {
      const query = `sitesAroundLocation?latitude=${site.latitude}&longitude=${site.longitude}` + (edit ? `&exclude=${siteId}` : '');
      await getResult(query).then((res) => setCoordWarning(res?.data?.join(', ')));
    }
    if (checkCoords && !isNaN(parseFloat(site.latitude)) && !isNaN(parseFloat(site.longitude))) {
      fetchSiteNear();
    }
  }, [checkCoords, site.latitude, site.longitude, siteId, edit]);

  const handleSubmit = () => {
    if (edit) {
      entityEdit(`site/${siteId}`, site).then((res) => {
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data);
        }
      });
    } else {
      entitySave(`site`, site).then((res) => {
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data);
        }
      });
    }
  };

  const title = (edit === true ? 'Edit ' : clone === true ? 'Clone ' : 'New ') + 'Site';

  if (saved) {
    const id = saved['siteId'];
    return <Navigate to={`/reference/site/${id}`} state={{message: edit ? 'Site Updated' : 'Site Saved'}} />;
  }

  return (
    <AuthContext.Consumer>
      {({ auth }) => {
        if (auth.roles.includes(AppConstants.ROLES.DATA_OFFICER)) {
          return (
            <EntityContainer name="Sites" goBackTo="/reference/sites">
              <Grid container alignItems="flex-start" direction="row">
                <Grid item xs={10}>
                  <Box fontWeight="fontWeightBold">
                    <Typography variant="h4">{title}</Typography>
                  </Box>
                </Grid>
              </Grid>
              <Grid container direction="column" justifyContent="flex-start" alignItems="center">
                {siteId && Object.keys(site).length === 0 ? (
                  <CircularProgress size={20} />
                ) : (
                  <Box pt={2} pb={6} padding={2} width="90%">
                    {errors.length > 0 ? (
                      <Box py={2}>
                        <Alert severity="error" variant="filled">
                          Please review this form for errors and try again.
                        </Alert>
                      </Box>
                    ) : null}
                    <Grid container spacing={2}>
                      <Grid item xs={6}>
                        <CustomTextInput
                          label="Site Code"
                          formData={site.siteCode}
                          field="siteCode"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'siteCode', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomTextInput
                          label="Site Name"
                          formData={site.siteName}
                          field="siteName"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'siteName', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomDropDownInput
                          label="Location"
                          field="locationId"
                          errors={errors}
                          options={options.locations?.map((l) => {
                            return {id: l.locationId, label: l.locationName};
                          })}
                          formData={site.locationId}
                          onChange={(t) => dispatch({field: 'locationId', value: t})}
                        />
                      </Grid>
                    </Grid>
                    <Grid container spacing={2}>
                      <Grid item xs={6}>
                        <CustomAutoCompleteInput
                          label="State"
                          options={options.siteStates}
                          formData={site.state}
                          field="state"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'state', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomAutoCompleteInput
                          label="Country"
                          options={options.siteCountries}
                          formData={site.country}
                          field="country"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'country', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomTextInput
                          label="Latitude"
                          type="number"
                          formData={site.latitude}
                          onBlur={latLongBlur}
                          field="latitude"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'latitude', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomTextInput
                          label="Longitude"
                          type="number"
                          formData={site.longitude}
                          onBlur={latLongBlur}
                          field="longitude"
                          errors={errors}
                          onChange={(t) => dispatch({field: 'longitude', value: t})}
                        />
                      </Grid>
                      {coordWarning && (
                        <Grid item xs={12}>
                          <Alert severity="warning">Warning: Within 200M of {coordWarning}.</Alert>
                        </Grid>
                      )}
                      <Grid item xs={6}>
                        <CustomAutoCompleteInput
                          label="Marine Protected Area"
                          options={options.marineProtectedAreas}
                          formData={site.mpa}
                          onChange={(t) => dispatch({field: 'mpa', value: t})}
                        />
                      </Grid>
                      <Grid item xs={6}>
                        <CustomAutoCompleteInput
                          label="Protection Status"
                          options={options.protectionStatuses}
                          formData={site.protectionStatus}
                          onChange={(t) => dispatch({field: 'protectionStatus', value: t})}
                        />
                      </Grid>
                      {siteId && (
                        <>
                          <Grid item xs={6}>
                            <CustomDropDownInput
                              label="Relief"
                              optional
                              options={numericOptions}
                              formData={site.relief}
                              onChange={(t) => dispatch({field: 'relief', value: t})}
                            />
                          </Grid>
                          <Grid item xs={6}>
                            <CustomDropDownInput
                              label="Slope"
                              optional
                              options={numericOptions}
                              formData={site.slope}
                              onChange={(t) => dispatch({field: 'slope', value: t})}
                            />
                          </Grid>
                          <Grid item xs={6}>
                            <CustomDropDownInput
                              label="Wave Exposure"
                              optional
                              options={numericOptions}
                              formData={site.waveExposure}
                              onChange={(t) => dispatch({field: 'waveExposure', value: t})}
                            />
                          </Grid>
                          <Grid item xs={6}>
                            <CustomDropDownInput
                              label="Currents"
                              optional
                              options={numericOptions}
                              formData={site.currents}
                              onChange={(t) => dispatch({field: 'currents', value: t})}
                            />
                          </Grid>
                        </>
                      )}
                      <Grid item xs={6}>
                        <CustomArrayInput
                          label="Old Site Codes"
                          values={site.oldSiteCodes}
                          onChange={(t) => dispatch({field: 'oldSiteCodes', value: t})}
                        />
                      </Grid>
                    </Grid>
                    <Box display="flex" justifyContent="center" mt={5}>
                      <Button variant="outlined" component={NavLink} to="/reference/sites">
                        Cancel
                      </Button>
                      <Button
                        variant="contained"
                        style={{width: '50%', marginLeft: '5%', marginRight: '20%'}}
                        onClick={handleSubmit}
                        startIcon={<Save></Save>}
                      >
                        Save Site
                      </Button>
                    </Box>
                  </Box>
                )}
              </Grid>
            </EntityContainer>);
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
  );
};

SiteEdit.propTypes = {
  clone: PropTypes.bool
};

export default SiteEdit;
