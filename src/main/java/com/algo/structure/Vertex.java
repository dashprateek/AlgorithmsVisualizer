package com.algo.structure;/* This file is part of Gralog, Copyright (c) 2016-2018 LaS group, TU Berlin.
 * License: https://www.gnu.org/licenses/gpl.html GPL version 3 or later. */

import com.algo.annotations.DataField;
import com.algo.config.Configuration;
import com.algo.plugins.XmlMarshallable;
import com.algo.plugins.XmlName;
import com.algo.rendering.GralogColor;
import com.algo.rendering.GralogTikStyles;
import com.algo.rendering.Vector2D;
import com.algo.rendering.shapes.Ellipse;
import com.algo.rendering.shapes.RenderingShape;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.lang.Boolean.FALSE;


/**
 * A vertex with a circle shape.
 */
@XmlName(name = "node")
public class Vertex extends XmlMarshallable implements Serializable {

    @DataField(display = true, readOnly = true)
    public int id;
    @DataField(display = true)
    public String label = "";
    @DataField(display = true, readOnly = true)
    public double radius = 0.7;     // cm for what?!

    //the position of the loop center on the circle
    ///note: -90 is on top because the coordinate system is flipped horizontally
    @DataField(display = false)
    public Double loopAnchor = -90d;  // degrees
    //the position of the endpoints of a loop
    @DataField(display = false)
    public double loopAngle = 20;   // degrees

    public Double strokeWidth = 2.54 / 96; // cm
    public Double textHeight = 0.4d; // cm; the height of the label text

    @DataField(display = true)
    public GralogColor fillColor = GralogColor.WHITE;
    @DataField(display = true)
    public GralogColor strokeColor = GralogColor.BLACK;

    // size and shape of the vertex
    @DataField(display = true)
    public RenderingShape shape = Ellipse.create(1.4, 1.4);
    @DataField(display = true)
    public GralogTikStyles style = GralogTikStyles.NODE_STYLE;
    @DataField(display = true)
    public String distanceFromSource = "";
    @DataField(display = true)
    public Boolean printLabel = FALSE;
    Set<Edge> outgoingEdges;
    Set<Edge> incomingEdges;
    Set<Edge> incidentEdges; // in directed graphs: outgoing and incoming
    private Vector2D coordinates = new Vector2D(0.0, 0.0);

    public Vertex() {
        outgoingEdges = new HashSet<>();
        incidentEdges = new HashSet<>();
        incomingEdges = new HashSet<>();
    }


    public Vertex(Configuration config) {
        this();
        if (config != null) {
            initWithConfig(config);
        }
    }

    /**
     * Initializes lots of variables from a given configuration
     *
     * @param config
     */
    protected void initWithConfig(Configuration config) {
        //TODO: complete
        strokeColor = config.getValue("Vertex_strokeColor", GralogColor::parseColor, GralogColor.BLACK);
        fillColor = config.getValue("Vertex_fillColor", GralogColor::parseColor, GralogColor.WHITE);
        shape.setWidth(config.getValue("Vertex_width", Double::parseDouble, 1.0));
        shape.setHeight(config.getValue("Vertex_height", Double::parseDouble, 1.0));
    }

    /**
     * Copies a vertex information from a given vertex object. Not the ID.
     */
    public <V extends Vertex> void copy(V v) {
        //this.id = v.id;
        this.radius = v.radius;
        this.loopAngle = v.loopAngle;
        this.loopAnchor = v.loopAnchor;
        this.strokeWidth = v.strokeWidth;
        try {
            this.shape = (RenderingShape) v.shape.getClass().getConstructors()[0].newInstance(v.shape.sizeBox);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.strokeColor = new GralogColor(v.strokeColor);
        this.fillColor = new GralogColor(v.fillColor);


        this.setCoordinates(v.getCoordinates());
        this.incidentEdges = new HashSet<>(v.incidentEdges);
        this.outgoingEdges = new HashSet<>(v.outgoingEdges);
        this.incomingEdges = new HashSet<>(v.incomingEdges);

    }

    @Override
    public String toString() {
        return "Vertex:" + getId();
//        return "Vertex{" + "label=" + label + ", radius=" + radius
//                + ", fillColor=" + fillColor
//                + ", strokeWidth=" + strokeWidth
//                + ", textHeight=" + textHeight
//                + ", strokeColor=" + strokeColor
//                + ", coordinates=" + coordinates + '}';
    }


    public String gralogPipify() {
        Class<?> c = this.getClass();
        String ret = "";
        for (Field f : c.getDeclaredFields()) {

            f.setAccessible(true);
            boolean toBeSent = false;
            Annotation[] annotations = f.getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation instanceof DataField) {
                    DataField dataField = (DataField) annotation;
                    toBeSent = dataField.display() && (!dataField.readOnly());
                    break;
                }
            }
            if (toBeSent) {

                ret = ret + f.getName() + "=";
                try {
                    ret = ret + f.get(this).toString() + "|" + Structure.pythonifyClass(f.getType()) + "#";
                } catch (Exception e) {
                    //todo: to handle!!!
                }
            }

        }
        if (ret.length() > 0) {
            ret = ret.substring(0, ret.length() - 1);
        }

        return ret;

    }

    public void setLabel(String label) {
        this.label = label;
    }

    void connectEdge(Edge e) {
        if (e.isDirected()) {
            if (e.getSource() == this) {
                outgoingEdges.add(e);
            }
            if (e.getTarget() == this) {
                this.incomingEdges.add(e);
            }
        } else {
            this.incomingEdges.add(e);
            this.outgoingEdges.add(e);
        }
        this.incidentEdges.add(e);
    }

    void connectEdgeLocal(Edge e) {
        if (e.getSource() == this) {
            //if id has not been set already, set it
            if (e.getId() == -1 && incidentEdges.isEmpty()) {
                e.setId(0);
            }
            if (e.getId() == -1) {
                int[] allIndices = new int[incidentEdges.size()];
                int k = 0;

                for (Edge edge : incidentEdges) {
                    allIndices[k] = edge.getId();
                    k++;
                }

                Arrays.sort(allIndices);

                boolean changedOnce = false;
                for (int i = 0; i < allIndices.length; i++) {
                    if (i < allIndices[i]) {
                        e.setId(i);
                        changedOnce = true;
                        break;
                    }
                }
                if (!changedOnce) {
                    e.setId(allIndices.length); //fallback
                }
            }
            outgoingEdges.add(e);
        }
        if (e.getTarget() == this) {
            this.incomingEdges.add(e);
        }
        this.incidentEdges.add(e);
    }

    public void disconnectEdge(Edge e) {
        if (e.getSource() == this || (!e.isDirected && e.getTarget() == this)) {
            outgoingEdges.remove(e);
        }
        if (e.getTarget() == this || (!e.isDirected && e.getSource() == this)) {
            incomingEdges.remove(e);
        }
        this.incidentEdges.remove(e);
    }

    public Set<Edge> getIncidentEdges() {
        return incidentEdges;
    }

    public int getDegree() {
        return incidentEdges.size();
    }

    public double getRadius() {
        return radius;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Edge> getOutgoingEdges() {
        return outgoingEdges;
    }

    public int getOutDegree() {
        return outgoingEdges.size();
    }

    public Set<Edge> getIncomingEdges() {
        return this.incomingEdges;
    }

    public int getInDegree() {
        return incomingEdges.size();
    }
//##########START depricated!!!! use getNeighbours instead#########

    /**
     * @return The set of adjacent vertices.
     */
    @Deprecated
    public Set<Vertex> getAdjacentVertices() {
        Set<Vertex> result = new HashSet<>();
        for (Edge e : incidentEdges) {
            Vertex v = e.getSource();
            if (v == this)
                v = e.getTarget();
            result.add(v);
        }
        return result;
    }

//##########END#########

    public double maximumCoordinate(int dimension) {
        if (coordinates.dimensions() > dimension)
            return coordinates.get(dimension);
        return Double.NEGATIVE_INFINITY;
    }

    public Vector2D intersection(Vector2D p1, Vector2D p2) {
        return p1.minus(p2).normalized().multiply(this.radius).plus(p2);
    }

    public Vector2D intersectionAdjusted(Vector2D p1, Vector2D p2, double adjust) {
        return p1.minus(p2).normalized().multiply(this.radius - adjust).plus(p2);
    }

    public void setCoordinates(double x, double y) {
        coordinates = new Vector2D(x, y);
    }

    public Vector2D getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Vector2D v) {
        coordinates = v;
    }

    public Set<Vertex> getNeighbours() {
        Set<Vertex> neighbours = new HashSet<Vertex>();
        for (Edge e : this.getIncidentEdges()) {
            if (e.getTarget() != this) {
                neighbours.add(e.getTarget());
            } else {
                neighbours.add(e.getSource());
            }
        }
        return neighbours;
    }

    public Set<Vertex> getOutgoingNeighbours() {
        Set<Vertex> outgoingNeighbours = new HashSet<Vertex>();
        for (Edge e : this.getOutgoingEdges()) {
            outgoingNeighbours.add(e.getTarget());
        }
        return outgoingNeighbours;
    }


    public Set<Vertex> getIncomingNeighbours() {
        Set<Vertex> incomingNeighbours = new HashSet<Vertex>();
        for (Edge e : this.getIncomingEdges()) {
            incomingNeighbours.add(e.getSource());
        }
        return incomingNeighbours;
    }

    public void snapToGrid(double gridSize) {
        Vector2D v = coordinates.snapToGrid(gridSize);
        setCoordinates(v.getX(), v.getY());
    }

    /**
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return True if the given coordinates are inside the circular shape of
     * this vertex.
     */
    public boolean containsCoordinate(double x, double y) {
        double tx = coordinates.getX();
        double ty = coordinates.getY();
        return (x - tx) * (x - tx) + (y - ty) * (y - ty) < radius * radius;
    }

    public Element toXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Double.toString(coordinates.getX()));
        vnode.setAttribute("y", Double.toString(coordinates.getY()));
        vnode.setAttribute("label", label);
        vnode.setAttribute("radius", Double.toString(radius));
        vnode.setAttribute("fillcolor", fillColor.toHtmlString());
        vnode.setAttribute("textheight", Double.toString(textHeight));
        vnode.setAttribute("strokewidth", Double.toString(strokeWidth));
        vnode.setAttribute("strokecolor", strokeColor.toHtmlString());
        vnode.setAttribute("shapewidth", Double.toString(shape.sizeBox.width));
        vnode.setAttribute("shapeheight", Double.toString(shape.sizeBox.height));
        return vnode;
    }

    public Element toSimpleXml(Document doc, String id) throws Exception {
        Element vnode = super.toXml(doc);
        vnode.setAttribute("id", id);
        vnode.setAttribute("x", Double.toString(coordinates.getX()));
        vnode.setAttribute("y", Double.toString(coordinates.getY()));
        vnode.setAttribute("label", label);
//        vnode.setAttribute("style", style.getStyle());
//        vnode.setAttribute("distance_from_source", distanceFromSource.isBlank() ? "\\infty" : distanceFromSource);
//        vnode.setAttribute("print_label", printLabel.toString());
        return vnode;
    }

    public String fromXml(Element vnode) {
        coordinates = new Vector2D(
                Double.parseDouble(vnode.getAttribute("x")),
                Double.parseDouble(vnode.getAttribute("y"))
        );
        if (vnode.hasAttribute("label"))
            label = vnode.getAttribute("label");
        if (vnode.hasAttribute("radius"))
            radius = Double.parseDouble(vnode.getAttribute("radius"));
        if (vnode.hasAttribute("fillcolor"))
            fillColor = GralogColor.parseColor(vnode.getAttribute("fillcolor"));
        if (vnode.hasAttribute("textheight"))
            textHeight = Double.parseDouble(vnode.getAttribute("textheight"));
        if (vnode.hasAttribute("strokewidth"))
            strokeWidth = Double.parseDouble(vnode.getAttribute("strokewidth"));
        if (vnode.hasAttribute("strokecolor"))
            strokeColor = GralogColor.parseColor(vnode.getAttribute("strokecolor"));
        if (vnode.hasAttribute("shapewidth"))
            shape.sizeBox.width = Double.parseDouble(vnode.getAttribute("shapewidth"));
        if (vnode.hasAttribute("shapeheight"))
            shape.sizeBox.height = Double.parseDouble(vnode.getAttribute("shapeheight"));
        if (vnode.hasAttribute("style"))
            style = GralogTikStyles.valueOfStyle(vnode.getAttribute("style"));
        if (vnode.hasAttribute("distance_from_source"))
            distanceFromSource = vnode.getAttribute("distance_from_source");
        if (vnode.hasAttribute("print_label"))
            printLabel = Boolean.parseBoolean(vnode.getAttribute("print_label"));
        return vnode.getAttribute("id");
    }

}
