import React from 'react';
import XlxsUpload from './XlxsUpload';
import DataSheetView from './DataSheetView';
import '../../ag-grid.scss'


const ImportPage = () => {

    return (
        <div>
            <XlxsUpload/>
            <DataSheetView/>
        </div>
    );
}

export default ImportPage;