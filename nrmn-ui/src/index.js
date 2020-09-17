import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import * as serviceWorker from './serviceWorker';
import { Provider } from 'react-redux';
import store from './components/store';
import {apiConfig} from "./axios/api";
import config from 'react-global-configuration';
import SwaggerParser from "@apidevtools/swagger-parser";

apiConfig().then((result) => {

  SwaggerParser.dereference(result.data).then((api) => {
    config.set({api: api});
    ReactDOM.render(
        <React.StrictMode>
          <Provider store={store}>
            <App/>
          </Provider>
        </React.StrictMode>, document.getElementById('root')
    );
  });


});

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
