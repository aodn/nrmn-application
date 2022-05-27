function getFiltersForId(id) {
  return localStorage.getItem(id + '-filters');;
}

// Component must set the id property if it needs to use this function
function stateFilterEventHandler(ref, event) {
  localStorage.setItem(ref.current.props.id + '-filters', JSON.stringify(event.api.getFilterModel()));
};

// Component must set the id property if it needs to use this function
function restoreStateFilters(ref) {
  const filtersJson = getFiltersForId(ref.current.props.id);

  if(filtersJson) {
    const filtersObject = JSON.parse(filtersJson);
    ref.current.api.setFilterModel(filtersObject);
  }
}

export {
  stateFilterEventHandler,
  restoreStateFilters,
  getFiltersForId,
};


