// @ts-ignore
import React from 'react';
import {describe} from '@jest/globals';
import {logRoles, render, screen, waitFor} from '@testing-library/react';
import '@testing-library/jest-dom';
import DriverList from '../DiverList';
import {BrowserRouter} from 'react-router-dom';
import * as axiosInstance from '../../../../api/api';
import {AxiosResponse} from 'axios';

jest.setTimeout(10000);

describe('<DiverList/>', () => {

    let mockGetResult;

    beforeAll(() => {
        mockGetResult = jest.spyOn(axiosInstance, 'getResult');
    });

    afterEach(() => {
        mockGetResult.mockRestore();
    });

    it('grid columns auto sizing after data load', async () => {
        // Load sample data.
        const canned = require('./diver.json');

        // Override function so that it return the data we set.
        mockGetResult.mockImplementation((url) => {
            console.debug('Mocked getResult called');
            const raw = {
                config: undefined,
                data: canned,
                headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
                status: 200,
                statusText: url
            };

            return new Promise<AxiosResponse>((resolve => {
                resolve(raw);
            }));
        });

        // Need to wrap with a Router otherwise the useLocation() error shows, the result will auto set to screen object
        render(<BrowserRouter><DriverList/></BrowserRouter>);

        // Wait for screen to complete render, react test library do not recommend you to touch the component
        // internal, so indirectly test it via screen display. Andrew Altieri is the first diver on the first page
        await waitFor(() => screen.findByText('Andrew Altieri'))
            .then(() => {
                // For debug to tell exactly what is inside.
                logRoles(screen.getByRole('grid'));

                const gridcells = screen.getAllByRole('gridcell');

                // Without adjust the size of browser the number of row display is 12, since we have two column so it is 24
                expect(gridcells).toHaveLength(24);
                expect(screen.getByText('Ana Gloria Guzman')).toBeInTheDocument();   // Last diver on the screen
                expect(screen.getByText('Ana Gloria Guzman')).toHaveStyle({width: '48px'});   // Last diver on the screen
            });
    });
});