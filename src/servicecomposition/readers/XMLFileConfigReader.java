package servicecomposition.readers;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Concrete reader for reading composition request configuration from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLFileConfigReader extends FileConfigReader
{
	/**
	 * Method for reading composition request and service repository details from the user-specified XML file.
	 * @return	Request configuration object containing the input request and repository details
	 */
	public RequestConfiguration readReqConfig()
	{
		//Creating an XML document for the configuration file
		Document doc = getXmlDocument(configFileName);

		//Fetching composition request components and service repository file location from configuration file
		String inputString = getTagValue("inputs", doc);
		String outputString = getTagValue("outputs", doc);
		String qosString = getTagValue("qos", doc);
		String constraintString = getTagValue("constraints", doc);
		String repoFileName = getTagValue("repofilename", doc);
		
		//Creating a Request Configuration object with the details fetched
		RequestConfiguration reqConfig = new RequestConfiguration(inputString, outputString, qosString, constraintString, repoFileName);
		
		return reqConfig;
	}
	
	/**
	 * Method for creating an XML document for the XML configuration file.
	 * @param	xmlFileName		Complete name and location of the XML configuration file
	 * @return	XML document for the configuration file
	 */
	private static Document getXmlDocument(String xmlFileName)
	{
		File xmlFile = new File(xmlFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try 
		{
			dBuilder = dbFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException pce) 
		{
			System.out.println("Exception while parsing XML configuration file: " + pce.getMessage());
		}
		
		Document doc = null;
		try 
		{
			doc = dBuilder.parse(xmlFile);
		}
		catch (SAXException se) 
		{
			System.out.println("Exception while parsing XML configuration file: " + se.getMessage());
		} 
		catch (IOException ioe) 
		{
			System.out.println("Exception while parsing XML configuration file: " + ioe.getMessage());
		}
		
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	/**
	 * Method for fetching the value of the "value" attribute of an XML tag.
	 * @param 	tagName		Name of the tag element
	 * @param 	doc			Source XML document
	 * @return	Value of the target attribute
	 */
	private String getTagValue(String tagName, Document doc)
	{
		String tagValue = null;
		Node targetNode = doc.getElementsByTagName(tagName).item(0);
		if (targetNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element targetElement = (Element) targetNode;
			tagValue = targetElement.getAttribute("value");
		}
		
		return tagValue;
	}
}