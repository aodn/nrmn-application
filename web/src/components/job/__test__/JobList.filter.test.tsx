// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import userEvent from '@testing-library/user-event';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../api/api';
import { AxiosResponse } from 'axios';
import JobList from '../JobList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';

jest.setTimeout(10000);

describe('<JobList/> filter testing', () => {
  let mockGetResult;
  let mockGetFiltersForId;
  const columns = ['Reference', 'Extended', 'Program', 'Creator', 'Created'];

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance, 'getEntity');
    mockGetFiltersForId = jest.spyOn(stateFilterHandler, 'getFiltersForId');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetResult.mockReset();
    mockGetFiltersForId.mockReset();
  });

  test('Render necessary fields and no filter restored', async () => {
    const canned = require('./sample1.json');

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url) => {

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {rerender} = render(<Router location={history.location} navigator={history}><JobList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><JobList/></Router>);

        // Expand the group otherwise you will not see the other file name, you need to click it after
        // rerender because FAILED and INGESTED will not be there until rerender happens
        userEvent.dblClick(screen.getByText('FAILED').parentElement.parentElement);
        userEvent.dblClick(screen.getByText('INGESTED').parentElement.parentElement);

        // Refresh the dom tree after click
        rerender(<Router location={history.location} navigator={history}><JobList/></Router>);

        expect(screen.getByText('staged1.xlsx')).toBeInTheDocument();
        screen.findByText('staged2.xlsx').then(i => expect(i).toBe({}));

        expect(screen.getByText('failed1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('failed2.xlsx')).toBeInTheDocument();

        expect(screen.getByText('ingested1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested2.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested3.xlsx')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./sample1.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id) => {
      return '{"reference":{"filterType":"text","type":"contains","filter":"ge"}}';
    });

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url) => {

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: false}}]});
    const {rerender} = render(<Router location={history.location} navigator={history}><JobList/></Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(<Router location={history.location} navigator={history}><JobList/></Router>);

        // Filter cause FAILED group disappeared, INGESTED can be found and hence clickable
        screen.findByText('FAILED').then(i => expect(i).toBe({}));
        userEvent.dblClick(screen.getByText('INGESTED').parentElement.parentElement);

        // Refresh the dom tree after click
        rerender(<Router location={history.location} navigator={history}><JobList/></Router>);

        // Restore filter called
        expect(mockGetFiltersForId).toBeCalledTimes(1);

        // Given filter, these items filtered out even you expended the list
        screen.findByText('failed1.xlsx').then(i => expect(i).toBe({}));
        screen.findByText('failed2.xlsx').then(i => expect(i).toBe({}));

        expect(screen.getByText('ingested1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested2.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested3.xlsx')).toBeInTheDocument();

        expect(screen.getByText('staged1.xlsx')).toBeInTheDocument();
        screen.findByText('staged2.xlsx').then(i => expect(i).toBe({}));
      });
  });
});
