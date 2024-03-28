import React, {useState, useCallback} from 'react';
import {Box, Divider, Paper, Grid, Tab, Tabs, TextField, Typography} from '@mui/material';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import TablePagination from '@mui/material/TablePagination';
import {Search} from '@mui/icons-material';
import Alert from '@mui/material/Alert';
import TabPanel from '../containers/TabPanel';
import {search} from '../../api/api';
import PropTypes from 'prop-types';
import LoadingButton from '@mui/lab/LoadingButton';
import { AppConstants } from '../../common/constants';


const SpeciesSearch = ({onRowClick}) => {
  const rowsPerPage = 50;

  const [tabIndex, setTabIndex] = useState(0);
  const [searchTerm, setSearchTerm] = useState();
  const [page, setPage] = useState(0);
  const [gridData, setGridData] = useState();
  const [lastSearch, setLastSearch] = useState({});
  const [info, setInfo] = useState();
  const [maxRows, setMaxRows] = useState(-1);
  const [searchError, setSearchError] = useState();
  const [searching, setSearching] = useState(false);

  const loading = searching && !gridData && !searchError;

  const handleTabChange = useCallback((_, newValue) => {
    setSearchTerm(null);
    setGridData(null);
    setInfo(null);
    setLastSearch(null);
    setPage(0);
    setMaxRows(-1);
    setTabIndex(newValue);
  }, []);

  const doSearch = (request) => {
    if (lastSearch?.species === request?.species && lastSearch?.page === request?.page) {
      // If the search is the same as previous, then no need to execute
      return;
    } else {
      setInfo(null);
      setSearchError(null);

      if (lastSearch?.species !== request.species) {
        // This is a new search, not change page.
        setGridData(null);
        setMaxRows(-1);
      }

      setSearching((prevValue) => !prevValue);

      // Add one more to test if we have next page or reach the end
      search({...request, pageSize: 51})
          .then((res) => {
            if (res.data.error) {
              setSearchError(res.data.error);
              return;
            }
            setLastSearch(request);

            let data = res?.data
                ? res.data.map((r, id) => {
                  // if not a generic name then remove the genus from the species to produce the species epithet
                  let speciesEpithet = '';
                  if (r.species) {
                    const isGenericName =
                        r.species.toUpperCase().includes('SP.') ||
                        r.species.toUpperCase().includes('SPP.') ||
                        r.species.includes('(') ||
                        r.species.includes('[') ||
                        !r.species.includes(' ');
                    if (!isGenericName) speciesEpithet = r.species.replace(`${r.genus} `, '');
                  }
                  return {id: id, ...r, speciesEpithet};
                })
                : null;

            if (data?.length === 0) {
              setGridData((prevValue) => (prevValue ? null : []));
            }

            // Must use temp variable due to slight time lag in update
            let i = data?.length === 51;
            if (i) {
              // We do not need the last item for now
              data = data.slice(0, -1);
            }

            if (data?.length > 0) {
              setGridData((prevValue) => {
                const k = prevValue ? [...prevValue, ...data] : data;
                setMaxRows(i ? -1 : k?.length);

                return k;
              });
              setPage(request.page);
            }
          })
          .catch((err) => setSearchError(err.message))
          .finally(() => setSearching((prevValue) => !prevValue));
    }
  };

  const handleChangePage = (_, newPage) => {
    if (maxRows < 0 && page < newPage && gridData.length < (newPage + 1) * rowsPerPage) {
      doSearch({
        searchType: tabIndex === 0 ? 'WORMS' : 'NRMN',
        species: searchTerm,
        includeSuperseded: true,
        page: newPage
      });
    } else {
      setPage(newPage);
    }
  };

  return (
      <Box style={{background: 'white'}} boxShadow={1} width="90%">
        <Box pl={6} py={2}>
          <Typography variant="h4">Species Lookup</Typography>
        </Box>
        <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
          <Tabs value={tabIndex} onChange={handleTabChange}>
            <Tab label="WoRMS" style={{minWidth: '50%', textTransform: 'none'}} />
            <Tab label="NRMN" style={{minWidth: '50%'}} />
          </Tabs>
        </Box>
        {searchError ? (
            <Box pt={2} mx={2}>
              <Alert severity="error" variant="filled">
                The server may be experiencing problems. Please wait a moment and try again. (Error: {searchError})
              </Alert>
            </Box>
        ) : null}
        <TabPanel value={tabIndex} index={0}>
          <Typography variant="subtitle2">Scientific Name</Typography>
          <Grid container direction="row" alignItems="center">
            <Grid item xs={5}>
              <TextField
                  fullWidth
                  data-testid="worm-search-field"
                  placeholder="WoRMS Search"
                  size="small"
                  disabled={loading}
                  onChange={(e) => setSearchTerm(e.target.value.trim())}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter' && searchTerm?.length > AppConstants.Filter.MIN_CHAR_LENGTH_FOR_WORM_SEARCH) {
                      doSearch({ searchType: 'WORMS', species: searchTerm, includeSuperseded: true, page: 0 });
                    }
                  }}
              />
            </Grid>
            <Grid item xs={1}></Grid>
            <Grid item xs={4}>
              <LoadingButton
                  data-testid="search-button"
                  variant="outlined"
                  disabled={!(searchTerm?.length > AppConstants.Filter.MIN_CHAR_LENGTH_FOR_WORM_SEARCH)}
                  loading={loading}
                  startIcon={<Search></Search>}
                  onClick={() => {
                    doSearch({searchType: 'WORMS', species: searchTerm, includeSuperseded: true, page: 0});
                  }}
                  style={{textTransform: 'none'}}
              >
                Search WoRMS
              </LoadingButton>
            </Grid>
          </Grid>
        </TabPanel>
        {info ? (
            <Box pt={2}>
              <Alert severity="info" variant="filled">
                {info}
              </Alert>
            </Box>
        ) : null}
        <TabPanel value={tabIndex} index={1}>
          <Typography variant="subtitle2">Scientific Name</Typography>
          <Grid container direction="row" alignItems="center">
            <Grid item xs={5}>
              <TextField
                  fullWidth
                  size="small"
                  disabled={loading}
                  onKeyDown={(e) => {
                    if (e.key === 'Enter') doSearch({searchType: 'NRMN', species: searchTerm, includeSuperseded: true, page: 0});
                  }}
                  onChange={(e) => setSearchTerm(e.target.value.trim())}
              />
            </Grid>
            <Grid item xs={1}></Grid>
            <Grid item xs={3}>
              <LoadingButton
                  variant="outlined"
                  disabled={loading || !(searchTerm?.length > 3)}
                  loading={loading}
                  startIcon={<Search></Search>}
                  onClick={() => doSearch({searchType: 'NRMN', species: searchTerm, includeSuperseded: true, page: 0})}
                  style={{textTransform: 'none'}}
              >
                Search NRMN
              </LoadingButton>
            </Grid>
          </Grid>
        </TabPanel>
        {gridData ? (
            <>
              <TableContainer component={Paper} disabled sx={{
                '& .MuiTable-root': {
                  '& .MuiTableHead-root': {
                    '& .MuiTableRow-head': {
                      '& .MuiTableCell-head': {
                        fontSize: (theme) => theme.typography.table?.fontSize,
                        background: (theme) => theme?.palette.primary?.rowHeader
                      }
                    }
                  },
                  '& .MuiTableRow-root': {
                    '&:nth-child(even)': {
                      backgroundColor: (theme) => theme?.palette.primary?.rowHighlight
                    }
                  },
                  '& .MuiTableCell-root': {
                    fontSize: (theme) => theme?.typography.table?.fontSize,
                    padding: (theme) => theme?.typography.table?.padding + 'px',
                    background: (theme) => theme?.palette.text?.textPrimary
                  }
                }
              }}>
                <Table>
                  <TableHead>
                    <TableRow>
                      <TableCell>Is Present</TableCell>
                      <TableCell>Status</TableCell>
                      <TableCell>Species</TableCell>
                      <TableCell>Phylum</TableCell>
                      <TableCell>Family</TableCell>
                      <TableCell>Class</TableCell>
                      <TableCell>Order</TableCell>
                      <TableCell>Genus</TableCell>
                      <TableCell>Species Epithet</TableCell>
                    </TableRow>
                  </TableHead>
                  <TableBody>
                    {gridData?.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage).map((row) => (
                        <TableRow
                            key={row.aphiaId}
                            style={{cursor: 'pointer'}}
                            onClick={() => {
                              const supersededBy = row.supersededBy;
                              const unacceptReason = row.unacceptReason;
                              const isPresent = row.isPresent;
                              if (supersededBy) {
                                setInfo(
                                    `This species has been superseded by ${supersededBy}` +
                                    (unacceptReason != null ? ` (Reason: ${unacceptReason})` : '')
                                );
                              } else if (isPresent) {
                                setInfo('This species name exists in the NRMN database');
                              } else {
                                setInfo();
                                onRowClick({
                                  aphiaId: row.aphiaId,
                                  phylum: row.phylum,
                                  family: row.family,
                                  class: row.class,
                                  order: row.order,
                                  genus: row.genus,
                                  speciesEpithet: row.speciesEpithet
                                });
                              }
                            }}
                        >
                          <TableCell>{row.isPresent ? 'True' : 'False'}</TableCell>
                          <TableCell>{row.status}</TableCell>
                          {row.supersededBy ? (
                              <TableCell>
                                <i>{row.species}</i>
                              </TableCell>
                          ) : (
                              <TableCell>{row.species}</TableCell>
                          )}
                          <TableCell>{row.phylum}</TableCell>
                          <TableCell>{row.family}</TableCell>
                          <TableCell>{row.class}</TableCell>
                          <TableCell>{row.order}</TableCell>
                          <TableCell>{row.genus}</TableCell>
                          <TableCell>{row.speciesEpithet}</TableCell>
                        </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </TableContainer>
              <TablePagination
                  showFirstButton
                  showLastButton
                  component="div"
                  rowsPerPageOptions={[]}
                  count={maxRows}
                  rowsPerPage={rowsPerPage}
                  page={page}
                  onPageChange={handleChangePage}
              />
            </>
        ) : (
            <Divider />
        )}
      </Box>
  );
};

SpeciesSearch.propTypes = {
  onRowClick: PropTypes.func
};

export default SpeciesSearch;