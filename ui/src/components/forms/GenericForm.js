import React from "react";

import { withTheme } from '@rjsf/core';
import { Theme as MaterialUITheme } from '@rjsf/material-ui';
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from 'react';
import { definitionRequested,resetState, idRequested, createEntityRequested } from "./redux-form";
import { useParams, Redirect } from "react-router-dom";
import ArrayApiField from './customWidget/ArrayApiField';
import pluralize from 'pluralize';


const Form = withTheme(MaterialUITheme);

const UserForm = () => {
    const { entities, id } = useParams();
    const definition = useSelector(state => state.form.definition)
    const editItem = useSelector(state => state.form.editItem)
    const createdEntity = useSelector(state => state.form.createdEntity)


    const dispatch = useDispatch();
    const singular = pluralize.singular(entities)
    const entity =  singular.charAt(0).toUpperCase() + singular.slice(1)
    useEffect(() => {
     //   dispatch(resetState());
        if (Object.keys(definition).length === 0)
            dispatch(definitionRequested());

        if (id !== undefined) {
            dispatch(idRequested(entities + "/" + id));

        }


    }, []);
    if (Object.keys(createdEntity).length !== 0) {
        const redirectPath = "/collection/" + entities;
        console.log('redirected:', redirectPath);
        return (<Redirect to={redirectPath}></Redirect>)

    }



    if (Object.keys(definition).length === 0 && typeof (definition[entity]) == 'undefined')
        return (<></>);


    const fields = { ArrayField: ArrayApiField}

    const handleSubmit = (form) => {
        console.info("submited:", form.formData)
        dispatch(createEntityRequested( {path:entities, data: form.formData}));
    }

    const { title, ...entityDef } = definition[entity]
    const editSchema = (id) ? { title: title.replace("Add", "Edit"), ...entityDef } : definition[entity]
    const JSSchema = { components: { schemas: definition }, ...editSchema };
    return (<Form
        schema={JSSchema}
        onSubmit={handleSubmit}
        fields={fields}
        formData={editItem}
    >
    </Form >)

}

export default UserForm;