import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@material-ui/core';
import {NavLink} from 'react-router-dom';
import {grey, red} from '@material-ui/core/colors';
import {getResult, entityEdit} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add, Save} from '@material-ui/icons';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const DiverList = () => {
  const [gridApi, setGridApi] = useState(null);
  const [delta, setDelta] = useState([]);
  const [errors, setErrors] = useState([]);
  const [disableResetFilter, setResetFilterDisabled] = useState(true);

  useEffect(() => gridApi && getResult('divers').then((res) => gridApi.setRowData(res.data)), [gridApi]);

  useEffect(() => gridApi && gridApi.redrawRows(), [gridApi, delta, errors]);

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

  const saveGrid = () =>
    entityEdit('divers', Object.values(delta)).then((res) => {
      res.status === 400 ? setErrors(res.data) : setDelta({});
    });
  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Divers</Typography>
        </Box>
        <Box mr={4}>
          <Button
            style={{width: '100%'}}
            disabled={disableResetFilter}
            onClick={() => gridApi.setFilterModel(null)}
            color="primary"
            variant={'contained'}
          >
            Reset Filter
          </Button>
        </Box>
        <Box mr={4}>
          <Button to="/reference/diver" component={NavLink} startIcon={<Add />}>
            New Diver
          </Button>
        </Box>
        <Box>
          <Button startIcon={<Save />} onClick={saveGrid} disabled={Object.keys(delta).length < 1}>
            Save
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          getRowNodeId={(r) => r.id}
          rowHeight={24}
          enableBrowserTooltips
          immutableData={true}
          animateRows={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          onCellValueChanged={onCellValueChanged}
          context={{useOverlay: 'Loading Divers', delta, errors}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          onFilterChanged={(e) => {
            const filterModel = e.api.getFilterModel();
            setResetFilterDisabled(Object.keys(filterModel)?.length < 1);
          }}
          defaultColDef={{
            editable: true,
            sortable: true,
            resizable: true,
            floatingFilter: true,
            filter: 'agTextColumnFilter',
            cellStyle: chooseCellStyle,
            tooltipValueGetter: tooltipValueGetter
          }}
        >
          <AgGridColumn width={80} field="initials" />
          <AgGridColumn flex={1} field="fullName" />
        </AgGridReact>
      </Box>
    </>
  );
};

export default DiverList;
