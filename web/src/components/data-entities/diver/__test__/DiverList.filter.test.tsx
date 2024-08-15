
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import { AxiosResponse } from 'axios';
import DiverList from '../DiverList';
import { createMemoryHistory } from 'history';
import stateFilterHandler from '../../../../common/state-event-handler/StateFilterHandler';
import {AuthContext} from '../../../../contexts/auth-context';
import { AppConstants } from '../../../../common/constants';

jest.setTimeout(30000);

describe('<DiverList/> filter testing', () => {
  let mockGetResult: any;
  let mockGetFiltersForId: any;
  let mockResetStateFilters: any;
  const columns = ['diver.initials', 'diver.fullName'];

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance, 'getResult');
    mockGetFiltersForId = jest.spyOn(stateFilterHandler, 'getFiltersForId');
    mockResetStateFilters = jest.spyOn(stateFilterHandler, 'resetStateFilters');

    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetResult.mockReset();
    mockGetFiltersForId.mockReset();
  });

  test('Render necessary fields and no filter restored', async () => {
    const canned = require('./DiverList.filter.data.json');

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {container, rerender} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <DiverList/>
        </AuthContext.Provider>
      </Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalled(), {timeout: 10000})
      .then(() => {
        // verify default columns exist

        columns.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <DiverList/>
            </AuthContext.Provider>
          </Router>);

        expect(screen.getByText('Apple Orange')).toBeInTheDocument();
        expect(screen.getByText('Cherry Melon')).toBeInTheDocument();
        expect(screen.getByText('Rock Melon')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
        expect(mockResetStateFilters).toBeCalledTimes(1);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./DiverList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id: string) => {
      return '{"diver.fullName":{"filterType":"text","type":"contains","filter":"Orange"}}';
    });

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {

      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' },
        status: 200,
        statusText: url
      };

      return new Promise<AxiosResponse>((resolve => {
        resolve(raw as any);
      }));
    });

    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: false}}]});
    const {container, rerender} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <DiverList/>
        </AuthContext.Provider>
      </Router>);

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalled(), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach(x => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(() => {
        // Refresh the dom tree
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <DiverList/>
            </AuthContext.Provider>
          </Router>
        );

        // All filter operation in server side, we just need to check if filter get restored
        expect(mockGetFiltersForId).toBeCalledTimes(1);
        expect(mockResetStateFilters).toBeCalledTimes(0);
      });
  });
});
