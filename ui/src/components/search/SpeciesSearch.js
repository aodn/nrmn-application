import React, {useEffect, useState} from 'react';
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
import {search} from '../../axios/api';
import PropTypes from 'prop-types';
import LoadingButton from '@mui/lab/LoadingButton';

const SpeciesSearch = ({onRowClick}) => {
  const [tabIndex, setTabIndex] = useState(0);
  const [searchTerm, setSearchTerm] = useState(null);
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(20);
  const [gridData, setGridData] = useState(null);
  const [currentSearch, setCurrentSearch] = useState({});
  const [info, setInfo] = useState(null);

  const [searchRequested, setSearchRequested] = useState(null);
  const [searchError, setSearchError] = useState(null);

  const loading = searchRequested && !gridData && !searchError;

  const handleChange = (_, newValue) => {
    setSearchTerm(null);
    setGridData(null);
    setInfo(null);
    setSearchRequested(null);
    setCurrentSearch(null);
    setPage(0);
    setTabIndex(newValue);
  };

  useEffect(() => {
    if (!searchRequested) return;
    setInfo(null);
    setGridData(null);
    setSearchError(null);
    if (currentSearch?.species !== searchRequested.species) setPage(0);
    search(searchRequested)
      .then((res) => {
        if (res.data.error) {
          setSearchError(res.data.error);
          return;
        }
        setCurrentSearch(searchRequested);
        setGridData(
          res?.data
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
            : null
        );
      })
      .catch((err) => setSearchError(err.message));
  }, [searchRequested, currentSearch]);

  const handleChangePage = (_, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  return (
    <Box style={{background: 'white'}} boxShadow={1} width="90%">
      <Box pl={6} py={2}>
        <Typography variant="h4">Species Lookup</Typography>
      </Box>
      <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
        <Tabs value={tabIndex} onChange={handleChange}>
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
              size="small"
              disabled={loading}
              onChange={(e) => setSearchTerm(e.target.value.trim())}
              onKeyDown={(e) => {
                if (e.key === 'Enter') setSearchRequested({searchType: 'WORMS', species: searchTerm, includeSuperseded: true});
              }}
            />
          </Grid>
          <Grid item xs={1}></Grid>
          <Grid item xs={4}>
            <LoadingButton
              variant="outlined"
              disabled={!(searchTerm?.length > 3)}
              loading={loading}
              startIcon={<Search></Search>}
              onClick={() => {
                setPage(1);
                setSearchRequested({searchType: 'WORMS', species: searchTerm, includeSuperseded: true});
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
                if (e.key === 'Enter') setSearchRequested({searchType: 'NRMN', species: searchTerm, includeSuperseded: true});
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
              onClick={() => setSearchRequested({searchType: 'NRMN', species: searchTerm, includeSuperseded: true})}
              style={{textTransform: 'none'}}
            >
              Search NRMN
            </LoadingButton>
          </Grid>
        </Grid>
      </TabPanel>
      {gridData ? (
        <>
          <TableContainer component={Paper}>
            <Table size="small">
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
            component="div"
            rowsPerPageOptions={[]}
            count={gridData?.length ?? 0}
            rowsPerPage={rowsPerPage}
            page={page}
            onPageChange={handleChangePage}
            onRowsPerPageChange={handleChangeRowsPerPage}
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
