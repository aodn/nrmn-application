import React from 'react';
import { useDispatch, useSelector } from 'react-redux';
import BaseForm from '../../../../ui/src/components/BaseForm';
import { loginSubmitted } from './auth-reducer';
import { Redirect, useLocation } from 'react-router-dom';

const schema = {
  'title': 'Login',
  'type': 'object',
  'required': [
    'username',
    'password'
  ],
  'properties': {
    'username': {
      'type': 'string',
      'title': 'Email/Username',
      'format': 'email'
    },
    'password': {
      'type': 'string',
      'title': 'Password'
    }
  }
};

const uiSchema = {
  password: {
    'ui:widget': 'password'
  }
};

var Login = () => {

  const dispatch = useDispatch();
  const errors = useSelector(state => state.auth.errors);
  let loading = useSelector(state => state.auth.loading);
  let success = useSelector(state => state.auth.success);
  let redirect = useSelector(state => state.auth.redirect);

  const location = new URLSearchParams(useLocation().search).get('redirect');

  const handleLogin = (form) => {
    if (location) {
      form.formData.redirect = location;
    }
    dispatch(loginSubmitted(form.formData));
  };
  if (success) {
    return (<Redirect  component='link' to={redirect}></Redirect>);
  }
  return (
    <BaseForm
      schema={schema}
      uiSchema={uiSchema}
      errors={errors}
      loading={loading}
      onSubmit={handleLogin}>
    </BaseForm>
  );
};

export default Login;
