import React, {useEffect, useState} from 'react';
import {Box, Typography} from '@mui/material';
import {entityEdit, entitySave} from '../../api/api';
import {PropTypes} from 'prop-types';
import TargetUrlComponent from '../input/TargetUrlComponent';

const UserComponent = ({value, onAdd}) => {
  const addMode = typeof onAdd !== 'undefined';
  const [user, setUser] = useState(value);
  const [userUpdate, setUserUpdate] = useState(value);
  const [editMode, setEditMode] = useState(addMode);

  const submitUserUpdate = async () => {
    const method = addMode ? entitySave : entityEdit;
    await method('admin/user', userUpdate).then((res) => {
      if (res.data.error) {
        setUser((u) => ({...u, error: res.data.error}));
      } else {
        setUser(res.data);
        setEditMode(false);
      }
    });
  };

  useEffect(() => {
    if (!editMode && onAdd) onAdd(null);
  }, [editMode]);

  useEffect(() => {
    if (onAdd && user?.userId) onAdd(user);
  }, [user]);

  const userEnabled = user.roles.length > 0;
  const userIsAdmin = user.roles.includes('ROLE_ADMIN');
  return (
    <Box m={1} border={1} borderColor="lightgray" borderRadius={1} p={1} key={user.userId} display="flex" flexDirection="row">
      <Box flex={1}>
        {editMode ? <button onClick={() => setEditMode(false)}>Cancel</button> : <button onClick={() => setEditMode(true)}>Edit</button>}
      </Box>
      <Box flex={2}>
        {editMode ? (
          <>
            {' '}
            <input onChange={(e) => setUserUpdate((u) => ({...u, email: e.target.value}))} value={userUpdate.email} />
            <br />
            <span style={{color: 'red'}}>{user.error}</span>
          </>
        ) : (
          <Typography fontSize={13} backgroundColor={userIsAdmin && 'yellow'} color={!userEnabled && 'gray'}>
            {user.email}
          </Typography>
        )}
      </Box>
      <Box flex={2}>
        {editMode ? (
          <input onChange={(e) => setUserUpdate((u) => ({...u, fullName: e.target.value}))} value={userUpdate.fullName} />
        ) : (
          <Typography fontSize={13} backgroundColor={userIsAdmin && 'yellow'} color={!userEnabled && 'gray'}>
            {user.fullName}
          </Typography>
        )}
      </Box>
      {editMode ? (
        <Box flex={3}>
          <button onClick={() => submitUserUpdate()}>Save</button>
        </Box>
      ) : (
        <>
          <Box flex={3}>
            {user.newPassword ? (
              <TargetUrlComponent value={user.newPassword} />
            ) : (
              <button onClick={() => setUserUpdate({...user, resetPassword: true})}>Reset Password</button>
            )}
            <button onClick={() => setUserUpdate({...user, roles: userEnabled ? [] : ['ROLE_DATA_OFFICER']})}>
              {userEnabled ? 'Disable' : 'Enable'} Account
            </button>
            <button onClick={() => setUserUpdate({...user, roles: userIsAdmin ? ['ROLE_DATA_OFFICER'] : ['ROLE_ADMIN']})}>
              {userIsAdmin ? 'Disable' : 'Enable'} Admin
            </button>
          </Box>
        </>
      )}
    </Box>
  );
};

export default UserComponent;

UserComponent.propTypes = {
  value: PropTypes.object,
  onAdd: PropTypes.func
};
