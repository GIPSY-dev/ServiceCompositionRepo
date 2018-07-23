package utilities;

import java.io.File;

/**
 * Utility class for recording error and status messages in a log file.
 * @author Jyotsana Gupta
 */
public class LogUtil 
{
	private String logFileName;
	
	/**
	 * Method for assigning complete name and path of the log file.
	 * If a file by the given name already exists in the given location, it is deleted.
	 * @param 	logFileName		Complete name and path of log file
	 */
	public void setLogFileName(String logFileName)
	{
		this.logFileName = logFileName;
		
		File logFile = new File(logFileName);
		if ((logFile.exists()) && (!logFile.isDirectory()))
		{
			if (!logFile.delete())
			{
				System.out.println("Cannot delete existing log file by the same name. "
									+ "New log messages will be appended to the old ones.");
			}
		}
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