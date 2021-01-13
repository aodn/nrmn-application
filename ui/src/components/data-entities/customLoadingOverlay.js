import React, { Component } from 'react';
import {LoadingBanner} from "../layout/loadingBanner";
import {Box} from "@material-ui/core";
import { withStyles } from '@material-ui/styles';

const styles = () => ({
  main: {
    "& > *": {
      margin: 10
    }
  }
});

class CustomLoadingOverlay extends Component {
  render() {
    return (
        <>
          <div className="ag-custom-loading-cell" >
            <Box component="div" ml={2} className={styles.main}>
              <LoadingBanner variant={"h5"} msg={"Loading data..  "  } />
            </Box>
          </div>
        </>
  )
  }
}

export default withStyles(styles)(CustomLoadingOverlay)