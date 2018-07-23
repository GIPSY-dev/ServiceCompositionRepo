package translation.drivers;

import java.util.Scanner;

import translation.readers.csconfigreaders.CSConfigReader;
import translation.readers.csconfigreaders.CSConfiguration;
import translation.readers.csconfigreaders.ConsoleCSConfigReader;
import translation.readers.csconfigreaders.FileCSConfigReader;
import translation.readers.csconfigreaders.XMLFileCSConfigReader;
import translation.translators.CompositeServiceTranslator;
import translation.translators.LucidCSTranslator;
import translation.translators.XMLCSTranslator;
import utilities.LogUtil;

/**
 * Driver class for composite service to formal language translation process.
 * It fetches composite service configuration from the user based on their selected mode of interaction,
 * triggers the service translation process and displays the result of the process on the console.
 * @author Jyotsana Gupta
 */
public class CSTranslationDriver 
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
			System.out.println("Given composite service configuration is invalid for translation. "
								+ "Please check the log file " + logFileName + " for error details.");
			return;
		}
		
		//Triggering the translation process
		CompositeServiceTranslator csTranslator = null;
		if (compSvcConfig.getTargetLanguage().equalsIgnoreCase("Lucid"))
		{
			csTranslator = new LucidCSTranslator();
		}
		else if (compSvcConfig.getTargetLanguage().equalsIgnoreCase("XML"))
		{
			csTranslator = new XMLCSTranslator();
		}
		else
		{
			System.out.println("Given target language is invalid. \nAborting translation.");
			return;
		}
		
		String csTranslationFileName = csTranslator.generateFormalLangCode(compSvcConfig, logger);
		if (csTranslationFileName == null)
		{
			System.out.println("Given composite service cannot be translated into the target language. "
								+ "Please check the log file " + logFileName + " for error details.");
		}
		else
		{
			System.out.println("Translation of the given composite service has been written to "
								+ csTranslationFileName);
		}
	}
}