const DataSheetEventHandlers = {

  pushUndo(api, delta) {
    const ctx = api.gridOptionsWrapper.gridOptions.context;
    ctx.undoStack.push(
      delta.map((d) => {
        ctx.putRowIds.push(d.id);
        return { ...d };
      })
    );
    return ctx.undoStack.length;
  },
  resetContext() {
    this.context.useOverlay = 'Loading';
    this.context.rowData = [];
    this.context.rowPos = [];
    this.context.highlighted = [];
    this.context.putRowIds = [];
    this.context.summary = [];
    this.context.errorList = {};
    this.context.errors = [];
    this.context.pendingPasteUndo = [];
    this.context.focusedRows = [];
    this.context.pasteMode = false;
  },
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
};

export default DataSheetEventHandlers;
