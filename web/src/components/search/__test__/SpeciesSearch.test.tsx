import {afterEach, describe} from '@jest/globals';
import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import SpeciesSearch from '../SpeciesSearch';

const visibleProps = ['class', 'family', 'genus', 'order', 'phylum', 'species', 'status'];
const props = [...visibleProps, 'supersededBy', 'unacceptReason', 'aphiaId'];

// Create array of [3][50] where field name is props and field values are combination of field names and iteration value
const longPages = Array.from({length: 3}, (_, i) => i).map((p) =>
  Array.from({length: p == 2 ? 25 : 50}, (_, i) => i).map((i) => {
    return props.reduce((row, prop) => {
      row[prop] = `${prop}.${p}.${i}`;
      return row;
    }, {});
  })
);

const server = (data) => setupServer(
  rest.get('/api/v1/species', (req, res, ctx) => {
    const pageParam = req.url.search
      .match(/page=.?/gm)
      ?.at(0)
      .slice(-1);
    const pageData = data[pageParam ? parseInt(pageParam) : 0];
    return res(ctx.json(pageData));
  })
);

describe('<SiteList/>', () => {

  let myServer = null;

  afterEach(() => {
    myServer?.close();
    myServer = null;
  });

  it('has both NRMN and WoRMS search options', async () => {
    const {getByText} = render(<SpeciesSearch />);
    await waitFor(() => {
      expect(getByText('WoRMS')).toBeInTheDocument();
      expect(getByText('NRMN')).toBeInTheDocument();
    });
  });

  it('disables the search button if less than 4 characters', async () => {
    const {getByPlaceholderText, getByTestId} = render(<SpeciesSearch />);
    const searchBox = getByPlaceholderText('WoRMS Search');

    myServer = server(longPages);
    myServer.listen();

    fireEvent.change(searchBox, {target: {value: '1234'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeEnabled());

    fireEvent.change(searchBox, {target: {value: '123'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeDisabled());
  });

  it('paginates', async () => {

    const {getByPlaceholderText, getByTestId, getByLabelText, queryByText, rerender} = render(<SpeciesSearch />);
    const searchBox = getByPlaceholderText('WoRMS Search');

    myServer = server(longPages);
    myServer.listen();

    fireEvent.change(searchBox, {target: {value: '1234'}});
    fireEvent.click(getByTestId('search-button'));

    const expectedPage = (page) => {
      for (const prop of visibleProps) {
        expect(queryByText(`${prop}.${page}.0`)).toBeInTheDocument();
        expect(queryByText(`${prop}.${page + 1}.0`)).not.toBeInTheDocument();

        if (page > 0) expect(queryByText(`${prop}.${page - 1}.0`)).not.toBeInTheDocument();

        expect(queryByText(`${prop}.${page}.${longPages[page].length - 1}`)).toBeInTheDocument();
        expect(queryByText(`${prop}.${page}.${longPages[page].length}`)).not.toBeInTheDocument();
      }
      expect(queryByText(`${page * 50 + 1}–${page * 50 + longPages[page].length} of`, {exact: false})).toBeInTheDocument();
    };

    await waitFor(() => expectedPage(0));

    fireEvent.click(getByLabelText('Go to next page'));
    rerender(<SpeciesSearch />);

    await waitFor(() => expectedPage(1));

    fireEvent.click(getByLabelText('Go to next page'));
    await waitFor(() => expectedPage(2));

    fireEvent.click(getByLabelText('Go to previous page'));
    await waitFor(() => expectedPage(1));

    fireEvent.click(getByLabelText('Go to previous page'));
    await waitFor(() => expectedPage(0));
  });
});
