package xmlTranslations;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import utils.XMLUtils;

public class TaskDumpStringsToTranslations extends DefaultTask {
    private final XMLUtils XMLUtils = new XMLUtils();

    private String stringsXMLPath = getProject().getProjectDir().getPath() + File.separatorChar + "res" + File.separatorChar + "values" + File.separatorChar + File.separatorChar + "strings.xml";
    private String translationsXMLPath = getProject().getProjectDir().getPath() + "res" + File.separatorChar + "xml" + File.separatorChar + "translations.xml";

    public TaskDumpStringsToTranslations() {
        super();
        setGroup("androidTools");
    }

    @TaskAction
    public void dumpStringsToTranslations() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document strings_xml = XMLUtils.instantateXMLParser(stringsXMLPath);
        Document translations_xml = XMLUtils.instantateXMLParser(translationsXMLPath);

        List<Node> missingTranslations = getListMissingTranslations(strings_xml, translations_xml);
        writeMissingTranslations(missingTranslations, translations_xml);
        replaceContentWithTranslationCode(missingTranslations, strings_xml);
        XMLUtils.saveChanges(translations_xml, translationsXMLPath);
        XMLUtils.saveChanges(strings_xml, stringsXMLPath);
    }

    private void replaceContentWithTranslationCode(List<Node> missingTranslations, Document strings_xml) {
        for (Node missingTranslation : missingTranslations) {
            missingTranslation.setTextContent(missingTranslation.getAttributes().getNamedItem("name").getNodeValue());
        }
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
}