// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import ObservableItemList from '../ObservableItemList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../../common/state-event-handler/StateFilterHandler';

jest.setTimeout(10000);

describe('<ObservableItemList/> filter testing', () => {
  let mockGetResult;
  let mockGetFiltersForId;
  const columns = [
    'ID', 'Type', 'Name', 'Common Name', 'Superseded By','Superseded Names','Superseded IDs','Phylum',
    'Class','Order','Family','Genus'];

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance, 'getResult');
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
    const {rerender} = render(<Router location={history.location} navigator={history}><ObservableItemList/></Router>);

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
        rerender(<Router location={history.location} navigator={history}><ObservableItemList/></Router>);

        expect(screen.getByText('Cardinalfish')).toBeInTheDocument();
        expect(screen.getByText('California')).toBeInTheDocument();
        expect(screen.getByText('Silve')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./sample1.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id) => {
      return '{"commonName":{"filterType":"text","type":"contains","filter":"Nap"}}';
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
    const {rerender} = render(<Router location={history.location} navigator={history}><ObservableItemList/></Router>);

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
        rerender(<Router location={history.location} navigator={history}><ObservableItemList/></Router>);

        // Restore filter called
        expect(mockGetFiltersForId).toBeCalledTimes(1);

        // id = 1 so show this one
        expect(screen.getByText('Nap')).toBeInTheDocument();

        // id != 1 so no show
        screen.findByText('California').then(i => expect(i).toBe({}));
        screen.findByText('Silve').then(i => expect(i).toBe({}));
      });
  });
});
