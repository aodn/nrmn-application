import { Column, FilterChangedEvent } from 'ag-grid-community';
import { Dispatch, MutableRefObject, SetStateAction } from 'react';

// Component must set the id property if it needs to use this function
function stateFilterEventHandler(ref: MutableRefObject, event: FilterChangedEvent) {
  localStorage.setItem(ref.current.props.id + '-filters', JSON.stringify(event.api.getFilterModel()));
};

// Component must set the id property if it needs to use this function
function restoreStateFilters(ref: MutableRefObject) {
  const filtersJson = localStorage.getItem(ref.current.props.id + '-filters');
  const filtersObject = JSON.parse(filtersJson);
  ref.current.api.setFilterModel(filtersObject);
}

export {
  stateFilterEventHandler,
  restoreStateFilters,
};


