package servicecomposition.compositionprocesses;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import constraint.Constraint;
import constraint.Operator;
import servicecomposition.entities.CompositionPlan;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.QualityOfService;
import servicecomposition.entities.SearchGraph;
import servicecomposition.entities.SearchNode;
import service.Service;
import service.parser.BasicServiceParser;
import service.parser.ConstrainedServiceXMLParser;
import service.parser.ServiceFileParserDecorator;

/**
 * Class for driving the service composition process.
 * @author Jyotsana Gupta
 */
public class ServiceComposition 
{
	/**
	 * Method for gathering information about the composition request and service repository from the user 
	 * and using it to trigger the construction of a valid composition request and constraint-aware 
	 * service composition plans.
	 * @return	List of constraint-aware service composition plans constructed for the composition request
	 * 			Null, if the service composition process fails at any point
	 */
	public static List<ConstraintAwarePlan> driveServiceComposition()
	{
		//Fetching the components of a composition request from the user
		Scanner userInput = new Scanner(System.in);
		System.out.println("Please enter the details required to build a composition request:");
		System.out.println("Comma-separated list of inputs:");
		String inputString = userInput.nextLine();
		System.out.println("Comma-separated list of outputs:");
		String outputString = userInput.nextLine();
		System.out.println("Comma-separated list of QoS features:");
		String qosString = userInput.nextLine();
		System.out.println("Comma-separated list of constraints:");
		String constraintString = userInput.nextLine();
		
		//Creating a valid composition request based on user inputs
		CompositionRequest compRequest = constructCompositionRequest(inputString, outputString, qosString, constraintString);
		if (compRequest == null)
		{
			System.out.println("Aborting service composition process.");
			userInput.close();
			return null;
		}
		
		//Fetching the service repository file location from the user
		System.out.println("Please enter the complete file path and name of the service repository XML file: ");
		String repoFileName = userInput.nextLine();
		userInput.close();		
		
		//Building constraint-aware composition plans for the given request and repository
		return buildServiceCompositions(compRequest, repoFileName);
	}
	
	/**
	 * Method for constructing a valid service composition request based on the user inputs.
	 * @param 	inputString			Comma-separated list of requested inputs
	 * @param 	outputString		Comma-separated list of requested outputs
	 * @param 	qosString			Comma-separated list of requested QoS features
	 * @param 	constraintString	Comma-separated list of requested constraints
	 * @return	A valid composition request, if it can be constructed
	 * 			Null, otherwise
	 */
	public static CompositionRequest constructCompositionRequest(String inputString, String outputString, String qosString, String constraintString)
	{
		boolean isRequestValid = true;
		
		//Creating a list of requested inputs
		List<String> inputs = new ArrayList<String>();
		String[] inputStrings = inputString.split(",");
		for (String input : inputStrings)
		{
			input = input.trim();
			if (input.length() > 0)
			{
				inputs.add(input);
			}
		}
		
		//Creating a list of requested outputs
		List<String> outputs = new ArrayList<String>();
		String[] outputStrings = outputString.split(",");
		for (String output : outputStrings)
		{
			output = output.trim();
			if (output.length() > 0)
			{
				outputs.add(output);
			}
		}
		
		//Creating a list of requested QoS features
		List<String> qos = new ArrayList<String>();
		String[] qosStrings = qosString.split(",");
		for (String qosFeature : qosStrings)
		{
			qosFeature = qosFeature.trim();
			if (qosFeature.length() > 0)
			{
				qos.add(qosFeature);
			}
			
		}
		
		//Creating a list of requested constraints
		List<Constraint> constraints = new ArrayList<Constraint>();
		String[] constraintStrings = constraintString.split(",");
		for (String constraintStr : constraintStrings)
		{
			constraintStr = constraintStr.trim();
			if (constraintStr.length() > 0)
			{
				String[] constraintElements = constraintStr.split("\\|");
				
				//Each constraint must have 3 elements: type, operator and literal value
				if (constraintElements.length == 3)
				{
					//Operator must be from the enumeration listing the accepted operators
					Operator operator = getOperator(constraintElements[1].trim());
					if (operator == null)
					{
						System.out.println("Invalid operator for requested constraint: " + constraintStr);
						isRequestValid = false;
					}
					else
					{
						//Creating a constraint object if all validations are passed 
						Constraint constraint = new Constraint("CompositeService", constraintElements[2].trim(), constraintElements[0].trim(), operator);
						constraints.add(constraint);
					}
				}
				else
				{
					System.out.println("Invalid format for requested constraint: " + constraintStr);
					isRequestValid = false;
				}
			}
		}
		
		if (isRequestValid)
		{
			//Performing other validations on the composition request components 
			isRequestValid = validateCompRequestComponents(inputs, outputs, qos, constraints);
			if (isRequestValid)
			{
				//Creating a composition request if all validations are passed
				CompositionRequest compRequest = new CompositionRequest(inputs, outputs, qos, constraints);
				return compRequest;
			}
		}		
		
		return null;
	}
	
	/**
	 * Method for reading the service repository and triggering the various phases of service 
	 * composition in sequence based on the given composition request and available services.
	 * @param 	compRequest		Service composition request
	 * @param 	repoFileName	Service repository file name
	 * @return	List of constraint-aware service composition plans constructed for the composition request
	 * 			Null, if the service composition process fails at any point
	 */
	public static List<ConstraintAwarePlan> buildServiceCompositions(CompositionRequest compRequest, String repoFileName)
	{
		//Reading the service repository
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation(repoFileName);
		ArrayList<Service> serviceRepo = serviceParser.parse();
		if (serviceRepo.size() == 0)
		{
			System.out.println("Service repository is empty.\nAborting service composition process.");
			return null;
		}
				
		//Using forward expansion to generate a search graph
		SearchGraph searchGraph = ForwardExpansion.forwardExpansion(compRequest, serviceRepo);
		
		boolean compositionFailure = false;
		if (searchGraph != null)
		{
			//Using backward search for constructing plan sets from the search graph
			List<Set<SearchNode>> planSets = BackwardSearch.backwardSearch(compRequest, searchGraph);
			
			if (planSets.size() > 0)
			{
				//Constructing pruned and validated composition plans from the plan sets
				List<CompositionPlan> plans = PlanConstruction.constructPlans(compRequest, planSets);
				
				if (plans.size() > 0)
				{
					//Constructing constraint aware plans from the composition plans
					List<ConstraintAwarePlan> cnstrAwrPlans = ConstraintAwarePlanConstruction.constructCAPlans(plans);
					return cnstrAwrPlans;
				}
				else
				{
					compositionFailure = true;
				}
			}
			else
			{
				compositionFailure = true;
			}
		}
		else
		{
			compositionFailure = true;
		}
		
		//In case the service composition process fails at some point
		if (compositionFailure)
		{
			System.out.println("The given composition problem is either unsolvable based on the existing "
								+ "repository or can be solved by a single service from the repository."
								+ "\nAborting service composition process.");
		}
		return null;
	}
	
	/**
	 * Method for fetching the operator name from the predefined enumeration based on the given operator symbol. 
	 * @param 	opSymbol	Operator symbol
	 * @return	Predefined operator name if the operator symbol is acceptable
	 * 			Null, otherwise
	 */
	private static Operator getOperator(String opSymbol)
	{
		switch(opSymbol)
		{
			case "<":	return Operator.LESS_THAN;
			case ">":	return Operator.GREATER_THAN;
			case "=":	return Operator.EQUALS;
			case "<=":	return Operator.LESS_THAN_OR_EQUAL_TO;
			case ">=":	return Operator.GREATER_THAN_OR_EQUAL_TO;
			default:	return null;
		}
	}
	
	/**
	 * Method for performing validations on composition request components.
	 * @param 	inputs			List of requested inputs
	 * @param 	outputs			List of requested outputs
	 * @param 	qos				List of requested QoS features
	 * @param 	constraints		List of requested constraints
	 * @return	true, if all the validations are passed
	 * 			false, if any of the validations fail
	 */
	private static boolean validateCompRequestComponents(List<String> inputs, List<String> outputs, List<String> qos, List<Constraint> constraints)
	{
		//Checking that there is at least 1 input requested
		if (inputs.size() == 0)
		{
			System.out.println("Invalid request. Composition request must have at least 1 input.");
			return false;
		}
		
		//Checking that there is at least 1 output requested
		if (outputs.size() == 0)
		{
			System.out.println("Invalid request. Composition request must have at least 1 output.");
			return false;
		}
		
		//Checking that the QoS features requested are from the predefined QoS enumeration
		for (String qosValue : qos)
		{
			if (!QualityOfService.contains(qosValue))
			{
				System.out.println("Unidentified requested QoS feature: " + qosValue);
				return false;
			}
		}
		
		//Checking that the constraints requested are enforced only on the requested input, output and QoS features
		List<String> requestParameters = new ArrayList<String>();
		for (String input : inputs)
		{
			requestParameters.add(input);
		}
		for (String output : outputs)
		{
			requestParameters.add(output);
		}
		for (String qosValue : qos)
		{
			requestParameters.add(qosValue);
		}
		for (Constraint constraint : constraints)
		{
			if (!requestParameters.contains(constraint.getType()))
			{
				System.out.println("Invalid type for requested constraint: " 
									+ constraint.getType() + " " 
									+ constraint.getOperator() + " "
									+ constraint.getLiteralValue());
				return false;
			}
		}
		
		return true;
	}
}