package entities;

import java.util.ArrayList;
import java.util.List;
import service.Service;

/**
 * Class representing the search graph of services created using the forward expansion technique.
 * @author Jyotsana Gupta
 */
public class SearchGraph 
{
	private List<List<SearchNode>> serviceLayers;
	private List<SearchNode> headServiceLayer;
	
	/**
	 * Default constructor.
	 */
	public SearchGraph()
	{
		this.serviceLayers = new ArrayList<List<SearchNode>>();
		this.headServiceLayer = new ArrayList<SearchNode>();
	}
	
	/**
	 * Constructor with some data member values accepted as arguments.
	 * The head service layer is set to the first layer of this graph.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	serviceLayers	List of all service layers constituting this search graph
	 */
	public SearchGraph(List<List<SearchNode>> serviceLayers)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
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
		
		this.headServiceLayer = new ArrayList<SearchNode>();		//Required in case this search graph is empty
		if (!this.serviceLayers.isEmpty())
		{
			//Head service layer is not copied. It points to the actual search graph layer.
			headServiceLayer = this.serviceLayers.get(0);
		}
	}
	
	/**
	 * Accessor method for the list of service layers constituting this search graph.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of all constituent service layers
	 */
	public List<List<SearchNode>> getServiceLayers()
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
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
	
	/**
	 * Accessor method for the head (first) layer of this search graph.
	 * Head service layer is not copied. It can be used to access and modify this search graph if required.
	 * @return	Head layer of this search graph
	 */
	public List<SearchNode> getHeadServiceLayer()
	{
		//Head service layer is not copied. It points to the actual search graph layer.
		return headServiceLayer;
	}
	
	/**
	 * Mutator method for the list of service layers constituting this search graph.
	 * The head service layer is set to the first layer of this graph.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	serviceLayers	List of service layers that would together constitute this search graph
	 */
	public void setServiceLayers(List<List<SearchNode>> serviceLayers)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
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
		
		if (!this.serviceLayers.isEmpty())
		{
			//Head service layer is not copied. It points to the actual search graph layer.
			headServiceLayer = this.serviceLayers.get(0);
		}
		else
		{
			this.headServiceLayer = new ArrayList<SearchNode>();		//Required in case this search graph is emptied
		}
	}
	
	/**
	 * Finds the correct position for the intended service in this search graph and adds it there.
	 * @param	newService	Service to be added
	 */
	public void addService(Service newService)
	{
		//Fetching all inputs of the service to be added
		List<String> newIn = newService.getInput();
		
		//Create a new search node for the service to be added
		SearchNode newSearchNode = new SearchNode();
		newSearchNode.setService(newService);
		
		//Loop through all the service layers of the search graph to find all the predecessors of the service to be added
		for (List<SearchNode> serviceLayer : this.serviceLayers)
		{
			//Check every search node in a service layer if it is a predecessor
			for (SearchNode searchNode : serviceLayer)
			{
				List<String> nodeOutputs = searchNode.getService().getOutput();
				boolean isPredecessor = false;
				
				//At least one output of an eligible predecessor node would be taken as an input by the new service node
				for (String input : newIn)
				{
					if (nodeOutputs.contains(input))
					{
						isPredecessor = true;
						break;
					}
				}
				
				//If the current search node is a predecessor node
				if(isPredecessor)
				{
					//Add the search node as a predecessor to the new service node
					List<SearchNode> currPredecessors = newSearchNode.getPredecessors();
					currPredecessors.add(searchNode);
					newSearchNode.setPredecessors(currPredecessors);
					
					//Add the new service node as a successor to the search node
					List<SearchNode> nodeSuccessors = searchNode.getSuccessors();
					nodeSuccessors.add(newSearchNode);
					searchNode.setSuccessors(nodeSuccessors);
				}
			}
		}
		
		if (newSearchNode.getPredecessors().isEmpty())
		{
			//If there are no predecessors, the new service node belongs to service layer 0
			newSearchNode.setLayerIndex(0);
		}
		else
		{
			//If predecessors exist, find the last layer in which a predecessor exists
			int maxPredLayerIndex = 0;
			for (SearchNode predecessor : newSearchNode.getPredecessors())
			{
				int currPredLayerIndex = predecessor.getLayerIndex();
				if (currPredLayerIndex > maxPredLayerIndex)
				{
					maxPredLayerIndex = currPredLayerIndex;
				}
			}
			
			//The new service node belongs to the layer after the last one containing its predecessors
			newSearchNode.setLayerIndex(maxPredLayerIndex + 1);
		}
		
		//If the new service node has predecessors in the last layer of the existing search graph
		if (newSearchNode.getLayerIndex() > (this.getServiceLayers().size() - 1))
		{
			//Create a new service layer with the new service node and add it at the end of the search graph 
			List<SearchNode> newServiceLayer = new ArrayList<SearchNode>();
			newServiceLayer.add(newSearchNode);
			this.serviceLayers.add(newServiceLayer);
			
			//Set the head service layer if the first service layer of the search graph has just been created
			if (newSearchNode.getLayerIndex() == 0)
			{
				this.headServiceLayer = this.serviceLayers.get(0);
			}
		}
		else
		{
			//If the new service node belongs to an existing service layer
			this.serviceLayers.get(newSearchNode.getLayerIndex()).add(newSearchNode);
		}
	}
}