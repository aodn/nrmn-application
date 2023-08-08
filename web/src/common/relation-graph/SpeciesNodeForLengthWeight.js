import React, { useCallback, memo, useState } from 'react';
import { Handle, Position } from 'reactflow';
import { PropTypes } from 'prop-types';
import { Card, CardHeader, CardContent, CardActions, CardActionArea } from '@mui/material';
import IconButton from '@mui/material/IconButton';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemText from '@mui/material/ListItemText';
import ListSubheader from '@mui/material/ListSubheader';
import { useNavigate } from 'react-router-dom';

import KeyboardArrowUpIcon from '@mui/icons-material/KeyboardArrowUp';
import KeyboardDoubleArrowUpRoundedIcon from '@mui/icons-material/KeyboardDoubleArrowUpRounded';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import KeyboardDoubleArrowDownIcon from '@mui/icons-material/KeyboardDoubleArrowDown';
import AlertDialog from '../../components/ui/AlertDialog';

const SpeciesNodeForLengthWeight = ({ data, isConnectable }) => {
  const navigate = useNavigate();
  const [showDialog, setShowDialog] = useState(0);
  const [showDialogCascade, setShowDialogCascade] = useState(0);

  const onContentClick = useCallback((id) => {
    navigate(`/reference/observableItem/${id}/edit`);
  },[navigate]);

  return (
    <div className="text-updater-node">
      <AlertDialog
        open={showDialog !== 0}
        text="Confirm one level Update and Save?"
        action="Submit"
        onClose={() => setShowDialog(0)}
      />
      <AlertDialog
        open={showDialogCascade !== 0}
        text="Confirm multiple level Update and Save?"
        action="Submit"
        onClose={() => setShowDialogCascade(0)}
      />
      <Handle type="target" position={Position.Top} isConnectable={isConnectable} />
      <Card sx={{ width: data.nodeWidth, height: data.nodeHeight }}>
        <CardHeader
          sx={ data.isFocus ? { backgroundColor: 'lightblue'} : { backgroundColor: 'lightgrey'}}
          title = {data.label}
        />
        <CardActionArea onClick={() => onContentClick(data.id)}>
          <CardContent>
            <List
              sx={{
                width: '100%',
                bgcolor: 'background.paper',
                position: 'relative',
                overflow: 'auto',
                height: '100%',
                maxHeight: 200,
                '& ul': { padding: 0 },
              }}
              subheader={<li />}
            >
                <li key={`section-lengthWeight-${data.id}`}>
                  <ul>
                    <ListSubheader>Length Weight</ListSubheader>
                    <ListItem key={`item-lengthWeight-${data.id}-a`}>
                      <ListItemText primary={`a: ${data.lengthWeightA}`}></ListItemText>
                    </ListItem>
                    <ListItem key={`item-lengthWeight-${data.id}-b`}>
                      <ListItemText primary={`b: ${data.lengthWeightB}`}></ListItemText>
                    </ListItem>
                    <ListItem key={`item-lengthWeight-${data.id}-cf`}>
                      <ListItemText primary={`cf: ${data.lengthWeightCf}`}></ListItemText>
                    </ListItem>
                  </ul>
                </li>
            </List>
          </CardContent>
        </CardActionArea>
        <CardActions>
          <IconButton aria-label="Copy one level up"
                      onClick={() => data.onUpdateParent(data.id, data.lengthWeightA, data.lengthWeightB, data.lengthWeightCf,false)}
                      disabled={data.hasParent()}>
            <KeyboardArrowUpIcon fontSize="large" />
          </IconButton>
          <IconButton aria-label="Copy to all level up"
                      onClick={() => data.onUpdateParent(data.id,data.lengthWeightA, data.lengthWeightB, data.lengthWeightCf,true)}
                      disabled={data.hasParent()}>
            <KeyboardDoubleArrowUpRoundedIcon fontSize="large" />
          </IconButton>
          <IconButton aria-label="Copy one level down"
                      onClick={() => data.onUpdateChildren(data.id,data.lengthWeightA, data.lengthWeightB, data.lengthWeightCf, false)}
                      disabled={data.hasChildren()}>
            <KeyboardArrowDownIcon fontSize="large" />
          </IconButton>
          <IconButton aria-label="Copy to all level down"
                      onClick={() => data.onUpdateChildren(data.id,data.lengthWeightA, data.lengthWeightB, data.lengthWeightCf,true)}
                      disabled={data.hasChildren()}>
            <KeyboardDoubleArrowDownIcon fontSize="large" />
          </IconButton>
        </CardActions>
      </Card>
      <Handle type="source" position={Position.Bottom} id="b" isConnectable={isConnectable} />
    </div>
  );

};

SpeciesNodeForLengthWeight.propTypes = {
  data: PropTypes.object.isRequired,
  isConnectable: PropTypes.bool.isRequired
};

export default memo(SpeciesNodeForLengthWeight);