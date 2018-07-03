package translation.readers;

/**
 * Abstract reader for reading composite service configuration from a file stored on disk.
 * This class must be extended by all concrete file configuration readers.
 * @author Jyotsana Gupta
 */
public abstract class FileCSConfigReader implements CSConfigReader
{
	protected String configFileName;
	
	/**
	 * Method for assigning complete name and path of the source CS configuration file.
	 * @param 	configFileName	Complete name and path of configuration file
	 */
	public void setConfigFileName(String configFileName)
	{
		this.configFileName = configFileName;
	}
}