package translation.readers.csconfigreaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import service.Service;
import translation.readers.csreaders.CompositeServiceReader;
import translation.readers.csreaders.SerializedCSReader;
import translation.readers.csreaders.XMLCSReader;
import utilities.LogUtil;

/**
 * Concrete reader for reading composite service configuration from the console.
 * @author Jyotsana Gupta
 */
public class ConsoleCSConfigReader implements CSConfigReader
{
	/**
	 * Method for reading composite service translation details from the user through console.
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	Composite service configuration object containing the information read from the user
	 */
	public CSConfiguration readCSConfig(LogUtil logger)
	{
		//Fetching the composite service name, repository file location and target language from the user
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the complete file path and name of the serialized or XML composite service repository: ");
		String csRepoFileName = scanner.nextLine();
		String destFolderName = csRepoFileName.substring(0, (csRepoFileName.lastIndexOf("/") + 1));
		System.out.println("Please enter the name of the composite service to be translated: ");
		String csName = scanner.nextLine();
		System.out.println("Please enter the name of the target formal language (Lucid/XML) for the translation: ");
		String targetLanguage = scanner.nextLine();
		
		//Parsing the source file to get the composite service object
		Service compService = null;
		if (csRepoFileName.endsWith(".txt"))
		{
			CompositeServiceReader csReader = new SerializedCSReader();
			compService = csReader.readCompositeService(csRepoFileName, csName, logger);
		}
		else if (csRepoFileName.endsWith(".xml"))
		{
			CompositeServiceReader csReader = new XMLCSReader();
			compService = csReader.readCompositeService(csRepoFileName, csName, logger);
		}
		else
		{
			logger.log("Invalid repository file name extension in the given composite service configuration.\n");
			scanner.close();
			return null;
		}
		
		//Fetching the input values for the given composite service from the user
		List<String[]> inputDetails = new ArrayList<String[]>();
		System.out.println("Please enter the values for the following composite service inputs:");
		
		for (String input : compService.getInput())
		{
			String[] inpDtlRecord = new String[3];
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			System.out.println(inpName + "(" + inpType + "): ");
			String inpValue = scanner.nextLine();
			
			inpDtlRecord[0] = inpName;
			inpDtlRecord[1] = inpType;
			inpDtlRecord[2] = inpValue;
			inputDetails.add(inpDtlRecord);
		}
		scanner.close();
		
		//Creating a composite service configuration object with the details fetched
		CSConfiguration csConfig = new CSConfiguration(compService, inputDetails, targetLanguage, destFolderName);
		
		return csConfig;
	}
}