import React from 'react';
import { ImportRequested, ImportStarted } from './reducers/create-import';
import { useDispatch, useSelector } from 'react-redux';
import BaseForm from '../BaseForm';
import { Box } from '@material-ui/core';
import LinearProgress from '@material-ui/core/LinearProgress';
import { Redirect } from 'react-router';


const XlxsUpload = () => {
    const schema = {
        'title': 'Add Excel Document',
        'type': 'object',
        'required': [
            'programId'
        ],
        'properties': {
            'file': {
                'title': 'Upload',
                'type': 'string',
                'format': 'data-url'
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
            'ui:widget': 'file'
        },
        'program': {
            'ui:options': {
                addable: false
            }
        }
    };

    const dispatch = useDispatch();
    const isLoading = useSelector(state => state.import.isLoading);
    const success = useSelector(state => state.import.success);
    const percentCompleted = useSelector(state => state.import.percentCompleted);
    const jobId = useSelector(state => state.import.jobId);
    const errors = useSelector(state => state.import.errors);

    const handleSubmit = (form) => {
        dispatch(ImportStarted());
        const data = {
            file: form.formData.file,
            programId: form.formData.programId,
            withInvertSize: form.formData.withInvertSize || false
        };
        dispatch(ImportRequested(data));

    };

    if (jobId !== '') {
        return (<Redirect component='link' to={'/validation/' + jobId} ></Redirect>);
    }
    return (
        <Box>
            {isLoading && percentCompleted >= 0 && <LinearProgress variant='determinate' value={percentCompleted} />}
            <BaseForm
                schema={schema}
                uiSchema={uiSchema}
                loading={isLoading}
                success={success}
                errors={errors.map(e => e.message)}
                onSubmit={handleSubmit}>

            </BaseForm>
        </Box>);
};

export default XlxsUpload;
