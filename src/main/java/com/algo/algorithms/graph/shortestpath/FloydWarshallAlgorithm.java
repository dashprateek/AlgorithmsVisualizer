package com.algo.algorithms.graph.shortestpath;

import com.algo.algorithms.graph.GraphAlgorithmsRunner;
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
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FloydWarshallAlgorithm extends GraphAlgorithmsRunner {
    public static final int INF = 99999;

    public static void run(Structure<Vertex, Edge> s, String fileName) throws Exception {
        int numberOfVertices = s.getVertices().size();
        Integer[][] distance = new Integer[numberOfVertices][numberOfVertices];
        Integer[][] intermediateVertices = new Integer[numberOfVertices][numberOfVertices];
        for (Edge e : s.getEdges()) {
            distance[e.getSource().getId()][e.getTarget().getId()] = e.weight.intValue();
            intermediateVertices[e.getSource().getId()][e.getTarget().getId()] = 0;
        }
        Export export = new Export(s);
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
            export.appendChild(distance, intermediateVertices, k);
        }
        export.writeToStream(fileName);
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
    public boolean run(Scanner sc) {
        try {
            DirectedGraph structure = (DirectedGraph) getGraph(sc);
            String outputFile = getOutputFile(sc);
            run(structure, outputFile);
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
        private final Function<Integer, String> distanceMapper = n -> {
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

        public void appendChild(Integer[][] distances, Integer[][] intermediate, int k) throws Exception {
            Element iteration = doc.createElement("iteration");
            Element dNode = doc.createElement("distances");
            dNode.setTextContent(Arrays.stream(distances).map(arr -> Arrays.stream(arr)
                            .map(distanceMapper)
                            .collect(Collectors.joining(",")))
                    .collect(Collectors.joining("|")));
            iteration.appendChild(dNode);
            Element iNode = doc.createElement("intermediateVertex");
            iNode.setTextContent(Arrays.stream(intermediate).map(arr -> Arrays.stream(arr)
                            .map(intermediateVertexMapper)
                            .collect(Collectors.joining(",")))
                    .collect(Collectors.joining("|")));
            iteration.appendChild(iNode);
            iterations.appendChild(iteration);
        }


    }
}
