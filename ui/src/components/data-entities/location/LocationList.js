import React, {useEffect, useState} from 'react';
import {Box, Button, Typography} from '@material-ui/core';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {Redirect, NavLink} from 'react-router-dom';
import {getEntity} from '../../../axios/api';
import LoadingOverlay from '../../overlays/LoadingOverlay';
import {Add} from '@material-ui/icons';
import PropTypes from 'prop-types';

import 'ag-grid-community/dist/styles/ag-theme-material.css';
import 'ag-grid-enterprise';

const LocationList = ({filterModel, setFilterModel}) => {
  const [gridApi, setGridApi] = useState(null);
  const [redirect, setRedirect] = useState(null);
  const [disableResetFilter, setResetFilterDisabled] = useState(true);

  useEffect(() => {
    if (gridApi) {
      getEntity('locations').then((res) => gridApi.setRowData(res.data));
    }
  }, [gridApi]);

  if (redirect) return <Redirect push to={`/reference/location/${redirect}`} />;

  return (
    <>
      <Box display="flex" flexDirection="row" p={1} pb={1}>
        <Box flexGrow={1}>
          <Typography variant="h4">Locations</Typography>
        </Box>
        <Box mr={2}>
          <Button disabled={disableResetFilter} onClick={() => gridApi.setFilterModel(null)} color="primary" variant={'contained'}>
            Reset Filter
          </Button>
        </Box>
        <Box>
          <Button to="/reference/location" component={NavLink} startIcon={<Add />}>
            New Location
          </Button>
        </Box>
      </Box>
      <Box flexGrow={1} overflow="hidden" className="ag-theme-material">
        <AgGridReact
          rowHeight={24}
          enableCellTextSelection={true}
          onGridReady={(e) => setGridApi(e.api)}
          context={{useOverlay: 'Loading Locations'}}
          frameworkComponents={{loadingOverlay: LoadingOverlay}}
          loadingOverlayComponent="loadingOverlay"
          suppressCellSelection={true}
          onFirstDataRendered={() => gridApi.setFilterModel(filterModel)}
          defaultColDef={{sortable: true, resizable: true, filter: 'agTextColumnFilter', floatingFilter: true}}
          onFilterChanged={(e) => {
            const newFilterModel = e.api.getFilterModel();
            setFilterModel(newFilterModel);
            setResetFilterDisabled(Object.keys(newFilterModel)?.length < 1);
          }}
        >
          <AgGridColumn
            width={40}
            field="id"
            headerName=""
            suppressMovable={true}
            filter={false}
            resizable={false}
            sortable={false}
            valueFormatter={() => '✎'}
            cellStyle={{paddingLeft: '10px', color: 'grey', cursor: 'pointer'}}
            onCellClicked={(e) => {
              if (e.event.ctrlKey) {
                window.open(`/reference/location/${e.data.id}/edit`, '_blank').focus();
              } else {
                setRedirect(`${e.data.id}/edit`);
              }
            }}
          />
          <AgGridColumn
            flex={1}
            field="locationName"
            sort="asc"
            cellStyle={{cursor: 'pointer'}}
            onCellClicked={(e) => setRedirect(e.data.id)}
          />
          <AgGridColumn maxWidth={80} field="status" />
          <AgGridColumn flex={2} field="ecoRegions" />
          <AgGridColumn flex={2} field="countries" />
          <AgGridColumn flex={2} field="areas" />
        </AgGridReact>
      </Box>
    </>
  );
};

LocationList.propTypes = {
  filterModel: PropTypes.any,
  setFilterModel: PropTypes.any
};

export default LocationList;
