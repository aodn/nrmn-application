import React from 'react';

import {useDispatch, useSelector} from 'react-redux';
import {useEffect} from 'react';
import pluralize from 'pluralize';
import {selectedItemsRequested} from '../middleware/entities';
import {markupProjectionQuery} from '../../utils/helpers';
import Grid from '@material-ui/core/Grid';
import ObjectListViewTemplate from '../ObjectListViewTemplate';

const NestedApiFieldDetails = (props) => {
  let editItemValues = useSelector((state) => state.form.editItem);
  const dispatch = useDispatch();

  const entity = props.name;
  const pluralEntity = pluralize(entity);

  let selectedItems = editItemValues[entity + 'Selected'] ? [editItemValues[entity + 'Selected']].filter(Boolean) : [];

  useEffect(() => {
    if (selectedItems.length === 0 && editItemValues._links) {
      let urls = [markupProjectionQuery(pluralEntity)];
      urls.push(markupProjectionQuery(editItemValues._links[entity].href));
      dispatch(selectedItemsRequested(urls));
    }
  }, [editItemValues]);

  let items = [];
  for (let key of Object.keys(selectedItems)) {
    items.push(<Grid item>{selectedItems[key].label}</Grid>);
  }

  return ObjectListViewTemplate({name: props.name, items: items});
};

export default NestedApiFieldDetails;
