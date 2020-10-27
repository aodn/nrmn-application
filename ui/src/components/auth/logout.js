import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {useDispatch, useSelector} from "react-redux";
import {logoutSubmitted} from "./auth-reducer";
import {toggleLogoutMenuOpen} from "../../../../ui/src/components/layout/layout-reducer";
import store from "../store";


function Logout() {
  const dispatch = useDispatch();
  const logoutMenuOpen = useSelector(state => state.toggle.logoutMenuOpen);
  const username = useSelector(state => state.auth.username);

  const handleCancel = () => {
    store.dispatch(toggleLogoutMenuOpen());
  };

  const handleClose = (form) => {
    dispatch(logoutSubmitted(form.formData));
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

export default Logout;
