import React from 'react';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';

interface AlertDialogProps {
  action?: string,
  open: boolean,
  text?: string,
  onClose?: React.MouseEventHandler<HTMLButtonElement>,
  onConfirm?: React.MouseEventHandler<HTMLButtonElement>,
}

const AlertDialog: React.FC<AlertDialogProps> = ({
  action, text,
  open = false,
  onClose,
  onConfirm,
}) => {
  return (
    <Dialog open={open} onClose={onClose}>
      <DialogTitle id="alert-dialog-title">Confirm</DialogTitle>
      <DialogContent>
        <DialogContentText id="alert-dialog-description">{text}</DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onClose}>
          Cancel
        </Button>
        <Button variant="contained" onClick={onConfirm}>
          {action}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default AlertDialog;
