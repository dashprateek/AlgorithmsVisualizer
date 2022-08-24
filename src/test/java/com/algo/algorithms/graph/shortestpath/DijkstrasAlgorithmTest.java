package com.algo.algorithms.graph.shortestpath;

import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.DirectedGraph;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static com.algo.export.XSLTService.*;
import static com.algo.structure.StructureUtils.getGraph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DijkstrasAlgorithmTest {
    String graphTexFile;
    String xsltFile;
    String structureFile;
    String dspFile;
    private DirectedGraph structure;

    @Before
    public void init() throws Exception {
        graphTexFile = TIK_PATH + "DijkstrasAlgorithm.tex";
        xsltFile = XSLT_PATH + "DijkstrasAlgorithm.xslt";
        structureFile = XML_PATH + "directedGraph.xml";
        dspFile = XML_PATH + "dsp.xml";
        structure = (DirectedGraph) getGraph(structureFile);
    }

    @Test
    public void run() throws Exception {
        SingleSourceShortestPathModel sssp = DijkstrasAlgorithm.run(structure, structure.getVertexById(0), dspFile);
        System.out.println(sssp);
        assertEquals(Lists.newArrayList(0.0, 1.0, 4.0, 99999.0, 99999.0, 99999.0), sssp.getShortestPaths().get(0).getDistances());
        assertEquals(Lists.newArrayList(0.0, 1.0, 2.0, 4.0, 3.0, 5.0), sssp.getShortestPaths().get(sssp.getShortestPaths().size() - 1).getDistances());
        runXSLTTransform(dspFile, graphTexFile, xsltFile);
        assertTrue(Files.exists(Path.of(graphTexFile)));
        assertTrue(Files.exists(Path.of(dspFile)));
    }

}