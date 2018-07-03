package servicecomposition.compositionprocesses;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import constraint.Constraint;
import constraint.Operator;
import servicecomposition.entities.CompositionPlan;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.QualityOfService;
import servicecomposition.entities.SearchGraph;
import servicecomposition.entities.SearchNode;
import servicecomposition.readers.RequestConfiguration;
import utilities.LogUtil;
import utilities.CompSvcStorageUtil;
import service.Service;
import service.composite.layeredcompsvc.LayeredCompositeService;
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
	 * Method for triggering the construction of a valid composition request and constraint-aware
	 * service composition plans based on the composition request and service repository configuration 
	 * received as input.
	 * @param	reqConfig	Object containing request and repository configuration
	 * @param	logger		Logging utility object for logging error or status messages to a text file
	 * @return	List of constraint-aware service composition plans constructed for the composition request
	 * 			Null, if the service composition process fails at any point
	 */
	public static List<ConstraintAwarePlan> driveServiceComposition(RequestConfiguration reqConfig, LogUtil logger)
	{
		//Creating a valid composition request based on details fetched
		CompositionRequest compRequest = constructCompositionRequest(reqConfig, logger);
		if (compRequest == null)
		{
			logger.log("Aborting service composition process.\n");
			return null;
		}	
		
		//Building constraint-aware composition plans for the given request and repository
		List<ConstraintAwarePlan> cnstrAwrPlans = buildServiceCompositions(compRequest, reqConfig.getRepoFileName(), logger);
		
		//Creating composite services for the composition plans generated 
		//and storing them in the source repository if the user requests for it
		if (reqConfig.getStoreCSFlag().equalsIgnoreCase("Y"))
		{
			ArrayList<Service> compSvcs = new ArrayList<Service>();
			for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
			{
				LayeredCompositeService layeredCS = CompSvcStorageUtil.createCompositeService(compRequest, cnstrAwrPlan);
				compSvcs.add(layeredCS);
			}
			
			CompSvcStorageUtil.writeCSToSerialSvcRepo(compSvcs, reqConfig.getRepoFileName());
		}
		
		return cnstrAwrPlans;
	}
	
	/**
	 * Method for constructing a valid service composition request based on the user inputs.
	 * @param 	reqConfig	Composition request details fetched from a source file or console
	 * @return	A valid composition request, if it can be constructed
	 * 			Null, otherwise
	 */
	public static CompositionRequest constructCompositionRequest(RequestConfiguration reqConfig, LogUtil logger)
	{
		boolean isRequestValid = true;
		
		//Creating a list of requested inputs
		List<String> inputs = new ArrayList<String>();
		String[] inputStrings = reqConfig.getInputs().split(",");
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
		String[] outputStrings = reqConfig.getOutputs().split(",");
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
		String[] qosStrings = reqConfig.getQos().split(",");
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
		String[] constraintStrings = reqConfig.getConstraints().split(",");
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
						logger.log("Invalid operator for requested constraint: " + constraintStr + "\n");
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
					logger.log("Invalid format for requested constraint: " + constraintStr + "\n");
					isRequestValid = false;
				}
			}
		}
		
		if (isRequestValid)
		{
			//Performing other validations on the composition request components 
			isRequestValid = validateCompRequestComponents(inputs, outputs, qos, constraints, logger);
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
	public static List<ConstraintAwarePlan> buildServiceCompositions(CompositionRequest compRequest, String repoFileName, LogUtil logger)
	{
		//Reading the service repository
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation(repoFileName);
		ArrayList<Service> serviceRepo = serviceParser.parse();
		if (serviceRepo.size() == 0)
		{
			logger.log("Service repository is empty.\nAborting service composition process.\n");
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
			logger.log("The given composition problem is either unsolvable based on the existing "
						+ "repository or can be solved by a single service from the repository."
						+ "\nAborting service composition process.\n");
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
	private static boolean validateCompRequestComponents(List<String> inputs, List<String> outputs, List<String> qos, List<Constraint> constraints, LogUtil logger)
	{
		//Checking that there is at least 1 input requested
		if (inputs.size() == 0)
		{
			logger.log("Invalid request. Composition request must have at least 1 input.\n");
			return false;
		}
		
		//Checking that there is at least 1 output requested
		if (outputs.size() == 0)
		{
			logger.log("Invalid request. Composition request must have at least 1 output.\n");
			return false;
		}
		
		//Checking that the QoS features requested are from the predefined QoS enumeration
		for (String qosValue : qos)
		{
			if (!QualityOfService.contains(qosValue))
			{
				logger.log("Unidentified requested QoS feature: " + qosValue + "\n");
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
				logger.log("Invalid type for requested constraint: " 
							+ constraint.getType() + " " 
							+ constraint.getOperator() + " "
							+ constraint.getLiteralValue() + "\n");
				return false;
			}
		}
		
		return true;
	}
}