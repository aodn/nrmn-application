import {waitFor} from '@testing-library/react';

// since our grid starts with no data, when the overlay has gone, data has loaded
export const waitForDataToHaveLoaded = () => {
  return waitFor(()=>{
    expect(document.querySelectorAll(".ag-cell-value").length).toBeGreaterThan(0);
  });
}
