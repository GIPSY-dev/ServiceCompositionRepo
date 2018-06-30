package translation.readers;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import servicecomposition.entities.CompositionRequest;
import utilities.ReadWriteUtil;

/**
 * Concrete reader for reading composite service input configuration from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLFileCSInpConfigReader extends FileCSInpConfigReader
{
	/**
	 * Method for reading composite service input values from the user-specified XML file.
	 * @param	compRequest		Composition request containing the list of inputs to be read 
	 * @return	Input configuration object containing the input values read from the file
	 */
	public CSInputConfiguration readCSInpConfig(CompositionRequest compRequest)
	{
		//Creating an XML document for the configuration file
		Document doc = ReadWriteUtil.getXmlDocument(configFileName);

		//Fetching composite service input details from configuration file
		List<String[]> inputDetails = getInputDetails(doc);
		
		//Creating a composite service input configuration object with the details fetched
		CSInputConfiguration csInpConfig = new CSInputConfiguration(inputDetails);
		
		return csInpConfig;
	}
	
	/**
	 * Method for parsing the configuration XML document to extract CS input details.
	 * @param 	doc		XML document for the CS input configuration file
	 * @return	List of all CS input details extracted from the XML document
	 */
	public List<String[]> getInputDetails(Document doc)
	{		
		//Fetching all the input nodes from the file
		NodeList inpNodeList = doc.getElementsByTagName("input");
		
		//Creating input detail records from the nodes
		List<String[]> inputDetails = new ArrayList<String[]>();
		for (int i = 0; i < inpNodeList.getLength(); i++)
		{
			String[] inpDtlRecord = new String[3];
			Node inpNode = inpNodeList.item(i);
			if (inpNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element inpElement = (Element) inpNode;
				inpDtlRecord[0] = getTagValue("name", inpElement);
				inpDtlRecord[1] = getTagValue("type", inpElement);
				inpDtlRecord[2] = getTagValue("value", inpElement);
				inputDetails.add(inpDtlRecord);
			}
		}
		
		return inputDetails;
	}
	
	/**
	 * Method for fetching the value of the "value" attribute of an XML tag.
	 * @param 	tagName		Name of the tag element
	 * @param 	elem		Container XML element node
	 * @return	Value of the target attribute
	 */
	private String getTagValue(String tagName, Element elem)
	{
		String tagValue = null;
		Node targetNode = elem.getElementsByTagName(tagName).item(0);
		if (targetNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element targetElement = (Element) targetNode;
			tagValue = targetElement.getAttribute("value");
		}
		
		return tagValue;
	}
}