package servicecomposition.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import constraint.Constraint;
import service.ConstrainedService;

/**
 * Class for representing a constraint-aware service composition plan.
 * @author Jyotsana Gupta
 */
public class ConstraintAwarePlan 
{
	private List<List<ServiceNode>> serviceLayers;
	
	/**
	 * Parameterized constructor.
	 * It creates as many empty service layers for this plan as the layer count provided as input. 
	 * @param	layerCount	Number of service layers to be contained by this constraint-aware composition plan
	 */
	public ConstraintAwarePlan(int layerCount)
	{
		serviceLayers = new ArrayList<List<ServiceNode>>();		
		for (int i = 0; i < layerCount; i++)
		{
			serviceLayers.add(new ArrayList<ServiceNode>());
		}
	}
	
	/**
	 * Method for fetching the number of service layers in this constraint-aware composition plan.
	 * @return	Number of service layers in this plan
	 */
	public int getServiceLayerCount()
	{
		return serviceLayers.size();
	}
	
	/**
	 * Method for fetching a specific service node from this constraint-aware composition plan.
	 * @param 	layerIndex		Index of the service layer that contains the target node
	 * @param 	serviceName		Name of the service that the target node represents
	 * @return	Target service node, if found in the plan
	 * 			Null, otherwise
	 */
	public ServiceNode getServiceNode(int layerIndex, String serviceName)
	{		
		int planLayerCount = serviceLayers.size();
		if (planLayerCount > layerIndex)
		{
			for (ServiceNode serviceNode : serviceLayers.get(layerIndex))
			{
				if (serviceNode.getService().getName().equals(serviceName))
				{
					return serviceNode;
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Method for fetching all the service layers of this constraint-aware composition plan.
	 * @return	List of service layers of this constraint-aware composition plan
	 */
	public List<List<ServiceNode>> getServiceLayers()
	{
		return serviceLayers;
	}
	
	/**
	 * Method for adding a service node to this constraint-aware composition plan.
	 * @param 	serviceNode		Service node to be added
	 */
	public void addServiceNode(ServiceNode serviceNode)
	{
		int planLayerCount = serviceLayers.size();
		int layerIndex = serviceNode.getLayerIndex();
		if (planLayerCount > layerIndex)
		{
			serviceLayers.get(layerIndex).add(serviceNode);
		}
	}
	
	/**
	 * Method for removing the empty service layers from this constraint-aware composition plan.
	 */
	public void removeEmptyLayers()
	{
		//Finding and removing the empty service layers
		boolean emptyLayersFound = false;
		int planLayerCount = serviceLayers.size();
		for (int i = 0; i < planLayerCount; i++)
		{
			int layerServiceCount = serviceLayers.get(i).size();
			if (layerServiceCount == 0)
			{
				emptyLayersFound = true;
				serviceLayers.remove(i);
				planLayerCount--;
				i--;
			}
		}
		
		//Adjusting the service layer index of all the service nodes after removal of service layers
		if (emptyLayersFound)
		{
			for (int i = 0; i < planLayerCount; i++)
			{
				List<ServiceNode> currLayer = serviceLayers.get(i);
				for (int j = 0; j < currLayer.size(); j++)
				{
					currLayer.get(j).setLayerIndex(i);
				}
			}
		}
	}
	
	/**
	 * Method for adjusting the constraints in this plan to be able to execute them as early as possible. 
	 */
	public void adjustConstraints()
	{
		//For each service layer (starting from the second one) in this plan
		int planLayerCount = serviceLayers.size();
		for (int i = 1; i < planLayerCount; i++)
		{
			//For each service node in the current layer
			List<ServiceNode> currLayer = serviceLayers.get(i);
			int layerServiceCount = currLayer.size();
			for (int j = 0; j < layerServiceCount; j++)
			{
				//For each constraint in the service object of the current service node
				ServiceNode currServiceNode = currLayer.get(j);
				ConstrainedService currConstrService = (ConstrainedService) currServiceNode.getService();
				List<Constraint> adjConstraints = currConstrService.getConstraints();
				int adjCnstrCount = adjConstraints.size();
				for (int k = 0; k < adjCnstrCount; k++)
				{
					boolean constraintAdjusted = false;
					boolean currNodeGetsContsraint = false;
					Constraint currConstraint = adjConstraints.get(k);
					Set<ServiceNode> predSet = new HashSet<ServiceNode>();
					predSet.addAll(currServiceNode.getPredecessors());
					
					//For each layer before the current layer
					for (int l = i - 1; l >= 0; l--)
					{
						//Creating a set of predecessors to be checked with the highest layer index
						Set<ServiceNode> closestPredSet = new HashSet<ServiceNode>();
						for (ServiceNode predecessor : predSet)
						{
							if (predecessor.getLayerIndex() == l)
							{
								closestPredSet.add(predecessor);
							}
						}
						
						//For each of the highest layer predecessors
						for (ServiceNode closestPred : closestPredSet)
						{
							//Checking if the predecessor affects the current constraint's feature
							if (((ConstrainedService) closestPred.getService()).getEffects().contains(currConstraint.getType()))
							{
								//If yes, adding the constraint to the predecessor's successor nodes
								constraintAdjusted = true;
								List<ServiceNode> successorList = closestPred.getSuccessors();								
								int successorCount = successorList.size();
								for (int m = 0; m < successorCount; m++)
								{
									successorList.get(m).addConstraint(currConstraint);
									
									//If the current service node is one of the successor
									//nodes to get the constraint during adjustment									
									if (successorList.get(m) == currServiceNode)
									{
										currNodeGetsContsraint = true;
									}
								}
							}
							else
							{
								//If not, adding the predecessor's predecessor nodes in the predecessor set
								//and removing the predecessor from that set
								predSet.addAll(closestPred.getPredecessors());
								predSet.remove(closestPred);
							}
						}
						
						//If the current constraint was adjusted successfully
						if (constraintAdjusted)
						{
							if (!currNodeGetsContsraint)
							{
								//If the current service node does not receive this constraint during
								//adjustment, remove it from the node and adjust the next constraint
								currServiceNode.removeConstraint(currConstraint);
							}
							
							break;
						}
					}
					
					//If the constraint feature is not affected by any predecessors
					if (!constraintAdjusted)
					{
						//Move the constraint to the beginning of this plan
						List<ServiceNode> firstLayer = serviceLayers.get(0);
						int fstLayerSvcCount = firstLayer.size();
						for (int n = 0; n < fstLayerSvcCount; n++)
						{
							firstLayer.get(n).addConstraint(currConstraint);
						}
						currServiceNode.removeConstraint(currConstraint);
					}
				}
			}
		}
	}
	
	/**
	 * Overridden toString method for Constraint Aware Plan class.
	 * @return	String containing details of this constraint-aware composition plan
	 */
	@Override
	public String toString()
	{
		String planString = "";
		List<List<String>> planServiceDetails = new ArrayList<List<String>>();
		
		for (List<ServiceNode> serviceLayer : serviceLayers)
		{
			List<String> layerServiceDetails = new ArrayList<String>();
			for (ServiceNode serviceNode : serviceLayer)
			{
				//Gathering predecessor service names for the current service node
				List<String> predNames = new ArrayList<String>();
				for (ServiceNode predecessor : serviceNode.getPredecessors())
				{
					predNames.add(predecessor.getService().getName());
				}
				Collections.sort(predNames);
				
				String predDetails = "";
				for (String predName : predNames)
				{
					predDetails += predName + ", ";
				}
				if (predDetails.lastIndexOf(",") >= 0)
				{
					predDetails = predDetails.substring(0, predDetails.lastIndexOf(","));
				}
				
				//Gathering successor service names for the current service node 
				List<String> succNames = new ArrayList<String>();
				for (ServiceNode successor : serviceNode.getSuccessors())
				{
					succNames.add(successor.getService().getName());
				}
				Collections.sort(succNames);
				
				String succDetails = "";
				for (String succName : succNames)
				{
					succDetails += succName + ", ";
				}
				if (succDetails.lastIndexOf(",") >= 0)
				{
					succDetails = succDetails.substring(0, succDetails.lastIndexOf(","));
				}
				
				//Gathering constraint details for the current service node
				List<String> constraintStrs = new ArrayList<String>();
				for (Constraint constraint : serviceNode.getConstraints())
				{
					String cnstrStr = constraint.getType() + " " 
										+ constraint.getOperator() + " "
										+ constraint.getLiteralValue();
					constraintStrs.add(cnstrStr);
				}
				Collections.sort(constraintStrs);
				
				String cnstrDetails = "";
				for (String cnstrStr : constraintStrs)
				{
					cnstrDetails += cnstrStr + ", ";
				}
				if (cnstrDetails.lastIndexOf(",") >= 0)
				{
					cnstrDetails = cnstrDetails.substring(0, cnstrDetails.lastIndexOf(","));
				}
				
				//Combining predecessor, successor and service names for the current service node 
				String serviceDetails = "{" + predDetails + "} " 
										+ "[" + cnstrDetails + "] "
										+ serviceNode.getService().getName() 
										+ " {" + succDetails + "}";
				layerServiceDetails.add(serviceDetails);
			}
			Collections.sort(layerServiceDetails);
			planServiceDetails.add(layerServiceDetails);
		}
		
		//Preparing a single string with all the required details for this plan
		for (int i = 0; i < planServiceDetails.size(); i++)
		{
			planString += "\nLayer " + i + ": ";
			for (String serviceDetails : planServiceDetails.get(i))
			{
				planString += serviceDetails + ", ";
			}
			if (planString.lastIndexOf(",") >= 0)
			{
				planString = planString.substring(0, planString.lastIndexOf(","));
			}		
		}
		planString = planString.trim();
		
		return planString;
	}
}