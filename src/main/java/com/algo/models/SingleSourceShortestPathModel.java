package com.algo.models;

import com.algo.annotations.StructureDescription;
import com.algo.plugins.XmlMarshallable;
import com.algo.plugins.XmlName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@StructureDescription(
        name = "Directed Graph",
        text = "",
        url = "https://en.wikipedia.org/wiki/Shortest_path_problem")
@XmlName(name = "SingleSourceShortestPath")
public class SingleSourceShortestPathModel extends XmlMarshallable {
    private List<ShortestPathEntry> shortestPaths = new ArrayList<>();
    private List<Integer> vertexList;

    public void add(int v, List<Double> distances, Map<Integer, Integer> predecessor) {
        shortestPaths.add(ShortestPathEntry.valueOf(v, distances, predecessor));
    }

    public List<ShortestPathEntry> getShortestPaths() {
        return shortestPaths;
    }

    public void setShortestPaths(List<ShortestPathEntry> shortestPaths) {
        this.shortestPaths = shortestPaths;
    }

    public List<Integer> getVertexList() {
        return vertexList;
    }

    public void setVertexList(List<Integer> vertexList) {
        this.vertexList = vertexList;
    }

    public void writeToFile(String filename) throws Exception {
        writeToStream(new StreamResult(filename));
    }

    public void writeToStream(StreamResult stream) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = toXml(doc);
        if (root == null)
            throw new Exception("Error writing to XML");
        doc.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, stream);
    }

    @Override
    public Element toXml(Document doc) throws Exception {
        Element snode = super.toXml(doc);

        //Ordered Vertex id list
        List<Integer> ids = getVertexList();
        Element vertices = doc.createElement("vertices");
        ids.forEach(id -> {
            Element vertex = doc.createElement("vertex");
            vertex.setAttribute("id", String.valueOf(id));
            vertices.appendChild(vertex);
        });
        vertices.setAttribute("size", String.valueOf(ids.size()));
        snode.appendChild(vertices);

        Element iterations = doc.createElement("iterations");
        for (ShortestPathEntry path : getShortestPaths()) {
            iterations.appendChild(path.toXml(doc));
        }
        snode.appendChild(iterations);
        return snode;
    }

    @XmlName(name = "shortestPath")
    private static class ShortestPathEntry extends XmlMarshallable {
        private Integer queuedElement;
        private List<Double> distances;
        private Map<Integer, Integer> predecessor;

        public ShortestPathEntry(Integer queuedElement, List<Double> distance, Map<Integer, Integer> predecessor) {
            this.queuedElement = queuedElement;
            this.distances = distance;
            this.predecessor = predecessor;
        }

        public ShortestPathEntry() {
        }

        private static ShortestPathEntry valueOf(Integer queuedElement, List<Double> distance, Map<Integer, Integer> predecessor) {
            return new ShortestPathEntry(queuedElement, distance, predecessor);
        }

        public Integer getQueuedElement() {
            return queuedElement;
        }

        public void setQueuedElement(Integer queuedElement) {
            this.queuedElement = queuedElement;
        }

        public List<Double> getDistances() {
            return distances;
        }

        public void setDistances(List<Double> distances) {
            this.distances = distances;
        }

        public Map<Integer, Integer> getPredecessor() {
            return predecessor;
        }

        public void setPredecessor(Map<Integer, Integer> predecessor) {
            this.predecessor = predecessor;
        }

        @Override
        public Element toXml(Document doc) throws Exception {
            Element sp = super.toXml(doc);
            sp.setAttribute("queuedElement", getQueuedElement().toString());
            List<Double> distances = getDistances();
            Map<Integer, Integer> predecessor = getPredecessor();
            for (int i = 0; i < distances.size(); i++) {
                Element path = doc.createElement("path");
                String distance = Optional.ofNullable(distances.get(i))
//                        .filter(dist -> dist >= 0)
                        .map(Object::toString)
                        .orElse("\\infty");
                System.out.println("distance:"+distance);
                path.setAttribute("distance", distance);
                if (predecessor.containsKey(i))
                    path.setAttribute("parent", predecessor.get(i).toString());
                sp.appendChild(path);
            }
            return sp;
        }
    }
}
