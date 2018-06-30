package servicecomposition.readers;

/**
 * Abstract reader for reading composition request configuration from a file stored on disk.
 * This class must be extended by all concrete file configuration readers.
 * @author Jyotsana Gupta
 */
public abstract class FileConfigReader implements RequestConfigReader
{
	protected String configFileName;
	
	/**
	 * Method for assigning complete name and path of the source request configuration file.
	 * @param 	configFileName	Complete name and path of configuration file
	 */
	public void setConfigFileName(String configFileName)
	{
		this.configFileName = configFileName;
	}
	
	/**
	 * Method for reading composition request and service repository details from the user-specified file.
	 * This method will be properly implemented by the concrete file configuration readers. Here we use a dummy definition.
	 * @return	Request configuration object containing the input request and repository details
	 * 			The dummy definition used here always returns null.
	 */
	public RequestConfiguration readReqConfig()
	{
		return null;
	}
}