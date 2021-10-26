import React from 'react';

import {TextField, Typography} from '@material-ui/core';

import {PropTypes} from 'prop-types';

const CustomNumberInput = ({onChange, schema, formData, error}) => {
  return (
    <>
      <Typography variant="subtitle2">{schema.title}</Typography>
      <TextField
        color="primary"
        type="number"
        error={error}
        helperText={error?.message}
        value={formData ?? ''}
        onBlur={(event) => {
          if (isNaN(event.target.value)) {
            // TODO: Validation of eg. latitude or longitude
            // getEntity('sitesAroundLocation?exclude=PAC31&longitude=-169.65513&latitude=-14.17817').then((res) => {console.log(res);});
          }
        }}
        onChange={(event) => onChange(event.target.value)}
      />
    </>
  );
};

CustomNumberInput.propTypes = {
  schema: PropTypes.object,
  error: PropTypes.object,
  formData: PropTypes.number,
  onChange: PropTypes.func
};

export default CustomNumberInput;
