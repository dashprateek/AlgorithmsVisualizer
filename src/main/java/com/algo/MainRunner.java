package com.algo;

import com.algo.algorithms.AlgorithmsRunner;
import com.algo.algorithms.OptionsEnum;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

import static com.algo.algorithms.OptionsEnum.Options.YES;
import static com.algo.algorithms.OptionsEnum.Options.valueOf;
import static com.algo.export.ExportUtils.runXSLTTransform;

public class MainRunner extends Runner {
    private static final AlgorithmsRunner algorithms = new AlgorithmsRunner();

    @Override
    public boolean run(Scanner sc) throws IOException, TransformerException {
        boolean exit = false;
        while (true) {
            MainWindowOptions input = this.getInput(sc, MainWindowOptions.values());
            if (Objects.isNull(input)) continue;
            switch (input) {
                case SELECT_ALGORITHMS:
                    exit = algorithms.run(sc);
                    break;
                case PROCESS_XSLT:
                    String outputFile = getOutputFile(sc);
                    transform(sc, outputFile, false);
                    break;
                case EXIT:
                    return true;
            }
            if (exit)
                return true;
        }
    }

    protected String getOutputFile(Scanner sc) {
        System.out.println("Please enter the absolute path for the output file");

        return sc.nextLine().trim();
    }

    protected void transform(Scanner sc, String outputFile, boolean checkTransform) throws TransformerException, IOException {
        var option = YES;
        if (checkTransform) {
            System.out.println("Do you want to perform any transformation (YES/NO)?");

            option = valueOf(sc.nextLine().toUpperCase());
        }
        switch (option) {
            case YES:
                System.out.println("Please enter the absolute path for the XSLT file");
                String xsltFile = sc.nextLine().trim();
                System.out.println("Please enter the absolute path for the output TeX file");
                String texFile = sc.nextLine().trim();
                runXSLTTransform(outputFile, texFile, xsltFile);
            case NO:
        }
    }

    public enum MainWindowOptions implements OptionsEnum {
        SELECT_ALGORITHMS("Select an Algorithm"), PROCESS_XSLT("Perform an XSLT Transform"), EXIT("Exit");
        private final String text;

        MainWindowOptions(String text) {
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
