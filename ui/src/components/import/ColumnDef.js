
function cell(params) {
    if (params.data?.errors?.length > 0) {
        const fields = params.data.errors.map(e =>
            ({ field: e.columnTarget.toLowerCase(), errorLevel: e.errorLevel }));
        const target = params.colDef.field.toLowerCase();
        const inter = fields.filter(e => e.field === target);
        if (inter.length > 0) {
            const color = (inter[0].errorLevel == 'BLOCKING') ? '#f44336' : '#ff9800';
            return { color: color + ' !important' };
        }
    }
    return null;
}


export const ColumnDef = [
    {
        field: 'id',
        editable: false,
    },
    {
        field: 'diver',
        editable: true,
        pivot: true,
        enablePivot: false,
        cellStyle: cell,

    },
    {
        field: 'buddy',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'siteCode',
        headerName: 'Site Code',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell

    },
    {
        field: 'siteName',
        headerName: 'Site Name',
        editable: true,
    },
    {
        field: 'latitude',
        headerName: 'Lat',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'longitude',
        headerName: 'Long',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'date',
        editable: true,
        cellStyle: cell,
        minWidth: 105

    },
    {
        field: 'vis',
        editable: true,
    },
    {
        field: 'direction',
        editable: true,
        cellStyle: cell
    },
    {
        field: 'time',
        editable: true,
        cellStyle: cell
    },
    {
        field: 'P-Qs',
        editable: true,
        cellStyle: cell
    },
    {
        field: 'depth',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell
    },
    {
        field: 'method',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        width: 105
    },
    {
        field: 'block',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell,
        width: 80
    },
    {
        field: 'code',
        editable: true,
        cellStyle: cell,
        width: 105,
        minWidth: 50
    },
    {
        field: 'species',
        editable: true,
        hide: true,
        'aggFunc': 'count',
        cellStyle: cell

    },
    {
        field: 'species',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'commonName',
        headerName: 'Common Name',
        editable: true,
    },
    {
        field: 'total',
        editable: true,
        width: 80
    },
    {
        field: 'inverts',
        editable: true,
        width: 80

    },
    {
        field: '2.5',
        editable: true,
        width: 105,

    },
    {
        field: '5',
        editable: true,
        width: 105,
    },
    {
        field: '7.5',
        editable: true,
        width: 105,

    },
    {
        field: '10',
        editable: true,
        width: 105,

    },
    {
        field: '12.5',
        editable: true,
        width: 105,

    },
    {
        field: '15',
        editable: true,
        width: 105,
    },
    {
        field: '20',
        editable: true,
        width: 105,

    },
    {
        field: '25',
        editable: true,
        width: 105,

    },
    {
        field: '30',
        editable: true,
        width: 105,

    },
    {
        field: '35',
        editable: true,
        width: 105,

    },
    {
        field: '40',
        editable: true,
        width: 105,

    },
    {
        field: '50',
        editable: true,
        width: 105,

    },
    {
        field: '62.5',
        editable: true,
        width: 105,

    },
    {
        field: '75',
        editable: true,
        width: 105,

    },
    {
        field: '87.5',
        editable: true,
        width: 105,

    },
    {
        field: '100',
        editable: true,
        width: 105,
    },
    {
        field: '112.5',
        editable: true,
        width: 105,
    },
    {
        field: '125',
        editable: 75,
        width: 105,

    },
    {
        field: '137.5',
        editable: true,
        width: 105,

    },
    {
        field: '150',
        editable: true,
        width: 105,

    },
    {
        field: '162.5',
        editable: true,
        width: 105,

    },
    {
        field: '175',
        editable: true,
        width: 105,

    },
    {
        field: '187.5',
        editable: true,
        width: 105,

    },
    {
        field: '200',
        editable: true,
        width: 105,

    },
    {
        field: '250',
        editable: true,
        width: 105,

    },
    {
        field: '300',
        editable: true,
        width: 105,

    },
    {
        field: '350',
        editable: true,
        width: 105,
    },
    {
        field: '400',
        editable: true,
        width: 105,

    },

];

export const ExtendedSize = [
    {
        field: 'l5',
        editable: true,
        width: 105,

    },
    {
        field: 'l95',
        editable: true,
        width: 105,

    },
    {
        field: 'inverts',
        editable: true,
    },
    {
        field: 'm2InvertSizingSpecies',
        editable: true,
    }, {
        field: 'isInvertSizing',
        editable: true,
    },
    {
        field: 'lmax',
        editable: true,
    },
    {
        field: '450',
        editable: true,
        width: 105,

    },
    {
        field: '500',
        editable: true,
        width: 105,

    },
    {
        field: '550',
        editable: true,
        width: 105,

    },
    {
        field: '800',
        editable: true,
        width: 105,

    },
    {
        field: '650',
        editable: true,
        width: 105,

    },
    {
        field: '700',
        editable: true,
        width: 105,

    },
    {
        field: '750',
        editable: true,
        width: 105,

    },
    {
        field: '800',
        editable: true,
        width: 105,

    },
    {
        field: '850',
        editable: true,
        width: 105,

    },
    {
        field: '900',
        editable: true,
        width: 105,

    },
    {
        field: '950',
        editable: true,
        width: 105,

    },
    {
        field: '1000',
        editable: true,
        width: 105,

    }
];
