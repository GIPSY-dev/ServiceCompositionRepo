package translation.drivers;

import java.util.List;
import java.util.Scanner;
import service.Service;
import translation.readers.CSConfigReader;
import translation.readers.CSConfiguration;
import translation.readers.ConsoleCSConfigReader;
import translation.readers.FileCSConfigReader;
import translation.readers.XMLFileCSConfigReader;
import translation.translationprocesses.JavaCodeGenerator;
import translation.translationprocesses.ObjLucidCodeGenerator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Driver class for composite service translation process.
 * It fetches composition service configuration from the user based on their selected mode of interaction,
 * triggers the service translation process and displays the result of the process on the console.
 * @author Jyotsana Gupta
 */
public class LucidTranslationDriver 
{
	public static void main(String[] args)
	{
		//Fetching the mode of configuration input from the user
		Scanner scanner = new Scanner(System.in);
		String input = null;
		int inputMode = 0;
		do
		{
			System.out.println("Please select one of the following input modes. Press 'X' to exit.");
			System.out.println("1. Console");
			System.out.println("2. XML file");
			input = scanner.nextLine();
			input = input.trim();
			
			if (input.equalsIgnoreCase("X"))
			{
				scanner.close();
				return;
			}
			else
			{
				try
				{
					inputMode = Integer.parseInt(input);
				}
				catch(NumberFormatException nfe)
				{
					inputMode = 0;
				}
				
				if ((inputMode < 1) || (inputMode > 2))
				{
					System.out.println("Invalid input mode selected.");
				}
			}
		} while ((inputMode < 1) || (inputMode > 2));
		
		//Creating a logger for recording the status and error messages during translation
		String logFileName = "testoutput/servicetranslationruns/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(logFileName);
		
		//Creating composite service configuration object based on the selected mode
		CSConfiguration compSvcConfig = null;
		switch(inputMode)
		{
			case 1	: 	CSConfigReader consoleConfigReader = new ConsoleCSConfigReader();
						compSvcConfig = consoleConfigReader.readCSConfig(logger);
						break;
						
			case 2	:	System.out.println("Please enter the XML configuration file name and location.");
						input = scanner.nextLine();				
						FileCSConfigReader xmlConfigReader = new XMLFileCSConfigReader();
						xmlConfigReader.setConfigFileName(input);
						compSvcConfig = xmlConfigReader.readCSConfig(logger);
						break;
		}
		scanner.close();
		
		//Aborting in case of issues creating a composite service configuration
		if (compSvcConfig == null)
		{
			System.out.println("Given configuration cannot be translated into a Lucid program. "
								+ "Please check the log file " + logFileName + " for error details.");
			return;
		}
		
		//Triggering the translation process
		boolean inpValid = validateInpValues(compSvcConfig.getInputDetails(), logger);
		if (inpValid)
		{
			String csLucidFileName = compPlanToObjLucid(compSvcConfig);
			System.out.println("Lucid translation of the given composite service has been written to "
								+ csLucidFileName);
		}
		else
		{
			System.out.println("Given composite service cannot be translated into Lucid. "
								+ "Please check the log file " + logFileName + " for error details.");
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
	public static String compPlanToObjLucid(CSConfiguration compSvcConfig)
	{
		//Getting details required for translation from the given configuration
		Service compService = compSvcConfig.getCompositeService();
		List<String[]> compSvcInputs = compSvcConfig.getInputDetails();		
		
		//Triggering the Lucid code generation
		String javaSegment = JavaCodeGenerator.generateJavaSegment(compService);
		String objLucidSegment = ObjLucidCodeGenerator.generateObjLucidSegment(compService, compSvcInputs);
		String lucidProgram = javaSegment + "\n\n\n" + objLucidSegment;
		
		//Writing Lucid code to a file
		String csLucidFileName = "CSLucid_" + System.nanoTime() + ".ipl";
		ReadWriteUtil.writeToTextFile(csLucidFileName, lucidProgram);
				
		return csLucidFileName;
	}
}