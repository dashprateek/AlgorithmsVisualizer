package com.algo.algorithms.cfg;

import com.algo.algorithms.AlgorithmsRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CYKAlgorithm extends AlgorithmsRunner {

    private static final Function<String, Set<String>> rules = s -> new HashSet<>(Arrays.asList(s.split("\\-\\>")[1].split("\\|")));
    private static final Function<String, String> getVariable = s -> s.substring(0, 1);

    public static void run(Grammar grammar, final String s, String fileName) throws Exception {
        int n = s.length();
        Set<String>[][] V = new Set[n][n];
        Export export = new Export(grammar);
        runCYKAlgorithm(grammar, s, n, V, export);
        if (!V[0][n - 1].contains(grammar.getStart())) {
            System.out.println("String (" + s + ") cannot be produced from start node " + grammar.getStart() + " for this CFG.");
            return;
        }

        Set[][] newV = cloneArray(V, grammar.getStart());
        printMatrix(newV);

        List<TreeNode> op = new ArrayList<>();
        generateEncodingTrees(grammar, grammar.getStart(), s, 0,
                s.length() - 1,
                newV, op);
        printOutput(grammar, op);

        for (TreeNode root : op) {
            StringBuilder tree = new StringBuilder("[");
            inorderTraversal(root, tree);
            export.appendEncodingTrees(getEncoding(grammar, root), tree.append("]").toString());
        }

        export.writeToStream(fileName);
    }

    private static void printOutput(Grammar grammar, List<TreeNode> op) {
        System.out.println("output");
        op.stream().map(node -> getEncoding(grammar, node)).forEach(System.out::println);
        op.stream().map(node -> {
            StringBuilder str = new StringBuilder();
            inorderTraversal(node, str);
            return str.toString();
        }).forEach(System.out::println);
    }

    public static TreeNode generateEncodingTrees(Grammar grammar, String start, String s, int startIndex, int endIndex, Set<String>[][] V, List<TreeNode> op) {

        System.out.println("\n\nstart:" + start + " startIndex" + startIndex + " endIndex" + endIndex);
        if (startIndex == endIndex) {
            TreeNode node = new TreeNode(start);
            node.left = new TreeNode(String.valueOf(s.charAt(startIndex)));
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
                    TreeNode node = new TreeNode(start);


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


    private static void inorderTraversal(TreeNode node, StringBuilder str) {
        if (Objects.isNull(node))
            return;
        str.append(node.val);

        boolean brackets = Objects.nonNull(node.left) && Objects.nonNull(node.right);
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

    private static String getEncoding(Grammar grammar, TreeNode node) {
        StringBuilder postOrderTraversal = new StringBuilder();
        preOrderTraversal(node, postOrderTraversal, grammar);
        return postOrderTraversal.toString();
    }

    public static void preOrderTraversal(TreeNode node, StringBuilder str, Grammar grammar) {
        if (Objects.isNull(node))
            return;
        if (grammar.getTerminals().contains(node.val)) {
//            if (!str.toString().isBlank())
//                str.append(",");
            str.append(node.val);
        } else {
            boolean brackets = Objects.nonNull(node.left) && Objects.nonNull(node.right) && !grammar.getStart().equals(node.val);
            if (brackets)
//            if(str.charAt(str.length()-1)!='('){
                str.append('(');
//            }
            preOrderTraversal(node.left, str, grammar);
            preOrderTraversal(node.right, str, grammar);
            if (brackets)
                str.append(')');
        }
    }

    private static void runCYKAlgorithm(Grammar grammar, String s, int n, Set<String>[][] V, Export export) throws Exception {
        export.appendChildIteration(V);
        for (int i = 0; i < n; i++) {
            final int index = i;
            V[i][i] = grammar.getProductionRules().entrySet().stream()
                    .filter(e -> e.getValue().contains(String.valueOf(s.charAt(index))))
                    .collect(Collectors.mapping(Map.Entry::getKey, Collectors.toSet()));
        }
//        System.out.println("start");
//        print(V);
        for (int b = 0; b < n; b++) {
            for (int i = 0; i < n - b; i++) {
                final int k = i + b;
                if (Objects.isNull(V[i][k]))//Check should be present - report to professor
                    V[i][k] = new HashSet<>();
                for (int j = i; j < k; j++) { //report to professor
//                    System.out.println("i=" + i + " j=" + j + " k=" + k);
                    int finalI = i;
                    int finalJ = j;
                    for (Map.Entry<String, Set<String>> entry : grammar.getProductionRules().entrySet()) {
                        String var = entry.getKey();
                        Set<String> rules = entry.getValue();
                        for (String production : rules) {
                            if (grammar.getTerminals().contains(production))
                                continue;
                            for (Map.Entry<String, String> e : combinations(production)) {
                                if ((e.getKey().isBlank() || (Objects.nonNull(V[finalI][finalJ]) && V[finalI][finalJ].contains(e.getKey())))
                                        && (e.getValue().isBlank() || (Objects.nonNull(V[finalJ + 1][k]) && V[finalJ + 1][k].contains(e.getValue())))) {
                                    if (Objects.isNull(V[finalI][k])) {
                                        V[finalI][k] = new LinkedHashSet<>();
                                    }
                                    V[finalI][k].add(var);
//                                    System.out.println("i=" + finalI + " j=" + finalJ + " V[i][k]=" + V[finalI][k] + " l=" + rules + " e= " + e);
                                }
                            }
                        }
                    }
                }
//                System.out.println("i=" + i + " k=" + k);

//                print(V);
            }
            export.appendChildIteration(V);
        }
//        print(V);
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


    private static Set<Map.Entry<String, String>> combinations(String s) {
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
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
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

    private static class TreeNode implements Cloneable {
        String val;
        TreeNode left;
        TreeNode right;

        public TreeNode(String val) {
            this.val = val;
        }
    }

    public static class Export {
        private final Document doc;
        private final Element root;
        private final Element iterations;
        private final Element encodingTrees;

        public Export(Grammar grammar) throws Exception {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            root = doc.createElement("cyk");
            iterations = doc.createElement("iterations");
            encodingTrees = doc.createElement("encodingTrees");
            Element gNode = grammar.toXml(doc);
            root.appendChild(gNode);
        }

        public void writeToStream(String filename) throws Exception {
            root.appendChild(iterations);
            root.appendChild(encodingTrees);
            doc.appendChild(root);
            Transformer transformer = (new net.sf.saxon.TransformerFactoryImpl()).newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            StreamResult stream = new StreamResult(filename);
            transformer.transform(new DOMSource(doc), stream);
        }

        public void appendChildIteration(Set<String>[][] V) throws Exception {
            Element iteration = doc.createElement("iteration");
            Arrays.stream(V).forEach(v -> {
                Element rNode = doc.createElement("row");
                String text = Arrays.stream(v).map(Optional::ofNullable)
                        .map(op -> op.map(String::valueOf).orElse(" "))
                        .collect(Collectors.joining(";"));
//                text = text.replaceAll("\\[", "{").replaceAll("]", "}");
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
}



