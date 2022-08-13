package com.algo;

import com.algo.algorithms.cfg.CYKAlgorithm;
import com.algo.algorithms.cfg.Grammar;
import com.algo.algorithms.graph.shortestpath.BellmanFordAlgorithm;
import com.algo.algorithms.graph.shortestpath.DijkstrasAlgorithm;
import com.algo.algorithms.graph.shortestpath.FloydWarshallAlgorithm;
import com.algo.algorithms.graph.traversal.BFSTraversal;
import com.algo.algorithms.graph.traversal.DFSTraversal;
import com.algo.export.ExportUtils;
import com.algo.structure.DirectedGraph;
import com.algo.structure.UndirectedGraph;
import com.algo.structure.Vertex;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import static com.algo.export.ExportUtils.*;
import static com.algo.structure.StructureUtils.getGraph;

public class Main {


    public static void main(String[] args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        try (Scanner scanner = new Scanner(System.in)) {
            new MainRunner().run(scanner);
        } catch (IOException | TransformerException e) {
            e.printStackTrace();
        }
        System.out.println("Thank you for using the application! :)");
//        System.out.println(Algorithms.class);
//        Algorithms type = AlgorithmsEnum
//        Enum<?> classType[] = Algorithms.values();
//        classType.getDeclaringClass()
//        try (Scanner scanner = new Scanner(System.in)) {
//            while (true) {
//                System.out.println("Please Enter the number next to the Algorithm to be used:\n " +
//                        "Press - to return or * to exit");
//                List<String> inputs = new ArrayList<>();
//                for (Algorithms algorithm : Algorithms.values()) {
//                    System.out.println(algorithm.ordinal() + " " + algorithm.getType());
//                    inputs.add(String.valueOf(algorithm.ordinal()));
//                }
//
//                String ip = scanner.nextLine();
//                if(inputs.contains(ip)){
//                    System.out.println(Algorithms.valueOf(type[Integer.valueOf(ip)].name()));
//                }else if("-".equals(ip)){
//
//                }else if("*".equals(ip)){
//                    break;
//                }else{
//
//                }
//
//                break;
//            }
//        }
    }

    private static void tempMain() {
        try {
            allPairShortestPath();
            graphTraversal();
            dijkstrasShortestPathTransforms();
            allPairShortestPathBellmanFordAlgorithm();
            cykAlgorithm();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done! :)");
    }

    private static void cykAlgorithm() throws Exception {
        //        Grammar grammar = buildGrammar();
        Grammar grammar = Grammar.loadFromFile(XML_PATH + "cfg.xml");
        grammar.writeToStream(XML_PATH + "cfg.xml");
        String xmlFile = XML_PATH + "cyk.xml";
        CYKAlgorithm.run(grammar, "baaba", xmlFile);
        String texFile = TIK_PATH + "cyk.tex";
        String xsltFile = XSLT_PATH + "cyk.xslt";

        ExportUtils.runXSLTTransform(xmlFile, texFile, xsltFile);

    }

    private static void allPairShortestPathBellmanFordAlgorithm() throws Exception {
        String texFile = TIK_PATH + "BellmanFordAlgorithm.tex";
        String xsltFile = XSLT_PATH + "BellmanFord.xslt";
        String structureFile = XML_PATH + "bellmanFord.xml";
        String xmlFile = XML_PATH + "BellmanFordAlgorithm.xml";
        DirectedGraph structure = (DirectedGraph) getGraph(structureFile);
        BellmanFordAlgorithm.run(structure, structure.getVertexById(5), xmlFile);
        runXSLTTransform(xmlFile, texFile, xsltFile);
    }

    private static void allPairShortestPath() throws Exception {
        String texFile = TIK_PATH + "floydWarshall.tex";
        String xsltFile = XSLT_PATH + "FloydWarshall.xslt";
        String structureFile = XML_PATH + "fwGraph.xml";
        String xmlFile = XML_PATH + "fw.xml";
        DirectedGraph structure = (DirectedGraph) getGraph(structureFile);
        FloydWarshallAlgorithm.run(structure, xmlFile);
        runXSLTTransform(xmlFile, texFile, xsltFile);
    }

    private static void graphTraversal() throws Exception {
        String structureFile = XML_PATH + "UndirectedGraph.xml";
        UndirectedGraph structure = (UndirectedGraph) getGraph(structureFile);
        String bfsTexFile = TIK_PATH + "bfsIterations.tex";
        String dfsTexFile = TIK_PATH + "dfsIterations.tex";
        Vertex root = structure.getVertexById(3);
        String bfsFileName = XML_PATH + "bfs.xml";
        String dfsFileName = XML_PATH + "dfsIterations.xml";
        BFSTraversal.run(structure, root, bfsFileName);
        DFSTraversal.run(structure, root, dfsFileName);
        String xsltFile = XSLT_PATH + "GraphTraversal.xslt";
        runXSLTTransform(bfsFileName, bfsTexFile, xsltFile);
        runXSLTTransform(dfsFileName, dfsTexFile, xsltFile);
    }

    private static void dijkstrasShortestPathTransforms() throws Exception {
        String graphTexFile = TIK_PATH + "DijkstrasAlgorithm.tex";
        String xsltFile = XSLT_PATH + "DijkstrasAlgorithm.xslt";
        String structureFile = XML_PATH + "directedGraph.xml";
        String dspFile = XML_PATH + "dsp.xml";
        DirectedGraph structure = (DirectedGraph) getGraph(structureFile);
        DijkstrasAlgorithm.run(structure, structure.getVertexById(0), dspFile);
        runXSLTTransform(dspFile, graphTexFile, xsltFile);
    }


//    private static void graphTraversals() throws Exception {
//        String structureFile = XML_PATH + "UndirectedGraph.xml";
//        UndirectedGraph structure = (UndirectedGraph) getGraph(structureFile);
//        exportStructureToXml(structureFile, structure);
//        String graphTexFile = TIK_PATH + "UndirectedGraph.tex";
//        runXSLTTransformGraph(structureFile, graphTexFile);
//        structure = (UndirectedGraph) getGraph(structureFile);
//        Vertex root = structure.getVertexById(3);
//        String bfsFileName = XML_PATH + "bfsIterations.xml";
//        String dfsFileName = XML_PATH + "dfsIterations.xml";
//
//        GraphTraversals.BFS(structure, root, bfsFileName);
//        structure = (UndirectedGraph) getGraph(structureFile);
//        root = structure.getVertexById(3);
//        GraphTraversals.DFS(structure, root, dfsFileName);
//
//        String bfsIterationsTexFile = TIK_PATH + "bfsIterations.tex";
//        String dfsIterationsTexFile = TIK_PATH + "dfsIterations.tex";
//
//        runXSLTTransformGraphIterations(bfsFileName, bfsIterationsTexFile);
//        runXSLTTransformGraphIterations(dfsFileName, dfsIterationsTexFile);
//    }


}
