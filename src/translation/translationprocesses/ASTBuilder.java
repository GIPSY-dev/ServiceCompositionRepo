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

/**
 * Class for translating a constraint-aware composition plan into an abstract syntax tree required for code generation.
 * @author Jyotsana Gupta
 */
public class ASTBuilder 
{
	/**
	 * Method for translating a constraint-aware composition plan into an AST.
	 * @param 	cnstrAwrPlan		Source constraint-aware plan
	 * @param 	compositionReq		Composition request for which the plan was generated
	 * @return	Root node of the AST representing the source composition plan.
	 * 			The root node can be used to access the entire AST.
	 */
	public ASTNode cnstrAwrPlanToAST(ConstraintAwarePlan cnstrAwrPlan, CompositionRequest compositionReq)
	{
		int lastLayerIndex = cnstrAwrPlan.getServiceLayerCount() - 1;
		Set<String> compReqOutputSet = new HashSet<String>(compositionReq.getOutputs());
		ASTNode accumChildList = null;
				
		//Creating a sibling list of subtrees for execution of all the service nodes that produce requested outputs
		for (List<ServiceNode> serviceLayer : cnstrAwrPlan.getServiceLayers())
		{
			for (ServiceNode serviceNode : serviceLayer)
			{
				//If the current service node does not belong to the last layer in the plan
				if (serviceNode.getLayerIndex() != lastLayerIndex)
				{
					//Checking if the node produces any of the requested outputs
					Set<String> svcNodeOutputSet = new HashSet<String>(serviceNode.getService().getOutput());
					svcNodeOutputSet.retainAll(compReqOutputSet);
					
					//If not, it should not be added as a child to the output accumulator node
					if (svcNodeOutputSet.size() == 0)
					{
						continue;
					}
				}
				
				//Creating a subtree of predecessors of the current service node
				//Root of this subtree is a parallel execution operator
				ASTNode predList = getPredecessors(serviceNode);
				ASTNode parExecOpAST = new ParExecOpASTNode();
				parExecOpAST.adoptChildren(predList);
				
				//Creating an AST node for the current service node
				ASTNode svcAST = new ServiceASTNode(serviceNode);
				
				//Creating a subtree of the current service node and its predecessors
				//Root of this subtree is a sequential execution operator
				ASTNode seqExecOpAST = new SeqExecOpASTNode();
				parExecOpAST.makeSiblings(svcAST);
				seqExecOpAST.adoptChildren(parExecOpAST);
				
				//Adding the current service subtree to the child list of output accumulator node
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
		
		//Creating a family with output accumulator node as the parent and the service subtrees as its children
		//This family represents the entire AST for the source plan
		ASTNode outputAccumAST = new OutputAccumASTNode();
		outputAccumAST.adoptChildren(accumChildList);
				
		return outputAccumAST;
	}
	
	/**
	 * Method for creating a sibling list of all predecessor subtrees and input parameter data nodes required for a given service node.
	 * A predecessor subtree consists of all the nodes required for the execution of that predecessor.
	 * @param 	serviceNode		Service node whose predecessor list needs to be created
	 * @return	Leftmost AST node from the required list of siblings
	 */
	private ASTNode getPredecessors(ServiceNode serviceNode)
	{
		ASTNode svcPredList = null;
		Set<String> predProducedInputSet = new HashSet<String>();
		Set<String> svcInputSet = new HashSet<String>(serviceNode.getService().getInput());
		
		//Preparing a sibling list of all predecessor subtrees
		for (ServiceNode predecessor : serviceNode.getPredecessors())
		{
			//Extracting the outputs produced by the current predecessor that serve as input for the service node
			Set<String> currPredProdInpSet = new HashSet<String>(predecessor.getService().getOutput());
			currPredProdInpSet.retainAll(svcInputSet);
			predProducedInputSet.addAll(currPredProdInpSet);
			
			//Creating a subtree of predecessors of the current predecessor node
			//Root of this subtree is a parallel execution operator
			ASTNode predList = getPredecessors(predecessor);
			ASTNode parExecOpAST = new ParExecOpASTNode();
			parExecOpAST.adoptChildren(predList);
			
			//Creating an AST node for the current predecessor node
			ASTNode svcAST = new ServiceASTNode(predecessor);
			
			//Creating a subtree of the current predecessor node and its predecessors
			//Root of this subtree is a sequential execution operator
			ASTNode seqExecOpAST = new SeqExecOpASTNode();
			parExecOpAST.makeSiblings(svcAST);
			seqExecOpAST.adoptChildren(parExecOpAST);
			
			//Adding the current predecessor subtree to the sibling list of predecessors
			if (svcPredList == null)
			{
				svcPredList = seqExecOpAST;
			}
			else
			{
				svcPredList.makeSiblings(seqExecOpAST);
			}
		}
		
		//Creating a set of inputs for the service node that are provided by the user, not by another service
		Set<String> reqProvidedInputSet = new HashSet<String>(svcInputSet);
		reqProvidedInputSet.removeAll(predProducedInputSet);
		
		//Creating a data node for each of these inputs and adding them to the sibling list of predecessors
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