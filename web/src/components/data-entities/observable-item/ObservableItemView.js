import React, {useEffect, useState} from 'react';
import {Alert, Box, Divider, Grid, Typography, Button} from '@mui/material';
import {getEntity} from '../../../api/api';
import {useParams} from 'react-router';
import {Edit} from '@mui/icons-material';
import {NavLink, useLocation} from 'react-router-dom';
import EntityContainer from '../../containers/EntityContainer';
import CustomTextInput from '../../input/CustomTextInput';
import {AuthContext} from '../../../contexts/auth-context';
import {AppConstants} from '../../../common/constants';

const ObservableItemView = () => {
  const id = useParams()?.id;
  const {state} = useLocation();
  const [data, setData] = useState({});

  useEffect(() => {
    document.title = 'View Observable Item';
    async function fetchObservableItem() {
      await getEntity(`reference/observableItem/${id}`)
        .then((res) => {
          setData(res.data);
        });
    }
    if (id >= 0 && !data.observableItemId) fetchObservableItem();
  }, [id, data]);

  return (
    <AuthContext.Consumer>
      {({ auth }) =>
        <EntityContainer name="Observable Items" goBackTo="/reference/observableItems">
          <Box m={2} display="flex" flexDirection="row" width="100%">
            <Box flexGrow={1}>
              <Typography variant="h4">Observable Items</Typography>
            </Box>
            <Box>
              {data?.observableItemId && (
                <Button
                  data-testid="edit-button"
                  variant="outlined"
                  component={NavLink}
                  disabled={!(auth.roles.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles.includes(AppConstants.ROLES.ADMIN))}
                  to={`/reference/observableItem/${id}/edit`}
                  startIcon={<Edit>edit</Edit>}>
                  {'Edit'}
                </Button>
              )}
            </Box>
          </Box>
          {state?.message && (
            <Box mx={5} flexGrow={1}>
              <Alert severity="info" variant="filled">
                {state.message}
              </Alert>
            </Box>
          )}
          {data?.observableItemId && (
            <>
              <Box p={2}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="ID" formData={data.observableItemId} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Species Name" formData={data.observableItemName} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Observable Item Type" formData={data.obsItemTypeName} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Common Name" formData={data.commonName} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Aphia ID" formData={data.aphiaId ?? '---'} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Aphia Relation" formData={data.aphiaRelTypeName ?? '---'} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Superseded By" formData={data.supersededBy ?? '---'} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Superseded Names" formData={data.supersededNames ?? '---'} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Superseded IDs" formData={data.supersededIds ?? '---'} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Letter Code" formData={data.letterCode ?? '---'} />
                  </Grid>
                </Grid>
              </Box>
              <Divider />
              <Box p={2}>
                <Grid container spacing={2}>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Phylum" formData={data.phylum} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Class" formData={data.class} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Order" formData={data.order} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Family" formData={data.family} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Genus" formData={data.genus} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Report Group" formData={data.reportGroup} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Habitat Group" formData={data.habitatGroups} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Species Epithet" formData={data.speciesEpithet} />
                  </Grid>

                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Length-Weight a" formData={data.lengthWeightA} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Length-Weight b" formData={data.lengthWeightB} />
                  </Grid>
                  <Grid item xs={6}>
                    <CustomTextInput readOnlyInput label="Length-Weight cf" formData={data.lengthWeightCf} />
                  </Grid>
                  <Grid item xs={12}>
                    {data.obsItemAttribute && Object.keys(data.obsItemAttribute).length > 0 ? (
                      <>
                        <Box ml={10} my={2}>
                          <Typography variant="subtitle2" component="i">Other Attributes</Typography>
                        </Box>
                        {Object.keys(data.obsItemAttribute).map((v) => (
                          <Grid item key={v} xs={7}>
                            <CustomTextInput readOnlyInput label={v} formData={data.obsItemAttribute[v]} />
                          </Grid>
                        ))}
                      </>
                    ) : (
                      <Box ml={10} my={2}>
                        <Typography variant="subtitle2" component="i">
                          No Other Attributes
                        </Typography>
                      </Box>
                    )}
                  </Grid>
                </Grid>
              </Box>
            </>
          )}
          {state?.species && (
            <Box mx={5} flexGrow={1}>
              <Alert severity="success">
                Species length weight updated : { state.species.map(s => ' [ ' + s.observableItemName + ' ] ')}
              </Alert>
            </Box>
          )}
          {state?.error && (
            <Box mx={5} flexGrow={1}>
              <Alert severity="error">{ state.error }</Alert>
            </Box>
          )}
        </EntityContainer>
      }
    </AuthContext.Consumer>
  );
};

export default ObservableItemView;
