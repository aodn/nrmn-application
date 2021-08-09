import React, {useState} from 'react';
import {useSelector} from 'react-redux';
import {useHistory} from 'react-router-dom';
import {AccountCircle} from '@material-ui/icons';
import {Button} from '@material-ui/core';
import {useDispatch} from 'react-redux';
import {logoutSubmitted} from '../auth/auth-reducer';
import AlertDialog from '../ui/AlertDialog';

const AuthState = () => {
  const expires = useSelector((state) => state.auth.expires);
  const username = useSelector((state) => state.auth.username);
  const history = useHistory();
  const [confirmLogout, showConfirmLogout] = useState(false);
  const loggedIn = Date.now() < expires;
  const dispatch = useDispatch();

  if (!loggedIn) {
    return (
      <Button
        pt={10}
        variant="text"
        color="inherit"
        size="small"
        onClick={() => {
          history.push('/login');
        }}
      >
        Log In
      </Button>
    );
  } else {
    return (
      <>
        <Button
          pt={10}
          variant="text"
          color="inherit"
          size="small"
          title="Log out"
          startIcon={<AccountCircle />}
          onClick={() => showConfirmLogout(true)}
        >
          {username}
        </Button>
        <AlertDialog
          open={confirmLogout}
          text="Do you want to log out?"
          action="Log Out"
          onClose={() => showConfirmLogout(false)}
          onConfirm={() => dispatch(logoutSubmitted())}
        />
      </>
    );
  }
};

export default AuthState;
