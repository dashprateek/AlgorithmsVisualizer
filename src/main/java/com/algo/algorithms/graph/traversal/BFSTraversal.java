/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.algorithms.graph.traversal;

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
public class BFSTraversal extends GraphTraversalRunner {

    public static void run(Structure s, Vertex root, String fileName) throws Exception {
        boolean[] visited = new boolean[s.getVertices().size()];
        LinkedList<Vertex> queue = new LinkedList<>();
        queue.add(root);
        visited[root.getId()] = true;
        GraphTraversalExport graphTraversalExport = new GraphTraversalExport("bfs", s);
        graphTraversalExport.appendChild(visited, queue);
        while (!queue.isEmpty()) {
            Vertex v = queue.poll();
            List<Vertex> outgoingNeighbours = getNeighbours(v, s instanceof DirectedGraph);
            for (Vertex oVertex : outgoingNeighbours) {
                if (!visited[oVertex.getId()]) {
                    visited[oVertex.getId()] = true;
                    queue.add(oVertex);
                    graphTraversalExport.appendChild(visited, queue);
                }
            }
        }
        graphTraversalExport.writeToStream(fileName);
    }

    private static List<Vertex> getNeighbours(Vertex v, boolean directed) {
        return (directed ? v.getOutgoingNeighbours() : v.getNeighbours())
                .stream()
                .distinct()
                .sorted(Comparator.comparingInt(Vertex::getId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean run(Scanner sc) throws Exception {
            Structure structure = getGraph(sc);
            String outputFile = getOutputFile(sc);
            Vertex source = getSourceVertex(sc, structure);
            run(structure, source, outputFile);
            transform(sc, outputFile, true);
            return false;
    }

}