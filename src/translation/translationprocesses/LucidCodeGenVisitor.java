package translation.translationprocesses;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import servicecomposition.entities.ServiceNode;
import translation.entities.ASTNode;
import translation.entities.DataASTNode;
import translation.entities.OutputAccumASTNode;
import translation.entities.ParExecOpASTNode;
import translation.entities.SeqExecOpASTNode;
import translation.entities.ServiceASTNode;

public class LucidCodeGenVisitor extends Visitor
{
	private String lucidCode = null;
	private Map<String, String> definitions = null;
	
	public LucidCodeGenVisitor()
	{
		lucidCode = new String();
		definitions = new HashMap<String, String>();
	}
			
	public String getLucidCode()
	{
		return lucidCode;
	}
	
	public void visit(OutputAccumASTNode outputAccumASTNode)		
	{
		//Generating Lucid code for the expression
		lucidCode += "outputaccum(";
		
		for (ASTNode child : outputAccumASTNode.getChildren())
		{
			child.accept(this);
			lucidCode += ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
				
		lucidCode += ")";
		
		//Generating Lucid code for the definitions
		lucidCode += "\nwhere";
		
		definitions.put("outputaccum", getOADefinition(outputAccumASTNode));
		for (Entry<String, String> definition : definitions.entrySet())
		{
			lucidCode += "\n\t" + definition.getValue();
		}
		
		lucidCode += "\nend";
		
		//TODO definition code generation
	}
	
	public void visit(SeqExecOpASTNode seqExecOpASTNode)		
	{
		seqExecOpASTNode.getChildren().get(1).accept(this);
		lucidCode += "(";
		
		seqExecOpASTNode.getChildren().get(0).accept(this);
		lucidCode += ")";
	}
	
	public void visit(ParExecOpASTNode parExecOpASTNode)		
	{
		for (ASTNode child : parExecOpASTNode.getChildren())
		{
			child.accept(this);
			lucidCode += ", ";
		}
		if (lucidCode.lastIndexOf(",") >= 0)
		{
			lucidCode = lucidCode.substring(0, lucidCode.lastIndexOf(","));
		}
	}
	
	public void visit(ServiceASTNode serviceASTNode)		
	{
		ServiceNode serviceNode = (ServiceNode)serviceASTNode.getValue();	
		String serviceName = serviceNode.getService().getName();
		lucidCode += serviceName;
		definitions.put(serviceName, getServiceDefinition(serviceASTNode));
	}
	
	public void visit(DataASTNode dataASTNode)		
	{
		String dataName = dataASTNode.getValue().toString();
		lucidCode += dataName;		
		definitions.put(dataName, getDataDefinition(dataASTNode));
	}
	
	public void visit(ASTNode astNode)
	{
		for (ASTNode child : astNode.getChildren())
		{
			child.accept(this);
		}
	}
	
	private String getOADefinition(OutputAccumASTNode outputAccumASTNode)
	{
		String outputAccumDef = "outputaccum(";
		//TODO include params with unique names
		outputAccumDef += ") = ";
		outputAccumDef += "<accumulation logic>";						//TODO replace dummy logic with actual logic
		outputAccumDef += ";";
		return outputAccumDef;
	}
	
	private String getServiceDefinition(ServiceASTNode serviceASTNode)
	{
		String serviceDef = null;
		//TODO
		return serviceDef;
	}
	
	private String getDataDefinition(DataASTNode dataASTNode)
	{
		String dataValue = "10";											//TODO replace dummy value with actual value
		String dataDef = dataASTNode.getValue() + " = " + dataValue + ";";
		return dataDef;
	}
}