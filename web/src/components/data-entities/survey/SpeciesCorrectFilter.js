import {Box, Button, Chip, LinearProgress, TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import React, {useEffect, useCallback, useMemo, useReducer, useState} from 'react';
import {getEntity} from '../../../api/api';
import LoadingButton from '@mui/lab/LoadingButton';
import {PropTypes} from 'prop-types';
import CustomSearchInput from '../../input/CustomSearchInput';
import SpeciesCorrectGeometryFilter from './SpeciesCorrectGeometryFilter';

const SpeciesCorrectFilter = ({onSearch, onLoadLocations}) => {
  const [data, setData] = useState();
  const [countries, setCountries] = useState();
  const [states, setState] = useState();
  const [ecoRegions, setEcoRegions] = useState();

  const [locations, setLocations] = useState([]);
  const [countryLabels, setCountryLabels] = useState([]);
  const [stateLabels, setStateLabels] = useState([]);
  const [ecoRegionLabels, setEcoRegionLabels] = useState([]);

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
      observableItemId: null,
      geometry: null,
      species: null,
      locationIds: []
    };
  }, []);

  useEffect(() => {
    async function fetchLocations() {
      await getEntity('locations?all=true').then((res) => {
        const locations = [];
        const locationIds = [];
        res.data
          .filter((i) => i.status === 'Active')
          .sort((a, b) => a.locationName.localeCompare(b.locationName))
          .forEach((d) => {
            locations[d.id] = d.locationName;
            locationIds.push(d.id);
          });
        setData({locations, locationIds});
        const groups = {ecoRegions: [], countries: [], areas: []};
        res.data.forEach((d) => {
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
        setLocations(locations);
        setEcoRegions(groups.ecoRegions);
        setCountries(groups.countries);
        setState(groups.areas);

        const labels = res.data.reduce(
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
    if(locations.length === 0)
      fetchLocations();
  }, [locations.length]);

  useEffect(() => {
    if (data?.locations && onLoadLocations) {
      onLoadLocations(data.locations);
    }
  }, [data, onLoadLocations]);

  const [filter, updateFilter] = useReducer(
    (filter, action) => {
      if (!action) return {...initialFilter};
      const updated = {...filter};
      if (action.action) {
        switch (action.action) {
          case 'addAllLocations':
            updated['locationIds'] = [...filter.locationIds, ...new Set(filter.stagedLocationIds.filter((d) => d))];
            break;
          case 'addLocation':
            updated['locationIds'] = [...filter.locationIds, action.id];
            break;
          case 'removeLocation':
            updated['locationIds'] = [...filter.locationIds.filter((l) => l !== action.id)];
            break;
          default:
            break;
        }
      } else if (!action.value) {
        delete updated[action.field];
      }
      updated[action.field] = action.value;

      const set_1 = new Set(states[updated['state']]);
      const set_2 = new Set(countries[updated['country']]);
      const set_3 = new Set(ecoRegions[updated['ecoRegion']]);

      var intersect = new Set([...set_1, ...set_2, ...set_3]);
      if (set_1.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_1.has(i)));
      if (set_2.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_2.has(i)));
      if (set_3.size > 0) intersect = new Set(Array.from(intersect).filter((i) => set_3.has(i)));

      var intersect_arr = Array.from(intersect);
      intersect_arr.filter((l) => !filter.locationIds.includes(l));
      updated['stagedLocationIds'] = [...new Set(intersect_arr.filter((d) => d))];

      return updated;
    },
    {...initialFilter}
  );

  const updateStartDate = (e) => {
    updateFilter({field: 'startDate', value: e.target.value});
  };

  const updateEndDate = (e) => {
    updateFilter({field: 'endDate', value: e.target.value});
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

  const updateObservableItem = useCallback((e) => {
    updateFilter({field: 'observableItemId', value: e ? e.id : null});
    updateFilter({field: 'species', value: e ? e.species : null});
  }, [updateFilter]);

  const canSearch = filter.startDate && filter.endDate && (filter.locationIds.length > 0 || filter.observableItemId || filter.geometry);

  const filteredEcoRegionLabels =
    filter.stagedLocationIds?.length > 0
      ? ecoRegionLabels.filter((d) => ecoRegions[d].some((id) => filter.stagedLocationIds.includes(id)))
      : ecoRegionLabels;

  const filteredCountryLabels =
    filter.stagedLocationIds?.length > 0
      ? countryLabels.filter((d) => countries[d].some((id) => filter.stagedLocationIds.includes(id)))
      : countryLabels;

  const filteredStateLabels =
    filter.stagedLocationIds?.length > 0
      ? stateLabels.filter((d) => states[d].some((id) => filter.stagedLocationIds.includes(id)))
      : stateLabels;

  const visibleLocations = filter.stagedLocationIds?.length > 0 || filter.locationIds?.length > 0;
  const visibleStagedLocations = filter.stagedLocationIds?.filter((l) => !filter.locationIds.includes(l)) || [];

  return (
    <>
      {data?.locationIds && states && countries ? (
        (
          <>
            <Box ml={1} display="flex" flexDirection="row">
              <Box m={1} minWidth={300}  display="flex">
                <Box>
                  <Typography variant="subtitle2">Start Date</Typography>
                  <input
                    disabled={loading}
                    max={filter.endDate}
                    onChange={updateStartDate}
                    style={{height: '35px', width: '130px'}}
                    type="date"
                    value={filter.startDate}
                  />
                </Box>
                <Box ml={1}>
                  <Typography variant="subtitle2">End Date</Typography>
                  <input
                    disabled={loading}
                    min={filter.startDate}
                    onChange={updateEndDate}
                    style={{height: '35px', width: '130px'}}
                    type="date"
                    value={filter.endDate}
                  />
                </Box>
              </Box>
              <Box m={1} minWidth={300}>
                <Typography variant="subtitle2">Geometry</Typography>
                <SpeciesCorrectGeometryFilter onChange={updateGeometry} filter={filter} />
              </Box>
              <Box m={1} minWidth={300}>
                <CustomSearchFilterInput fullWidth label="Species" formData={filter.species || null} onChange={updateObservableItem} />
              </Box>
            </Box>
            <Box ml={1} display="flex" flexDirection="row">
              <Box m={1} minWidth={300}>
                <Typography variant="subtitle2">EcoRegion</Typography>
                <Autocomplete
                  disabled={loading}
                  filterSelectedOptions
                  onChange={updateEcoRegion}
                  options={filteredEcoRegionLabels}
                  value={filter.ecoRegion || null}
                  renderInput={(params) => <TextField {...params} />}
                  size="small"
                />
              </Box>
              <Box m={1} minWidth={300}>
                <Typography variant="subtitle2">Country</Typography>
                <Autocomplete
                  disabled={loading}
                  filterSelectedOptions
                  onChange={updateCountry}
                  options={filteredCountryLabels}
                  value={filter.country || null}
                  renderInput={(params) => <TextField {...params} />}
                  size="small"
                />
              </Box>
              <Box m={1} minWidth={300}>
                <Typography variant="subtitle2">Area/State</Typography>
                <Autocomplete
                  disabled={loading}
                  filterSelectedOptions
                  onChange={updateState}
                  options={filteredStateLabels}
                  value={filter.state || null}
                  renderInput={(params) => <TextField {...params} />}
                  size="small"
                />
              </Box>
            </Box>
            {visibleLocations && (
              <Box display="flex" flexDirection="column">
                <Box minWidth={600}>
                  {visibleStagedLocations.map((l) => (
                    <Chip key={l} style={{margin: 5}} label={locations[l]} onClick={() => updateFilter({action: 'addLocation', id: l})} />
                  ))}
                </Box>
                <Box width={300} m={1}>
                  <Button
                    variant="outlined"
                    disabled={visibleStagedLocations.length < 1}
                    onClick={() => updateFilter({action: 'addAllLocations'})}
                  >
                    Add All Locations
                  </Button>
                </Box>
                <Box>
                  <hr></hr>
                </Box>
                <Box>
                  {filter.locationIds &&
                    filter.locationIds.map((l) => (
                      <Chip
                        key={l}
                        style={{margin: 5}}
                        label={locations[l]}
                        onDelete={() => updateFilter({action: 'removeLocation', id: l})}
                      />
                    ))}
                </Box>
              </Box>
            )}
            <Box ml={1} py={1} display="flex" flexDirection="row">
              <Box ml={1} width={50}>
                <Button onClick={() => updateFilter()} fullWidth variant="outlined">
                  Reset
                </Button>
              </Box>
              <Box ml={3} mr={1} width={220}>
                <LoadingButton disabled={!canSearch} onClick={() => onSearch(filter)} fullWidth variant="contained">
                  Search
                </LoadingButton>
              </Box>
            </Box>
          </>
        )
      ) : (
        <LinearProgress />
      )}
    </>
  );
};

SpeciesCorrectFilter.propTypes = {
  onSearch: PropTypes.func.isRequired,
  onLoadLocations: PropTypes.func.isRequired
};

export default SpeciesCorrectFilter;
