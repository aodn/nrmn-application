import React, {useEffect, useReducer} from 'react';
import {Box, Typography} from '@mui/material';
import {entityEdit, entitySave} from '../../api/api';
import {PropTypes} from 'prop-types';
import TargetUrlComponent from '../input/TargetUrlComponent';
import { AppConstants } from '../../common/constants';

const UserComponent = ({value, onAdd}) => {
  const addMode = typeof onAdd !== 'undefined';

  const dataOfficerRole = [AppConstants.ROLES.DATA_OFFICER];
  const surveyEditorRole = [AppConstants.ROLES.SURVEY_EDITOR];
  const adminRoles = [AppConstants.ROLES.DATA_OFFICER, AppConstants.ROLES.ADMIN];

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
            userIsSurveyEditor: action.value.roles.includes(AppConstants.ROLES.SURVEY_EDITOR),
            userIsDataOfficer: action.value.roles.includes(AppConstants.ROLES.DATA_OFFICER),
            userIsAdmin: action.value.roles.includes(AppConstants.ROLES.ADMIN),
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
            case 'toggleDataOfficer':
              return {
                ...state,
                userPayload: {
                  ...state.user,
                  roles: !state.user.roles.includes(AppConstants.ROLES.DATA_OFFICER) ? dataOfficerRole : []
                }
              };
            case 'toggleSurveyEditor':
              return {
                ...state,
                userPayload: {
                  ...state.user,
                  roles: !state.user.roles.includes(AppConstants.ROLES.SURVEY_EDITOR) ? surveyEditorRole : []
                }
              };
            case 'toggleIsAdmin':
              return {
                ...state,
                userPayload: {
                  ...state.user,
                  roles: state.user.roles.includes(AppConstants.ROLES.ADMIN) ? dataOfficerRole : adminRoles
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
      userIsSurveyEditor: value.roles.includes(AppConstants.ROLES.SURVEY_EDITOR),
      userIsDataOfficer: value.roles.includes(AppConstants.ROLES.DATA_OFFICER),
      userIsAdmin: value.roles.includes(AppConstants.ROLES.ADMIN),
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
        <>
          {state.editMode ? (
            <>
              <input onChange={(e) => dispatch({type: 'userUpdate', value: {email: e.target.value}})} value={state.userUpdate.email} />
              {state.error && <br />}
            </>
          ) : (
            <Typography fontSize={13} backgroundColor={state.userIsAdmin && 'yellow'} color={!state.userEnabled && 'gray'}>
              {state.user.email}
            </Typography>
          )}
          <span style={{color: 'red'}}>{state.error}</span>
        </>
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
            <button onClick={() => dispatch({type: 'submit', value: 'toggleSurveyEditor'})}>
              {state.userIsSurveyEditor ? 'Disable' : 'Enable'} Survey Editor
            </button>
            <button onClick={() => dispatch({type: 'submit', value: 'toggleDataOfficer'})} disabled={state.userIsAdmin}>
              {state.userIsDataOfficer || state.userIsAdmin ? 'Disable' : 'Enable'} Data Officer
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
