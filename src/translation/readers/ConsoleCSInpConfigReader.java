package translation.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import servicecomposition.entities.CompositionRequest;

/**
 * Concrete reader for reading composite service input configuration from the console.
 * @author Jyotsana Gupta
 */
public class ConsoleCSInpConfigReader implements CSInputConfigReader
{
	/**
	 * Method for reading composite service input values from the user through console.
	 * @param	compRequest		Composition request containing the list of inputs to be read 
	 * @return	Input configuration object containing the input values read from the user
	 */
	public CSInputConfiguration readCSInpConfig(CompositionRequest compRequest)
	{
		//Fetching the input values for the given composite service from the user
		List<String[]> inputDetails = new ArrayList<String[]>();
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the values for the following composite service inputs:");
		
		for (String input : compRequest.getInputs())
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
		
		//Creating a composite service input configuration object with the details fetched
		CSInputConfiguration csInpConfig = new CSInputConfiguration(inputDetails);
		
		return csInpConfig;
	}
}