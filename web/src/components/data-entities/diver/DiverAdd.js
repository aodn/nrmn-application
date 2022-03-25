import {Grid} from '@mui/material';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import Save from '@mui/icons-material/Save';
import React, {useReducer, useState} from 'react';
import {Navigate, NavLink, useParams} from 'react-router-dom';
import {entitySave} from '../../../api/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';

const DiverAdd = () => {
  const diverId = useParams()?.id;
  const [savedId, setSavedId] = useState(false);
  const [errors, setErrors] = useState([]);

  const formReducer = (state, action) => {
    if (action.form) return {...state, ...action.form};
    switch (action.field) {
      default:
        return {...state, [action.field]: action.value};
    }
  };

  const [diver, dispatch] = useReducer(formReducer, {
    initials: '',
    fullName: ''
  });

  const handleSubmit = () => {
    entitySave(`diver`, diver).then((res) => {
      if (res.data.diverId) {
        setSavedId(res.data.diverId);
      } else {
        setErrors(res.data);
      }
    });
  };

  if (savedId) return <Navigate to={`/reference/divers`} state={{message: 'Diver Saved'}} />;

  return (
    <EntityContainer name="diver" goBackTo="/reference/divers">
      <Grid container alignItems="flex-start" direction="row" spacing={2}>
        <Grid item xs={12}>
          <Typography variant="h4">{diverId ? 'Edit' : 'New'} diver</Typography>
        </Grid>
        <Grid item xs={3}>
          <CustomTextInput
            label="Initials"
            formData={diver.initials}
            field="initials"
            errors={errors}
            onChange={(t) => dispatch({field: 'initials', value: t})}
          />
        </Grid>
        <Grid item xs={6}>
          <CustomTextInput
            label="Full Name"
            formData={diver.fullName}
            field="fullName"
            errors={errors}
            onChange={(t) => dispatch({field: 'fullName', value: t})}
          />
        </Grid>
        <Grid item xs={12}>
          <Button variant="outlined" component={NavLink} to="/reference/divers">
            Cancel
          </Button>
          <Button variant="contained" style={{width: '50%', marginLeft: '20px'}} type="submit" startIcon={<Save />} onClick={handleSubmit}>
            Save diver
          </Button>
        </Grid>
      </Grid>
    </EntityContainer>
  );
};

export default DiverAdd;
