package utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class XMLUtils {
    public XMLUtils() {
    }

    public void saveChanges(Document xml, String path) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource source = new DOMSource(xml);
        StreamResult result = new StreamResult(new File(path));
        transformer.transform(source, result);
    }

    public Document instantateXMLParser(String pathToXML) throws ParserConfigurationException, IOException, SAXException {
        System.out.println(pathToXML);
        File stringsXML = new File(pathToXML);
        if (!stringsXML.exists()) throw new RuntimeException("El archivo strings.xml no existe");
        Document parsedInstance = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stringsXML);
        parsedInstance.getDocumentElement().normalize();
        return parsedInstance;
    }
}