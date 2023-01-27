import React, {useEffect, useState} from 'react';
import {TextField, Typography} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import PropTypes from 'prop-types';
import {search} from '../../api/api';
import axios from 'axios';


const CustomSearchInput = ({label, exclude, formData, onChange, fullWidth}) => {
  const textValue = formData ? formData : '';
  const minMatchCharacters = 2;
  const [results, setResults] = useState([]);
  const [searchTerm, setSearchTerm] = useState(null);

  useEffect(() => {
    if (searchTerm?.length > minMatchCharacters) {
      const cancelTokenSource = axios.CancelToken.source();
      const query = {searchType: 'NRMN', species: searchTerm, includeSuperseded: false};
      search(query, cancelTokenSource.token).then((resp) => {
        const resultSet = resp.data
          ? resp.data.map((i) => ({id: i.observableItemId, label: i.species})).filter((f) => f.label !== exclude)
          : [];
        setResults(resultSet);

        if(textValue !== searchTerm && resultSet.find(f => f.label === searchTerm) !== undefined) {
          /**
           * Handle case where user type the species without select the dropdown
           * in this case the handleOnInputChanged will only contains reason === 'input' and will not be set
           * with onChange function.
           *
           * This check can make sure the value user type matches an item in the resultSet list (that is valid value)
           * and not the same as previous value which cause infinity loop
           */
          onChange(searchTerm);
        }
      });
      return () => cancelTokenSource.cancel();
    }
    else if(searchTerm?.length === 0) {
      // User clear the input, this is a valid action
      onChange('');
    }
  }, [searchTerm, exclude, onChange]);

  const handleOnInputChanged = (event, value, reason) => {
    if (reason === 'input') {
      // User type text directly to text box
      if(searchTerm !== value) {
        setSearchTerm(value);
      }
    }
    else {
      // Programmatic change or 'clear'
      if(textValue !== value) {
        // Update if diff to avoid looping
        onChange(value);
      }
    }
  };

  return (
    <>
      {label && <Typography variant="subtitle2">{label}</Typography>}
      <Autocomplete
        options={results}
        freeSolo
        fullWidth={fullWidth}
        value={textValue}
        onInputChange={handleOnInputChanged}
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
