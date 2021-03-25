import AgGridHeader from './custom/header';

function cell(params) {
  if (params.data?.errors?.length > 0) {
    const fields = params.data.errors.map((e) => ({field: e.columnTarget?.toLowerCase(), errorLevel: e.errorLevel}));
    const target = params.colDef.field?.toLowerCase();
    const inter = fields.filter((e) => e.field === target);
    if (inter.length > 0) {
      const color = inter[0].errorLevel == 'BLOCKING' ? '#f44336' : '#ff9800';
      return {color: color + ' !important'};
    }
  }
  return null;
}

export const ColumnDef = [
  {
    field: 'id',
    editable: false,
    hide: true
  },
  {
    field: 'pos',
    editable: false,
    headerName: '#',
    sort: 'asc'
  },
  {
    field: 'diver',
    editable: true,
    pivot: true,
    enablePivot: false,
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'buddy',
    editable: true,
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'siteCode',
    headerName: 'Site Code',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'siteName',
    headerName: 'Site Name',
    editable: true
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
    rowGroup: false,
    enableRowGroup: true,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'vis',
    editable: true
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
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'method',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'block',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
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
    aggFunc: 'count',
    cellStyle: cell
  },
  {
    field: 'species',
    pivot: true,
    enablePivot: false,
    rowGroup: false,
    enableRowGroup: true,
    aggFunc: 'count',
    editable: true,
    cellStyle: cell,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'commonName',
    headerName: 'Common Name',
    editable: true
  },
  {
    field: 'total',
    editable: true,
    aggFunc: 'count',
    rowGroup: false,
    enableRowGroup: true
  },
  {
    field: 'inverts',
    editable: true
  },
  {
    field: '2-5',
    headerName: '2.5',
    invertSize: '0.5',
    editable: true,

    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '5',
    editable: true,
    invertSize: '1',
    headerComponentFramework: AgGridHeader,
    width: 35
  },

  {
    field: '7-5',
    headerName: '7.5',
    editable: true,

    invertSize: '1.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '10',
    editable: true,
    invertSize: '2',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '12-5',
    headerName: '12.5',
    editable: true,

    invertSize: '2.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '15',
    editable: true,

    invertSize: '3',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '20',
    editable: true,

    invertSize: '3.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '25',
    editable: true,

    invertSize: '4',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '30',
    editable: true,

    invertSize: '4.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '35',
    editable: true,

    invertSize: '5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '40',
    editable: true,

    invertSize: '5.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '50',
    editable: true,

    invertSize: '6',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '62-5',
    headerName: '62.5',
    editable: true,

    invertSize: '6.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '75',
    editable: true,

    invertSize: '7',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '87-5',
    headerName: '87.5',
    editable: true,

    invertSize: '7.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '100',
    editable: true,

    invertSize: '8',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '112-5',
    headerName: '112.5',
    editable: true,

    invertSize: '8.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '125',
    editable: 75,

    invertSize: '9',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '137-5',
    headerName: '137.5',
    editable: true,

    invertSize: '9.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '150',
    editable: true,

    invertSize: '10',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '162-5',
    headerName: '162.5',
    editable: true,

    invertSize: '10.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '175',
    editable: true,

    invertSize: '11',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '187-5',
    headerName: '187.5',
    editable: true,

    invertSize: '11.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '200',
    editable: true,

    invertSize: '12',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '250',
    editable: true,

    invertSize: '12.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '300',
    editable: true,

    invertSize: '13',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '350',
    editable: true,

    invertSize: '13.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '400',
    editable: true,

    invertSize: '14',
    headerComponentFramework: AgGridHeader,
    width: 35
  }
];

export const ExtendedSize = [
  {
    field: '450',
    editable: true,

    invertSize: '14.5',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '500',
    editable: true,

    invertSize: '15',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '550',
    editable: true,

    invertSize: '16',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '600',
    editable: true,

    invertSize: '17',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '650',
    editable: true,

    invertSize: '18',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '700',
    editable: true,

    invertSize: '19',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '750',
    editable: true,

    invertSize: '20',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '800',
    editable: true,

    invertSize: '22',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '850',
    editable: true,

    invertSize: '24',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '900',
    editable: true,

    invertSize: '26',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '950',
    editable: true,

    invertSize: '28',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: '1000',
    editable: true,

    invertSize: '30',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: 'l5',
    editable: true,

    invertSize: '1',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: 'l95',
    editable: true,

    invertSize: '1',
    headerComponentFramework: AgGridHeader,
    width: 35
  },
  {
    field: 'inverts',
    editable: true,
    width: 35
  },
  {
    field: 'm2InvertSizingSpecies',
    editable: true,
    width: 55
  },
  {
    field: 'isInvertSizing',
    editable: true,
    width: 40
  },
  {
    field: 'lmax',
    editable: true,
    width: 35
  }
];
