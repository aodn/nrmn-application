import {render} from '@testing-library/react';
import '@testing-library/jest-dom';
import {describe, beforeAll, afterEach, jest} from '@jest/globals';
import {setupServer} from 'msw/node';
import {createMemoryHistory} from 'history';
import {rest} from 'msw';
import SiteList from '../SiteList';
import {Router} from 'react-router-dom';
import {waitForDataToHaveLoaded} from '../../../../common/AgGridTestHelper';
import {AuthContext} from '../../../../contexts/auth-context';
import { AppConstants } from '../../../../common/constants';

jest.setTimeout(30000);

describe('<SiteList/>', () => {
  const siteTestData = {
      "lastRow": 3,
      "items": [
        {
          siteId: 1,
          siteCode: 'AAA',
          siteName: 'Site A',
          locationName: 'Location A',
          state: 'State A',
          country: 'Country A',
          latitude: 11.0,
          longitude: -11.0,
          isActive: true
        },
        {
          siteId: 2,
          siteCode: 'BBB',
          siteName: 'Site B',
          locationName: 'Location B',
          state: 'State B',
          country: 'Country B',
          latitude: 22.0,
          longitude: -22.0,
          isActive: false
        },
        {
          siteId: 3,
          siteCode: 'CCC',
          siteName: 'Site C',
          locationName: 'Location C',
          state: 'State C',
          country: 'Country C',
          latitude: 33.0,
          longitude: -33.0,
          isActive: false
      }
    ]
  };

  const server = setupServer(
    rest.get('/api/v1/sites', (_, res, ctx) => {
      return res(ctx.json(siteTestData));
    })
  );

  beforeAll(() => {
    // silence errors caused by not setting an AG Grid licence
    jest.spyOn(console, 'error').mockImplementation(() => {});
    server.listen();
  });

  afterEach(() => {
    server.resetHandlers();
  });

  afterAll(() => server.close());

  const visibleColumns = ['siteCode', 'siteName', 'locationName', 'state', 'country', 'latitude', 'longitude'];

  it('renders ' + visibleColumns.join(','), async () => {
    const history = createMemoryHistory();
    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <SiteList />
        </AuthContext.Provider>
      </Router>
    );
    await waitForDataToHaveLoaded();
    for (const field of visibleColumns) {
      expect(getByText(`${(siteTestData as any).items[0][field]}`)).toBeInTheDocument();
      expect(getByText(`${(siteTestData as any).items[1][field]}`)).toBeInTheDocument();
      expect(getByText(`${(siteTestData as any).items[2][field]}`)).toBeInTheDocument();
    }
  });

  it('shows the clone icon for every site', async () => {
    const history = createMemoryHistory({initialEntries: [{state: {resetFilters: true}}]});
    const {queryAllByTestId} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <SiteList />
        </AuthContext.Provider>
      </Router>
    );
    await waitForDataToHaveLoaded();
    const copyAllIcons: Element[] = queryAllByTestId('CopyAllIcon');
    expect(copyAllIcons.length === siteTestData.lastRow);
  });

  it('shows a delete icon for inactive sites', async () => {
    const history = createMemoryHistory({initialEntries: [{state: {resetFilters: true}}]});
    const {queryAllByTestId} = render(
      <Router location={history.location} navigator={history}>
        <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
          <SiteList />
        </AuthContext.Provider>
      </Router>
    );
    await waitForDataToHaveLoaded();
    const visibleDeleteIcons: Element[] = queryAllByTestId('DeleteIcon');
    const activeSites: Object[] = siteTestData.items.filter((s) => s.isActive == false);
    expect(visibleDeleteIcons.length === activeSites.length);
  });
});
