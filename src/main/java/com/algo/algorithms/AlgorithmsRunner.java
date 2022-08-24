package com.algo.algorithms;

import com.algo.MainRunner;
import com.algo.Runner;
import com.algo.algorithms.cfg.CYKAlgorithm;
import com.algo.algorithms.graph.GraphAlgorithmsRunner;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Scanner;

public class AlgorithmsRunner extends MainRunner {

    private static final Runner graphAlgorithmsRunner = new GraphAlgorithmsRunner();
    private static final Runner cykAlgorithmsRunner = new CYKAlgorithm();

    @Override
    public boolean run(Scanner sc) throws Exception {
        boolean exit = false;
        while (true) {
            AlgorithmsEnum input = getInput(sc, AlgorithmsEnum.values());
            if (input == null) continue;
            switch (input) {
                case GRAPH_ALGORITHMS:
                    exit = graphAlgorithmsRunner.run(sc);
                    break;
                case CYK:
                    exit = cykAlgorithmsRunner.run(sc);
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


    public enum AlgorithmsEnum implements OptionsEnum {
        GRAPH_ALGORITHMS("Graph Algorithms"), CYK("Cocke Younger Kasami (CYK) Algorithm"),
        BACK("BACK"), EXIT("Exit");
        public String text;


        AlgorithmsEnum(String text) {
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
