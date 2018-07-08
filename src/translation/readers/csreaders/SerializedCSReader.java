package translation.readers.csreaders;

import java.util.ArrayList;
import service.Service;
import service.parser.BasicServiceParser;
import service.parser.ServiceFileParserDecorator;
import service.parser.ServiceSerializedParser;
import utilities.LogUtil;

/**
 * Concrete reader for reading a composite service from a serialized Java object file.
 * @author Jyotsana Gupta
 */
public class SerializedCSReader implements CompositeServiceReader
{
	/**
	 * Method for initiating parsing of a serialized composite service repository file to extract a specific composite service.
	 * @param 	csRepoFileName	Complete name and path of the repository file
	 * @param	csName			Name of the target composite service
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Target composite service object, if successfully extracted
	 * 			Null, otherwise
	 */
	public Service readCompositeService(String csRepoFileName, String csName, LogUtil logger)
	{
		//Parsing the serialized composite service repository file to read all its services
		ServiceFileParserDecorator svcParser = new ServiceSerializedParser(new BasicServiceParser());
		svcParser.setLocation(csRepoFileName);
		ArrayList<Service> services = svcParser.parse();
		
		//Extracting the target service from all the services read
		if ((services != null) && (services.size() > 0))
		{
			for (Service service : services)
			{
				if (service.getName().equals(csName))
				{
					return service;
				}
			}
		}
		
		//In case the target service is not found in the repository
		logger.log("The given serialized composite service repository does not contain the target service.\n"
					+ "Aborting translation process.\n");
		return null;
	}
}