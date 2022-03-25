import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {AccountCircle} from '@mui/icons-material';
import {Button} from '@mui/material';
import AlertDialog from '../ui/AlertDialog';
import {userLogout} from '../../api/api';
import {AuthContext} from '../../contexts/auth-context';

const AuthState = () => {
  const navigate = useNavigate();
  const [confirmLogout, showConfirmLogout] = useState(false);

  return (
    <AuthContext.Consumer>
      {({auth, setAuth}) =>
        auth.expires < Date.now() ? (
          <Button variant="contained" disableElevation onClick={() => navigate('/login', {push: true})}>
            Log In
          </Button>
        ) : (
          <>
            <Button variant="contained" disableElevation startIcon={<AccountCircle />} onClick={() => showConfirmLogout(true)}>
              {auth.username}
              {auth.isAdmin && <span style={{color: 'yellow', marginLeft: 5}}>(ADMIN)</span>}
            </Button>
            <AlertDialog
              open={confirmLogout}
              text="Do you want to log out?"
              action="Log Out"
              onClose={() => showConfirmLogout(false)}
              onConfirm={() => {
                userLogout().then(() => {
                  showConfirmLogout(false);
                  localStorage.removeItem('auth');
                  setAuth({expires: 0});
                });
              }}
            />
          </>
        )
      }
    </AuthContext.Consumer>
  );
};

export default AuthState;
