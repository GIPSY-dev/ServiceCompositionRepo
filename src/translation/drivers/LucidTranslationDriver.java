package translation.drivers;

import java.util.Scanner;
import translation.readers.CSConfigReader;
import translation.readers.CSConfiguration;
import translation.readers.ConsoleCSConfigReader;
import translation.readers.FileCSConfigReader;
import translation.readers.XMLFileCSConfigReader;
import translation.translationprocesses.CompositeServiceTranslation;
import utilities.LogUtil;

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
		String csLucidFileName = CompositeServiceTranslation.driveServiceTranslation(compSvcConfig, logger);
		if (csLucidFileName == null)
		{
			System.out.println("Given composite service cannot be translated into Lucid. "
								+ "Please check the log file " + logFileName + " for error details.");
		}
		else
		{
			System.out.println("Lucid translation of the given composite service has been written to "
								+ csLucidFileName);
		}
	}
}