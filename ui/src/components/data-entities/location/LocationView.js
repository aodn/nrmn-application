import {Button, Grid} from '@material-ui/core';
import Box from '@material-ui/core/Box';
import Typography from '@material-ui/core/Typography';
import {Edit} from '@material-ui/icons';
import Alert from '@material-ui/lab/Alert';
import React, {useEffect, useState} from 'react';
import {useParams} from 'react-router';
import {Link} from 'react-router-dom';
import {getResult} from '../../../axios/api';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';

const LocationView = () => {
  const locationId = useParams()?.id;
  const locationVerb = useParams()?.verb;

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
        {locationVerb && (
          <Grid item xs={12}>
            <Box>
              <Alert severity="info" variant="filled">
                Location {locationVerb === 'new' ? 'Created' : 'Updated'}
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
