
function cell(params) {
    if (params.data.Errrors && params.data.Errors.length > 0) {
        const present = params.data.Errors.map(e => e.ColunmTarget)
        const inter = present.filter(e => e === params.colDef.field);
        if (inter.length > 0)
            return {backgroundColor: 'red'};
    }
    return  null;
}

const ColunmDef = [
    {
        field: "Diver",
        editable: true,
        width: 100,
        pivot: true,
        enablePivot: true,
        cellStyle: cell

    },
    {
        field: "Buddy",
        editable: true,
        width: 100
    },
    {
        field: "Site No",
        editable: true,
        width: 100,
        hide: true,
        rowGroup: true,
        enableRowGroup: true,
        cellStyle: cell

    },
    {
        field: "Site Name",
        editable: true,
        width: 100
    },
    {
        field: "Latitude",
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: "Longitude",
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: "Date",
        editable: true,
        width: 100
    },
    {
        field: "vis",
        editable: true,
        width: 100
    },
    {
        field: "Direction",
        editable: true,
        width: 100
    },
    {
        field: "Time",
        editable: true,
        width: 100
    },
    {
        field: "P-Qs",
        editable: true,
        width: 100
    },
    {
        field: "Depth",
        editable: true,
        width: 100,
        hide: true,
        rowGroup: true,
        enableRowGroup: true,
    },
    {
        field: "Method",
        editable: true,
        width: 100,
        hide: true,
        rowGroup: true,
        enableRowGroup: true,
    },
    {
        field: "Block",
        editable: true,
        width: 100,
        hide: true,
        rowGroup: true,
        enableRowGroup: true,
    },
    {
        field: "Code",
        editable: true,
        width: 100
    },
    {
        field: "Species",
        editable: true,
        width: 100,
        "aggFunc": "count"
    },
    {
        field: "Common name",
        editable: true,
        width: 100
    },
    {
        field: "Total",
        editable: true,
        width: 100
    },
    {
        field: "Inverts",
        editable: true,
        width: 100
    },
    {
        field: "2.5",
        editable: true,
        width: 100
    },
    {
        field: "5",
        editable: true,
        width: 100
    },
    {
        field: "7.5",
        editable: true,
        width: 100
    },
    {
        field: "10",
        editable: true,
        width: 100
    },
    {
        field: "12.5",
        editable: true,
        width: 100
    },
    {
        field: "15",
        editable: true,
        width: 100
    },
    {
        field: "20",
        editable: true,
        width: 100
    },
    {
        field: "25",
        editable: true,
        width: 100
    },
    {
        field: "30",
        editable: true,
        width: 100
    },
    {
        field: "35",
        editable: true,
        width: 100
    },
    {
        field: "40",
        editable: true,
        width: 100
    },
    {
        field: "50",
        editable: true,
        width: 100
    },
    {
        field: "62.5",
        editable: true,
        width: 100
    },
    {
        field: "75",
        editable: true,
        width: 100
    },
    {
        field: "87.5",
        editable: true,
        width: 100
    },
    {
        field: "100",
        editable: true,
        width: 100
    },
    {
        field: "112.5",
        editable: true,
        width: 100
    },
    {
        field: "125",
        editable: true,
        width: 100
    },
    {
        field: "137.5",
        editable: true,
        width: 100
    },
    {
        field: "150",
        editable: true,
        width: 100
    },
    {
        field: "162.5",
        editable: true,
        width: 100
    },
    {
        field: "175",
        editable: true,
        width: 100
    },
    {
        field: "187.5",
        editable: true,
        width: 100
    },
    {
        field: "200",
        editable: true,
        width: 100
    },
    {
        field: "250",
        editable: true,
        width: 100
    },
    {
        field: "300",
        editable: true,
        width: 100
    },
    {
        field: "350",
        editable: true,
        width: 100
    },
    {
        field: "400",
        editable: true,
        width: 100
    },
    {
        field: "450",
        editable: true,
        width: 100
    },
    {
        field: "500",
        editable: true,
        width: 100
    },
    {
        field: "550",
        editable: true,
        width: 100
    },
    {
        field: "600",
        editable: true,
        width: 100
    },
    {
        field: "650",
        editable: true,
        width: 100
    },
    {
        field: "700",
        editable: true,
        width: 100
    },
    {
        field: "750",
        editable: true,
        width: 100
    },
    {
        field: "800",
        editable: true,
        width: 100
    },
    {
        field: "850",
        editable: true,
        width: 100
    },
    {
        field: "900",
        editable: true,
        width: 100
    },
    {
        field: "950",
        editable: true,
        width: 100
    },
    {
        field: "1000",
        editable: true,
        width: 100
    }
];

export default ColunmDef;