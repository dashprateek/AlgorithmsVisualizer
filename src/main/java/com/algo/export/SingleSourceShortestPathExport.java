package com.algo.export;

import com.algo.models.SingleSourceShortestPathModel;
import com.algo.structure.Structure;
import org.w3c.dom.Element;

public class SingleSourceShortestPathExport extends ExportService {

    public SingleSourceShortestPathExport(SingleSourceShortestPathModel singleSourceShortestPath, Structure structure) throws Exception {
        super();
        initRoot(singleSourceShortestPath.toXml(doc));
        Element node = doc.createElement("graphml");
        node.appendChild(structure.toSimpleXml(doc));
        root.appendChild(node);
    }


    public void writeToStream(String filename) throws Exception {
        writeToFile(filename);
    }

}
