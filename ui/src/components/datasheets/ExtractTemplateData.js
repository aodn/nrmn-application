import React, {useState, useEffect} from 'react';
import {
  Box,
  Button,
  CircularProgress,
  Divider,
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

const ExtractTemplateData = () => {
  const [areas, setAreas] = useState([]);
  const [filters, setFilters] = useState([]);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(false);

  const [entity, setEntity] = useState(null);
  const [downloadParams, setDownloadParams] = useState(null);

  const options = [
    {entity: 'locations', label: 'Location'},
    {entity: 'siteProvinces', label: 'MEOW Province'},
    {entity: 'siteStates', label: 'State'},
    {entity: 'siteCodes', label: 'Site Code'}
  ];

  useEffect(() => {
    const fetch = async () => {
      setLoading(true);
      const result = await getResult(entity);
      setLoading(false);
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
      setEntity(null);
    };

    const downloadZip = async (params) => {
      const response = await templateZip(qs.stringify(params, {indices: false}));
      FileDownload(response.data, `template.zip`);
      setDownloadParams(null);
    };

    if (downloadParams) {
      const p = options.reduce((a, o) => {
        a[o.entity] = downloadParams.filter((f) => f.type === o.entity).map((f) => f.value);
        return a;
      }, {});
      downloadZip({locations: p.locations, siteCodes: p.siteCodes, states: p.siteStates, provinces: p.siteProvinces});
    } else if (entity) {
      fetch();
    }
  }, [entity, downloadParams]);

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
              <TableCell>
                <Tooltip title="Remove" xs={1}>
                  <IconButton size="small" onClick={() => setFilters([...filters.filter((ff) => ff.value !== f.value)])}>
                    <RemoveCircleOutline />
                  </IconButton>
                </Tooltip>
              </TableCell>
              <Divider />
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
              onChange={(_, o) => setEntity(o.entity)}
              renderInput={(params) => <TextField {...params} variant="outlined" />}
            />
          </Grid>
          <Grid item xs={4}>
            <Typography variant="subtitle2">Area</Typography>
            {loading ? (
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
            <Button
              style={{width: '100%'}}
              onClick={() => setDownloadParams(filters)}
              disabled={downloadParams || filters.length < 1}
              color="secondary"
              variant="contained"
            >
              {downloadParams ? <CircularProgress size={25} /> : 'Download Sheets'}
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Grid>
  );
};

export default ExtractTemplateData;
