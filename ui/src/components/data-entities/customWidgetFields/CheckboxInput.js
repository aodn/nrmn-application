import React from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {Checkbox, FormControlLabel, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {setField} from '../middleware/entities';

const CheckboxInput = (props) => {
  const dispatch = useDispatch();
  let formData = useSelector((state) => state.form.data);
  const entity = props.name;

  return (
    <FormControlLabel
      style={{marginTop: '25px', marginLeft: '5px'}}
      control={
        <Checkbox
          checked={formData[entity] ?? props.schema.default}
          onChange={(event) => {
            const newValue = event.target.checked;
            dispatch(setField({newValue, entity}));
          }}
        />
      }
      label={<Typography variant="subtitle2">{props.schema.title}</Typography>}
    />
  );
};

CheckboxInput.propTypes = {
  name: PropTypes.string,
  schema: PropTypes.object
};

export default CheckboxInput;
