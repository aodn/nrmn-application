import {afterEach, describe} from '@jest/globals';
import '@testing-library/jest-dom';
import { fireEvent, screen, render, waitFor, prettyDOM } from '@testing-library/react';
import {rest} from 'msw';
import {setupServer} from 'msw/node';
import SpeciesSearch from '../SpeciesSearch';

const visibleProps = ['class', 'family', 'genus', 'order', 'phylum', 'species', 'status'];
const props = [...visibleProps, 'supersededBy', 'unacceptReason', 'aphiaId'];

// dynamic create an array where size of page size and data contains identity of page value
const createPageData = (page: number, pageSize: number) =>
  Array.from({length: pageSize}, (_, i) => i).map((i) => {
    return props.reduce((row: any, prop) => {
      row[prop] = `${prop}.${page}.${i}`;
      return row;
    }, {});
  });

const handler = (properties: any) =>
  rest.get('/api/v1/species', (req, res, ctx) => {
    const pageParam = req.url.search.match(/page=.[0-9]*/gm)?.pop();
    const pageSizeParam = req.url.search.match(/pageSize=.[0-9]*/gm)?.pop();

    const page = pageParam === undefined ? 0 : parseInt(pageParam.replace('page=',''));
    const pageSize = pageSizeParam ? parseInt(pageSizeParam.replace('pageSize=','')) : 0;

    // Remove server default page is 50, if you have total of 100 then you have 2 page
    // if you have total of 101 then you have 3 page where page 3 contains 1 item
    const length = ((page * 50) + pageSize < properties.maxDataSize) ? pageSize : properties.maxDataSize - page * 50;
    properties.invokeCount++;

    return res(ctx.json(createPageData(page,length)));
  });

const expectedPage = (page: number, pageSize: number) => {
  for (const prop of visibleProps) {
    expect(screen.queryByText(`${prop}.${page}.0`)).toBeInTheDocument();
    expect(screen.queryByText(`${prop}.${page + 1}.0`)).not.toBeInTheDocument();

    if (page > 0) expect(screen.queryByText(`${prop}.${page - 1}.0`)).not.toBeInTheDocument();

    expect(screen.queryByText(`${prop}.${page}.${pageSize - 1}`)).toBeInTheDocument();
    expect(screen.queryByText(`${prop}.${page}.${pageSize}`)).not.toBeInTheDocument();
  }
};

describe('<SiteList/>', () => {

  let myServer: any = null;

  beforeAll(() => {
    myServer = setupServer();
    myServer.listen();
  });

  afterAll(() => {
    myServer?.close();
    myServer = null;
  });

  afterEach(() => {
    myServer?.resetHandlers();
  });

  it('has both NRMN and WoRMS search options', async () => {
    const {getByText} = render(<SpeciesSearch />);
    await waitFor(() => {
      expect(getByText('WoRMS')).toBeInTheDocument();
      expect(getByText('NRMN')).toBeInTheDocument();
    });
  });

  it('disables the search button if less than 4 characters', async () => {

    myServer.use(handler({maxDataSize: 150, invokeCount: 0}));

    const {getByPlaceholderText, getByTestId} = render(<SpeciesSearch />);
    const searchBox = getByPlaceholderText('WoRMS Search');

    fireEvent.change(searchBox, {target: {value: '1234'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeEnabled());

    fireEvent.change(searchBox, {target: {value: '123'}});
    await waitFor(() => expect(getByTestId('search-button')).toBeDisabled());
  });

  it('Paginates with 3 pages', async () => {
    const properties = {maxDataSize: 150, invokeCount: 0};
    myServer.use(handler(properties));

    render(<SpeciesSearch/>);
    const searchBox = screen.getByPlaceholderText('WoRMS Search');

    fireEvent.change(searchBox, {target: {value: '1234'}});
    fireEvent.click(screen.getByTestId('search-button'));

    await waitFor(() => screen.findByText('class.0.0'))
      .then(() => {
        expectedPage(0, 50);
        expect(properties.invokeCount).toBe(1);
      });

    fireEvent.click(screen.getByLabelText('Go to next page'));
    await waitFor(() => screen.findByText('class.1.0'))
      .then(() => {
        expectedPage(1, 50);
        expect(properties.invokeCount).toBe(2);
      });

    fireEvent.click(screen.getByLabelText('Go to next page'));
    await waitFor(() => screen.findByText('class.2.0'))
      .then(() => {
        expectedPage(2, 50);
        expect(properties.invokeCount).toBe(3);
      });

    // Should not have next page, so the class should contains Mui-disabled
    expect(screen.getByLabelText('Go to next page')).toHaveClass('Mui-disabled');

    fireEvent.click(screen.getByLabelText('Go to previous page'));
    await waitFor(() => screen.findByText('class.1.0'))
      .then(() => {
        expectedPage(1, 50);
        // Value cached, so will not count will not increase
        expect(properties.invokeCount).toBe(3);
      });

    fireEvent.click(screen.getByLabelText('Go to previous page'));
    await waitFor(() => screen.findByText('class.0.0'))
      .then(() => {
        expectedPage(0, 50);
        // Value cached, so will not count will not increase
        expect(properties.invokeCount).toBe(3);
      });

    fireEvent.click(screen.getByLabelText('Go to next page'));
    await waitFor(() => screen.findByText('class.1.0'))
      .then(() => {
        expectedPage(1, 50);
        expect(properties.invokeCount).toBe(3);
      });
  });

  it('Paginates with only 1 page', async () => {
    const properties = {maxDataSize: 17, invokeCount: 0};
    myServer.use(handler(properties));

    render(<SpeciesSearch />);
    const searchBox = screen.getByPlaceholderText('WoRMS Search');

    fireEvent.change(searchBox, { target: { value: '1234' } });
    fireEvent.click(screen.getByTestId('search-button'));

    await waitFor(() => screen.findByText('class.0.0'))
      .then(() => {
        expectedPage(0, 17);
      });

    // Should not have next page, so the class should contains Mui-disabled
    expect(screen.getByLabelText('Go to next page')).toHaveClass('Mui-disabled');
  });

});
