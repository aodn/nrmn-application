import {Box, Button, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useMemo, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';
import {PropTypes} from 'prop-types';
import CustomSearchInput from '../../input/CustomSearchInput';
import SpeciesCorrectGeometryFilter from './SpeciesCorrectGeometryFilter';

const SpeciesCorrectFilter = ({onSearch}) => {
  const [data, setData] = useState();
  const [countries, setCountries] = useState();
  const [states, setState] = useState();
  const [ecoRegions, setEcoRegions] = useState();

  const [locationChips, setLocationChips] = useState([]);
  const [countryLabels, setCountryLabels] = useState([]);
  const [stateLabels, setStateLabels] = useState([]);
  const [ecoRegionLabels, setEcoRegionLabels] = useState([]);

  const [enabledFilters, setEnabledFilters] = useState({ecoRegion: true, country: true, state: true});

  const [dataResponse, setDataResponse] = useState();

  const [loading, setLoading] = useState(true);

  const initialFilter = useMemo(() => {
    const d = new Date();
    const localTime = new Date(d.getTime() - d.getTimezoneOffset() * 60000);
    return {
      startDate: '1996-01-01',
      endDate: localTime.toISOString().slice(0, 10),
      country: null,
      state: null,
      ecoRegion: null,
      locationId: null,
      observableItemId: null,
      geometry: '',
      species: null,
      locationIds: []
    };
  }, []);

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations?pageSize=1000').then((res) => {
        const activeLocations = res.data.items.filter((i) => i.status === 'Active');
        setDataResponse(activeLocations);
        const locations = [];
        const locationIds = [];
        res.data.items
          .sort((a, b) => a.locationName.localeCompare(b.locationName))
          .forEach((d) => {
            locations[d.id] = d.locationName;
            locationIds.push(d.id);
          });
        setData({locations, locationIds});

        const groups = {ecoRegions: [], countries: [], areas: []};
        res.data.items.forEach((d) => {
          locations[d.id] = d.locationName;
          ['ecoRegions', 'countries', 'areas'].forEach((prop) => {
            d[prop]
              ?.split(',')
              .map((a) => a.trim())
              .forEach((a) => {
                groups[prop][a] = groups[prop][a] ? [...groups[prop][a], d.id] : [d.id];
              });
          });
        });
        setEcoRegions(groups.ecoRegions);
        setCountries(groups.countries);
        setState(groups.areas);

        const labels = res.data.items.reduce(
          (acc, cur) => {
            cur.countries?.split(',').forEach((country) => {
              country = country.trim();
              if (country && !acc.countries.includes(country)) acc.countries.push(country);
            });

            cur.areas?.split(',').forEach((area) => {
              area = area.trim();
              if (area && !acc.areas.includes(area)) acc.areas.push(area);
            });

            cur.ecoRegions?.split(',').forEach((ecoRegion) => {
              ecoRegion = ecoRegion.trim();
              if (ecoRegion && !acc.ecoRegions.includes(ecoRegion)) acc.ecoRegions.push(ecoRegion);
            });

            return acc;
          },
          {countries: [], areas: [], ecoRegions: []}
        );
        labels.countries.sort();
        setCountryLabels(labels.countries);
        labels.areas.sort();
        setStateLabels(labels.areas);
        labels.ecoRegions.sort();
        setEcoRegionLabels(labels.ecoRegions);
      });
      setLoading(false);
    }
    fetchLocations();
  }, []);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      if (!action) return {...initialFilter};
      const updated = {...filter};
      if (!action.value) {
        delete updated[action.field];
      } else {
        updated[action.field] = action.value;
      }

      var locationIds = [];
      locationIds = [updated['locationId']];
      if (states[updated['state']]) locationIds = [...locationIds, ...states[updated['state']]];
      if (countries[updated['country']]) locationIds = [...locationIds, ...countries[updated['country']]];
      if (ecoRegions[updated['ecoRegion']]) locationIds = [...locationIds, ...ecoRegions[updated['ecoRegion']]];

      updated['locationIds'] = [...new Set(locationIds.filter((d) => d))];

      const chips = dataResponse.filter((d) => locationIds.includes(d.id)).map((d) => ({id: d.id, locationName: d.locationName}));
      setLocationChips(chips);
      return updated;
    },
    {...initialFilter}
  );

  useEffect(() => {
    setEnabledFilters({
      ecoRegion: !filter.country && !filter.state && !filter.location,
      country: !filter.state && !filter.location,
      state: !filter.location
    });
  }, [filter]);

  const updateStartDate = (e) => {
    updateFilter({field: 'startDate', value: e.target.value});
  };

  const updateEndDate = (e) => {
    updateFilter({field: 'endDate', value: e.target.value});
  };

  const updateLocation = (e, value) => {
    updateFilter({field: 'locationId', value: value});
  };

  const updateCountry = (e, value) => {
    updateFilter({field: 'country', value: value});
  };

  const updateState = (e, value) => {
    updateFilter({field: 'state', value: value});
  };

  const updateGeometry = (value) => {
    updateFilter({field: 'geometry', value: value});
  };

  const updateEcoRegion = (e, value) => {
    updateFilter({field: 'ecoRegion', value: value});
  };

  const updateObservableItem = (e) => {
    updateFilter({field: 'observableItemId', value: e ? e.id : null});
    updateFilter({field: 'species', value: e ? e.species : null});
  };

  const canSearch =
    filter.startDate &&
    filter.endDate &&
    (filter.locationId || filter.country || filter.ecoRegion || filter.state || filter.observableItemId || filter.geometry);

  const filteredEcoRegionLabels =
    filter.locationIds.length > 0
      ? ecoRegionLabels.filter((d) => ecoRegions[d].some((id) => filter.locationIds.includes(id)))
      : ecoRegionLabels;
  const filteredCountryLabels =
    filter.locationIds.length > 0 ? countryLabels.filter((d) => countries[d].some((id) => filter.locationIds.includes(id))) : countryLabels;
  const filteredStateLabels =
    filter.locationIds.length > 0 ? stateLabels.filter((d) => states[d].some((id) => filter.locationIds.includes(id))) : stateLabels;

  return (
    <>
      {data?.locationIds && states && countries ? (
        <>
          <Box ml={1} display="flex" flexDirection="row">
            <Box m={1} width={150}>
              <Typography variant="subtitle2">Start Date</Typography>
              <input
                disabled={loading}
                max={filter.endDate}
                onChange={updateStartDate}
                style={{height: '35px', width: '150px'}}
                type="date"
                value={filter.startDate}
              />
            </Box>
            <Box m={1} width={150}>
              <Typography variant="subtitle2">End Date</Typography>
              <input
                disabled={loading}
                min={filter.startDate}
                onChange={updateEndDate}
                style={{height: '35px', width: '150px'}}
                type="date"
                value={filter.endDate}
              />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">EcoRegion</Typography>
              <Autocomplete
                disabled={loading || !enabledFilters.ecoRegion}
                filterSelectedOptions
                onChange={updateEcoRegion}
                options={filteredEcoRegionLabels}
                value={filter.ecoRegion}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} width={300}>
              <Typography variant="subtitle2">Country</Typography>
              <Autocomplete
                disabled={loading || !enabledFilters.country}
                filterSelectedOptions
                onChange={updateCountry}
                options={filteredCountryLabels}
                value={filter.country}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box m={1} width={300}>
              <CustomSearchInput fullWidth label="Species" formData={filter.species} onChange={updateObservableItem} />
            </Box>
          </Box>
          <Box ml={1} display="flex" flexDirection="row">
            <Box ml={1} width={300} mr={3}>
              <Typography variant="subtitle2">Geometry</Typography>
              <SpeciesCorrectGeometryFilter onChange={updateGeometry} filter={filter} />
            </Box>
            <Box mx={1} width={300}>
              <Typography variant="subtitle2">Area/State</Typography>
              <Autocomplete
                disabled={loading || !enabledFilters.state}
                filterSelectedOptions
                onChange={updateState}
                options={filteredStateLabels}
                value={filter.state}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box mx={1} width={300}>
              <Typography variant="subtitle2">Location</Typography>
              <Autocomplete
                disabled={loading}
                filterSelectedOptions
                getOptionLabel={(id) => data.locations[id]}
                value={filter.locationId}
                onChange={updateLocation}
                options={data.locationIds}
                renderInput={(params) => <TextField {...params} />}
                size="small"
              />
            </Box>
            <Box ml={1} mt={3} width={50}>
              <Button onClick={() => updateFilter()} fullWidth variant="outlined">
                Reset
              </Button>
            </Box>
            <Box ml={3} mr={1} my={3} width={220}>
              <LoadingButton disabled={!canSearch} onClick={() => onSearch(filter, locationChips)} fullWidth variant="contained">
                Search
              </LoadingButton>
            </Box>
          </Box>
        </>
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

SpeciesCorrectFilter.propTypes = {
  onSearch: PropTypes.func.isRequired
};

export default SpeciesCorrectFilter;
