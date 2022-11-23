import React, {useState} from 'react';
import {PropTypes} from 'prop-types';

import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import {Paper} from '@mui/material';

import {makeStyles} from '@mui/styles';

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
  const [selected, setSelected] = useState(null);
  return (
    <TableContainer classes={classes} component={Paper} disabled>
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
            results.map((r) => (
              <TableRow
                key={r.observableItemId}
                selected={selected === r.observableItemId}
                onClick={() => {
                  setSelected(r.observableItemId);
                  onClick(r.observableItemId);
                }}
                style={{cursor: 'pointer'}}
              >
                <TableCell>{r.observableItemName}</TableCell>
                <TableCell>{r.commonName}</TableCell>
                <TableCell>{r.supersededBy}</TableCell>
                <TableCell>{Object.keys(r.surveyJson).length}</TableCell>
              </TableRow>
            ))
          ) : (
            <TableCell colSpan={4}>No Results</TableCell>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

SpeciesCorrectResults.propTypes = {
  results: PropTypes.array,
  onClick: PropTypes.func
};

export default SpeciesCorrectResults;
