import React from "react";

import { withTheme } from '@rjsf/core';
import { Theme as MaterialUITheme } from '@rjsf/material-ui';
import {  useDispatch, useSelector } from "react-redux";
import {  useEffect } from 'react';
import { definitionRequested } from "./redux-form";
import { useParams } from "react-router-dom";
 import SelectWidget from './customWidget/SelectWidget';


const Form = withTheme(MaterialUITheme);
const UserForm = () => {
    const {entity} = useParams();
    const definition = useSelector(state => state.form.definition)
    const dispatch = useDispatch();

    useEffect(() => {
            if (Object.keys(definition).length === 0) {
                dispatch(definitionRequested());
            }
        },[]);

        console.log(definition)
    if (Object.keys(definition).length === 0 && typeof(definition[entity]) == 'undefined' )
        return (<></>)


    const uiSchema = {
        roles : {
            "ui:widget": SelectWidget,
            "ui:options": {
                id: "roles",
                entity: "secRoleEntities"
            }
        }
};
    console.log(uiSchema)
    console.log(definition[entity])
    return (<Form 
    schema={definition[entity]} 
    uiSchema={uiSchema}
    >
    </Form>)

}

export default UserForm;