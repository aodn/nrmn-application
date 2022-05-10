'use strict';

import React, {useCallback, useRef, useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {Navigate, NavLink} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add} from '@mui/icons-material';

import 'ag-grid-enterprise';

const ObservableItemList = () => {
  const [rowData, setRowData] = useState();
  const [redirect, setRedirect] = useState();
  const oGridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (skipHeader) => {
    if(oGridRef.current != null) {
        oGridRef.current.columnApi.autoSizeAllColumns(skipHeader);
    }};

  const onGridReady = useCallback(() => {
    async function fetchObservableItems() {
      await getResult('reference/observableItems').then(
          (res) => {
              setRowData(res.data);
              autoSizeAll(false);
          }
      );
    }
    fetchObservableItems();
  }, []);

  if (redirect) return <Navigate to={`/reference/observableItem/${redirect}`} />;

  return (
    <>
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
        ref={oGridRef}
        className="ag-theme-material"
        rowHeight={24}
        pagination={true}
        enableCellTextSelection={true}
        onGridReady={onGridReady()}
        rowData={rowData}
        context={{useOverlay: 'Loading Observable Items'}}
        components={{loadingOverlay: LoadingOverlay}}
        loadingOverlayComponent="loadingOverlay"
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
          cellStyle={{cursor: 'pointer'}}
          onCellClicked={(e) => {
            if (e.event.ctrlKey) {
              window.open(`/reference/observableItem/${e.data.observableItemId}`, '_blank').focus();
            } else {
              setRedirect(e.data.observableItemId);
            }
          }}
        />
        <AgGridColumn field="typeName" headerName="Type" />
        <AgGridColumn field="name" />
        <AgGridColumn field="commonName" />
        <AgGridColumn field="supersededBy" />
        <AgGridColumn field="supersededNames" />
        <AgGridColumn field="supersededIDs" />
        <AgGridColumn field="phylum" />
        <AgGridColumn field="class" />
        <AgGridColumn field="order" />
        <AgGridColumn field="family" />
        <AgGridColumn field="genus" />
      </AgGridReact>
    </>
  );
};

export default ObservableItemList;
