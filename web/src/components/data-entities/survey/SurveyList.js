import {Box, Button, Typography} from '@mui/material';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useCallback, useRef, useState} from 'react';
import {Navigate, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import {AuthContext} from '../../../contexts/auth-context';

const SurveyList = () => {
  const rowsPerPage = 50;

  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();
  const [loading, setLoading] = React.useState(false);
  const [selected, setSelected] = useState();

  const onGridReady = useCallback(
    (event) => {
      async function fetchSurveys(e) {
        e.api.setDatasource({
          // This is the functional structure need for datasource
          rowCount: rowsPerPage,
          getRows: (params) => {
            let url = `data/surveys?page=${params.startRow / 100}`;
            let conditions = [];
            // Filter section
            for (let name in params.filterModel) {
              const p = params.filterModel[name];

              if (p.type) {
                // This is single condition
                conditions.push({
                  field: name,
                  ops: p.type,
                  val: p.filter
                });
              } else {
                // This is a multiple condition, currently max two conditions
                conditions.push({
                  field: name,
                  ops: p.operator,
                  conditions: [
                    {ops: p.condition1.type, val: p.condition1.filter},
                    {ops: p.condition2.type, val: p.condition2.filter}
                  ]
                });
              }
            }

            // Sorting section, order is important
            let sort = [];
            params.sortModel.forEach((i) => {
              sort.push({
                field: i.colId,
                order: i.sort
              });
            });

            url = conditions.length !== 0 ? url + `&filters=${encodeURIComponent(JSON.stringify(conditions))}` : url;
            url = sort.length !== 0 ? url + `&sort=${encodeURIComponent(JSON.stringify(sort))}` : url;

            setLoading(true);
            getResult(url)
              .then((res) => {
                params.successCallback(res.data.items, res.data.lastRow);
              })
              .finally(() => {
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
    },
    [location]
  );

  if (redirect) return <Navigate to={`/data/survey/${redirect}`} />;

  return (
    <AuthContext.Consumer>
      {({auth}) => (
        <>
          <Backdrop sx={{color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1}} open={loading}>
            <CircularProgress color="inherit" />
          </Backdrop>
          <Box display="flex" flexDirection="row" p={1} pb={1}>
            <Box flexGrow={1}>
              <Typography variant="h4">Surveys</Typography>
            </Box>
            <Box>
              <Button
                variant="outlined"
                onClick={() => setRedirect(`${selected.join(',')}/correct`)}
                disabled={!selected || selected.length < 1 || selected.length > 25}
              >
                Correct Survey Data
              </Button>
            </Box>
          </Box>
          <AgGridReact
            ref={gridRef}
            id={'survey-list'}
            className="ag-theme-material"
            rowHeight={24}
            rowSelection={'multiple'}
            animateRows={true}
            enableCellTextSelection={true}
            pagination={true}
            paginationPageSize={rowsPerPage}
            tooltipShowDelay={0}
            tooltipHideDelay={10000}
            rowModelType={'infinite'}
            row
            onGridReady={(e) => onGridReady(e)}
            onSelectionChanged={(e) => {
              setSelected(e.api.getSelectedRows().map((i) => i.surveyId));
            }}
            onFilterChanged={(e) => {
              stateFilterHandler.stateFilterEventHandler(gridRef, e);
            }}
            suppressCellFocus={true}
            defaultColDef={{lockVisible: true, sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
          >
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
                checkboxSelection={true}
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
              width={40}
              field="surveyId"
              headerName=""
              suppressMovable={true}
              filter={false}
              resizable={false}
              sortable={false}
              tooltipValueGetter={() => 'Edit Survey Metadata'}
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
            <AgGridColumn width={100} field="surveyDate" colId="survey.surveyDate" />
            <AgGridColumn width={100} field="latitude" colId="survey.latitude" />
            <AgGridColumn width={100} field="longitude" colId="survey.longitude" />
            <AgGridColumn width={100} headerName="Has PQs" field="pqCatalogued" colId="survey.pqCatalogued" />
            <AgGridColumn width={100} field="siteCode" colId="survey.siteCode" />
            <AgGridColumn flex={1} field="siteName" colId="survey.siteName" />
            <AgGridColumn width={100} field="depth" colId="survey.depth" />
            <AgGridColumn flex={1} field="diverName" colId="survey.diverName" />
            <AgGridColumn flex={1} field="method" colId="survey.method" />
            <AgGridColumn width={100} field="programName" headerName="Program" colId="survey.programName" />
            <AgGridColumn flex={1} field="country" colId="survey.country" />
            <AgGridColumn flex={1} field="state" colId="survey.state" />
            <AgGridColumn flex={1} field="ecoregion" colId="survey.ecoregion" />
            <AgGridColumn flex={1} field="locationName" colId="survey.locationName" />
            <AgGridColumn flex={1} field="species" colId="survey.species"
                          tooltipValueGetter={(param) => param.value} />
          </AgGridReact>
        </>
      )}
    </AuthContext.Consumer>
  );
};

export default SurveyList;
