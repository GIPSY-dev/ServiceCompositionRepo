package translation.readers.csreaders;

import service.Service;
import utilities.LogUtil;

/**
 * Interface to be implemented by all concrete composite service readers.
 * @author Jyotsana Gupta
 */
public interface CompositeServiceReader 
{
	/**
	 * Method for initiating parsing of a composite service repository file to extract a specific composite service.
	 * Type of repository file (e.g. serialized Java object file, XML file etc.) may vary according to the concrete implementation.
	 * @param 	csRepoFileName	Complete name and path of the repository file
	 * @param 	csName			Name of the target composite service
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Target composite service object, if successfully extracted
	 * 			Null, otherwise
	 */
	public Service readCompositeService(String csRepoFileName, String csName, LogUtil logger);
}