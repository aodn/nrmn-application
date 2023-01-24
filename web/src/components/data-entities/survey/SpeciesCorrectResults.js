import React, {useState} from 'react';
import {PropTypes} from 'prop-types';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TablePagination from '@mui/material/TablePagination';
import {Alert, Button, Paper, Box} from '@mui/material';

import SpeciesCorrectEdit from './SpeciesCorrectEdit';
import {makeStyles} from '@mui/styles';
import LaunchIcon from '@mui/icons-material/Launch';

const useStyles = makeStyles(({palette, typography}) => ({
  root: {
    '& .MuiTable-root': {
      '& .MuiTableHead-root': {
        '& .MuiTableRow-head': {
          '& .MuiTableCell-head': {
            fontSize: typography?.table.fontSize,
            background: palette?.primary.rowHeader
          }
        }
      },
      '& .MuiTableRow-root': {
        '&:nth-child(even)': {
          backgroundColor: palette?.primary.rowHighlight
        }
      },
      '& .MuiTableCell-root': {
        fontSize: typography?.table.fontSize,
        padding: typography?.table.padding,
        color: palette?.text.textPrimary
      }
    }
  }
}));

const SpeciesCorrectResults = ({results, onClick}) => {
  const classes = useStyles();
  const pageSize = 50;
  const [selected, setSelected] = useState(null);
  const [page, setPage] = useState(0);

  const finishedMessage = selected?.jobId && (
    <Box m={1} width="30%">
      <Alert>Species Corrected</Alert>
      <Box m={1}>
        <Button
          variant="outlined"
          endIcon={<LaunchIcon />}
          onClick={() => window.open(`/data/job/${selected.jobId}/view`, '_blank').focus()}
        >
          Open Job
        </Button>
      </Box>
    </Box>
  );

  return (
    <TableContainer classes={classes} component={Paper} disabled>
      <TablePagination
        component="div"
        rowsPerPageOptions={[]}
        count={results.length}
        rowsPerPage={pageSize}
        page={page}
        onPageChange={(e, p) => setPage(p)}
      />
      <Table>
        <TableHead>
          <TableRow>
            <TableCell width="30%">Observable Item Name</TableCell>
            <TableCell width="30%">Common name</TableCell>
            <TableCell width="30%">Superseded By</TableCell>
            <TableCell width="10%">Surveys</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {results.length > 0 ? (
            results.slice(page * pageSize, page * pageSize + pageSize).map((r) => {
              const isSelected = selected === r.observableItemId;
              return (
                <>
                  <TableRow
                    key={r.observableItemId}
                    selected={selected === r.observableItemId}
                    onClick={() => onClick(r)}
                    style={{cursor: 'pointer', border: isSelected ? '2px solid' : 'none'}}
                  >
                    <TableCell>{r.observableItemName}</TableCell>
                    <TableCell>{r.commonName}</TableCell>
                    <TableCell>{r.supersededBy}</TableCell>
                    <TableCell>{r.surveyCount}</TableCell>
                  </TableRow>
                  {isSelected && (
                    <TableCell colSpan={4}>
                      <SpeciesCorrectEdit detail={r} />
                    </TableCell>
                  )}
                </>
              );
            })
          ) : (
            <TableRow>
              <TableCell colSpan={4}>No Results</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
      <TablePagination
        component="div"
        rowsPerPageOptions={[]}
        count={results.length}
        rowsPerPage={pageSize}
        page={page}
        onPageChange={(e, p) => setPage(p)}
      />
    </TableContainer>
  );
};

SpeciesCorrectResults.propTypes = {
  results: PropTypes.array,
  onClick: PropTypes.func
};

export default SpeciesCorrectResults;
