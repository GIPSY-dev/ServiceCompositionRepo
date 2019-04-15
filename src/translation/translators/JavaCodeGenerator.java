package translation.translators;

import java.util.ArrayList;
import java.util.List;
import service.Service;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;
import service.composite.layeredcompsvc.LayeredCompositeService;

/**
 * Class for generating the Java segment of the Objective Lucid program translation of a layered composite service.
 * @author Jyotsana Gupta
 */
public class JavaCodeGenerator 
{
	/**
	 * Method for generating the Java segment for a given composite service.
	 * @param	compService		Layered composite service whose Java representation needs to be generated
	 * @return	Java segment generated
	 */
	public static String generateJavaSegment(Service compService)
	{
		//Generating and combining all sub-definitions to form the Java segment
		String javaCode = "#JAVA"
						+ defineCompSvcClass(compService.getOutput())
						+ defineAtmSvcClasses(((LayeredCompositeService)compService).getCompositionPlan());
		
		return javaCode;
	}
	
	/**
	 * Method for generating the Java class definition for the output accumulator node.
	 * @param	csOutputs	List of composite service outputs
	 * @return	Java class definition for the output accumulator node
	 */
	private static String defineCompSvcClass(List<String> csOutputs)
	{
		String javaCode = "";
		
		//Generating and combining all sub-definitions to form the Java class definition
		javaCode += "\n\n" + "public class CAWSReqComp"
					+ "\n" + "{"
					+ listSvcDataMembers(csOutputs)
					+ generateCompSvcCtor(csOutputs)
					+ "\n" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for generating Java class and free function definitions for all the component services.
	 * @param	cnstrAwrPlan	Constraint-aware plan of the composite service to be translated
	 * @return	Code block comprising of Java definitions representing all the component services
	 */
	private static String defineAtmSvcClasses(ConstraintAwarePlan cnstrAwrPlan)
	{
		String javaCode = "";
		
		//Generating and appending Java definition of each component service
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
	
	/**
	 * Method for transforming a given list of parameters into a list of Java class data member definitions.
	 * @param	dataMembers		List of parameters to serve as data members
	 * @return	Code block comprising of Java class data member definitions
	 */
	private static String listSvcDataMembers(List<String> dataMembers)
	{
		String javaCode = "";
		
		for (String output : dataMembers)
		{
			//Generating appropriate Java data type for each data member
			String outpType = output.substring(0, output.indexOf(':') - 1);
			if (outpType.equalsIgnoreCase("string"))
			{
				outpType = "String";
			}
			else if (outpType.equalsIgnoreCase("float"))
			{
				outpType = "double";
			}
			else
			{
				outpType = outpType.toLowerCase();
			}
			
			//Generating and appending data member definition for each parameter
			javaCode += "\n\t" + "private " + outpType + " " + output.substring(output.indexOf(':') + 2) + ";";
		}
		
		return javaCode;
	}
	
	/**
	 * Method for generating Java constructor definition for the output accumulator node.
	 * @param	csOutputs	List of composite service outputs
	 * @return	Java constructor definition for the output accumulator node
	 */
	private static String generateCompSvcCtor(List<String> csOutputs)
	{
		String javaCode = "";
		String ctorParamCode = "";
		String dataMembInitCode = "";
		
		for (String output : csOutputs)
		{
			//Extracting the name and data type of each composite service output
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);
			
			//Generating appropriate Java data type for each output parameter
			if (outpType.equalsIgnoreCase("string"))
			{
				outpType = "String";
			}
			else if (outpType.equalsIgnoreCase("float"))
			{
				outpType = "double";
			}
			else
			{
				outpType = outpType.toLowerCase();
			}
			
			//Appending the current output parameter to the list of constructor parameters
			ctorParamCode += outpType + " " + outpName + ", ";
			
			//Generating and adding the initialization statement for the current parameter to constructor body
			dataMembInitCode += "\n\t\t" + "this." + outpName + " = " + outpName + ";";
		}
		if (ctorParamCode.lastIndexOf(",") >= 0)
		{
			ctorParamCode = ctorParamCode.substring(0, ctorParamCode.lastIndexOf(","));
		}
		
		//Combining all generated statements to form the constructor definition
		javaCode += "\n\n\t" + "public CAWSReqComp(" + ctorParamCode + ")"
					+ "\n\t" + "{"
					+ dataMembInitCode
					+ "\n\t" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for generating Java class definition for a component service.
	 * @param 	serviceNode		Service node for which Java class needs to be generated
	 * @return	Java class definition for the given component service
	 */
	private static String defineAtmSvcClass(ServiceNode serviceNode)
	{
		String javaCode = "";
		
		//Defining the service's input and output parameters as its Java class data members
		List<String> dataMembers = new ArrayList<String>();
		dataMembers.addAll(serviceNode.getService().getInput());
		dataMembers.addAll(serviceNode.getService().getOutput());
		
		//Generating and combining all sub-definitions to form the Java class definition
		javaCode += "\n\n" + "public class CAWS" + serviceNode.getService().getName()
					+ "\n" + "{"
					+ listSvcDataMembers(dataMembers)
					+ generateAtmSvcCtor(serviceNode)
					+ defineAtmSvcProcess(serviceNode)
					+ "\n" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for generating Java free function definition for a component service.
	 * @param	serviceNode		Service node for which Java free function needs to be generated
	 * @return	Java free function definition for the given component service
	 */
	private static String defineAtmSvcFreeFn(ServiceNode serviceNode)
	{
		String freeFnParamCode = "";
		String ctorArgCode = "";
		
		for (String input : serviceNode.getService().getInput())
		{
			//Extracting the name and data type of each component service input
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			//Generating appropriate Java data type for each input parameter
			if (inpType.equalsIgnoreCase("string"))
			{
				inpType = "String";
			}
			else if (inpType.equalsIgnoreCase("float"))
			{
				inpType = "double";
			}
			else
			{
				inpType = inpType.toLowerCase();
			}
			
			//Appending the current input parameter to the list of method parameters
			freeFnParamCode += inpType + " " + inpName + ", ";
			
			//Appending the current input parameter to the list of constructor arguments
			ctorArgCode += inpName + ", ";
		}
		if (freeFnParamCode.lastIndexOf(",") >= 0)
		{
			freeFnParamCode = freeFnParamCode.substring(0, freeFnParamCode.lastIndexOf(","));
			ctorArgCode = ctorArgCode.substring(0, ctorArgCode.lastIndexOf(","));
		}
		
		//Combining all generated statements to form the free function definition
		String svcName = serviceNode.getService().getName();
		String javaCode = "\n\n" + "public CAWS" + svcName + " " + svcName + "(" + freeFnParamCode + ")"
						+ "\n" + "{"
						+ "\n\t" + "CAWS" + svcName + " oCAWS" + svcName + " = new CAWS" + svcName + "(" + ctorArgCode + ");"
						+ "\n\t" + "oCAWS" + svcName + ".process();"
						+ "\n\t" + "return oCAWS" + svcName + ";"
						+ "\n" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for generating constructor definition for a component service Java class.
	 * @param 	serviceNode		Service node for which constructor needs to be generated
	 * @return	Java constructor definition for the given component service
	 */
	private static String generateAtmSvcCtor(ServiceNode serviceNode)
	{
		String ctorParamCode = "";
		String dataMembInitCode = "";
		
		for (String input : serviceNode.getService().getInput())
		{
			//Extracting the name and data type of each component service input
			String inpType = input.substring(0, input.indexOf(':') - 1);
			String inpName = input.substring(input.indexOf(':') + 2);
			
			//Generating appropriate Java data type for each input parameter
			if (inpType.equalsIgnoreCase("string"))
			{
				inpType = "String";
			}
			else if (inpType.equalsIgnoreCase("float"))
			{
				inpType = "double";
			}
			else
			{
				inpType = inpType.toLowerCase();
			}
			
			//Appending the current input parameter to the list of constructor parameters
			ctorParamCode += inpType + " " + inpName + ", ";
			
			//Generating and adding the initialization statement for the current parameter to constructor body
			dataMembInitCode += "\n\t\t" + "this." + inpName + " = " + inpName + ";";
		}
		if (ctorParamCode.lastIndexOf(",") >= 0)
		{
			ctorParamCode = ctorParamCode.substring(0, ctorParamCode.lastIndexOf(","));
		}
		
		//Generating and adding the initialization statements for service output data members to constructor body
		for (String output : serviceNode.getService().getOutput())
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);			
			dataMembInitCode += "\n\t\t" + outpName + " = " + getDefaultInitValue(outpType) + ";";
		}
		
		//Combining all generated statements to form the constructor definition
		String javaCode = "\n\n\t" + "public CAWS" + serviceNode.getService().getName() + "(" + ctorParamCode + ")"
						+ "\n\t" + "{"
						+ dataMembInitCode
						+ "\n\t" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for generating process method definition for a component service Java class.
	 * @param	serviceNode		Service node for which method needs to be generated
	 * @return	Process method definition for the given component service
	 */
	private static String defineAtmSvcProcess(ServiceNode serviceNode)
	{
		String processCode = "";
		
		//Generating and adding the assignment statements for service output data members to method body
		for (String output : serviceNode.getService().getOutput())
		{
			String outpType = output.substring(0, output.indexOf(':') - 1);
			String outpName = output.substring(output.indexOf(':') + 2);			
			processCode += "\n\t\t" + outpName + " = " + getDefaultProcessedValue(outpType) + ";";
		}
		
		//Combining all generated statements to form the method definition
		String javaCode = "\n\n\t" + "public void process()"
						+ "\n\t" + "{"
						+ processCode
						+ "\n\t" + "}";
		
		return javaCode;
	}
	
	/**
	 * Method for fetching the default initialization values for component service output data members based on their data type.
	 * @param 	dataType	Java data type whose default initialization value is required
	 * @return	Default initialization value for the given data type
	 */
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
	
	/**
	 * Method for fetching the placeholder processed values for component service output data members based on their data type.
	 * @param 	dataType	Java data type whose placeholder processed value is required
	 * @return	Placeholder processed value for the given data type
	 */
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