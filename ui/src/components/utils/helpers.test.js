import './helpers';
import {markupProjectionQuery} from './helpers';

test('markupProjectionQuery function', () => {

  const relativeUrl = markupProjectionQuery('arelativeurl');
  const api1 = markupProjectionQuery('http://localhost:8080/api/locations/1{?projection}');

  expect(relativeUrl).toEqual('arelativeurl?projection=selection');
  expect(api1).toEqual('http://localhost:8080/api/locations/1?projection=selection');
});