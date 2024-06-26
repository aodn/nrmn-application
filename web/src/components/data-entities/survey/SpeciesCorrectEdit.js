import React, {useCallback, useEffect, useState} from 'react';

import {styled} from '@mui/material/styles';

import {Box, Button, LinearProgress, Paper} from '@mui/material';

import IconButton from '@mui/material/IconButton';
import DeleteIcon from '@mui/icons-material/Close';
import TextField from '@mui/material/TextField';
import Typography from '@mui/material/Typography';
import PropTypes from 'prop-types';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TablePagination from '@mui/material/TablePagination';
import CustomSearchFilterInput from '../../input/CustomSearchFilterInput';
import {postSpeciesCorrection, searchSpecies} from '../../../api/api';

const StyledBox = styled(Box)(({
                                 theme: {palette, typography}
                               }) => ({
  [`& .SpeciesCorrectEdit-root`]: {
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

const SpeciesCorrectEdit = ({selected, onSubmit, onError}) => {
  const initialCorrectionState = {
    prevObservableItemId: selected.result.observableItemId,
    newObservableItemId: null,
    newObservableItemName: null,
    invertCondition: false,
    surveyIds: []
  };

  const pageSize = 10;
  const [page, setPage] = useState(0);
  const [detail, setDetail] = useState();
  const [surveys, setSurveys] = useState();
  const [loading, setLoading] = useState(false);
  const [correction, setCorrection] = useState({...initialCorrectionState});

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
    setCorrection((c) => ({...c, surveyIds}));
  }, [surveys]);

  const updateCorrection =  useCallback((t) => {
    setCorrection(c => ({...c, newObservableItemId: t?.id, newObservableItemName: t?.species}));
  }, [setCorrection]);

  return (
      <StyledBox border={1} borderRadius={1} m={1} p={2} borderColor="divider">
        <Box display="flex" flexDirection="row">
          <Box flex={1} maxWidth={500} m={1}>
            <Typography variant="subtitle2">Current species name</Typography>
            <TextField fullWidth color="primary" size="small" value={selected.result.observableItemName} spellCheck={false} readOnly />
          </Box>
          <Box flex={1} maxWidth={500} m={1}>
            <Typography variant="subtitle2">Correct to</Typography>
            <Box flexDirection={'row'} display={'flex'} alignItems={'center'}>
              <CustomSearchFilterInput
                  dataTestId='species-correction-to'
                  fullWidth
                  formData={correction?.newObservableItemName}
                  exclude={selected.result.observableItemName}
                  onChange={updateCorrection}
              />
            </Box>
          </Box>
        </Box>
        {loading ? (
            <LinearProgress />
        ) : (
            <>
              <Box display="flex" flexDirection="row-reverse">
                <Box width={200} m={2}>
                  <Button
                      variant="contained"
                      data-testid='submit-correction-button'
                      disabled={!correction?.newObservableItemName || correction.surveyIds.length < 1}
                      onClick={() => {
                        setLoading(true);
                        const postDto = {...correction, filterSet: selected.filter};
                        postSpeciesCorrection(postDto)
                            .then((res) => {
                              if(res.status === 200) {
                                setCorrection({ ...initialCorrectionState});
                                onSubmit(res.data);
                              }
                            })
                            .catch((err) => {
                              onError(err);
                            });
                      }}
                  >
                    Submit Correction
                  </Button>
                </Box>
              </Box>
              <TableContainer key={selected.result.observableItemId} classes={{
                root: `SpeciesCorrectEdit-root`
              }} component={Paper} disabled>
                <TablePagination
                    showFirstButton
                    showLastButton
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
                      <TableCell width="25%">Surveys</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {surveys?.slice(page * pageSize, page * pageSize + pageSize).map((d) => {
                      return (
                          <TableRow key={d.locationName}>
                            <TableCell style={{verticalAlign: 'top'}}>
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
                    showFirstButton
                    showLastButton
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
      </StyledBox>
  );
};

export default SpeciesCorrectEdit;

SpeciesCorrectEdit.propTypes = {
  selected: PropTypes.object,
  onSubmit: PropTypes.func,
  onError: PropTypes.func,
};