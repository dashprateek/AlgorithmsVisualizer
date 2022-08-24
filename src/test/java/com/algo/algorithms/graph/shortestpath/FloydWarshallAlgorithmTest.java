package com.algo.algorithms.graph.shortestpath;

import com.algo.structure.DirectedGraph;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.algo.export.XSLTService.*;
import static com.algo.structure.StructureUtils.getGraph;
import static org.junit.Assert.assertTrue;

public class FloydWarshallAlgorithmTest {

    String texFile = TIK_PATH + "floydWarshall.tex";
    String xsltFile = XSLT_PATH + "FloydWarshall.xslt";
    String structureFile = XML_PATH + "fwGraph.xml";
    String xmlFile = XML_PATH + "fw.xml";
    DirectedGraph structure;

    @Before
    public void init() throws Exception {
        structure = (DirectedGraph) getGraph(structureFile);
    }

    @Test
    public void testRun() throws Exception {
        FloydWarshallAlgorithm.run(structure, xmlFile);
        runXSLTTransform(xmlFile, texFile, xsltFile);
        assertTrue(Files.exists(Path.of(texFile)));
        assertTrue(Files.exists(Path.of(xmlFile)));
    }
}