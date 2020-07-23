import React from "react";
import { connect } from "react-redux";
import { AgGridReact } from "ag-grid-react";
import { AllModules } from "ag-grid-enterprise";
import { useState, useEffect } from 'react';



const mapStateToProps = state => {
    return { sheet: state.import.sheet, columnDefs: state.import.columnDefs };
};

function useWindowSize() {
    const isClient = typeof window === 'object';

    function getSize() {
        return {
            width: isClient ? window.innerWidth : undefined,
            height: isClient ? window.innerHeight : undefined
        };
    }

    const [windowSize, setWindowSize] = useState(getSize);

    useEffect(() => {
        if (!isClient) {
            return false;
        }

        function handleResize() {
            setWindowSize(getSize());
        }

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []); // Empty array ensures that effect is only run on mount and unmount

    return windowSize;
}




const ReduxDataSheetView = (data) => {
    const size = useWindowSize();

    console.log(size)
    console.log("dataviewSheet got:", data)
    return (data.sheet.length) ? (
        <div style={{ height: size.height - 170, width: '100%', marginTop: 25 }} className="ag-theme-alpine">
            <AgGridReact
                pivotMode={true}
                pivotColumnGroupTotals={"before"}
                sideBar={true}
                autoGroupColumnDef={{
                    width: 100,
                    cellRendererParams: {
                        suppressCount: true,
                        innerRenderer: 'nameCellRenderer'
                    }
                }}
                columnDefs={data.columnDefs}
                groupDefaultExpanded={4}
                rowData={data.sheet}
                rowSelection="multiple"
                animateRows={true}
                groupMultiAutoColumn={true}
                enableRangeSelection={true}
                groupHideOpenParents={true}
                groupUseEntireRow={true}

                defaultColDef={{
                    filter: true,
                    sortable: true,
                    resizable: true,
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
    ) : (<></>)
}

const DataSheetView = connect(mapStateToProps)(ReduxDataSheetView);
export default DataSheetView