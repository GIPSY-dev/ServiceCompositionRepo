package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
				String predDetails = "";
				for (ServiceNode predecessor : serviceNode.getPredecessors())
				{
					predDetails += predecessor.getService().getName() + ", ";
				}
				if (predDetails.lastIndexOf(",") >= 0)
				{
					predDetails = predDetails.substring(0, predDetails.lastIndexOf(","));
				}	
				
				//Gathering successor service names for the current service node 
				String succDetails = "";
				for (ServiceNode successor : serviceNode.getSuccessors())
				{
					succDetails += successor.getService().getName() + ", ";
				}
				if (succDetails.lastIndexOf(",") >= 0)
				{
					succDetails = succDetails.substring(0, succDetails.lastIndexOf(","));
				}	
				
				//Combining predecessor, successor and service names for the current service node 
				String serviceDetails = "{" + predDetails + "} " 
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