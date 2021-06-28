import {Box, Button, IconButton, Tooltip, Typography} from '@material-ui/core';
import {DataGrid} from '@material-ui/data-grid';
import {CloudUploadOutlined, Delete, Info, GridOn} from '@material-ui/icons';
import React, {useEffect, useState} from 'react';
import {NavLink} from 'react-router-dom';
import {deleteJob, getEntity} from '../../axios/api';
import AlertDialog from '../ui/AlertDialog';

const disabledHeader = {headerName: ' ', filterable: false, sortable: false, disableColumnMenu: true};

const JobList = () => {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [deleteJobId, setDeleteJobId] = useState(null);

  useEffect(() => {
    if (loading)
      getEntity('stagedJobs').then((result) => {
        // HACK: fix the api to return the correct format instead
        const stagedJobs = result.data?._embedded?.stagedJobs || [];
        const jobRows = stagedJobs.map((j) => {
          return {
            ...j,
            program: j.program.programName,
            creator: j.creator.email,
            created: new Date(j.created).toLocaleDateString('en-AU'),
            updated: j.updated ? new Date(j.updated).toLocaleDateString('en-AU') : '---'
          };
        });
        setJobs(jobRows);
        setLoading(false);
      });
  }, [jobs, loading]);

  const ActionCell = (params) => {
    return (
      <Tooltip title="Delete">
        <IconButton name="delete" onClick={() => setDeleteJobId(params.row.id)}>
          <Delete />
        </IconButton>
      </Tooltip>
    );
  };

  const LinkCell = (params) => {
    return (
      <>
        <Tooltip title="Info">
          <IconButton component={NavLink} to={`/jobs/${params.row.id}/view`}>
            <Info />
          </IconButton>
        </Tooltip>
        {params.row.status === 'STAGED' && (
          <Tooltip title="View Staged Sheet">
            <IconButton component={NavLink} to={`/validation/${params.row.id}`}>
              <GridOn />
            </IconButton>
          </Tooltip>
        )}
      </>
    );
  };

  return (
    <>
      <AlertDialog
        open={deleteJobId !== null}
        text="Delete Job?"
        action="Delete"
        onClose={() => setDeleteJobId(null)}
        onConfirm={() => {
          const id = deleteJobId;
          setDeleteJobId(null);
          deleteJob(deleteJobId).then(() => {
            setJobs((jobs) => jobs.filter((j) => j.id !== id));
          });
        }}
      />
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Jobs</Typography>
        </Box>
        <Box>
          <Button to="/upload" component={NavLink} startIcon={<CloudUploadOutlined />}>
            Upload XLSX File
          </Button>
        </Box>
      </Box>
      <DataGrid
        columns={[
          {...disabledHeader, field: 'id', width: 100, renderCell: LinkCell},
          {field: 'reference', headerName: 'Reference', flex: 2},
          {field: 'isExtendedSize', headerName: 'Extended', width: 140},
          {field: 'status', headerName: 'Status', width: 120},
          {field: 'program', headerName: 'Program', width: 140},
          {field: 'source', headerName: 'Type', width: 120},
          {field: 'creator', headerName: 'Creator', flex: 1},
          {field: 'created', headerName: 'Uploaded', width: 150},
          {...disabledHeader, field: 'actions', width: 70, renderCell: ActionCell}
        ]}
        autoPageSize
        disableSelectionOnClick
        density="compact"
        loading={loading}
        hideFooter={loading}
        rows={jobs}
      />
    </>
  );
};

export default JobList;
