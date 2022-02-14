import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@material-ui/core';
import {Redirect, NavLink} from 'react-router-dom';
import {getResult} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Add} from '@material-ui/icons';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const saveFilterModel = (entityName, filterModel) => {
  window[`AgGrid-FilterModel-${entityName}`] = JSON.stringify(filterModel);
};

const restoreFilterModel = (entityName) => {
  const serialisedFilter = window[`AgGrid-FilterModel-${entityName}`];
  return serialisedFilter ? JSON.parse(serialisedFilter) : null;
};

const DiverList = () => {
  const [gridApi, setGridApi] = useState(null);
  const [redirect, setRedirect] = useState(null);
  const [disableResetFilter, setResetFilterDisabled] = useState(true);

  useEffect(() => {
    if (gridApi) getResult('divers').then((res) => {
      gridApi.setRowData(res.data);
    });
  }, [gridApi]);

  if (redirect) return <Redirect push to={`/reference/diver/${redirect}`} />;

  const onFirstDataRendered = (e) => setTimeout(() => e.api.setFilterModel(restoreFilterModel('diver')), 25);

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
        <Box>
          <Button to="/reference/diver" component={NavLink} startIcon={<Add />}>
            New Diver
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={24}
          animateRows={true}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Divers'}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellSelection={true}
          onFirstDataRendered={onFirstDataRendered}
          onFilterChanged={(e) => {
            const filterModel = e.api.getFilterModel();
            saveFilterModel('diver', filterModel);
            setResetFilterDisabled(Object.keys(filterModel)?.length < 1);
          }}
          defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
        >
          <AgGridColumn
            width={40}
            field="diverId"
            headerName=""
            suppressMovable={true}
            filter={false}
            resizable={false}
            sortable={false}
            valueFormatter={() => '✎'}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/diver/${e.data.diverId}/edit`, '_blank').focus();
              } else {
                setRedirect(`${e.data.diverId}/edit`);
              }
            }}
          />
          <AgGridColumn width={80} field="initials" />
          <AgGridColumn
            flex={1}
            field="fullName"
            cellStyle={{cursor: 'pointer'}}
            onCellClicked={(e) => setRedirect(e.data.diverId)} />
        </AgGridReact>
      </Box>
    </>
  );
};

export default DiverList;
