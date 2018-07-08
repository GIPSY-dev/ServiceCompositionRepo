package utilities;

/**
 * Utility class for recording error and status messages in a log file.
 * @author Jyotsana Gupta
 */
public class LogUtil 
{
	private String logFileName;
	
	/**
	 * Method for assigning complete name and path of the log file.
	 * @param 	logFileName		Complete name and path of log file
	 */
	public void setLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
	}
	
	/**
	 * Method for writing a text message to the log file associated with this logger.
	 * @param 	message		Text message to be written
	 */
	public void log(String message)
	{
		ReadWriteUtil.writeToTextFile(logFileName, message);
	}
}