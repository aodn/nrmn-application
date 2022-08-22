import {Box, Button, Typography} from '@mui/material';
import {CloudUploadOutlined} from '@mui/icons-material';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useState, useRef} from 'react';
import {Navigate} from 'react-router';
import {NavLink, useLocation} from 'react-router-dom';
import {deleteJob, getEntity} from '../../api/api';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import {GridOn, Delete, Info} from '@mui/icons-material';
import stateFilterHandler from '../../common/state-event-handler/StateFilterHandler';

const TimeStampCell = (params) => {
  return params.value
    ? new Date(params.value).toLocaleDateString('en-AU') +
        ' ' +
        new Date(params.value).toLocaleTimeString('en-AU', {hour: 'numeric', minute: '2-digit'})
    : '';
};

const JobList = () => {
  const location = useLocation();
  const gridRef = useRef(null);
  const [redirect, setRedirect] = useState();
  const [deleteJobId, setDeleteJobId] = useState();
  const iconViewBoxDimension = '-2 -2 30 30';

  // callback tells the grid to use the 'id' attribute for IDs, IDs should always be strings
  const getRowId = (params) => params.data.id;

  const onCellClicked = (e) => {
    if (['STAGED', 'INGESTED'].includes(e.data.status)) {
      const target = `/data/job/${e.data.id}/edit`;
      e.event.ctrlKey ? window.open(target, '_blank').focus() : setRedirect(target);
    }
  };

  const onGridReady = (event) => {
    async function fetchJobs(e) {
      await getEntity('stage/jobs').then((res) => e.api.setRowData(res.data));
    }
    fetchJobs(event).then(() => {
      if (!location?.state?.resetFilters) {
        stateFilterHandler.restoreStateFilters(gridRef);
      } else {
        stateFilterHandler.resetStateFilters(gridRef);
      }
    });
  };

  if (redirect) return <Navigate to={redirect} />;

  return (
    <>
      <AlertDialog
        open={deleteJobId ? true : false}
        text="Delete Job?"
        action="Delete"
        onClose={() => setDeleteJobId()}
        onConfirm={() => {
          deleteJob(deleteJobId).then(() => {
            setDeleteJobId();
            gridRef.current.api.getRowNode(deleteJobId).setSelected(true);
            gridRef.current.api.applyTransaction({remove: gridRef.current.api.getSelectedRows()});
          });
        }}
      />
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Jobs</Typography>
        </Box>
        <Box>
          <Button variant="contained" to="/data/upload" component={NavLink} startIcon={<CloudUploadOutlined />}>
            Upload XLSX File
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          ref={gridRef}
          id={'job-list'}
          rowHeight={24}
          pagination={false}
          getRowId={getRowId}
          enableCellTextSelection={true}
          onGridReady={(e) => onGridReady(e)}
          onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
          context={{useOverlay: 'Loading Jobs'}}
          components={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellFocus={true}
          isGroupOpenByDefault={(params) => params.key === 'STAGED'}
          autoGroupColumnDef={{sortable: true, sort: 'asc', width: 150}}
          groupDisplayType="singleColumn"
          defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true, suppressMenu: true}}
        >
          <AgGridColumn
            width={40}
            field="id"
            headerName=""
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={(e) => (e.data?.id ? <Info viewBox={iconViewBoxDimension} /> : <></>)}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (!e.data.id) return;
              if (e.event.ctrlKey) {
                window.open(`/data/job/${e.data.id}/view`, '_blank').focus();
              } else {
                setRedirect(`/data/job/${e.data.id}/view`);
              }
            }}
          />
          <AgGridColumn
            width={40}
            cellStyle={{cursor: 'pointer'}}
            cellRenderer={(e) =>
              ['STAGED', 'INGESTED'].includes(e.data?.status) ? <GridOn htmlColor={'#808080'} viewBox={iconViewBoxDimension} /> : <></>
            }
            onCellClicked={onCellClicked}
          />
          <AgGridColumn flex={1} field="reference" cellStyle={{cursor: 'pointer'}} onCellClicked={onCellClicked} />
          <AgGridColumn width={80} field="isExtendedSize" headerName="Extended" />
          <AgGridColumn
            width={80}
            field="status"
            headerName="Status"
            rowGroup={true}
            hide={true}
            comparator={(a, b) => {
              const status = ['STAGED', 'INGESTED', 'CORRECTED', 'FAILED'];
              return status.indexOf(a) - status.indexOf(b);
            }}
          />
          <AgGridColumn width={100} field="program" />
          <AgGridColumn width={200} field="creator" />
          <AgGridColumn width={200} field="created" sort="desc" valueFormatter={TimeStampCell} />
          <AgGridColumn
            width={60}
            suppressMovable={true}
            suppressMenu={true}
            filter={false}
            resizable={false}
            sortable={false}
            cellRenderer={(e) =>
              e.data && ['STAGED', 'ERROR'].includes(e.data.status) ? <Delete viewBox={iconViewBoxDimension} /> : <></>
            }
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => setDeleteJobId(e.data.id)}
          />
        </AgGridReact>
      </Box>
    </>
  );
};

export default JobList;
