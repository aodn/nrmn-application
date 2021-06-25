import React from 'react';
import {useSelector} from 'react-redux';
import {Redirect, useParams} from 'react-router';
import DataSheetView from './DataSheetView';
import ValidationDrawer from './ValidationDrawer';

const ValidationJob = () => {
  const {jobId} = useParams();
  const ingestSuccess = useSelector((state) => state.import.ingestSuccess);

  if (ingestSuccess) {
    return <Redirect to={`/jobs/${jobId}/view`}></Redirect>;
  }

  return (
    <>
      <DataSheetView jobId={jobId} />
      <ValidationDrawer></ValidationDrawer>
    </>
  );
};

export default ValidationJob;
