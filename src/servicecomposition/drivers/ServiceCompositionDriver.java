package servicecomposition.drivers;

import java.util.List;
import java.util.Scanner;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.readers.ConsoleConfigReader;
import servicecomposition.readers.FileConfigReader;
import servicecomposition.readers.RequestConfigReader;
import servicecomposition.readers.RequestConfiguration;
import servicecomposition.readers.XMLFileConfigReader;

/**
 * Driver class for service composition process.
 * It fetches composition request configuration from the user based on their selected mode of interaction,
 * triggers the service composition process and displays the result of the process on the console.
 * @author Jyotsana Gupta
 */
public class ServiceCompositionDriver 
{
	public static void main(String[] args)
	{
		//Fetching the mode of configuration input from the user
		Scanner scan = new Scanner(System.in);
		String input = null;
		int inputMode = 0;
		do
		{
			System.out.println("Please select one of the following input modes. Press 'X' to exit.");
			System.out.println("1. Console");
			System.out.println("2. XML file");
			input = scan.nextLine();
			input = input.trim();
			
			if (input.equalsIgnoreCase("X"))
			{
				scan.close();
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
				
		//Creating composition request configuration object based on the selected mode
		RequestConfiguration reqConfig = null;
		switch(inputMode)
		{
			case 1	: 	RequestConfigReader consoleConfigReader = new ConsoleConfigReader();
						reqConfig = consoleConfigReader.readReqConfig();
						break;
						
			case 2	:	System.out.println("Please enter the XML configuration file name and location.");
						input = scan.nextLine();				
						FileConfigReader xmlConfigReader = new XMLFileConfigReader();
						xmlConfigReader.setConfigFileName(input);
						reqConfig = xmlConfigReader.readReqConfig();
						break;
		}
		scan.close();
		
		//Triggering the service composition process
		String planDetails = "";
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		if (cnstrAwrPlans == null)
		{
			System.out.println("No composition plans could be generated for the given request.");
		}
		else
		{
			for (int i = 0; i < cnstrAwrPlans.size(); i++)
			{
				planDetails += "Plan " + (i+1) + ":\n" + cnstrAwrPlans.get(i).toString() + "\n\n";
			}
			planDetails = planDetails.trim();
			
			//TODO send dtls to file
			
			System.out.println("The composition plans generated for the given request have been written to "
								+ "filename");
		}	
	}
}