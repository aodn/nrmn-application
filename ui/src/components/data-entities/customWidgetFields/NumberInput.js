import React from 'react';
import {useDispatch, useSelector} from 'react-redux';

import {TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {setField} from '../middleware/entities';

const NumberInput = (props) => {
  const entity = props.name;
  const dispatch = useDispatch();
  let formData = useSelector((state) => state.form.formData[entity]);

  return (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <TextField
        color="primary"
        type="number"
        value={formData ?? ''}
        onBlur={(event) => {
          if (isNaN(event.target.value)) {
            // TODO: Validation of eg. latitude or longitude
          }
        }}
        onChange={(event) => {
          const value = event.target.value;
          const newValue = parseFloat(value);
          dispatch(setField({newValue, entity}));
        }}
      />
    </>
  );
};

NumberInput.propTypes = {
  name: PropTypes.string,
  title: PropTypes.string,
  schema: PropTypes.object,
  formData: PropTypes.string
};

export default NumberInput;
