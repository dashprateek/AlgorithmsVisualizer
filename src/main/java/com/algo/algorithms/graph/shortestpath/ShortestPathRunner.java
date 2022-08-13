package com.algo.algorithms.graph.shortestpath;

import com.algo.Runner;
import com.algo.algorithms.OptionsEnum;

import java.util.Scanner;

public class ShortestPathRunner extends Runner {
    FloydWarshallAlgorithm floydWarshallAlgorithmRunner = new FloydWarshallAlgorithm();
    BellmanFordAlgorithm bellmanFordAlgorithmRunner = new BellmanFordAlgorithm();
    DijkstrasAlgorithm dijkstrasAlgorithmRunner = new DijkstrasAlgorithm();

    @Override
    public boolean run(Scanner sc) {
        boolean exit = true;
        while (true) {
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
        }
    }

    public enum ShortestPathEnum implements OptionsEnum {


        FLOYD_WARSHALL("Floyd-Warshall"), BELLMAN_FORD("Bellman-Ford"), DIJKSTRA("Dijkstra"), BACK("BACK"), EXIT("Exit");
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