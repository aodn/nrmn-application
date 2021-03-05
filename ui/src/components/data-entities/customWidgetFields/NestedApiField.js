import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import {Typography, CircularProgress, TextField} from '@material-ui/core';
import {selectedItemsRequested, setField} from '../middleware/entities';
import {markupProjectionQuery} from '../../utils/helpers';
import {PropTypes} from 'prop-types';

const NestedApiField = (props) => {
  let editItemValues = useSelector((state) => state.form.formData);
  let formOptions = useSelector((state) => state.form.formOptions);

  const dispatch = useDispatch();

  const entity = props.name;
  let itemsList = formOptions['locations'] ?? []; // FIXME: hardcoded test value!

  const pluralEntity = entity;

  let selectedItems = editItemValues[entity + 'Selected'] ? [editItemValues[entity + 'Selected']].filter(Boolean) : [];

  useEffect(() => {
    let urls = [markupProjectionQuery(pluralEntity)];
    if (editItemValues._links) {
      urls.push(markupProjectionQuery(editItemValues._links[entity].href));
    }
    // FIXME: hardcoded test value!
    dispatch(selectedItemsRequested(['locations?projection=selection']));
  }, []);

  return itemsList.length > 0 ? (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <Autocomplete
        id={'select-auto-' + entity}
        options={itemsList}
        multiple={props.multiple || false}
        getOptionLabel={(option) => option.label}
        defaultValue={props.multiple ? selectedItems : selectedItems[0]}
        filterSelectedOptions
        onChange={(_, value) => dispatch(setField({newValue: value.id, entity}))}
        renderInput={(params) => <TextField {...params} variant="outlined" />}
      />
    </>
  ) : (
    <>
      <Typography variant="subtitle2">{props.schema.title}</Typography>
      <CircularProgress size={30} />
    </>
  );
};

NestedApiField.propTypes = {
  name: PropTypes.string,
  multiple: PropTypes.bool,
  schema: PropTypes.object
};

export default NestedApiField;
