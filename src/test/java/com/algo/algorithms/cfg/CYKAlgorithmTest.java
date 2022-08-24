package com.algo.algorithms.cfg;

import com.algo.export.XSLTService;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import static com.algo.export.XSLTService.*;
import static org.junit.Assert.assertTrue;

public class CYKAlgorithmTest {

    @Test
    public void testRun() throws Exception {
        Grammar grammar = Grammar.loadFromFile(XML_PATH + "cfg.xml");
        grammar.writeToStream(XML_PATH + "cfg.xml");
        String xmlFile = XML_PATH + "cyk.xml";
        CYKAlgorithm.run(grammar, "baaba", xmlFile);
        String texFile = TIK_PATH + "cyk.tex";
        String xsltFile = XSLT_PATH + "cyk.xslt";
        XSLTService.runXSLTTransform(xmlFile, texFile, xsltFile);
        assertTrue(Files.exists(Path.of(xsltFile)));
        assertTrue(Files.exists(Path.of(xmlFile)));
    }
}