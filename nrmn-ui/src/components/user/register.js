import React from 'react';
import FormRenderer from '@data-driven-forms/react-form-renderer/dist/cjs/form-renderer';
import componentTypes from '@data-driven-forms/react-form-renderer/dist/cjs/component-types';
import componentMapper from '@data-driven-forms/mui-component-mapper/dist/cjs/component-mapper';
import FormTemplate from '@data-driven-forms/mui-component-mapper/dist/cjs/form-template';
import validatorTypes from '@data-driven-forms/react-form-renderer/dist/cjs/validator-types';
import {connect} from "react-redux";
import Container from "@material-ui/core/Container";
import Grid from "@material-ui/core/Grid";
import store from "../store";
import {registerUser} from "../import/reducers/auth-reducer";


// https://data-driven-forms.org/

const validatorMapper = {
  'same-email': () => (
      value, allValues
  ) => (
      value !== allValues.email ?
          'Email does not match' :
          undefined
  )
}

const schema = {
  fields: [{
    component: componentTypes.TEXT_FIELD,
    name: 'full_name',
    label: 'Your full name',
    isRequired: true,
    validate: [{ type: validatorTypes.REQUIRED }]
  },
    {
    component: componentTypes.TEXT_FIELD,
    name: 'email',
    label: 'Email',
    isRequired: true,
    validate: [
      { type: validatorTypes.REQUIRED },
      {
        type: validatorTypes.PATTERN,
        pattern: '[a-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,}$',
        message: 'Not valid email'
      }
    ]
  },{
    component: componentTypes.TEXT_FIELD,
    name: 'confirm-email',
    label: 'Confirm email',
    type: 'email',
    isRequired: true,
    validate: [{ type: 'same-email' }]
  }]
}

const submitRegisterUser = (formValues) => {
  store.dispatch(registerUser(formValues))
  // get response and show errors, or move to next page
}

const RegisterUser = () => {

  return <>
    <Grid
        container
        spacing={0}
        alignItems="center"
        justify="center"
        style={{ minHeight: "70vh" }}
        >
      <Container  maxWidth="sm">
        <FormRenderer
            initialValues={{
              'full_name': 'set by "initialValues"'
            }}
            schema={schema}
            componentMapper={componentMapper}
            FormTemplate={FormTemplate}
            onSubmit={submitRegisterUser}
            validatorMapper={validatorMapper}
        />
      </Container>
    </Grid>
    </>;
}

export default RegisterUser;


