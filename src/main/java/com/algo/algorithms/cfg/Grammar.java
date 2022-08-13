package com.algo.algorithms.cfg;

import com.algo.plugins.XmlMarshallable;
import com.algo.plugins.XmlName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

@XmlName(name = "cfg")
public class Grammar extends XmlMarshallable {
    private final String start;
    private Set<String> variables;
    private Set<String> terminals;
    private Map<String, Set<String>> productionRules;

    public Grammar(String start, Set<String> variables, Set<String> terminals, Map<String, Set<String>> productionRules) {
        this.start = start;
        this.variables = variables;
        this.terminals = terminals;
        this.productionRules = productionRules;
    }

    public Grammar(String start) {
        this.start = start;
        this.variables = new HashSet<>();
        this.terminals = new HashSet<>();
        this.productionRules = new HashMap<>();
    }

    public static Grammar loadFromFile(String fileName) throws Exception {
        return loadFromStream(new FileInputStream(fileName));
    }

    public static Grammar loadFromStream(InputStream stream) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(stream);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        if (!root.getTagName().equalsIgnoreCase("cfg"))
            throw new Exception("Not a CFG file");
        Grammar grammar = new Grammar(root.getAttribute("start"));
        parseNode((Element) Optional.ofNullable(root.getElementsByTagName("variables").item(0))
                .orElseThrow(() -> new Exception("variables not found within cfg tag")), grammar.getVariables());

        parseNode((Element) Optional.ofNullable(root.getElementsByTagName("terminals").item(0))
                .orElseThrow(() -> new Exception("terminals not found within cfg tag")), grammar.getTerminals());

        parseProjectionRules((Element) Optional.ofNullable(root.getElementsByTagName("projectionRules").item(0))
                .orElseThrow(() -> new Exception("projectionRules not found within cfg tag")), grammar);
        return grammar;
    }

    private static void parseNode(Element child, Set<String> set) throws Exception {
        if (!set.isEmpty()) {
            throw new Exception("Multiple variables nodes found in file ");
        } else {
            set.addAll(Arrays.asList(child.getTextContent().split(",")));
        }
    }

    private static void parseProjectionRules(Element child, Grammar grammar) throws Exception {
        NodeList rules = child.getChildNodes();
        for (int j = 0; j < rules.getLength(); j++) {
            if (rules.item(j).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element rule = (Element) rules.item(j);
            if (!rule.getTagName().equalsIgnoreCase("rule")) {
                throw new Exception("Invalid node found within projectionRules with name " + rule.getTagName());
            }
            parseRules(grammar, rule);
        }
    }

    private static void parseRules(Grammar grammar, Element rule) {
        String[] str = rule.getTextContent().split("->");
        Set<String> rules = grammar.getProductionRules().getOrDefault(str[0], new HashSet<>());
        rules.addAll(Arrays.asList(str[1].split("\\|")));
        grammar.getProductionRules().put(str[0], rules);
    }

    public Set<String> getVariables() {
        return variables;
    }

    public void setVariables(Set<String> variables) {
        this.variables = variables;
    }

    public Set<String> getTerminals() {
        return terminals;
    }

    public void setTerminals(Set<String> terminals) {
        this.terminals = terminals;
    }

    public Map<String, Set<String>> getProductionRules() {
        return productionRules;
    }

    public void setProductionRules(Map<String, Set<String>> productionRules) {
        this.productionRules = productionRules;
    }

    public String getStart() {
        return start;
    }

    @Override
    public Element toXml(Document doc) throws Exception {
        Element root = super.toXml(doc);
        root.setAttribute("start", getStart());
        Element variables = doc.createElement("variables");
        variables.setTextContent(String.join(",", this.getVariables()));
        root.appendChild(variables);
        Element terminals = doc.createElement("terminals");
        terminals.setTextContent(String.join(",", this.getTerminals()));
        root.appendChild(terminals);
        Element projectionRules = doc.createElement("projectionRules");
        this.getProductionRules().forEach(projectionRulesToXml(doc, projectionRules));
        root.appendChild(projectionRules);
        return root;
    }

    private BiConsumer<String, Set<String>> projectionRulesToXml(Document doc, Element projectionRules) {
        return (k, v) -> {
            Element rule = doc.createElement("rule");
            rule.setTextContent(new StringBuilder(k).append("->").append(String.join("|", v)).toString());
            projectionRules.appendChild(rule);
        };
    }

    public void writeToStream(String fileName) throws Exception {
        writeToStream(new StreamResult(fileName));
    }

    public void writeToStream(StreamResult stream) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = toXml(doc);
        if (root == null)
            throw new Exception("Error writing to XML");
        doc.appendChild(root);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, stream);
    }
}
