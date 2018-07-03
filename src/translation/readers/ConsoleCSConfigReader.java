package translation.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import service.Service;
import utilities.CompSvcStorageUtil;
import utilities.LogUtil;

/**
 * Concrete reader for reading composite service configuration from the console.
 * @author Jyotsana Gupta
 */
public class ConsoleCSConfigReader implements CSConfigReader
{
	/**
	 * Method for reading composite service source file location and its input values from the user through console.
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	Composite service configuration object containing the information read from the user
	 */
	public CSConfiguration readCSConfig(LogUtil logger)
	{
		//Fetching the composite service source file location from the user
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the complete file path and name of the serialized composite service source file: ");
		String csSrcFileName = scanner.nextLine();
		
		//Parsing the source file to get the composite service object
		Service compService = CompSvcStorageUtil.readCSFromSerialFile(csSrcFileName, logger);
		if (compService == null)
		{
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
		CSConfiguration csConfig = new CSConfiguration(compService, inputDetails);
		
		return csConfig;
	}
}