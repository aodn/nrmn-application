import React, { useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { AgGridReact } from 'ag-grid-react';
import { AllModules } from 'ag-grid-enterprise';
import { useEffect } from 'react';
import { EditRowStarting, EnableSubmit, JobFinished, RowUpdateRequested } from './reducers/create-import';
import { ColumnDef, ExtendedSize } from './ColumnDef';
import 'ag-grid-community/dist/styles/ag-grid.css';
import 'ag-grid-community/dist/styles/ag-theme-material.css';
import { Box } from '@material-ui/core';
import useWindowSize from '../utils/useWindowSize';

Object.unfreeze = function (o) {
    var oo = undefined;
    if (o instanceof Array) {
        oo = []; var clone = function (v) { oo.push(v); };
        o.forEach(clone);
    } else if (o instanceof String) {
        oo = new String(o).toString();
    } else if (typeof o == 'object') {
        oo = {};
        for (var property in o) { oo[property] = o[property]; }
    }
    return oo;
};

const DataSheetView = () => {
    const dispatch = useDispatch();
    const immutableRows = useSelector(state => state.import.rows);
    const isLoading = useSelector(state => state.import.isLoading);
    const editLoading = useSelector(state => state.import.editLoading);

    const errSelected = useSelector(state => state.import.errSelected);
    const [gridApi, setGridApi] = useState(null);
    var rows = immutableRows.map(Object.unfreeze);
    const job = useSelector(state => state.import.job);

    const colDefinition = (job && job.isExtendedSize) ? ColumnDef.concat(ExtendedSize) : ColumnDef;

    const agGridReady = (params) => {
        setGridApi(params.api);
        dispatch(JobFinished());

        var allColumnIds = [];
        params.columnApi.getAllColumns().forEach(function (column) {
            allColumnIds.push(column.colId);
        });
        console.log(allColumnIds);
        params.columnApi.autoSizeColumns(allColumnIds, false);

        params.api.ensureIndexVisible(25, 49);
    };

    const onCellChanged = (input) => {
        dispatch(RowUpdateRequested(input.data.id, input.data));
        dispatch(EditRowStarting());
        console.log('stop');
        dispatch(EnableSubmit(false));
    };

    const getContextMenuItems = (params) => {
        return [{
            name: 'Delete selected Row(s)',
            action: () => {
                const selectedRows = params.api.getSelectedRows();
                params.api.applyTransaction({ remove: selectedRows });

            },
            cssClasses: ['redBoldFont']

        }];
    };

    useEffect(() => {
        if (gridApi && errSelected.ids && errSelected.ids.length > 0) {
            const instance = gridApi.getFilterInstance('id');
            instance.setModel({ values: errSelected.ids.map(id => id.toString()) }).then(() =>
                gridApi.onFilterChanged()
            );

        }

        if (errSelected.ids === null) {
            const instance = gridApi.getFilterInstance('id');
            instance.setModel(null).then(() =>
                gridApi.onFilterChanged()
            );

        }
    });

    useEffect(() => {
        console.log('edit load', editLoading);
        colDefinition.forEach(def => {
            def.editable = !editLoading;
        });
    }, [editLoading]);

    const size = useWindowSize();
    const themeType = useSelector(state => state.theme.themeType);
    const condition = rows && rows.length && !isLoading;

    return (<Box>
        {condition &&
            <div style={{ height: size.height - 165, width: '100%', marginTop: 25 }} className={themeType ? 'ag-theme-material-dark' : 'ag-theme-material'} >
                <AgGridReact
                    pivotMode={false}
                    pivotColumnGroupTotals={'before'}
                    sideBar={true}
                    autoGroupColumnDef={{
                        width: 20,
                        cellRendererParams: {
                            suppressCount: true,
                            innerRenderer: 'nameCellRenderer'
                        }
                    }}
                    onCellValueChanged={onCellChanged}
                    columnDefs={colDefinition}
                    groupDefaultExpanded={4}
                    rowData={rows}
                    animateRows={true}
                    groupMultiAutoColumn={true}
                    groupHideOpenParents={true}
                    rowSelection={'multiple'}
                    enableCellTextSelection={true}
                    suppressClipboardPaste={false}
                    undoRedoCellEditing={true}
                    ensureDomOrder={true}
                    defaultColDef={{
                        minWidth: 85,
                        filter: true,
                        sortable: true,
                        resizable: true,
                        headerComponentParams: {
                            menuIcon: 'fa-bars'
                        }
                    }}
                    onGridReady={agGridReady}
                    modules={AllModules}
                    getContextMenuItems={getContextMenuItems}
                >
                </AgGridReact>
            </div>}
    </Box>);
};

export default DataSheetView;
