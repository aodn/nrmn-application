import { describe } from '@jest/globals';
import * as api from '../api';
import axiosInstance from '../index.js';
import '@testing-library/jest-dom';

describe('Test api functions', () => {

  const onLocked = jest.fn().mockImplementation(res => console.log('lock called')).mockName('locked');
  const onResult = jest.fn().mockImplementation(res => console.log('result called')).mockName('result');

  afterEach(() => {
    // restore the spy created with spyOn
    jest.restoreAllMocks();
  });

  test('submitIngest call lock function if db global lock is on', async () => {
    const mockAxiosPost = jest.spyOn(axiosInstance, 'post');

    mockAxiosPost.mockImplementation((a, b, c) =>
      new Promise((resolve) => {
        resolve({
          headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
          status: 400,
          data: {
            jobStatus: 'FAILED',
            reason: 'locked'
          }
        });
      }));

    // Must use await here otherwise the expect run too fast and always failed
    await api.submitIngest(33, onLocked, onResult, axiosInstance);

    expect(mockAxiosPost).toHaveBeenCalled();
    expect(onLocked).toHaveBeenCalled();
    expect(onResult).not.toHaveBeenCalled();
  });

  test('submitIngest call result function when jobStatus changed to INGESTED', async () => {
    const mockAxiosPost = jest.spyOn(axiosInstance, 'post');

    mockAxiosPost.mockImplementation((a, b, c) =>
      new Promise((resolve) => {
        resolve({
          headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
          status: 200,
          data: {
            jobStatus: 'INGESTING',
          }
        });
      }));

    const mockAxiosGet = jest.spyOn(axiosInstance, 'get');
    mockAxiosGet
      .mockReturnValueOnce(
        new Promise((resolve) => {
          resolve({
            headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
            status: 200,
            data: {
              jobStatus: 'INGESTING',
            }
          });
        }))
      .mockReturnValueOnce(
        new Promise((resolve) => {
          resolve({
            headers: { 'Content-Type': 'application/json', Accept: 'application/json' },
            status: 200,
            data: {
              jobStatus: 'INGESTED',
            }
          });
        }));

    // Must use await here otherwise the expect run too fast and always failed
    // also lower the timeout so test complete faster
    await api.submitIngest(33, onLocked, onResult, axiosInstance, 10);

    expect(mockAxiosPost).toBeCalledTimes(1);
    expect(mockAxiosGet).toBeCalledTimes(2);
    expect(onLocked).not.toHaveBeenCalled();
    expect(onResult).toHaveBeenCalled();
  });
});
