import React from 'react';
import XlxsUpload from './XlxsUpload';
import DataSheetView from './DataSheetView';
import { useParams } from 'react-router-dom';


const ImportPage = () => {

    return (
        <div>
            <XlxsUpload></XlxsUpload>
            <DataSheetView></DataSheetView>
        </div>
    );
}

export default ImportPage;