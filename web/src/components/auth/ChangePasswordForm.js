import React, {useState} from 'react';
import Box from '@mui/material/Box';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';
import LoadingButton from '@mui/lab/LoadingButton';
import Typography from '@mui/material/Typography';
import {PropTypes} from 'prop-types';
import {Paper, TextField} from '@mui/material';
import {changePassword} from '../../api/api';
import {AuthContext} from '../../contexts/auth-context';
import Chip from '@mui/material/Chip';

const ChangePasswordForm = () => {
  const [loading, setLoading] = useState(false);
  const version = process.env.REACT_APP_VERSION ? process.env.REACT_APP_VERSION.split('.') : [0, 0, 0];

  const onSubmit = (event, setAuth) => {
    event.preventDefault();
    setLoading(true);
    const {username, password, newPassword} = event.target.elements;
    const form = {username: username?.value, password: password?.value, newPassword: newPassword?.value};
    changePassword(form, (result, error) => {
      setLoading(false);
      if (!error) setAuth(result);
    });
  };

  return (
    <AuthContext.Consumer>
      {({setAuth}) => (
        <Grid container alignItems="center" direction="column"  justifyContent="center" style={{minHeight: '70vh'}}>
          <Paper>
            <Box paddingX={20} paddingY={5}>
              <Typography variant="h4">Change Password</Typography>
              <hr />
              <Alert data-testid="alert" severity="warning">
                Please update your password
              </Alert>
              <form role="form" onSubmit={(e) => onSubmit(e, setAuth)}>
                <Box my={2}>
                  <Typography variant="subtitle2">Email</Typography>
                  <TextField
                    size="small"
                    name="username"
                    type="username"
                    placeholder="Email"
                    fullWidth
                    color="primary"
                    disabled={loading}
                  />
                </Box>
                <Box my={2}>
                  <Typography variant="subtitle2">Existing Password</Typography>
                  <TextField
                    size="small"
                    name="password"
                    type="password"
                    placeholder="Password"
                    fullWidth
                    color="primary"
                    disabled={loading}
                  />
                </Box>
                 <Box my={2}>
                  <Typography variant="subtitle2">New Password</Typography>
                  <TextField
                    size="small"
                    name="newPassword"
                    type="password"
                    placeholder="New Password"
                    fullWidth
                    color="primary"
                    disabled={loading}
                  />
                </Box>
                <Box my={2}>
                  <LoadingButton data-testid="submit" variant="contained" type="submit" fullWidth loading={loading}>
                    Change Password
                  </LoadingButton>
                </Box>
              </form>
            </Box>
          </Paper>
          <Box m={2}>
            <Chip variant="outlined" color="primary" size="small" label={`Version ${version[0]}.${version[1]} (${version[2]})`}/>
          </Box>
        </Grid>
      )}
    </AuthContext.Consumer>
  );
};

ChangePasswordForm.propTypes = {
  submitLabel: PropTypes.string
};

export default ChangePasswordForm;
