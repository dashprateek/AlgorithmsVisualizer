package com.algo.export;

import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.Structure;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class ShortestPathIterationsExport {
    private final Document doc;
    private final Element root;

    public ShortestPathIterationsExport(SingleSourceShortestPathModel singleSourceShortestPath, Structure structure) throws Exception {
        doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        root = singleSourceShortestPath.toXml(doc);
        Element snode = doc.createElement("graphml");
        snode.appendChild(structure.toSimpleXml(doc));
        root.appendChild(snode);
    }


    public void writeToStream(String filename) throws Exception {
        doc.appendChild(root);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult stream = new StreamResult(filename);
        transformer.transform(new DOMSource(doc), stream);
    }

}
