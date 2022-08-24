package com.algo.export;

import org.w3c.dom.Element;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class IterationsExport extends ExportService {
    protected final Element iterations;

    protected IterationsExport(String root) throws ParserConfigurationException {
        super(root);
        iterations = doc.createElement("iterations");
    }

    @Override
    public void writeToStream(String filename) throws Exception {
        root.appendChild(iterations);
        writeToFile(filename);
    }

}
