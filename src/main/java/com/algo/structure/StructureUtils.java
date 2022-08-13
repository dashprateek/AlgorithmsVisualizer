package com.algo.structure;

import com.algo.config.RandomGraphParameters;
import com.algo.generator.RandomGraph;
import com.algo.rendering.Vector2D;
import com.algo.structure.controlpoints.ControlPoint;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import static com.algo.plugins.PluginManager.registerClass;

public class StructureUtils {

    static {
        registerPlugins();
    }

    public static Structure getGraph(String structureFile) throws Exception {
        Structure<Vertex, Edge> structure = getGraphFromFile(structureFile);
        structure.getVertices().stream()
                .filter(v -> v.label.isBlank())
                .forEach(v -> v.label = String.valueOf(v.getId()+1));
        structure.getVertices().forEach(vertex ->
            vertex.setCoordinates(getRoundedVector2D(vertex.getCoordinates())));
        structure.getEdges().stream()
                .flatMap(e -> e.controlPoints.stream())
                .forEach(cp -> cp.setPosition(getRoundedVector2D(cp.getPosition())));
        return structure;
    }

    private static Vector2D getRoundedVector2D(Vector2D coordinates) {
        return new Vector2D(Math.round(1000.0 * coordinates.getX()) / 1000.0,
                Math.round(-1000.0 * coordinates.getY()) / 1000.0);
    }

    public static Structure<Vertex, Edge> getGraphFromFile(String structureFile) throws Exception {
        if (Objects.nonNull(structureFile) && !structureFile.isEmpty())
            return Structure.loadFromFile(structureFile);
        int n = 8;
        double p = 0.15;
        boolean directed = true;
        int maxWeight = 10;
        Structure<Vertex, Edge> structure = new RandomGraph()
                .generate(new RandomGraphParameters(Arrays.asList(Integer.toString(n),
                        Double.toString(p),
                        Boolean.toString(directed))));
        structure.getEdges().forEach(e -> e.weight = Double.valueOf(ThreadLocalRandom.current().nextInt(1, maxWeight)));
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
