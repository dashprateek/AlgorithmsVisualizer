package com.algo.algorithms.cfg;

import com.algo.algorithms.AlgorithmsRunner;
import com.algo.export.CYKAlgorithmsExport;

import java.util.*;
import java.util.stream.Collectors;

public class CYKAlgorithm extends AlgorithmsRunner {

    public static void run(Grammar grammar, final String s, String fileName) throws Exception {
        int n = s.length();
        Set<String>[][] memorizationTable = new Set[n][n];
        CYKAlgorithmsExport CYKAlgorithmsExport = new CYKAlgorithmsExport(grammar);
        runCYKAlgorithm(grammar, s, n, memorizationTable, CYKAlgorithmsExport);
        if (!memorizationTable[0][n - 1].contains(grammar.getStart())) {
            System.out.println("String (" + s + ") cannot be produced from start node " + grammar.getStart() + " for this CFG.");
            return;
        }

        Set[][] newV = cloneArray(memorizationTable, grammar.getStart());
        printMatrix(newV);

        List<BTreeNode> bTrees = new ArrayList<>();
        generateEncodingTrees(grammar, grammar.getStart(), s, 0,
                s.length() - 1,
                newV, bTrees);
        printOutput(grammar, bTrees);

        for (BTreeNode root : bTrees) {
            StringBuilder tree = new StringBuilder("[");
            inorderTraversal(root, tree);
            CYKAlgorithmsExport.appendEncodingTrees(getEncoding(grammar, root), tree.append("]").toString());
        }

        CYKAlgorithmsExport.writeToStream(fileName);
    }

    private static void printOutput(Grammar grammar, List<BTreeNode> op) {
        System.out.println("output");
        op.stream().map(node -> getEncoding(grammar, node)).forEach(System.out::println);
        op.stream().map(node -> {
            StringBuilder str = new StringBuilder();
            inorderTraversal(node, str);
            return str.toString();
        }).forEach(System.out::println);
    }

    public static BTreeNode generateEncodingTrees(Grammar grammar, String start, String s, int startIndex, int endIndex, Set<String>[][] V, List<BTreeNode> op) {
        System.out.println("\n\nstart:" + start + " startIndex" + startIndex + " endIndex" + endIndex);
        if (startIndex == endIndex) {
            BTreeNode node = new BTreeNode(start);
            node.left = new BTreeNode(String.valueOf(s.charAt(startIndex)));
            System.out.println(s.charAt(startIndex));
            return node;
        }
        for (int i = startIndex; i < endIndex; i++) {
            if (!V[startIndex][i].isEmpty() && !V[i + 1][endIndex].isEmpty()) {
                System.out.println("start:" + start + " startIndex:" + startIndex + " i:" + i + " endIndex:" + endIndex + " " + V[startIndex][i] + "  " + V[i + 1][endIndex]);
                Optional<String> ruleOptional = getCombinations(V[startIndex][i], V[i + 1][endIndex], start).stream()
                        .filter(l -> grammar.getProductionRules().get(start).contains(l))
                        .findAny();
                System.out.println("combinations " + ruleOptional);
                if (ruleOptional.isPresent()) {
                    BTreeNode node = new BTreeNode(start);
                    String rule = ruleOptional.get();
                    System.out.println("rule:" + rule);
                    node.left = generateEncodingTrees(grammar, rule.substring(0, 1), s,
                            startIndex, i, V, op);
                    node.right = generateEncodingTrees(grammar, rule.substring(1, 2), s,
                            i + 1, endIndex, V, op);
                    System.out.println("\n\n\n");
                    if (grammar.getStart().equals(start))
                        op.add(node);
                    else
                        return node;
                }
            }
        }
        return null;
    }

    static Set<String> getCombinations(Set<String> s1, Set<String> s2, String start) {
        Set<String> combinations = new HashSet<>();
        for (String i : s1) {
            for (String j : s2) {
                combinations.add(i + j);
            }
        }
        System.out.println("getCombinations" + combinations);
        return combinations;
    }


    private static void inorderTraversal(BTreeNode node, StringBuilder str) {
        if (Objects.isNull(node))
            return;
        str.append(node.val);
        if (Objects.nonNull(node.left))
            str.append("[");
        inorderTraversal(node.left, str);
        if (Objects.nonNull(node.left))
            str.append("]");
        if (Objects.nonNull(node.right))
            str.append("[");
        inorderTraversal(node.right, str);
        if (Objects.nonNull(node.right))
            str.append("]");
    }

    private static String getEncoding(Grammar grammar, BTreeNode node) {
        StringBuilder preOrderTraversal = new StringBuilder();
        preOrderTraversal(node, preOrderTraversal, grammar);
        return preOrderTraversal.toString();
    }

    public static void preOrderTraversal(BTreeNode node, StringBuilder str, Grammar grammar) {
        if (Objects.isNull(node))
            return;
        if (grammar.getTerminals().contains(node.val))
            str.append(node.val);
        else {
            boolean brackets = Objects.nonNull(node.left) && Objects.nonNull(node.right) && !grammar.getStart().equals(node.val);
            if (brackets)
                str.append('(');
            preOrderTraversal(node.left, str, grammar);
            preOrderTraversal(node.right, str, grammar);
            if (brackets)
                str.append(')');
        }
    }

    private static void runCYKAlgorithm(Grammar grammar, String s, int n, Set<String>[][] memoTable, CYKAlgorithmsExport CYKAlgorithmsExport) throws Exception {
        CYKAlgorithmsExport.appendChildIteration(memoTable);
        for (int i = 0; i < n; i++) {
            final int index = i;
            memoTable[i][i] = grammar.getProductionRules().entrySet().stream()
                    .filter(e -> e.getValue().contains(String.valueOf(s.charAt(index)))).map(Map.Entry::getKey).collect(Collectors.toSet());
        }
        for (int b = 0; b < n; b++) {
            for (int i = 0; i < n - b; i++) {
                final int k = i + b;
                if (Objects.isNull(memoTable[i][k]))
                    memoTable[i][k] = new HashSet<>();
                for (int j = i; j < k; j++) {
                    final int finalI = i, finalJ = j;
                    for (Map.Entry<String, Set<String>> entry : grammar.getProductionRules().entrySet()) {
                        String var = entry.getKey();
                        Set<String> rules = entry.getValue();
                        for (String production : rules) {
                            if (grammar.getTerminals().contains(production))
                                continue;
                            for (Map.Entry<String, String> e : getCombinations(production)) {
                                if ((e.getKey().isBlank() || (Objects.nonNull(memoTable[finalI][finalJ]) && memoTable[finalI][finalJ].contains(e.getKey())))
                                        && (e.getValue().isBlank() || (Objects.nonNull(memoTable[finalJ + 1][k]) && memoTable[finalJ + 1][k].contains(e.getValue())))) {
                                    if (Objects.isNull(memoTable[finalI][k])) {
                                        memoTable[finalI][k] = new LinkedHashSet<>();
                                    }
                                    memoTable[finalI][k].add(var);
                                }
                            }
                        }
                    }
                }
            }
            CYKAlgorithmsExport.appendChildIteration(memoTable);
        }
    }

    //TODO Remove this method
    private static Grammar buildGrammar() {
        Set<String> v = Arrays.stream("S,A,B,C".split(",")).collect(Collectors.toSet());
        Set<String> t = Arrays.stream("a,b".split(",")).collect(Collectors.toSet());

        Map<String, Set<String>> pr = Arrays.stream("S->AB|BC,A->BA|a,B->CC|b,C->AB|a".split(","))
                .map(s -> s.split("->"))
                .map(s -> Map.entry(s[0], new HashSet<>(Arrays.asList(s[1].split("\\|")))))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                }));
        Grammar grammar = new Grammar("S", v, t, pr);
        return grammar;
    }

    //TODO Remove this method
    private static void printMatrix(Set[][] V) {
        int i = 0;
        for (Set[] t : V) {
            System.out.println(i++ + " " + Arrays.toString(t));
        }
        System.out.println();
    }


    private static Set<Map.Entry<String, String>> getCombinations(String s) {
        Set<Map.Entry<String, String>> combinations = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            combinations.add(Map.entry(s.substring(0, i), s.substring(i)));
        }
        return combinations;
    }

    private static Set<String>[][] cloneArray(Set<String>[][] V, String start) {
        Set<String>[][] newV = new Set[V.length][V.length];
        for (int i = 0; i < newV.length; i++)
            for (int j = 0; j < newV[i].length; j++) {
                newV[i][j] = V[i][j];
                if (Objects.nonNull(newV[i][j]))
                    newV[i][j].remove(start);
            }

        return newV;
    }

    @Override
    public boolean run(Scanner sc) {
        try {
            Grammar grammar = getGrammar(sc);
            String outputFile = getOutputFile(sc);
            String inputString = getParsingString(sc);
            run(grammar, inputString, outputFile);
            transform(sc, outputFile, true);
        } catch (Exception e) {
            System.out.println("The execution of the algorithm terminates with the following stack trace:");
            e.printStackTrace();
            System.out.println("Please try again");
        }
        return false;
    }

    private String getParsingString(Scanner sc) {
        System.out.println("Please enter the String to be parsed:");
        return sc.nextLine();
    }

    private Grammar getGrammar(Scanner sc) throws Exception {
        System.out.println("Please enter the absolute path for the CFG file");

        String cfgFile = sc.nextLine();
        return Grammar.loadFromFile(cfgFile.trim());
    }

    private static class BTreeNode implements Cloneable {
        String val;
        BTreeNode left;
        BTreeNode right;

        public BTreeNode(String val) {
            this.val = val;
        }
    }

}



