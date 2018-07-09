package translation.translators;

import java.util.List;

import utilities.LogUtil;

/**
 * Utility class for composite service translators. It contains methods that are shared by multiple concrete translators.
 * @author Jyotsana Gupta
 */
public class TranslatorUtil 
{
	/**
	 * Method for validating composite service input values provided by the user.
	 * @param 	inputDetails	Input details received from the user
	 * @param	logger			Logging utility object for logging error or status messages to a text file
	 * @return	true, if all validation checks pass
	 * 			false, otherwise
	 */
	public static boolean validateInpValues(List<String[]> inputDetails, LogUtil logger)
	{
		for (String[] input : inputDetails)
		{
			if (input[1].equalsIgnoreCase("char"))
			{
				if (input[2].length() != 1)
				{
					logger.log("Input " + input[0] + " of type char must have a length of 1.\n");
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("int"))
			{
				try
				{
					Integer.parseInt(input[2].trim());
				}
				catch(NumberFormatException nfe)
				{
					logger.log("Invalid value for input " + input[0] + " of type int.\n");
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("float"))
			{
				try
				{
					Float.parseFloat(input[2].trim());
				}
				catch(NumberFormatException nfe)
				{
					logger.log("Invalid value for input " + input[0] + " of type float.\n");
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("boolean"))
			{
				if (!(input[2].trim().equals("true") || input[2].trim().equals("false")))
				{
					logger.log("Invalid value for input " + input[0] + " of type boolean.\n");
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("String"))
			{
				if (input[2].isEmpty())
				{
					logger.log("Input " + input[0] + " of type string must not be empty.\n");
					return false;
				}
			}
		}
		
		return true;
	}
}