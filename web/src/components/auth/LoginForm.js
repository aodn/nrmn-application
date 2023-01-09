import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';
import LoadingButton from '@mui/lab/LoadingButton';
import Typography from '@mui/material/Typography';
import { PropTypes } from 'prop-types';
import { Paper, TextField } from '@mui/material';
import { userLogin } from '../../api/api';
import { AuthContext } from '../../contexts/auth-context';
import Chip from '@mui/material/Chip';

const LoginForm = () => {
  const [formState, setFormState] = useState();
  const [loading, setLoading] = useState(false);
  const version = process.env.REACT_APP_VERSION ? process.env.REACT_APP_VERSION.split('.') : [0, 0, 0];

  const onSubmit = (event, setAuth) => {
    event.preventDefault();
    setLoading(true);
    const { username, password } = event.target.elements;
    const form = { username: username?.value, password: password?.value };
    userLogin(form, (result, error) => {
      setFormState(error ? { error } : { result });
      setLoading(false);
      if (!error) setAuth(result);
    });
  };

  const wasPasswordReset = window.location.search.includes('reset');

  return (
    <AuthContext.Consumer>
      {({ setAuth }) => (
        <Grid container alignItems="center" direction="column" justifyContent="center" style={{ minHeight: '70vh' }}>
          <Paper>
            <Box paddingX={20} paddingY={5} minWidth="400px">
              <Typography variant="h4">Login</Typography>
              <hr />
              {wasPasswordReset && !formState?.error ? (<Alert data-testid="alert" severity="success">
                Password reset successfully!<br />
                Please log in with your new password
              </Alert>) :
                (<Alert data-testid="alert" severity={formState?.error ? 'error' : 'info'}>
                  {formState?.error ? formState.error : formState?.result ? formState.result.username : 'Please login in to view this page'}
                </Alert>)}
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
                  <Typography variant="subtitle2">Password</Typography>
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
                  <LoadingButton data-testid="submit" variant="contained" type="submit" fullWidth loading={loading}>
                    Submit
                  </LoadingButton>
                </Box>
              </form>
            </Box>
          </Paper>
          <Box m={2}>
            <Chip variant="outlined" color="primary" size="small" label={`Version ${version[0]}.${version[1]} (${version[2]})`} />
          </Box>
        </Grid>
      )}
    </AuthContext.Consumer>
  );
};

LoginForm.propTypes = {
  submitLabel: PropTypes.string
};

export default LoginForm;
