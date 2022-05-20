import ObservableItemView from '../ObservableItemView';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import {render, waitFor} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {describe, beforeAll, afterAll, afterEach, test, expect} from '@jest/globals';
import '@testing-library/jest-dom';
import {Routes, Route, Router} from 'react-router-dom';

const observableItemTestData = {
  observableItemId: 1,
  observableItemName: 'observableItemNameValue',
  speciesEpithet: 'speciesEpithetValue',
  obsItemTypeName: 'obsItemTypeNameValue',
  obsItemTypeId: 1,
  commonName: '',
  aphiaId: '145881',
  aphiaRelTypeName: null,
  supersededBy: null,
  supersededNames: null,
  supersededIds: null,
  phylum: 'Chlorophyta',
  order: 'Cladophorales',
  family: 'Valoniaceae',
  genus: 'Valonia',
  letterCode: '',
  reportGroup: '',
  habitatGroups: '',
  isInvertSized: 11,
  lengthWeightA: 22,
  lengthWeightB: 33,
  lengthWeightCf: 44,
  obsItemAttribute: {Attribute1: 'Value1'},
  class: 'Ulvophyceae'
};

const server = setupServer(rest.get('/api/v1/reference/observableItem/1', (_, res, ctx) => res(ctx.json(observableItemTestData))));

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('<ObservableItemView/>', () => {
  test('Renders ', async () => {
    const history = createMemoryHistory({initialEntries: ['/reference/observableItem/1']});
    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={<ObservableItemView />} />
        </Routes>
      </Router>
    );
    await waitFor(() => {
      expect(getByText(`observableItemNameValue`));
      expect(getByText(`speciesEpithetValue`));
      expect(getByText(`obsItemTypeNameValue`));
      expect(getByText(`Value1`));
    });
  });
});
