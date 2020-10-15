import React from "react";
import {connect} from "react-redux";
import store from '../store';
import {login} from '../import/reducers/auth-reducer'
import BaseForm from "../BaseForm";
import * as axios from "../../axios/api";


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
      "title": "Email/Username",
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


class Login extends React.Component {

  handleLogin = (form) => {
    axios.userLogin(form.formData).then(response =>
      store.dispatch(login(response))
    ).catch(error => {
      if (error.response) {
        store.dispatch(login(error.response));
      }
    });
  }

  render(){
    const { errors } = this.props;

    return (
        <BaseForm
          schema={schema}
          uiSchema={uiSchema}
          errors={errors}
          onSubmit={this.handleLogin}>
        </BaseForm>
    )
  }
}

function mapStateToProps(state) {
  return {
    errors: state.auth.errors
  };
}

export default connect(mapStateToProps)(Login);
