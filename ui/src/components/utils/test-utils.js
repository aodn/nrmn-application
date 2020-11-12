import React from "react";
import { createMemoryHistory } from "history";
import { Router, Route } from "react-router-dom";
import { render } from "@testing-library/react";

export function renderWithProviders(
    ui,
    {
      route = "/",
      history = createMemoryHistory({ initialEntries: [route] })
    } = {},
    apolloMocks
) {
  console.log("I am route inside renderWithProviders Wrapper:", route);
  return {
    ...render(
        <Router history={history}>
          <Route path={route}>{ui}</Route>
        </Router>
    ),
    history
  };
}