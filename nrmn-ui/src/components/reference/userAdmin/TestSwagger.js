import React from 'react';

import ReferenceForm from "../ReferenceForm";
import store from "../../store";
import {register} from "../../import/reducers/auth-reducer";
import config from "react-global-configuration";

import { useParams } from "react-router-dom";


function getPostSchema(path) {

  const schemaPaths = config.get('api').paths;
  return schemaPaths[path].post.requestBody.content['application/json'].schema
}


const TestSwagger = () => {

  let mapping = Object.values(useParams()).filter(Boolean).join('/');
  mapping = (mapping !== "") ? mapping : "api/user";
  let postJson = getPostSchema(`/${mapping}`);

  const submitAction = (formValues) => {
    console.log(formValues);
    //store.dispatch(register(formValues))
  }

  const uiSchema = {
    "ui:options": {
      label: false
    },
    "name": {
        "ui:autofocus": true,
        "ui:title": "Your password",
        "ui:description": "Short Username to store in the DB",
        "ui:emptyValue": ""
    },
      "lastName": {
          "ui:emptyValue": "",
          "ui:autocomplete": "given-name"
    },
      "age": {
      "ui:widget": "updown",
          "ui:title": "Age of person",
          "ui:description": "(earthian year)"
    },
      "bio": {
      "ui:widget": "textarea"
    },
      "password": {
      "ui:widget": "password",
          "ui:help": "Hint: Make it strong!"
    },
      "date": {
      "ui:widget": "alt-datetime"
    },
      "telephone": {
      "ui:options": {
        "inputType": "tel"
      }
    }

  }


  return <>
    <ReferenceForm
        schema={postJson}
        uiSchema={uiSchema}
        submitAction={submitAction}
    />
      </>;
}

export default TestSwagger;
