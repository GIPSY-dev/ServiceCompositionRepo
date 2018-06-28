package servicecomposition.compositionprocesses;

import java.util.ArrayList;
import java.util.List;
import servicecomposition.entities.CompositionPlan;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.SearchNode;
import servicecomposition.entities.ServiceNode;

/**
 * Class for constructing constraint-aware plans from simple composition plans.
 * This class also performs constraint adjustments on the constraint-aware plan service nodes.
 * @author Jyotsana Gupta
 */
public class ConstraintAwarePlanConstruction 
{
	/**
	 * Method for constructing constraint-aware plans from simple composition plans.
	 * It also triggers adjustment of constraints in the constraint-aware plans constructed.
	 * @param 	plans	List of simple composition plans that need to be converted into constraint-aware plans
	 * @return	List of the adjusted constraint-aware plans
	 */
	public static List<ConstraintAwarePlan> constructCAPlans(List<CompositionPlan> plans)
	{
		List<ConstraintAwarePlan> cnstrAwrPlans = new ArrayList<ConstraintAwarePlan>();
		
		for (CompositionPlan plan : plans)
		{
			//Constructing constraint-aware plans with empty service layers
			int planLayerCount = plan.getServiceLayers().size();
			ConstraintAwarePlan cnstrAwrPlan = new ConstraintAwarePlan(planLayerCount);			
			
			for (List<SearchNode> serviceLayer : plan.getServiceLayers())
			{
				for (SearchNode searchNode : serviceLayer)
				{
					//Creating a service node for every search node in the current composition plan
					ServiceNode serviceNode = new ServiceNode(searchNode.getService(), searchNode.getLayerIndex());
					
					//Adding the service node created to the current constraint-aware plan
					cnstrAwrPlan.addServiceNode(serviceNode);
					
					//Assigning predecessor and successor nodes to the current constraint-aware plan's nodes
					assignPredsSuccs(searchNode, serviceNode, cnstrAwrPlan);
				}
			}
			
			//Removing empty layers (if any) from the current constraint-aware plan
			cnstrAwrPlan.removeEmptyLayers();
			
			//Adjusting constraints in the current constraint-aware plan
			cnstrAwrPlan.adjustConstraints();
			
			//Adding the current constraint-aware plan to the list of plans to be returned
			cnstrAwrPlans.add(cnstrAwrPlan);
		}
		
		return cnstrAwrPlans;
	}
	
	/**
	 * Method for assigning predecessors and successors to constraint-aware plan service nodes.
	 * @param 	searchNode		Search node corresponding to the service node to which predecessors need to be assigned
	 * @param 	serviceNode		Service node to which predecessors need to be assigned
	 * @param 	cnstrAwrPlan	Constraint-aware plan to which the service node belongs
	 */
	private static void assignPredsSuccs(SearchNode searchNode, ServiceNode serviceNode, ConstraintAwarePlan cnstrAwrPlan)
	{
		for (SearchNode predecessor : searchNode.getPredecessors())
		{
			//Check if the current predecessor of the given search node exists in the given constraint-aware plan
			int predLayerIndex = predecessor.getLayerIndex();
			String predServiceName = predecessor.getService().getName();
			ServiceNode predServiceNode = cnstrAwrPlan.getServiceNode(predLayerIndex, predServiceName);
			
			//If a service node for the predecessor exists in the given constraint-aware plan
			if (predServiceNode != null)
			{
				//Assign the predecessor service node as a predecessor to the given service node
				serviceNode.addPredecessor(predServiceNode);
				
				//Assign the given service node as a successor to the predecessor service node
				predServiceNode.addSuccessor(serviceNode);
			}
		}
	}
}