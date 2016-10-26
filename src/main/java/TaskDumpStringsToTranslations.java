
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class TaskDumpStringsToTranslations extends DefaultTask {
    String stringsXMLPath = getProject().getProjectDir().getPath() + "\\res\\values\\strings.xml";
    String translationsXMLPath = getProject().getProjectDir().getPath() + "\\res\\xml\\translations.xml";

    public TaskDumpStringsToTranslations() {
        super();
        setGroup("androidTools");
    }

    @TaskAction
    public void dumpStringsToTranslations() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document strings_xml = instantateXMLParser(stringsXMLPath);
        Document translations_xml = instantateXMLParser(translationsXMLPath);

        List<Node> missingTranslations = getListMissingTranslations(strings_xml, translations_xml);
        writeMissingTranslations(missingTranslations, translations_xml);
        replaceContentWithTranslationCode(missingTranslations, strings_xml);
        saveChanges(translations_xml, translationsXMLPath);
        saveChanges(strings_xml, stringsXMLPath);
    }

    private void replaceContentWithTranslationCode(List<Node> missingTranslations, Document strings_xml) {
        for (Node missingTranslation : missingTranslations) {
            missingTranslation.setTextContent(missingTranslation.getAttributes().getNamedItem("name").getNodeValue());
        }
    }

    private void saveChanges(Document xml, String path) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(xml);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }

    private void writeMissingTranslations(List<Node> missingTranslations, Document translations_xml) {
        for (Node missingTranslation : missingTranslations) {
            Element valueElement = translations_xml.createElement("value");
            valueElement.setTextContent(missingTranslation.getTextContent());

            Element languageEntryElement = translations_xml.createElement("language-entry");
            languageEntryElement.setAttribute("language", "es");
            languageEntryElement.appendChild(valueElement);

            Element resourceEntryElement = translations_xml.createElement("resource-entry");
            resourceEntryElement.setAttribute("resource-code", missingTranslation.getAttributes().getNamedItem("name").getNodeValue());
            resourceEntryElement.appendChild(languageEntryElement);

            translations_xml.getDocumentElement().appendChild(resourceEntryElement);
        }
    }

    private List<Node> getListMissingTranslations(Document strings_xml, Document translations_xml) {
        LinkedList<Node> listNodes = new LinkedList<>();

        NodeList stringNodeList = strings_xml.getElementsByTagName("string");
        NodeList translationsNodeList = translations_xml.getElementsByTagName("resource-entry");

        for (int i = 0; i < stringNodeList.getLength(); i++) {
            if (!hasTranslation(translationsNodeList, stringNodeList.item(i))) listNodes.add(stringNodeList.item(i));
        }

        return listNodes;
    }

    private boolean hasTranslation(NodeList translationsNodeList, Node node) {
        for (int i = 0; i < translationsNodeList.getLength(); i++) {
            if (translationsNodeList.item(i).getAttributes().getNamedItem("resource-code").getNodeValue().
                    equals(node.getAttributes().getNamedItem("name").getNodeValue())) return true;
        }
        return false;
    }


    private Document instantateXMLParser(String pathToXML) throws ParserConfigurationException, IOException, SAXException {
        File stringsXML = new File(pathToXML);
        if (!stringsXML.exists()) throw new RuntimeException("El archivo strings.xml no existe");
        Document parsedInstance = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringsXML);
        parsedInstance.getDocumentElement().normalize();
        return parsedInstance;
    }
}