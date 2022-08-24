import React, {useState, useRef} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {NavLink, useLocation} from 'react-router-dom';
import {grey, red} from '@mui/material/colors';
import {getResult, entityEdit} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, Save} from '@mui/icons-material';
import 'ag-grid-enterprise';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

const DiverList = () => {
  const location = useLocation();
  const [delta, setDelta] = useState([]);
  const [errors, setErrors] = useState([]);
  const gridRef = useRef(null);

  const onGridReady = (event) => {
    async function fetchDivers(event) {
      await getResult('divers').then((res) => {
        event.api.setRowData(res.data);
      });
    }

    fetchDivers(event).then(() => {
      if (!location?.state?.resetFilters) {
        stateFilterHandler.restoreStateFilters(gridRef);
      } else {
        stateFilterHandler.resetStateFilters(gridRef);
      }
    });
  };

  const onCellValueChanged = (e) => {
    setDelta((data) => {
      const newData = {...data};
      newData[e.data.diverId] = e.data;
      return newData;
    });
  };

  const chooseCellStyle = (params) => {
    if (params.context.errors.some((e) => e.id === params.data.diverId)) return {backgroundColor: red[100]};
    if (params.context.delta[params.data.diverId]) return {backgroundColor: grey[100]};
    return null;
  };

  const tooltipValueGetter = (params) => {
    const error = params.context.errors.find((e) => e.id === params.data.diverId);
    if (error) return error.message;
  };

  const saveGrid = () => {
    setErrors([]);
    entityEdit('divers', Object.values(delta)).then((res) => {
      res.status === 400 ? setErrors(res.data) : setDelta({});
    });
  };

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Divers</Typography>
        </Box>
        <Box>
          <Button variant="contained" to="/reference/diver" component={NavLink} startIcon={<Add />}>
            New Diver
          </Button>
        </Box>
        <Box mx={2}>
          <Button variant="contained" startIcon={<Save />} onClick={saveGrid} disabled={Object.keys(delta).length < 1}>
            Save Changes
          </Button>
        </Box>
      </Box>
      <AgGridReact
        ref={gridRef}
        id={'diver-list'}
        className="ag-theme-material"
        getRowId={(r) => r.data.diverId}
        rowHeight={20}
        fillHandleDirection="y"
        pagination
        enableBrowserTooltips
        onCellValueChanged={onCellValueChanged}
        context={{useOverlay: 'Loading Divers', delta, errors}}
        components={{loadingOverlay: LoadingOverlay}}
        loadingOverlayComponent="loadingOverlay"
        onGridReady={(e) => onGridReady(e)}
        onFilterChanged={(e) => stateFilterHandler.stateFilterEventHandler(gridRef, e)}
        defaultColDef={{
          editable: true,
          sortable: true,
          resizable: true,
          suppressMenu: true,
          floatingFilter: true,
          filter: 'agTextColumnFilter',
          cellStyle: chooseCellStyle,
          tooltipValueGetter: tooltipValueGetter
        }}
      >
        <AgGridColumn field="initials" />
        <AgGridColumn flex={1} field="fullName" />
      </AgGridReact>
    </>
  );
};

export default DiverList;
