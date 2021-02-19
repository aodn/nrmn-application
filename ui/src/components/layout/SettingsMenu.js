import React from 'react';
import {ListItem, ListItemText} from '@material-ui/core';
import store from '../store';
import {toggleColumnFit} from './theme-reducer';
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import Switch from '@material-ui/core/Switch';

const SettingsMenu = () => {
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleSettingsClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleColumnFitMethod = (event) => {
    store.dispatch(toggleColumnFit(event.target.checked));
    handleClose();
  };

  return (
    <>
      <ListItem button onClick={handleSettingsClick}>
        <ListItemText primary="Settings" />
      </ListItem>
      <Menu id="settings-menu" anchorEl={anchorEl} keepMounted open={Boolean(anchorEl)} onClose={handleClose}>
        <MenuItem>
          <FormControlLabel
            label="Fit to Content"
            labelPlacement="start"
            control={
              <Switch
                title="Fit all columns in table"
                onChange={handleColumnFitMethod}
                size="small"
                checked={store.getState().theme.columnFit}
              />
            }
          />
        </MenuItem>
      </Menu>
    </>
  );
};

export default SettingsMenu;
