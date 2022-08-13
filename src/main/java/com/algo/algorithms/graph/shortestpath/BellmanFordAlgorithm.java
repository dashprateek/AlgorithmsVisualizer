package com.algo.algorithms.graph.shortestpath;

import com.algo.algorithms.graph.GraphAlgorithmsRunner;
import com.algo.export.ShortestPathIterationsExport;
import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.DirectedGraph;
import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BellmanFordAlgorithm extends GraphAlgorithmsRunner {
    public static final double INF = 99999;

    public static void run(Structure<Vertex, Edge> s, Vertex source, String fileName) throws Exception {
        SingleSourceShortestPathModel singleSourceShortestPath = new SingleSourceShortestPathModel();
        int V = s.getVertices().size(), E = s.getEdges().size();
        double[][] M = new double[V][V];
        List<Vertex> vertexList = new ArrayList<>(s.getVertices());
        vertexList.remove(source);
        Collections.sort(vertexList, Comparator.comparingInt(Vertex::getId));
        HashMap<Vertex, Double> distances = new HashMap<>();
        HashMap<Integer, Integer> predecessor = new HashMap<>();
        distances.put(source, 0.);
        singleSourceShortestPath.setVertexList(vertexList.stream()
                .map(Vertex::getId)
                .collect(Collectors.toList()));
        for (Vertex v : vertexList) {
            M[0][v.getId()] = INF;
        }
        for (int i = 1; i < V; i++) {
            for (Vertex v : vertexList) {
                if (M[i][v.getId()] != M[i - 1][v.getId()]) {
                    M[i][v.getId()] = M[i - 1][v.getId()];
                    distances.put(v, M[i][v.getId()]);
                }
            }
            for (Edge edge : s.getEdges()) {
                Vertex v = edge.getSource(), u = edge.getTarget();
                if (M[i - 1][u.getId()] != INF && M[i][v.getId()] > M[i - 1][u.getId()] + edge.weight) {
                    M[i][v.getId()] = M[i - 1][u.getId()] + edge.weight;
                    predecessor.put(v.getId(), u.getId());
                    distances.put(v, M[i][v.getId()]);
                }
//                export.appendChild();
            }
            List<Double> dist = vertexList.stream()
                    .map(v -> distances.getOrDefault(v, -1.))
                    .collect(Collectors.toList());
            System.out.println(distances);
            singleSourceShortestPath.add(i, dist, new HashMap<>(predecessor));
        }

        System.out.println(Arrays.deepToString(M));
//        export.appendChild(distance, successor);
        ShortestPathIterationsExport export = new ShortestPathIterationsExport(singleSourceShortestPath, s);
        export.writeToStream(fileName);
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
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static class Export {
        private final Document doc;
        private final Element root;
        private final Element iterations;
        private final Function<Double, String> distanceMapper = n -> {
            if (n.equals(INF)) {
                return "\\oo";
            } else {
                return n.toString();
            }
        };
        private final Function<Integer, String> intermediateVertexMapper = n -> {
            if (Objects.isNull(n)) {
                return "~";
            } else {
                return n.toString();
            }
        };


        public Export(Structure structure) throws Exception {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            root = doc.createElement("ShortestPath");
            iterations = doc.createElement("iterations");

            Element snode = doc.createElement("graphml");
            snode.appendChild(structure.toSimpleXml(doc));
            root.appendChild(snode);
        }

        public void writeToStream(String filename) throws Exception {
            root.appendChild(iterations);
            doc.appendChild(root);
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            Transformer transformer = (new net.sf.saxon.TransformerFactoryImpl()).newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult stream = new StreamResult(filename);
            transformer.transform(new DOMSource(doc), stream);
        }

        public void appendUpdates(Double[] distances, Integer[] successor) throws Exception {
            Element iteration = doc.createElement("iteration");
            Element dNode = doc.createElement("distances");
            dNode.setTextContent(Arrays.stream(distances)
                    .map(distanceMapper)
                    .collect(Collectors.joining(",")));
            iteration.appendChild(dNode);
            Element iNode = doc.createElement("intermediateVertex");
            iNode.setTextContent(Arrays.stream(successor)
                    .map(intermediateVertexMapper)
                    .collect(Collectors.joining(",")));
            iteration.appendChild(iNode);
            iterations.appendChild(iteration);
        }


    }
}
//package com.algo.algorithms.graph.shortestpath;
//
//        import com.algo.structure.Edge;
//        import com.algo.structure.Structure;
//        import com.algo.structure.Vertex;
//        import org.w3c.dom.Document;
//        import org.w3c.dom.Element;
//
//        import javax.xml.parsers.DocumentBuilderFactory;
//        import javax.xml.transform.OutputKeys;
//        import javax.xml.transform.Transformer;
//        import javax.xml.transform.dom.DOMSource;
//        import javax.xml.transform.stream.StreamResult;
//        import java.util.ArrayList;
//        import java.util.Arrays;
//        import java.util.List;
//        import java.util.Objects;
//        import java.util.function.Function;
//        import java.util.stream.Collectors;
//
//public class BellmanFordAlgorithm {
//    public static final double INF = 99999;
//
//    public static void run(Structure<Vertex, Edge> s, Vertex source, String fileName) throws Exception {
//        int V = s.getVertices().size(), E = s.getEdges().size();
//        List<Vertex> vertexList = new ArrayList<>(s.getVertices());
//        double M[][] = new M[V][V];
//        vertexList.remove(source);
//        // Step 1: Initialize distances from src to all other
//        // vertices as INFINITE
//        for (Vertex v: vertexList) {
//            M[0][v.getId()] = INF;
//        }
////        List<Double[]> list = new ArrayList<>();
////        Double[] distance = new Double[V];
////        Integer[] successor = new Integer[V];
////        for (int i = 0; i < V; ++i) {
////            distance[i] = INF;
////        }
////        distance[source.getId()] = 0.;
////        Export export = new Export(s);
//        // Step 2: Relax all edges |V| - 1 times. A simple
//        // shortest path from src to any other vertex can
//        // have at-most |V| - 1 edges
//        List<Edge> edges = new ArrayList<>(s.getEdges());
//        for (int i = 1; i < V; ++i) {
//            boolean noChange = true;
//            for (int j = 0; j < E; ++j) {
//                int u = edges.get(j).getSource().getId();
//                int v = edges.get(j).getTarget().getId();
//                Double weight = edges.get(j).weight;
//                if (distance[u] != INF && distance[u] + weight < distance[v]) {
//                    distance[v] = distance[u] + weight;
//                    successor[v] = u;
//                    noChange = false;
//                }
//            }
//            list.add(distance.clone());
////            export.appendChild(distance, successor);
//            if (noChange)
//                break;
//        }
//        list.stream().map(Arrays::toString).forEach(System.out::println);
//        export.appendChild(distance, successor);
//        export.writeToStream(fileName);
//    }
//
//
//    public static class Export {
//        private final Document doc;
//        private final Element root;
//        private final Element iterations;
//        private final Function<Double, String> distanceMapper = n -> {
//            if (n.equals(INF)) {
//                return "\\oo";
//            } else {
//                return n.toString();
//            }
//        };
//        private final Function<Integer, String> intermediateVertexMapper = n -> {
//            if (Objects.isNull(n)) {
//                return "~";
//            } else {
//                return n.toString();
//            }
//        };
//
//
//        public Export(Structure structure) throws Exception {
//            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
//            root = doc.createElement("ShortestPath");
//            iterations = doc.createElement("iterations");
//
//            Element snode = doc.createElement("graphml");
//            snode.appendChild(structure.toSimpleXml(doc));
//            root.appendChild(snode);
//        }
//
//        public void writeToStream(String filename) throws Exception {
//            root.appendChild(iterations);
//            doc.appendChild(root);
////            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            Transformer transformer = (new net.sf.saxon.TransformerFactoryImpl()).newTransformer();
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            StreamResult stream = new StreamResult(filename);
//            transformer.transform(new DOMSource(doc), stream);
//        }
//
//        public void appendChild(Double[] distances, Integer[] successor) throws Exception {
//            Element iteration = doc.createElement("iteration");
//            Element dNode = doc.createElement("distances");
//            dNode.setTextContent(Arrays.stream(distances)
//                    .map(distanceMapper)
//                    .collect(Collectors.joining(",")));
//            iteration.appendChild(dNode);
//            Element iNode = doc.createElement("intermediateVertex");
//            iNode.setTextContent(Arrays.stream(successor)
//                    .map(intermediateVertexMapper)
//                    .collect(Collectors.joining(",")));
//            iteration.appendChild(iNode);
//            iterations.appendChild(iteration);
//        }
//
//
//    }
//}
