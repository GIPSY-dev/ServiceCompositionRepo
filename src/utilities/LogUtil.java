package utilities;

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
	 * 
	 * @param message
	 */
	public void log(String message)
	{
		ReadWriteUtil.writeToTextFile(logFileName, message);
	}
}