import React, {useRef, useState} from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography} from '@mui/material';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import {getResult} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, CopyAll, Delete, Edit} from '@mui/icons-material';
import {entityDelete} from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

import 'ag-grid-enterprise';

const SiteList = () => {
  const location = useLocation();
  const [redirect, setRedirect] = useState();
  const [dialogState, setDialogState] = useState({open: false});
  const gridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt, skipHeader) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = (event) => {
    async function fetchSites(event) {
      await getResult('sites').then((res) => {
        // Use setRowData in api will not trigger onGridReady but onDataChange event.
        // if you use useState and connect row to setRowData then you will
        // keep fire onGridReady as row change
        event.api.setRowData(res.data);
      });
    }

    fetchSites(event).then(() => {
      if(!(location?.state?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef);
      }
      else {
        stateFilterHandler.resetStateFilters(gridRef);
      }

      // autoSizeAll(event, false);
    });
  };

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
              gridRef.current.api.applyTransaction({remove: [dialogState.item]});
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
          ref={gridRef}
          id={'site-list'}
          rowHeight={24}
          pagination={true}
          enableCellTextSelection={true}
          onGridReady={(e) => onGridReady(e)}
          onBodyScroll={(e) => autoSizeAll(e, false)}
          onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
          context={{useOverlay: 'Loading Sites'}}
          components={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellFocus={true}
          defaultColDef={{
            sortable: true,
            resizable: true,
            filter: 'agTextColumnFilter',
            floatingFilter: true
          }}
        >
          <AgGridColumn
            field="siteId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={() => <Edit />}
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
          <AgGridColumn minWidth={500} field="siteName" />
          <AgGridColumn minWidth={200} field="locationName" />
          <AgGridColumn field="state" />
          <AgGridColumn minWidth={200} field="country" />
          <AgGridColumn field="latitude" />
          <AgGridColumn field="longitude" />
          <AgGridColumn suppressMenu={true} field="isActive" headerName="Active" />
          <AgGridColumn
            field="siteId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={() => <CopyAll />}
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
            field="observableItemId"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={(e) => (e.data.isActive ? <></> : <Delete />)}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              !e.data.isActive &&
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
