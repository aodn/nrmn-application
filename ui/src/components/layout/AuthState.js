import React from 'react';
import { connect, useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import store from '../store';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import AccountCircle from '@material-ui/icons/AccountCircle';
import TopbarButton from './TopbarButton';
import Logout from '../auth/logout';
import { toggleLogoutMenuOpen } from './layout-reducer';

const basicButton = {
  textTransform: 'none',
  fontWeight: 300
};

const AuthState = () => {
  const username = useSelector(state => state.auth.username);
  const dispatch = useDispatch();
  const openLogout = (dis) => dis(toggleLogoutMenuOpen());

  return (
    <>
      { (username) ?
        <>
          <TopbarButton
            variant="text"
            color="secondary"
            size="small"
            title={'Log out'}
            style={basicButton}
            startIcon={<VerifiedUserIcon />}
            onClick={() => openLogout(dispatch)}> Logged in as {username}</TopbarButton>
          <Logout />
        </> :
        <>
          <TopbarButton
            color="secondary"
            size="small"
            startIcon={<AccountCircle />}
            component={Link}
            to="/login"
          >Login</TopbarButton>
        </>
      }
    </>
  );
};

export default AuthState;
