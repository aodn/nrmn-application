import SiteList from '../SiteList';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import {render, waitFor, screen} from '@testing-library/react';
import {createMemoryHistory} from 'history'
import {describe, beforeAll, afterAll, afterEach, test, expect, jest} from '@jest/globals';
import '@testing-library/jest-dom';
import { Router } from 'react-router-dom';

const siteTestData = [
  {'siteId':1,'siteCode':'AAA','siteName':'Site A','locationName':'Location A','state':'State A','country':'Country A','latitude':11.0,'longitude':-11.0,'isActive':true},
  {'siteId':2,'siteCode':'BBB','siteName':'Site B','locationName':'Location B','state':'State B','country':'Country B','latitude':22.0,'longitude':-22.0,'isActive':false},
  {'siteId':3,'siteCode':'CCC','siteName':'Site C','locationName':'Location C','state':'State C','country':'Country C','latitude':33.0,'longitude':-33.0,'isActive':false}
];

const server = setupServer(rest.get('/api/v1/sites', (_, res, ctx) => res(ctx.json(siteTestData))));

beforeAll(() => {
  // silence errors caused by not setting an AG Grid licence
  jest.spyOn(console, 'error').mockImplementation(() => {});
  server.listen();
});

afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('<SiteList/>', () => {

  const visibleColumns = ['siteCode', 'siteName', 'locationName', 'state', 'country', 'latitude', 'longitude'];

  test('Renders ' + visibleColumns.join(','), async () => {
    const history = createMemoryHistory();
    render(<Router location={history.location} navigator={history}><SiteList /></Router>);
      await waitFor(() => {
        for(const field of visibleColumns) {
          expect(screen.getByText(`${siteTestData[0][field]}`)).toBeInTheDocument();
          expect(screen.getByText(`${siteTestData[1][field]}`)).toBeInTheDocument();
          expect(screen.getByText(`${siteTestData[2][field]}`)).toBeInTheDocument();
        }
      });
  });

  test('Clone Icon appears for every site', async () => {
    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {queryAllByTestId} = render(<Router location={history.location} navigator={history}><SiteList /></Router>);
    await waitFor(() => {
      const copyAllIcons : Element[] = queryAllByTestId('CopyAllIcon');
      expect(copyAllIcons.length === siteTestData.length);
    });
  });

  test('Delete Icon only appears for inactive sites', async () => {
    const history = createMemoryHistory({initialEntries:[{state: {resetFilters: true}}]});
    const {queryAllByTestId} = render(<Router location={history.location} navigator={history}><SiteList /></Router>);
    await waitFor(() => {
      const visibleDeleteIcons : Element[] = queryAllByTestId('DeleteIcon');
      const activeSites : Object[] = siteTestData.filter(s => s.isActive == false);
      expect(visibleDeleteIcons.length === activeSites.length);
    });
  });
});
