package com.algo.algorithms.graph.shortestpath;

import com.algo.export.SingleSourceShortestPathExport;
import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.DirectedGraph;
import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;

import java.util.*;
import java.util.stream.Collectors;

public class BellmanFordAlgorithm extends ShortestPathRunner {
    public static final double INF = 99999;

    public static void run(Structure<Vertex, Edge> s, Vertex source, String fileName) throws Exception {
        SingleSourceShortestPathModel singleSourceShortestPath = new SingleSourceShortestPathModel();
        int V = s.getVertices().size();
        double[][] distanceMatrix = new double[V][V];
        List<Vertex> vertexList = getVertexList(s, source);
        HashMap<Vertex, Double> distances = new HashMap<>();
        HashMap<Integer, Integer> predecessor = new HashMap<>();
        distances.put(source, 0.);
        singleSourceShortestPath.setVertexList(getVertexIdList(vertexList));
        for (Vertex v : vertexList)
            distanceMatrix[0][v.getId()] = INF;
        for (int i = 1; i < V; i++) {
            for (Vertex v : vertexList) {
                if (distanceMatrix[i][v.getId()] != distanceMatrix[i - 1][v.getId()]) {
                    distanceMatrix[i][v.getId()] = distanceMatrix[i - 1][v.getId()];
                    distances.put(v, distanceMatrix[i][v.getId()]);
                }
            }
            for (Edge edge : s.getEdges()) {
                Vertex v = edge.getSource(), u = edge.getTarget();
                if (distanceMatrix[i - 1][u.getId()] != INF && distanceMatrix[i][v.getId()] > distanceMatrix[i - 1][u.getId()] + edge.weight) {
                    distanceMatrix[i][v.getId()] = distanceMatrix[i - 1][u.getId()] + edge.weight;
                    predecessor.put(v.getId(), u.getId());
                    distances.put(v, distanceMatrix[i][v.getId()]);
                }
            }
            List<Double> dist = getDistancesFromSource(vertexList, distances);
            singleSourceShortestPath.add(i, dist, new HashMap<>(predecessor));
        }
        SingleSourceShortestPathExport export = new SingleSourceShortestPathExport(singleSourceShortestPath, s);
        export.writeToStream(fileName);
    }

    private static List<Double> getDistancesFromSource(List<Vertex> vertexList, HashMap<Vertex, Double> distances) {
        return vertexList.stream()
                .map(v -> distances.getOrDefault(v, -1.))
                .collect(Collectors.toList());
    }

    private static List<Integer> getVertexIdList(List<Vertex> vertexList) {
        return vertexList.stream()
                .map(Vertex::getId)
                .collect(Collectors.toList());
    }

    private static List<Vertex> getVertexList(Structure<Vertex, Edge> s, Vertex source) {
        List<Vertex> vertexList = new ArrayList<>(s.getVertices());
        vertexList.remove(source);
        vertexList.sort(Comparator.comparingInt(Vertex::getId));
        return vertexList;
    }

    @Override
    public boolean run(Scanner sc) {
        try {
            DirectedGraph structure = (DirectedGraph) getGraph(sc);
            String outputFile = getOutputFile(sc);
            Vertex source = getSourceVertex(sc, structure);
            run(structure, source, outputFile);
            transform(sc, outputFile, true);
            return false;
        }  catch (Exception e) {
            System.out.println("The execution of the algorithm terminates with the following stack trace:");
            e.printStackTrace();
            System.out.println("Please try again");
            return false;
        }
    }

}
