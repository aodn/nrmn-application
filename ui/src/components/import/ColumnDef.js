import AgGridHeader from './custom/header';

function cell(params) {
  if (params.data?.errors?.length > 0) {
    const fields = params.data.errors.map((e) => ({field: e.columnTarget.toLowerCase(), errorLevel: e.errorLevel}));
    const target = params.colDef.field.toLowerCase();
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
    editable: false
  },
  {
    field: 'diver',
    editable: true,
    pivot: true,
    enablePivot: false,
    cellStyle: cell
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
    minWidth: 105
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
    cellStyle: cell
  },
  {
    field: 'method',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    width: 105,

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
    minWidth:80,
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
    editable: true,
    cellStyle: cell
  },
  {
    field: 'commonName',
    headerName: 'Common Name',
    editable: true
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
    field: '2-5',
    headerName: '2.5',
    width: 105,
    minWidth:80,
    invertSize:  '0.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '1',
    headerComponentFramework: AgGridHeader
  },

  {
    field: '7-5',
    headerName: '7.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '1.5',
    headerComponentFramework: AgGridHeader,
  },
  {
    field: '10',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '2',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '12-5',
    headerName: '12.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '2.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '15',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '3',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '20',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '3.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '25',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '4',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '30',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '4.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '35',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '40',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '5.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '50',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '6',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '62-5',
    headerName: '62.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '6.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '75',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '7',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '87-5',
    headerName: '87.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '7.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '100',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '8',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '112-5',
    headerName: '112.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '8.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '125',
    editable: 75,
    width: 105,
    minWidth:80,
    invertSize:  '9',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '137-5',
    headerName: '137.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '9.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '150',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '10',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '162-5',
    headerName: '162.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '10.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '175',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '11',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '187-5',
    headerName: '187.5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '11.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '200',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '12',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '250',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '12.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '300',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '13',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '350',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '13.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '400',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '14',
    headerComponentFramework: AgGridHeader
  }
];

export const ExtendedSize = [
  {
    field: '450',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '14.5',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '500',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '15',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '550',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '16',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '600',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '17',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '650',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '18',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '700',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '19',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '750',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '20',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '800',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '22',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '850',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '24',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '900',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '26',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '950',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '28',
    headerComponentFramework: AgGridHeader
  },
  {
    field: '1000',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '30',
    headerComponentFramework: AgGridHeader
  },
  {
    field: 'l5',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '1',
    headerComponentFramework: AgGridHeader
  },
  {
    field: 'l95',
    editable: true,
    width: 105,
    minWidth:80,
    invertSize:  '1',
    headerComponentFramework: AgGridHeader
  },
  {
    field: 'inverts',
    editable: true
  },
  {
    field: 'm2InvertSizingSpecies',
    editable: true
  },
  {
    field: 'isInvertSizing',
    editable: true
  },
  {
    field: 'lmax',
    editable: true
  }
];
