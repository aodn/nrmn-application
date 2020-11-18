import React, { Component } from 'react';

export default class CustomTooltip extends Component {

  getReactContainerClasses() {
    return ['custom-tooltip'];
  }

  stringToHTML = (str) => {
    let parser = new DOMParser();
    let doc = parser.parseFromString(str, 'text/html');
    return doc.body;
  };

  render() {

    let data;

    if (typeof this.props.value === 'object') {

      data = JSON.stringify(this.props.value)?.replaceAll(/["\{\}]/g,'').trim();

      if (data.length > 0) {
        return (<div className={"custom-tooltip"} >
              <span>
                <div className={"body"}>{data.split(",").map(item => <p>{item}</p>)}</div>
              </span>
            </div>
        );
      }
    }

    return (<div className={"custom-tooltip ag-tooltip-hiding"} ></div>)


  }
}