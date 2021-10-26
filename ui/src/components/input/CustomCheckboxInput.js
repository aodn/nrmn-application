import React from 'react';
import {Checkbox, FormControlLabel, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';

const CustomCheckboxInput = ({schema, onChange, formData}) => {
  return (
    <FormControlLabel
      style={{marginTop: '25px', marginLeft: '5px'}}
      control={<Checkbox checked={formData ?? schema.default} onChange={(event) => onChange(event.target.checked)} />}
      label={<Typography variant="subtitle2">{schema.title}</Typography>}
    />
  );
};

CustomCheckboxInput.propTypes = {
  schema: PropTypes.object,
  onChange: PropTypes.func,
  formData: PropTypes.boolean
};

export default CustomCheckboxInput;
