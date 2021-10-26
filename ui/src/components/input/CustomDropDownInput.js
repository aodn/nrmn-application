import React, {useState, useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {getEntity} from '../../axios/api';

const CustomDropDownInput = ({name, schema, uiSchema, formData, formContext, onChange}) => {
  const {values, optional, route} = uiSchema;
  const error = formContext ? formContext.find((f) => f.property === name) : null;

  const [options, setOptions] = useState(values);

  useEffect(() => {
    if (route !== undefined) {
      getEntity(route).then((res) => {
        const locations = res.data._embedded.locations.map((l) => {
          return {id: l.locationId, label: l.locationName};
        });
        setOptions(locations);
      });
    }
  }, [route]);

  if (!options || options.length < 1) {
    return (
      <>
        <Typography variant="subtitle2">{schema.title}</Typography>
        <CircularProgress size={30} />
      </>
    );
  }

  return (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
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
  name: PropTypes.String,
  schema: PropTypes.object,
  uiSchema: PropTypes.object,
  formData: PropTypes.string,
  formContext: PropTypes.array,
  onChange: PropTypes.func
};

export default CustomDropDownInput;
