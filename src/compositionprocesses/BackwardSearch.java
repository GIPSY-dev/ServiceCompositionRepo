package compositionprocesses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import entities.CompositionRequest;
import entities.SearchGraph;
import entities.SearchNode;

/**
 * Class for extracting validated service plan sets from a search graph using the Backward Search algorithm.
 * @author Jyotsana Gupta
 */
public class BackwardSearch 
{
	/**
	 * Method for performing a backward search on the search graph constructed by the forward expansion algorithm.
	 * Its goal is to find all the validated sets of service search nodes (plan sets) that can be used to construct service plans.
	 * @param 	compositionReq	Service composition request from the user
	 * @param 	searchGraph		Search graph constructed by the forward expansion algorithm
	 * @return	List of all validated plan sets
	 */
	public static List<Set<SearchNode>> backwardSearch(CompositionRequest compositionReq, SearchGraph searchGraph)
	{
		List<Set<SearchNode>> allValidPlanSets = new ArrayList<Set<SearchNode>>();
		
		//Invoking backward search for all the service layers of the search graph
		//Each iteration uses a different layer as the starting layer
		for (int i = searchGraph.getServiceLayers().size() - 1; i >= 0; i--)
		{
			List<SearchNode> startingLayer = searchGraph.getServiceLayer(i);
			Set<SearchNode> serviceSet = new HashSet<SearchNode>(startingLayer);
			Set<SearchNode> branchPlanSet = new HashSet<SearchNode>();
			List<Set<SearchNode>> validPlanSets = new ArrayList<Set<SearchNode>>();
			
			depthFirstTraversal(serviceSet, compositionReq, i, branchPlanSet, validPlanSets);
			allValidPlanSets.addAll(validPlanSets);
		}
		
		return allValidPlanSets;
	}
	
	/**
	 * Creates service power sets and processes their elements using depth-first search mechanism.
	 * Processing starts at the given layer and proceeds down to the first layer in the search graph.
	 * @param 	serviceSet			Set of services whose power set will be constructed and processed
	 * @param 	compositionReq		Service composition request from the user, if this method is called by the backwardSearch method
	 * 								Null, if this method is called recursively 
	 * @param 	currentLayerIndex	Index of the service layer currently being processed
	 * @param 	branchPlanSet		Set of search nodes that together constitute 1 branch of this depth-first traversal
	 * 								It is constructed as the traversal progresses.
	 * @param 	allValidPlanSets	List of all branch plan sets that are complete and successfully validated in this entire traversal
	 */
	private static void depthFirstTraversal(Set<SearchNode> serviceSet, CompositionRequest compositionReq, 
											int currentLayerIndex, Set<SearchNode> branchPlanSet, List<Set<SearchNode>> allValidPlanSets)
	{
		//Creating the power set of the input service set
		Set<Set<SearchNode>> planPowerSet = getPowerSet(serviceSet);
		
		//Processing each element of the power set
		for (Set<SearchNode> planSet : planPowerSet)
		{
			//Ignoring the null set element of the power set
			if(planSet.size() > 0)
			{
				//Checking if the current plan set generates at least 1 output from the composition request
				//This check needs to be done only for the starting layer
				if (compositionReq != null)
				{
					boolean outputVerified = false;
					List<String> planSetOutputs = new ArrayList<String>();
					for (SearchNode searchNode : planSet)
					{
						planSetOutputs.addAll(searchNode.getService().getOutput());
					}
					for (String reqOutput : compositionReq.getOutputs())
					{
						if (planSetOutputs.contains(reqOutput))
						{
							outputVerified = true;
							break;
						}
					}
					if (!outputVerified)
					{
						continue;
					}
				}
				
				//Adding the current plan set to the plan set constructed until the next higher layer 
				//for collecting all the services that constitute the current branch of traversal
				Set<SearchNode> extendedBranchPlanSet = new HashSet<SearchNode>();
				extendedBranchPlanSet.addAll(branchPlanSet);
				extendedBranchPlanSet.addAll(planSet);
				
				if (currentLayerIndex > 0)
				{
					//Creating a set of predecessors of each service in the plan set
					//These predecessors must belong to the layer immediately preceding the current layer
					int previousLayerIndex = currentLayerIndex - 1;
					Set<SearchNode> preSet = new HashSet<SearchNode>();
					for (SearchNode searchNode : planSet)
					{
						List<SearchNode> predecessors = searchNode.getPredecessors();
						for (SearchNode predecessor : predecessors)
						{
							if (predecessor.getLayerIndex() == previousLayerIndex)
							{
								preSet.add(predecessor);
							}
						}
					}
					
					//Invoking depth-first creation and traversal of power sets on the current predecessor set
					//Null composition request indicates that the traversal is called for a non-starting layer
					depthFirstTraversal(preSet, null, previousLayerIndex, extendedBranchPlanSet, allValidPlanSets);
				}
				else
				{
					//Creating a list of all the outputs generated by the current branch's services
					List<String> planSetOutputs = new ArrayList<String>();
					for (SearchNode searchNode : extendedBranchPlanSet)
					{
						planSetOutputs.addAll(searchNode.getService().getOutput());
					}
					
					//Checking if the current branch generates all the requested outputs
					boolean isPlanSetValid = true;
					for (String reqOutput : compositionReq.getOutputs())
					{
						if (!(planSetOutputs.contains(reqOutput)))
						{
							isPlanSetValid = false;
							break;
						}
					}
					
					//If the current plan set is validated successfully and if it contains more than 1 service,
					//it is added to the list of valid plan sets
					if ((isPlanSetValid) && (extendedBranchPlanSet.size() > 1))
					{
						allValidPlanSets.add(extendedBranchPlanSet);
					}
				}
			}
		}
	}
	
	/**
	 * Method for creating the power set of a given set (in this case, a set of services/search nodes).
	 * @param 	serviceSet	The service set whose power set needs to be constructed
	 * @return	Power set of the given set of services/search nodes
	 */
	private static Set<Set<SearchNode>> getPowerSet(Set<SearchNode> serviceSet)
	{
		//TODO implementation
		return null;
	}
}