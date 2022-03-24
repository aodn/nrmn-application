import {Button, Grid} from '@mui/material';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import {Edit} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router';
import {Link, useLocation} from 'react-router-dom';
import {getResult} from '../../../axios/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';

const LocationView = () => {
  const locationId = useParams()?.id;
  const {state} = useLocation();

  const [location, setLocation] = useState();

  useEffect(() => {
    if (locationId)
      getResult(`location/${locationId}`).then((res) => {
        setLocation(res.data);
      });
  }, [locationId]);

  return location ? (
    <EntityContainer name="location" goBackTo="/reference/locations">
      <Grid container alignItems="flex-start" direction="row" spacing={2}>
        {state?.message && (
          <Grid item xs={12}>
            <Box>
              <Alert severity="info" variant="filled">
                {state.message}
              </Alert>
            </Box>
          </Grid>
        )}
        <Grid item xs={10}>
          <Typography variant="h4">Location Details</Typography>
        </Grid>
        <Grid item xs={2}>
          <Button component={Link} to={`/reference/location/${locationId}/edit`} startIcon={<Edit>edit</Edit>}>
            Edit
          </Button>
        </Grid>
        <Grid item xs={12}>
          <Box padding={3}>
            <CustomTextInput label="Location Name" formData={location.locationName} readOnlyInput />
            <CustomTextInput label="Is Active" formData={location.isActive} readOnlyInput />
          </Box>
        </Grid>
      </Grid>
    </EntityContainer>
  ) : (
    <></>
  );
};

export default LocationView;
