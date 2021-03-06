import React from 'react';
import AgGridHeader from './custom/header';
import GridHeader from '../datasheets/GridHeader';

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

function cellRenderer(p) {
  const mask = p.data?.selected?.find((s) => s.key === p.colDef.field)?.value;
  if (mask) {
    const prefix = p.value.substr(0, mask.idx);
    const text = `<span style="background-color: yellow">${p.value.substr(mask.idx, mask.length)}</span>`;
    const suffix = p.value.substr(mask.idx + mask.length);
    return prefix + text + suffix;
  } else {
    return p.value;
  }
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
    cellRenderer: cellRenderer,

    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'buddy',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer,

    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'siteCode',
    headerName: 'Site Code',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    cellStyle: cell,
    cellRenderer: cellRenderer,

    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'siteName',
    headerName: 'Site Name',
    cellStyle: cell,
    cellRenderer: cellRenderer,

    editable: true
  },
  {
    field: 'latitude',
    headerName: 'Lat',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'longitude',
    headerName: 'Long',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'date',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer,
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
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'time',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'P-Qs',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'depth',
    editable: true,
    rowGroup: false,
    enableRowGroup: true,
    cellStyle: cell,
    cellRenderer: cellRenderer,
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
    cellRenderer: cellRenderer,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'code',
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer
  },
  {
    field: 'species',
    pivot: true,
    enablePivot: false,
    enableRowGroup: true,
    editable: true,
    cellStyle: cell,
    cellRenderer: cellRenderer,
    keyCreator: (params) => params.value?.toLowerCase()
  },
  {
    field: 'commonName',
    headerName: 'Common Name',
    cellRenderer: cellRenderer,
    editable: true
  },
  {
    field: 'total',
    editable: true,
    aggFunc: 'count',
    cellStyle: cell
  },
  {
    field: 'inverts',
    editable: true,
    cellStyle: cell
  },
  {
    field: '2-5',
    editable: true,
    headerComponent: <GridHeader fishSize="2.5" invertSize="0.5" />,
    cellStyle: cell,
    width: 35
  },
  {
    field: '5',
    editable: true,
    invertSize: '1',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },

  {
    field: '7-5',
    headerName: '7.5',
    editable: true,
    invertSize: '1.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '10',
    editable: true,
    invertSize: '2',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '12-5',
    headerName: '12.5',
    editable: true,
    invertSize: '2.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '15',
    editable: true,
    invertSize: '3',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '20',
    editable: true,
    invertSize: '3.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '25',
    editable: true,
    invertSize: '4',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '30',
    editable: true,
    invertSize: '4.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '35',
    editable: true,
    invertSize: '5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '40',
    editable: true,
    invertSize: '5.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '50',
    editable: true,

    invertSize: '6',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '62-5',
    headerName: '62.5',
    editable: true,

    invertSize: '6.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '75',
    editable: true,

    invertSize: '7',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '87-5',
    headerName: '87.5',
    editable: true,

    invertSize: '7.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '100',
    editable: true,

    invertSize: '8',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '112-5',
    headerName: '112.5',
    editable: true,

    invertSize: '8.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '125',
    editable: 75,

    invertSize: '9',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '137-5',
    headerName: '137.5',
    editable: true,

    invertSize: '9.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '150',
    editable: true,

    invertSize: '10',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '162-5',
    headerName: '162.5',
    editable: true,

    invertSize: '10.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '175',
    editable: true,

    invertSize: '11',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '187-5',
    headerName: '187.5',
    editable: true,

    invertSize: '11.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '200',
    editable: true,

    invertSize: '12',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '250',
    editable: true,

    invertSize: '12.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '300',
    editable: true,

    invertSize: '13',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '350',
    editable: true,

    invertSize: '13.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '400',
    editable: true,

    invertSize: '14',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  }
];

export const ExtendedSize = [
  {
    field: '450',
    editable: true,

    invertSize: '14.5',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '500',
    editable: true,

    invertSize: '15',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '550',
    editable: true,

    invertSize: '16',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '600',
    editable: true,

    invertSize: '17',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '650',
    editable: true,

    invertSize: '18',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '700',
    editable: true,

    invertSize: '19',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '750',
    editable: true,

    invertSize: '20',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '800',
    editable: true,

    invertSize: '22',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '850',
    editable: true,

    invertSize: '24',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '900',
    editable: true,

    invertSize: '26',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '950',
    editable: true,

    invertSize: '28',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: '1000',
    editable: true,
    invertSize: '30',
    headerComponentFramework: AgGridHeader,
    cellStyle: cell,
    width: 35
  },
  {
    field: 'isInvertSizing',
    headerName: 'Use InvertSizing',
    editable: true,
    width: 40
  }
];
