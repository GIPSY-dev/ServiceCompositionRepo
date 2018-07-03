package translation.translationprocesses;

import java.util.ArrayList;
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

public class ObjLucidCodeGenerator 
{	
	public static String generateObjLucidSegment(Service compService, List<String[]> compSvcInputs)
	{
		List<String> compSvcDims = new ArrayList<String>();
		
		String lucidCode = "#OBJECTIVELUCID"
							+ "\n\n" + "oCAWS_main @ [" + assignCompSvcInpContext(compSvcInputs) + "]"
							+ "\n" + "where" 
							+ "\n\t" + "dimension " + listCompSvcInpsOutps(compService.getInput(), compService.getOutput(), compSvcDims) + ";"
							+ defineOCAWSMain(compService.getOutput(), ((LayeredCompositeService)compService).getCompositionPlan(), compSvcDims)
							+ "\n" + "end;";
				
		return lucidCode;
	}
	
	private static String assignCompSvcInpContext(List<String[]> compSvcInputs)
	{
		String lucidCode = "";
		
		for (String[] input : compSvcInputs)
		{
			String inputVal = new String();
			if ((input[1].equalsIgnoreCase("string")) || (input[1].equalsIgnoreCase("char")))
			{
				inputVal = "'" + input[2] + "'";
			}
			else
			{
				inputVal = input[2];
			}
			lucidCode += input[0] + ":" + inputVal + ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
		
		return lucidCode;
	}
	
	private static String listCompSvcInpsOutps(List<String> csInputs, List<String> csOutputs, List<String> compSvcDims)
	{
		String lucidCode = ""; 
		List<String> compSvcInpsOutps = new ArrayList<String>();
		compSvcInpsOutps.addAll(csInputs);
		compSvcInpsOutps.addAll(csOutputs);
		
		for (String param : compSvcInpsOutps)
		{
			String dimension = param.substring(param.indexOf(':') + 2);
			lucidCode += dimension + ", ";
			compSvcDims.add(dimension);
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
		
		return lucidCode;
	}
	
	private static String defineOCAWSMain(List<String> csOutputs, ConstraintAwarePlan cnstrAwrPlan, List<String> compSvcDims)
	{
		String lucidCode = ""; 
		
		lucidCode += "\n\n\t" + "oCAWS_main = CAWSReqComp(" + listCompSvcOutpDims(csOutputs) + ")"
					+ "\n\t\t\t\t\t" + "wvr CAWSreq_cnstr"
					+ "\n\t\t\t\t\t" + "@ [" + "\t" + assignCompSvcOutpContext(csOutputs, cnstrAwrPlan) + " ]"
					+ "\n\t\t\t\t\t" + "where"
					+ "\n\t\t\t\t\t\t" + "CAWSreq_cnstr = true;"
					+ listAtomicSvcDefs(cnstrAwrPlan, compSvcDims)
					+ "\n\t\t\t\t\t" + "end;";
		
		return lucidCode;
	}
	
	private static String listCompSvcOutpDims(List<String> csOutputs)
	{
		String lucidCode = ""; 
		
		for (String output : csOutputs)
		{
			lucidCode += "#." + output.substring(output.indexOf(':') + 2) + ", ";
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
			lucidCode += "\n\t\t\t\t\t\t" + outpName + ":CAWS_" + svcName + "." + outpName + ", ";
		}
		
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
		lucidCode = lucidCode.trim();
		
		return lucidCode;
	}
		
	private static String listAtomicSvcDefs(ConstraintAwarePlan cnstrAwrPlan, List<String> compSvcDims)
	{
		String lucidCode = "";
		
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				lucidCode += defineAtomicSvc(serviceNode, compSvcDims);
			}
		}
		
		return lucidCode;
	}
	
	private static String defineAtomicSvc(ServiceNode serviceNode, List<String> compSvcDims)
	{
		String lucidCode = "";
		String svcDef = "";
		String svcName = serviceNode.getService().getName();
		
		svcDef += svcName + "(" + listAtmSvcInpDims(serviceNode) + ")" 
				+ "\n\t\t\t\t\t\t\t\t\t" + "wvr c_" + svcName
				+ "\n\t\t\t\t\t\t\t\t\t" + "@ [" + assignAtmSvcInpContext(serviceNode) + "]"
				+ "\n\t\t\t\t\t\t\t\t\t" + "where"
				+ listAtmSvcDims(serviceNode, compSvcDims)
				+ "\n\t\t\t\t\t\t\t\t\t\t" + "c_" + svcName + " = " + listAtmSvcCnstrs(serviceNode)
				+ "\n\t\t\t\t\t\t\t\t\t" + "end;";
		
		lucidCode += "\n\n\t\t\t\t\t\t" + "CAWS_" + svcName + " = " + svcDef;
		
		return lucidCode;
	}
	
	private static String listAtmSvcInpDims(ServiceNode serviceNode)
	{
		String svcDef = "";
		
		for (String input : serviceNode.getService().getInput())
		{
			svcDef += "#." + input.substring(input.indexOf(':') + 2) + ", ";
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
				svcDef += "\n\t\t\t\t\t\t\t\t\t\t" + inpName + ":CAWS_" + predName + "." + inpName + ", ";
			}
			else
			{
				svcDef += "\n\t\t\t\t\t\t\t\t\t\t" + inpName + ":#." + inpName + ", ";
			}
		}
		
		if (svcDef.lastIndexOf(",") >= 0)
		{
			svcDef = svcDef.substring(0, svcDef.lastIndexOf(","));
		}
		svcDef = svcDef.trim();
		
		return svcDef;
	}
	
	private static String listAtmSvcDims(ServiceNode serviceNode, List<String> compSvcDims)
	{
		String svcDef = "";
		Set<String> atmSvcInpSet = new HashSet<String>();		
		for (String input : serviceNode.getService().getInput())
		{
			String inpName = input.substring(input.indexOf(':') + 2);
			atmSvcInpSet.add(inpName);
		}
		
		atmSvcInpSet.removeAll(compSvcDims);
		
		if (atmSvcInpSet.size() > 0)
		{
			svcDef += "\n\t\t\t\t\t\t\t\t\t\t" + "dimension ";
			
			for (String input : atmSvcInpSet)
			{
				svcDef += input + ", ";
			}
			if (svcDef.lastIndexOf(",") >= 0)
			{
				svcDef = svcDef.substring(0, svcDef.lastIndexOf(","));
			}
						
			svcDef += ";";
		}
		
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
			
			svcDef += "#." + cnstrFeatureName + " " + getOpSymbol(constraint.getOperator()) + " ";
			if ((cnstrFeatureType.equalsIgnoreCase("string")) 
				|| (cnstrFeatureType.equalsIgnoreCase("char")))
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