package translation.translationprocesses;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.utilities.ReadWriteUtil;

public class LucidTranslationDriver 
{
	public static void driveTranslation(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compRequest)
	{
		String lucidCodeFileName = "testinput/complexPlanTranslation/testobjlucidprogram.ipl";
		List<String[]> inputDetails = new ArrayList<String[]>();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please enter the values for the following composite service inputs:");
		
		for (String input : compRequest.getInputs())
		{
			String[] inpDtls = new String[3];
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			System.out.print("\n" + inpName + "(" + inpType + "): ");
			String inpValue = scanner.nextLine();
			
			inpDtls[0] = inpName;
			inpDtls[1] = inpType;
			inpDtls[2] = inpValue;
			inputDetails.add(inpDtls);
		}
		scanner.close();
		
		boolean inpValid = validateInpValues(inputDetails);
		if (inpValid)
		{
			compPlanToObjLucid(cnstrAwrPlan, compRequest, inputDetails, lucidCodeFileName);
			System.out.println("\nTranslated Lucid program has been written to file " + lucidCodeFileName);
		}
		else
		{
			System.out.println("\nOne or more input values are blank or invalid according to their data types."
								+ "\nTranslation aborted.");
		}
	}
	
	public static boolean validateInpValues(List<String[]> inputDetails)
	{
		for (String[] input : inputDetails)
		{
			if (input[1].equalsIgnoreCase("char"))
			{
				if (input[2].length() != 1)
				{
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
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("boolean"))
			{
				if (!(input[2].trim().equals("true") || input[2].trim().equals("false")))
				{
					return false;
				}
			}
			else if (input[1].equalsIgnoreCase("String"))
			{
				if (input[2].isEmpty())
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public static void compPlanToObjLucid(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compRequest, List<String[]> compSvcInputs, String lucidCodeFileName)
	{
		String objLucidSegment = ObjLucidCodeGenerator.generateObjLucidSegment(cnstrAwrPlan, compRequest, compSvcInputs);
		String javaSegment = JavaCodeGenerator.generateJavaSegment(cnstrAwrPlan, compRequest);
		String lucidProgram = javaSegment + "\n\n\n" + objLucidSegment;
		ReadWriteUtil.writeToTextFile(lucidCodeFileName, lucidProgram);
	}
}