package servicecomposition.readers;

import java.util.Scanner;

/**
 * Concrete reader for reading composition request configuration from the console.
 * @author Jyotsana Gupta
 */
public class ConsoleReqConfigReader implements RequestConfigReader
{
	/**
	 * Method for reading composition request and service repository details from the user through console.
	 * @return	Request configuration object containing the input request and repository details
	 */
	public RequestConfiguration readReqConfig()
	{
		//Fetching the components of a composition request from the user
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please enter the details required to build a composition request:");
		System.out.println("Comma-separated list of inputs:");
		String inputString = scanner.nextLine();
		System.out.println("Comma-separated list of outputs:");
		String outputString = scanner.nextLine();
		System.out.println("Comma-separated list of QoS features:");
		String qosString = scanner.nextLine();
		System.out.println("Comma-separated list of constraints:");
		String constraintString = scanner.nextLine();
		
		//Fetching the service repository file location from the user
		System.out.println("Please enter the complete file path and name of the service repository XML file: ");
		String repoFileName = scanner.nextLine();
		
		//Fetching user choice for storing composite services generated in the repository
		System.out.println("Please select (Y/N) whether any composite services generated for this request should be stored in the source repository:");
		String storeCSFlag = scanner.nextLine();
		scanner.close();
		
		//Creating a Request Configuration object with the details fetched
		RequestConfiguration reqConfig = new RequestConfiguration(inputString, outputString, qosString, constraintString, repoFileName, storeCSFlag);
		
		return reqConfig;
	}
}