package com.algo.structure;

import com.algo.config.RandomGraphParameters;
import com.algo.generator.RandomGraph;
import com.algo.rendering.Vector2D;
import com.algo.structure.controlpoints.ControlPoint;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.algo.plugins.PluginManager.registerClass;

public class StructureUtils {

    static {
        registerPlugins();
    }

    public static Structure<Vertex, Edge> getGraph(String structureFile) throws Exception {
        Structure<Vertex, Edge> structure = getGraphFromFile(structureFile);
        standardiseStructure(structure);
        return structure;
    }

    private static void standardiseStructure(Structure<Vertex, Edge> structure) {
        for (Vertex vertex : structure.getVertices()) {
            vertex.setCoordinates(getRoundedVector2D(vertex.getCoordinates()));
            if (vertex.label.isBlank()) {
                vertex.label = String.valueOf(vertex.getId());
            }
        }
        List<ControlPoint> controlPoints = structure.getEdges().stream().flatMap(e -> e.controlPoints.stream()).collect(Collectors.toList());
        for (ControlPoint cp : controlPoints) {
            cp.setPosition(getRoundedVector2D(cp.getPosition()));
        }
    }

    private static Vector2D getRoundedVector2D(Vector2D coordinates) {
        return new Vector2D(Math.round(1000.0 * coordinates.getX()) / 1000.0,
                Math.round(-1000.0 * coordinates.getY()) / 1000.0);
    }

    public static Structure<Vertex, Edge> getGraphFromFile(String structureFile) throws Exception {
        return Structure.loadFromFile(structureFile);
    }

    public static Structure<Vertex, Edge> getRandomGraph(boolean directed, boolean weighted) throws Exception {
        int n = 16;
        double p = 0.15;
//        boolean directed = false;
        int maxWeight = 10;
        Structure<Vertex, Edge> structure = new RandomGraph()
                .generate(new RandomGraphParameters(Arrays.asList(Integer.toString(n),
                        Double.toString(p),
                        Boolean.toString(directed))));
        if(weighted)
            structure.getEdges().forEach(e -> e.weight = Double.valueOf(ThreadLocalRandom.current().nextInt(1, maxWeight)));
        standardiseStructure(structure);
        return structure;
    }

    private static void registerPlugins() {
        try {
            registerClass(EdgeIntermediatePoint.class);
            registerClass(Vertex.class);
            registerClass(Edge.class);
            registerClass(UndirectedGraph.class);
            registerClass(DirectedGraph.class);
            registerClass(ControlPoint.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
