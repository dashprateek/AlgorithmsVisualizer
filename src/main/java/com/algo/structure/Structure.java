/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */
package com.algo.structure;

import com.algo.annotations.StructureDescription;
import com.algo.config.Configuration;
import com.algo.plugins.PluginManager;
import com.algo.plugins.XmlMarshallable;
import com.algo.plugins.XmlName;
import com.algo.rendering.Vector2D;
import com.algo.utility.DeepCopy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 *
 */

@XmlName(name = "graph")
public abstract class Structure<V extends Vertex, E extends Edge> extends XmlMarshallable {

    //    private final Set<StructureListener> listeners = new HashSet<>();
    public TreeSet<Interval> vertexIdHoles;
    public TreeSet<Interval> edgeIdHoles;
    protected HashMap<Integer, V> vertices;
    protected HashMap<Integer, E> edges;
    //File IO
    private boolean isOpenFile = false;
    private String structureFilePath = "";

    public Structure() {
        vertices = new HashMap<>();
        edges = new HashMap<>();
        vertexIdHoles = new TreeSet<>(new Comparator<>() {
            /**
             * Returns a positive value if number1 is larger than number 2, a
             * negative number if number1 is less than number2, and 0 if they
             * are equal.
             */
            public int compare(Interval first, Interval second) {
                return first.a - second.a;
            }
        });
        edgeIdHoles = new TreeSet<>(new Comparator<>() {
            /**
             * Returns a positive value if number1 is larger than number 2, a
             * negative number if number1 is less than number2, and 0 if they
             * are equal.
             */
            public int compare(Interval first, Interval second) {
                return first.a - second.a;
            }
        });
        vertexIdHoles.add(new Interval(0, Integer.MAX_VALUE));
        edgeIdHoles.add(new Interval(0, Integer.MAX_VALUE));
    }

    protected static String pythonifyClass(Class c) {
        if (c == String.class) {
            return "string";
        }
        if (c == Integer.class || c == int.class) {
            return "int";
        }
        if (c == Double.class || c == double.class) {
            return "float";
        }
        if (c == Boolean.class || c == boolean.class) {
            return "bool";
        }
        return "notAPythonifiableClass";
    }

    private static boolean rectContainsVector(Rectangle2D rect, Vector2D c) {
        return rect.contains(c.getX(), c.getY());
    }

    private static boolean checkContainsAnyX(Vector2D[] vectors, Rectangle2D rect) {
        for (int i = 0; i < vectors.length; i++) {
            if (vectors[i] != null) {
                if (rect.getMinX() < vectors[i].getX()
                        && rect.getMaxX() > vectors[i].getX()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean checkContainsAnyY(Vector2D[] vectors, Rectangle2D rect) {
        for (int i = 0; i < vectors.length; i++) {
            if (vectors[i] != null) {
                if (rect.getMinY() < vectors[i].getY()
                        && rect.getMaxY() > vectors[i].getY()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Structure loadFromFile(String fileName) throws Exception {
        return loadFromStream(new FileInputStream(fileName));
    }

    public static Structure loadFromStream(InputStream stream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        if (!root.getTagName().equalsIgnoreCase("graphml"))
            throw new Exception("Not a GraphML file");

        NodeList children = root.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; --i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element child = (Element) childNode;
            String className = child.getTagName();        // catch additional
            // tag name(should be type) = buechiautomat/automaton if existent
            if (child.hasAttribute("type")) {
                className = child.getAttribute("type");
            } else {
                if (child.hasAttribute("edgedefault")) {
                    if (child.getAttribute("edgedefault").equals("directed")) {
                        className = "digraph";
                    }
                }
            }
            Object result = PluginManager.instantiateClass(className);
            if (result == null)
                continue;
            if (result instanceof Structure) {
                ((Structure) result).fromXml(child);
                return (Structure) result;
            }
        }

        return null;
    }

    public boolean isEmpty() {
        return vertices.size() + edges.size() == 0;
    }

    /**
     * @return An unmodifiable set of vertices.
     */
    //public Set<V> getVertices() { return Collections.unmodifiableSet(vertices); }
    public Collection<V> getVertices() {
        return vertices.values();
    }

    /**
     * @return A pseudo-randommly selected vertrex.
     */
    public Edge getRandomEdge() {
        ArrayList<Edge> edges = new ArrayList<Edge>(this.getEdges());
        int randomEdgeIndex = ThreadLocalRandom.current().nextInt(0, edges.size());
        Edge edge = edges.get(randomEdgeIndex);
        return edge;
    }

    /**
     * @return A pseudo-randommly selected vertrex.
     */
    public Vertex getRandomVertex() {
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(this.getVertices());
        int randomVertexIndex = ThreadLocalRandom.current().nextInt(0, vertices.size());
        Vertex vertex = vertices.get(randomVertexIndex);
        return vertex;
    }

    public HashMap<Integer, V> getVerticesT() {
        return vertices;
    }

    /**
     * Overrides vertices. Only for internal use
     */
    public void setVerticesT(HashMap<Integer, V> verticesNew) {
        vertices = verticesNew;
    }

    public HashMap<Integer, E> getEdgesT() {
        return edges;
    }

    public void setEdgesT(HashMap<Integer, E> edgesNew) {
        edges = edgesNew;
    }

    /**
     * @return An unmodifiable set of edges.
     */
    public Set<E> getEdges() {
        return new HashSet<>(edges.values());
    }

    public void snapToGrid(double gridSize) {
        for (Edge e : getEdges())
            e.snapToGrid(gridSize);
        for (Vertex v : getVertices())
            v.snapToGrid(gridSize);
    }

    /**
     * @param dimension 0 for the x-dimension and 1 for the y-dimension.
     * @return The largest x or y value occurring in vertex/edges coordinates.
     */
    public double maximumCoordinate(int dimension) {
        double result = Double.NEGATIVE_INFINITY;
        for (Vertex v : getVertices())
            result = Math.max(result, v.maximumCoordinate(dimension));
        for (Edge e : getEdges())
            result = Math.max(result, e.maximumCoordinate(dimension));
        return result;
    }

    /**
     * Creates a new vertex instance without adding it to the structure.
     *
     * @return The new vertex.
     */
    public abstract V createVertex();

    /**
     * Creates a new vertex instance without adding it to the structure.
     *
     * @return The new vertex.
     */
    public abstract V createVertex(Configuration config);

    /**
     * Adds a vertex to the structure, and uses the given id, given it is
     * it is stil available. if not, it uses the next free id.
     *
     * @param config The configuration of the vertex
     * @param id     The id of which v will soon hopefully be the proud owner
     */
    public V addVertex(Configuration config, int id) {
        // v.id = pollNextFreeVertexID();
        V v = createVertex(config);
        if (this.getVertexById(id) != null) {
            //send warning to console that the id has already been assigned
            v.setId(pollNextFreeVertexID());
            vertices.put(v.getId(), v);
            return v;
        }
        Interval me = new Interval(id, id);
        Interval smallestGreaterThanOrEqual = this.vertexIdHoles.ceiling(me);
        Interval newInterval = new Interval(0, 0);
        if (smallestGreaterThanOrEqual != null) { //if the next biggest
            //interval starts with the id we want to add
            newInterval.a = id + 1;
            newInterval.b = smallestGreaterThanOrEqual.b;
            this.vertexIdHoles.remove(smallestGreaterThanOrEqual);
            if (newInterval.a < newInterval.b) {
                this.vertexIdHoles.add(newInterval);
            }
        }
        v.setId(id);
        vertices.put(id, v);
        return v;
    }

    /**
     * Adds another structure s to this structure (this) identifying thisVertex from this structure with thatVertex from s.
     * The identified vertex has the properties of thatVertex (only properties from Vertex and Edge, not from their descendants).
     * Structure s remains unchanged (the objects from s are copied to this).
     * If the same structure is given as s, produces a copy an merges it into this structure.
     *
     * @param s          Another structure
     * @param thisVertex A vertex from this structure
     * @param thatVertex A vertex from that structure
     */
    public void mergeStructure(Structure<V, E> s, V thisVertex, V thatVertex) {
        Structure<V, E> sCopy = (Structure<V, E>) DeepCopy.copy(s);

        // this handles also the ids
        this.addVertices(sCopy.getVertices());
        this.addEdges(sCopy.getEdges());

        V thatVertexCopy = sCopy.getVertexById(thatVertex.getId());
        // reconnect edges incident with thisVertex to thatVertexCopy
        for (E e : getEdges()) {
            if (e.isLoop() && e.getSource() == thisVertex) {
                e.setSource(thatVertexCopy);
                e.setTarget(thatVertexCopy);
                thatVertex.incidentEdges.add(e);
                thatVertexCopy.outgoingEdges.add(e);
                thatVertexCopy.incomingEdges.add(e);
                continue;
            }
            if (e.getSource() == thisVertex) {
                e.setSource(thatVertexCopy);
                thatVertexCopy.incidentEdges.add(e);
                thatVertexCopy.outgoingEdges.add(e);
            }
            if (e.getTarget() == thisVertex) {
                e.setTarget(thatVertexCopy);
                thatVertexCopy.incidentEdges.add(e);
                thatVertexCopy.incomingEdges.add(e);
            }
        }

        removeVertex(thisVertex);
    }

    public V addVertex() {
        return addVertex((Configuration) null);
    }

    /**
     * Don't use this method. Adding vertices this way is deprecated.
     *
     * @param v
     */
    @Deprecated
    public void addVertex(V v) {
        if (!vertices.containsKey(v.id)) {
            v.id = pollNextFreeVertexID();
            vertices.put(v.id, v);
        }
    } //todo why depricated?

    /**
     * Adds a vertex to the structure. Has no effect if the vertex already
     * exists in the structure.
     *
     * @param config The config with which the vertex will be initialized
     * @return The created vertex
     */
    public V addVertex(Configuration config) {
        V v = createVertex(config);
        v.id = pollNextFreeVertexID();
        vertices.put(v.id, v);
        return v;
    }

    /**
     * Creates a new vertex with the given label and adds it to the structure.
     * This is a convenience function combining createVertex and addVertex.
     * Adding multiple vertices with the same name adds multiple vertices.
     *
     * @param label The label of the new vertex to be added.
     * @return The new vertex.
     */
    public V addVertex(String label) {
        V v = addVertex();
        v.label = label;
        return v;
    }

    public V addVertex(String label, Configuration config) {
        V v = addVertex(config);
        v.label = label;
        return v;
    }

    /**
     * Adds a set of vertices to the structure. Incident edges are not added.Vertex IDs are generated automatically.
     *
     * @param vs The vertices to be added.
     */
    public void addVertices(Collection<V> vs) {
        for (V v : vs) {
            v.id = pollNextFreeVertexID();
            vertices.put(v.id, v);
        }
    }

    /**
     * Clear the structure. Removes all vertices and all edges.
     */
    public void clear() {
        vertices.clear();
        edges.clear();
    }

    public Edge getEdgeByEndVertices(Vertex sourceVertex, Vertex targetVertex) {

        int inputSourceId = sourceVertex.getId();
        int inputTargetId = targetVertex.getId();
        if (sourceVertex == null || targetVertex == null) {
            return null;
        }

        for (Edge e : sourceVertex.getIncidentEdges()) {

            int sourceId = e.getSource().getId();
            int targetId = e.getTarget().getId();


            if (targetId == inputTargetId && sourceId == inputSourceId) {
                return e;
            } else if (!e.isDirected && (targetId == inputSourceId) && (sourceId == inputTargetId)) {
                return e;
            }
        }
        return null;
    }

    /**
     * preliminary method (to be updated with edge id's) for removing an edge
     * very inefficient
     */
    public Edge getEdgeByVertexIds(int inputSourceId, int inputTargetId) {

        Vertex sourceVertex = this.getVertexById(inputSourceId);
        Vertex targetVertex = this.getVertexById(inputTargetId);
        if (sourceVertex == null || targetVertex == null) {
            return null;
        }

        for (Edge e : sourceVertex.getIncidentEdges()) {

            int sourceId = e.getSource().getId();
            int targetId = e.getTarget().getId();


            if (targetId == inputTargetId && sourceId == inputSourceId) {

                return e;
            } else if (!e.isDirected && (targetId == inputSourceId) && (sourceId == inputTargetId)) {
                return e;
            }
        }
        return null;
    }

    /*!!!!!!
    *
    DEPRECATED method for local vertex id's. To be perhaps undeprecated...*/
    @Deprecated
    public Edge getEdgeByVertexIdsAndId(int inputSourceId, int inputTargetId, int inputEdgeId) {

        Vertex sourceVertex = this.getVertexById(inputSourceId);
        Vertex targetVertex = this.getVertexById(inputTargetId);
        if (sourceVertex == null || targetVertex == null) {
            return null;
        }
        for (Edge e : sourceVertex.getIncidentEdges()) {

            int sourceId = e.getSource().getId();
            int targetId = e.getTarget().getId();
            int edgeId = e.getId();
            if (targetId == inputTargetId && sourceId == inputSourceId && edgeId == inputEdgeId) {
                return e;
            } else if (!e.isDirected && (targetId == inputSourceId) && (sourceId == inputTargetId) && edgeId == inputEdgeId) {
                return e;
            }
        }
        return null;
    }

    public Edge getEdgeById(int inputEdgeId) {

        return edges.get(inputEdgeId);
    }

    /**
     * Find the vertex with a given ID. Lookup speed is O(1), since
     * vertices are implemented as a HashMap.
     *
     * @param id The id of the vertex
     * @return Returns the Vertex or null if no vertex has the ID
     */
    public V getVertexById(int id) {
        return vertices.get(id);
    }

    /**
     * Removes vertex from the structure for a given ID. You can remove by
     * Object as well
     *
     * @param id The id of the vertex
     * @return True if the vertex was removed, false if either no entry could be found
     * or the associated vertex was null to begin with (see HashMap.remove() for further details)
     */
    public boolean removeVertexByID(int id) {
        return vertices.remove(id) != null;
    }

    /**
     * Removes a vertex and its incident edges from the structure.
     *
     * @param v The vertex to be removed.
     */
    public void removeVertex(Vertex v) {
        Set<Edge> deletedEdges = new HashSet<>(v.incidentEdges);

        for (Edge e : deletedEdges) {
            Vertex other;
            removeEdge(e);
            // e.getSource().disconnectEdge(e);
            // e.getTarget().disconnectEdge(e);
        }

        vertices.remove(v.id);

        Interval deleteThisInterval = null;
        if (vertexIdHoles.size() == 0) {
            vertexIdHoles.add(new Interval(v.id, v.id));
        } else {
            for (Interval hole : vertexIdHoles) {
                //find hole with[,]..v.id..[a,b]
                if (hole.a > v.id + 1) {
                    //find the hole smaller than [a,b]
                    Interval minInterval = vertexIdHoles.lower(hole);
                    if (minInterval == null) {
                        vertexIdHoles.add(new Interval(v.id, v.id));
                        return;
                    }
                    int min = minInterval.b;
                    if (min < v.id - 1) {
                        vertexIdHoles.add(new Interval(v.id, v.id));
                        return;
                    } else if (min == v.id - 1) {
                        minInterval.b += 1;
                        return;
                    } else {
                        System.err.println("vertex hole indexing error");
                    }
                } else if (hole.a == v.id + 1) { //if v.id is exactly below the hole, extend it [,]..v.id,[a,b] -> [,]..[v.id,b]
                    hole.a--;
                    //in case the extension makes hole lie next to a different interval, merge
                    Interval lower = vertexIdHoles.lower(hole);
                    if (lower != null && lower.b == hole.a - 1) {
                        //merge
                        lower.b = hole.b;
                        deleteThisInterval = hole;
                    }
                    break;
                }
            }
            //did two intervals merge
            if (deleteThisInterval != null) {
                vertexIdHoles.remove(deleteThisInterval);
            } else { //if not, for loop exited without finding a hole that's greater than v.id.
                // Get last hole and decide if to extend or create new interval
                if (vertexIdHoles.last().b == v.id - 1) {
                    vertexIdHoles.last().b += 1;
                } else {
                    vertexIdHoles.add(new Interval(v.id, v.id));
                }
            }
        }
    }

    //TODO: implement
    public void removeVertices(Collection<V> v) {
        //vertices.removeAll(v);
        System.err.println("not implemented");
    }

    /**
     * Create a new edge instance without adding it to the structure.
     *
     * @return The new edge.
     */
    protected abstract E createEdge(Configuration config);

    @Deprecated
    public E createEdge(V source, V target) {
        E e = createEdge(-1, null);
        e.setSource(source);
        e.setTarget(target);
        return e;
    }

    /**
     * Creates an edge without adding it to the graph.
     *
     * @return The new edge.
     */
    public E createEdge(int id, Configuration config) {
        E edge = createEdge(config);
//        if (this.edges.get(id) != null) {
//            // return (E)null;
//            //send warning message that the id had already been assigned
//        }
        if (id != -1) {
            if (this.getEdgeById(id) != null) {
                //send warning to console that the id has already been assigned
                edge.setId(pollNextFreeEdgeID());
                edges.put(edge.getId(), edge);
            } else {
                Interval me = new Interval(id, id);
                Interval smallestGreaterThanOrEqual = this.edgeIdHoles.ceiling(me);
                Interval newInterval = new Interval(0, 0);

                if (smallestGreaterThanOrEqual != null) { //if the next biggest
                    //interval starts with the id we want to add
                    newInterval.a = id + 1;
                    newInterval.b = smallestGreaterThanOrEqual.b;
                    this.edgeIdHoles.remove(smallestGreaterThanOrEqual);
                    if (newInterval.a < newInterval.b) {
                        this.edgeIdHoles.add(newInterval);
                    }
                }
                edge.setId(id);
                edges.put(id, edge);
            }
        } else {
            int nextFreeId = this.pollNextFreeEdgeID();
            edge.setId(nextFreeId);
        }

        return edge;
    }

    public boolean addEdge(E e, V source, V target) {
        e.setSource(source);
        e.setTarget(target);

        return addEdge(e);
    }

    /**
     * Add an edge to the structure. Has no effect if the edge already exists in
     * the structure.
     *
     * @param e The edge to be added.
     */
    public boolean addEdge(E e) {
        //correct siblings first
        e.siblings.clear();
        int nonLoopEdges = 0; // # other edges edge with the same ends {edge.target,edge.source} = {e.target,e.source},
        // non-loops

        for (Edge edge : e.getSource().getIncidentEdges()) {
            if (edge == e) {
                continue;
            }
            if (edge.getTarget() == e.getTarget() || edge.getSource() == e.getTarget()) {

                if (edge.getSource() != edge.getTarget()) {
                    nonLoopEdges++;
                } else if (e.getSource() == e.getTarget()) { //if vertex has loop, don't add another loop e
                    removeEdge(e);
                    return false;
                }
            }
        }

        if (e.getSource() == e.getTarget()) {
            edges.put(e.getId(), e);
        } else {
            for (Edge edge : e.getSource().getIncidentEdges()) {
                if (e.isSiblingTo(edge)) {
                    e.siblings.add(edge);
                }
            }

            //very special case: if the two outer edges of a 3-edge multi edge connection are
            //oriented the opposite way of the middle one (do that for all sibling lists)
            if (e.siblings.size() == 3) {
                if (!e.siblings.get(0).sameOrientationAs(e.siblings.get(1)) && !e.siblings.get(1).sameOrientationAs(e)) {
                    Collections.swap(e.siblings, 1, 2);
                }
            }


            for (Edge edge : e.getSource().getIncidentEdges()) {
                if (edge == e) {
                    continue;
                }
                if (e.isSiblingTo(edge)) {
                    edge.siblings = e.siblings;
                }
            }

            edges.put(e.getId(), e);

        }

        return true;
    }

    /**
     * Adds a set of edges to the structure if their ends are contained in the structure. Edges whose at least one vertex
     * is not in the structure are not addded. IDs are generated automatically.
     *
     * @param es The edges to be added.
     */
    public void addEdges(Collection<E> es) {

        for (E e : es) {
            if (vertices.containsKey(e.getSource().id) && vertices.containsKey(e.getTarget().id)) {
                int id = pollNextFreeEdgeID();
                e.setId(id);
                edges.put(e.getId(), e);
            }
        }
    }

    /**
     * @param sourceID ID of the source vertex
     * @param targetID ID of the target vertex=
     */
    public E addEdge(int sourceID, int targetID) {
        return addEdge(vertices.get(sourceID), vertices.get(targetID));
    }

    public E addEdge(V source, V target) {
        return addEdge(source, target, -1, null);
    }

    public E addEdge(V source, V target, Configuration config) {
        return addEdge(source, target, -1, config);
    }

    public E addEdge(V source, V target, int id) {
        return addEdge(source, target, id, null);
    }

    public E addEdge(V source, V target, int id, Configuration config) {
        /*
         * Add a new edge with source source, target target, id id and config config.
         * If no edge is added (e.g., a new selfloop is not addded), returns null, otherwise
         * the added new edge.
         * */
        E e = createEdge(id, config);

        if (addEdge(e, source, target)) {
            return e;
        }
        return null;
    }

    public void removeEdge(int id) {

        Edge e = this.getEdgeById(id);
        if (e != null) {
            this.removeEdge(e, true);
        }

    }

    /**
     * Removes an edge from the structure. Does not affect vertices, incident or
     * not.
     *
     * @param edge The edge to be removed.
     * @return boolean whether the edge was successfully removed
     */
    public void removeEdge(Edge edge, boolean removeSiblingsEntries) {
        if (edges.get(edge.getId()) == null) { //tried to delete an edge that already exists!
            return;
        }


        // edge.getSource().disconnectEdge(edge);
        // edge.getTarget().disconnectEdge(edge);
        edge.setSource(null);
        edge.setTarget(null);
        if (removeSiblingsEntries) {
            for (int i = 0; i < edge.siblings.size(); i++) {
                if (edge != edge.siblings.get(i)) {
                    edge.siblings.get(i).siblings.remove(edge);
                }
            }
        }

        edges.remove(edge.getId());


        Interval deleteThisInterval = null;
        if (this.edgeIdHoles.size() == 0) {
            this.edgeIdHoles.add(new Interval(edge.getId(), edge.getId()));
        } else {
            int id = edge.getId();
            Interval me = new Interval(edge.getId(), id);
            Interval justAbove = this.edgeIdHoles.ceiling(me);
            Interval justBelow = this.edgeIdHoles.floor(me);


            if (justAbove == null) {
                //this means justBelow cannot be null
                if (justBelow.b == id - 1) {
                    Interval newJustBelow = new Interval(justBelow.a, id);
                    this.edgeIdHoles.remove(justBelow);
                    this.edgeIdHoles.add(newJustBelow);
                } else {
                    this.edgeIdHoles.add(me);
                }
                return;
            } else {
                if (justBelow == null) {
                    if (justAbove.a == id + 1) {
                        Interval newJustAbove = new Interval(id, justAbove.b);
                        this.edgeIdHoles.remove(justAbove);
                        this.edgeIdHoles.add(newJustAbove);
                    } else {
                        this.edgeIdHoles.add(me);
                    }
                    return;
                }
            }


            if (justAbove.a == id + 1) {
                if (justBelow.b == id - 1) {
                    //case 1: [x,id-1] [id+1,y]
                    Interval combinedInterval = new Interval(justBelow.a, justAbove.b);
                    this.edgeIdHoles.remove(justAbove);
                    this.edgeIdHoles.remove(justBelow);
                    this.edgeIdHoles.add(combinedInterval);
                } else {
                    Interval newJustAbove = new Interval(id, justAbove.b);
                    this.edgeIdHoles.remove(justAbove);
                    this.edgeIdHoles.add(newJustAbove);
                }
                return;
            } else {
                if (justBelow.b == id - 1) {
                    Interval newJustBelow = new Interval(justBelow.a, id);
                    this.edgeIdHoles.remove(justBelow);
                    this.edgeIdHoles.add(newJustBelow);
                    return;
                }

            }

            this.edgeIdHoles.add(me);
        }


    }

    public void removeEdge(Edge e) {
        removeEdge(e, true);
    }

    /**
     * @param a The first vertex.
     * @param b The second vertex.
     * @return True if the given two vertices are adjacent.
     */
    public boolean adjacent(V a, V b) {
        for (Edge e : this.getEdges())
            if (e.containsVertex(a) && e.containsVertex(b))
                return true;
        return false;
    }

    public Set<Edge> edgesBetweenVertices(V a, V b) {
        Set<Edge> edges = new HashSet<Edge>();
        for (Edge e : a.getIncidentEdges()) {
            if ((e.getSource() == a && e.getTarget() == b) || ((!e.isDirected()) && e.getSource() == b && e.getTarget() == a)) {
                edges.add(e);
            }
        }
        return edges;
    }

    /**
     * Returns the next free available ID, so that all vertices' ids are continuously
     * filled on a single interval [0, n)
     */
    public int pollNextFreeEdgeID() {
        if (edgeIdHoles.size() != 0) {
            Interval hole = edgeIdHoles.first();
            hole.a++;
            if (hole.a > hole.b) {
                edgeIdHoles.remove(hole);
            }
            return hole.a - 1;
        } else {
            return 0;
        }
    }

    /**
     * Returns the next free available ID, so that all vertices' ids are continuously
     * filled on a single interval [0, n). Updates vertexIdHoles.
     */
    public int pollNextFreeVertexID() {
        if (vertexIdHoles.size() != 0) {
            Interval hole = vertexIdHoles.first();
            hole.a++;
            if (hole.a > hole.b) {
                vertexIdHoles.remove(hole);
            }
            return hole.a - 1;
        } else {
            return 0;
        }
    }

    /**
     * Returns true if projections are separate
     */
    private boolean separatingAxisTest(Vector2D rectFrom, Vector2D rectTo, Vector2D edgeFrom, Vector2D edgeTo) {
        //x axis
        if (Math.max(rectFrom.getX(), rectTo.getX()) < Math.min(edgeFrom.getX(), edgeTo.getX())) {
            return true;
        }
        if (Math.min(rectFrom.getX(), rectTo.getX()) > Math.max(edgeFrom.getX(), edgeTo.getX())) {
            return true;
        }
        //y axis
        if (Math.max(rectFrom.getY(), rectTo.getY()) < Math.min(edgeFrom.getY(), edgeTo.getY())) {
            return true;
        }
        return Math.min(rectFrom.getY(), rectTo.getY()) > Math.max(edgeFrom.getY(), edgeTo.getY());
    }

    private boolean areNegativelyProportional(Vector2D a, Vector2D b) {
        double ax = a.getX();
        double ay = a.getY();

        double bx = b.getX();
        double by = b.getY();

        if (ax != 0) {
            return Math.signum(ax) != Math.signum(bx);
        } else {
            return Math.signum(ay) != Math.signum(by);
        }
    }

    /* checking if x coordinate is in bounds of the rect (for x and y respectively)
     * |q-v|<|c|  and  sgn(c)|q-v|>0
     */
    private boolean insideSelection(double qx, double qy, double cx, double cy, double vx, double vy) {
        return Math.signum(cx) * (qx - vx) <= Math.abs(cx)
                && Math.signum(cy) * (qy - vy) <= Math.abs(cy)
                && Math.signum(cx) * (qx - vx) >= 0
                && Math.signum(cy) * (qy - vy) >= 0;
    }

    /**
     * Collapses edges or, when every single edge in the selection is already collapsed,
     * inflates every set of edges.
     */
    public void collapseEdges(Set<Object> selection) {
        HashSet<Edge> representativeEdges = new HashSet<>();
        //linear time
        for (Object edge : selection) {
            if (edge instanceof Edge) {
                Edge e = (Edge) edge;
                if (!e.isLoop()) {
                    boolean nonRelated = true;
                    //edges can only be related to siblings. Iterate over all siblings
                    //and check if one of them is already declared a representative
                    for (Edge sibling : e.siblings) {
                        nonRelated &= !representativeEdges.contains(sibling);
                    }
                    if (nonRelated) {
                        representativeEdges.add(e);
                    }

                }
            }
        }
        for (Edge representative : representativeEdges) {
            representative.collapse(this);

        }
    }

    /**
     * Aligns all selected vertices along their y coordinate
     */
    public void alignHorizontallyMean(LinkedHashSet<Object> selection) {
        Set<Vertex> vertices = new HashSet<>();
        double sum = 0;
        double count = 0;
        for (Object o : selection) {
            if (o instanceof Vertex) {
                vertices.add((Vertex) o);
                sum += ((Vertex) o).getCoordinates().getY();
                count++;
            }
        }
        for (Vertex v : vertices) {
            v.setCoordinates(v.getCoordinates().getX(), sum / count);
        }
    }

    /**
     * Aligns all selected vertices along the y coordinate of the first
     * selected element
     */
    public void alignHorizontallyFirst(LinkedHashSet<Object> selection) {

    }

    public void alignVerticallyMean(LinkedHashSet<Object> selection) {
        Set<Vertex> vertices = new HashSet<>();
        double sum = 0;
        double count = 0;
        for (Object o : selection) {
            if (o instanceof Vertex) {
                vertices.add((Vertex) o);
                sum += ((Vertex) o).getCoordinates().getX();
                count++;
            }
        }
        for (Vertex v : vertices) {
            v.setCoordinates(sum / count, v.getCoordinates().getY());
        }
    }

    @Override
    public Element toXml(Document doc) throws Exception {
        Element snode = doc.createElement("graph"); //super.toXml(doc);
        if (this.getClass().getAnnotation(XmlName.class).name().equals("graph")) {
            snode.setAttribute("edgedefault", "undirected");
        } else {
            snode.setAttribute("edgedefault", "directed");
        }
        snode.setAttribute("type", this.getClass().getAnnotation(XmlName.class).name()); //super.xmlName());

        HashMap<Vertex, String> ids = new HashMap<>();
        Integer i = 1;
        for (Vertex v : getVertices()) {
            String id = "n" + (i++);
            ids.put(v, id);
            Element vnode = v.toXml(doc, id);
            if (vnode != null)
                snode.appendChild(vnode);
        }

        for (Edge e : getEdges()) {
            Element enode = e.toXml(doc, ids);
            if (enode != null)
                snode.appendChild(enode);
        }

        return snode;
    }

    public Element toSimpleXml(Document doc) throws Exception {
        Element snode = doc.createElement("graph"); //super.toXml(doc);
        if (this.getClass().getAnnotation(XmlName.class).name().equals("graph")) {
            snode.setAttribute("edgedefault", "undirected");
        } else {
            snode.setAttribute("edgedefault", "directed");
        }
        snode.setAttribute("type", this.getClass().getAnnotation(XmlName.class).name()); //super.xmlName());

        HashMap<Vertex, String> ids = new HashMap<>();
        Integer i = 1;
        for (Vertex v : getVertices()) {
            String id = "n" + (i++);
            ids.put(v, id);
            Element vnode = v.toSimpleXml(doc, id);
            if (vnode != null)
                snode.appendChild(vnode);
        }
        Set<E> edges = getEdges();
        if (this instanceof DirectedGraph) {
            List<List<E>> pairs = edges.stream().collect(Collectors.groupingBy(e -> new Pair(e)))
                    .entrySet().stream()
                    .map(Map.Entry::getValue)
                    .filter(l -> l.size() > 1)
                    .collect(Collectors.toList());
            for (List<E> pair : pairs) {
                snode.appendChild(pair.get(0).toSimpleXml(doc, ids, "bend left"));
                snode.appendChild(pair.get(1).toSimpleXml(doc, ids));
            }
            edges.removeAll(pairs.stream().flatMap(List::stream).collect(Collectors.toList()));
        }
        appendEdges(doc, snode, ids, edges);


        return snode;
    }

    private void appendEdges(Document doc, Element snode, HashMap<Vertex, String> ids, Set<E> edges) throws Exception {
        for (Edge e : edges) {
            Element enode = e.toSimpleXml(doc, ids);
            if (enode != null)
                snode.appendChild(enode);
        }
    }

    public int[][] toIncM() {    //TODO: write function ToIncidenceMatrix()
        int[][] matrixM = new int[1][1];
        return matrixM;
    }

    public void writeToFile(String filename, Boolean simple) throws Exception {
        writeToStream(new StreamResult(filename), simple);
    }

    public void writeToStream(StreamResult stream, Boolean simple) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("graphml");
        Element snode = simple ? toSimpleXml(doc) : toXml(doc);
        if (snode == null)
            throw new Exception("Error writing to XML");
        root.appendChild(snode);
        doc.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, stream);
    }

    public String xmlToString() throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument(); //a new xml document

        Element root = doc.createElement("graphml"); //<graphml></graphml>
        Element snode = toXml(doc); //<restOfGraph></restOfGraph>
        if (snode == null)
            throw new Exception("Error writing to XML");
        root.appendChild(snode);
        doc.appendChild(root);


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        DOMSource source = new DOMSource(root);
        StreamResult result = new StreamResult(new StringWriter());


        transformer.transform(source, result);

        String strObject = result.getWriter().toString();

        return strObject;

    }

    public String incToString() throws Exception {    //TODO: Write Function IncidenceMatrix to String
        return "";
    }

    public String tgfToString() throws Exception {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument(); //a new xml document

        Element root = doc.createElement("graphml"); //<graphml></graphml>
        Element snode = toXml(doc); //<restOfGraph></restOfGraph>
        if (snode == null)
            throw new Exception("Error writing to XML");
        root.appendChild(snode);
        doc.appendChild(root);


        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(root);
        StreamResult result = new StreamResult(new StringWriter());


        transformer.transform(source, result);

        String strObject = result.getWriter().toString();

        return strObject;

    }

    public void fromXml(Element gnode) throws Exception {
        HashMap<String, Vertex> vertexRegister = new HashMap<>();
        HashMap<Edge, Element> loadedFrom = new HashMap<>();
        ArrayList<Edge> tempEdges = new ArrayList<>();

        NodeList children = gnode.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node childNode = children.item(i);
            if (childNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            Element child = (Element) childNode;
            Object obj = PluginManager.instantiateClass(child.getTagName());
            if (obj instanceof Vertex) {
                V v = addVertex();
                v.copy((V) obj);
                String id = v.fromXml(child);
                vertexRegister.put(id, v);
            } else if (obj instanceof Edge) {
                tempEdges.add((E) obj);
                loadedFrom.put((E) obj, child);
            }
        }
        for (Edge e : tempEdges) {
            e.fromXml(loadedFrom.get(e), vertexRegister);

            e.setId(this.pollNextFreeEdgeID());
            edges.put(e.getId(), (E) e);
        }
    }

    /**
     * Was the structure opened from a file or does this structure reference
     * a file? (e.g.: was it saved as...?)
     * <p>
     * You can set the file reference here.
     */
    public void setFileReference(boolean hasReference, String ref) {
        isOpenFile = hasReference;
        structureFilePath = ref;
    }

    /**
     * @return A string containing the path of the referenced file. Empty if
     * no file is referenced by this structure.
     */
    public String getFileReference() {
        return structureFilePath;
    }

    /**
     * @return True if the structure is referencing a file
     */
    public boolean hasFileReference() {
        return isOpenFile;
    }

    public StructureDescription getDescription() throws Exception {
        if (!this.getClass().isAnnotationPresent(StructureDescription.class))
            throw new Exception("class " + this.getClass().getName() + " has no @StructureDescription Annotation");
        return this.getClass().getAnnotation(StructureDescription.class);
    }

    private static class Pair {
        int v1, v2;

        public Pair(int v1, int v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public Pair(Edge e) {
            this.v1 = e.getSource().getId();
            this.v2 = e.getTarget().getId();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            return (pair.v1 == v1 && pair.v2 == v2) || (pair.v2 == v1 && pair.v1 == v2);
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(v1 + v2);
        }
    }

    protected static class Interval {
        public int a;
        public int b;

        Interval(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "(" + a + ", " + b + ")";
        }
    }


}
