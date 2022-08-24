package com.algo.algorithms.graph.shortestpath;

import com.algo.Runner;
import com.algo.algorithms.OptionsEnum;
import com.algo.algorithms.graph.GraphAlgorithmsRunner;

import java.util.Scanner;

public class ShortestPathRunner extends GraphAlgorithmsRunner {
    private static final Runner floydWarshallAlgorithmRunner = new FloydWarshallAlgorithm();
    private static final Runner bellmanFordAlgorithmRunner = new BellmanFordAlgorithm();
    private static final Runner dijkstrasAlgorithmRunner = new DijkstrasAlgorithm();

    @Override
    public boolean run(Scanner sc) throws Exception {
        boolean exit = true;
        while (true) {
            try {
                ShortestPathEnum input = getInput(sc, ShortestPathEnum.values());
                if (input == null) continue;
                switch (input) {
                    case FLOYD_WARSHALL:
                        exit = floydWarshallAlgorithmRunner.run(sc);
                        break;
                    case BELLMAN_FORD:
                        exit = bellmanFordAlgorithmRunner.run(sc);
                        break;
                    case DIJKSTRA:
                        exit = dijkstrasAlgorithmRunner.run(sc);
                        break;
                    case BACK:
                        return false;
                    case EXIT:
                        return true;
                }
                if (exit) {
                    return true;
                }
            } catch (Exception e) {
                System.out.println("The execution of the algorithm terminates with the following stack trace:");
                e.printStackTrace();
                System.out.println("Please try again");
            }
        }
    }

    public enum ShortestPathEnum implements OptionsEnum {


        FLOYD_WARSHALL("Floyd Warshall"), BELLMAN_FORD("Bellman Ford"), DIJKSTRA("Dijkstra"), BACK("BACK"), EXIT("Exit");
        public String text;

        ShortestPathEnum(String text) {
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