package translation.readers.csconfigreaders;

import utilities.LogUtil;

/**
 * Interface to be implemented by all concrete composite service configuration readers.
 * @author Jyotsana Gupta
 */
public interface CSConfigReader 
{
	/**
	 * Method for reading composite service translation details from the user.
	 * Mode of input (e.g. console, XML file etc.) may vary according to the concrete implementation.
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	Composite service configuration object containing the information read from the user
	 */
	public CSConfiguration readCSConfig(LogUtil logger);
}