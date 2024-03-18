import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow} from '@mui/material';
import { styled } from '@mui/material/styles';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';
import React from 'react';
import PropTypes from 'prop-types';

const PREFIX = 'SurveyDiff';

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


  return (
      <StyledTableContainer classes={classes} component={Paper} disabled>
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
              <TableCell>Species</TableCell>
              <TableCell>Column</TableCell>
              <TableCell>Old Value</TableCell>
              <TableCell>New Value</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {surveyDiff?.cellDiffs?.sort((a,b) => (a.columnName - b.columnName)).map((res, idx) => {
              return (
                  <TableRow key={idx}>
                    <TableCell>{res.diffRowId}</TableCell>
                    <TableCell>{res.speciesName}</TableCell>
                    <TableCell>{res.columnName}</TableCell>
                    <TableCell>{res.oldValue}</TableCell>
                    <TableCell>{res.newValue}</TableCell>
                  </TableRow>
              );
            })}
          </TableBody>
        </Table>
      </StyledTableContainer>
  );
};

export default SurveyDiff;

SurveyDiff.propTypes = {
  surveyDiff: PropTypes.any
};