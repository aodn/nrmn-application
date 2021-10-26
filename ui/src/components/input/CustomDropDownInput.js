import React from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const CustomDropDownInput = ({label, field, errors, optional, options, formData, onChange}) => {
  const error = errors?.find((f) => f.property === field);

  if (!options || options.length < 1) {
    return (
      <>
        <Typography variant="subtitle2">{label}</Typography>
        <CircularProgress size={30} />
      </>
    );
  }

  return (
    <>
      <Typography variant="subtitle2">{label}</Typography>
      <Autocomplete
        disableClearable={optional ? !optional : true}
        filterSelectedOptions
        options={options}
        getOptionLabel={(o) => o.label}
        getOptionSelected={(o, v) => (v ? o.id === v.id : null)}
        value={options.find((o) => o.id === formData) ?? null}
        onChange={(_, o) => onChange(o?.id)}
        renderInput={(params) => <TextField {...params} variant="outlined" error={error} helperText={error?.message} />}
      />
    </>
  );
};

CustomDropDownInput.propTypes = {
  label: PropTypes.String,
  field: PropTypes.String,
  errors: PropTypes.array,
  uiSchema: PropTypes.object,
  formData: PropTypes.string,
  onChange: PropTypes.func,
  options: PropTypes.array,
  optional: PropTypes.boolean
};

export default CustomDropDownInput;
