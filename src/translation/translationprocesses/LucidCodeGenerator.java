package translation.translationprocesses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;

public class LucidCodeGenerator 
{	
	public static void cnstrAwrPlanToLucid(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compRequest, List<String[]> compSvcInputs)
	{
		String lucidCode = "oCAWS_main @ [";
		assignCompSvcInpContext(lucidCode, compSvcInputs);
		lucidCode += "]"
					+ "\n" + "where" 
					+ "\n\t" + "dimension ";
		listCompSvcInpsOutps(lucidCode, compRequest);
		lucidCode += ";";
		defineOCAWSMain(lucidCode, compRequest, cnstrAwrPlan);
		lucidCode += "\n" + "end;";
	}
	
	private static void assignCompSvcInpContext(String lucidCode, List<String[]> compSvcInputs)
	{
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
	}
	
	private static void listCompSvcInpsOutps(String lucidCode, CompositionRequest compRequest)
	{
		List<String> compSvcInpsOutps = new ArrayList<String>();
		compSvcInpsOutps.addAll(compRequest.getInputs());
		compSvcInpsOutps.addAll(compRequest.getOutputs());
		
		for (String param : compSvcInpsOutps)
		{
			lucidCode += param.substring(param.indexOf(':') + 2) + ", ";
		}
		
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
	}
	
	private static void defineOCAWSMain(String lucidCode, CompositionRequest compRequest, ConstraintAwarePlan cnstrAwrPlan)
	{
		lucidCode += "\n\t" + "oCAWS_main = CAWSReqComp(";
		listCompSvcOutpDims(lucidCode, compRequest);
		lucidCode += ")"
					+ "\n\t\t\t\t\t" + "wvr CAWSreq_cnstr"
					+ "\n\t\t\t\t\t" + "@ [" + "\t";
		assignCompSvcOutpContext(lucidCode, compRequest, cnstrAwrPlan);
		lucidCode += "]"
					+ "\n\t\t\t\t\t" + "where"
					+ "\n\t\t\t\t\t\t" + "CAWSreq_cnstr = true;";
		listAtomicSvcDefs(lucidCode, cnstrAwrPlan);
		lucidCode += "\n\t\t\t\t\t" + "end;";
	}
	
	private static void listCompSvcOutpDims(String lucidCode, CompositionRequest compRequest)
	{
		for (String output : compRequest.getOutputs())
		{
			lucidCode += "#." + output.substring(output.indexOf(':') + 2) + ", ";
		}
		
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
	}
	
	private static void assignCompSvcOutpContext(String lucidCode, CompositionRequest compRequest, ConstraintAwarePlan cnstrAwrPlan)
	{
		Set<String> compReqOutputSet = new HashSet<String>(compRequest.getOutputs());
		Map<String, String> compSvcOutpMapping = new HashMap<String, String>();
		String outpContextCode = "";
	
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
			outpContextCode += "\n\t\t\t\t\t\t" + outpName + ":CAWS_" + svcName + "." + outpName + ", ";
		}
		
		if (outpContextCode.lastIndexOf(",") >= 0)
		{
			outpContextCode = outpContextCode.substring(0, outpContextCode.lastIndexOf(","));
		}
		outpContextCode = outpContextCode.trim();
		
		lucidCode += outpContextCode;
	}
		
	private static void listAtomicSvcDefs(String lucidCode, ConstraintAwarePlan cnstrAwrPlan)
	{
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				defineAtomicSvc(serviceNode);
			}
		}
	}
	
	private static void defineAtomicSvc(ServiceNode serviceNode)
	{
		//TODO implement
	}
}