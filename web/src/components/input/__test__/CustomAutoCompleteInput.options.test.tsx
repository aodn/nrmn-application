// @ts-ignore
import React from 'react';
import '@testing-library/jest-dom/extend-expect';
import { render, waitFor, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import { describe, beforeAll, afterEach } from '@jest/globals';
import { Router } from 'react-router-dom';
import * as axiosInstance from '../../../api/api';
import { AxiosResponse } from 'axios';
import CustomAutoCompleteInput from '../CustomAutoCompleteInput';
import { createMemoryHistory } from 'history';

jest.setTimeout(10000);

describe('<CustomAutoCompleteInput/> filter testing', () => {
  test('Text not appears in options create warning', async () => {

  });
});