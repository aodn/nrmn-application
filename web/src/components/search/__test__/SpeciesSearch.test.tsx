import {afterEach, describe} from '@jest/globals';
import '@testing-library/jest-dom';
import { fireEvent, screen, render, waitFor, prettyDOM } from '@testing-library/react';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import SpeciesSearch from '../SpeciesSearch';

const visibleProps = ['class', 'family', 'genus', 'order', 'phylum', 'species', 'status'];
const props = [...visibleProps, 'supersededBy', 'unacceptReason', 'aphiaId'];

// dynamic create an array where size of page size and data contains identity of page value
const createPageData = (page, pageSize) =>
  Array.from({length: pageSize}, (_, i) => i).map((i) => {
    return props.reduce((row, prop) => {
      row[prop] = `${prop}.${page}.${i}`;
      return row;
    }, {});
  });

const server = (maxDataSize) => setupServer(
  rest.get('/api/v1/species', (req, res, ctx) => {
    const pageParam = req.url.search.match(/page=.[0-9]*/gm)?.pop();
    const pageSizeParam = req.url.search.match(/pageSize=.[0-9]*/gm)?.pop();

    const page = pageParam === undefined ? 0 : parseInt(pageParam.replace('page=',''));
    const pageSize = pageSizeParam ? parseInt(pageSizeParam.replace('pageSize=','')) : 0;

    const pageData = createPageData(page,
      (page * pageSize < maxDataSize) ? pageSize : maxDataSize - (page * pageSize));

    return res(ctx.json(pageData));
  })
);

const expectedPage = (page, pageSize) => {
  for (const prop of visibleProps) {
    expect(screen.queryByText(`${prop}.${page}.0`)).toBeInTheDocument();
    expect(screen.queryByText(`${prop}.${page + 1}.0`)).not.toBeInTheDocument();

    if (page > 0) expect(screen.queryByText(`${prop}.${page - 1}.0`)).not.toBeInTheDocument();

    expect(screen.queryByText(`${prop}.${page}.${pageSize - 1}`)).toBeInTheDocument();
    expect(screen.queryByText(`${prop}.${page}.${pageSize}`)).not.toBeInTheDocument();
  }
//      expect(queryByText(`${page * 50 + 1}–${page * 50 + longPages[page].length} of`, {exact: false})).toBeInTheDocument();
};

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
    myServer = server(150);
    myServer.listen();

    const {getByPlaceholderText, getByTestId} = render(<SpeciesSearch />);
    const searchBox = getByPlaceholderText('WoRMS Search');

    fireEvent.change(searchBox, {target: {value: '1234'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeEnabled());

    fireEvent.change(searchBox, {target: {value: '123'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeDisabled());
  });

  it('paginates', async () => {

    myServer = server(150);
    myServer.listen();

    render(<SpeciesSearch />);
    const searchBox = screen.getByPlaceholderText('WoRMS Search');

    fireEvent.change(searchBox, {target: {value: '1234'}});
    fireEvent.click(screen.getByTestId('search-button'));

    await waitFor(() => screen.findByText('class.0.0'))
      .then(() => {
        expectedPage(0, 50);
      });

    fireEvent.click(screen.getByLabelText('Go to next page'));
    await waitFor(() => screen.findByText('class.1.0'))
      .then(() => expectedPage(1, 50));

    fireEvent.click(screen.getByLabelText('Go to next page'));
    await waitFor(() => screen.findByText('class.2.0'))
      .then(() => expectedPage(2, 50));

    fireEvent.click(screen.getByLabelText('Go to previous page'));
    await waitFor(() => screen.findByText('class.1.0'))
      .then(() => expectedPage(1, 50));

    fireEvent.click(screen.getByLabelText('Go to previous page'));
    await waitFor(() => screen.findByText('class.0.0'))
      .then(() => expectedPage(0, 50));
  });
});
