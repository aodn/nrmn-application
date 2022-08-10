import React, {useState, useRef, useCallback} from 'react';
import {Box, Typography} from '@mui/material';
import {Navigate, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {AuthContext} from '../../../contexts/auth-context';
import 'ag-grid-enterprise';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

const SurveyList = () => {
  const rowsPerPage = 50;

  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();
  const [loading, setLoading] = React.useState(false);

  const onGridReady = useCallback((event) => {
    async function fetchSurveys(e) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `data/surveys?page=${params.startRow / 100}`;
          let conditions = [];
          // Filter section
          for(let name in params.filterModel) {
            const p = params.filterModel[name];

            if(p.type) {
              // This is single condition
              conditions.push({
                field: name,
                ops: p.type,
                val: p.filter
              });
            }
            else {
              // This is a multiple condition, currently max two conditions
              conditions.push({
                field: name,
                ops: p.operator,
                conditions: [
                  { ops: p.condition1.type, val: p.condition1.filter },
                  { ops: p.condition2.type, val: p.condition2.filter }
                ]
              });
            }
          };

          // Sorting section, order is important
          let sort = [];
          params.sortModel.forEach(i => {
            sort.push({
              field: i.colId,
              order: i.sort
            });
          });

          url = conditions.length !== 0 ? url + `&filters=${encodeURIComponent(JSON.stringify(conditions))}` : url;
          url = sort.length !== 0 ? url + `&sort=${encodeURIComponent(JSON.stringify(sort))}` : url;

          getResult(url)
            .then(res => {
              params.successCallback(res.data.items, res.data.lastRow);
            })
            .finally(()=> {
              setLoading(false);
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
          <Backdrop
            sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}>
            <CircularProgress color="inherit" />
          </Backdrop>
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
            onFilterChanged={(e) => {
              setLoading(true);
              stateFilterHandler.stateFilterEventHandler(gridRef, e);
              }}
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
              colId="survey.surveyId"
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
            <AgGridColumn width={100} field="siteCode" colId="survey.siteCode" />
            <AgGridColumn width={100} field="surveyDate" colId="survey.surveyDate"/>
            <AgGridColumn width={100} field="depth" colId="survey.depth"/>
            <AgGridColumn flex={1} field="siteName" colId="survey.siteName"/>
            <AgGridColumn width={100} field="programName" headerName="Program" colId="survey.programName"/>
            <AgGridColumn flex={1} field="locationName" colId="survey.locationName"/>
            <AgGridColumn width={100} field="hasPQs" colId="survey.hasPQs"/>
            <AgGridColumn flex={1} field="mpa" colId="survey.mpa"/>
            <AgGridColumn flex={1} field="country" colId="survey.country"/>
            <AgGridColumn flex={1} field="diverName" colId="survey.diverName"/>
          </AgGridReact>
        </>
      )}
    </AuthContext.Consumer>
  );
};

export default SurveyList;
