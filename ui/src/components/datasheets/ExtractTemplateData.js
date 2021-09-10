import React, {useState, useEffect} from 'react';
import {
  Box,
  Button,
  CircularProgress,
  Grid,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography
} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {RemoveCircleOutline, AddCircleOutline} from '@material-ui/icons';

import qs from 'qs';
import FileDownload from 'js-file-download';

import {getResult, templateZip} from '../../axios/api';

const options = [
  {entity: 'locations', label: 'Location'},
  {entity: 'siteProvinces', label: 'MEOW Province'},
  {entity: 'siteStates', label: 'State'},
  {entity: 'siteCodes', label: 'Site Code'},
  {entity: 'countries', label: 'Country'}
];

const ExtractTemplateData = () => {
  const [areas, setAreas] = useState([]);
  const [filters, setFilters] = useState([]);
  const [selected, setSelected] = useState(null);

  const [entity, setEntity] = useState(null);
  const [downloadParams, setDownloadParams] = useState(null);

  const downloadZip = (params) => {
    templateZip(qs.stringify(params, {indices: false})).then((result) => {
      FileDownload(result.data, `template.zip`);
      setDownloadParams(null);
    });
  };

  useEffect(() => {
    if (entity)
      getResult(entity).then((result) => {
        if (result.data) {
          if (entity === 'locations')
            setAreas(
              result.data._embedded.locations.map((l) => {
                return {type: entity, value: l.locationId, label: l.locationName};
              })
            );
          else
            setAreas(
              result.data.map((c) => {
                return {type: entity, value: c, label: c};
              })
            );
        }
      });
  }, [entity]);

  useEffect(() => {
    if (downloadParams) {
      const p = options.reduce((a, o) => {
        a[o.entity] = downloadParams.filter((f) => f.type === o.entity).map((f) => f.value);
        return a;
      }, {});
      downloadZip({
        locations: p.locations,
        siteCodes: p.siteCodes,
        states: p.siteStates,
        countries: p.countries,
        provinces: p.siteProvinces
      });
    }
  }, [downloadParams]);

  const filterTable = (
    <TableContainer component={Paper}>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Filter Options</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {filters.map((f) => (
            <TableRow key={f.value}>
              <TableCell>{options.find((o) => o.entity === f.type).label}</TableCell>
              <TableCell>{f.label}</TableCell>
              <TableCell style={{borderBottom: 'grey'}}>
                <Tooltip title="Remove" xs={1}>
                  <IconButton size="small" onClick={() => setFilters([...filters.filter((ff) => ff.value !== f.value)])}>
                    <RemoveCircleOutline />
                  </IconButton>
                </Tooltip>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );

  return (
    <Grid container justify="center">
      <Box width={900} boxShadow={1} padding={4} bgcolor="white">
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <Typography variant="h4">Template Data</Typography>
          </Grid>
          <Grid item xs={2} />
          <Grid item xs={4}>
            <Typography variant="subtitle2">Area Type</Typography>
            <Autocomplete
              disableClearable
              filterSelectedOptions
              options={options}
              getOptionLabel={(o) => o.label}
              getOptionSelected={(o, v) => o.entity === v.entity}
              onChange={(_, o) => {
                setAreas(null);
                setEntity(o.entity);
              }}
              renderInput={(params) => <TextField {...params} variant="outlined" />}
            />
          </Grid>
          <Grid item xs={4}>
            <Typography variant="subtitle2">Area</Typography>
            {!areas ? (
              <CircularProgress size={25} />
            ) : (
              <Autocomplete
                filterSelectedOptions
                options={areas}
                getOptionLabel={(o) => o.label}
                onChange={(_, o) => setSelected(o)}
                renderInput={(params) => <TextField {...params} variant="outlined" />}
              />
            )}
          </Grid>
          <Grid item xs={1}>
            <Box pt={3} ml={-2}>
              <IconButton
                disabled={!selected}
                onClick={() => {
                  if (selected && filters.filter((f) => f.value === selected.value).length < 1) setFilters([selected, ...filters]);
                }}
              >
                <AddCircleOutline />
              </IconButton>
            </Box>
          </Grid>
          <Grid item xs={2} />
          <Grid item xs={8}>
            {filters.length > 0 && filterTable}
          </Grid>
          <Grid item xs={4} />
          <Grid item xs={4}>
            <Button style={{width: '100%'}} onClick={() => setDownloadParams(filters)} disabled={downloadParams || filters.length < 1}>
              {downloadParams ? <CircularProgress size={25} /> : 'Download Sheets'}
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Grid>
  );
};

export default ExtractTemplateData;
