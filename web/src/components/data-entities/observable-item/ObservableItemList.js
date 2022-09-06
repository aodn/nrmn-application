'use strict';

import React, { useRef, useState, useCallback } from 'react';
import {Box, Button, Typography} from '@mui/material';
import {Navigate, NavLink, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add} from '@mui/icons-material';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';

import 'ag-grid-enterprise';

const ObservableItemList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

  const location = useLocation();
  const [redirect, setRedirect] = useState();
  const gridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt, skipHeader) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = useCallback((event) => {
    async function fetchObservableItems(e) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `reference/observableItems?page=${params.startRow / 100}`;
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

          setLoading(true);
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

    fetchObservableItems(event).then(() => {
      if(!(location?.state?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef);
      }
      else {
        stateFilterHandler.resetStateFilters(gridRef);
      }

      autoSizeAll(event, false);
    });
  },[location]);

  if (redirect) return <Navigate to={`/reference/observableItem/${redirect}`} />;

  return (
    <>
      <Backdrop
        sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
        open={loading}>
        <CircularProgress color="inherit" />
      </Backdrop>
      <Box display="flex" flexDirection="row" pt={1}>
        <Box p={1} pl={2} flexGrow={1}>
          <Typography variant="h4">Observable Items</Typography>
        </Box>
        <Box mr={4}>
          <Button style={{width: '100%'}} to="/reference/observableItem" component={NavLink} variant="contained" startIcon={<Add></Add>}>
            New Observable Item
          </Button>
        </Box>
      </Box>
      <AgGridReact
        ref={gridRef}
        className="ag-theme-material"
        id={'observable-item-list'}
        rowHeight={24}
        pagination={true}
        paginationPageSize={rowsPerPage}
        rowModelType={'infinite'}
        enableCellTextSelection={true}
        onGridReady={(e) => onGridReady(e)}
        onBodyScroll={(e) => autoSizeAll(e, false)}
        onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
        suppressCellFocus={true}
        defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
      >
        <AgGridColumn
          field="observableItemId"
          headerName=""
          suppressMovable={true}
          filter={false}
          resizable={false}
          sortable={false}
          valueFormatter={() => '✎'}
          cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
          onCellClicked={(e) => {
            if (e.event.ctrlKey) {
              window.open(`/reference/observableItem/${e.data.observableItemId}/edit`, '_blank').focus();
            } else {
              setRedirect(`${e.data.observableItemId}/edit`);
            }
          }}
        />
        <AgGridColumn
          field="observableItemId"
          headerName="ID"
          sort="desc"
          colId="observation.observableItemId"
          cellStyle={{cursor: 'pointer'}}
          onCellClicked={(e) => {
            if (e.event.ctrlKey) {
              window.open(`/reference/observableItem/${e.data.observableItemId}`, '_blank').focus();
            } else {
              setRedirect(e.data.observableItemId);
            }
          }}
        />
        <AgGridColumn field="typeName" headerName="Type" colId="observation.typeName"/>
        <AgGridColumn minWidth={200} field="name" colId="observation.name"/>
        <AgGridColumn field="commonName" colId="observation.commonName"/>
        <AgGridColumn field="supersededBy" colId="observation.supersededBy"/>
        <AgGridColumn field="supersededNames" colId="observation.supersededNames"/>
        <AgGridColumn field="supersededIDs" colId="observation.supersededIds"/>
        <AgGridColumn field="phylum" colId="observation.phylum"/>
        <AgGridColumn field="class" colId="observation.class"/>
        <AgGridColumn field="order" colId="observation.order"/>
        <AgGridColumn field="family" colId="observation.family"/>
        <AgGridColumn field="genus" colId="observation.genus"/>
      </AgGridReact>
    </>
  );
};

export default ObservableItemList;
