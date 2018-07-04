package translation.translationprocesses;

import java.util.List;
import service.Service;
import translation.readers.CSConfiguration;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for managing the composite service translation process.
 * @author Jyotsana Gupta
 */
public class CompositeServiceTranslation 
{
	/**
	 * Method for triggering the validation and translation of a composite service into a Lucid program.
	 * @param 	compSvcConfig	Composite service configuration received from the user
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Complete name and path of the file containing the target Lucid program
	 */
	public static String driveServiceTranslation(CSConfiguration compSvcConfig, LogUtil logger)
	{
		boolean csInpValid = validateInpValues(compSvcConfig.getInputDetails(), logger);
		if (csInpValid)
		{
			String csLucidFileName = compPlanToObjLucid(compSvcConfig);
			return csLucidFileName;
		}
		else
		{
			return null;
		}
	}
	
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
	
	/**
	 * Method for sequentially triggering the phases involved in generation of a Lucid program for a composite service.
	 * @param 	compSvcConfig	Composite service configuration received from the user
	 * @return	Complete name and path of the file containing the target Lucid program
	 */
	private static String compPlanToObjLucid(CSConfiguration compSvcConfig)
	{
		//Getting details required for translation from the given configuration
		Service compService = compSvcConfig.getCompositeService();
		List<String[]> compSvcInputs = compSvcConfig.getInputDetails();		
		
		//Triggering the Lucid code generation
		String javaSegment = JavaCodeGenerator.generateJavaSegment(compService);
		String objLucidSegment = ObjLucidCodeGenerator.generateObjLucidSegment(compService, compSvcInputs);
		String lucidProgram = javaSegment + "\n\n\n" + objLucidSegment;
		
		//Writing Lucid code to a file
		String csLucidFileName = compSvcConfig.getDestinationFolder() + "CSLucid_" + compSvcConfig.getCompositeService().getName() + ".ipl";
		ReadWriteUtil.writeToTextFile(csLucidFileName, lucidProgram);
				
		return csLucidFileName;
	}
}