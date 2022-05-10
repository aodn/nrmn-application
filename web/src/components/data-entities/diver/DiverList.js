import React, {useState, useCallback, useRef} from 'react';
import {Box, Button, Typography} from '@mui/material';
import {NavLink} from 'react-router-dom';
import {grey, red} from '@mui/material/colors';
import {getResult, entityEdit} from '../../../api/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, Save} from '@mui/icons-material';
import 'ag-grid-enterprise';

const DiverList = () => {
  const [delta, setDelta] = useState([]);
  const [rowData, setRowData] = useState();
  const [errors, setErrors] = useState([]);
  const dGridRef = useRef(null);

  // Auto size function to be call each time data changed, so the grid always autofit
  const autoSizeAll = (skipHeader) => {
      if(dGridRef.current != null) {
          dGridRef.current.columnApi.autoSizeAllColumns(skipHeader);
      }};

  const onGridReady = useCallback(() => {
    async function fetchDivers() {
      await getResult('divers').then(
          (res) => {
              setRowData(res.data);
              autoSizeAll(false);
          });
    }
    fetchDivers();
  }, []);

  // Why need this extra redraw?
  // useEffect(() => {
  //   if (gridApi) gridApi.redrawRows();
  // }, [gridApi, delta, errors]);

  const onCellValueChanged = (e) => {
    setDelta((data) => {
      const newData = {...data};
      newData[e.data.diverId] = e.data;
      autoSizeAll(false);
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
        ref={dGridRef}
        className="ag-theme-material"
        getRowId={(r) => r.data.diverId}
        rowHeight={20}
        rowData={rowData}
        fillHandleDirection="y"
        pagination={true}
        enableBrowserTooltips
        onCellValueChanged={onCellValueChanged}
        context={{useOverlay: 'Loading Divers', delta, errors}}
        components={{loadingOverlay: LoadingOverlay}}
        loadingOverlayComponent="loadingOverlay"
        onGridReady={onGridReady()}
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
        <AgGridColumn field="fullName" />
      </AgGridReact>
    </>
  );
};

export default DiverList;
