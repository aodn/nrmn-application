import React from 'react';
import {useDispatch, useSelector} from 'react-redux';

import {TextField, Typography} from '@material-ui/core';
import {PropTypes} from 'prop-types';
import {setField} from '../middleware/entities';

const TextInput = (props) => {
  const dispatch = useDispatch();
  const formData = useSelector((state) => state.form.formData);
  const entity = props.name;
  const readOnly = props.uiSchema['ui:field'] === 'readonly';

  return (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      {readOnly ? (
        <Typography>{formData[entity]}</Typography>
      ) : (
        <TextField
          color="primary"
          inputProps={{
            readOnly: readOnly
          }}
          value={formData[entity] ?? ''}
          onChange={(event) => {
            const newValue = event.target.value;
            dispatch(setField({newValue, entity}));
          }}
        />
      )}
    </>
  );
};

TextInput.propTypes = {
  name: PropTypes.string,
  title: PropTypes.string,
  schema: PropTypes.object,
  uiSchema: PropTypes.object,
  formData: PropTypes.string
};

export default TextInput;
