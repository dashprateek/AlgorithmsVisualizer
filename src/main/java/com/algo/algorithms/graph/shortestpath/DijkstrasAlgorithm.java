/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.algorithms.graph.shortestpath;

import com.algo.algorithms.graph.GraphAlgorithmsRunner;
import com.algo.annotations.AlgorithmDescription;
import com.algo.export.ShortestPathIterationsExport;
import com.algo.models.SingleSourceShortestPathModel;
import com.algo.rendering.GralogTikStyles;
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
public class DijkstrasAlgorithm extends GraphAlgorithmsRunner {

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
        List<Vertex> vList = Stream.concat(Stream.of(start),
                        new ArrayList<Vertex>(s.getVertices()).stream().filter(e -> !e.equals(start))
                                .sorted(Comparator.comparingInt(Vertex::getId)))
                .collect(Collectors.toList());
        singleSourceShortestPath.setVertexList(vList.stream().map(Vertex::getId).collect(Collectors.toList()));
        while (!next.isEmpty()) {
            ShortestPathTuple tuple = next.poll();
            Vertex source = tuple.getNode();
            source.style = GralogTikStyles.SELECTED_NODE_STYLE;
            source.distanceFromSource = tuple.getDistance().toString();
            if (Objects.nonNull(tuple.edge))
                tuple.edge.style = GralogTikStyles.SELECTED_EDGE_STYLE;
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
            List<Double> dist = vList.stream()
                    .map(v -> distances.getOrDefault(v, -1.))
                    .collect(Collectors.toList());
            singleSourceShortestPath.add(source.getId(), dist, new HashMap<>(predecessor));
//            graphIterationsExport.appendChild(s);
        }
//        graphIterationsExport.writeToStream(fileName);
        ShortestPathIterationsExport export = new ShortestPathIterationsExport(singleSourceShortestPath, s);
        export.writeToStream(fileName);
        return singleSourceShortestPath;
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