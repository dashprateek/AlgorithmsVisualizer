package com.algo.export;

import com.algo.algorithms.graph.shortestpath.FloydWarshallAlgorithm;
import com.algo.structure.Structure;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllPairShortestPathExport extends IterationsExport {

    private final Function<Integer, String> distanceMapper = n -> {
        if (n.equals(FloydWarshallAlgorithm.INF)) {
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


    public AllPairShortestPathExport(Structure structure) throws Exception {
        super("ShortestPath");
        Element graphNode = doc.createElement("graphml");
        graphNode.appendChild(structure.toSimpleXml(doc));
        root.appendChild(graphNode);
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
