import userEvent from '@testing-library/user-event';
import {render, waitFor, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import {describe, beforeAll, afterEach, jest, test} from '@jest/globals';
import {Router} from 'react-router-dom';
import * as axiosInstance from '../../../api/api';
import {AxiosResponse} from 'axios';
import JobList from '../JobList';
import {createMemoryHistory} from 'history';
import stateFilterHandler from '../../../common/state-event-handler/StateFilterHandler';
import {AuthContext} from '../../../contexts/auth-context';
import { AppConstants } from '../../../common/constants';

jest.setTimeout(30000);

describe('<JobList/> filter testing', () => {
  let mockGetResult: any;
  let mockGetFiltersForId: any;
  let mockResetStateFilters: any;
  const columns = ['reference', 'isExtendedSize', 'program', 'creator', 'created'];

  beforeAll(() => {
    mockGetResult = jest.spyOn(axiosInstance, 'getEntity');
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
    const canned = require('./JobList.filter.data.json');

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: canned,
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

    const history = createMemoryHistory({initialEntries: [{state: {resetFilters: true}}]});
    const {container, rerender} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <JobList />
        </AuthContext.Provider>
      </Router>
    );

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach((x) => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(async () => {
        // Refresh the dom tree
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <JobList />
            </AuthContext.Provider>
          </Router>
        );

        // Expand the group otherwise you will not see the other file name, you need to click it after
        // rerender because FAILED and INGESTED will not be there until rerender happens
        await userEvent.dblClick(screen.getByText('FAILED'));
        await userEvent.dblClick(screen.getByText('INGESTED'));

        // Refresh the dom tree after click
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <JobList />
            </AuthContext.Provider>
          </Router>
        );

        expect(screen.getByText('staged1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('failed1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('failed2.xlsx')).toBeInTheDocument();

        expect(screen.getByText('ingested1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested2.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested3.xlsx')).toBeInTheDocument();

        // Restore filter not called if you pass the resetFilter false to the component
        expect(mockGetFiltersForId).toBeCalledTimes(0);
        expect(mockResetStateFilters).toBeCalledTimes(1);
      });
  });

  test('Render necessary fields with filter restored', async () => {
    const canned = require('./JobList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id: string) => {
      return '{"reference":{"filterType":"text","type":"contains","filter":"ge"}}';
    });

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: canned,
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

    const history = createMemoryHistory({initialEntries: [{state: {resetFilters: false}}]});
    const {container, rerender} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <JobList />
        </AuthContext.Provider>
      </Router>
    );

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), {timeout: 10000})
      .then(() => {
        // verify default columns exist
        columns.forEach((x) => {
          expect(container.querySelector('[col-id="' + x + '"]')).toBeInTheDocument();
        });
      })
      .finally(async () => {
        // Refresh the dom tree
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <JobList />
            </AuthContext.Provider>
          </Router>
        );

        // Filter cause FAILED group disappeared, INGESTED can be found and hence clickable
        screen.findByText('FAILED').then((i) => expect(i).toBe({}));
        await userEvent.dblClick(screen.getByText('INGESTED'));

        // Refresh the dom tree after click
        rerender(
          <Router location={history.location} navigator={history}>
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <JobList />
            </AuthContext.Provider>
          </Router>
        );

        // Restore filter called
        expect(mockGetFiltersForId).toBeCalledTimes(1);
        expect(mockResetStateFilters).toBeCalledTimes(0);

        // Given filter, these items filtered out even you expended the list
        screen.findByText('failed1.xlsx').then((i) => expect(i).toBe({}));
        screen.findByText('failed2.xlsx').then((i) => expect(i).toBe({}));

        expect(screen.getByText('ingested1.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested2.xlsx')).toBeInTheDocument();
        expect(screen.getByText('ingested3.xlsx')).toBeInTheDocument();
        expect(screen.getByText('staged1.xlsx')).toBeInTheDocument();
      });
  });

  test('Upload button disabled for survey editor permission', async () => {
    const canned = require('./JobList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id: string) => {
      return '{"reference":{"filterType":"text","type":"contains","filter":"ge"}}';
    });

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        status: 200,
        statusText: url
      };

      return (
        new Promise<AxiosResponse>((resolve) => {
          resolve(raw as any);
        })
      );
    });

    const history = createMemoryHistory({ initialEntries: [{ state: { resetFilters: false } }] });
    render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{ auth: { roles: [AppConstants.ROLES.SURVEY_EDITOR] } }}>
          <JobList />
        </AuthContext.Provider>
      </Router>
    );

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), { timeout: 10000 })
      .then(() => {
        // MUI use aria-disabled="true" to disable the button
        expect(screen.getByTestId('xls-upload-button')).toHaveAttribute('aria-disabled');
      });
  });

  test('Upload button disabled for data officer permission', async () => {
    const canned = require('./JobList.filter.data.json');

    // Filter set will cause some items disappeared
    mockGetFiltersForId.mockImplementation((id: string) => {
      return '{"reference":{"filterType":"text","type":"contains","filter":"ge"}}';
    });

    // Override function so that it return the data we set.
    mockGetResult.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: canned,
        headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
        status: 200,
        statusText: url
      };

      return (
        new Promise<AxiosResponse>((resolve) => {
          resolve(raw as any);
        })
      );
    });

    const history = createMemoryHistory({ initialEntries: [{ state: { resetFilters: false } }] });
    render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{ auth: { roles: [AppConstants.ROLES.DATA_OFFICER] } }}>
          <JobList />
        </AuthContext.Provider>
      </Router>
    );

    // Data loaded due to mock object being called once
    await waitFor(() => expect(mockGetResult).toHaveBeenCalledTimes(1), { timeout: 10000 })
      .then(() => {
        // MUI use aria-disabled="true" to disable the button
        expect(screen.getByTestId('xls-upload-button')).not.toHaveAttribute('aria-disabled');
      });
  });
});
