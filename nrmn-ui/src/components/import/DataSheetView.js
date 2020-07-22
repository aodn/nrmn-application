import React from "react";
import { connect } from "react-redux";
import { AgGridReact } from "ag-grid-react";
import { AllModules } from "ag-grid-enterprise";
const mapStateToProps = state => {
    return { sheet: state.import.sheet, columnDefs: state.import.columnDefs };
};

const ReduxDataSheetView = (data) => {
    console.log("dataviewSheet got:", data)
    return (data.sheet.length) ? (
        <div style={{ height: 650, width: '100%', marginTop: 25 }} className="ag-theme-alpine">
            <AgGridReact
                // properties
                pivotMode={true}
                pivotColumnGroupTotals={"before"}
                sideBar={true}
                autoGroupColumnDef={{ minWidth: 200 }}
                columnDefs={data.columnDefs}
                rowData={data.sheet}
                rowSelection="multiple"
                animateRows
                groupMultiAutoColumn={true}
                enableRangeSelection={true}
                defaultColDef={{
                    sortable: true,
                    filter: true,

                    headerComponentParams: {
                        menuIcon: 'fa-bars'
                    }
                }
                }
                modules={AllModules}
            //onGridReady={onGridReady}
            >
            </AgGridReact>
        </div>
    ) : (<div>no data</div>)
}

const DataSheetView = connect(mapStateToProps)(ReduxDataSheetView);
export default DataSheetView