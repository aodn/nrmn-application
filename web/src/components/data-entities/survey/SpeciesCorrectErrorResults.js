import React from 'react';
import {PropTypes} from 'prop-types';
import {makeStyles} from '@mui/styles';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TableCell from '@mui/material/TableCell';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';

const useStyles = makeStyles(({palette, typography}) => ({
  root: {
    '& .MuiTable-root': {
      '& .MuiTableHead-root': {
        '& .MuiTableRow-head': {
          '& .MuiTableCell-head': {
            fontSize: typography?.table.fontSize,
            background: palette?.primary.rowHeader,
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

const SpeciesCorrectErrorResults = ({correctionErrors}) => {
  const classes = useStyles();

  return(
    <TableContainer classes={classes} data-testid='species-correct-error-result'>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell colSpan={3} style={{color: 'red', textAlign: 'center', fontSize: 'medium'}}>{correctionErrors.message}</TableCell>
          </TableRow>
          <TableRow>
            <TableCell width="30%">Original Species Name</TableCell>
            <TableCell width="30%">Target Species Name</TableCell>
            <TableCell width="40%">Problem Survey Id</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          { correctionErrors.surveyIds.length > 0 ? (
              correctionErrors.surveyIds.map((r) => (
                <TableRow key={'surveyid-' + r} style={{ cursor: 'pointer' }}>
                  <TableCell>{correctionErrors.currentSpeciesName}</TableCell>
                  <TableCell>{correctionErrors.nextSpeciesName}</TableCell>
                  <TableCell onClick={() => window.open(`/data/survey/${r}/edit`, '_blank').focus()} style={{textDecoration: 'underline dotted'}}>{r}</TableCell>
                </TableRow>
              ))
          ) : (
              <TableRow>
                <TableCell colSpan={3}>{correctionErrors.messages}</TableCell>
              </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

SpeciesCorrectErrorResults.propTypes = {
  correctionErrors: PropTypes.object,
};

export default SpeciesCorrectErrorResults;