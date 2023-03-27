import React, {useEffect, useState} from 'react';
import {Box, Paper, Typography} from '@mui/material';
import {getResult} from '../../api/api';
import UserComponent from './UserComponent';

const UserList = () => {
  const [users, setUsers] = useState([]);
  const [newUser, setNewUser] = useState(null);

  useEffect(() => {
    async function getUsers() {
      await getResult('admin/users').then((res) => setUsers(res.data));
    }
    getUsers();
  } , []);

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">User Management</Typography>
        </Box>
      </Box>
      <Box m={2} flexGrow={1} minWidth={1000}>
        <Box p={1} border={1} borderRadius={2}>
          <ul>
            <li>
              <b>Add New User</b> will create a new user with the <samp>ROLE_DATA_OFFICER</samp> and assign a random password that must be
              changed on first login.
            </li>
            <li>
              <b>Edit</b> will change the user email or full name.
            </li>
            <li>
              <b>Disable Account</b> will prevent the user from logging in next time but removing all user roles and setting account status
              to <samp>DEACTIVATED</samp>.
            </li>
            <li>
              <b>Reset Password</b> will assign a random password that must be changed on first login.
            </li>
            <li>
              <b>Enable Admin</b> will give user access to the admin options from the user menu (toggles the <samp>ROLE_ADMIN</samp>). Admin
              users will appear highlighted in <span style={{backgroundColor: 'yellow'}}>yellow</span>.
            </li>
          </ul>
        </Box>
        <Paper>
          <Box p={1}>
            <Box p={1}>
              <button onClick={() => setNewUser({email: '', fullName: '', roles: []})}>Add New User</button>
            </Box>
            <>
              {newUser && (
                <UserComponent
                  onAdd={(newUser) => {
                    if (newUser?.userId) setUsers((u) => [newUser, ...u]);
                    setNewUser(null);
                  }}
                  value={newUser}
                />
              )}
              {users.map((u) => (
                <UserComponent key={u.userId} value={u} />
              ))}
            </>
          </Box>
        </Paper>
      </Box>
    </>
  );
};

export default UserList;
