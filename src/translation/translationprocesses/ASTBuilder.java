package translation.translationprocesses;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.ServiceNode;
import translation.entities.ASTNode;
import translation.entities.DataASTNode;
import translation.entities.OutputAccumASTNode;
import translation.entities.ParExecOpASTNode;
import translation.entities.SeqExecOpASTNode;
import translation.entities.ServiceASTNode;

public class ASTBuilder 
{
	public ASTNode cnstrAwrPlanToAST(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compositionReq)
	{
		int lastLayerIndex = cnstrAwrPlan.getServiceLayerCount() - 1;
		Set<String> compReqOutputSet = new HashSet<String>(compositionReq.getOutputs());
		ASTNode accumChildList = null;
				
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				if (serviceNode.getLayerIndex() != lastLayerIndex)
				{
					Set<String> svcNodeOutputSet = new HashSet<String>(serviceNode.getService().getOutput());
					svcNodeOutputSet.retainAll(compReqOutputSet);
					
					if (svcNodeOutputSet.size() == 0)
					{
						continue;
					}
				}
				
				ASTNode predList = getPredecessors(serviceNode);
				ASTNode parExecOpAST = new ParExecOpASTNode();
				parExecOpAST.adoptChildren(predList);
				
				ASTNode svcAST = new ServiceASTNode(serviceNode);
				
				ASTNode seqExecOpAST = new SeqExecOpASTNode();
				parExecOpAST.makeSiblings(svcAST);
				seqExecOpAST.adoptChildren(parExecOpAST);
				
				if (accumChildList == null)
				{
					accumChildList = seqExecOpAST;
				}
				else
				{
					accumChildList.makeSiblings(seqExecOpAST);
				}
			}
		}
		
		ASTNode outputAccumAST = new OutputAccumASTNode();
		outputAccumAST.adoptChildren(accumChildList);
				
		return outputAccumAST;
	}
	
	private ASTNode getPredecessors(ServiceNode serviceNode)
	{
		ASTNode svcPredList = null;
		Set<String> predProducedInputSet = new HashSet<String>();
		Set<String> svcInputSet = new HashSet<String>(serviceNode.getService().getInput());
		
		for (ServiceNode predecessor : serviceNode.getPredecessors())
		{
			Set<String> currPredProdInpSet = new HashSet<String>(predecessor.getService().getOutput());
			currPredProdInpSet.retainAll(svcInputSet);
			predProducedInputSet.addAll(currPredProdInpSet);
			
			ASTNode predList = getPredecessors(predecessor);
			ASTNode parExecOpAST = new ParExecOpASTNode();
			parExecOpAST.adoptChildren(predList);
			
			ASTNode svcAST = new ServiceASTNode(serviceNode);
			
			ASTNode seqExecOpAST = new SeqExecOpASTNode();
			parExecOpAST.makeSiblings(svcAST);
			seqExecOpAST.adoptChildren(parExecOpAST);
			
			if (svcPredList == null)
			{
				svcPredList = seqExecOpAST;
			}
			else
			{
				svcPredList.makeSiblings(seqExecOpAST);
			}
		}
		
		Set<String> reqProvidedInputSet = new HashSet<String>(svcInputSet);
		reqProvidedInputSet.removeAll(predProducedInputSet);
		for (String input : reqProvidedInputSet)
		{
			ASTNode dataAST = new DataASTNode(input);
			
			if (svcPredList == null)
			{
				svcPredList = dataAST;
			}
			else
			{
				svcPredList.makeSiblings(dataAST);
			}
		}
		
		return svcPredList;
	}
}