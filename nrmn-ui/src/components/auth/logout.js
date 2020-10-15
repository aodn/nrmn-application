import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {connect} from "react-redux";
import store from "../store";
import {logout} from "../import/reducers/auth-reducer";
import {toggleLogoutMenuOpen} from "../import/reducers/redux-layout";


const LogoutDialog = (props) => {
  const { logoutMenuOpen, username } = props;

  const handleCancel = () => {
    store.dispatch(toggleLogoutMenuOpen());
  };

  const handleClose = () => {
    store.dispatch(logout())
    handleCancel();
  };


  return (
      <>
        <Dialog
            open={logoutMenuOpen}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
        >
          <DialogTitle id="alert-dialog-title">Logout</DialogTitle>
          <DialogContent>
            <DialogContentText id="alert-dialog-description">
              Do you really want to log out as '{username}'?
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={handleCancel} color="primary">
              Cancel
            </Button>
            <Button onClick={handleClose} color="primary" autoFocus>
              Logout
            </Button>
          </DialogActions>
        </Dialog>
      </>
  );
}
const mapStateToProps = (state) => {
  return {
    logoutMenuOpen: state.toggle.logoutMenuOpen,
    username: state.auth.username
  };
};

const Logout = connect(mapStateToProps)(LogoutDialog);
export default Logout;
