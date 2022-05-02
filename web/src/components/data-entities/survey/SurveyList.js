import React, {useEffect, useState} from 'react';
import {Box, Typography} from '@mui/material';
import {Navigate} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import 'ag-grid-enterprise';

const SurveyList = () => {
  const [rowData, setRowData] = useState();
  const [redirect, setRedirect] = useState();

  useEffect(() => {
    async function fetchSurveys() {
      await getResult('data/surveys').then((res) => setRowData(res.data));
    }
    fetchSurveys();
  }, []);

  if (redirect) return <Navigate to={`/data/survey/${redirect}`} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Surveys</Typography>
        </Box>
      </Box>
      <AgGridReact
        className="ag-theme-material"
        rowHeight={24}
        animateRows={true}
        enableCellTextSelection={true}
        pagination={true}
        rowData={rowData}
        context={{useOverlay: 'Loading Surveys'}}
        components={{loadingOverlay: LoadingOverlay}}
        loadingOverlayComponent="loadingOverlay"
        suppressCellFocus={true}
        defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
      >
        <AgGridColumn
          width={40}
          field="surveyId"
          headerName=""
          suppressMovable={true}
          filter={false}
          resizable={false}
          sortable={false}
          valueFormatter={() => '✎'}
          cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
          onCellClicked={(e) => {
            if (e.event.ctrlKey) {
              window.open(`/data/survey/${e.data.surveyId}/edit`, '_blank').focus();
            } else {
              setRedirect(`${e.data.surveyId}/edit`);
            }
          }}
        />
        <AgGridColumn
          width={110}
          field="surveyId"
          headerName="Survey ID"
          sort="desc"
          cellStyle={{cursor: 'pointer'}}
          onCellClicked={(e) => {
            if (e.event.ctrlKey) {
              window.open(`/data/survey/${e.data.surveyId}`, '_blank').focus();
            } else {
              setRedirect(e.data.surveyId);
            }
          }}
        />
        <AgGridColumn width={100} field="siteCode" />
        <AgGridColumn width={100} field="surveyDate" />
        <AgGridColumn width={100} field="depth" />
        <AgGridColumn flex={1} field="siteName" />
        <AgGridColumn width={100} field="programName" headerName="Program" />
        <AgGridColumn flex={1} field="locationName" />
        <AgGridColumn width={100} field="hasPQs" />
        <AgGridColumn flex={1} field="mpa" />
        <AgGridColumn flex={1} field="country" />
        <AgGridColumn flex={1} field="diverName" />
      </AgGridReact>
    </>
  );
};

export default SurveyList;
