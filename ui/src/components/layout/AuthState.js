import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import Logout from '../auth/logout';
import { toggleLogoutMenuOpen } from './layout-reducer';
import { Button } from '@material-ui/core';

const AuthState = () => {
  const userName = useSelector(state => state.auth.username);
  const dispatch = useDispatch();

  const openLogout = () => dispatch(toggleLogoutMenuOpen());

  if (!userName || userName === '')
    return (<></>);

  return (<>
    <Button pt={10}
      variant="text"
      color="inherit"
      size="small"
      title={'Log out'}
      startIcon={<VerifiedUserIcon />}
      onClick={openLogout}>{userName}</Button>
    <Logout />
  </>);

};

export default AuthState;
