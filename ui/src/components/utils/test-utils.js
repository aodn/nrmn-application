import React from "react";
import { createMemoryHistory } from "history";
import { Router, Route } from "react-router-dom";
import { render } from "@testing-library/react";

// USAGE
// See https://testing-library.com/docs/dom-testing-library/api-queries
// Returns the specified testing functions
//
//    const {findByTitle, findByText} = renderWithProviders(
//         <Route path="/list/:entityName">
//           <EntityList/>
//         </Route>,
//         {
//           route: "/list/TestEntity"
//         }
//     );
//     await findByTitle("Add new TestEntity");
//
//

export function renderWithProviders(
    ui,
    {
      route = "/",
      history = createMemoryHistory({ initialEntries: [route] })
    } = {}
) {
  return {
    ...render(
        <Router history={history}>
          <Route path={route}>{ui}</Route>
        </Router>
    ),
    history
  };
}