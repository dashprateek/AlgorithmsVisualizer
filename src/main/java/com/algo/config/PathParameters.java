package com.algo.config;


import com.algo.parser.IntSyntaxChecker;
import com.algo.parser.SyntaxChecker;

import java.util.Arrays;
import java.util.List;

/*
 *
 */

public class PathParameters extends StringAlgorithmParametersList {
    public PathParameters(List<String> initialValues) {
        super(initialValues);

        this.labels = Arrays.asList("number of vertices", "directed");

        IntSyntaxChecker isc = new IntSyntaxChecker(1, Integer.MAX_VALUE);
        List<SyntaxChecker> syntaxCheckers = Arrays.asList(isc, null);


        List<String> explanations = Arrays.asList("The number of vertices in a path should be at least 1.", "");
        this.explanations = explanations;


    }
}
