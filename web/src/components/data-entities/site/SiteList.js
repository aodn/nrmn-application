import React, {useEffect, useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography} from '@mui/material';
import {Navigate, NavLink} from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, CopyAll, Delete, Edit} from '@mui/icons-material';
import {entityDelete} from '../../../api/api';

import 'ag-grid-enterprise';

const SiteList = () => {
  const [gridApi, setGridApi] = useState();
  const [redirect, setRedirect] = useState();
  const [dialogState, setDialogState] = useState({open: false});

  useEffect(() => {
    async function fetchSites() {
      await getResult('sites').then((res) => gridApi.setRowData(res.data));
    }
    if (gridApi) fetchSites();
  }, [gridApi]);

  if (redirect) return <Navigate to={`/reference/site/${redirect}`} />;

  const dialog = (
    <Dialog disableBackdropClick disableEscapeKeyDown maxWidth="xs" open>
      <DialogTitle>Delete Site?</DialogTitle>
      <DialogContent>
        Are you sure you want to permanently delete this site?
        <Box p={2}>
          <Typography variant="subtitle2">{dialogState.description}</Typography>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" autoFocus onClick={() => setDialogState({open: false})}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={() => {
            entityDelete('site', dialogState.item.siteId).then(() => {
              gridApi.applyTransaction({remove: [dialogState.item]});
              setDialogState({open: false});
            });
          }}
        >
          Delete Site
        </Button>
      </DialogActions>
    </Dialog>
  );

  return (
    <>
      {dialogState.open && dialog}
      <Box display="flex" flexDirection="row" pt={1}>
        <Box p={1} pl={2} flexGrow={1}>
          <Typography variant="h4">Sites</Typography>
        </Box>
        <Box mr={4}>
          <Button style={{width: '100%'}} to="/reference/site" component={NavLink} variant={'contained'} startIcon={<Add></Add>}>
            New Site
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={24}
          pagination={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Sites'}}
          components={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellFocus={true}
          defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
        >
          <AgGridColumn
            width={40}
            field="siteId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={() => <Edit/> }
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/site/${e.data.siteId}/edit`, '_blank').focus();
              } else {
                setRedirect(`${e.data.siteId}/edit`);
              }
            }}
          />
          <AgGridColumn
            width={100}
            field="siteCode"
            cellStyle={{cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/site/${e.data.siteId}`, '_blank').focus();
              } else {
                setRedirect(`${e.data.siteId}`);
              }
            }}
          />
          <AgGridColumn flex={1} field="siteName" />
          <AgGridColumn flex={1} field="locationName" />
          <AgGridColumn flex={1} field="state" />
          <AgGridColumn flex={1} field="country" />
          <AgGridColumn width={100} field="latitude" />
          <AgGridColumn width={100} field="longitude" />
          <AgGridColumn width={50} suppressMenu={true} field="isActive" headerName="Active" />
          <AgGridColumn
            width={40}
            field="siteId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={() => <CopyAll/>}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/site/${e.data.siteId}/clone`, '_blank').focus();
              } else {
                setRedirect(`${e.data.siteId}/clone`);
              }
            }}
          />
          <AgGridColumn
            width={60}
            field="observableItemId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={() => <Delete/>}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              setDialogState({
                open: true,
                item: e.data
              });
            }}
          />
        </AgGridReact>
      </Box>
    </>
  );
};

export default SiteList;
