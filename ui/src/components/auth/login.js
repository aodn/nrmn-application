import React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import BaseForm from '../../../../ui/src/components/BaseForm';
import {loginSubmitted} from './auth-reducer';
import {useLocation} from 'react-router-dom';

const schema = {
  title: 'Login',
  type: 'object',
  required: ['username', 'password'],
  properties: {
    username: {
      type: 'string',
      title: 'Email/Username',
      format: 'email'
    },
    password: {
      type: 'string',
      title: 'Password'
    }
  }
};

const uiSchema = {
  password: {
    'ui:widget': 'password'
  }
};

const Login = () => {
  const dispatch = useDispatch();
  const location = useLocation();
  const errors = useSelector((state) => state.auth.errors);
  const loading = useSelector((state) => state.auth.loading);
  const message = errors.length === 0 && location.pathname !== '/login' ? ['Please login to view this page'] : errors;
  return (
    <>
      <BaseForm
        schema={schema}
        uiSchema={uiSchema}
        errors={message}
        loading={loading}
        hideCancel={true}
        onSubmit={(form) => dispatch(loginSubmitted(form.formData))}
        submitLabel="Login"
      ></BaseForm>
    </>
  );
};

export default Login;
