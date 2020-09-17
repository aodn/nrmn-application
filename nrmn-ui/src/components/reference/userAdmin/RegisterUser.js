import React from 'react';

import ReferenceForm from "../ReferenceForm";
import store from "../../store";
import {register} from "../../import/reducers/auth-reducer";
import config from "react-global-configuration";


function getPostSchema(path) {
  const schemaPaths = config.get('api').paths;
  return schemaPaths[path].post.requestBody.content['application/json'].schema
}


const RegisterUser = () => {

  let postJson = getPostSchema('/api/user');

  const submitAction = (formValues) => {
    console.log(formValues);
    store.dispatch(register(formValues))
  }

  return <>
    <ReferenceForm
        schema={postJson}
        submitAction={submitAction}
    />
      </>;
}

export default RegisterUser;
