package servicecomposition.readers;

import org.w3c.dom.Document;
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
		String inputString = ReadWriteUtil.getXMLTagValue("inputs", "value", doc, null);
		String outputString = ReadWriteUtil.getXMLTagValue("outputs", "value", doc, null);
		String qosString = ReadWriteUtil.getXMLTagValue("qos", "value", doc, null);
		String constraintString = ReadWriteUtil.getXMLTagValue("constraints", "value", doc, null);
		String repoFileName = ReadWriteUtil.getXMLTagValue("repofilename", "value", doc, null);
		String storeCSFlag = ReadWriteUtil.getXMLTagValue("storecsflag", "value", doc, null);
		
		//Creating a Request Configuration object with the details fetched
		RequestConfiguration reqConfig = new RequestConfiguration(inputString, outputString, qosString, constraintString, repoFileName, storeCSFlag);
		
		return reqConfig;
	}
}