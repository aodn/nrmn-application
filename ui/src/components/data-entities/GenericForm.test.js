/* eslint-disable no-unused-expressions */

import React from "react";
import GenericForm from "./GenericForm";
import { Route } from "react-router-dom";
import {renderWithProviders} from "../utils/test-utils";
import "@testing-library/jest-dom/extend-expect";
import config from "react-global-configuration";
import {useSelector} from "react-redux";

const testSchema = {
  TestEntity: {
    required: [
      "password",
      "username"
    ],
    type: "object",
    properties: {
      username: {
        type: "string"
      },
      password: {
        type: "string"
      }
    }
  }
}

config.set({api: testSchema});


const mockState = {
  form: {
    entities: [],
    editItem: {},
    newlyCreatedEntity: {},
    errors: []
  }
};

jest.mock('react-redux', () => {
  const ActualReactRedux = require.requireActual('react-redux');
  return {
    ...ActualReactRedux,
    useSelector: jest.fn().mockImplementation(() => {
      return function(){ return {}};
    }),
    useDispatch: jest.fn().mockImplementation(() => {
      return function(){ return {}};
    }),
    useEffect: jest.fn().mockImplementation(() => {
      return function(){ return {}};
    }),
  };
});


describe("GenericForm.js Component", () => {

  beforeEach(() => {
    useSelector.mockImplementation(callback => {
      return callback(mockState);
    });
  });
  afterEach(() => {
    useSelector.mockClear();
  });

  test("Test GenericForm.js exists", async () => {
    const {findByText} = renderWithProviders(
        <Route path="/form/:entityName">
          <GenericForm/>
        </Route>,
        {
          route: "/form/munt"
        }
    );
    await findByText("ERROR: Entity 'Munt' missing from API Schema");
  });


  test("Test GenericForm.js TestEntity renders form submit button", async () => {

    const {findByText} = renderWithProviders(
        <Route path="/form/:entityName">
          <GenericForm/>
        </Route>,
        {
          route: "/form/TestEntity"
        }
    );
    await findByText("Submit");
  });

});



