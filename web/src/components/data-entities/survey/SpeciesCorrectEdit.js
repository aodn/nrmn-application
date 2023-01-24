import React, {useEffect} from 'react';

import {Button, Box} from '@mui/material';

import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Close';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import PropTypes from 'prop-types';
import CustomCheckboxInput from '../../input/CustomCheckboxInput';
import {useState} from 'react';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TablePagination from '@mui/material/TablePagination';
import CustomSearchInput from '../../input/CustomSearchInput';
import {searchSpecies} from '../../../api/api';
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

const SpeciesCorrectEdit = ({selected}) => {
  const classes = useStyles();
  const pageSize = 10;
  const [page, setPage] = useState(0);
  const [detail, setDetail] = useState();
  const [surveys, setSurveys] = useState();
  const [loading, setLoading] = useState(false);
  const [correction, setCorrection] = useState({
    oldObservableItemId: selected.result.observableItemId,
    newObservableItemId: null,
    newObservableItemName: null,
    invertCondition: false,
    surveyIds: []
  });

  useEffect(() => {
    if (selected?.filter) {
      setLoading(true);
      searchSpecies(selected.filter).then((res) => {
        const detail = res.data
          .map((v) => ({locationName: v.locationName, siteName: v.siteName, surveyIds: JSON.parse(v.surveyIds)}))
          .reduce((p, v) => {
            const location = p.find((l) => l.locationName === v.locationName);
            if (location) {
              location.sites.push({siteName: v.siteName, surveyIds: v.surveyIds});
            } else {
              p.push({locationName: v.locationName, sites: [{siteName: v.siteName, surveyIds: v.surveyIds}]});
            }
            return p;
          }, []);
        setDetail(detail);
      });
    }
  }, [selected]);

  useEffect(() => {
    if (detail) {
      setSurveys(detail);
      setLoading(false);
    }
  }, [detail]);

  useEffect(() => {
    if (!surveys) return;
    const surveyIds = surveys.reduce((p, v) => {
      v.sites.forEach((s) => {
        p.push(...s.surveyIds);
      });
      return p;
    }, []);
    setCorrection({...correction, surveyIds});
  }, [surveys, setCorrection, correction]);

  return (
    <Box border={1} borderRadius={1} m={1} p={2} borderColor="divider">
      <Box maxWidth={500}>
        <Typography variant="subtitle2">Current species name</Typography>
        <TextField fullWidth color="primary" size="small" value={selected.result.observableItemName} spellCheck={false} readOnly />
        <Typography variant="subtitle2">Correct to</Typography>
        <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
          <CustomSearchInput
            fullWidth
            formData={correction?.newObservableItemName}
            exclude={selected.result.observableItemName}
            onChange={(t) => {
              if (t) {
                setCorrection({...correction, newObservableItemId: t.id, newObservableItemName: t.species});
              } else {
                setCorrection({...correction, newObservableItemId: null, newObservableItemName: null});
              }
            }}
          />
        </Box>
      </Box>
      {loading ? (
        <Typography variant="caption">Loading Surveys...</Typography>
      ) : (
        <>
          <Box>
            <CustomCheckboxInput label="Invert Correction" field="invertCondition" />
          </Box>
          <Box>
            <Button variant="contained" disabled={!correction?.newObservableItemName}>
              Submit Correction
            </Button>
          </Box>
          <TableContainer key={selected.result.observableItemId} classes={classes} component={Paper} disabled>
            <TablePagination
              component="div"
              rowsPerPageOptions={[]}
              count={surveys?.length || 0}
              rowsPerPage={pageSize}
              page={page}
              onPageChange={(e, p) => setPage(p)}
            />
            <Table>
              <TableHead>
                <TableRow>
                  <TableCell width="25%">Location</TableCell>
                  <TableCell width="50%">Site</TableCell>
                  <TableCell width="25%">SurveyIDs</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {surveys?.slice(page * pageSize, page * pageSize + pageSize).map((d) => {
                  return (
                    <TableRow key={d.locationName} borderBottom={1} borderColor="divider">
                      <TableCell>
                        <IconButton size="small" onClick={() => setSurveys(surveys.filter((v) => v.locationName !== d.locationName))}>
                          <DeleteIcon fontSize="inherit" />
                        </IconButton>
                        <Typography variant="caption" sx={{fontWeight: 'medium'}}>
                          {d.locationName}{' '}
                        </Typography>
                      </TableCell>

                      <TableCell>
                        {d.sites.map((s) => {
                          return (
                            <Box key={s.siteName}>
                              <Typography
                                variant="bold"
                                color="primary"
                                style={{cursor: 'pointer'}}
                                onClick={() => {
                                  setSurveys(
                                    surveys.map((v) => {
                                      if (v.locationName === d.locationName) {
                                        v.sites = v.sites.filter((v) => v.siteName !== s.siteName);
                                      }
                                      return v;
                                    })
                                  );
                                }}
                              >
                                {'⨯ '}
                              </Typography>
                              <Typography variant="caption">{s.siteName}</Typography>
                            </Box>
                          );
                        })}
                      </TableCell>
                      <TableCell>
                        {d.sites.map((s) => (
                          <Box key={s.surveyIds[0]}>
                            <Typography variant="caption">{s.surveyIds.length}</Typography>
                          </Box>
                        ))}
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
            <TablePagination
              component="div"
              rowsPerPageOptions={[]}
              count={surveys?.length || 0}
              rowsPerPage={pageSize}
              page={page}
              onPageChange={(e, p) => setPage(p)}
            />
          </TableContainer>
        </>
      )}
    </Box>
  );
};

export default SpeciesCorrectEdit;

SpeciesCorrectEdit.propTypes = {
  selected: PropTypes.object
};
