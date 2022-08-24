package com.algo.algorithms.graph.shortestpath;


import com.algo.structure.DirectedGraph;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.algo.export.XSLTService.*;
import static com.algo.structure.StructureUtils.getGraph;
import static org.junit.Assert.assertTrue;

public class BellmanFordAlgorithmTest {
    DirectedGraph structure;
    String texFile = TIK_PATH + "BellmanFordAlgorithm.tex";
    String xsltFile = XSLT_PATH + "BellmanFord.xslt";
    String structureFile = XML_PATH + "bellmanFord.xml";
    String xmlFile = XML_PATH + "BellmanFordAlgorithm.xml";

    @Before
    public void init() throws Exception {
        structure = (DirectedGraph) getGraph(structureFile);
    }

    @Test
    public void testRun() throws Exception {
        BellmanFordAlgorithm.run(structure, structure.getVertexById(5), xmlFile);
        runXSLTTransform(xmlFile, texFile, xsltFile);
        assertTrue(Files.exists(Path.of(xsltFile)));
        assertTrue(Files.exists(Path.of(xmlFile)));
    }
}