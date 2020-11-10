import moment from 'moment-timezone';

export const preventSpaces = value => {
  return value.replace(/ /g, "");
};

export const getSearchParams = (location) => {
  return new URLSearchParams(location.search);
}

export const getFullPath = (location) => {
  return location.pathname + location.search;
}

export const toDisplayUtc = (ts, tz) => {
  const d = moment.tz(ts, tz);
  return d.isValid() ? d.utc().format("YYYY-MM-DD HH:mm") : '-';
}

export const toUTC = (dateTime, timezone) => {
  // Format for sending data to API: 2020-07-18T01:00:00.000+0000
  const original = moment.tz(dateTime, timezone);
  const utc = original.clone().tz("UTC");
  return utc.format("YYYY-MM-DDTHH:mm:ss.SSSZZ");
}

export const toLocaltime = (dateTime, timezone) => {
  const original = moment.tz(dateTime, 'UTC');
  const localTime = original.tz(timezone);
  if(!dateTime || !timezone){
    return dateTime;
  }
  return localTime.format("YYYY-MM-DDTHH:mm:ss.SSSZZ");
}

export const fromUTC = (utc, format) => {
  // For display of data from API
  if(!utc || !utc.timestamp || !utc.timezone) return "-";
  const original = moment(utc.timestamp);
  const dateTime = original.clone().tz(utc.timezone);
  if (!format) format = "YYYY-MM-DD, HH:mm";
  return dateTime.format(format);
}
