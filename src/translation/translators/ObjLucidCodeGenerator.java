package translation.translators;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import constraint.Constraint;
import constraint.Operator;
import service.Service;
import service.composite.layeredcompsvc.LayeredCompositeService;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;

/**
 * Class for generating the Lucid segment of the Objective Lucid program translation of a layered composite service.
 * @author Jyotsana Gupta
 */
public class ObjLucidCodeGenerator 
{	
	/**
	 * Method for generating the Lucid segment for a given composite service.
	 * @param 	compService		Layered composite service whose Lucid representation needs to be generated
	 * @param 	compSvcInputs	List of composite service input names, data types and values
	 * @return	Lucid segment generated
	 */
	public static String generateObjLucidSegment(Service compService, List<String[]> compSvcInputs)
	{
		//Generating and combining all sub-definitions to form the Lucid segment
		String lucidCode = "#OBJECTIVELUCID"
							+ "\n\n" + "oCAWSMain " + assignCompSvcContext(compSvcInputs)
							+ "\n" + "where" 
							+ "\n\t" + "dimension " + listCompSvcDims(compService.getInput()) + ";"
							+ defineOutputAccmr(compService.getOutput(), ((LayeredCompositeService)compService).getCompositionPlan())
							+ "\n" + "end";
				
		return lucidCode;
	}
	
	/**
	 * Method for generating the composite service's Lucid context specification.
	 * @param 	compSvcInputs	List of composite service input names, data types and values
	 * @return	Composite service's Lucid context specification
	 */
	private static String assignCompSvcContext(List<String[]> compSvcInputs)
	{
		String lucidCode = "";
		
		for (String[] input : compSvcInputs)
		{
			//Extracting composite service input value and enclosing it in quotes if required
			String inputVal = new String();
			if (input[1].equalsIgnoreCase("string"))
			{
				inputVal = "\"" + input[2] + "\"";
			}
			else if (input[1].equalsIgnoreCase("char"))
			{
				inputVal = "'" + input[2] + "'";
			}
			else
			{
				inputVal = input[2];
			}
			
			//Generating and appending context name-value pair for each dimension
			lucidCode += "@.g_" + input[0] + " " + inputVal + " ";
		}
		
		return lucidCode;
	}
	
	/**
	 * Method for generating the composite service's Lucid dimension list.
	 * @param 	csInputs	List of composite service input names
	 * @return	Composite service's Lucid dimension list
	 */
	private static String listCompSvcDims(List<String> csInputs)
	{
		String lucidCode = "";
		
		//Forming the global dimension list with composite service inputs
		for (String param : csInputs)
		{
			String dimension = param.substring(param.indexOf(':') + 2);
			lucidCode += "g_" + dimension + ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
		
		return lucidCode;
	}
	
	/**
	 * Method for generating Lucid definition for the output accumulator node.
	 * @param 	csOutputs		List of composite service output names
	 * @param 	cnstrAwrPlan	Constraint-aware plan of the composite service to be translated
	 * @return	Lucid definition for the output accumulator node
	 */
	private static String defineOutputAccmr(List<String> csOutputs, ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = ""; 
		
		//Generating and combining all sub-definitions to form the accumulator's Lucid definition
		lucidCode += "\n\n\t" + "oCAWSMain = CAWSReqComp(" + listCompSvcOutpParams(csOutputs) + ")"
					+ "\n\t\t\t\t\t" + "wvr CAWSReqCnstr"
					+ "\n\t\t\t\t\t" + assignOutputAccmrContext(csOutputs, cnstrAwrPlan)
					+ "\n\t\t\t\t\t" + "where"
					+ "\n\t\t\t\t\t\t" + listOutputAccmrDims(csOutputs)
					+ "\n\t\t\t\t\t\t" + "CAWSReqCnstr = true;"
					+ listAtomicSvcDefs(cnstrAwrPlan)
					+ "\n\t\t\t\t\t" + "end;";
		
		return lucidCode;
	}
	
	/**
	 * Method for transforming composite service outputs into output accumulator's constructor argument list.
	 * @param 	csOutputs	List of composite service output names
	 * @return	Output accumulator's constructor argument list
	 */
	private static String listCompSvcOutpParams(List<String> csOutputs)
	{
		String lucidCode = ""; 
		
		//Forming the argument list with composite service outputs
		for (String output : csOutputs)
		{
			lucidCode += "#.l_" + output.substring(output.indexOf(':') + 2) + ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
		
		return lucidCode;
	}
	
	/**
	 * Method for generating the output accumulator's Lucid context specification.
	 * @param 	csOutputs		List of composite service output names
	 * @param 	cnstrAwrPlan	Constraint-aware plan of the composite service to be translated
	 * @return	Output accumulator's Lucid context specification
	 */
	private static String assignOutputAccmrContext(List<String> csOutputs, ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = "";
		Set<String> compReqOutputSet = new HashSet<String>(csOutputs);
		Map<String, String> compSvcOutpMapping = new HashMap<String, String>();
	
		//Looping through each service-node in the plan
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				//Checking if the node produces any of the composite service outputs
				Set<String> svcNodeOutputSet = new HashSet<String>(serviceNode.getService().getOutput());
				svcNodeOutputSet.retainAll(compReqOutputSet);
				
				//Recording names of the current component service and 
				//the composite service outputs that it produces, if any
				for (String output : svcNodeOutputSet)
				{
					compSvcOutpMapping.put(output, serviceNode.getService().getName());
				}
			}
		}
		
		//Generating and appending context name-value pair for each composite service output
		for (Entry<String, String> svcOutpEntry : compSvcOutpMapping.entrySet())
		{
			String output = svcOutpEntry.getKey();
			String outpName = output.substring(output.indexOf(':') + 2);
			String svcName = svcOutpEntry.getValue();
			lucidCode += "\n\t\t\t\t\t" + "@.l_" + outpName + " oCAWS" + svcName + "." + outpName + " ";
		}
		
		lucidCode = lucidCode.trim();
		
		return lucidCode;
	}
	
	/**
	 * Method for generating the output accumulator's Lucid dimension list.
	 * @param 	csOutputs	List of composite service output names
	 * @return	Output accumulator's Lucid dimension list
	 */
	private static String listOutputAccmrDims(List<String> csOutputs)
	{
		String lucidCode = "dimension "; 
		
		//Forming the accumulator's dimension list with composite service outputs
		for (String output : csOutputs)
		{
			lucidCode += "l_" + output.substring(output.indexOf(':') + 2) + ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
					
		lucidCode += ";";
		
		return lucidCode;
	}
		
	/**
	 * Method for generating Lucid definitions for all the component services.
	 * @param 	cnstrAwrPlan	Constraint-aware plan of the composite service to be translated
	 * @return	Code block comprising of Lucid definitions representing all the component services
	 */
	private static String listAtomicSvcDefs(ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = "";
		
		//Generating and appending Lucid definition of each component service
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				lucidCode += defineAtomicSvc(serviceNode);
			}
		}
		
		return lucidCode;
	}
	
	/**
	 * Method for generating Lucid definition for a component service.
	 * @param 	serviceNode		Service node for which Lucid definition needs to be generated
	 * @return	Lucid definition for the given component service
	 */
	private static String defineAtomicSvc(ServiceNode serviceNode)
	{
		String lucidCode = "";
		String svcDef = "";
		String svcName = serviceNode.getService().getName();
		
		//Generating and combining all sub-definitions to form the component's Lucid definition
		svcDef += svcName + "(" + listAtmSvcInpParams(serviceNode) + ")" 
				+ "\n\t\t\t\t\t\t\t\t\t" + "wvr c_" + svcName
				+ "\n\t\t\t\t\t\t\t\t\t" + assignAtmSvcContext(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t" + "where"
				+ listAtmSvcDims(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t\t" + "c_" + svcName + " = " + listAtmSvcCnstrs(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t" + "end;";
		
		lucidCode += "\n\n\t\t\t\t\t\t" + "oCAWS" + svcName + " = " + svcDef;
		
		return lucidCode;
	}
	
	/**
	 * Method for transforming a component service's inputs into its free function's argument list.
	 * @param 	serviceNode		Service node for which the argument list needs to be generated
	 * @return	Component service's free function argument list
	 */
	private static String listAtmSvcInpParams(ServiceNode serviceNode)
	{
		String svcDef = "";
		
		//Forming the argument list with component service inputs
		for (String input : serviceNode.getService().getInput())
		{
			svcDef += "#.l_" + input.substring(input.indexOf(':') + 2) + ", ";
		}
		
		if (svcDef.lastIndexOf(",") >= 0)
		{
			svcDef = svcDef.substring(0, svcDef.lastIndexOf(","));
		}
		
		return svcDef;
	}
	
	/**
	 * Method for generating a component service's Lucid context specification.
	 * @param 	serviceNode		Service node for which the context specification needs to be generated
	 * @return	Component service's Lucid context specification
	 */
	private static String assignAtmSvcContext(ServiceNode serviceNode)
	{
		String svcDef = "";
		
		//Defining the service-node's inputs and constraint features as its Lucid dimensions
		Set<String> atmSvcDimSet = new HashSet<String>(serviceNode.getService().getInput());
		for (Constraint constraint : serviceNode.getConstraints())
		{
			atmSvcDimSet.add(constraint.getType());
		}
		
		//Looping through each predecessor node of the service-node
		Map<String, String> svcDimPredMapping = new HashMap<String, String>();
		for (ServiceNode predecessor : serviceNode.getPredecessors())
		{
			//Checking if the current predecessor produces any of the service dimensions
			Set<String> predOutpSet = new HashSet<String>(predecessor.getService().getOutput());
			predOutpSet.retainAll(atmSvcDimSet);
			
			//Recording the names of the current predecessor service and the 
			//component service dimension parameters that it produces, if any
			for (String output : predOutpSet)
			{
				svcDimPredMapping.put(output, predecessor.getService().getName());
			}
		}
	
		//Generating and appending context name-value pair for each component service dimension
		for (String dimension : atmSvcDimSet)
		{
			String dimName = dimension.substring(dimension.indexOf(':') + 2);
			String predName = svcDimPredMapping.get(dimension);
			if (predName != null)
			{
				//For dimension parameters produced by a predecessor
				svcDef += "\n\t\t\t\t\t\t\t\t\t" + "@.l_" + dimName + " oCAWS" + predName + "." + dimName + " ";
			}
			else
			{
				//For dimension parameters provided by the user
				svcDef += "\n\t\t\t\t\t\t\t\t\t" + "@.l_" + dimName + " #.g_" + dimName + " ";
			}
		}
		
		svcDef = svcDef.trim();
		
		return svcDef;
	}
	
	/**
	 * Method for generating a component service's Lucid dimension list.
	 * @param 	serviceNode		Service node for which the dimension list needs to be generated
	 * @return	Component service's Lucid dimension list
	 */
	private static String listAtmSvcDims(ServiceNode serviceNode)
	{
		String svcDef = "";
		Set<String> atmSvcDimSet = new HashSet<String>();		
		
		//Forming the service-node's dimension list with its inputs and constraint features
		for (String input : serviceNode.getService().getInput())
		{
			String inpName = input.substring(input.indexOf(':') + 2);
			atmSvcDimSet.add(inpName);
		}
		for (Constraint constraint : serviceNode.getConstraints())
		{
			String cnstrFeature = constraint.getType();
			String cnstrFeatureName = cnstrFeature.substring(cnstrFeature.indexOf(':') + 2);
			atmSvcDimSet.add(cnstrFeatureName);
		}
		
		svcDef += "\n\t\t\t\t\t\t\t\t\t\t" + "dimension ";
			
		for (String dimension : atmSvcDimSet)
		{
			svcDef += "l_"+ dimension + ", ";
		}
		if (svcDef.lastIndexOf(",") >= 0)
		{
			svcDef = svcDef.substring(0, svcDef.lastIndexOf(","));
		}
					
		svcDef += ";";
		
		return svcDef;
	}
	
	/**
	 * Method for generating a component service's Lucid constraint definition.
	 * @param 	serviceNode		Service node for which the constraint definition needs to be generated
	 * @return	Component service's Lucid constraint definition
	 */
	private static String listAtmSvcCnstrs(ServiceNode serviceNode)
	{
		String svcDef = "";
		
		//Forming Lucid definition of each constraint of the service-node
		for (Constraint constraint : serviceNode.getConstraints())
		{
			//Extracting the name and data type of each constraint feature
			String cnstrFeature = constraint.getType();
			String cnstrFeatureName = cnstrFeature.substring(cnstrFeature.indexOf(':') + 2);
			String cnstrFeatureType = cnstrFeature.substring(0, cnstrFeature.indexOf(':') - 1);
			
			//Appending constraint operator to constraint feature name
			svcDef += "#.l_" + cnstrFeatureName + " " + getOpSymbol(constraint.getOperator()) + " ";
			
			//Appending constraint's literal value (enclosed in quotes if required) to its feature and operator  
			if (cnstrFeatureType.equalsIgnoreCase("string"))
			{
				svcDef += "\"" + constraint.getLiteralValue() + "\" and ";
			}
			else if (cnstrFeatureType.equalsIgnoreCase("char"))
			{
				svcDef += "'" + constraint.getLiteralValue() + "' and ";
			}
			else
			{
				svcDef += constraint.getLiteralValue() + " and ";
			}
		}		
		if (svcDef.length() > 0)
		{
			svcDef = svcDef.substring(0, svcDef.lastIndexOf(" and"));
		}
		else
		{
			//If the service-node has no internal constraints
			svcDef += "true";
		}		
		svcDef += ";";
		
		return svcDef;
	}
	
	/**
	 * Method for fetching the operator symbol based on the given operator name. 
	 * @param 	opName	Operator name
	 * @return	Operator symbol if the operator name is acceptable
	 * 			Null, otherwise. This should never happen because a check is made during service composition.
	 */
	private static String getOpSymbol(Operator opName)
	{
		switch(opName)
		{
			case LESS_THAN:					return "<";
			case GREATER_THAN:				return ">";
			case EQUALS:					return "==";
			case LESS_THAN_OR_EQUAL_TO:		return "<=";
			case GREATER_THAN_OR_EQUAL_TO:	return ">=";
			default:						return null;
		}
	}
}