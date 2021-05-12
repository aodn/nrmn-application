import React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {TextField, Typography} from '@material-ui/core';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {setField} from '../middleware/entities';
import {PropTypes} from 'prop-types';
import {searchRequested} from '../form-reducer';

const SearchInput = ({schema, name}) => {
  const dispatch = useDispatch();

  const value = useSelector((state) => state.form.data[name]) ?? '';
  const searchResults = useSelector((state) => state.form.searchResults);

  return (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <Autocomplete
        options={searchResults?.map((i) => i.species) ?? []}
        freeSolo
        defaultValue={value}
        onSelect={(e) => {
          dispatch(setField({newValue: e.target.value, entity: name}));
        }}
        onKeyUp={(e) => {
          dispatch(setField({newValue: e.target.value, entity: name}));
          if (e.target.value?.length > 2)
            dispatch(searchRequested({searchType: 'NRMN', species: e.target.value, includeSuperseded: false}));
        }}
        renderInput={(params) => <TextField {...params} color="primary" variant="outlined" />}
      />
    </>
  );
};

SearchInput.propTypes = {
  name: PropTypes.string,
  uiSchema: PropTypes.object,
  schema: PropTypes.object
};

export default SearchInput;
