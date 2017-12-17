package entities;

import java.util.ArrayList;
import java.util.List;
import service.Service;

public class SearchGraph 
{
	private List<List<SearchNode>> serviceLayers;
	private List<SearchNode> headServiceLayer;
	
	public SearchGraph()
	{
		this.serviceLayers = new ArrayList<List<SearchNode>>();
		this.headServiceLayer = new ArrayList<SearchNode>();
	}
	
	public SearchGraph(List<List<SearchNode>> serviceLayers)
	{
		this.serviceLayers = new ArrayList<List<SearchNode>>();		
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			List<SearchNode> newServiceLayer = new ArrayList<SearchNode>();
			for (SearchNode searchNode : serviceLayer)
			{
				SearchNode newSearchNode = new SearchNode(searchNode);
				newServiceLayer.add(newSearchNode);
			}
			this.serviceLayers.add(newServiceLayer);
		}
		
		headServiceLayer = this.serviceLayers.get(0);
	}
	
	public List<List<SearchNode>> getServiceLayers()
	{
		List<List<SearchNode>> retServiceLayers = new ArrayList<List<SearchNode>>();
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			List<SearchNode> newServiceLayer = new ArrayList<SearchNode>();
			for (SearchNode searchNode : serviceLayer)
			{
				SearchNode newSearchNode = new SearchNode(searchNode);
				newServiceLayer.add(newSearchNode);
			}
			retServiceLayers.add(newServiceLayer);
		}
		
		return retServiceLayers;
	}
	
	public List<SearchNode> getHeadServiceLayer()
	{
		return headServiceLayer;
	}
	
	public void setServiceLayers(List<List<SearchNode>> serviceLayers)
	{
		this.serviceLayers = new ArrayList<List<SearchNode>>();		
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			List<SearchNode> newServiceLayer = new ArrayList<SearchNode>();
			for (SearchNode searchNode : serviceLayer)
			{
				SearchNode newSearchNode = new SearchNode(searchNode);
				newServiceLayer.add(newSearchNode);
			}
			this.serviceLayers.add(newServiceLayer);
		}
		
		headServiceLayer = this.serviceLayers.get(0);
	}
	
	public void addService(Service service)
	{
		//TBD
	}
}