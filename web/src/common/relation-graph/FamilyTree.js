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

  if(direction === 'up' && (isCascade || depth ===0)) {
    nodes
      .filter(f => f.id === currentSpeciesId)
      .map(m => m.data.getParentId())
      .forEach(k => {console.log(k); ids.push(...getAllTargetId(nodes, k, direction, isCascade, depth + 1));});
  }
  else if(direction === 'down' && (isCascade || depth === 0)){
    nodes
      .filter(f => f.id === currentSpeciesId)
      .map(m => m.data.getChildrenId())
      .forEach(k => ids.push(...getAllTargetId(nodes, k, direction, isCascade, depth + 1)));
  }

  return ids;
};

const FamilyTree = ({ items, focusNodeId }) => {
  // const defaultViewport = { x: 0, y: 0, zoom: 0.8 };
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const onConnect = useCallback((params) => setEdges((eds) => addEdge(params, eds)), [setEdges]);

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
          label: `${value.self.observableItemName}`,
          lengthWeightA: value.self.lengthWeightA === null ? '' : value.self.lengthWeightA,
          lengthWeightB: value.self.lengthWeightB === null ? '' : value.self.lengthWeightB,
          lengthWeightCf: value.self.lengthWeightCf === null ? '' : value.self.lengthWeightCf,

          onUpdateParent: (id, isCascade) => onUpdateSpecies(
            '' + id, {
                          a: value.self.lengthWeightA,
                          b: value.self.lengthWeightB,
                          cf: value.self.lengthWeightCf
                      }, 'up', isCascade),

          onUpdateChildren: (id, isCascade) => onUpdateSpecies(
            '' + id, {
                          a: value.self.lengthWeightA,
                          b: value.self.lengthWeightB,
                          cf: value.self.lengthWeightCf
                      }, 'down', isCascade),

          hasParent: () => value.parent === null,
          hasChildren: () => value.children === null || value.children.length === 0,

          getParentId: () => value.parent === null ? null : '' + value.parent.observableItemId,
          getChildrenId: () => value.children === null ? null  : value.children.flatMap(m => '' + m.self.observableItemId)
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
  items: PropTypes.object,
  focusNodeId: PropTypes.number,
};

export default FamilyTree;