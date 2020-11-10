import React from 'react';
import {useDispatch, useSelector} from "react-redux";
import BaseForm from "../../../../ui/src/components/BaseForm";
import {loginSubmitted} from "./auth-reducer";
import {useLocation}  from "react-router-dom";


function Login()  {

  const schema = {
    "title": "Login",
    "type": "object",
    "required": [
      "username",
      "password"
    ],
    "properties": {
      "username": {
        "type": "string",
        "title": "Email/Username"
      },
      "password": {
        "type": "string",
        "title": "Password",
      }
    }
  }

  const uiSchema = {
    password: {
      "ui:widget": "password"
    }
  };

  const dispatch = useDispatch();
  const errors = useSelector(state => state.auth.errors);
  let loading = useSelector(state => state.auth.loading);
  const location = new URLSearchParams(useLocation().search).get('redirect');
  console.log(location);

  const handleLogin = (form) => {
    if (location) {
      form.formData.redirect = location;
    }
    dispatch(loginSubmitted(form.formData));
  }

  return (
      <BaseForm
        schema={schema}
        uiSchema={uiSchema}
        errors={errors}
        loading={loading}
        onSubmit={handleLogin}>
      </BaseForm>
  )
}

export default Login;
