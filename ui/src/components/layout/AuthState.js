import React from 'react';
import {connect} from 'react-redux';
import { Link } from 'react-router-dom';
import store from '../store';
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import AccountCircle from '@material-ui/icons/AccountCircle';
import { TopbarButton } from './TopbarButton';
import Logout from '../auth/logout';
import {toggleLogoutMenuOpen} from './layout-reducer';
import {PropTypes} from 'prop-types';
const basicButton = {
  textTransform: 'none',
  fontWeight: 300
};

class AuthState extends React.Component {

  openLogout = () => {
    store.dispatch(toggleLogoutMenuOpen());
  }

  render(){

    const { username } = this.props;

    return (

        <>
          { (username) ?
              <>
                <TopbarButton
                    variant="text"
                    color="secondary"
                    size="small"
                    title={'Log out'}
                    style={ basicButton}
                    startIcon={<VerifiedUserIcon />}
                    onClick={this.openLogout}> { username }</TopbarButton>
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
  }
}

function mapStateToProps(state) {
  return {
    username: state.auth.username
  };
}
AuthState.propTypes = {
  username: PropTypes.string
};
export default connect(mapStateToProps)(AuthState);
