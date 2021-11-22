import React, {useEffect, useState} from 'react';
import {TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {PropTypes} from 'prop-types';
import {search} from '../../axios/api';
import axios from 'axios';

const CustomSearchInput = ({label, exclude, formData, onChange}) => {
  const minMatchCharacters = 2;
  const [results, setResults] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    if (searchTerm.length > minMatchCharacters) {
      const cancelTokenSource = axios.CancelToken.source();
      const query = {searchType: 'NRMN', species: searchTerm, includeSuperseded: false};
      search(query, cancelTokenSource.token).then((resp) =>
        setResults(resp.data ? resp.data.map((i) => i.species).filter((f) => f !== exclude) : [])
      );
      return () => cancelTokenSource.cancel();
    }
  }, [searchTerm, minMatchCharacters, exclude]);

  return (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <Autocomplete
        options={results}
        clearOnBlur
        freeSolo
        defaultValue={formData}
        onSelect={(e) => onChange(e.target.value)}
        onKeyUp={(e) => setSearchTerm(e.target.value)}
        renderInput={(params) => <TextField {...params} color="primary" variant="outlined" />}
      />
    </>
  );
};

CustomSearchInput.propTypes = {
  onChange: PropTypes.func.isRequired,
  formData: PropTypes.string,
  label: PropTypes.string.isRequired,
  exclude: PropTypes.string
};

export default CustomSearchInput;
