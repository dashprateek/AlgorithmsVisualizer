package com.algo.export;

import com.algo.structure.Structure;
import com.algo.structure.Vertex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GraphTraversalExport {
    private final Document doc;
    private final Element iterations;
    private final Element root;

    public GraphTraversalExport(String algorithm, Structure structure) throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        root = doc.createElement("GraphTraversal");
        root.setAttribute("algorithm", algorithm);
        iterations = doc.createElement("iterations");
        Element snode = doc.createElement("graphml");
        snode.appendChild(structure.toSimpleXml(doc));
        root.appendChild(snode);
    }


    public void writeToStream(String filename) throws Exception {
        root.appendChild(iterations);
        doc.appendChild(root);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult stream = new StreamResult(filename);
        transformer.transform(new DOMSource(doc), stream);
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
