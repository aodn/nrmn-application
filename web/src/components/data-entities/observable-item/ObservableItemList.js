'use strict';

import React, { useRef, useState } from 'react';
import {Box, Button, Typography} from '@mui/material';
import {Navigate, NavLink, useLocation} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add} from '@mui/icons-material';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

import 'ag-grid-enterprise';

const ObservableItemList = () => {
  const location = useLocation();
  const [redirect, setRedirect] = useState();
  const gridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt, skipHeader) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = (event) => {
    async function fetchObservableItems(event) {
      await getResult('reference/observableItems').then((res) => {
        // Use setRowData in api will not trigger onGridReady but onDataChange event.
        // if you use useState and connect row to setRowData then you will
        // keep fire onGridReady as row change
        event.api.setRowData(res.data);
      });
    }

    fetchObservableItems(event).then(() => {
      if(!(location?.state?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef);
      }
      autoSizeAll(event, false);
    });
  };

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
        ref={gridRef}
        className="ag-theme-material"
        id={'observable-item-list'}
        rowHeight={24}
        pagination={true}
        enableCellTextSelection={true}
        onGridReady={(e) => onGridReady(e)}
        onBodyScroll={(e) => autoSizeAll(e, false)}
        onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
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
        <AgGridColumn minWidth={200} field="name" headerName="Name"/>
        <AgGridColumn field="commonName" headerName="Common Name"/>
        <AgGridColumn field="supersededBy" headerName="Superseded By"/>
        <AgGridColumn field="supersededNames" headerName="Superseded Names"/>
        <AgGridColumn field="supersededIDs" headerName="Superseded IDs"/>
        <AgGridColumn field="phylum" headerName="Phylum"/>
        <AgGridColumn field="class" headerName="Class"/>
        <AgGridColumn field="order" headerName="Order"/>
        <AgGridColumn field="family" headerName="Family"/>
        <AgGridColumn field="genus" headerName="Genus"/>
      </AgGridReact>
    </>
  );
};

export default ObservableItemList;
