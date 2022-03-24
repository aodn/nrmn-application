import React, {useState} from 'react';
import Box from '@mui/material/Box';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';
import LoadingButton from '@mui/lab/LoadingButton';
import Typography from '@mui/material/Typography';
import {PropTypes} from 'prop-types';
import {Paper, TextField} from '@mui/material';
import {userLogin} from '../../axios/api';
import {AuthContext} from '../../auth-context';

const LoginForm = () => {
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const onSubmit = (event, setAuth) => {
    event.preventDefault();
    setLoading(true);
    const form = {username: event.target.username.value, password: event.target.password.value};
    userLogin(form, (res, err) => {
      setError(err);
      setLoading(false);
      setAuth(res);
    });
  };

  return (
    <AuthContext.Consumer>
      {({setAuth}) => (
        <Grid container alignItems="center" justifyContent="center" style={{minHeight: '70vh'}}>
          <Paper>
            <Box paddingX={20} paddingY={5}>
              <Typography variant="h4">Sign in</Typography>
              <hr />
              {error ? <Alert severity="error">{error}</Alert> : <Alert severity="info">Please sign in to view this page</Alert>}
              <form onSubmit={(e) => onSubmit(e, setAuth)}>
                <Box my={2}>
                  <Typography variant="subtitle2">Email</Typography>
                  <TextField size="small" name="username" type="username" fullWidth color="primary" disabled={loading} />
                </Box>
                <Box my={2}>
                  <Typography variant="subtitle2">Password</Typography>
                  <TextField size="small" name="password" type="password" fullWidth color="primary" disabled={loading} />
                </Box>
                <Box my={2}>
                  <LoadingButton variant="contained" type="submit" fullWidth loading={loading}>
                    Submit
                  </LoadingButton>
                </Box>
              </form>
            </Box>
          </Paper>
        </Grid>
      )}
    </AuthContext.Consumer>
  );
};

LoginForm.propTypes = {
  submitLabel: PropTypes.string
};

export default LoginForm;
