import React from "react";
import {connect} from "react-redux";
import store from '../store';
import { login, logout} from '../import/reducers/auth-reducer'
import VerifiedUserIcon from '@material-ui/icons/VerifiedUser';
import AccountCircle from '@material-ui/icons/AccountCircle';
import { TopbarButton } from './TopbarButton'

const basicButton = {
  textTransform: 'none',
  fontWeight: 300
};

class AuthState extends React.Component {

  handleLogin = () => {
    store.dispatch(login())
  }

  handleLogout = () => {
    store.dispatch(logout())
  }

  render(){

    const { loggedIn, username } = this.props;

    return (
        <>
          { (loggedIn) ?
              <>
                <TopbarButton
                    variant="text"
                    color="secondary"
                    size="small"
                    title={"Log out"}
                    style={ basicButton}
                    startIcon={<VerifiedUserIcon />}
                    onClick={this.handleLogout}> Logged in as '{ username }'</TopbarButton>
              </> :
              <>
                <TopbarButton  variant="text"
                         color="secondary"
                         size="small"
                         href="/register"
                >Register</TopbarButton> |
                <TopbarButton
                        color="secondary"
                        size="small"
                        startIcon={<AccountCircle />}
                        onClick={this.handleLogin}
                >Login</TopbarButton>
              </>
          }
        </>
    )
  }
}

function mapStateToProps(state) {
  return {
    loggedIn: state.auth.loggedIn,
    username: state.auth.username
  };
}

export default connect(mapStateToProps)(AuthState);
