package entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CompositionPlan 
{
	private List<List<SearchNode>> serviceLayers;
	private List<SearchNode> headServiceLayer;
	
	public CompositionPlan(int layerCount)
	{
		serviceLayers = new ArrayList<List<SearchNode>>();		
		for (int i = 0; i < layerCount; i++)
		{
			serviceLayers.add(new ArrayList<SearchNode>());
		}
		
		if (!serviceLayers.isEmpty())
		{
			headServiceLayer = serviceLayers.get(0);
		}
		else
		{
			headServiceLayer = new ArrayList<SearchNode>();				//In case layerCount = 0
		}
	}
	
	public List<List<SearchNode>> getServiceLayers()
	{
		return serviceLayers;
	}
	
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
}