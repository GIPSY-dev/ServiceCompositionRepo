package servicecomposition.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Class representing a service composition plan constructed from the plan set generated by the Backward Search algorithm.
 * @author Jyotsana Gupta
 */
public class CompositionPlan 
{
	private List<List<SearchNode>> serviceLayers;
	
	/**
	 * Parameterized constructor.
	 * It creates as many empty service layers for this composition plan as the layer count provided as input. 
	 * @param	layerCount	Number of service layers to be contained by this composition plan
	 */
	public CompositionPlan(int layerCount)
	{
		serviceLayers = new ArrayList<List<SearchNode>>();		
		for (int i = 0; i < layerCount; i++)
		{
			serviceLayers.add(new ArrayList<SearchNode>());
		}
	}
	
	/**
	 * Method for fetching all the service layers of this composition plan.
	 * Lists are not deep-copied here so as to allow removal of invalid search nodes from the service layers.
	 * @return	List of service layers of this composition plan
	 */
	public List<List<SearchNode>> getServiceLayers()
	{
		return serviceLayers;
	}
	
	/**
	 * Method for adding all the services in a plan set to this composition plan based on each search node's layer index.
	 * @param 	planSet		Plan set containing the search nodes (services) to be added
	 */
	public void addSearchNodes(Set<SearchNode> planSet)
	{
		int planLayerCount = serviceLayers.size();
		for (SearchNode searchNode : planSet)
		{
			int layerIndex = searchNode.getLayerIndex();
			if (planLayerCount > layerIndex)
			{
				serviceLayers.get(layerIndex).add(searchNode);
			}			
		}
	}
	
	/**
	 * Method for removing search nodes from this composition plan based on each search node's layer index.
	 * @param 	nodesToBeRemoved	List of search nodes to be removed
	 */
	public void removeSearchNodes(List<SearchNode> nodesToBeRemoved)
	{
		for (SearchNode searchNode : nodesToBeRemoved)
		{
			//Removing the current search node from its service layer
			int nodeLayerIndex = searchNode.getLayerIndex();
			serviceLayers.get(nodeLayerIndex).remove(searchNode);
		}
	}
	
	/**
	 * Overridden toString method for Composition Plan class.
	 * @return	String containing details of this composition plan
	 */
	@Override
	public String toString()
	{
		String planString = "";
		List<List<String>> planServiceNames = new ArrayList<List<String>>();
		
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			List<String> layerServiceNames = new ArrayList<String>();
			for (SearchNode searchNode : serviceLayer)
			{
				layerServiceNames.add(searchNode.getService().getName());
			}
			Collections.sort(layerServiceNames);
			planServiceNames.add(layerServiceNames);
		}
		
		for (int i = 0; i < planServiceNames.size(); i++)
		{
			planString += "\nLayer " + i + ": ";
			for (String serviceName : planServiceNames.get(i))
			{
				planString += serviceName + ", ";
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