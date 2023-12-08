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
  const [error, setError] = useState(false);

  useEffect(() => {
    // The setTimout is used to delay the whole search operation by 800ms, this
    // avoid search happens on each key input, and wait user stop entering before
    // search happens
    const t = setTimeout(() => {
      if (searchTerm?.length > minMatchCharacters) {
        const cancelTokenSource = axios.CancelToken.source();
        const query = { searchType: 'NRMN', species: searchTerm, includeSuperseded: false };
        search(query, cancelTokenSource.token).then((resp) => {
          const resultSet = resp.data
            ? resp.data.map((i) => ({ id: i.observableItemId, label: i.species })).filter((f) => f.label !== exclude)
            : [];
          setResults(resultSet);

          const isValid = resultSet.find(f => f.label === searchTerm) !== undefined;
          setError(!isValid);

          if (textValue !== searchTerm && isValid) {
            /**
             * Handle case where user type the species without select the dropdown
             * in this case the handleOnInputChanged will only contains reason === 'input' and will not be set
             * with onChange function.
             *
             * This check can make sure the value user typed matches an item in the resultSet list (that is valid value)
             * get updated to the datastructure and not cause infinity loop
             */
            onChange(searchTerm);
          }
        });
        return () => cancelTokenSource.cancel();
      }
      else if (searchTerm?.length === 0) {
        // User clear the input, this is a valid action
        setError(false);
        onChange('');
      }
    }, 800);

    // You need to invalidate previous timeout because user have new input
    return () => clearTimeout(t);
  }, [textValue, searchTerm, exclude, onChange]);

  const handleOnInputChanged = (event, value, reason) => {

    setError(false);
    if (reason === 'input') {
      // User type text directly to text box
      if(searchTerm !== value) {
        setSearchTerm(value);
      }
    }
    else {
      // Programmatic change or 'clear' || run check on init case
      // it is used to handle case where the species no longer exist but
      // used in supersededby.
      if(textValue !== value || searchTerm === null) {
        // Update if diff to avoid looping
        setSearchTerm(value);
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
        renderInput={(params) => <TextField {...params} error={error} helperText = {error ? 'Not a valid species for this field.' : ''} size="small" color="primary" variant="outlined" />}
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
