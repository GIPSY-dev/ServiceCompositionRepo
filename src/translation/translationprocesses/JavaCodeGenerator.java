package translation.translationprocesses;

import java.util.ArrayList;
import java.util.List;

import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;

public class JavaCodeGenerator 
{
	public static String generateJavaSegment(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compRequest)
	{
		String javaCode = "#JAVA"
						+ defineCompSvcClass(compRequest)
						+ defineAtmSvcClasses(cnstrAwrPlan);
		
		return javaCode;
	}
	
	private static String defineCompSvcClass(CompositionRequest compRequest)
	{
		String javaCode = "";
		
		javaCode += "\n\n" + "public class CAWSReqComp"
					+ "\n" + "{"
					+ listSvcDataMembers(compRequest.getOutputs())
					+ generateCompSvcCtor(compRequest)
					+ "\n" + "}";
		
		return javaCode;
	}
	
	private static String defineAtmSvcClasses(ConstraintAwarePlan cnstrAwrPlan)
	{
		String javaCode = "";
		
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				javaCode += defineAtmSvcClass(serviceNode);
				javaCode += defineAtmSvcFreeFn(serviceNode);
			}
		}
		
		return javaCode;
	}
	
	private static String listSvcDataMembers(List<String> dataMembers)
	{
		String javaCode = "";
		
		for (String output : dataMembers)
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			if (outpType.equalsIgnoreCase("string"))
			{
				outpType = "String";
			}
			else
			{
				outpType = outpType.toLowerCase();
			}
			
			javaCode += "\n\t" + "private " + outpType + " " + output.substring(output.indexOf(':') + 2) + ";";
		}
		
		return javaCode;
	}
	
	private static String generateCompSvcCtor(CompositionRequest compRequest)
	{
		String javaCode = "";
		String ctorParamCode = "";
		String dataMembInitCode = "";
		
		for (String output : compRequest.getOutputs())
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);
			
			if (outpType.equalsIgnoreCase("string"))
			{
				outpType = "String";
			}
			else
			{
				outpType = outpType.toLowerCase();
			}
			ctorParamCode += outpType + " " + outpName + ", ";
			
			dataMembInitCode += "\n\t\t" + "this." + outpName + " = " + outpName + ";";
		}
		if (ctorParamCode.lastIndexOf(",") >= 0)
		{
			ctorParamCode = ctorParamCode.substring(0, ctorParamCode.lastIndexOf(","));
		}
		
		javaCode += "\n\n\t" + "public CAWSReqComp(" + ctorParamCode + ")"
					+ "\n\t" + "{"
					+ dataMembInitCode
					+ "\n\t" + "}";
		
		return javaCode;
	}
	
	private static String defineAtmSvcClass(ServiceNode serviceNode)
	{
		String javaCode = "";
		List<String> dataMembers = new ArrayList<String>();
		dataMembers.addAll(serviceNode.getService().getInput());
		dataMembers.addAll(serviceNode.getService().getOutput());
		
		javaCode += "\n\n" + "public class CAWS" + serviceNode.getService().getName()
					+ "\n" + "{"
					+ listSvcDataMembers(dataMembers)
					+ generateAtmSvcCtor(serviceNode)
					+ defineAtmSvcProcess(serviceNode)
					+ "\n" + "}";
		
		return javaCode;
	}
	
	private static String defineAtmSvcFreeFn(ServiceNode serviceNode)
	{
		String freeFnParamCode = "";
		String ctorArgCode = "";
		
		for (String input : serviceNode.getService().getInput())
		{
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			if (inpType.equalsIgnoreCase("string"))
			{
				inpType = "String";
			}
			else
			{
				inpType = inpType.toLowerCase();
			}
			freeFnParamCode += inpType + " " + inpName + ", ";
			
			ctorArgCode += inpName + ", ";
		}
		if (freeFnParamCode.lastIndexOf(",") >= 0)
		{
			freeFnParamCode = freeFnParamCode.substring(0, freeFnParamCode.lastIndexOf(","));
			ctorArgCode = ctorArgCode.substring(0, ctorArgCode.lastIndexOf(","));
		}
		
		String svcName = serviceNode.getService().getName();
		String javaCode = "\n\n" + "public CAWS" + svcName + " " + svcName + "(" + freeFnParamCode + ")"
						+ "\n" + "{"
						+ "\n\t" + "CAWS" + svcName + " CAWS_" + svcName + " = new CAWS" + svcName + "(" + ctorArgCode + ");"
						+ "\n\t" + "CAWS_" + svcName + ".process();"
						+ "\n\t" + "return CAWS_" + svcName + ";"
						+ "\n" + "}";
		
		return javaCode;
	}
	
	private static String generateAtmSvcCtor(ServiceNode serviceNode)
	{
		String ctorParamCode = "";
		String dataMembInitCode = "";
		
		for (String input : serviceNode.getService().getInput())
		{
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			if (inpType.equalsIgnoreCase("string"))
			{
				inpType = "String";
			}
			else
			{
				inpType = inpType.toLowerCase();
			}
			ctorParamCode += inpType + " " + inpName + ", ";
			
			dataMembInitCode += "\n\t\t" + "this." + inpName + " = " + inpName + ";";
		}
		if (ctorParamCode.lastIndexOf(",") >= 0)
		{
			ctorParamCode = ctorParamCode.substring(0, ctorParamCode.lastIndexOf(","));
		}
		
		for (String output : serviceNode.getService().getOutput())
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);			
			dataMembInitCode += "\n\t\t" + outpName + " = " + getDefaultInitValue(outpType) + ";";
		}
		
		String javaCode = "\n\n\t" + "public CAWS" + serviceNode.getService().getName() + "(" + ctorParamCode + ")"
						+ "\n\t" + "{"
						+ dataMembInitCode
						+ "\n\t" + "}";
		
		return javaCode;
	}
	
	private static String defineAtmSvcProcess(ServiceNode serviceNode)
	{
		String processCode = "";
		
		for (String output : serviceNode.getService().getOutput())
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);			
			processCode += "\n\t\t" + outpName + " = " + getDefaultProcessedValue(outpType) + ";";
		}
		
		String javaCode = "\n\n\t" + "public void process()"
						+ "\n\t" + "{"
						+ processCode
						+ "\n\t" + "}";
		
		return javaCode;
	}
	
	private static String getDefaultInitValue(String dataType)
	{
		if (dataType.equalsIgnoreCase("int"))
		{
			return "0";
		}
		else if (dataType.equalsIgnoreCase("float"))
		{
			return "0.0";
		}
		else if (dataType.equalsIgnoreCase("boolean"))
		{
			return "false";
		}
		else if (dataType.equalsIgnoreCase("char"))
		{
			return "' '";
		}
		else if (dataType.equalsIgnoreCase("string"))
		{
			return "\" \"";
		}
		else
		{
			return null;
		}
	}
	
	private static String getDefaultProcessedValue(String dataType)
	{
		if (dataType.equalsIgnoreCase("int"))
		{
			return "10";
		}
		else if (dataType.equalsIgnoreCase("float"))
		{
			return "20.0";
		}
		else if (dataType.equalsIgnoreCase("boolean"))
		{
			return "true";
		}
		else if (dataType.equalsIgnoreCase("char"))
		{
			return "'a'";
		}
		else if (dataType.equalsIgnoreCase("string"))
		{
			return "\"test\"";
		}
		else
		{
			return null;
		}
	}
}