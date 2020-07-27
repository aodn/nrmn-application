import React from 'react';
import clsx from 'clsx';
import { makeStyles } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CssBaseline';
import TopBar from './components/layout/TopBar';
import SideMenu from './components/layout/SideMenu';
import XlxsUpload from './components/import/XlxsUpload';
import DataSheetView from './components/import/DataSheetView';
const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
  },

  content: {
    flexGrow: 1,
    padding: theme.spacing(3),
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen,
    }),
    marginLeft: -drawerWidth,
  },
  contentShift: {
    transition: theme.transitions.create('margin', {
      easing: theme.transitions.easing.easeOut,
      duration: theme.transitions.duration.enteringScreen,
    }),
    marginLeft: 0,
  },
}));

export default function PersistentDrawerLeft() {
  const classes = useStyles();

  return (
    <div className={classes.root}>
      <CssBaseline />
      <TopBar></TopBar>
      <SideMenu></SideMenu>
      <main
        className={clsx(classes.content, {
          [classes.contentShift]: false,
        })}
      >W
        <div className={classes.drawerHeader} />
       <XlxsUpload></XlxsUpload>
       <DataSheetView></DataSheetView>
      </main>
    </div>
  );
}
