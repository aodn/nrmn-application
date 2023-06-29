import React, { useCallback, useEffect, useMemo } from 'react';
import 'reactflow/dist/style.css';
import SpeciesNode from './SpeciesNode';

import ReactFlow, {
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
} from 'reactflow';

import 'reactflow/dist/style.css';
import {PropTypes} from 'prop-types';

const nodeTypes = { customTreeNode: SpeciesNode };

const FamilyTree = (props) => {
  const defaultViewport = { x: 0, y: 0, zoom: 1 };
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);

  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

  const createReactFlowNodes = useCallback((nodes, value, depth = 0, childCount = 1, childIndex = 1) => {
    if(value != null) {
      nodes.nodes.push({
        id: '' + value.self.observableItemId,
        type: 'customTreeNode',
        data: {
          label: `${value.self.observableItemName}`,
          lengthWeightA: `${value.self.lengthWeightA}`,
          lengthWeightB: `${value.self.lengthWeightB}`,
          lengthWeightCf: `${value.self.lengthWeightCf}`
        },
        position: { x: 300 * (childIndex - childCount / 2), y: 450 * depth }
      });

      if(value.children != undefined) {
        let c = 1;
        value.children.forEach(i => {
          nodes.edges.push({
            id: `${value.self.observableItemId}-${i.self.observableItemId}`,
            source: '' + value.self.observableItemId,
            target: '' + i.self.observableItemId
          });

          createReactFlowNodes(nodes, i, depth + 1, value.children.length, c++);
        });
      }
    }
  }, []);

  useEffect(() => {
    const reactFlowNodes = {
      nodes: [],
      edges: []
    };

    createReactFlowNodes(reactFlowNodes, props.nodes);
    // No need to update if no nodes there
    if(reactFlowNodes.nodes.length != 0) {
      setNodes(reactFlowNodes.nodes);
      setEdges(reactFlowNodes.edges);
    }

  }, [props.nodes, setNodes, setEdges, createReactFlowNodes]);

  return (
    <ReactFlow
      fitView
      defaultViewport={defaultViewport}
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
  nodes: PropTypes.object
};

export default FamilyTree;