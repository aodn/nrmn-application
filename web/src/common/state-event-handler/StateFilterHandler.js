import { FilterChangedEvent } from 'ag-grid-community';
import { MutableRefObject } from 'react';

function stateStorage() {
  return localStorage;
}

// Component must set the id property if it needs to use this function
function stateFilterEventHandler(ref: MutableRefObject, event: FilterChangedEvent) {
  stateStorage().setItem(ref.current.props.id + '-filters', JSON.stringify(event.api.getFilterModel()));
};

// Component must set the id property if it needs to use this function
function restoreStateFilters(ref: MutableRefObject) {
  const filtersJson = stateStorage().getItem(ref.current.props.id + '-filters');

  if(filtersJson) {
    const filtersObject = JSON.parse(filtersJson);
    ref.current.api.setFilterModel(filtersObject);
  }
}

export {
  stateFilterEventHandler,
  restoreStateFilters,
};


