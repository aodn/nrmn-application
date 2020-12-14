import React, { Component } from 'react';
import _ from 'lodash';
import PropTypes from 'prop-types';

export default class CustomTooltip extends Component {



  getReactContainerClasses() {
    return ['custom-tooltip'];
  }

  render() {

    let data;

    if (typeof this.props.value === 'object') {

      data = JSON.stringify(this.props.value)?.replaceAll(/["{}]/g,'').trim();

      if (data.length > 0) {
        return (
              <div className={'custom-tooltip'} >
              <span>
                <div className={'body'}>
                  {data.split(',').map( item => <p key={ _.uniqueId('tooltipItem-')} >{item}</p>)}
                </div>
              </span>
            </div>
        );
      }
    }
    return (<div className={'custom-tooltip ag-tooltip-hiding'} ></div>);
  }
}

CustomTooltip.propTypes = {
  value: PropTypes.object
};