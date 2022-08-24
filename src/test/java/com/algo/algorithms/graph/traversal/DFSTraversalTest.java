package com.algo.algorithms.graph.traversal;

import com.algo.structure.UndirectedGraph;
import com.algo.structure.Vertex;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.algo.export.XSLTService.*;
import static com.algo.export.XSLTService.runXSLTTransform;
import static com.algo.structure.StructureUtils.getGraph;
import static org.junit.Assert.assertTrue;

public class DFSTraversalTest {
    public String structureFile = XML_PATH + "UndirectedGraph.xml";
    public String dfsTexFile = TIK_PATH + "dfsIterations.tex";
    public String dfsFileName = XML_PATH + "dfsIterations.xml";
    public String xsltFile = XSLT_PATH + "GraphTraversal.xslt";
    public UndirectedGraph structure;

    @Before
    public void graphTraversal() throws Exception {
        structure = (UndirectedGraph) getGraph(structureFile);
    }

    @Test
    public void testRun() throws Exception {
        Vertex root = structure.getVertexById(3);
        DFSTraversal.run(structure, root, dfsFileName);
        runXSLTTransform(dfsFileName, dfsTexFile, xsltFile);
        assertTrue(Files.exists(Path.of(xsltFile)));
        assertTrue(Files.exists(Path.of(dfsFileName)));
    }

}