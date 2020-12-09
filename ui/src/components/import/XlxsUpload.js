import React from 'react';
import { ImportRequested, ImportStarted } from './reducers/create-import';
import { useDispatch, useSelector } from 'react-redux';
import BaseForm from '../BaseForm';


const XlxsUpload = () => {


    const schema = {
        'title': 'Add Excel Document',
        'type': 'object',
        'required': [
            'file',
            'programId'
        ],
        'properties': {
            'file': {
                'type': 'string',
                'title': 'Excel File',
                format: 'data-url'

            },
            'programId': {
                title: 'Program File',
                type: 'number',
                enum: [1, 2],
                enumNames: ['RLS', 'ATRC']

            },
            'withInvertSize': {
                'title': 'Extended size?',
                'type': 'boolean'
            }
        }
    };


    const uiSchema = {
        'file': {
            'ui: widget': 'file'
        },
        'program': {
            'ui:widget': 'file',
            'ui:options': {
                addable: false
            }
        }
    };

    const dispatch = useDispatch();
    const isLoading = useSelector(state => state.import.isLoading);
    const success = useSelector(state => state.import.success);

    const handleSubmit = (form) => {
        console.log(form.formData.file);
        dispatch(ImportStarted());
        const data = {
            file: form.formData.file,
            programId: form.formData.programId,
            withInvertSize: form.formData.withInvertSize || false
        };
        console.log(data);
        dispatch(ImportRequested(data));

    };

    return (
        <BaseForm
            schema={schema}
            uiSchema={uiSchema}
            //      errors={errors}
            loading={isLoading}
            success={success}
            onSubmit={handleSubmit}>
        </BaseForm>);
};

export default XlxsUpload;
