import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {useDispatch, useSelector} from 'react-redux';
import {logoutSubmitted} from './auth-reducer';
import {PropTypes} from 'prop-types';

var Logout = ({open}) => {
  const dispatch = useDispatch();
  const username = useSelector((state) => state.auth.username);

  const handleClose = (form) => {
    localStorage.clear();
    dispatch(logoutSubmitted(form.formData));
  };

  return (
    <>
      <Dialog open={open}>
        <DialogTitle id="alert-dialog-title">Logout</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">{`Do you really want to log out as '` + username + `' ?`}</DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} autoFocus>
            Logout
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default Logout;

Logout.propTypes = {
  open: PropTypes.bool
};
