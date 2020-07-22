import React from 'react';
import { DropzoneAreaBase } from 'material-ui-dropzone';
import XLSX from 'xlsx';
import store from "../store";
import { loadXlxs } from './redux-import';

const handleMainMenu = () => {
}

const onAddFile = (fileObjs) => {
    console.log('Added Files:', fileObjs)
    const reader = new FileReader();
    reader.onload = (event) => {
        const workbook = XLSX.read(event.target.result, { type: 'binary' });
        const dataSheet = XLSX.utils.sheet_to_json(workbook.Sheets["data"], { header: 1 });
        store.dispatch(loadXlxs(dataSheet))
    };
    reader.readAsBinaryString(fileObjs[0].file);

}

const XlxsUpload = () => {
    return (
        <div style={{ marginTop: 50 }}>
            <DropzoneAreaBase
                acceptedFiles={
                    ["text/csv,application/vnd.ms-excel,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"]
                }

                onAdd={onAddFile}
                onDelete={(fileObj) => console.log('Removed File:', fileObj)}
                onAlert={(message, variant) => console.log(`${variant}: ${message}`)}
            />
        </div>
    );
};

export default XlxsUpload;
