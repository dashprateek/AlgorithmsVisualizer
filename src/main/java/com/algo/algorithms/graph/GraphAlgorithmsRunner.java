package com.algo.algorithms.graph;

import com.algo.Runner;
import com.algo.algorithms.AlgorithmsRunner;
import com.algo.algorithms.OptionsEnum;
import com.algo.algorithms.graph.shortestpath.ShortestPathRunner;
import com.algo.algorithms.graph.traversal.GraphTraversalRunner;
import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.StructureUtils;
import com.algo.structure.Vertex;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GraphAlgorithmsRunner extends AlgorithmsRunner {
    private static final Runner shortestPathRunner = new ShortestPathRunner();
    private static final Runner traversalRunner = new GraphTraversalRunner();

    @Override
    public boolean run(Scanner sc) throws Exception {
        boolean exit = false;
        while (true) {
            GraphAlgorithmsEnum input = getInput(sc, GraphAlgorithmsEnum.values());
            if (input == null) continue;
            switch (input) {
                case SHORTEST_PATH:
                    exit = shortestPathRunner.run(sc);
                    break;
                case Traversal:
                    exit = traversalRunner.run(sc);
                    break;
                case BACK:
                    return false;
                case EXIT:
                    return true;
            }
            if (exit)
                return true;
        }
    }

    protected Structure<Vertex, Edge> getGraph(Scanner sc) throws Exception {
        System.out.println("Please enter the absolute path for the structure file");

        String structureFile = sc.nextLine().trim();
        return StructureUtils.getGraph(structureFile);
    }

    protected Vertex getSourceVertex(Scanner sc, Structure<Vertex, Edge> structure) {
        System.out.println("Please enter the id of the start vertex");
        String possibleIds = "Possible Ids:" + structure.getVertices().stream().map(Vertex::getId).sorted().map(String::valueOf).collect(Collectors.joining(","));
        System.out.println(possibleIds);
        Vertex source = structure.getVertexById(getIntegerFromConsole(sc));
        while (Objects.isNull(source)) {
            System.out.println("Invalid Id\nPlease enter the id of the start vertex");
            System.out.println(possibleIds);
            source = structure.getVertexById(getIntegerFromConsole(sc));
        }
        return source;
    }

    private enum GraphAlgorithmsEnum implements OptionsEnum {
        SHORTEST_PATH("Shortest Path"), Traversal("Traversal"), BACK("BACK"), EXIT("Exit");
        public String text;

        GraphAlgorithmsEnum(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }

        @Override
        public int getOrdinal() {
            return this.ordinal();
        }
    }
}