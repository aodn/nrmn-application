import React, {useRef, useState, useCallback} from 'react';
import {Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography} from '@mui/material';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import {getResult} from '../../../api/api';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, CopyAll, Delete, Edit} from '@mui/icons-material';
import {entityDelete} from '../../../api/api';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import {AuthContext} from '../../../contexts/auth-context';
import {AppConstants} from '../../../common/constants';

import 'ag-grid-enterprise';

const SiteList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);

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

  const onGridReady = useCallback((event) => {
    document.title = 'Sites';
    async function fetchSites(e) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `sites?page=${params.startRow / 100}`;
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

    fetchSites(event).then(() => {
      if(!(location?.state?.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef);
      }
      else {
        stateFilterHandler.resetStateFilters(gridRef);
      }

      // autoSizeAll(event, false);
    });
  }, [location]);

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
              gridRef.current.api.refreshInfiniteCache();
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
    <AuthContext.Consumer>
      {({auth}) => (
        <>
          <Backdrop
            sx={{ color: '#fff', zIndex: (theme) => theme.zIndex.drawer + 1 }}
            open={loading}>
            <CircularProgress color="inherit" />
          </Backdrop>
          {dialogState.open && dialog}
          <Box display="flex" flexDirection="row" pt={1}>
            <Box p={1} pl={2} flexGrow={1}>
              <Typography variant="h4">Sites</Typography>
            </Box>
            <Box mr={4}>
              <Button style={{width: '100%'}}
                      to="/reference/site"
                      component={NavLink}
                      disabled={!(auth.roles.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles.includes(AppConstants.ROLES.ADMIN))}
                      variant={'contained'}
                      startIcon={<Add></Add>}>
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
              paginationPageSize={rowsPerPage}
              rowModelType={'infinite'}
              enableCellTextSelection={true}
              onGridReady={(e) => onGridReady(e)}
              onBodyScrollEnd={(e) => autoSizeAll(e, false)}
              onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
              suppressCellFocus={true}
              defaultColDef={{
                lockVisible: true,
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
                cellRenderer={() => <Edit/>}
                cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
                onCellClicked={(e) => {
                  if(auth.roles.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles.includes(AppConstants.ROLES.ADMIN)) {
                    if (e.event.ctrlKey) {
                      window.open(`/reference/site/${e.data.siteId}/edit`, '_blank').focus();
                    } else {
                      setRedirect(`${e.data.siteId}/edit`);
                    }
                  }
                }}
              />
              <AgGridColumn
                field="siteCode"
                colId="site.siteCode"
                cellStyle={{cursor: 'pointer'}}
                onCellClicked={(e) => {
                  if (e.event.ctrlKey) {
                    window.open(`/reference/site/${e.data.siteId}`, '_blank').focus();
                  } else {
                    setRedirect(`${e.data.siteId}`);
                  }
                }}
              />
              <AgGridColumn minWidth={500} field="siteName" colId="site.siteName"/>
              <AgGridColumn minWidth={200} field="locationName" colId="site.locationName"/>
              <AgGridColumn field="state" colId="site.state"/>
              <AgGridColumn minWidth={200} field="country" colId="site.country"/>
              <AgGridColumn field="latitude" colId="site.latitude"/>
              <AgGridColumn field="longitude" colId="site.longitude"/>
              <AgGridColumn suppressMenu={true} field="isActive" headerName="Active" colId="site.isActive"/>
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
                cellRenderer={(e) => (e.data?.isActive === 'true' ? <></> : <Delete />)}
                cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
                onCellClicked={(e) => {
                  e.data.isActive !== 'true' &&
                    setDialogState({
                      open: true,
                      item: e.data
                    });
                }}
              />
            </AgGridReact>
          </Box>
        </>)}
    </AuthContext.Consumer>
  );
};

export default SiteList;
