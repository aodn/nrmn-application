import React, { useEffect } from 'react';
import { makeStyles } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import ListItemAvatar from '@material-ui/core/ListItemAvatar';
import Avatar from '@material-ui/core/Avatar';
import DescriptionIcon from '@material-ui/icons/Description';
import { useDispatch, useSelector } from 'react-redux';
import { fileListStarted, fileListRequested } from './reducers/list-import';
import {
    NavLink
  } from 'react-router-dom';

const useStyles = makeStyles((theme) => ({
    root: {
        width: '100%',
        maxWidth: 360,
        backgroundColor: theme.palette.background.paper,
    },
}));

const fileName2ListItem = (fileName) => {
    const split = fileName.split('-');
    const timeStamp = split.slice(-1);
    const timeString = new Date(parseInt(timeStamp)).toUTCString();
    const cleanName = split.slice(0, split.length -1);
    return (<ListItem key={fileName} component={NavLink} to={'/import-file/' + fileName}>
    <ListItemAvatar>
        <Avatar>
            <DescriptionIcon />
        </Avatar>
    </ListItemAvatar>
    <ListItemText primary={cleanName} secondary={timeString} />
</ListItem>);

};

 const FileList = () => {

    const fileIDs = useSelector(state =>  state.fileList.fileIDs);
  //  const isLoading = useSelector(state => state.fileList.isLoading);

    const dispatch = useDispatch();

     useEffect(() => {
        dispatch(fileListRequested());
        dispatch(fileListStarted());

     }, []);

    const classes = useStyles();

    return (
        (fileIDs && fileIDs.length > 0) ?
        (<List className={classes.root}>
            {fileIDs.map(fileName2ListItem)}
        </List>) : <></>
    );
};
 export default FileList;