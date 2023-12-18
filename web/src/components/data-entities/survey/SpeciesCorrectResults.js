import React, {useState} from 'react';
import { styled } from '@mui/material/styles';
import {PropTypes} from 'prop-types';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TablePagination from '@mui/material/TablePagination';
import {Paper} from '@mui/material';

const PREFIX = 'SpeciesCorrectResults';

const classes = {
  root: `${PREFIX}-root`
};

const StyledTableContainer = styled(TableContainer)(({
  theme: {palette, typography}
}) => ({
  [`& .${classes.root}`]: {
    '& .MuiTable-root': {
      '& .MuiTableHead-root': {
        '& .MuiTableRow-head': {
          '& .MuiTableCell-head': {
            fontSize: typography?.table?.fontSize,
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
        fontSize: typography?.table?.fontSize,
        padding: typography?.table?.padding,
        color: palette?.text.textPrimary
      }
    }
  }
}));

const SpeciesCorrectResults = ({results, onClick}) => {

  const pageSize = 50;
  const [page, setPage] = useState(0);

  return (
    <StyledTableContainer classes={classes} component={Paper} disabled>
      <TablePagination
        showFirstButton
        showLastButton
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
              return (
                <TableRow key={r.observableItemId} onClick={() => onClick(r)} style={{cursor: 'pointer'}}>
                  <TableCell>{r.observableItemName}</TableCell>
                  <TableCell>{r.commonName}</TableCell>
                  <TableCell>{r.supersededBy}</TableCell>
                  <TableCell>{r.surveyCount}</TableCell>
                </TableRow>
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
        showFirstButton
        showLastButton
        component="div"
        rowsPerPageOptions={[]}
        count={results.length}
        rowsPerPage={pageSize}
        page={page}
        onPageChange={(e, p) => setPage(p)}
      />
    </StyledTableContainer>
  );
};

SpeciesCorrectResults.propTypes = {
  results: PropTypes.array,
  onClick: PropTypes.func
};

export default SpeciesCorrectResults;
