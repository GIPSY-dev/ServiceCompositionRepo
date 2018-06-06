package translation.translationprocesses;

import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;

public class JavaCodeGenerator 
{
	public static String generateJavaSegment(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compRequest)
	{
		String javaCode = "#JAVA"
						+ "\n\n" + "public class CAWSReqComp"
						+ "\n" + "{"
						+ listCompSvcDataMembs(compRequest)
						+ generateCompSvcCtor(compRequest)
						+ "\n" + "}";
		
		return javaCode;
	}
	
	private static String listCompSvcDataMembs(CompositionRequest compRequest)
	{
		String javaCode = "";
		
		for (String output : compRequest.getOutputs())
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
}