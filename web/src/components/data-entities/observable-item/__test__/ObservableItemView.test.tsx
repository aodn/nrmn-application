// @ts-ignore
import React from 'react';
import ObservableItemView from '../ObservableItemView';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import {render, waitFor, screen} from '@testing-library/react';
import {createMemoryHistory} from 'history';
import {describe, beforeAll, afterAll, afterEach, test} from '@jest/globals';
import '@testing-library/jest-dom';
import {Routes, Route, Router} from 'react-router-dom';
import {AuthContext} from '../../../../contexts/auth-context';
import { AppConstants } from '../../../../common/constants';

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

const observableItemNullData: any = {...observableItemTestData};
for (const key in observableItemNullData) observableItemNullData[key] = null;

const getTestData = (req: any, res: any, ctx: any) => {
  return res(ctx.json(req.params[0] == '1' ? observableItemTestData : observableItemNullData));
};

const server = setupServer(rest.get('/api/v1/reference/observableItem/*', getTestData));

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('<ObservableItemView/>', () => {
  test('Renders fields', async () => {
    const history = createMemoryHistory({initialEntries: [{pathname: '/reference/observableItem/1', state: {resetFilters: true}}]});
    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <ObservableItemView />
            </AuthContext.Provider>
          } />
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

      expect(getByText((observableItemTestData as any)[key]));

      // Observable Item Attributes
      expect(getByText(observableItemTestData.obsItemAttribute.Attribute1));
      expect(getByText(observableItemTestData.obsItemAttribute.Attribute2));
    });
  });

  test('Renders when null', async () => {
    // Avoid loading filter from storage
    const history = createMemoryHistory({initialEntries: [{pathname: '/reference/observableItem/2', state: {resetFilters: true}}]});

    const {getByText} = render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.ADMIN]}}}>
              <ObservableItemView />
            </AuthContext.Provider>
          }/>
        </Routes>
      </Router>
    );
    await waitFor(() => expect(getByText('Observable Items')));
  });

  test('Edit button disabled with survey editor permission', async () => {
    // Avoid loading filter from storage
    const history = createMemoryHistory({initialEntries: [{pathname: '/reference/observableItem/1', state: {resetFilters: true}}]});

    render(
      <Router location={history.location} navigator={history}>
        <Routes>
          <Route path="/reference/observableItem/:id" element={
            <AuthContext.Provider value={{auth : {roles: [AppConstants.ROLES.SURVEY_EDITOR]}}}>
              <ObservableItemView />
            </AuthContext.Provider>
          }/>
        </Routes>
      </Router>
    );
    // Wait until screen printed with the value from the canned data, then we can do other check
    await waitFor(() => expect(screen.getByText('supersededByVakyes')),{ timeout: 10000})
      .then(() => {
        expect(screen.getByTestId('edit-button')).toHaveAttribute('aria-disabled');
      });
  });

});
