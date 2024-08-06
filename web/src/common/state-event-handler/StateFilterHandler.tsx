import { AgGridReact } from 'ag-grid-react';
import { FilterChangedEvent } from 'ag-grid-community';

export type LocationState = {
  resetFilters: boolean;
}

function getFiltersForId(id: string) {
  return localStorage.getItem(id + '-filters');
}

// Component must set the id property if it needs to use this function
function stateFilterEventHandler(id: string, event: FilterChangedEvent) {
  localStorage.setItem(id + '-filters', JSON.stringify(event.api.getFilterModel()));
}

function resetStateFilters(id: string) {
  localStorage.removeItem(id + '-filters');
}

// Component must set the id property if it needs to use this function
function restoreStateFilters(ref: React.RefObject<AgGridReact>, id: string) {
  const filtersJson = stateFilterHandler.getFiltersForId(id);

  if(filtersJson) {
    const filtersObject = JSON.parse(filtersJson);
    ref.current?.api
      .setFilterModel(filtersObject);
  }
}

const stateFilterHandler = {
  stateFilterEventHandler,
  restoreStateFilters,
  resetStateFilters,
  getFiltersForId
};

export default stateFilterHandler;

