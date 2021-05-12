import {
  Drawer,
  Divider,
  List,
  ListItem,
  ListItemIcon,
  Badge,
  ListItemText,
  fade,
  makeStyles,
  Box,
  IconButton,
  Toolbar,
  InputBase,
  Fab
} from '@material-ui/core';
import {PlaylistAddCheckOutlined, SearchOutlined} from '@material-ui/icons';
import {BlockOutlined as BlockOutlinedIcon, WarningOutlined as WarningOutlinedIcon} from '@material-ui/icons';
import clsx from 'clsx';
import React, {useState} from 'react';
import {useDispatch, useSelector} from 'react-redux';
import {validationFilter, ValidationRequested} from './reducers/create-import';
import SelectAllOutlinedIcon from '@material-ui/icons/SelectAllOutlined';
import ArrowBackIosOutlinedIcon from '@material-ui/icons/ArrowBackIosOutlined';
import ArrowForwardIosOutlinedIcon from '@material-ui/icons/ArrowForwardIosOutlined';
import Accordion from '@material-ui/core/Accordion';
import AccordionDetails from '@material-ui/core/AccordionDetails';
import AccordionSummary from '@material-ui/core/AccordionSummary';
import Typography from '@material-ui/core/Typography';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';

const drawerWidth = 500;

const useStyles = makeStyles((theme) => ({
  root: {
    display: 'flex',
    '& > *': {
      margin: theme.spacing(1)
    }
  },

  extendedIcon: {
    marginRight: theme.spacing(1)
  },
  drawer: {
    width: drawerWidth,
    flexShrink: 0,
    whiteSpace: 'nowrap'
  },
  drawerOpen: {
    width: drawerWidth,
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.enteringScreen
    })
  },
  drawerClose: {
    transition: theme.transitions.create('width', {
      easing: theme.transitions.easing.sharp,
      duration: theme.transitions.duration.leavingScreen
    }),
    overflowX: 'hidden',
    width: theme.spacing(8) + 1,
    [theme.breakpoints.up('sm')]: {
      width: theme.spacing(9) + 1
    }
  },
  selected: {
    backgroundColor: theme.palette.primary[50]
  },
  toolbar: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'flex-end',
    padding: theme.spacing(0, 1),
    // necessary for content to be below app bar
    ...theme.mixins.toolbar
  },
  content: {
    flexGrow: 1,
    padding: theme.spacing(3)
  },
  errorItem: {
    backgroundColor: fade('#ff0000', 0.15)
  },
  searchIcon: {
    padding: theme.spacing(0, 2),
    height: '100%',
    position: 'absolute',
    pointerEvents: 'none',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center'
  },
  search: {
    position: 'relative',
    borderRadius: theme.shape.borderRadius,
    backgroundColor: fade(theme.palette.common.white, 0.15),
    '&:hover': {
      backgroundColor: fade(theme.palette.common.white, 0.25)
    },
    marginRight: theme.spacing(2),
    marginLeft: 0,
    width: '100%',
    [theme.breakpoints.up('sm')]: {
      marginLeft: theme.spacing(3),
      width: 'auto'
    }
  },
  inputRoot: {
    color: 'inherit'
  },
  inputInput: {
    padding: theme.spacing(1, 1, 1, 0),
    backgroundColor: '#f0f0f0',
    paddingLeft: `calc(1em + ${theme.spacing(4)}px)`,
    transition: theme.transitions.create('width'),
    width: '100%',
    [theme.breakpoints.up('md')]: {
      width: '20ch'
    }
  }
}));

const ValidationDrawer = () => {
  const dispatch = useDispatch();
  const classes = useStyles();
  const errorsByMsg = useSelector((state) => state.import.errorsByMsg);
  const errSelected = useSelector((state) => state.import.errSelected);
  const isLoading = useSelector((state) => state.import.isLoading);
  const editLoading = useSelector((state) => state.import.editLoading);
  const job = useSelector((state) => state.import.job);

  const [open, setOpen] = useState(false);
  const [filter, setFilter] = useState('');

  const handleFilter = (err) => {
    dispatch(validationFilter(err));
  };

  const handleValidate = () => {
    if (job.id) {
      dispatch(ValidationRequested(job.id));
    }
  };

  const titleCase = (word) => {
    const result = word.replace(/([A-Z])/g, ' $1');
    const finalResult = result.charAt(0).toUpperCase() + result.slice(1);
    return finalResult;
  };

  const removeFilter = () => {
    dispatch(validationFilter({ids: null}));
    setFilter('');
  };

  const filterChange = (event) => {
    const {value: targetValue} = event.target;
    setFilter(targetValue.toLowerCase());
  };

  var errList = Object.keys(errorsByMsg).map((label) => {
    const b = errorsByMsg[label].find((e) => e.errorLeve === 'BLOCKING');
    return {
      key: label,
      total: errorsByMsg[label].length,
      value: errorsByMsg[label],
      blocking: b ? true : false
    };
  });

  if (errList && errList.length > 0 && filter !== '') {
    errList = errList.map((pair) => ({
      key: pair.key,
      total: pair.total,
      value: pair.value.filter((err) => err.message.toLowerCase().indexOf(filter) >= 0),
      blocking: errList.find((e) => e.errorLeve === 'BLOCKING') ? true : false
    }));
  }
  return errList && errList.length > 0 ? (
    <Drawer
      anchor="right"
      variant="permanent"
      className={clsx(classes.drawer, {
        [classes.drawerOpen]: open,
        [classes.drawerClose]: !open
      })}
      classes={{
        paper: clsx({
          [classes.drawerOpen]: open,
          [classes.drawerClose]: !open
        })
      }}
    >
      <div className={classes.toolbar}></div>
      <Divider />
      <Box>
        <Toolbar position="static">
          <IconButton color="inherit" aria-label="open drawer" onClick={() => setOpen(!open)} edge="start" title={open ? 'Close' : 'Open'}>
            {open ? <ArrowForwardIosOutlinedIcon /> : <ArrowBackIosOutlinedIcon />}
          </IconButton>
          <IconButton
            variant="extended"
            disabled={editLoading || isLoading}
            onClick={() => handleValidate()}
            color="inherit"
            title={'Validate'}
          >
            <Fab size="small" color="secondary">
              {' '}
              <PlaylistAddCheckOutlined />
            </Fab>
          </IconButton>
          <div className={classes.search}>
            <div className={classes.searchIcon}>
              <SearchOutlined />
            </div>
            <InputBase
              placeholder="Filter"
              value={filter}
              classes={{
                root: classes.inputRoot,
                input: classes.inputInput
              }}
              inputProps={{'aria-label': 'filter'}}
              onChange={filterChange}
            />
          </div>
          <IconButton title={'Clear Filters'} edge="end" onClick={removeFilter}>
            <SelectAllOutlinedIcon></SelectAllOutlinedIcon>
          </IconButton>
        </Toolbar>
      </Box>
      {errList.map((err) => (
        <Accordion key={err.key}>
          <AccordionSummary expandIcon={<ExpandMoreIcon />} aria-controls="panel1c-content" id="panel1c-header">
            <div className={classes.column}>
              <Typography className={classes.heading}>{titleCase(err.key)}</Typography>
            </div>
            <div className={classes.column}>
              <Typography className={classes.secondaryHeading}> ({err.total}) </Typography>
            </div>
          </AccordionSummary>
          <AccordionDetails className={classes.details}>
            <List>
              {err.value.map((item, i) => (
                <ListItem
                  onClick={() => handleFilter(item)}
                  selected={item.message === errSelected.message}
                  className={item.errorLeve === 'WARNING' ? classes.selected : classes.errorItem}
                  button
                  key={i}
                >
                  <ListItemIcon>
                    <Badge badgeContent={item.count} color="primary">
                      {item.errorLeve == 'WARNING' ? <WarningOutlinedIcon color="error" /> : <BlockOutlinedIcon color="error" />}
                    </Badge>
                  </ListItemIcon>
                  <ListItemText color="secondary" primary={item.message} secondary={item.columnTarget} />
                </ListItem>
              ))}
            </List>
          </AccordionDetails>
        </Accordion>
      ))}
    </Drawer>
  ) : (
    <></>
  );
};

export default ValidationDrawer;
