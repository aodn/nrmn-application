import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import {makeStyles} from '@mui/styles';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import React from 'react';
import PropTypes from 'prop-types';

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

const SurveyDiff = ({surveyDiff}) => {
  const classes = useStyles();

  return (
    <TableContainer classes={classes} component={Paper} disabled>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Rows Deleted</TableCell>
            <TableCell>{surveyDiff?.deletedRows?.length}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Rows Inserted</TableCell>
            <TableCell>{surveyDiff?.insertedRows?.length}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell>Row Changed</TableCell>
            <TableCell>Column</TableCell>
            <TableCell>Old Value</TableCell>
            <TableCell>New Value</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {surveyDiff?.cellDiffs?.map((res, idx) => {
            return (
              <TableRow key={idx}>
                <TableCell>{res.diffRowId}</TableCell>
                <TableCell>{res.columnName}</TableCell>
                <TableCell>{res.oldValue}</TableCell>
                <TableCell>{res.newValue}</TableCell>
              </TableRow>
            );
          })}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default SurveyDiff;

SurveyDiff.propTypes = {
  surveyDiff: PropTypes.any
};
