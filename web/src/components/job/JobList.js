import {Box, Button, Typography} from '@mui/material';
import {CloudUploadOutlined} from '@mui/icons-material';
import 'ag-grid-enterprise';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import React, {useEffect, useState} from 'react';
import {Navigate} from 'react-router';
import {NavLink} from 'react-router-dom';
import {deleteJob, getEntity} from '../../api/api';
import LoadingOverlay from '../overlays/LoadingOverlay';
import AlertDialog from '../ui/AlertDialog';
import {GridOn, Delete, Info} from '@mui/icons-material';

const TimeStampCell = (params) => {
  return params.value
    ? new Date(params.value).toLocaleDateString('en-AU') +
        ' ' +
        new Date(params.value).toLocaleTimeString('en-AU', {hour: 'numeric', minute: '2-digit'})
    : '';
};

const JobList = () => {
  const [rowData, setRowData] = useState([]);
  const [redirect, setRedirect] = useState();
  const [deleteJobId, setDeleteJobId] = useState();

  useEffect(() => {
    async function fetchJobs() {
      await getEntity('stage/jobs').then((res) => setRowData(res.data));
    }
    fetchJobs();
  }, []);

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
            setRowData([...rowData.filter((d) => d.id !== deleteJobId)]);
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
          rowHeight={24}
          pagination={false}
          rowData={rowData}
          enableCellTextSelection={true}
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
            cellRenderer={(e) => (e.data?.id ? <Info/> : <></>)}
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
            flex={1}
            field="reference"
            cellStyle={{cursor: 'pointer'}}
            cellRenderer={(e) => (e.data?.status === 'STAGED' ?
              <div style={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap', }}><GridOn/><span>{e.data?.reference}</span></div>
                : <>{e.data?.reference}</>)}
            onCellClicked={(e) => {
              if (e.data.status === 'STAGED') {
                const target = `/data/job/${e.data.id}/edit`;
                e.event.ctrlKey ? window.open(target, '_blank').focus() : setRedirect(target);
              }
            }}
          />
          <AgGridColumn width={80} field="isExtendedSize" headerName="Extended" />
          <AgGridColumn
            width={80}
            field="status"
            rowGroup={true}
            hide={true}
            comparator={(a, b) => {
              const status = ['STAGED', 'FAILED', 'INGESTED'];
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
            cellRenderer={(e) => (e.data && e.data.status !== 'INGESTED' ? <Delete/> : <></>)}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => setDeleteJobId(e.data.id)}
          />
        </AgGridReact>
      </Box>
    </>
  );
};

export default JobList;
