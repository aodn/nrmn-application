import React from "react";

import { withTheme } from '@rjsf/core';
import { Theme as MaterialUITheme } from '@rjsf/material-ui';
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from 'react';
import { definitionRequested, idRequested } from "./redux-form";
import { useParams } from "react-router-dom";
import ArrayApiField from './customWidget/ArrayApiField';


const Form = withTheme(MaterialUITheme);

const UserForm = () => {
    const { entity, id } = useParams();
    const definition = useSelector(state => state.form.definition)
    const editItem = useSelector(state => state.form.editItem)

    const dispatch = useDispatch();

    useEffect(() => {
        if (Object.keys(definition).length === 0)
            dispatch(definitionRequested());

        if (id !== undefined)
            dispatch(idRequested(entity + "/" + id));


    }, []);

    if (Object.keys(definition).length === 0 && typeof (definition[entity]) == 'undefined')
        return (<></>);


    const fields = { ArrayField: ArrayApiField}

    const handleSubmit = (form) => {
        console.info("submited:", form.formData)
    }

    const { title, ...entityDef } = definition[entity]
    const editSchema = (editItem) ? { title: title.replace("Add", "Edit"), ...entityDef } : definition[entity]
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