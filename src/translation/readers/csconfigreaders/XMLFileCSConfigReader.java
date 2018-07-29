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
			logger.log("Invalid repository file type in the given composite service configuration. "
						+ "Only serialized Java object file or XML file can be parsed.\n");
			return null;
		}
		
		//If composite service parsing fails
		if (compService == null)
		{
			return null;
		}
		
		//Fetching the input values for the given composite service from the configuration file, if required
		List<String[]> inputDetails = new ArrayList<String[]>();
		if (targetLanguage.equalsIgnoreCase("Lucid"))
		{			
			inputDetails = getInputDetails(doc, compService);
		}
		
		//Fetching the dot executable file details from the configuration file, if required
		String dotExeName = new String();
		if (targetLanguage.equalsIgnoreCase("Dot"))
		{			
			dotExeName = ReadWriteUtil.getXMLTagValue("dotexename", "value", doc, null);
		}
		
		//Creating a composite service configuration object with the details fetched
		CSConfiguration csConfig = new CSConfiguration(compService, inputDetails, targetLanguage, 
														destFolderName, dotExeName);
		
		return csConfig;
	}
	
	/**
	 * Method for parsing the configuration XML document to extract composite service input details.
	 * @param 	doc				XML document for the composite service input configuration file
	 * @param	compService		Composite service object created for this configuration
	 * @return	List of all composite service input details extracted from the XML document
	 */
	public List<String[]> getInputDetails(Document doc, Service compService)
	{		
		//Creating input detail records for all composite service inputs
		List<String[]> inputDetails = new ArrayList<String[]>();
		for (String input : compService.getInput())
		{
			String[] inpDtlRecord = new String[3];
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);			
			inpDtlRecord[0] = inpName;
			inpDtlRecord[1] = inpType;
			inputDetails.add(inpDtlRecord);
		}
		
		//Fetching all input values from the file
		NodeList inpNodeList = doc.getElementsByTagName("input");
		for (int i = 0; i < inpNodeList.getLength(); i++)
		{
			Node inpNode = inpNodeList.item(i);
			if (inpNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element inpElement = (Element) inpNode;
				String inpName = ReadWriteUtil.getXMLTagValue("name", "value", null, inpElement);
				String inpType = ReadWriteUtil.getXMLTagValue("type", "value", null, inpElement);
				String inpValue = ReadWriteUtil.getXMLTagValue("value", "value", null, inpElement);
				
				for (String[] inpDtlRecord : inputDetails)
				{
					if ((inpDtlRecord[0].equals(inpName)) && (inpDtlRecord[1].equals(inpType)))
					{
						inpDtlRecord[2] = inpValue;
						break;
					}
				}
			}
		}
		
		return inputDetails;
	}
}