import React, {useEffect, useReducer} from 'react';
import {Box, Typography} from '@mui/material';
import {entityEdit, entitySave} from '../../api/api';
import {PropTypes} from 'prop-types';
import TargetUrlComponent from '../input/TargetUrlComponent';

const UserComponent = ({value, onAdd}) => {
  const addMode = typeof onAdd !== 'undefined';

  const userRoles = ['ROLE_DATA_OFFICER'];
  const adminRoles = ['ROLE_DATA_OFFICER', 'ROLE_ADMIN'];

  const [state, dispatch] = useReducer(
    (state, action) => {
      switch (action.type) {
        case 'error':
          return {...state, error: action.value};
        case 'user':
          return {
            user: action.value,
            userUpdate: action.value,
            userEnabled: action.value.roles.length > 0,
            userIsAdmin: action.value.roles.includes('ROLE_ADMIN'),
            userPayload: null,
            editMode: false,
            error: null
          };
        case 'editMode':
          return {...state, editMode: action.value, userUpdate: {...state.user}, error: null};
        case 'userUpdate':
          return {...state, userUpdate: {...state.userUpdate, ...action.value}};
        case 'submit': {
          switch (action.value) {
            case 'resetPassword':
              return {...state, userPayload: {...state.user, resetPassword: true}};
            case 'toggleUserEnabled':
              return {
                ...state,
                userPayload: {
                  ...state.user,
                  roles: state.user.roles.length > 0 ? [] : userRoles
                }
              };
            case 'toggleIsAdmin':
              return {
                ...state,
                userPayload: {
                  ...state.user,
                  roles: state.user.roles.includes('ROLE_ADMIN') ? userRoles : adminRoles
                }
              };
            default:
              return {...state, userPayload: {...state.userUpdate}};
          }
        }
        default:
          return state;
      }
    },
    {
      user: value,
      userUpdate: value,
      userEnabled: value.roles.length > 0,
      userIsAdmin: value.roles.includes('ROLE_ADMIN'),
      userPayload: null,
      editMode: addMode,
      error: null
    }
  );

  useEffect(() => {
    const submitUserUpdate = async () => {
      const method = addMode && state.user.email === '' ? entitySave : entityEdit;
      await method('admin/user', state.userPayload).then((res) => {
        if (res.data.error) {
          dispatch({type: 'error', value: res.data.error});
        } else {
          dispatch({type: 'user', value: res.data});
        }
      });
    };
    if (state.userPayload) submitUserUpdate();
  }, [state.userPayload, state.user, addMode]);

  return (
    <Box m={1} border={1} borderColor="lightgray" borderRadius={1} p={1} key={state.user.userId} display="flex" flexDirection="row">
      <Box flex={1}>
        <button onClick={() => dispatch({type: 'editMode', value: !state.editMode})}>{state.editMode ? 'Cancel' : 'Edit'}</button>
      </Box>
      <Box flex={2}>
        {state.editMode ? (
          <>
            <input onChange={(e) => dispatch({type: 'userUpdate', value: {email: e.target.value}})} value={state.userUpdate.email} />
            <br />
            <span style={{color: 'red'}}>{state.error}</span>
          </>
        ) : (
          <Typography fontSize={13} backgroundColor={state.userIsAdmin && 'yellow'} color={!state.userEnabled && 'gray'}>
            {state.user.email}
          </Typography>
        )}
      </Box>
      <Box flex={2}>
        {state.editMode ? (
          <input onChange={(e) => dispatch({type: 'userUpdate', value: {fullName: e.target.value}})} value={state.userUpdate.fullName} />
        ) : (
          <Typography fontSize={13} backgroundColor={state.userIsAdmin && 'yellow'} color={!state.userEnabled && 'gray'}>
            {state.user.fullName}
          </Typography>
        )}
      </Box>
      {state.editMode ? (
        <Box flex={3}>
          <button onClick={() => dispatch({type: 'submit'})}>Save</button>
        </Box>
      ) : (
        <>
          <Box flex={3}>
            {state.user.newPassword ? (
              <TargetUrlComponent value={state.user.newPassword} />
            ) : (
              <button onClick={() => dispatch({type: 'submit', value: 'resetPassword'})}>Reset Password</button>
            )}
            <button onClick={() => dispatch({type: 'submit', value: 'toggleUserEnabled'})}>
              {state.userEnabled ? 'Disable' : 'Enable'} Account
            </button>
            <button onClick={() => dispatch({type: 'submit', value: 'toggleIsAdmin'})}>
              {state.userIsAdmin ? 'Disable' : 'Enable'} Admin
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
