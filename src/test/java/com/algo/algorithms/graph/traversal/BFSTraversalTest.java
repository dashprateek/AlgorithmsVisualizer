package com.algo.algorithms.graph.traversal;

import com.algo.structure.UndirectedGraph;
import com.algo.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.algo.export.XSLTService.*;
import static com.algo.structure.StructureUtils.getGraph;
import static org.junit.Assert.assertTrue;

public class BFSTraversalTest{

    public String structureFile = XML_PATH + "UndirectedGraph.xml";
    public String bfsTexFile = TIK_PATH + "bfsIterations.tex";
    public String bfsFileName = XML_PATH + "bfs.xml";
    public String xsltFile = XSLT_PATH + "GraphTraversal.xslt";
    public UndirectedGraph structure;

    @Before
    public void graphTraversal() throws Exception {
        structure = (UndirectedGraph) getGraph(structureFile);
    }

    @Test
    public void testRun() throws Exception {
        Vertex root = structure.getVertexById(3);
        BFSTraversal.run(structure, root, bfsFileName);
        runXSLTTransform(bfsFileName, bfsTexFile, xsltFile);
        assertTrue(Files.exists(Path.of(xsltFile)));
        assertTrue(Files.exists(Path.of(bfsFileName)));
    }
}