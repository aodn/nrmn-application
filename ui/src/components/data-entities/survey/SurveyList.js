import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@material-ui/core';
import {Redirect} from 'react-router-dom';
import {getResult} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const saveFilterModel = (entityName, filterModel) => {
  window[`AgGrid-FilterModel-${entityName}`] = JSON.stringify(filterModel);
};

const restoreFilterModel = (entityName) => {
  const serialisedFilter = window[`AgGrid-FilterModel-${entityName}`];
  return serialisedFilter ? JSON.parse(serialisedFilter) : null;
};

const SurveyList = () => {
  const [gridApi, setGridApi] = useState(null);
  const [redirect, setRedirect] = useState(null);
  const [disableResetFilter, setResetFilterDisabled] = useState(true);

  useEffect(() => {
    if (gridApi) {
      getResult('data/surveys').then((res) => gridApi.setRowData(res.data));
    }
  }, [gridApi]);

  if (redirect) return <Redirect push to={`/data/survey/${redirect}`} />;

  const onFirstDataRendered = (e) => setTimeout(() => e.api.setFilterModel(restoreFilterModel('survey')), 25);

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Surveys</Typography>
        </Box>
        <Box mr={4}>
          <Button
            style={{width: '100%'}}
            disabled={disableResetFilter}
            onClick={() => gridApi.setFilterModel(null)}
            color="primary"
            variant={'contained'}
          >
            Reset Filter
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={24}
          animateRows={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Surveys'}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellSelection={true}
          onFirstDataRendered={onFirstDataRendered}
          onFilterChanged={(e) => {
            const filterModel = e.api.getFilterModel();
            saveFilterModel('survey', filterModel);
            setResetFilterDisabled(Object.keys(filterModel)?.length < 1);
          }}
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
      </Box>
    </>
  );
};

export default SurveyList;
