package servicecomposition.entities;

import java.util.ArrayList;
import java.util.List;
import service.Service;

/**
 * Class for representing a service node of the search graph created by forward expansion.
 * @author Jyotsana Gupta
 */
public class SearchNode 
{
	private Service service;
	private List<SearchNode> predecessors;
	private List<SearchNode> successors;
	private int layerIndex;
	
	/**
	 * Default constructor.
	 */
	public SearchNode()
	{
		service = null;
		predecessors = new ArrayList<SearchNode>();
		successors = new ArrayList<SearchNode>();
		layerIndex = -1;								//Valid values are >= 0. -1 indicates node has not been added to a search graph.
	}
	
	/**
	 * Constructor with all data member values accepted as arguments.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	service			Individual service object forming this node
	 * @param	predecessors	List of search nodes that are predecessors to this node
	 * @param	successors		List of search nodes that are successors to this node
	 * @param	layerIndex		Index of the service layer in the search graph that contains this node
	 */
	public SearchNode(Service service, List<SearchNode> predecessors, List<SearchNode> successors, int layerIndex)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.service = service;
		
		this.predecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : predecessors)
		{
			this.predecessors.add(predecessor);
		}
		
		this.successors = new ArrayList<SearchNode>();
		for (SearchNode successor : successors)
		{
			this.successors.add(successor);
		}
		
		this.layerIndex = layerIndex;
	}
	
	/**
	 * Copy constructor.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	searchNode	Search node to be copied for creating this search node
	 */
	public SearchNode(SearchNode searchNode)
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.service = searchNode.getService();
		
		this.predecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : searchNode.getPredecessors())
		{
			this.predecessors.add(predecessor);
		}
		
		this.successors = new ArrayList<SearchNode>();
		for (SearchNode successor : searchNode.getSuccessors())
		{
			this.successors.add(successor);
		}
		
		this.layerIndex = searchNode.getLayerIndex();
	}

	/**
	 * Accessor method for the individual service object forming this node.
	 * @return	Service object of this search node
	 */
	public Service getService() 
	{
		return service;
	}

	/**
	 * Accessor method for the list of search nodes that are predecessors to this node.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of predecessor nodes
	 */
	public List<SearchNode> getPredecessors() 
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<SearchNode> retPredecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : predecessors)
		{
			retPredecessors.add(predecessor);
		}
		
		return retPredecessors;
	}

	/**
	 * Accessor method for the list of search nodes that are successors to this node.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @return	List of successor nodes
	 */
	public List<SearchNode> getSuccessors() 
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		List<SearchNode> retSuccessors = new ArrayList<SearchNode>();
		for (SearchNode successor : successors)
		{
			retSuccessors.add(successor);
		}
		
		return retSuccessors;
	}
	
	/**
	 * Accessor method for the index of the service layer in the search graph that contains this node.
	 * @return	Container service layer index
	 */
	public int getLayerIndex()
	{
		return layerIndex;
	}

	/**
	 * Mutator method for the individual service object forming this node.
	 * @param	service	Service object to be assigned to this search node
	 */
	public void setService(Service service) 
	{
		this.service = service;
	}

	/**
	 * Mutator method for the list of search nodes that are predecessors to this node.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	predecessors	List of predecessor nodes to be assigned to this search node
	 */
	public void setPredecessors(List<SearchNode> predecessors) 
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.predecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : predecessors)
		{
			this.predecessors.add(predecessor);
		}
	}

	/**
	 * Mutator method for the list of search nodes that are successors to this node.
	 * Lists are deep-copied so as to avoid unintended alteration from outside the class.
	 * @param	successors	List of successor nodes to be assigned to this search node
	 */
	public void setSuccessors(List<SearchNode> successors) 
	{
		//Lists are deep-copied so as to avoid unintended alteration from outside the class
		this.successors = new ArrayList<SearchNode>();
		for (SearchNode successor : successors)
		{
			this.successors.add(successor);
		}
	}
	
	/**
	 * Mutator method for the index of the service layer in the search graph that contains this node.
	 * @param	layerIndex	Container service layer index to be assigned to this node
	 */
	public void setLayerIndex(int layerIndex) 
	{
		this.layerIndex = layerIndex;
	}
}