import React, {useEffect, useReducer, useState} from 'react';
import {useParams, NavLink, Redirect} from 'react-router-dom';
import {Box, Button, CircularProgress, Grid, Typography} from '@material-ui/core';
import {Save} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import PropTypes from 'prop-types';

import EntityContainer from '../containers/EntityContainer';

import CustomTextInput from '../input/CustomTextInput';
import CustomDropDownInput from '../input/CustomDropDownInput';
import CustomAutoCompleteInput from '../input/CustomAutoCompleteInput';

import {getResult, getSiteEdit, entityEdit, entitySave} from '../../axios/api';

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

  useEffect(() => getSiteEdit().then((options) => setOptions(options)), []);

  useEffect(() => {
    if (siteId) getResult(`sites/${siteId}`).then((res) => dispatch({form: res.data}));
  }, [siteId]);

  const latLongBlur = () => {
    if (site.latitude && site.longitude) {
      setCheckCoords({latitude: site.latitude, longitude: site.longitude});
    }
  };

  useEffect(() => {
    if (checkCoords) {
      const query = `sitesAroundLocation?latitude=${site.latitude}&longitude=${site.longitude}` + (siteId ? `&exclude=${siteId}` : '');
      getResult(query).then((res) => setCoordWarning(res?.data?.join(', ')));
    }
  }, [checkCoords, site.latitude, site.longitude, siteId]);

  const handleSubmit = (e) => {
    if (edit) {
      entityEdit(`sites/${siteId}`, site).then((res) => {
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data.errors);
        }
      });
    } else {
      delete e.formData.siteAttribute;
      entitySave(`sites`, e.formData).then((res) => {
        dispatch({form: e.formData});
        if (res.data.siteId) {
          setSaved(res.data);
        } else {
          setErrors(res.data.errors);
        }
      });
    }
  };

  const title = (edit === true ? 'Edit ' : clone === true ? 'Clone ' : 'New ') + 'Site';

  if (saved) {
    const id = saved['siteId'];
    return <Redirect to={`/reference/site/${id}/${edit ? 'saved' : 'new'}`} />;
  }

  return (
    <EntityContainer name="Sites" goBackTo="/reference/sites">
      <Grid container alignItems="flex-start" direction="row">
        <Grid item xs={10}>
          <Box fontWeight="fontWeightBold">
            <Typography variant="h4">{title}</Typography>
          </Box>
        </Grid>
      </Grid>
      <Grid container direction="column" justify="flex-start" alignItems="center">
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
                <CustomTextInput label="Site Code" formData={site.siteCode} onChange={(t) => dispatch({field: 'siteCode', value: t})} />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput label="Site Name" formData={site.siteName} onChange={(t) => dispatch({field: 'siteName', value: t})} />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Locations"
                  options={options.locations}
                  formData={site.locationId}
                  onChange={(t) => dispatch({field: 'locationId', value: t})}
                />
              </Grid>
            </Grid>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <CustomTextInput label="State" formData={site.state} onChange={(t) => dispatch({field: 'state', value: t})} />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput label="Country" formData={site.country} onChange={(t) => dispatch({field: 'country', value: t})} />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Latitude"
                  type="number"
                  formData={site.latitude}
                  onBlur={latLongBlur}
                  onChange={(t) => dispatch({field: 'latitude', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Longitude"
                  type="number"
                  formData={site.longitude}
                  onBlur={latLongBlur}
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
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Relief"
                  options={numericOptions}
                  formData={site.relief}
                  onChange={(t) => dispatch({field: 'relief', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Slope"
                  options={numericOptions}
                  formData={site.slope}
                  onChange={(t) => dispatch({field: 'slope', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Wave Exposure"
                  options={numericOptions}
                  formData={site.waveExposure}
                  onChange={(t) => dispatch({field: 'waveExposure', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomDropDownInput
                  label="Currents"
                  options={numericOptions}
                  formData={site.currents}
                  onChange={(t) => dispatch({field: 'currents', value: t})}
                />
              </Grid>
              <Grid item xs={6}>
                <CustomTextInput
                  label="Old Site Codes"
                  formData={site.oldSiteCodes}
                  onChange={(t) => dispatch({field: 'oldSiteCodes', value: t})}
                />
              </Grid>
            </Grid>
            <Box display="flex" justifyContent="center" mt={5}>
              <Button component={NavLink} to="/reference/sites">
                Cancel
              </Button>
              <Button style={{width: '50%', marginLeft: '5%', marginRight: '20%'}} onClick={handleSubmit} startIcon={<Save></Save>}>
                Save Site
              </Button>
            </Box>
          </Box>
        )}
      </Grid>
    </EntityContainer>
  );
};

SiteEdit.propTypes = {
  clone: PropTypes.bool
};

export default SiteEdit;
