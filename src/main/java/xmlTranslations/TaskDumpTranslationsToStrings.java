package xmlTranslations;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import utils.XMLUtils;

public class TaskDumpTranslationsToStrings extends DefaultTask {
    private final XMLUtils XMLUtils = new XMLUtils();

    private String stringsXMLPath = getProject().getProjectDir().getPath() + "\\res\\values\\strings.xml";
    private String translationsXMLPath = getProject().getProjectDir().getPath() + "\\res\\xml\\translations.xml";

    public TaskDumpTranslationsToStrings() {
        super();
        setGroup("androidTools");
    }

    @TaskAction
    public void dumpTranslationsToStrings() throws ParserConfigurationException, IOException, SAXException, TransformerException {
        Document strings_xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Document translations_xml = XMLUtils.instantateXMLParser(translationsXMLPath);

        Element resourcesElement = strings_xml.createElement("resources");
        strings_xml.normalizeDocument();
        strings_xml.appendChild(resourcesElement);

        NodeList translationsNodeList = translations_xml.getElementsByTagName("resource-entry");
        for (int i = 0; i < translationsNodeList.getLength(); i++) {
            Element stringElement = strings_xml.createElement("string");
            stringElement.setAttribute("name",  translationsNodeList.item(i).getAttributes().getNamedItem("resource-code").getNodeValue());
            stringElement.setTextContent(translationsNodeList.item(i).getAttributes().getNamedItem("resource-code").getNodeValue());
            resourcesElement.appendChild(stringElement);
        }

        XMLUtils.saveChanges(strings_xml, stringsXMLPath);
    }
}