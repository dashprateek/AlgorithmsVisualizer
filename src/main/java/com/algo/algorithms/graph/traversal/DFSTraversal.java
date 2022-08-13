/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.algorithms.graph.traversal;

import com.algo.algorithms.graph.GraphAlgorithmsRunner;
import com.algo.annotations.AlgorithmDescription;
import com.algo.export.GraphTraversalExport;
import com.algo.structure.DirectedGraph;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@AlgorithmDescription(
        name = "Graph traversal",
        text = "The process of visiting (checking and/or updating) each vertex in a graph.",
        url = "https://en.wikipedia.org/wiki/Graph_traversal"
)
public class DFSTraversal extends GraphAlgorithmsRunner {

    private static List<Vertex> getNeighbours(Vertex v, boolean directed) {
        return (directed ? v.getOutgoingNeighbours() : v.getNeighbours())
                .stream()
                .distinct()
                .sorted(Comparator.comparingInt(Vertex::getId))
                .collect(Collectors.toList());
    }

    public static void run(Structure s, Vertex root, String fileName) throws Exception {
        boolean[] visited = new boolean[s.getVertices().size()];
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.add(root);
        visited[root.getId()] = true;
        GraphTraversalExport graphTraversalExport = new GraphTraversalExport("dfs", s);
        DFSHelper(s, root, visited, graphTraversalExport, new Stack<>());
        graphTraversalExport.writeToStream(fileName);
    }

    private static void DFSHelper(Structure s, Vertex v, boolean[] visited, GraphTraversalExport exporter, Stack<Vertex> stack) throws Exception {
        visited[v.getId()] = true;
        stack.push(v);
        exporter.appendChild(visited, stack);
        List<Vertex> outgoingNeighbours = getNeighbours(v, s instanceof DirectedGraph);
        for (Vertex oVertex : outgoingNeighbours) {
            if (!visited[oVertex.getId()]) {
                DFSHelper(s, oVertex, visited, exporter, stack);
            }
        }
        if (!stack.empty())
            stack.pop();
    }

    @Override
    public boolean run(Scanner sc) {
        try {
            Structure structure = getGraph(sc);
            String outputFile = getOutputFile(sc);
            Vertex source = getSourceVertex(sc, structure);
            run(structure, source, outputFile);
            transform(sc, outputFile, true);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}