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
  commonName: 'commonNameValue',
  aphiaId: '145881',
  aphiaRelTypeName: 'aphiaRelTypeNameValue',
  supersededBy: 'supersededByVakyes',
  supersededNames: 'supersededNamesValues',
  supersededIds: 'supersededIdsValues',
  phylum: 'Chlorophyta',
  order: 'Cladophorales',
  family: 'Valoniaceae',
  genus: 'Valonia',
  letterCode: 'letterCodeValue',
  reportGroup: 'reportGroupValue',
  habitatGroups: 'habitatGroupsValue',
  isInvertSized: true,
  lengthWeightA: 22,
  lengthWeightB: 33,
  lengthWeightCf: 44,
  obsItemAttribute: {Attribute1: 'Value1', Attribute2: 'Value2'},
  class: 'Ulvophyceae'
};

const observableItemNullData = {...observableItemTestData};
for(const key in observableItemNullData) observableItemNullData[key] = null;

const getTestData = (req, res, ctx) => {
  return res(ctx.json(req.params[0] == '1' ? observableItemTestData : observableItemNullData));
};

const server = setupServer(rest.get('/api/v1/reference/observableItem/*', getTestData));

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('<ObservableItemView/>', () => {
  test('Renders fields', async () => {
    const history = createMemoryHistory({initialEntries: ['/reference/observableItem/1']});
    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={<ObservableItemView />} />
        </Routes>
      </Router>
    );
    await waitFor(() => {
      // Text Fields
      for (const key of [
        'observableItemName',
        'speciesEpithet',
        'obsItemTypeName',
        'commonName',
        'aphiaId',
        'aphiaRelTypeName',
        'supersededBy',
        'supersededNames',
        'supersededIds',
        'phylum',
        'order',
        'family',
        'genus',
        'letterCode',
        'reportGroup',
        'habitatGroups',
        'lengthWeightA',
        'lengthWeightB',
        'lengthWeightCf',
        'class'
      ])
        expect(getByText(observableItemTestData[key]));
      // Observable Item Attributes
      expect(getByText(observableItemTestData.obsItemAttribute.Attribute1));
      expect(getByText(observableItemTestData.obsItemAttribute.Attribute2));
    });
  });

  test('Renders when ull', async () => {
    const history = createMemoryHistory({initialEntries: ['/reference/observableItem/2']});
    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={<ObservableItemView />} />
        </Routes>
      </Router>
    );
    await waitFor(() => expect(getByText('Observable Items')));
  });
});
