import React from "react";

import { withTheme } from '@rjsf/core';
import { Theme as MaterialUITheme } from '@rjsf/material-ui';
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from 'react';
import { resetState, idRequested, createEntityRequested } from "./form-reducer";
import { useParams, Redirect } from "react-router-dom";
import ArrayApiField from './customWidget/ArrayApiField';
import pluralize from 'pluralize';
import config from "react-global-configuration";


const Form = withTheme(MaterialUITheme);

const GenericForm = () => {
    const { entities, id } = useParams();
    const schemaDefinition = config.get('api');
    const editItem = useSelector(state => state.form.editItem)
    const createdEntity = useSelector(state => state.form.createdEntity)
    
    const dispatch = useDispatch();
    const singular = pluralize.singular(entities)
    const entity =  singular.charAt(0).toUpperCase() + singular.slice(1)

    useEffect(() => {
        //   dispatch(resetState());
        if (id !== undefined) {
            dispatch(idRequested(entities + "/" + id));
        }
    }, []);
    if (Object.keys(createdEntity).length !== 0) {
        const redirectPath = "/collection/" + entities;
        console.log('redirected:', redirectPath);
        return (<Redirect to={redirectPath}></Redirect>)

    }

    if (Object.keys(schemaDefinition).length === 0 && typeof (schemaDefinition[entity]) == 'undefined')
        return (<></>);


    const fields = { ArrayField: ArrayApiField}

    const handleSubmit = (form) => {
        console.info("submited:", form.formData)
        dispatch(createEntityRequested( {path:entities, data: form.formData}));
    }

    const { title, ...entityDef } = schemaDefinition[entity]
    const editSchema = (id) ? { title: title.replace("Add", "Edit"), ...entityDef } : schemaDefinition[entity]
    const JSSchema = { components: { schemas: schemaDefinition }, ...editSchema };
    return (<Form
        schema={JSSchema}
        onSubmit={handleSubmit}
        fields={fields}
        formData={editItem}
    >
    </Form >)

}

export default GenericForm;