import React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {useHistory} from 'react-router-dom';
import {AccountCircle, VerifiedUser} from '@material-ui/icons';
import Logout from '../auth/logout';
import {toggleLogoutMenuOpen} from './layout-reducer';
import {Button} from '@material-ui/core';

const AuthState = () => {
  const userName = useSelector((state) => state.auth.username);
  const dispatch = useDispatch();
  const history = useHistory();

  const openLogout = () => dispatch(toggleLogoutMenuOpen());

  if (!userName || userName === '') {
    return (
      <Button
        pt={10}
        variant="text"
        color="inherit"
        size="small"
        onClick={() => {
          history.push('/login');
        }}
        startIcon={<AccountCircle />}
      >
        Log In
      </Button>
    );
  } else {
    return (
      <>
        <Button pt={10} variant="text" color="inherit" size="small" title={'Log out'} startIcon={<VerifiedUser />} onClick={openLogout}>
          {userName}
        </Button>
        <Logout />
      </>
    );
  }
};

export default AuthState;
