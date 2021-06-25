import {Box, Button, CircularProgress, IconButton, Tooltip, Typography} from '@material-ui/core';
import {DataGrid} from '@material-ui/data-grid';
import {CloudUploadOutlined, Delete, Info, Attachment} from '@material-ui/icons';
import React, {useEffect, useState} from 'react';
import {NavLink} from 'react-router-dom';
import {getEntity} from '../../axios/api';
import AlertDialog from '../ui/AlertDialog';

const JobList = () => {
  const [jobs, setJobs] = useState(null);

  useEffect(() => {
    if (!jobs)
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
      });
  }, [jobs]);

  // const defaultPopup = {isOpen: false, jobId: 0, index: 0};
  const [deletePopup, setDeletePopup] = useState(null);

  const ActionCell = (params) => {
    return (
      <Tooltip title="Delete">
        <IconButton name="delete" onClick={() => setDeletePopup(params.row.id)}>
          <Delete />
        </IconButton>
      </Tooltip>
    );
  };

  const LinkCell = (params) => {
    return (
      <>
        <Tooltip title="Info">
          <IconButton name="info" onClick={() => setDeletePopup(params.row.id)}>
            <Info />
          </IconButton>
        </Tooltip>
        <Tooltip title="Attachment">
          <IconButton name="Attachment" onClick={() => setDeletePopup(params.row.id)}>
            <Attachment />
          </IconButton>
        </Tooltip>
      </>
    );
  };

  return (
    <>
      <AlertDialog
        open={deletePopup !== null}
        text="Delete Job?"
        action="Delete"
        onClose={() => {
          setDeletePopup(null);
        }}
      />
      <Box display="flex" flexDirection="row">
        <Box p={1} flexGrow={1}>
          <Typography variant="h4">Jobs</Typography>
        </Box>
        <Box p={1}>
          <Button to="/upload" component={NavLink} startIcon={<CloudUploadOutlined />}>
            Upload XLSX File
          </Button>
        </Box>
      </Box>

      {jobs ? (
        <Box flexGrow={1}>
          <DataGrid
            columns={[
              {field: 'id', headerName: ' ', flex: 0.5, renderCell: LinkCell},
              {field: 'reference', headerName: 'Reference', flex: 2},
              {field: 'isExtendedSize', headerName: 'Extended', flex: 0.75, filterable: false, sortable: false, disableColumnMenu: true},
              {field: 'status', headerName: 'Status', flex: 0.75},
              {field: 'program', headerName: 'Program', flex: 0.75},
              {field: 'source', headerName: 'Type', flex: 0.75},
              {field: 'creator', headerName: 'Creator', flex: 1.5},
              {field: 'created', headerName: 'Created', flex: 1},
              {
                field: 'actions',
                headerName: ' ',
                flex: 0.5,
                filterable: false,
                sortable: false,
                disableColumnMenu: true,
                renderCell: ActionCell
              }
            ]}
            autoPageSize
            disableSelectionOnClick
            density="compact"
            rows={jobs}
          />
        </Box>
      ) : (
        <CircularProgress size={200} style={{color: '#ccc'}}></CircularProgress>
      )}
    </>
  );
};

export default JobList;
