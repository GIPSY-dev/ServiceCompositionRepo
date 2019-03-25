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
	public static String generateObjLucidSegment(Service compService, List<String[]> compSvcInputs)
	{	
		String lucidCode = "#OBJECTIVELUCID"
							+ "\n\n" + "oCAWSMain " + assignCompSvcInpContext(compSvcInputs)
							+ "\n" + "where" 
							+ "\n\t" + "dimension " + listCompSvcInpsOutps(compService.getInput()) + ";"
							+ defineOCAWSMain(compService.getOutput(), ((LayeredCompositeService)compService).getCompositionPlan())
							+ "\n" + "end";
				
		return lucidCode;
	}
	
	private static String assignCompSvcInpContext(List<String[]> compSvcInputs)
	{
		String lucidCode = "";
		
		for (String[] input : compSvcInputs)
		{
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
			lucidCode += "@.g_" + input[0] + " " + inputVal + " ";
		}
		
		return lucidCode;
	}
	
	private static String listCompSvcInpsOutps(List<String> csInputs)
	{
		String lucidCode = "";
		
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
	
	private static String defineOCAWSMain(List<String> csOutputs, ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = ""; 
		
		lucidCode += "\n\n\t" + "oCAWSMain = CAWSReqComp(" + listCompSvcOutpDims(csOutputs) + ")"
					+ "\n\t\t\t\t\t" + "wvr CAWSReqCnstr"
					+ "\n\t\t\t\t\t" + assignCompSvcOutpContext(csOutputs, cnstrAwrPlan)
					+ "\n\t\t\t\t\t" + "where"
					+ "\n\t\t\t\t\t\t" + "CAWSReqCnstr = true;"
					+ listAtomicSvcDefs(cnstrAwrPlan)
					+ "\n\t\t\t\t\t" + "end;";
		
		return lucidCode;
	}
	
	private static String listCompSvcOutpDims(List<String> csOutputs)
	{
		String lucidCode = ""; 
		
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
	
	private static String assignCompSvcOutpContext(List<String> csOutputs, ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = "";
		Set<String> compReqOutputSet = new HashSet<String>(csOutputs);
		Map<String, String> compSvcOutpMapping = new HashMap<String, String>();
	
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				//Checking if the node produces any of the requested outputs
				Set<String> svcNodeOutputSet = new HashSet<String>(serviceNode.getService().getOutput());
				svcNodeOutputSet.retainAll(compReqOutputSet);
				
				for (String output : svcNodeOutputSet)
				{
					compSvcOutpMapping.put(output, serviceNode.getService().getName());
				}
			}
		}
		
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
		
	private static String listAtomicSvcDefs(ConstraintAwarePlan cnstrAwrPlan)
	{
		String lucidCode = "";
		
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				lucidCode += defineAtomicSvc(serviceNode);
			}
		}
		
		return lucidCode;
	}
	
	private static String defineAtomicSvc(ServiceNode serviceNode)
	{
		String lucidCode = "";
		String svcDef = "";
		String svcName = serviceNode.getService().getName();
		
		svcDef += svcName + "(" + listAtmSvcInpDims(serviceNode) + ")" 
				+ "\n\t\t\t\t\t\t\t\t\t" + "wvr c_" + svcName
				+ "\n\t\t\t\t\t\t\t\t\t" + assignAtmSvcInpContext(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t" + "where"
				+ listAtmSvcDims(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t\t" + "c_" + svcName + " = " + listAtmSvcCnstrs(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t" + "end;";
		
		lucidCode += "\n\n\t\t\t\t\t\t" + "oCAWS" + svcName + " = " + svcDef;
		
		return lucidCode;
	}
	
	private static String listAtmSvcInpDims(ServiceNode serviceNode)
	{
		String svcDef = "";
		
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
	
	private static String assignAtmSvcInpContext(ServiceNode serviceNode)
	{
		String svcDef = "";
		Set<String> atmSvcInpSet = new HashSet<String>(serviceNode.getService().getInput());
		Map<String, String> svcInpPredMapping = new HashMap<String, String>();
	
		for (ServiceNode predecessor : serviceNode.getPredecessors())
		{
			//Checking if the current predecessor produces any of the service inputs
			Set<String> predOutpSet = new HashSet<String>(predecessor.getService().getOutput());
			predOutpSet.retainAll(atmSvcInpSet);
			
			for (String output : predOutpSet)
			{
				svcInpPredMapping.put(output, predecessor.getService().getName());
			}
		}
	
		for (String input : atmSvcInpSet)
		{
			String inpName = input.substring(input.indexOf(':') + 2);
			String predName = svcInpPredMapping.get(input);
			if (predName != null)
			{
				svcDef += "\n\t\t\t\t\t\t\t\t\t" + "@.l_" + inpName + " oCAWS" + predName + "." + inpName + " ";
			}
			else
			{
				svcDef += "\n\t\t\t\t\t\t\t\t\t" + "@.l_" + inpName + " #.g_" + inpName + " ";
			}
		}
		
		svcDef = svcDef.trim();
		
		return svcDef;
	}
	
	private static String listAtmSvcDims(ServiceNode serviceNode)
	{
		String svcDef = "";
		Set<String> atmSvcInpSet = new HashSet<String>();		
		for (String input : serviceNode.getService().getInput())
		{
			String inpName = input.substring(input.indexOf(':') + 2);
			atmSvcInpSet.add(inpName);
		}
		
		svcDef += "\n\t\t\t\t\t\t\t\t\t\t" + "dimension ";
			
		for (String input : atmSvcInpSet)
		{
			svcDef += "l_"+ input + ", ";
		}
		if (svcDef.lastIndexOf(",") >= 0)
		{
			svcDef = svcDef.substring(0, svcDef.lastIndexOf(","));
		}
					
		svcDef += ";";
		
		return svcDef;
	}
	
	private static String listAtmSvcCnstrs(ServiceNode serviceNode)
	{
		String svcDef = "";
		for (Constraint constraint : serviceNode.getConstraints())
		{
			String cnstrFeature = constraint.getType();
			String cnstrFeatureName = cnstrFeature.substring(cnstrFeature.indexOf(':') + 2);
			String cnstrFeatureType = cnstrFeature.substring(0, cnstrFeature.indexOf(':') - 1);
			
			svcDef += "#.l_" + cnstrFeatureName + " " + getOpSymbol(constraint.getOperator()) + " ";
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