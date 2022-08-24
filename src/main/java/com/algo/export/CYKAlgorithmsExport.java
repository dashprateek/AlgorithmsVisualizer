package com.algo.export;

import com.algo.algorithms.cfg.Grammar;
import org.w3c.dom.Element;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CYKAlgorithmsExport extends IterationsExport {
    private final Element encodingTrees;

    public CYKAlgorithmsExport(Grammar grammar) throws Exception {
        super("cyk");
        encodingTrees = doc.createElement("encodingTrees");
        root.appendChild(grammar.toXml(doc));
    }

    @Override
    public void writeToStream(String filename) throws Exception {
        root.appendChild(encodingTrees);
        super.writeToStream(filename);
    }

    public void appendChildIteration(Set<String>[][] V) throws Exception {
        Element iteration = doc.createElement("iteration");
        Arrays.stream(V).forEach(v -> {
            Element rNode = doc.createElement("row");
            String text = Arrays.stream(v).map(Optional::ofNullable)
                    .map(op -> op.map(String::valueOf).orElse(" "))
                    .collect(Collectors.joining(";"));
            rNode.setTextContent(text);
            iteration.appendChild(rNode);
        });
        iterations.appendChild(iteration);
    }

    public void appendEncodingTrees(String encoding, String tree) throws Exception {
        Element encodingTree = doc.createElement("encodingTree");
        encodingTree.setAttribute("encoding", encoding);
        encodingTree.setTextContent(tree);
        encodingTrees.appendChild(encodingTree);
    }
}
