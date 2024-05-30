import {ErrorMessages} from '../../common/constants';
import {round} from 'lodash';
import {updateRows} from '../../api/api';

/**
 * This class is used to automatically rectify the data based on the validation result
 */
class DataRectificationHandler {

  constructor() {
    const {ROUND_LAT_MSG, ROUND_LON_MSG} = ErrorMessages;
    Object.assign(this, {ROUND_LAT_MSG, ROUND_LON_MSG});
  }

  setValidationResult(validationResult) {
    this.validationResult = validationResult;
  }

  setRows(rowDtos) {
    this.rowDtos = rowDtos;
  }

  // reminder: return a state
  async submitRectification(id) {
    this.#findRowsToRectify();
    await updateRows(id, this.rowsToRectify, () => {});
  }

  #findRowsToRectify() {
    this.rowsToRectify = [];

    // if there are other data need to rectify, please declare functions and invoke them here:
    for (let error of this.validationResult) {
      this.#rectifyLatLon(error);
    }
  }

  #rectifyLatLon(error) {

    if (error.message.includes(this.ROUND_LAT_MSG)) {
      error.rowIds.forEach(rowId => {

        const rowDto = this.rowDtos.find(row => row.rowId === rowId);
        rowDto.row.latitude = round(rowDto.row.latitude, 5);
        this.rowsToRectify.push (rowDto);
      });
    }
    if (error.message.includes(this.ROUND_LON_MSG)) {
      error.rowIds.forEach(rowId => {
        const rowDto = this.rowDtos.find(row => row.rowId === rowId);
        rowDto.row.longitude = round(rowDto.row.longitude, 5);
        this.rowsToRectify.push (rowDto);
      });
    }
  }
}

export default DataRectificationHandler;