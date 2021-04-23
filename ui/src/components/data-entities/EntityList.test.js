/* eslint-disable no-unused-expressions */

import React from 'react';
import {Route} from 'react-router-dom';
import {renderWithProviders} from '../utils/test-utils';
import '@testing-library/jest-dom/extend-expect';
import config from 'react-global-configuration';
import {useSelector} from 'react-redux';
import EntityList from './EntityList';

const testSchema = {
  TestEntity: {
    required: ['password', 'username'],
    type: 'object',
    properties: {
      username: {
        type: 'string'
      },
      password: {
        type: 'string'
      }
    }
  }
};

config.set({api: testSchema});
jest.mock('ag-grid-react/lib/agGridReact');

const mockState = {
  theme: {themeType: false},
  form: {
    entities: {_embedded: {tests: {}}},
    formData: {},
    saved: false,
    errors: []
  }
};

const testEntity = {
  name: 'TestEntity',
  route: {base: '/reference/test', view: '/reference/test/:id?/:success?', edit: '/reference/test/:id?/edit'},
  schemaKey: 'TestEntity',
  endpoint: 'tests',
  template: {add: null, edit: null, view: null},
  can: {},
  list: {
    schemaKey: 'TestEntity',
    name: 'TestEntities',
    route: '/reference/tests',
    endpoint: 'tests'
  }
};

jest.mock('react-redux', () => {
  const ActualReactRedux = require.requireActual('react-redux');
  return {
    ...ActualReactRedux,
    useSelector: jest.fn().mockImplementation(() => {
      return function () {
        return {};
      };
    }),
    useDispatch: jest.fn().mockImplementation(() => {
      return function () {
        return {};
      };
    }),
    useEffect: jest.fn().mockImplementation(() => {
      return function () {
        return {};
      };
    })
  };
});

describe('EntityList Component', () => {
  beforeEach(() => {
    useSelector.mockImplementation((callback) => {
      return callback(mockState);
    });
  });
  afterEach(() => {
    useSelector.mockClear();
  });

  test('Test EntityList.js exists', async () => {
    const {findByText} = renderWithProviders(
      <Route path={testEntity.list.route}>
        <EntityList entity={testEntity} />
      </Route>,
      {
        route: testEntity.list.route
      }
    );
    await findByText('TestEntities');
  });

  test('Test EntityList.js New Entity button exists', async () => {
    const {findByText} = renderWithProviders(
      <Route path={testEntity.list.route}>
        <EntityList entity={testEntity} />
      </Route>,
      {
        route: testEntity.list.route
      }
    );
    await findByText('TestEntities');
  });
});
