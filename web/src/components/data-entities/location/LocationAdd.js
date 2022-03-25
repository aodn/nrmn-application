import {Grid} from '@mui/material';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Save from '@mui/icons-material/Save';
import React, {useEffect, useReducer, useState} from 'react';
import {useParams} from 'react-router-dom';
import {NavLink} from 'react-router-dom';
import {Navigate} from 'react-router-dom';
import {entitySave, entityEdit, getEntity} from '../../../api/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomCheckboxInput from '../../input/CustomCheckboxInput';
import CustomTextInput from '../../input/CustomTextInput';

const LocationAdd = () => {
  const locationId = useParams()?.id;
  const [savedId, setSavedId] = useState(false);
  const [errors, setErrors] = useState([]);

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [location, dispatch] = useReducer(formReducer, {
    locationName: '',
    isActive: true
  });

  const handleSubmit = () => {
    const action = locationId ? entityEdit(`location/${locationId}`, location) : entitySave(`location`, location);
    action.then((res) => {
      if (res.data.locationId) {
        setSavedId(res.data.locationId);
      } else {
        setErrors(res.data);
      }
    });
  };

  useEffect(() => {
    if (locationId)
      getEntity(`location/${locationId}`).then((res) => {
        dispatch({form: res.data});
      });
  }, [locationId]);

  if (savedId) return <Navigate to={`/reference/location/${savedId}`} state={{message: 'Location Saved'}} />;

  return (
    <EntityContainer name="location" goBackTo="/reference/locations">
      <Grid container alignItems="flex-start" direction="row" spacing={2}>
        <Grid item xs={12}>
          <Typography variant="h4">{locationId ? 'Edit' : 'New'} Location</Typography>
        </Grid>
        <Grid item xs={12}>
          <CustomTextInput
            label="Location Name"
            formData={location.locationName}
            field="locationName"
            errors={errors}
            onChange={(t) => dispatch({field: 'locationName', value: t})}
          />
        </Grid>
        <Grid item xs={12}>
          <CustomCheckboxInput
            label="Is Active"
            formData={location.isActive}
            onChange={(t) => dispatch({field: 'isActive', value: t})}
            field="isActive"
          />
        </Grid>
        <Grid item xs={12}>
          <Button variant="outlined" component={NavLink} to="/reference/locations">
            Cancel
          </Button>
          <Button variant="contained" style={{width: '50%', marginLeft: '20px'}} type="submit" startIcon={<Save />} onClick={handleSubmit}>
            Save Location
          </Button>
        </Grid>
      </Grid>
    </EntityContainer>
  );
};

export default LocationAdd;
