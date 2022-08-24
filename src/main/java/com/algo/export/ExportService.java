package com.algo.export;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public abstract class ExportService {
    protected Document doc;
    protected Element root;

    protected ExportService(String root) throws ParserConfigurationException {
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.root = doc.createElement(root);
    }

    protected ExportService() throws ParserConfigurationException {
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    protected void initRoot(Element root) {
        this.root = root;
    }


    public abstract void writeToStream(String filename) throws Exception;

    protected void writeToFile(String filename) throws TransformerException {
        doc.appendChild(root);
        Transformer transformer = (new net.sf.saxon.TransformerFactoryImpl()).newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult stream = new StreamResult(filename);
        transformer.transform(new DOMSource(doc), stream);
    }

}
