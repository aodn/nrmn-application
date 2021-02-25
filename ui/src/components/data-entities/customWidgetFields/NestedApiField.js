import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import Autocomplete from '@material-ui/lab/Autocomplete';
import TextField from '@material-ui/core/TextField';
import {Typography} from '@material-ui/core';
import {selectedItemsRequested, setNestedField} from '../middleware/entities';
import {markupProjectionQuery} from '../../utils/helpers';
import {PropTypes} from 'prop-types';

const NestedApiField = (props) => {
  let editItemValues = useSelector((state) => state.form.editItem);
  const dispatch = useDispatch();

  const entity = props.name;
  const pluralEntity = entity;

  let itemsList = editItemValues[pluralEntity] ? editItemValues[pluralEntity][pluralEntity] : [];
  let selectedItems = editItemValues[entity + 'Selected'] ? [editItemValues[entity + 'Selected']].filter(Boolean) : [];

  useEffect(() => {
    let urls = [markupProjectionQuery(pluralEntity)];
    if (editItemValues._links) {
      urls.push(markupProjectionQuery(editItemValues._links[entity].href));
    }
    dispatch(selectedItemsRequested(urls));
  }, []);

  return itemsList.length > 0 ? (
    <>
      <Typography variant={'h5'}>{entity}</Typography>
      <Autocomplete
        id={'select-auto-' + entity}
        options={itemsList}
        multiple={props.multiple || false}
        getOptionLabel={(option) => option.label}
        defaultValue={props.multiple ? selectedItems : selectedItems[0]}
        filterSelectedOptions
        onChange={(event, newValues) => dispatch(setNestedField({newValues, entity}))}
        renderInput={(params) => <TextField {...params} label={'Select ' + entity} variant="outlined" />}
      />
    </>
  ) : (
    <>
      <Typography variant={'h5'}>{pluralEntity}</Typography>
      <div>add item</div>
    </>
  );
};

NestedApiField.propTypes = {
  name: PropTypes.string,
  multiple: PropTypes.bool
};

export default NestedApiField;
