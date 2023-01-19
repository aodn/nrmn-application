import React, { useState } from 'react';
import Box from '@mui/material/Box';
import Alert from '@mui/material/Alert';
import Grid from '@mui/material/Grid';
import LoadingButton from '@mui/lab/LoadingButton';
import Typography from '@mui/material/Typography';
import { PropTypes } from 'prop-types';
import { Paper, TextField } from '@mui/material';
import { changePassword } from '../../api/api';
import { AuthContext } from '../../contexts/auth-context';
import { useNavigate } from 'react-router-dom';

const ChangePasswordForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const onSubmit = (event, setAuth) => {
    event.preventDefault();
    const { username, password, newPassword, confirmNewPassword } = event.target.elements;
    if (newPassword.value !== confirmNewPassword.value) {
      setError('New password fields do not match');
      return;
    }
    setLoading(true);
    const form = { username: username?.value, password: password?.value, newPassword: newPassword?.value };
    changePassword(form, ({ data }) => {
      setLoading(false);
      if (data) {
        setError(data);
      } else {
        localStorage.removeItem('auth');
        setAuth({ expires: 0 });
        navigate('/login?reset', { push: true });
      }
    });
  };

  return (
    <AuthContext.Consumer>
      {({ setAuth }) => (
        <Grid container alignItems="center" direction="column" justifyContent="center" style={{ minHeight: '70vh' }}>
          <Paper>
            <Box paddingX={20} paddingY={5} minWidth="400px">
              <Typography variant="h4">Change Password</Typography>
              <hr />
              {error ? (<Alert data-testid="alert" severity="error">
                {error}
              </Alert>) : (<Alert data-testid="alert" severity="warning">
                Please choose a new password at least 8 characters long
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
                    fullWidth
                    color="primary"
                    disabled={loading}
                  />
                </Box>
                <Box my={2}>
                  <Typography variant="subtitle2">Confirm New Password</Typography>
                  <TextField
                    size="small"
                    name="confirmNewPassword"
                    type="password"
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
        </Grid>
      )}
    </AuthContext.Consumer>
  );
};

ChangePasswordForm.propTypes = {
  submitLabel: PropTypes.string
};

export default ChangePasswordForm;
