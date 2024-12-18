import React, { useState, useRef, useCallback } from 'react';
import { Navigate } from 'react-router';
import { NavLink, useLocation } from 'react-router-dom';
import { Box, Button, Typography } from '@mui/material';
import { CloudUploadOutlined } from '@mui/icons-material';
import 'ag-grid-enterprise';
import { AgGridReact } from 'ag-grid-react';
import { deleteJob, getEntity } from '../../api/api';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import { GridOn, Delete, Info } from '@mui/icons-material';
import stateFilterHandler, { LocationState } from '../../common/state-event-handler/StateFilterHandler';
import { AuthContext } from '../../contexts/auth-context';
import { AppConstants } from '../../common/constants';
import { CellClickedEvent, ColDef, FilterChangedEvent, GetRowIdParams, GridReadyEvent, ICellRendererParams, ValueFormatterParams } from 'ag-grid-community';
import { StatusJobType } from '../../common/types';

const JOBLIST_GRID_ID = 'job-list';

const TimeStampCell = (params: ValueFormatterParams): string => {
  return params.value
    ? new Date(params.value).toLocaleDateString('en-AU') +
    ' ' +
    new Date(params.value).toLocaleTimeString('en-AU', { hour: 'numeric', minute: '2-digit' })
    : '';
};

const JobList: React.FC = () => {
  const location = useLocation();
  const gridRef = useRef<AgGridReact>(null);
  const [redirect, setRedirect] = useState<string | undefined>();
  const [deleteJobId, setDeleteJobId] = useState<string | undefined>();
  const iconViewBoxDimension = '-2 -2 30 30';

  // callback tells the grid to use the 'id' attribute for IDs, IDs should always be strings
  const getRowId = (params: GetRowIdParams) => params.data.id;

  const onCellClicked = (event: CellClickedEvent) => {
    if ([StatusJobType.STAGED, StatusJobType.INGESTED].includes(event.data.status)) {
      const mouseEvent = event.event as MouseEvent;
      const target = `/data/job/${event.data.id}/edit`;
      mouseEvent.ctrlKey ? window.open(target, '_blank')?.focus() : setRedirect(target);
    }
  };

  const onGridReady = useCallback((event: GridReadyEvent) => {
    document.title = 'Jobs';
    async function fetchJobs(e: GridReadyEvent) {
      await getEntity('stage/jobs').then((res) => e.api.setRowData(res.data));
    }

    fetchJobs(event).then(() => {
      const filter = location.state as LocationState;
      if (!filter?.resetFilters) {
        stateFilterHandler.restoreStateFilters(gridRef, JOBLIST_GRID_ID);
      } else {
        stateFilterHandler.resetStateFilters(JOBLIST_GRID_ID);
      }
    });
  }, [location]);

  const createColumns = useCallback((roles: Array<string> | undefined) => {
    const cols: ColDef[] = [];

    cols.push({
      width: 40,
      field: 'id',
      headerName: '',
      suppressMovable: true,
      suppressMenu: true,
      filter: false,
      resizable: false,
      sortable: false,
      cellRenderer: (e: ICellRendererParams) => (e.data?.id ? <Info viewBox={iconViewBoxDimension} /> : <></>),
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        if (event.data.id) {
          const mouseEvent = event.event as MouseEvent;
          if (mouseEvent.ctrlKey) {
            window.open(`/data/job/${event.data.id}/view`, '_blank')?.focus();
          } else {
            setRedirect(`/data/job/${event.data.id}/view`);
          }
        }
      }
    });

    cols.push({
      width: 40,
      cellStyle: { cursor: 'pointer' },
      cellRenderer: (e: ICellRendererParams) =>
        [StatusJobType.STAGED, StatusJobType.INGESTED]
          .includes(e.data?.status) ? <GridOn htmlColor={'#808080'} viewBox={iconViewBoxDimension} /> : <></>,
      onCellClicked: onCellClicked,
    });

    cols.push({
      flex: 1,
      field: 'reference',
      cellStyle: { cursor: 'pointer' },
      onCellClicked: onCellClicked,
    });

    cols.push({
      width: 80,
      field: 'isExtendedSize',
      headerName: 'Extended',
    });

    cols.push({
      width: 80,
      field: 'status',
      headerName: 'Status',
      rowGroup: true,
      hide: true,
      comparator: (a, b) => {
        const status = [StatusJobType.STAGED, StatusJobType.INGESTED, StatusJobType.CORRECTED, StatusJobType.FAILED];
        return status.indexOf(a) - status.indexOf(b);
      }
    });

    cols.push({
      width: 100,
      field: 'program',
    });

    cols.push({
      width: 200,
      field: 'creator',
    });

    cols.push({
      width: 200,
      field: 'created',
      sort: 'desc',
      valueFormatter: TimeStampCell,
    });

    cols.push({
      width: 60,
      suppressMovable: true,
      suppressMenu: true,
      filter: false,
      resizable: false,
      sortable: false,
      cellRenderer: (e: ICellRendererParams) =>
        e.data && [StatusJobType.STAGED, StatusJobType.ERROR].includes(e.data.status) ? <Delete viewBox={iconViewBoxDimension} /> : <></>,
      cellStyle: { paddingLeft: '10px', color: 'grey', cursor: 'pointer' },
      onCellClicked: (event: CellClickedEvent) => {
        if (roles?.includes(AppConstants.ROLES.DATA_OFFICER) || roles?.includes(AppConstants.ROLES.ADMIN))
          setDeleteJobId(event.data.id);
      }

    });

    return cols;
  }, []);

  if (redirect) return <Navigate to={redirect} />;

  return (
    <AuthContext.Consumer>
      {({ auth }) =>
        <>
          <AlertDialog
            open={deleteJobId ? true : false}
            text="Delete Job?"
            action="Delete"
            onClose={() => setDeleteJobId(undefined)}
            onConfirm={() => {
              deleteJob(deleteJobId)
                .then(() =>
                  setDeleteJobId((value) => {
                    if (value) {
                      gridRef.current?.api.getRowNode(value)?.setSelected(true);
                      gridRef.current?.api.applyTransaction({ remove: gridRef.current.api.getSelectedRows() });
                    }
                    return undefined;
                  })
                );
            }}
          />
          <Box display="flex" flexDirection="row" p={1} pb={1}>
            <Box flexGrow={1}>
              <Typography variant="h4">Jobs</Typography>
            </Box>
            <Box>
              <Button
                data-testid="xls-upload-button"
                variant="contained"
                to="/data/upload"
                component={NavLink}
                disabled={!(auth.roles?.includes(AppConstants.ROLES.DATA_OFFICER) || auth.roles?.includes(AppConstants.ROLES.ADMIN))}
                startIcon={<CloudUploadOutlined />}>
                {'Upload XLSX File'}
              </Button>
            </Box>
          </Box>
          <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
            <AgGridReact
              ref={gridRef}
              rowHeight={24}
              pagination={false}
              getRowId={getRowId}
              enableCellTextSelection={true}
              onGridReady={(e) => onGridReady(e)}
              onFilterChanged={(e: FilterChangedEvent) => stateFilterHandler.stateFilterEventHandler(JOBLIST_GRID_ID, e)}
              context={{ useOverlay: 'Loading Jobs' }}
              components={{ loadingOverlay: LoadingOverlay }}
              loadingOverlayComponent="loadingOverlay"
              suppressCellFocus={true}
              isGroupOpenByDefault={(params) => params.key === 'STAGED'}
              autoGroupColumnDef={{ sortable: true, sort: 'asc', width: 150 }}
              groupDisplayType="singleColumn"
              defaultColDef={{ lockVisible: true, sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true, suppressMenu: true }}
              columnDefs={createColumns(auth.roles)}
            >
            </AgGridReact>
          </Box>
        </>}
    </AuthContext.Consumer>
  );
};

export default JobList;
