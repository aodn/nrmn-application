import React from 'react';
import IconButton from '@material-ui/core/IconButton';
import store from '../store';
import {toggleColumnFit, toggleTheme} from './theme-reducer';
import {Settings} from '@material-ui/icons';
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

  const handleToggleThemeChange = event => {
    store.dispatch(toggleTheme(event.target.checked));
    handleClose();
  };

  const handleColumnFitMethod = event => {
    store.dispatch(toggleColumnFit(event.target.checked));
    handleClose();
  };

  return (
      <>
      <IconButton color="inherit" aria-label="upload picture" component="span" onClick={handleSettingsClick}>
        <Settings />
      </IconButton>
      <Menu
          id="settings-menu"
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClose}
      >
        <MenuItem>
          <FormControlLabel
            label="Theme"
            labelPlacement="start"
            control={
              <Switch title="Dark/Light theme toggle"
                      onChange={handleToggleThemeChange}
                      size="small"
                      checked={store.getState().theme.themeType} />
            }
          /></MenuItem>
        <MenuItem>
          <FormControlLabel
            label="Fit to Content"
            labelPlacement="start"
            control={
              <Switch title="Fit all columns in table"
                      onChange={handleColumnFitMethod}
                      size="small"
                      checked={store.getState().theme.columnFit} />
            }
          /></MenuItem>
      </Menu>
      </>
);

};

export default SettingsMenu;
