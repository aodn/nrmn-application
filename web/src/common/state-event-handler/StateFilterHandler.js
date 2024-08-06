function getFiltersForId(id) {
  return localStorage.getItem(id + '-filters');
}

// Component must set the id property if it needs to use this function
function stateFilterEventHandler(ref, event) {
  localStorage.setItem(ref.current.props.id + '-filters', JSON.stringify(event.api.getFilterModel()));
}

function resetStateFilters(ref) {
  localStorage.removeItem(ref.current.props.id + '-filters');
}

// Component must set the id property if it needs to use this function
function restoreStateFilters(ref) {
  const filtersJson = stateFilterHandler.getFiltersForId(ref.current.props.id);

  if(filtersJson) {
    const filtersObject = JSON.parse(filtersJson);
    ref.current.api
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

