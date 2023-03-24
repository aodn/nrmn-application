import React, {useEffect, useState} from 'react';
import {Box, Paper, Typography} from '@mui/material';
import {getResult, entityEdit} from '../../api/api';
import {PropTypes} from 'prop-types';

const UserInstructions = () => (
  <Box p={1} border={1} borderRadius={2}>
    <ul>
      <li>
        <b>Add New User</b> will create a new user with the <samp>ROLE_DATA_OFFICER</samp> role and the default password <samp>login</samp>
        <br />
      </li>
      <li>
        <b>Edit</b> will change the user email or full name
        <br />
      </li>
      <li>
        <b>Disable Account</b> will prevent the user from logging in next (Removes all the user roles)
        <br />
      </li>
      <li>
        <b>Reset Password</b> will reset the users password to <samp>login</samp> and require a password change on next login
        <br />
      </li>
      <li>
        <b>Enable Admin</b> will give user access to the admin options from the user menu (toggles the <samp>ROLE_ADMIN</samp>)
        <br />
      </li>
    </ul>
  </Box>
);

const UserComponent = ({value}) => {
  const [user, setUser] = useState(value);
  const [userUpdate, setUserUpdate] = useState();
  const [editMode, setEditMode] = useState(false);

  useEffect(() => {
    if (!userUpdate) return;
    entityEdit('admin/user', userUpdate).then((res) => setUser(res.data));
  }, [userUpdate]);

  const userEnabled = user.roles.length > 0;
  const userIsAdmin = user.roles.includes('ROLE_ADMIN');
  return (
    <Box m={1} border={1} borderColor="lightgray" borderRadius={1} p={1} key={user.userId} display="flex" flexDirection="row">
      <Box flex={1}>
        {editMode ? <button onClick={() => setEditMode(false)}>Cancel</button> : <button onClick={() => setEditMode(true)}>Edit</button>}
      </Box>
      <Box flex={2}>{editMode ? <input value={user.email} /> : user.email}</Box>
      <Box flex={2}>{editMode ? <input value={user.fullName} /> : user.fullName}</Box>
      {editMode ? (
        <Box flex={3}>
          <button disabled>Save</button>
        </Box>
      ) : (
        <>
          <Box flex={3}>
            {userEnabled ? (
              <button onClick={() => setUserUpdate({...user, newRoles: []})}>Disable Account</button>
            ) : (
              <button onClick={() => setUserUpdate({...user, newRoles: ['ROLE_DATA_OFFICER']})}>Enable Account</button>
            )}
            <button disabled>Reset Password</button>
            {userIsAdmin ? <button disabled>Disable Admin</button> : <button disabled>Enable Admin</button>}
          </Box>
        </>
      )}
    </Box>
  );
};

UserComponent.propTypes = {
  value: PropTypes.object
};

const UserList = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    getResult('admin/users').then((res) => {
      setUsers(res.data);
    });
  }, []);

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">User Management</Typography>
        </Box>
      </Box>
      <Box m={2} flexGrow={1} minWidth={1000}>
        <UserInstructions />
        <Paper>
          <Box p={1}>
            <Box p={1}>
              <button disabled>Add New User</button>
            </Box>
            {users.map((u, i) => (
              <UserComponent key={i} value={u} />
            ))}
          </Box>
        </Paper>
      </Box>
    </>
  );
};

export default UserList;
