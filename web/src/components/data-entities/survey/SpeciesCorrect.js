import {Box, Button, Chip, IconButton, LinearProgress, TextField, Typography, Paper} from '@mui/material';
import React, {useEffect, useState, useReducer} from 'react';
import {getSurveySpecies} from '../../../api/api';
import {makeStyles} from '@mui/styles';
import SpeciesCorrectFilter from './SpeciesCorrectFilter';
import {PropTypes} from 'prop-types';
import CustomSearchInput from '../../input/CustomSearchInput';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
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
                <TableCell>{r.surveyIds.length}</TableCell>
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

const SpeciesCorrect = () => {
  const [selected, setSelected] = useState(null);
  const [correction, setCorrection] = useState(null);

  const [request, dispatch] = useReducer((state, action) => {
    switch (action.type) {
      case 'getRequest':
        return {loading: true, results: null, request: action.payload};
      case 'showResults': {
        const results = action.payload.map((p) => ({...p, surveyIds: JSON.parse(p.surveyIds)}));
        return {loading: false, request: null, results};
      }
      default:
        return state;
    }
  }, {});

  useEffect(() => {
    if (request.request)
      getSurveySpecies(request.request).then((res) => {
        dispatch({type: 'showResults', payload: res.data});
      });
  }, [request.request]);

  const detail = request.results?.find((r) => r.observableItemId === selected);

  return (
    <>
      <Box p={1}>
        <Typography variant="h4">Correct Species</Typography>
      </Box>
      <SpeciesCorrectFilter onSearch={(filter) => dispatch({type: 'getRequest', payload: filter})} />
      {request.loading && <LinearProgress />}
      {request.results && (
        <Box display="flex" flex={2} overflow="hidden" flexDirection="row">
          <Box width="50%" style={{overflowX: 'hidden', overflowY: 'auto'}}>
            <SpeciesCorrectResults
              results={request.results}
              onClick={(r) => {
                setSelected(r);
              }}
            />
          </Box>
          {detail && (
            <Box width="50%" style={{overflowX: 'hidden', overflowY: 'auto'}}>
              <Box m={1}>
                <Typography variant="subtitle2">Current species name</Typography>
                <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
                  <TextField fullWidth color="primary" size="small" value={detail.observableItemName} spellCheck={false} readOnly />
                  <IconButton
                    style={{marginLeft: 5, marginRight: 15}}
                    onClick={() => window.open(`/reference/observableItem/${detail.observableItemId}`, '_blank').focus()}
                  >
                    <LaunchIcon />
                  </IconButton>
                </Box>
              </Box>
              <Box m={1}>
              <Typography variant="subtitle2">Correct to</Typography>
                <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
                  <CustomSearchInput
                    fullWidth
                    formData={correction?.newObservableItemName}
                    exclude={detail.observableItemName}
                    onChange={(t) => {
                      if(t) {
                      setCorrection({...request.request, newObservableItemId: t.id, newObservableItemName: t.species});
                      } else {
                        setCorrection({...request.request});
                      }
                    }}
                  />
                  <IconButton
                    style={{marginLeft: 5, marginRight: 15}}
                    disabled={!correction?.newObservableItemId}
                    onClick={() => window.open(`/reference/observableItem/${correction.newObservableItemId}`, '_blank').focus()}
                  >
                    <LaunchIcon />
                  </IconButton>
                </Box>
              </Box>
              <Box m={1}>
                <Button variant="contained" disabled={!correction?.newObservableItemName} onClick={() => setSelected(null)}>
                  Submit Correction
                </Button>
              </Box>
              <Box m={1} key={detail.observableItemId}>
                <Typography variant="subtitle2">Surveys to correct</Typography>
                <Box m={1}>
                  {detail?.surveyIds.map((id) => (
                    <Chip
                      key={`${detail.observableItemId}-${id}`}
                      label={id}
                      style={{margin: 5}}
                      onClick={() => window.open(`/data/survey/${id}`, '_blank').focus()}
                      clickable
                    />
                  ))}
                </Box>
              </Box>
            </Box>
          )}
        </Box>
      )}
    </>
  );
};

export default SpeciesCorrect;
