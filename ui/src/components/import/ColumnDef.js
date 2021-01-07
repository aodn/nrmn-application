
function cell(params) {
    if (params.data?.errors?.length > 0) {
        const fields = params.data.errors.map(e => e.columnTarget.toLowerCase());
        const inter = fields.filter(e => e === params.colDef.field);
        if (inter.length > 0)
            return { color: 'red !important' };
    }
    return null;
}

const ColunmDef = [
    {
        field: 'id',
        editable: false,
        width: 75
    },
    {
        field: 'diver',
        editable: true,
        width: 100,
        pivot: true,
        enablePivot: false,
        cellStyle: cell

    },
    {
        field: 'buddy',
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: 'site #',
        editable: true,
        width: 200,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell

    },
    {
        field: 'site name',
        editable: true,
        width: 200
    },
    {
        field: 'latitude',
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: 'longitude',
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: 'date',
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: 'vis',
        editable: true,
        width: 100
    },
    {
        field: 'direction',
        editable: true,
        width: 100,
        cellStyle: cell
    },
    {
        field: 'time',
        editable: true,
        width: 100,
        cellStyle: cell
    },
    {
        field: 'P-Qs',
        editable: true,
        width: 100,
        cellStyle: cell
    },
    {
        field: 'depth',
        editable: true,
        width: 50,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell
    },
    {
        field: 'method',
        editable: true,
        width: 50,
        rowGroup: false,
        enableRowGroup: true,
    },
    {
        field: 'block',
        editable: true,
        width: 50,
        rowGroup: false,
        enableRowGroup: true,
        cellStyle: cell
    },
    {
        field: 'code',
        editable: true,
        width: 75,
        cellStyle: cell
    },
    {
        field: 'species',
        editable: true,
        width: 100,
        hide: true,
        'aggFunc': 'count',
        cellStyle: cell

    },
    {
        field: 'species',
        editable: true,
        width: 100,
        cellStyle: cell

    },
    {
        field: 'common name',
        editable: true,
        width: 100
    },
    {
        field: 'total',
        editable: true,
        width: 100
    },
    {
        field: 'inverts',
        editable: true,
        width: 100
    },
    {
        field: '2.5',
        editable: true,
        width: 50
    },
    {
        field: '5',
        editable: true,
        width: 50
    },
    {
        field: '7.5',
        editable: true,
        width: 50
    },
    {
        field: '10',
        editable: true,
        width: 50
    },
    {
        field: '12.5',
        editable: true,
        width: 50
    },
    {
        field: '15',
        editable: true,
        width: 100
    },
    {
        field: '20',
        editable: true,
        width: 50
    },
    {
        field: '25',
        editable: true,
        width: 50
    },
    {
        field: '30',
        editable: true,
        width: 50
    },
    {
        field: '35',
        editable: true,
        width: 50
    },
    {
        field: '40',
        editable: true,
        width: 50
    },
    {
        field: '50',
        editable: true,
        width: 50
    },
    {
        field: '62.5',
        50: true,
        width: 50
    },
    {
        field: '75',
        editable: true,
        width: 50
    },
    {
        field: '87.5',
        editable: true,
        width: 50
    },
    {
        field: '100',
        editable: true,
        width: 100
    },
    {
        field: '112.5',
        editable: true,
        width: 50
    },
    {
        field: '125',
        editable: 50,
        width: 100
    },
    {
        field: '137.5',
        editable: true,
        width: 100
    },
    {
        field: '150',
        editable: true,
        width: 100
    },
    {
        field: '162.5',
        editable: true,
        width: 100
    },
    {
        field: '175',
        editable: true,
        width: 100
    },
    {
        field: '187.5',
        editable: true,
        width: 100
    },
    {
        field: '200',
        editable: true,
        width: 100
    },
    {
        field: '250',
        editable: true,
        width: 100
    },
    {
        field: '300',
        editable: true,
        width: 100
    },
    {
        field: '350',
        editable: true,
        width: 100
    },
    {
        field: '400',
        editable: true,
        width: 100
    },
    {
        field: '450',
        editable: true,
        width: 100
    },
    {
        field: '500',
        editable: true,
        width: 100
    },
    {
        field: '550',
        editable: true,
        width: 100
    },
    {
        field: '600',
        editable: true,
        width: 100
    },
    {
        field: '650',
        editable: true,
        width: 100
    },
    {
        field: '700',
        editable: true,
        width: 100
    },
    {
        field: '750',
        editable: true,
        width: 100
    },
    {
        field: '800',
        editable: true,
        width: 100
    },
    {
        field: '850',
        editable: true,
        width: 100
    },
    {
        field: '900',
        editable: true,
        width: 100
    },
    {
        field: '950',
        editable: true,
        width: 100
    },
    {
        field: '1000',
        editable: true,
        width: 100
    }
];

export default ColunmDef;