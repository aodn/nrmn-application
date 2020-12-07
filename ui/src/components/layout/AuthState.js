import React from 'react';
import { connect, useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import store from '../store';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import AccountCircle from '@material-ui/icons/AccountCircle';
import TopbarButton from './TopbarButton';
import Logout from '../auth/logout';
import { toggleLogoutMenuOpen } from './layout-reducer';
import { Button } from '@material-ui/core';


const AuthState = () => {
  const userName = useSelector(state => state.auth.username);
  const dispatch = useDispatch();
  const openLogout = (dis) => dis(toggleLogoutMenuOpen());
  if (!userName || userName === '')
    return (<></>);

  return (<>
    <Button pt={10}
      variant="text"
      color="inherit"
      size="small"
      title={'Log out'}
      startIcon={<VerifiedUserIcon />}
      onClick={() => openLogout(dispatch)}> Logged in as {userName}</Button>
    <Logout /> |
  </>);

};

export default AuthState;
