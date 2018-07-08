package translation.readers.csconfigreaders;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import service.Service;
import translation.readers.csreaders.CompositeServiceReader;
import translation.readers.csreaders.SerializedCSReader;
import translation.readers.csreaders.XMLCSReader;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Concrete reader for reading composite service configuration from an XML file.
 * @author Jyotsana Gupta
 */
public class XMLFileCSConfigReader extends FileCSConfigReader
{
	/**
	 * Method for reading composite service translation details from the user-specified XML file.
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	Composite service configuration object containing the information read from the file
	 */
	public CSConfiguration readCSConfig(LogUtil logger)
	{
		//Creating an XML document for the configuration file
		Document doc = ReadWriteUtil.getXmlDocument(configFileName);

		//Fetching composite service details from the configuration file
		String csRepoFileName = ReadWriteUtil.getXMLTagValue("csrepofilename", "value", doc, null);	
		String destFolderName = csRepoFileName.substring(0, (csRepoFileName.lastIndexOf("/") + 1));
		String csName = ReadWriteUtil.getXMLTagValue("csname", "value", doc, null);
		String targetLanguage = ReadWriteUtil.getXMLTagValue("targetlang", "value", doc, null);
		List<String[]> inputDetails = getInputDetails(doc);
		
		//Parsing the source file to get the composite service object
		Service compService = null;
		if (csRepoFileName.endsWith(".txt"))
		{
			CompositeServiceReader csReader = new SerializedCSReader();
			compService = csReader.readCompositeService(csRepoFileName, csName, logger);
		}
		else if (csRepoFileName.endsWith(".xml"))
		{
			CompositeServiceReader csReader = new XMLCSReader();
			compService = csReader.readCompositeService(csRepoFileName, csName, logger);
		}
		else
		{
			logger.log("Invalid repository file name extension in the given composite service configuration.\n");
			return null;
		}
		
		//Creating a composite service configuration object with the details fetched
		CSConfiguration csConfig = new CSConfiguration(compService, inputDetails, targetLanguage, destFolderName);
		
		return csConfig;
	}
	
	/**
	 * Method for parsing the configuration XML document to extract composite service input details.
	 * @param 	doc		XML document for the composite service input configuration file
	 * @return	List of all composite service input details extracted from the XML document
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
				inpDtlRecord[0] = ReadWriteUtil.getXMLTagValue("name", "value", null, inpElement);
				inpDtlRecord[1] = ReadWriteUtil.getXMLTagValue("type", "value", null, inpElement);
				inpDtlRecord[2] = ReadWriteUtil.getXMLTagValue("value", "value", null, inpElement);
				inputDetails.add(inpDtlRecord);
			}
		}
		
		return inputDetails;
	}
}