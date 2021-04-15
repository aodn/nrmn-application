import React from 'react';
import XlxsUpload from './XlxsUpload';
import DataSheetView from './DataSheetView';

const ImportPage = () => {
  return (
    <div>
      <XlxsUpload></XlxsUpload>
      <DataSheetView></DataSheetView>
    </div>
  );
};

export default ImportPage;
