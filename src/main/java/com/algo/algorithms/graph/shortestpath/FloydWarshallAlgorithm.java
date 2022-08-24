package com.algo.algorithms.graph.shortestpath;

import com.algo.export.AllPairShortestPathExport;
import com.algo.structure.DirectedGraph;
import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;

import java.util.Objects;
import java.util.Scanner;

public class FloydWarshallAlgorithm extends ShortestPathRunner {
    public static final int INF = 99999;

    public static void run(Structure<Vertex, Edge> s, String fileName) throws Exception {
        int numberOfVertices = s.getVertices().size();
        Integer[][] distance = new Integer[numberOfVertices][numberOfVertices];
        Integer[][] intermediateVertices = new Integer[numberOfVertices][numberOfVertices];
        for (Edge e : s.getEdges()) {
            distance[e.getSource().getId()][e.getTarget().getId()] = e.weight.intValue();
            intermediateVertices[e.getSource().getId()][e.getTarget().getId()] = 0;
        }
        AllPairShortestPathExport allPairShortestPathExport = new AllPairShortestPathExport(s);
        for (int k = 0; k < numberOfVertices; k++) {
            for (int i = 0; i < numberOfVertices; i++) {
                for (int j = 0; j < numberOfVertices; j++) {
                    if (i == j)
                        distance[i][j] = 0;
                    if (nullCheck(distance, k, i, j))
                        continue;
                    if (distance[i][j] > distance[i][k] + distance[k][j]) {
                        distance[i][j] = distance[i][k] + distance[k][j];
                        intermediateVertices[i][j] = k + 1;
                    }
                }
            }
            allPairShortestPathExport.appendChild(distance, intermediateVertices, k);
        }
        allPairShortestPathExport.writeToStream(fileName);
    }

    private static boolean nullCheck(Integer[][] distance, int k, int i, int j) {
        if (Objects.isNull(distance[i][j]))
            distance[i][j] = INF;
        if (Objects.isNull(distance[k][j]))
            distance[k][j] = INF;
        if (Objects.isNull(distance[i][k]))
            distance[i][k] = INF;
        return distance[k][j] == INF || distance[i][k] == INF;
    }

    @Override
    public boolean run(Scanner sc) throws Exception {
        DirectedGraph structure = (DirectedGraph) getGraph(sc);
        String outputFile = getOutputFile(sc);
        run(structure, outputFile);
        transform(sc, outputFile, true);
        return false;

    }

}
