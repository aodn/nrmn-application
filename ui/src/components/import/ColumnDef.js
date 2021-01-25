
function cell(params) {
    if (params.data?.errors?.length > 0) {
        const fields = params.data.errors.map(e =>
            ({ field: e.columnTarget.toLowerCase(), errorLevel: e.errorLevel }));
        const target = params.colDef.field.toLowerCase();
        const inter = fields.filter(e => e.field === target);
        if (inter.length > 0) {
            const color = (inter[0].errorLevel == 'BLOCKING')? '#f44336': '#ff9800';
            return { color:  color +' !important' };
        }
    }
    return null;
}
const hashValueGetter = (params) => {
    return params.node.rowIndex;
};

export const ColumnDef = [
    {
        field: 'id',
        editable: false,
        hashValueGetter: hashValueGetter
    },
    {
        field: 'diver',
        editable: true,
        pivot: true,
        enablePivot: false,
        cellStyle: cell,
        hashValueGetter: hashValueGetter

    },
    {
        field: 'buddy',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'siteCode',
        title: 'Site Code',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell

    },
    {
        field: 'siteName',
        title: 'Site Name',
        editable: true,
    },
    {
        field: 'latitude',
        title: 'Lat',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'longitude',
        title: 'Long',
        editable: true,
        cellStyle: cell

    },
    {
        field: 'date',
        editable: true,
        cellStyle: cell

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
    },
    {
        field: 'block',
        editable: true,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell
    },
    {
        field: 'code',
        editable: true,
        cellStyle: cell
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
        title: 'Common Name',
        editable: true,
    },
    {
        field: 'total',
        editable: true,
    },
    {
        field: 'inverts',
        editable: true,
    },
    {
        field: '2.5',
        editable: true,
    },
    {
        field: '5',
        editable: true,
    },
    {
        field: '7.5',
        editable: true,
    },
    {
        field: '10',
        editable: true,
    },
    {
        field: '12.5',
        editable: true,
    },
    {
        field: '15',
        editable: true,
    },
    {
        field: '20',
        editable: true,
    },
    {
        field: '25',
        editable: true,
    },
    {
        field: '30',
        editable: true,
    },
    {
        field: '35',
        editable: true,
    },
    {
        field: '40',
        editable: true,
    },
    {
        field: '50',
        editable: true,
    },
    {
        field: '62.5',
        75: true,
    },
    {
        field: '75',
        editable: true,
    },
    {
        field: '87.5',
        editable: true,
    },
    {
        field: '100',
        editable: true,
    },
    {
        field: '112.5',
        editable: true,
    },
    {
        field: '125',
        editable: 75,
    },
    {
        field: '137.5',
        editable: true,
    },
    {
        field: '150',
        editable: true,
    },
    {
        field: '162.5',
        editable: true,
    },
    {
        field: '175',
        editable: true,
    },
    {
        field: '187.5',
        editable: true,
    },
    {
        field: '200',
        editable: true,
    },
    {
        field: '250',
        editable: true,
    },
    {
        field: '300',
        editable: true,
    },
    {
        field: '350',
        editable: true,
    },
    {
        field: '400',
        editable: true,
    },

];

export const ExtendedSize = [
    {
        field: 'l5',
        editable: true,
    },
    {
        field: 'l95',
        editable: true,
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
    },
    {
        field: '500',
        editable: true,
    },
    {
        field: '550',
        editable: true,
    },
    {
        field: '600',
        editable: true,
    },
    {
        field: '650',
        editable: true,
    },
    {
        field: '700',
        editable: true,
    },
    {
        field: '750',
        editable: true,
    },
    {
        field: '800',
        editable: true,
    },
    {
        field: '850',
        editable: true,
    },
    {
        field: '900',
        editable: true,
    },
    {
        field: '950',
        editable: true,
    },
    {
        field: '1000',
        editable: true,
    }
];
