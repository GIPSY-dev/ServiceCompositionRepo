package translation.readers;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import service.Service;
import utilities.CompSvcStorageUtil;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Concrete reader for reading composite service configuration from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLFileCSConfigReader extends FileCSConfigReader
{
	/**
	 * Method for reading composite service source file location and its input values from the user-specified XML file.
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	Composite service configuration object containing the information read from the file
	 */
	public CSConfiguration readCSConfig(LogUtil logger)
	{
		//Creating an XML document for the configuration file
		Document doc = ReadWriteUtil.getXmlDocument(configFileName);

		//Fetching composite service details from the configuration file
		String csRepoFileName = ReadWriteUtil.getTagValue("csrepofilename", doc);	
		String destFolderName = csRepoFileName.substring(0, (csRepoFileName.lastIndexOf("/") + 1));
		String csName = ReadWriteUtil.getTagValue("csname", doc);
		List<String[]> inputDetails = getInputDetails(doc);
		
		//Parsing the source file to get the composite service object
		Service compService = CompSvcStorageUtil.readCSFromSerialFile(csRepoFileName, csName, logger);
		if (compService == null)
		{
			return null;
		}
		
		//Creating a composite service configuration object with the details fetched
		CSConfiguration csConfig = new CSConfiguration(compService, inputDetails, destFolderName);
		
		return csConfig;
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
				inpDtlRecord[0] = getElemTagValue("name", inpElement);
				inpDtlRecord[1] = getElemTagValue("type", inpElement);
				inpDtlRecord[2] = getElemTagValue("value", inpElement);
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
	private String getElemTagValue(String tagName, Element elem)
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