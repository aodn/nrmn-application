import React, { useEffect, useState } from 'react';
import { ImportRequested, ImportReset } from './reducers/upload';
import { useDispatch, useSelector, } from 'react-redux';
import BaseForm from '../BaseForm';
import { Box, useMediaQuery, useTheme } from '@material-ui/core';
import LinearProgress from '@material-ui/core/LinearProgress';
import { useHistory } from 'react-router';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

const XlxsUpload = () => {
    const schema = {
        'title': 'Add Excel Document',
        'type': 'object',
        'required': [
            'programId',
            'file'
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
    const isLoading = useSelector(state => state.upload.isLoading);
    const success = useSelector(state => state.upload.success);
    const percentCompleted = useSelector(state => state.upload.percentCompleted);
    const jobId = useSelector(state => state.upload.jobId);
    const errors = useSelector(state => state.upload.errors);
    const formData = useSelector(state => state.upload.formData);

    const history = useHistory();

    const [open, setOpen] = useState(false);
    const theme = useTheme();
    const fullScreen = useMediaQuery(theme.breakpoints.down('sm'));

    const handleSubmit = (form) => {
        const data = {
            file: form.formData.file,
            programId: form.formData.programId,
            withInvertSize: form.formData.withInvertSize || false
        };
        dispatch(ImportRequested(data));
    };


    useEffect(() => {
        if (jobId != '') {
            setOpen(true);
        }
    });

    const goToEdit = () => {
        history.push('/validation/' + jobId);
        dispatch(ImportReset());
    };

    const handleResetAndClose = () => {
        setOpen(false);
        dispatch(ImportReset());

    };

    var displayErros = [];
    if (errors && errors.length > 0) {
        displayErros = errors.map(e => e.message);
    }
    return (
        <Box>
            {isLoading && percentCompleted >= 0 && <LinearProgress variant='determinate' value={percentCompleted} />}
            <BaseForm
                schema={schema}
                uiSchema={uiSchema}
                loading={isLoading}
                success={success}
                errors={displayErros}
                formData={formData}
                onSubmit={handleSubmit}>
            </BaseForm>

            <Dialog
                fullScreen={fullScreen}
                open={open}
                onClose={()  => setOpen(false)}
                aria-labelledby="Confirmation-upload"
            >
                <DialogTitle color="primary" id="Confirmation-upload">{'Success!'}</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        The file has been staged.
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={handleResetAndClose} color="secondary">
                        Add another file
                    </Button>
                    <Button color="secondary" onClick={goToEdit}  autoFocus>
                        View File
                     </Button>
                </DialogActions>
            </Dialog>
        </Box>);
};

export default XlxsUpload;
