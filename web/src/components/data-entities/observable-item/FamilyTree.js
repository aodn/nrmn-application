import React, { useCallback, useEffect } from 'react';
import 'reactflow/dist/style.css';

import ReactFlow, {
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
} from 'reactflow';

import 'reactflow/dist/style.css';
import {PropTypes} from 'prop-types';

const FamilyTree = (props) => {
  const defaultViewport = { x: 0, y: 0, zoom: 1.5 };
  const [nodes, setNodes, onNodesChange] = useNodesState(props.nodes.nodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState(props.nodes.edges);

  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

  useEffect(() => {
    const nodes = props.nodes;

    setNodes(nodes.nodes);
    setEdges(nodes.edges);

  }, [props.nodes]);

  return (
    <ReactFlow
      fitView
      defaultViewport={defaultViewport}
      nodes={nodes}
      edges={edges}
      onNodesChange={onNodesChange}
      onEdgesChange={onEdgesChange}
      onConnect={onConnect}>
      <Background />
    </ReactFlow>
  );
};

FamilyTree.propTypes = {
  nodes: PropTypes.object.isRequired
};

export default FamilyTree;