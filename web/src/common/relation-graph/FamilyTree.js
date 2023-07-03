import React, { useCallback, useEffect } from 'react';
import 'reactflow/dist/style.css';
import SpeciesNodeForLengthWeight from './SpeciesNodeForLengthWeight';

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

const FamilyTree = (props) => {
  // const defaultViewport = { x: 0, y: 0, zoom: 0.8 };
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

  const createReactFlowNodes = useCallback((nodes, value, depth = 0) => {
    if(value != null) {
      nodes.nodes.push({
        id: '' + value.self.observableItemId,
        type: 'customTreeNode',
        data: {
          id: '' + value.self.observableItemId,
          depth: depth,
          hasParent: value.parent === null,
          hasChildren: value.children === null || value.children.length === 0,
          isFocus: value.self.observableItemId === props.focusNodeId,
          nodeHeight: nodeHeight,
          nodeWidth: nodeWidth,
          label: `${value.self.observableItemName}`,
          lengthWeightA: value.self.lengthWeightA === null ? '' : value.self.lengthWeightA,
          lengthWeightB: value.self.lengthWeightB === null ? '' : value.self.lengthWeightB,
          lengthWeightCf: value.self.lengthWeightCf === null ? '' : value.self.lengthWeightCf,
          reload: props.reload
        },
        position: { x: 0, y: 0 }
      });

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
  }, [props]);

  useEffect(() => {
    const reactFlowNodes = {
      nodes: [],
      edges: []
    };

    // Create the node data for react flow
    createReactFlowNodes(reactFlowNodes, props.nodes);

    // No need to update if no nodes there
    if(reactFlowNodes.nodes.length != 0) {
      recalculateNodeXY(reactFlowNodes.nodes);
      setNodes(reactFlowNodes.nodes);
      setEdges(reactFlowNodes.edges);
    }

  }, [props.nodes, setNodes, setEdges, createReactFlowNodes]);

  return (
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
  );
};

FamilyTree.propTypes = {
  nodes: PropTypes.object,
  focusNodeId: PropTypes.number,
  reload: PropTypes.func,
};

export default FamilyTree;