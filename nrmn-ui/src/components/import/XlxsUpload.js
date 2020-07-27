import React from 'react';
import { DropzoneDialog } from 'material-ui-dropzone';
import XLSX from 'xlsx';
import store from "../store";
import { loadXlxs } from './redux-import';
import { Fab } from '@material-ui/core';
import AddIcon from '@material-ui/icons/Add'
import Tooltip from '@material-ui/core/Tooltip';

const XlxsUpload = () => {
    const [openPopup, setOpenPopup] = React.useState(false);

    const handleClose = () => {
        setOpenPopup(false)
    }

    const handleOpen = () => {
        setOpenPopup(true)
    }

    const onAddFile = (fileObjs) => {
        setOpenPopup(false)
        const reader = new FileReader();
        reader.onload = (event) => {
            const workbook = XLSX.read(event.target.result, { type: 'binary' });
            console.log(workbook)
            const dataSheet = XLSX.utils.sheet_to_json(workbook.Sheets["data"], { header: 1 });
            store.dispatch(loadXlxs(dataSheet))
        };
        reader.readAsBinaryString(fileObjs[0]);

    }

    return (
        <div style={{ marginTop: 50 }}>
            <Tooltip title="Import Excel Data" aria-label="Import Excel Data">
             <Fab size="small" color="primary" aria-label='Import Excel Data' onClick={handleOpen}><AddIcon></AddIcon></Fab>
             </Tooltip>
             <DropzoneDialog
                open={openPopup}
                onSave={onAddFile}
                showPreviews={true}
                acceptedFiles={
                    ["text/csv,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
                }
                maxFileSize={5000000}
                onClose={handleClose}
            />
        </div>
    );
};

export default XlxsUpload;
