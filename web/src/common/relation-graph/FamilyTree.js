import React, { useCallback, useEffect } from 'react';
import 'reactflow/dist/style.css';
import SpeciesNodeForLengthWeight from './SpeciesNodeForLengthWeight';
import RestoreIcon from '@mui/icons-material/Restore';
import SaveIcon from '@mui/icons-material/Save';
import SkipNextIcon from '@mui/icons-material/SkipNext';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import {Button, Grid, Box} from '@mui/material';
import ReactFlow, {
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
} from 'reactflow';

import 'reactflow/dist/style.css';
import { PropTypes } from 'prop-types';

const nodeTypes = { customTreeNode: SpeciesNodeForLengthWeight };
const nodeHeight = 400;
const nodeWidth = 300;

/**
 * We know the number of nodes on each level, we can then calcuate the
 * x y coordinate so that node align correctly
 * @param nodes
 */
const recalculateNodeXY = (nodes) => {
  // Get the max depth of all nodes
  let maxDepth = 0;
  nodes.forEach(i => {
    maxDepth = Math.max(maxDepth, i.data.depth);
  });

  // Re-calculate the x, y position so that nodes will not overlap, node
  // depth is zero based
  for(let d = 0; d < (maxDepth + 1); d++) {
    // All nodes of that depth
    let ns = nodes.filter(f => f.data.depth === d);

    for(let k = 0; k < ns.length; k++) {
      ns[k].position.x = (nodeWidth + 20) * (k - ns.length / 2);
      ns[k].position.y = (nodeHeight + 50) * d;
    }
  }
};
/**
 * Find all the Ids that needs to update with weight value
 * @param nodes
 * @param currentSpeciesId
 * @param direction
 * @param isCascade - If true, not only update immediate parent child but generations
 * @param depth
 * @returns {[string]}
 */
const getAllTargetId = (nodes, currentSpeciesId, direction, isCascade, depth=0) => {
  // Exclude self
  const ids = depth === 0 ? [] : ['' + currentSpeciesId];

  if(isCascade || depth ===0) {
    nodes
      .filter(f => f.id === currentSpeciesId)
      .map(m => direction === 'up' ? m.data.getParentId() : m.data.getChildrenId())
      .flat()   // getChildrenId() return array, so you will have array of array, hence use flat() to make it to 1d
      .filter(f => f !== null)  // In case no more parent, then no need to loop
      .forEach(k => ids.push(...getAllTargetId(nodes, k, direction, isCascade, depth + 1)));
  }

  return ids;
};

const FamilyTree = ({ items, focusNodeId, onSkipLengthWeightChange, onSaveLengthWeightChange, onExistEdit }) => {
  // const defaultViewport = { x: 0, y: 0, zoom: 0.8 };
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

  const saveChanges = useCallback(() => {
      const updatedItems = nodes
        .filter(f => (
          f.data.lengthWeightA !== f.data.originalWeightA
          || f.data.lengthWeightB !== f.data.originalWeightB
          || f.data.lengthWeightCf !== f.data.originalWeightCf)
        )
        .map(i => {
          return {
            observableItemId: i.data.id,
            lengthWeightA: i.data.lengthWeightA,
            lengthWeightB: i.data.lengthWeightB,
            lengthWeightCf: i.data.lengthWeightCf,
          };
        });

      onSaveLengthWeightChange(updatedItems);
      // Update changed items
  }, [nodes, onSaveLengthWeightChange]);

  const undoAllChanges = useCallback(() => {
    setNodes((nodes) =>
      nodes.map((node) => {
        return {
          ...node,
          data: {
            ...node.data,
            lengthWeightA: node.data.originalWeightA,
            lengthWeightB: node.data.originalWeightB,
            lengthWeightCf: node.data.originalWeightCf
          }
        };
    }));
  }, [setNodes]);

  const onUpdateSpecies = useCallback((currentSpeciesId, data, direction, isCascade) => {
    // Update the useStates of nodes.
    setNodes((nodes) => {
      // We get a list of ids that needs to update weight
      const targetIds = new Set(getAllTargetId(nodes, currentSpeciesId, direction, isCascade));

      return nodes.map((node) => {
          // We find the target
          if(!targetIds.has(node.id)) {
            return node;
          }

          return {
            ...node,
            data: {
              ...node.data,
              lengthWeightA: data.a,
              lengthWeightB: data.b,
              lengthWeightCf: data.cf
            }
          };
        });
    });
  }, [setNodes]);

  const createReactFlowNode = useCallback((value, depth) => {
    return {
        id: '' + value.self.observableItemId,
        type: 'customTreeNode',
        position: { x: 0, y: 0 },
        data: {
          id: '' + value.self.observableItemId,
          depth: depth,
          isFocus: value.self.observableItemId === focusNodeId,
          nodeHeight: nodeHeight,
          nodeWidth: nodeWidth,
          observableItemName: `${value.self.observableItemName}`,
          lengthWeightA: value.self.lengthWeightA === null ? '' : value.self.lengthWeightA,
          lengthWeightB: value.self.lengthWeightB === null ? '' : value.self.lengthWeightB,
          lengthWeightCf: value.self.lengthWeightCf === null ? '' : value.self.lengthWeightCf,

          originalWeightA: value.self.lengthWeightA === null ? '' : value.self.lengthWeightA,
          originalWeightB: value.self.lengthWeightB === null ? '' : value.self.lengthWeightB,
          originalWeightCf: value.self.lengthWeightCf === null ? '' : value.self.lengthWeightCf,

          onUpdateParent: (id, a, b, cf, isCascade) => onUpdateSpecies(id, { a: a, b: b, cf: cf }, 'up', isCascade),
          onUpdateChildren: (id, a, b, cf, isCascade) => onUpdateSpecies(id, { a: a, b: b, cf: cf }, 'down', isCascade),

          hasParent: () => value.parent === null,
          hasChildren: () => value.children === null || value.children.length === 0,

          getParentId: () => value.parent === null ? null : '' + value.parent.observableItemId,
          getChildrenId: () => value.children === null ? null  : value.children.map(m => '' + m.self.observableItemId)
        }
      };
    }, [focusNodeId, onUpdateSpecies]);

  const createReactFlowNodes = useCallback((nodes, value, depth = 0) => {
    if(value != null) {
      nodes.nodes.push(createReactFlowNode(value, depth));

      if(value.children != undefined) {

        value.children.forEach(i => {
          nodes.edges.push({
            id: `${value.self.observableItemId}-${i.self.observableItemId}`,
            source: '' + value.self.observableItemId,
            target: '' + i.self.observableItemId
          });

          createReactFlowNodes(nodes, i, depth + 1);
        });
      }
    }
  }, [createReactFlowNode]);

  useEffect(() => {
    const reactFlowNodes = {
      nodes: [],
      edges: []
    };

    // Create the node data for react flow
    createReactFlowNodes(reactFlowNodes, items);

    // No need to update if no nodes there
    if(reactFlowNodes.nodes.length != 0) {
      recalculateNodeXY(reactFlowNodes.nodes);
      setNodes(reactFlowNodes.nodes);
      setEdges(reactFlowNodes.edges);
    }

  }, [items, createReactFlowNodes, setEdges, setNodes]);


  return (
    <>
      <Grid container spacing={2}>
        <Grid item xs={12} height={720} width={900}>
          <ReactFlow
            fitView
            nodes={nodes}
            edges={edges}
            nodeTypes={nodeTypes}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}>
            <Background />
          </ReactFlow>
        </Grid>
      </Grid>
      <Box display="flex" justifyContent="space-between" m={5}>
        <Button
          onClick={undoAllChanges}
          variant="outlined"
          style={{ float: 'left' }}
          startIcon={<RestoreIcon/>}>
          Undo all changes
        </Button>
        <Button
          onClick={onExistEdit}
          variant="outlined"
          style={{ float: 'left' }}
          startIcon={<ArrowBackIcon/>}>
          Back to species edit
        </Button>
        { onSkipLengthWeightChange &&
            <Button
              onClick={onSkipLengthWeightChange}
              variant="outlined"
              style={{ width: '90%' }}
              startIcon={<SkipNextIcon />}>
              Skip changes
            </Button>
        }
        <Button
          variant="contained"
          style={{ float: 'right'}}
          onClick={saveChanges}
          startIcon={<SaveIcon/>}
        >
          Save Changes
        </Button>
      </Box>
    </>
  );
};

FamilyTree.propTypes = {
  items: PropTypes.object,
  focusNodeId: PropTypes.number,
  onSkipLengthWeightChange: PropTypes.func,
  onSaveLengthWeightChange: PropTypes.func,
  onExistEdit: PropTypes.func,
};

export default FamilyTree;