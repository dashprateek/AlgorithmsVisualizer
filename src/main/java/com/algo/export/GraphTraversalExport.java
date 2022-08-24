package com.algo.export;

import com.algo.structure.Structure;
import com.algo.structure.Vertex;
import org.w3c.dom.Element;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphTraversalExport extends IterationsExport {

    public GraphTraversalExport(String algorithm, Structure structure) throws Exception {
        super("GraphTraversal");
        root.setAttribute("algorithm", algorithm);
        Element node = doc.createElement("graphml");
        node.appendChild(structure.toSimpleXml(doc));
        root.appendChild(node);
    }

    public void appendChild(boolean[] visited, Collection<Vertex> collection) throws Exception {
        Element iteration = doc.createElement("iteration");
        iteration.setAttribute("list", collection.stream().map(Vertex::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        iteration.setAttribute("visited", IntStream.range(0, visited.length)
                .filter(i -> visited[i])
                .mapToObj(String::valueOf)
                .collect(Collectors.joining("|", "|", "|")));
        iterations.appendChild(iteration);
    }
}
