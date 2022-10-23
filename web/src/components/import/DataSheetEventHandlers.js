import {blue, grey, orange, red, yellow} from '@mui/material/colors';
import {extendedMeasurements, measurements} from '../../common/constants';

class DataSheetEventHandlers {
  constructor() {
    this.fillRegion = this.fillRegion.bind(this);
    this.handleUndo = this.handleUndo.bind(this);
    this.onClearRegion = this.onClearRegion.bind(this);
    this.onCopyRegion = this.onCopyRegion.bind(this);
    this.onCutRegion = this.onCutRegion.bind(this);
    this.overrideKeyboardEvents = this.overrideKeyboardEvents.bind(this);
  }

  pushUndo(api, delta) {
    const context = api.gridOptionsWrapper.gridOptions.context;
    context.undoStack.push(
      delta.map((d) => {
        context.putRowIds.push(d.id);
        return {...d};
      })
    );
    return context.undoStack.length;
  }

  resetContext() {
    this.context = this.context || {};
    this.context.errorList = {};
    this.context.errors = [];
    this.context.focusedRows = [];
    this.context.highlighted = [];
    this.context.pasteMode = false;
    this.context.pendingPasteUndo = [];
    this.context.putRowIds = [];
    this.context.rowData = [];
    this.context.rowPos = [];
    this.context.summary = [];
    this.context.useOverlay = 'Loading';
  }

  dateComparator(date1, date2) {
    const dateToNum = (date) => {
      if (date === undefined || (date === null && (date.length !== 10 || date.length !== 8))) return null;
      const yearNumber = date.length === 10 ? date.substring(6, 10) : '20' + date.substring(6, 8);
      const monthNumber = date.substring(3, 5);
      const dayNumber = date.substring(0, 2);
      return yearNumber * 10000 + monthNumber * 100 + dayNumber;
    };
    const date1Number = dateToNum(date1);
    const date2Number = dateToNum(date2);
    if (date1Number === null && date2Number === null) return 0;
    if (date1Number === null) return -1;
    if (date2Number === null) return 1;
    return date1Number - date2Number;
  }

  fillRegion(e, fill) {
    const rowData = e.context.rowData;
    const [cells] = e.api.getCellRanges();
    const fields = cells.columns.map((col) => col.colId);
    const columnDefs = e.api.columnModel.columnDefs;
    const delta = [];
    const startIdx = Math.min(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const endIdx = Math.max(cells.startRow.rowIndex, cells.endRow.rowIndex);
    for (let i = startIdx; i < endIdx + 1; i++) {
      const row = e.api.getDisplayedRowAtIndex(i);
      const dataIdx = rowData.findIndex((d) => d.id == row.data.id);
      const data = {...rowData[dataIdx]};
      delta.push(data);
      let newData = {};
      Object.keys(data).forEach((key) => (newData[key] = data[key]));
      fields
        .filter((key) => !(columnDefs.find((d) => d.field === key)?.editable == false))
        .forEach((key) => {
          if (key.includes('.') > 0) {
            const splitKey = key.split('.');
            newData[splitKey[0]][splitKey[1]] = fill;
          } else {
            newData[key] = fill;
          }
        });
      rowData[dataIdx] = newData;
    }
    this.pushUndo(e.api, delta);
    e.api.setRowData(rowData);
  }

  popUndo(api) {
    const context = api.gridOptionsWrapper.gridOptions.context;
    const deltaSet = context.undoStack.pop();
    let rowData = context.rowData;
    for (const deltaIdx in deltaSet) {
      const deltaId = deltaSet[deltaIdx].id;
      context.putRowIds.push(deltaId);
      const rowIdx = rowData.findIndex((d) => d.id === deltaId);
      if (Object.keys(deltaSet[deltaIdx]).length < 2) {
        rowData.splice(rowIdx, 1);
      } else {
        if (rowIdx < 0) {
          rowData.push(deltaSet[deltaIdx]);
        } else {
          rowData[rowIdx] = deltaSet[deltaIdx];
        }
      }
    }
    context.fullRefresh = true;
    api.setRowData(rowData);
    context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
    return context.undoStack.length;
  }

  chooseCellStyle(params) {
    // Read-only columns
    if (!params.colDef.editable) return {color: grey[800], backgroundColor: grey[50]};

    // Search results
    const row = params.context.highlighted[params.rowIndex];
    if (row && row[params.colDef.field]) return {backgroundColor: yellow[100]};

    // Cell validations
    if (params.context.cellValidations) {
      const row = params.data.id;

      const field = params.colDef.field;
      const level = params.context.cellValidations[row]?.[field]?.levelId;
      switch (level) {
        case 'BLOCKING':
          return {backgroundColor: red[100]};
        case 'WARNING':
          return {backgroundColor: orange[100]};
        case 'INFO':
          return {backgroundColor: grey[100]};
      }

      if (params.context.cellValidations[row]?.id?.levelId === 'DUPLICATE') return {backgroundColor: blue[100]};

    } else {
      const error = params.context.errors.find(
        (e) => e.rowIds.includes(params.data.id) && (!e.columnNames || e.columnNames.includes(params.colDef.field))
      );
      switch (error?.levelId) {
        case 'BLOCKING':
          return {backgroundColor: red[100]};
        case 'WARNING':
          return {backgroundColor: orange[100]};
        case 'INFO':
          return {backgroundColor: grey[100]};
        case 'DUPLICATE':
          if (params.context.focusedRows?.includes(params.data.id)) {
            return {backgroundColor: blue[100], fontWeight: 'bold'};
          } else {
            return {backgroundColor: blue[100], fontWeight: 'normal'};
          }
      }
    }
  }

  onSortChanged(e) {
    e.api.refreshCells();
  }

  toolTipValueGetter(params) {
    const error = params.context.errors.find(
      (e) => e.rowIds.includes(params.data.id) && (!e.columnNames || e.columnNames.includes(params.colDef.field))
    );

    if (error?.levelId === 'DUPLICATE') {
      const rowPositions = error.rowIds.map((r) => params.context.rowData.find((d) => d.id === r)?.pos).filter((r) => r);
      const duplicates = rowPositions.map((r) => params.context.rowPos.indexOf(r) + 1);
      return duplicates.length > 1 ? 'Rows are duplicated: ' + duplicates.join(', ') : 'Duplicate rows have been removed';
    }

    return error?.message;
  }

  rowValueGetter(e) {
    return e.context.rowPos ? e.context.rowPos.indexOf(e.data.pos) + 1 : 0;
  }

  generateErrorTree(rowData, rowPos, errors) {
    const tree = {blocking: [], warning: [], info: [], duplicate: []};
    errors
      .sort((a, b) => (a.message < b.message ? -1 : a.message > b.message ? 1 : 0))
      .forEach((e) => {
        const rows = rowData.filter((r) => e.rowIds.includes(r.id));
        let summary = [];
        if (e.columnNames && e.categoryId !== 'SPAN') {
          const col = e.columnNames[0];
          summary = rows.reduce((acc, r) => {
            const rowPosition = rowData.find((d) => d.id === r.id)?.pos;
            const rowNumber = rowPos.indexOf(rowPosition) + 1;
            const existingIdx = acc.findIndex((m) => m.columnName === col && m.value === r[col]);
            if (existingIdx >= 0 && isNaN(parseInt(acc[existingIdx].columnName)))
              acc[existingIdx] = {
                columnName: col,
                value: r[col],
                rowIds: [...acc[existingIdx].rowIds, r.id],
                rowNumbers: [...acc[existingIdx].rowNumbers, rowNumber]
              };
            else
              acc.push({columnName: col, value: r[col], rowIds: [r.id], rowNumbers: [rowNumber], isInvertSize: r.isInvertSizing === 'Yes'});
            return acc;
          }, []);
        } else {
          const rowPositions = e.rowIds.map((r) => rowData.find((d) => d.id === r)?.pos).filter((r) => r);
          const rowNumbers = rowPositions.map((r) => rowPos.indexOf(r) + 1);
          summary = [{rowIds: e.rowIds, columnNames: e.columnNames, rowNumbers}];
        }
        tree[e.levelId.toLowerCase()].push({key: `err-${e.id}`, message: e.message, count: e.rowIds.length, description: summary});
      });
    return tree;
  }

  onPasteStart(e) {
    const context = e.api.gridOptionsWrapper.gridOptions.context;
    context.pasteMode = true;
  }

  onCopyRegion(e) {
    e.api.copySelectedRangeToClipboard();
  }

  onClickExcelExport(api, name, isExtended) {
    const columns = [
      'id',
      'diver',
      'buddy',
      'siteCode',
      'siteName',
      'latitude',
      'longitude',
      'date',
      'vis',
      'direction',
      'time',
      'P-Qs',
      'depth',
      'method',
      'block',
      'code',
      'species',
      'commonName',
      'total',
      'inverts',
      ...measurements.map((m) => m.field)
    ];

    const extendedColumns = [...extendedMeasurements.map((m) => m.field), 'isInvertSizing'];
    const requiredColumns = isExtended ? [...columns, ...extendedColumns] : columns;
    const headers = [];

    requiredColumns.forEach((x) => {
      // Get the row display name from the fields, this is because we turn on skipColumnHeaders so that
      // we can add empty row, '' is used to force type to string.
      const column = api.getColumnDefs().filter((y) => y.field === x)[0]?.headerName;
      if(column) headers.push({data: {value: '' + column, type: 'String'}});
    });

    api.exportDataAsExcel({
      sheetName: 'DATA',
      author: 'NRMN',
      columnKeys: requiredColumns,
      skipColumnHeaders: true,
      prependContent: [headers, []],
      fileName: `export_${name}`
    });
  }

  onCellKeyDown(e) {
    const editingCells = e.api.getEditingCells();
    if (editingCells.length === 1) {
      const context = e.api.gridOptionsWrapper.gridOptions.context;
      context.navigationKey = e.event.key;
      if (['ArrowLeft', 'ArrowUp'].includes(e.event.key)) {
        e.event.preventDefault();
        e.api.stopEditing();
        e.api.tabToPreviousCell();
      }
      if (['ArrowRight', 'ArrowDown'].includes(e.event.key)) {
        e.event.preventDefault();
        e.api.stopEditing();
        e.api.tabToNextCell();
      }
      context.navigationKey = '';
    }
  }

  onTabToNextCell(e) {
    let context = e.api.gridOptionsWrapper.gridOptions.context;
    let result;

    if (['ArrowUp', 'ArrowDown'].includes(context.navigationKey) && e.previousCellPosition) {
      let previousCell = e.previousCellPosition,
        lastRowIndex = previousCell.rowIndex,
        nextRowIndex = e.backwards ? lastRowIndex - 1 : lastRowIndex + 1,
        renderedRowCount = e.api.getModel().getRowCount();

      if (nextRowIndex < 0) nextRowIndex = -1;
      if (nextRowIndex >= renderedRowCount) nextRowIndex = renderedRowCount - 1;

      result = {
        rowIndex: nextRowIndex,
        column: previousCell.column,
        floating: previousCell.floating
      };
    }

    if (['ArrowLeft', 'ArrowRight'].includes(context.navigationKey) && e.nextCellPosition) {
      result = {
        rowIndex: e.nextCellPosition.rowIndex,
        column: e.nextCellPosition.column,
        floating: e.nextCellPosition.floating
      };
    }

    return result;
  }

  getContextMenuItems(e, eh) {
    const [cells] = e.api.getCellRanges();
    if (!cells) return;

    const colId = cells.startColumn.colId;
    const row = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex);
    const label = row.data[colId];

    const items = [];

    if (label) {
      items.push({
        name: `Fill with '${label}'`,
        action: () => eh.fillRegion(e, label)
      });
    }

    const cloneRow = (clearData) => {
      const [cells] = e.api.getCellRanges();
      const columnDefs = e.api.columnModel.columnDefs;
      const row = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex);
      const data = e.context.rowData.find((d) => d.id == row.data.id);
      const newId = +(new Date().valueOf() + '').slice(-10);
      const posMap = e.context.rowData.map((r) => r.pos).sort((a, b) => a - b);
      const currentPosIdx = posMap.findIndex((p) => p == data.pos);
      let newData = {};
      Object.keys(data).forEach((key) => {
        newData[key] =
          clearData && columnDefs.some((d) => d.field === key && d.editable !== false)
            ? Array.isArray(data[key])
              ? []
              : typeof data[key] === 'string'
              ? ''
              : 0
            : data[key];
      });
      newData.measurements = clearData ? {} : {...data.measurements};
      newData.pos = posMap[currentPosIdx + 1]
        ? posMap[currentPosIdx] + (posMap[currentPosIdx + 1] - posMap[currentPosIdx]) / 2
        : posMap[currentPosIdx] + 1000;
      if (newData.pos % 1 !== 0) return;
      newData.id = newId;
      delete newData.errors;

      eh.pushUndo(e.api, [{id: newId}]);
      e.context.rowData.push(newData);
      e.api.setRowData(e.context.rowData);
      const positions = e.context.rowData.map((r) => r.pos).sort((a, b) => a - b);
      e.context.rowPos = positions.map((p) => e.context.rowData.find((r) => r.pos === p).pos);

      const filterModel = e.api.getFilterModel();
      const isFiltered = Object.getOwnPropertyNames(filterModel).length > 0;
      if (isFiltered) {
        const values = e.api.getRenderedNodes().reduce((acc, field) => acc.concat(field.id.toString()), [newId.toString()]);
        e.api.setFilterModel({
          id: {
            type: 'set',
            values: values
          }
        });
      }
      e.api.refreshCells();
    };

    const multiRowsSelected = e.api.getSelectedRows().length > 1 || cells.startRow.rowIndex !== cells.endRow.rowIndex;
    if (!multiRowsSelected) {
      if (items.length > 0) items.push('separator');
      items.push({
        name: 'Delete Row',
        action: () => eh.deleteRow(e)
      });
      items.push({
        name: 'Clone Row',
        action: () => cloneRow(false)
      });
      items.push({
        name: 'Insert Row',
        action: () => cloneRow(true)
      });
      if (e.column.colId === 'species') {
        if (items.length > 0) items.push('separator');
        items.push({
          name: 'View on reeflifesurvey.com',
          action: () => {
            const species = e.api.getDisplayedRowAtIndex(cells.startRow.rowIndex).data.species;
            const speciesUrl = species.replace(/\s+/g, '-');
            const url = `https://reeflifesurvey.com/species/${encodeURIComponent(speciesUrl)}`;
            window.open(url, '_blank');
          }
        });
      }
    } else {
      if (items.length > 0) items.push('separator');
      items.push({
        name: 'Delete Selected Rows',
        action: () => this.deleteRow(e)
      });
    }
    return items;
  }

  deleteRow(e) {
    const rowData = e.context.rowData;
    const [cells] = e.api.getCellRanges();
    const startIdx = Math.min(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const endIdx = Math.max(cells.startRow.rowIndex, cells.endRow.rowIndex);
    const delta = [];

    if (startIdx === endIdx && startIdx === e.node.rowIndex) {
      e.api.getSelectedRows().forEach(() => {
        const data = e.node.data;
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      });
    } else if (startIdx === endIdx) {
      e.api.getSelectedRows().forEach((row) => {
        const data = rowData.find((d) => d.id === row.id);
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      });
    } else {
      for (let i = startIdx; i < endIdx + 1; i++) {
        const row = e.api.getDisplayedRowAtIndex(i);
        const data = rowData.find((d) => d.id === row.data.id);
        delta.push({...data});
        rowData.splice(rowData.indexOf(data), 1);
      }
    }
    this.pushUndo(e.api, delta);
    e.api.setRowData(rowData);
    e.context.rowPos = rowData.map((r) => r.pos).sort((a, b) => a - b);
    e.api.refreshCells();
  }

  overrideKeyboardEvents(e) {
    if (e.event.key === 'Delete') {
      if (e.event.type === 'keydown') this.onClearRegion(e);
      return true;
    }
    if (e.event.ctrlKey || e.event.metaKey) {
      if (e.event.key === 'c' && e.event.type === 'keydown') {
        this.onCopyRegion(e);
        return true;
      }
      if (e.event.key === 'x' && e.event.type === 'keydown') {
        this.onCutRegion(e);
        return true;
      }
      return false;
    }
  }

  onClearRegion(e) {
    this.fillRegion(e, '');
  }

  onCutRegion(e) {
    this.onCopyRegion(e);
    this.onClearRegion(e);
  }

  handlePasteEnd(e) {
    const context = e.api.gridOptionsWrapper.gridOptions.context;
    context.pasteMode = false;
    let oldRows = [];
    Array.from(new Set(context.pendingPasteUndo.map((u) => u.id))).forEach((id) => {
      let oldRow = {};
      let rowData = context.rowData;
      const newRow = rowData.find((r) => r.id === id);
      Object.keys(newRow).forEach(function (key) {
        oldRow[key] = newRow[key];
      });
      context.pendingPasteUndo
        .filter((u) => u.id === id)
        .forEach((p) => {
          const field = p.field;
          oldRow[field] = p.value;
        });
      oldRows.push(oldRow);
    });
    context.pendingPasteUndo = [];
    return context.pushUndo(e.api, [...oldRows]);
  }

  handleCellValueChanged(e) {
    if (e.context.pasteMode) e.context.pendingPasteUndo.push({id: e.data.id, field: e.colDef.field, value: e.oldValue});
    return e.context.undoStack.length;
  }

  handleUndo(e) {
    const context = e.api.gridOptionsWrapper.gridOptions.context;
    if (context.undoStack.length < 1) return;
    this.popUndo(e.api);
    e.api.refreshCells();
  }

  handleCellEditingStopped(e) {
    if (e.oldValue === e.newValue) return;
    const row = {...e.data};
    row[e.column.colId] = e.oldValue;
    return this.pushUndo(e.api, [row]);
  }
}

export default new DataSheetEventHandlers();
