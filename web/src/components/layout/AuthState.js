import React, {useState} from 'react';
import {useNavigate} from 'react-router-dom';
import {AccountCircle} from '@mui/icons-material';
import {Button, ClickAwayListener, Divider, Popper, MenuList, MenuItem, Grow, Paper} from '@mui/material';
import AlertDialog from '../ui/AlertDialog';
import {userLogout} from '../../api/api';
import {AuthContext} from '../../contexts/auth-context';

import {runDailyTasks, runStartupTasks} from '../../api/api';

const AuthState = () => {
  const navigate = useNavigate();
  const [confirmLogout, showConfirmLogout] = useState(false);
  const [open, setOpen] = useState(false);
  const anchorRef = React.useRef(null);

  const handleClose = (event) => {
    if (anchorRef.current && anchorRef.current.contains(event.target)) {
      return;
    }
    setOpen(false);
  };

  function handleListKeyDown(event) {
    if (event.key === 'Tab') {
      event.preventDefault();
      setOpen(false);
    } else if (event.key === 'Escape') {
      setOpen(false);
    }
  }

  const prevOpen = React.useRef(open);
  React.useEffect(() => {
    if (prevOpen.current === true && open === false) {
      anchorRef.current.focus();
    }

    prevOpen.current = open;
  }, [open]);

  return (
    <AuthContext.Consumer>
      {({auth, setAuth}) =>
        auth.expires < Date.now() ? (
          <Button variant="contained" disableElevation onClick={() => navigate('/login', {push: true})}>
            Log In
          </Button>
        ) : (
          <>
            <Button
              ref={anchorRef}
              variant="contained"
              disableElevation
              startIcon={<AccountCircle />}
              onClick={() => setOpen((prevOpen) => !prevOpen)}
            >
              {auth.username}
              {auth.roles.includes('ROLE_ADMIN') && <span style={{color: 'yellow', marginLeft: 5}}>(ADMIN)</span>}
            </Button>
            {auth.username && (
              <Popper open={open} anchorEl={anchorRef.current} role={undefined} placement="bottom-start" transition disablePortal>
                {({TransitionProps}) => (
                  <Grow
                    {...TransitionProps}
                    style={{
                      transformOrigin: 'right top'
                    }}
                  >
                    <Paper>
                      <ClickAwayListener onClickAway={handleClose}>
                        <MenuList
                          autoFocusItem={open}
                          id="composition-menu"
                          aria-labelledby="composition-button"
                          onKeyDown={handleListKeyDown}
                        >
                          {auth.roles.includes('ROLE_ADMIN') && [
                            <MenuItem
                              key="runDailyTasks"
                              onClick={(e) => {
                                runDailyTasks();
                                handleClose(e);
                              }}
                            >
                              Refresh Endpoints
                            </MenuItem>,
                            <MenuItem
                              key="runStartupTasks"
                              onClick={(e) => {
                                runStartupTasks();
                                handleClose(e);
                              }}
                            >
                              Clear Ingestion Lock
                            </MenuItem>,
                            <MenuItem
                              key="userManagement"
                              onClick={(e) => {
                                handleClose(e);
                                navigate('/admin/users', {push: true});
                              }}
                            >
                              Manage Users
                            </MenuItem>,
                            <Divider key="adminDivider" />
                          ]}
                          <MenuItem
                            onClick={(e) => {
                              handleClose(e);
                              navigate('/changePassword', {push: true});
                            }}
                          >
                            Change Password
                          </MenuItem>
                          <MenuItem
                            onClick={(e) => {
                              handleClose(e);
                              showConfirmLogout(true);
                            }}
                          >
                            Logout
                          </MenuItem>
                        </MenuList>
                      </ClickAwayListener>
                    </Paper>
                  </Grow>
                )}
              </Popper>
            )}
            <AlertDialog
              open={confirmLogout}
              text="Do you want to log out?"
              action="Log Out"
              onClose={() => showConfirmLogout(false)}
              onConfirm={() => {
                userLogout().then(() => {
                  showConfirmLogout(false);
                  localStorage.removeItem('auth');
                  setAuth({expires: 0});
                });
              }}
            />
          </>
        )
      }
    </AuthContext.Consumer>
  );
};

export default AuthState;
