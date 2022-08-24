package com.algo;

import com.algo.algorithms.cfg.CYKAlgorithm;
import com.algo.algorithms.cfg.Grammar;
import com.algo.algorithms.graph.shortestpath.BellmanFordAlgorithm;
import com.algo.algorithms.graph.shortestpath.DijkstrasAlgorithm;
import com.algo.algorithms.graph.shortestpath.FloydWarshallAlgorithm;
import com.algo.algorithms.graph.traversal.BFSTraversal;
import com.algo.algorithms.graph.traversal.DFSTraversal;
import com.algo.export.XSLTService;
import com.algo.structure.DirectedGraph;
import com.algo.structure.StructureUtils;
import com.algo.structure.UndirectedGraph;
import com.algo.structure.Vertex;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Scanner;

import static com.algo.export.XSLTService.*;
import static com.algo.structure.StructureUtils.getGraph;

public class Main {


    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            new MainRunner().run(scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Thank you for using the application! :)");
//      tempMain();
//        temp();
//        dijkstrasShortestPathTransforms("/Users/prateekdash/Semester2/MscProjectPlan/MscProject/xml/RandomStructureTest.xml");
//        graphTraversal("/Users/prateekdash/Semester2/MscProjectPlan/MscProject/xml/RandomStructureTest.xml");
    }


    private static void temp() {
        try {
            StructureUtils.getRandomGraph(true, true).writeToFile("/Users/prateekdash/Semester2/MscProjectPlan/MscProject/xml/RandomStructureTest.xml",true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Done! :)");
    }
    private static void tempMain() {
        try {
            allPairShortestPath(null);
            graphTraversal(null);
            dijkstrasShortestPathTransforms(null);
            allPairShortestPathBellmanFordAlgorithm();
            cykAlgorithm();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void cykAlgorithm() throws Exception {
        //        Grammar grammar = buildGrammar();
        Grammar grammar = Grammar.loadFromFile(XML_PATH + "cfg.xml");
        grammar.writeToStream(XML_PATH + "cfg.xml");
        String xmlFile = XML_PATH + "cyk.xml";
        CYKAlgorithm.run(grammar, "baaba", xmlFile);
        String texFile = TIK_PATH + "cyk.tex";
        String xsltFile = XSLT_PATH + "cyk.xslt";

        XSLTService.runXSLTTransform(xmlFile, texFile, xsltFile);

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

    private static void allPairShortestPath(String structureFile) throws Exception {
        String texFile = TIK_PATH + "floydWarshall.tex";
        String xsltFile = XSLT_PATH + "FloydWarshall.xslt";
        if(Optional.ofNullable(structureFile).map(String::isBlank).orElse(false))
            structureFile = XML_PATH + "fwGraph.xml";
        String xmlFile = XML_PATH + "fw.xml";
        DirectedGraph structure = (DirectedGraph) getGraph(structureFile);
        FloydWarshallAlgorithm.run(structure, xmlFile);
        runXSLTTransform(xmlFile, texFile, xsltFile);
    }

    private static void graphTraversal(String structureFile) throws Exception {
        if(Optional.ofNullable(structureFile).map(String::isBlank).orElse(false))
            structureFile = XML_PATH + "UndirectedGraph.xml";
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

    private static void dijkstrasShortestPathTransforms(String structureFile) throws Exception {

        String graphTexFile = TIK_PATH + "DijkstrasAlgorithm.tex";
        String xsltFile = XSLT_PATH + "DijkstrasAlgorithm.xslt";
        if(Optional.ofNullable(structureFile).map(String::isBlank).orElse(false))
            structureFile = XML_PATH + "directedGraph.xml";
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
