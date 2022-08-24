/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.algorithms.graph.shortestpath;

import com.algo.annotations.AlgorithmDescription;
import com.algo.export.SingleSourceShortestPathExport;
import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.DirectedGraph;
import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@AlgorithmDescription(
        name = "Shortest Path",
        text = "Finds a shortest path between from a starting vertice, using Dijkstra's Algorithm",
        url = "https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm"
)
public class DijkstrasAlgorithm extends ShortestPathRunner {

    @Override
    public boolean run(Scanner sc) {
        try {
            DirectedGraph structure = (DirectedGraph) getGraph(sc);
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

    public static SingleSourceShortestPathModel run(Structure s, Vertex start, String fileName) throws Exception {
        SingleSourceShortestPathModel singleSourceShortestPath = new SingleSourceShortestPathModel();
        HashSet<Vertex> visited = new HashSet<>();
        HashMap<Vertex, Double> distances = new HashMap<>();
        HashMap<Integer, Integer> predecessor = new HashMap<>();
        distances.put(start, 0.);
        PriorityQueue<ShortestPathTuple> next = new PriorityQueue<>(Comparator.comparingDouble(ShortestPathTuple::getDistance));
        next.add(ShortestPathTuple.valueOf(start, 0., null));
        List<Vertex> vList = getVertexList(s, start);
        singleSourceShortestPath.setVertexList(vList.stream().map(Vertex::getId).collect(Collectors.toList()));
        while (!next.isEmpty()) {
            ShortestPathTuple tuple = next.poll();
            Vertex source = tuple.getNode();
            source.distanceFromSource = tuple.getDistance().toString();
            if (visited.contains(source))
                continue;
            visited.add(source);
            if (distances.get(source) <= tuple.getDistance())
                distances.put(source, tuple.getDistance());
            for (Edge edge : source.getOutgoingEdges()) {
                Vertex target = edge.getTarget();
                if (!distances.containsKey(target) || distances.get(target) > edge.weight + distances.get(source)) {
                    distances.put(target, edge.weight + distances.get(source));
                    predecessor.put(target.getId(), source.getId());
                }
                next.add(ShortestPathTuple.valueOf(target, edge.weight + distances.get(source), edge));
            }
            List<Double> dist = getDistanceList(distances, vList);
            singleSourceShortestPath.add(source.getId(), dist, new HashMap<>(predecessor));
        }
        SingleSourceShortestPathExport export = new SingleSourceShortestPathExport(singleSourceShortestPath, s);
        export.writeToStream(fileName);
        return singleSourceShortestPath;
    }

    private static List<Double> getDistanceList(HashMap<Vertex, Double> distances, List<Vertex> vList) {
        return vList.stream()
                .map(v -> distances.getOrDefault(v, BellmanFordAlgorithm.INF))
                .collect(Collectors.toList());
    }

    private static List<Vertex> getVertexList(Structure s, Vertex start) {
        return Stream.concat(Stream.of(start),
                        new ArrayList<Vertex>(s.getVertices()).stream().filter(e -> !e.equals(start))
                                .sorted(Comparator.comparingInt(Vertex::getId)))
                .collect(Collectors.toList());
    }

    private static class ShortestPathTuple {
        private final Vertex node;
        private final Double distance;
        private final Edge edge;

        private ShortestPathTuple(Vertex node, Double distance, Edge edge) {
            this.node = node;
            this.distance = distance;
            this.edge = edge;
        }

        private static ShortestPathTuple valueOf(Vertex node, Double distance, Edge edge) {
            return new ShortestPathTuple(node, distance, edge);
        }

        public Vertex getNode() {
            return node;
        }

        public Double getDistance() {
            return distance;
        }

        public Edge getEdge() {
            return edge;
        }
    }

}