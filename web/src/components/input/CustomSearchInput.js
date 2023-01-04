import React, {useEffect, useState} from 'react';
import {TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import {PropTypes} from 'prop-types';
import {search} from '../../api/api';
import axios from 'axios';

const CustomSearchInput = ({label, exclude, formData, onChange, fullWidth}) => {
  const minMatchCharacters = 2;
  const [results, setResults] = useState([]);
  const [options, setOptions] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    if (searchTerm?.length > minMatchCharacters) {
      const cancelTokenSource = axios.CancelToken.source();
      const query = {searchType: 'NRMN', species: searchTerm, includeSuperseded: false};
      search(query, cancelTokenSource.token).then((resp) => {
        const resultSet = resp.data
          ? resp.data.map((i) => ({id: i.observableItemId, species: i.species})).filter((f) => f !== exclude)
          : [];
        setResults(resultSet);
        setOptions(resultSet.map((i) => i.species));
        if (resultSet.length === 1) {
          onChange(resultSet[0]);
        }
      });
      return () => cancelTokenSource.cancel();
    }
  }, [searchTerm, minMatchCharacters, exclude, onChange]);

  return (
    <>
      {label && <Typography variant="subtitle2">{label}</Typography>}
      <Autocomplete
        options={options}
        clearOnBlur
        freeSolo
        fullWidth={fullWidth}
        value={formData}
        onInputChange={(e) => setSearchTerm(e.target.value)}
        onSelect={(e) => onChange(results.filter(r => r.species === e.target.value)[0])}
        renderInput={(params) => <TextField {...params} size="small" color="primary" variant="outlined" />}
      />
    </>
  );
};

CustomSearchInput.propTypes = {
  onChange: PropTypes.func.isRequired,
  formData: PropTypes.string,
  label: PropTypes.string,
  exclude: PropTypes.string,
  fullWidth: PropTypes.bool
};

export default CustomSearchInput;
