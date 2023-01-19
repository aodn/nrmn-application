import React from 'react';
import {PropTypes} from 'prop-types';

const ApplicationError = ({error}) => {
  const data = error?.response?.data;
  return (
    <div className="error">
      <div>
        <b>{error?.message}</b>
        {data && (
          <>
            <br />
            {data}
          </>
        )}
      </div>
      <button onClick={() => window.location.reload()}>Refresh Page</button>
    </div>
  );
};

export default ApplicationError;

ApplicationError.propTypes = {
  error: PropTypes.object
};
