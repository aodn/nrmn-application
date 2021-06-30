import React from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';
import {PropTypes} from 'prop-types';

const AlertDialog = ({action, text, open, onClose, onConfirm}) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle id="alert-dialog-title">Confirm</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">{text}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} variant="text">
          Cancel
        </Button>
        <Button onClick={onConfirm}>{action}</Button>
      </DialogActions>
    </Dialog>
  );
};

export default AlertDialog;

AlertDialog.propTypes = {
  action: PropTypes.string,
  open: PropTypes.bool,
  text: PropTypes.string,
  onClose: PropTypes.func,
  onConfirm: PropTypes.func
};
