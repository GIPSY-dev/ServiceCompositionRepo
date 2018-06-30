package servicecomposition.readers;

/**
 * Abstract reader for reading composition request configuration from a file stored on disk.
 * This class must be extended by all concrete file configuration readers.
 * @author Jyotsana Gupta
 */
public abstract class FileReqConfigReader implements RequestConfigReader
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
}