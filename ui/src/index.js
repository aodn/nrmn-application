import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import {Provider} from 'react-redux';
import store from './components/store';
import {apiDefinition} from './axios/api';
import config from 'react-global-configuration';
import './index.css';

window.setApplicationError = (message, error) => {
  if (message) {
    document.write(`<h1>National Reef Monitoring Network</h1> 
    <p>The server may be experiencing problems. Please wait a moment and try again.<br>
    If this problem persists, please contact info@aodn.org.au.</p>
    <b>Error: ${message}</b><br>
    <hr><small>`);
    document.write(JSON.stringify(error));
  }
};

apiDefinition().then((result) => {
  config.set({api: result.data.components.schemas});
  ReactDOM.render(
    <React.StrictMode>
      <Provider store={store}>
        <App />
      </Provider>
    </React.StrictMode>,
    document.getElementById('root')
  );
});
