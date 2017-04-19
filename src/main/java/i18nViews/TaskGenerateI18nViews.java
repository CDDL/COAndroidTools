package i18nViews;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import utils.XMLUtils;

/**
 * Created by cdamian on 19/4/17.
 */

public class TaskGenerateI18nViews extends DefaultTask {

    private String mAttrsXMLPath = getProject().getProjectDir().getPath() + File.separatorChar + "res" + File.separatorChar + "values" + File.separatorChar + "attrs.xml";
    private XMLUtils mXMLUtils = new XMLUtils();
    private Document mXMLDocument;

    public TaskGenerateI18nViews() {
        super();
        setGroup("androidTools");
    }

    @TaskAction
    public void generateI18nViews() throws IOException, ParserConfigurationException, SAXException, TransformerException {
        if (dosAttrsFileExits()) {
            openAttrsFile();
//            if (i18nFontsAttrExists())
            updateI18nFontsAttr();
//            else addI18nFontsAttrs();
//            if (!i18nTextViewDeclareStyleableExists()) addI18nTextViewDeclareStyleable();
//            if (!i18nEditTextDeclareStyleableExists()) addI18nEditTextDeclareStyleable();
            saveEditions();
        } else {
//            createAttrsFile();
//            openAttrsFile();
//            addI18nFontsAttrs();
//            addI18nTextViewDeclareStyleable();
//            addI18nEditTextDeclareStyleable();
//            saveEditions();
        }
    }

    private void saveEditions() throws TransformerException {
        mXMLUtils.saveChanges(mXMLDocument, mAttrsXMLPath);
    }

    private void updateI18nFontsAttr() {
        NodeList attrNodes = mXMLDocument.getDocumentElement().getChildNodes();
        for (int i = 0; i < attrNodes.getLength(); i++) {
            Node node = attrNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && ((Element) node).getTagName().equals("attr")) {
                Element elementNode = (Element) node;
                if (elementNode.getAttribute("name").equals("I18n_fonts")) {
                    Element enumElement = mXMLDocument.createElement("enum");
                    enumElement.setAttribute("name", "montserrat_black");
                    enumElement.setAttribute("value", "0");
                    elementNode.appendChild(enumElement);
                }
            }
        }
    }

    private boolean i18nFontsAttrExists() {
        NodeList attrNodes = mXMLDocument.getChildNodes();
        for (int i = 0; i < attrNodes.getLength(); i++) {
            Node node = attrNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementNode = (Element) node;
                if (elementNode.getAttribute("name").equals("I18n_font")) return true;
            }
        }
        return false;
    }

    private void openAttrsFile() throws IOException, SAXException, ParserConfigurationException {
        mXMLDocument = mXMLUtils.instantateXMLParser(mAttrsXMLPath);
        mXMLDocument.getDocumentElement().normalize();

    }

    private void createAttrsFile() throws IOException {
        if (!new File(mAttrsXMLPath).createNewFile()) throw new IOException("Attrs file creation failed");
    }

    private boolean dosAttrsFileExits() {
        return new File(mAttrsXMLPath).exists();
    }
}
