import React, {useEffect, useState} from 'react';
import {Box, Typography} from '@material-ui/core';
import {Redirect} from 'react-router-dom';
import {getResult} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {resetState} from '../form-reducer';
import {useDispatch} from 'react-redux';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const SurveyList = () => {
  const dispatch = useDispatch();
  const [gridApi, setGridApi] = useState(null);
  const [redirect, setRedirect] = useState(null);

  useEffect(() => {
    if (gridApi) {
      dispatch(resetState());
      getResult('data/surveys').then((res) => gridApi.setRowData(res.data));
    }
  }, [dispatch, gridApi]);

  if (redirect) return <Redirect to={`/data/survey/${redirect}`} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Surveys</Typography>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={25}
          animateRows={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Surveys'}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellSelection={true}
          defaultColDef={{sortable: true, resizable: true, filter: 'text', floatingFilter: true}}
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
            onCellClicked={(e) => setRedirect(`${e.data.surveyId}/edit`)}
          />
          <AgGridColumn
            width={110}
            field="surveyId"
            headerName="Survey ID"
            sort="desc"
            cellStyle={{cursor: 'pointer'}}
            onCellClicked={(e) => setRedirect(e.data.surveyId)}
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
      </Box>
    </>
  );
};

export default SurveyList;
