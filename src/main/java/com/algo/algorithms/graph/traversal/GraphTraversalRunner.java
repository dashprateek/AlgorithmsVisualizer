package com.algo.algorithms.graph.traversal;

import com.algo.Runner;
import com.algo.algorithms.OptionsEnum;
import com.algo.algorithms.graph.GraphAlgorithmsRunner;
import com.algo.annotations.AlgorithmDescription;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Scanner;

@AlgorithmDescription(
        name = "Graph traversal",
        text = "The process of visiting (checking and/or updating) each vertex in a graph.",
        url = "https://en.wikipedia.org/wiki/Graph_traversal"
)
public class GraphTraversalRunner extends GraphAlgorithmsRunner {
    private static final Runner bfsTraversal = new BFSTraversal();
    private static final Runner dfsTraversal = new DFSTraversal();


    @Override
    public boolean run(Scanner sc) throws Exception {
        boolean exit = false;
        while (true) {
            try {
                TraversalEnum input = getInput(sc, TraversalEnum.values());
                if (input == null) continue;
                switch (input) {
                    case BFS:
                        exit = bfsTraversal.run(sc);
                        break;
                    case DFS:
                        exit = dfsTraversal.run(sc);
                        break;
                    case BACK:
                        return false;
                    case EXIT:
                        return true;
                }
                if (exit)
                    return true;
            } catch (Exception e) {
                System.out.println("The execution of the algorithm terminates with the following stack trace:");
                e.printStackTrace();
                System.out.println("Please try again");
            }
        }
    }

    public enum TraversalEnum implements OptionsEnum {
        BFS("Breadth First Traversal"), DFS("Depth First Traversal"), BACK("BACK"), EXIT("Exit");
        public String text;

        TraversalEnum(String type) {
            this.text = type;
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
