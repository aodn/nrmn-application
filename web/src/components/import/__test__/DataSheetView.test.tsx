import {render, waitFor, screen} from '@testing-library/react';
import '@testing-library/jest-dom';
import {describe, beforeAll, afterEach, test} from '@jest/globals';
import {BrowserRouter} from 'react-router-dom';
import * as axiosInstance from '../../../api/api';
import DataSheetView from '../DataSheetView';
import {extendedMeasurements, measurements} from '../../../common/constants';
import eh from '../DataSheetEventHandlers';
import {AppConstants} from '../../../common/constants';
import React from 'react';

describe('<DataSheetView/>', () => {
  let mockGetDataJob: any;
  const ingest = () => {};

  const columns = [
    'ID',
    'Diver',
    'Buddy',
    'Site No.',
    'Site Name',
    'Latitude',
    'Longitude',
    'Date',
    'Vis',
    'Direction',
    'Time',
    'P-Qs',
    'Depth',
    'Method',
    'Block',
    'Code',
    'Species',
    'Common Name',
    'Total',
    'Inverts',
    ...measurements.map((m) => m.fishSize)
  ];

  const extendedColumns = [...extendedMeasurements.map((m) => m.fishSize), 'Use InvertSizing'];

  beforeAll(() => {
    mockGetDataJob = jest.spyOn(axiosInstance, 'getDataJob');
    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
  });

  afterEach(() => {
    mockGetDataJob.mockClear();
  });

  // The export function follows the column of the grid, if the grid is right, the export fields are correct
  test('Non extend data column correct, hence export column match', async () => {
    const canned = require('./job16.json');

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: canned,
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        status: 200,
        statusText: url
      };

      return (
        new Promise((resolve) => {
          resolve(raw);
        })
      );
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    const {rerender} = render(
      <BrowserRouter>
        <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.ADMIN]} />
      </BrowserRouter>
    );

    screen.findByText('user_noextend.xlsx',{}, {timeout: 2000})
      .then(() => {
        // verify default columns exist
        columns.forEach((x) => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Data loaded after initial render, need refresh to trigger HTML update
        rerender(
          <BrowserRouter>
            <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.ADMIN]} />
          </BrowserRouter>
        );

        // non extend job and hence you will not have the following column
        extendedColumns.forEach((x) => {
          expect(screen.queryAllByText(x).length).toEqual(0);
        });

        expect(screen.getByText('Shell substrate')).toBeInTheDocument();
        expect(screen.getByText('Blacklip abalone')).toBeInTheDocument();
        expect(screen.getByText('Strapweed')).toBeInTheDocument();
      });
  });
  // The export function follows the column of the grid, if the grid is right, the export fields are correct
  test('Extend data column correct, hence export column match', async () => {
    const nonextend = require('./job17.json');

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: nonextend,
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        status: 200,
        statusText: url
      };

      return (
        new Promise((resolve) => {
          resolve(raw);
        })
      );
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    const {rerender} = render(
      <BrowserRouter>
        <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.ADMIN]} />
      </BrowserRouter>
    );

    screen.findByText('id.xlsx', {}, {timeout: 2000})
      .then(() => {
        // verify default columns exist
        columns.forEach((x) => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });
      })
      .finally(() => {
        // Data loaded after initial render, need refresh to trigger HTML update
        rerender(
          <BrowserRouter>
            <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.ADMIN]} />
          </BrowserRouter>
        );

        // Extend job and hence you will have the following column
        extendedColumns.forEach((x) => {
          expect(screen.queryAllByText(x).length).toBeGreaterThanOrEqual(1);
        });

        expect(screen.getByText('Interesting Bay')).toBeInTheDocument();
        expect(screen.getByText('Boring Bay')).toBeInTheDocument();
        expect(screen.getByText('Happy Bay')).toBeInTheDocument();
        expect(screen.getByText('Sad Bay')).toBeInTheDocument();
      });
  });

  test('Date Comparator', async () => {
    expect(eh.dateComparator('01/01/2006', '01/01/2006')).toEqual(0);
    expect(eh.dateComparator('01/01/2006', '01/01/06')).toEqual(0);
    expect(eh.dateComparator('01/01/06', '01/01/2006')).toEqual(0);
    expect(eh.dateComparator('01/01/00', '01/01/06')).not.toBeGreaterThanOrEqual(0);
    expect(eh.dateComparator('01/01/06', '01/01/00')).toBeGreaterThanOrEqual(0);
    expect(eh.dateComparator('01/01/2000', '01/01/06')).not.toBeGreaterThanOrEqual(0);
    expect(eh.dateComparator('01/01/06', '01/01/2000')).toBeGreaterThanOrEqual(0);
  });

  test('Save & submit button disabled with survey editor permission', async () => {
    const nonextend = require('./job17.json');

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: nonextend,
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        status: 200,
        statusText: url
      };

      return (
        new Promise((resolve) => {
          resolve(raw);
        })
      );
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    const {rerender} = render(
      <BrowserRouter>
        <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.SURVEY_EDITOR]} />
      </BrowserRouter>
    );

    waitFor(() => screen.findByText('id.xlsx'))
      .then(() => {
          expect(screen.getByTestId('save-and-validate-button')).not.toBeEnabled();
      });
  });

  test('Save & submit button enabled with data officer permission', async () => {
    const nonextend = require('./job17.json');

    // Override function so that it return the data we set.
    mockGetDataJob.mockImplementation((url: string) => {
      const raw = {
        config: undefined,
        data: nonextend,
        headers: {'Content-Type': 'application/json', Accept: 'application/json'},
        status: 200,
        statusText: url
      };

      return (
        new Promise((resolve) => {
          resolve(raw);
        })
      );
    });

    // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
    render(
      <BrowserRouter>
        <DataSheetView onIngest={ingest} roles={[AppConstants.ROLES.DATA_OFFICER]} />
      </BrowserRouter>
    );

    waitFor(() => screen.findByText('id.xlsx'))
      .then(() => {
          expect(screen.getByTestId('save-and-validate-button')).toBeEnabled();
      });
  });
});
