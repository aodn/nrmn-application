
import React from 'react';
import { makeStyles, useTheme } from '@material-ui/core/styles';
import Drawer from '@material-ui/core/Drawer';
import Divider from '@material-ui/core/Divider';
import IconButton from '@material-ui/core/IconButton';
import ChevronLeftIcon from '@material-ui/icons/ChevronLeft';
import ChevronRightIcon from '@material-ui/icons/ChevronRight';
import List from '@material-ui/core/List';
import ListSubheader from '@material-ui/core/ListSubheader';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import InboxIcon from '@material-ui/icons/MoveToInbox';
import ExpandLess from '@material-ui/icons/ExpandLess';
import ExpandMore from '@material-ui/icons/ExpandMore';
import Collapse from '@material-ui/core/Collapse';
import StarBorder from '@material-ui/icons/StarBorder';
import { connect } from 'react-redux';
import { toggleLeftSideMenu } from './layout-reducer';
import store from '../store';
import {
    NavLink
  } from 'react-router-dom';
import {ArrowRight} from '@material-ui/icons';
import {PropTypes} from 'prop-types';



const drawerWidth = 240;

const useStyles = makeStyles((theme) => ({
    drawer: {
        width: drawerWidth,
        flexShrink: 0,
    },
    drawerPaper: {
        width: drawerWidth,
    },
    drawerHeader: {
        display: 'flex',
        alignItems: 'center',
        padding: theme.spacing(0, 1),
        // necessary for content to be below app bar
        ...theme.mixins.toolbar,
        justifyContent: 'flex-end',
    }
}));

const mapStateToProps = state => {
    return { leftSideMenuIsOpen: state.toggle.leftSideMenuIsOpen };
  };

const handleMainMenu = () => {
    store.dispatch(toggleLeftSideMenu());
};

const ReduxSideMenu = ({leftSideMenuIsOpen}) => {
    const [openSub, setOpenSub] = React.useState(true);
    const classes = useStyles();
    const theme = useTheme();

    const handleClick = () => {
        setOpenSub(!openSub);
    };

    return (
        <Drawer className={classes.drawer}
            variant="persistent"
            anchor="left"
            open={leftSideMenuIsOpen}
            classes={{
                paper: classes.drawerPaper,
            }}
        >
            <div className={classes.drawerHeader}>
                <IconButton onClick={handleMainMenu}>
                    {theme.direction === 'ltr' ? <ChevronLeftIcon /> : <ChevronRightIcon />}
                </IconButton>
            </div>
            <Divider />
            <List>
                <ListSubheader>DATA</ListSubheader>
                <ListItem button onClick={handleClick}>
                    <ListItemIcon>
                        <InboxIcon />
                    </ListItemIcon>
                    <ListItemText primary=" Uploads" />
                    {openSub ? <ExpandLess /> : <ExpandMore />}
                </ListItem>
                <Collapse in={openSub} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                        <ListItem button  onClick={handleMainMenu}  className={classes.nested} component={NavLink} aweirdprop="true" to="/list/StagedJob">
                            <ListItemText primary="List Jobs"   />
                        </ListItem>
                        <ListItem button  onClick={handleMainMenu} className={classes.nested} component={NavLink} to="/import-file">
                            <ListItemText primary="Add Job" />
                        </ListItem>
                    </List>
                </Collapse>

                <ListItem button key={'Survey'}>
                    <ListItemText primary={'Survey'} />
                </ListItem>
            </List>
            <Divider />
            <List>
                <ListSubheader>REFERENCE DATA</ListSubheader>
                {['Diver', 'Location', 'ObservableItem', 'Program', 'Site', 'Survey'].map((text, index) => (
                    <ListItem button key={text} component={NavLink} to={'/list/' + text} >
                        <ListItemText primary={text} />
                    </ListItem>
                ))}
            </List>
            <Divider />
            <List>
                <ListSubheader>ACTIONS</ListSubheader>

                {['Extract', 'Templates'].map((text, index) => (
                    <ListItem button key={text}>
                        <ListItemText primary={text} />
                    </ListItem>
                ))}
            </List>
        </Drawer>);
};

ReduxSideMenu.propTypes = {
    leftSideMenuIsOpen : PropTypes.bool
};
const SideMenu = connect(mapStateToProps)(ReduxSideMenu);
export default SideMenu;
