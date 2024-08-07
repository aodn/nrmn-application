import React, { useRef, useState, useCallback } from 'react';
import { Box, Button, Dialog, DialogActions, DialogContent, DialogTitle, Typography } from '@mui/material';
import { Navigate, NavLink, useLocation } from 'react-router-dom';
import { getResult } from '../../../api/api';
import { AgGridReact } from 'ag-grid-react';
import { Add, CopyAll, Delete, Edit } from '@mui/icons-material';
import { entityDelete } from '../../../api/api';
import stateFilterHandler, { LocationState } from '../../../common/state-event-handler/StateFilterHandler';
import Backdrop from '@mui/material/Backdrop';
import CircularProgress from '@mui/material/CircularProgress';
import { AuthContext } from '../../../contexts/auth-context';
import { AppConstants } from '../../../common/constants';

import 'ag-grid-enterprise';
import { ColDef } from 'ag-grid-enterprise';
import { BodyScrollEndEvent, CellClickedEvent, GridReadyEvent, ICellRendererParams } from 'ag-grid-community';

const SITE_LIST_GRID_ID = 'site-list';

const defaultColDef = {
  lockVisible: true,
  sortable: true,
  resizable: true,
  filter: 'agTextColumnFilter',
  floatingFilter: true,
  filterParams: { debounceMs: AppConstants.Filter.WAIT_TIME_ON_FILTER_APPLY },
};

const SiteList = () => {
  const rowsPerPage = 50;
  const [loading, setLoading] = React.useState(false);
  const [open, setOpen] = useState<boolean>(false);
  const location = useLocation();
  const [redirect, setRedirect] = useState<string | undefined>();
  const [dialogState, setDialogState] = useState<{ open: boolean, item?: {siteId: string}, description?: string }>({ open: false });
  const gridRef = useRef<AgGridReact>(null);

  const createColumns = useCallback((roles: Array<string> | undefined): ColDef[] => {
    const cols: ColDef[] = [];

    cols.push({
      field: 'siteId',
      headerName: '',
      suppressMovable: true,
      suppressMenu: true,
      filter: false,
      resizable: false,
      sortable: false,
      cellRenderer: () => <Edit />,
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        if (roles?.includes(AppConstants.ROLES.DATA_OFFICER) || roles?.includes(AppConstants.ROLES.ADMIN)) {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            if (event.data) {
              window.open(`/reference/site/${event.data.siteId}/edit`, '_blank')?.focus();
            }
          } else {
            setRedirect(`${event.data.siteId}/edit`);
          }
        }
      }
    });

    cols.push({
      field: 'siteCode',
      colId: 'site.siteCode',
      cellStyle: { cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        const mouseEvent = event.event as MouseEvent;
        if (mouseEvent.ctrlKey) {
          if (event.data) {
            window.open(`/reference/site/${event.data.siteId}`, '_blank')?.focus();
          }
        } else {
          setRedirect(`${event.data.siteId}`);
        }
      }
    });

    cols.push({
      minWidth: 500,
      field: 'siteName',
      colId: ' site.siteName',
    });

    cols.push({
      minWidth: 200,
      field: 'locationName',
      colId: 'site.locationName',
    });

    cols.push({
      field: 'state',
      colId: 'site.state',
    });

    cols.push({
      minWidth: 200,
      field: 'country',
      colId: 'site.country',
    });

    cols.push({
      field: 'latitude',
      colId: 'site.latitude',
    });

    cols.push({
      field: 'longitude',
      colId: 'site.longitude',
    });

    cols.push({
      suppressMenu: true,
      field: 'isActive',
      headerName: 'Active',
      colId: 'site.isActive',
    });

    cols.push({
      field: 'siteId',
      headerName: '',
      suppressMovable: true,
      suppressMenu: true,
      filter: false,
      resizable: false,
      sortable: false,
      cellRenderer: () => <CopyAll />,
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        const mouseEvent = event.event as MouseEvent;
        if (mouseEvent.ctrlKey) {
          window.open(`/reference/site/${event.data.siteId}/clone`, '_blank')?.focus();
        } else {
          setRedirect(`${event.data.siteId}/clone`);
        }
      }
    });

    cols.push({
      field: 'observableItemId',
      headerName: '',
      suppressMovable: true,
      suppressMenu: true,
      filter: false,
      resizable: false,
      sortable: false,
      cellRenderer: (e: ICellRendererParams) => (e.data?.isActive === 'true' ? <></> : <Delete />),
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        event.data.isActive !== 'true' &&
          setDialogState({
            open: true,
            item: event.data,
          });
      }
    });
    return cols;

  }, [setDialogState, setRedirect]);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (evt: BodyScrollEndEvent | GridReadyEvent, skipHeader: boolean) => {
    if (evt) {
      evt.columnApi.autoSizeAllColumns(skipHeader);
    }
  };

  const onGridReady = useCallback((event: GridReadyEvent) => {
    document.title = 'Sites';
    async function fetchSites(e: GridReadyEvent) {
      e.api.setDatasource({
        // This is the functional structure need for datasource
        rowCount: rowsPerPage,
        getRows: (params) => {
          let url = `sites?page=${params.startRow / 100}`;
          const conditions = [];
          // Filter section
          for (const name in params.filterModel) {
            const p = params.filterModel[name];

            if (p.type) {
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
          }

          // Sorting section, order is important
          const sort: Array<{ field: string; order: string }> = [];

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
            .finally(() => {
              setLoading(false);
            });
        }
      });
    }

    fetchSites(event).then(() => {
      const filter = location.state as LocationState;
      if (!(filter.resetFilters)) {
        stateFilterHandler.restoreStateFilters(gridRef, SITE_LIST_GRID_ID);
      }
      else {
        stateFilterHandler.resetStateFilters(SITE_LIST_GRID_ID);
      }

      // autoSizeAll(event, false);
    });
  }, [location]);

  // Replace disableBackdropClick
  const handleClose = useCallback((event: React.MouseEvent, reason: 'backdropClick' | 'escapeKeyDown') => {
    if (reason !== 'backdropClick') {
      setOpen(false);
    }
  }, []);

  if (redirect) return <Navigate to={`/reference/site/${redirect}`} />;

  const dialog = (
    <Dialog
      disableEscapeKeyDown
      maxWidth="xs"
      open={open}
      onClose={handleClose}
    >
      <DialogTitle>Delete Site?</DialogTitle>
      <DialogContent>
        Are you sure you want to permanently delete this site?
        <Box p={2}>
          <Typography variant="subtitle2">{dialogState.description}</Typography>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" autoFocus onClick={() => setDialogState({ open: false })}>
          Cancel
        </Button>
        <Button
          variant="contained"
          onClick={() => {
            entityDelete('site', dialogState.item?.siteId).then(() => {
              gridRef.current?.api.refreshInfiniteCache();
              setDialogState({ open: false });
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
      {({ auth }) => (
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
              <Button style={{ width: '100%' }}
                to="/reference/site"
                component={NavLink}
                disabled={!(auth.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles?.includes(AppConstants.ROLES.ADMIN))}
                variant={'contained'}
                startIcon={<Add></Add>}>
                New Site
              </Button>
            </Box>
          </Box>
          <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
            <AgGridReact
              ref={gridRef}
              rowHeight={24}
              pagination={true}
              paginationPageSize={rowsPerPage}
              rowModelType={'infinite'}
              enableCellTextSelection={true}
              onGridReady={(e) => onGridReady(e)}
              onBodyScrollEnd={(e) => autoSizeAll(e, false)}
              onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(SITE_LIST_GRID_ID, e)}
              suppressCellFocus={true}
              defaultColDef={defaultColDef}
              columnDefs={createColumns(auth.roles)}
            >
            </AgGridReact>
          </Box>
        </>)}
    </AuthContext.Consumer>
  );
};

export default SiteList;
