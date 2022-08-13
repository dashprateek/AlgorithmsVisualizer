package com.algo.export;

import com.algo.structure.Edge;
import com.algo.structure.Structure;
import com.algo.structure.Vertex;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class ExportUtils {
    public static final String ROOT_PATH = "/Users/prateekdash/Semester2/MscProjectPlan/MscProject";
    public static final String XML_PATH = ROOT_PATH + "/xml/";
    public static final String TIK_PATH = ROOT_PATH + "/tik/";
    public static final String XSLT_PATH = ROOT_PATH + "/xslt/";

    public static void runXSLTTransformGraphIterations(String inputFile, String outputTexFile) throws TransformerException, IOException {
        String xsltFile = XSLT_PATH + "Iterations.xslt";
        String outputXml = TIK_PATH + "temp.xml";
        xsltTransform(inputFile, outputXml, xsltFile);
        xmlToTex(outputXml, outputTexFile);
    }

    public static void runXSLTTransform(String inputFile, String outputTexFile, String xsltFile) throws TransformerException, IOException {
        String outputXml = TIK_PATH + "temp.xml";
        xsltTransform(inputFile, outputXml, xsltFile);
        xmlToTex(outputXml, outputTexFile);
    }

    public static void runXSLTTransformTable(String inputFile, String outputTexFile) throws TransformerException, IOException {
        String outputXml = TIK_PATH + "temp.xml";
        String xsltFile = XSLT_PATH + "cyk.xslt";
        xsltTransform(inputFile, outputXml, xsltFile);
        xmlToTex(outputXml, outputTexFile);
    }

    public static void xmlToTex(String outputXml, String outputTexFile) throws IOException {
        try (Scanner sc = new Scanner(Files.newInputStream(Path.of(outputXml)));
             PrintWriter printWriter = new PrintWriter(Files.newOutputStream(Path.of(outputTexFile)))) {
            boolean flag = true;
            while (sc.hasNextLine()) {
                if (flag) {
                    flag = false;
                    sc.nextLine();
                    continue;
                }
                printWriter.println(sc.nextLine()
                        .replaceAll("&amp;", "&")
                        .replaceAll("&gt;", ">"));
            }
        }
        new File(outputXml).delete();
    }

    public static void xsltTransform(String inputFile, String outputFile, String xsltFile) throws TransformerException {
//        TransformerFactory factory = TransformerFactory.newInstance();
        TransformerFactory factory = new net.sf.saxon.TransformerFactoryImpl();
        Source xslt = new StreamSource(new File(xsltFile));
        Transformer transformer = factory.newTransformer(xslt);
        Source text = new StreamSource(new File(inputFile));
        transformer.transform(text, new StreamResult(new File(outputFile)));
    }

    public static void exportStructureToXml(String structureFile, Structure<Vertex, Edge> structure) throws Exception {
        structure.getVertices().forEach(vertex -> {
            vertex.setCoordinates(Math.round(1000.0 * vertex.getCoordinates().getX()) / 1000.0,
                    Math.round(-1000.0 * vertex.getCoordinates().getY()) / 1000.0);
        });
        structure.writeToFile(structureFile, true);
    }
}
