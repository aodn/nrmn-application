import React, {useState, useRef, useCallback} from 'react';
import {Box, Typography} from '@mui/material';
import {Navigate, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {AuthContext} from '../../../contexts/auth-context';
import 'ag-grid-enterprise';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

const SurveyList = () => {
  const rowsPerPage = 50;

  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();

  const onGridReady = useCallback((event) => {
    async function fetchSurveys(e) {
      e.api.setDatasource({
        rowCount: rowsPerPage,
        getRows: (params) => {
          getResult(`data/surveys?page=${params.startRow / 100}`)
            .then(res => {
              console.log(params);
              params.successCallback(res.data.items, res.data.lastRow);
            });
        }
      });
    }

    fetchSurveys(event).then(() => {
      if (!location?.state?.resetFilters) {
        stateFilterHandler.restoreStateFilters(gridRef);
      } else {
        stateFilterHandler.resetStateFilters(gridRef);
      }
    });
  }, []);

  if (redirect) return <Navigate to={`/data/survey/${redirect}`} />;

  return (
    <AuthContext.Consumer>
      {({auth}) => (
        <>
          <Box display="flex" flexDirection="row" p={1} pb={1}>
            <Box flexGrow={1}>
              <Typography variant="h4">Surveys</Typography>
            </Box>
          </Box>
          <AgGridReact
            ref={gridRef}
            id={'survey-list'}
            className="ag-theme-material"
            rowHeight={24}
            animateRows={true}
            enableCellTextSelection={true}
            pagination={true}
            paginationPageSize={rowsPerPage}
            rowModelType={'infinite'}
            onGridReady={(e) => onGridReady(e)}
            onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
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
              tooltipValueGetter={() => 'Edit Survey'}
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
            {auth.features?.includes('corrections') && (
              <AgGridColumn
                width={40}
                field="surveyId"
                headerName=""
                suppressMovable={true}
                filter={false}
                tooltipValueGetter={() => 'Correct Survey'}
                resizable={false}
                sortable={false}
                valueFormatter={() => '🛠'}
                cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
                onCellClicked={(e) => {
                  if (e.event.ctrlKey) {
                    window.open(`/data/survey/${e.data.surveyId}/correct`, '_blank').focus();
                  } else {
                    setRedirect(`${e.data.surveyId}/correct`);
                  }
                }}
              />
            )}
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
      )}
    </AuthContext.Consumer>
  );
};

export default SurveyList;
