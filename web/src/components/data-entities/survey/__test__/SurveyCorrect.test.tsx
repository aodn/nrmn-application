import React from 'react';
import { render, waitFor, screen, fireEvent, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach, test, expect } from '@jest/globals';
import { BrowserRouter } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import SurveyCorrect from '../SurveyCorrect';
import { INSERT_ONE_ROW } from '../../../import/DataSheetEventHandlers';
import { PointerInput } from '@testing-library/user-event/dist/types/pointer';

jest.setTimeout(50000);

const toNonNullString = (s: string | undefined): string => s ? s : "";
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms));
const range = (start: any, stop: any, step: any) => 
  Array.from({ length: (stop - start) / step + 1}, (_, i) => "" + (start + (i * step)))

describe('<SurveyCorrect/> testing', () => {
  let mockGetDataJob: any;

  beforeAll(() => {
    mockGetDataJob = jest.spyOn(axiosInstance, 'getCorrections');
    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetDataJob.mockClear();
  });

  test('Insert 1 row works', async () => {
    // When data load the row-id will be 100, 200,.... 4000
    const initRowIds: string[] = range(100, 3900, 100);
    const temp = require('./cannedData/SurveyCorrect.contextMenu.json');

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: temp,
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        status: 200,
        statusText: url
      };

      return (
        new Promise<AxiosResponse>((resolve) => {
          resolve(raw as any);
        })
      );
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    // also need to suppress virtualisation, otherwise aggrid will not draw everything if screen is not big enough
    const { rerender, container } = render(
      <BrowserRouter>
        <SurveyCorrect suppressColumnVirtualisation={true}/>
      </BrowserRouter>
    );

    // Test dataset have 4 row only
    await waitFor(() => expect(mockGetDataJob).toHaveBeenCalledTimes(1));
    rerender(
      <BrowserRouter>
        <SurveyCorrect suppressColumnVirtualisation={true}/>
      </BrowserRouter>
    );

    // row-id = 200 is second row
    const rows = await screen.findAllByRole("row");
    expect(rows.length).toEqual(initRowIds.length);

    const rowTwo = rows.find(i => i.getAttribute("row-id") === '200');
    const rowTwoDriverColumn = container.querySelector("div[row-id='200'] div[col-id='diver']");
    const rowThreeDriverColumn = container.querySelector("div[row-id='300'] div[col-id='diver']");

    expect(rowTwoDriverColumn?.innerHTML).toEqual("ESO");
    expect(rowThreeDriverColumn?.innerHTML).toEqual("ESO");

    const checkedId: string[] = [];
    // We repeat the insert a couple of times, previously a bug appear where we cannot add more then 3 times
    for(let i = 0; i < 5; i++) {
      // Highlight the row
      userEvent.pointer({ keys: '[MouseLeft]', target: rowTwoDriverColumn } as PointerInput);
      // Show context menu
      userEvent.pointer({ keys: '[MouseRight]', target: rowTwo });
    
      waitFor(() => expect(container.querySelector("div[role='tree']")).toBeDefined());

      // This is the context menu
      screen.findAllByRole("treeitem")
        .then((treeitem) => {
          // Find the menu item that insert 1 row, change the children from HTMLCollection to array of HTMLElement
          const targetMenuItem = treeitem.find(
            (collection) => {
              for(let i = 0; i < collection.children.length; i++) {
                if(collection.children[i].innerHTML === INSERT_ONE_ROW) {
                  return true;
                }
              }
              return false;
            });
          userEvent.pointer({ keys: '[MouseLeft]', target: targetMenuItem });
        });

      let newRows: HTMLElement[] = [];
      
      // Give time to redraw
      await wait(5000);

      await waitFor(() => {
        // Find all row role and expect new role added one by one, we do not need those known id and previously seen
        screen.findAllByRole("row")
          .then((updatedRows)=> {
            newRows = updatedRows
              .filter(i => i.getAttribute("row-id") !== null)
              .filter(i => !initRowIds.includes("" + i.getAttribute("row-id")))
              .filter(i => !checkedId.includes("" + i.getAttribute("row-id")));
            expect(newRows.length).toBeGreaterThan(0);
          });
      }, {timeout: 5000});
        
      // Check the last row, it is the row we added. Now the row added will use row-id being generated by a 
      // date time, so that value will be big and must bigger than 4000, so len > 4
      const newRowId = newRows[newRows.length - 1].getAttribute("row-id");
      expect(newRowId?.length).toBeGreaterThan(4);

      const newRow = container.querySelector(`div[row-id='${newRowId}'] div[col-id='diver']`);

      // A new insert row will have empty value in field
      expect(newRow?.innerHTML).toEqual("");

      checkedId.push("" + newRowId);
      console.log("Verified new row with id ", newRowId);
    }
  });
});