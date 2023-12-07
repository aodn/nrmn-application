import React, {useEffect, useState} from 'react';
import {TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import {PropTypes} from 'prop-types';
import {search} from '../../api/api';
const axios = require('axios/dist/axios');

const CustomSearchFilterInput = ({label, exclude, formData, onChange, fullWidth, dataTestId}) => {
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
        if (resultSet.length === 1) onChange(resultSet[0]);
      });
      return () => cancelTokenSource.cancel();
    }
  }, [searchTerm, exclude, onChange]);

  return (
    <>
      {label && <Typography variant="subtitle2">{label}</Typography>}
      <Autocomplete
        options={options}
        clearOnBlur
        freeSolo
        fullWidth={fullWidth}
        data-testid={dataTestId}
        value={formData ? formData : ''}
        onKeyUp={(e) => setSearchTerm(e.target.value)}
        onInputChange={(e, v) => onChange(results.filter((r) => r.species === v)[0])}
        renderInput={(params) => <TextField {...params} size="small" color="primary" variant="outlined" />}
      />
    </>
  );
};

CustomSearchFilterInput.propTypes = {
  onChange: PropTypes.func.isRequired,
  formData: PropTypes.string,
  label: PropTypes.string,
  exclude: PropTypes.string,
  dataTestId: PropTypes.string,
  fullWidth: PropTypes.bool
};

export default CustomSearchFilterInput;
