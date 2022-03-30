'use strict';

import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {Navigate, NavLink} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add} from '@mui/icons-material';

import 'ag-grid-enterprise';
import 'ag-grid-community/dist/styles/ag-theme-material.css';

const ObservableItemList = () => {
  const [rowData, setRowData] = useState([]);
  const [redirect, setRedirect] = useState(null);

  useEffect(() => {
    async function fetchObservableItems() {
      await getResult('reference/observableItems').then((res) => setRowData(res.data));
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
          <Button style={{width: '100%'}} to="/reference/observableItem" component={NavLink} variant={'contained'} startIcon={<Add></Add>}>
            New Observable Item
          </Button>
        </Box>
      </Box>
      <AgGridReact
        className="ag-theme-material"
        rowHeight={24}
        pagination={true}
        enableCellTextSelection={true}
        rowData={rowData}
        context={{useOverlay: 'Loading Surveys'}}
        components={{loadingOverlay: LoadingOverlay}}
        loadingOverlayComponent="loadingOverlay"
        suppressCellFocus={true}
        defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
      >
        <AgGridColumn
          width={40}
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
          width={70}
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
        <AgGridColumn width={100} field="typeName" headerName="Type" />
        <AgGridColumn flex={1} field="name" />
        <AgGridColumn flex={1} field="commonName" />
        <AgGridColumn width={100} field="supersededBy" />
        <AgGridColumn width={100} field="supersededNames" />
        <AgGridColumn width={100} field="supersededIDs" />
        <AgGridColumn width={100} field="phylum" />
        <AgGridColumn width={100} field="class" />
        <AgGridColumn width={100} field="order" />
        <AgGridColumn width={100} field="family" />
        <AgGridColumn width={100} field="genus" />
      </AgGridReact>
    </>
  );
};

export default ObservableItemList;
