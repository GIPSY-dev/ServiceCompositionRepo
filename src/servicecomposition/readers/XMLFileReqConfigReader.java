package servicecomposition.readers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import utilities.ReadWriteUtil;

/**
 * Concrete reader for reading composition request configuration from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLFileReqConfigReader extends FileReqConfigReader
{
	/**
	 * Method for reading composition request and service repository details from the user-specified XML file.
	 * @return	Request configuration object containing the input request and repository details
	 */
	public RequestConfiguration readReqConfig()
	{
		//Creating an XML document for the configuration file
		Document doc = ReadWriteUtil.getXmlDocument(configFileName);

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
	 * Method for fetching the value of the "value" attribute of an XML tag.
	 * @param 	tagName		Name of the tag element
	 * @param 	doc			Source XML document
	 * @return	Value of the target attribute
	 */
	public static String getTagValue(String tagName, Document doc)
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