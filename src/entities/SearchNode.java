package entities;

import java.util.ArrayList;
import java.util.List;
import service.Service;

public class SearchNode 
{
	private Service service;
	private List<SearchNode> predecessors;
	private List<SearchNode> successors;
	
	public SearchNode()
	{
		service = null;
		predecessors = new ArrayList<SearchNode>();
		successors = new ArrayList<SearchNode>();
	}
	
	public SearchNode(Service service, List<SearchNode> predecessors, List<SearchNode> successors)
	{
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
	}
	
	public SearchNode(SearchNode searchNode)
	{
		this.service = searchNode.service;
		
		this.predecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : searchNode.predecessors)
		{
			this.predecessors.add(predecessor);
		}
		
		this.successors = new ArrayList<SearchNode>();
		for (SearchNode successor : searchNode.successors)
		{
			this.successors.add(successor);
		}
	}

	public Service getService() 
	{
		return service;
	}

	public List<SearchNode> getPredecessors() 
	{
		List<SearchNode> retPredecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : predecessors)
		{
			retPredecessors.add(predecessor);
		}
		
		return retPredecessors;
	}

	public List<SearchNode> getSuccessors() 
	{
		List<SearchNode> retSuccessors = new ArrayList<SearchNode>();
		for (SearchNode successor : successors)
		{
			retSuccessors.add(successor);
		}
		
		return retSuccessors;
	}

	public void setService(Service service) 
	{
		this.service = service;
	}

	public void setPredecessors(List<SearchNode> predecessors) 
	{
		this.predecessors = new ArrayList<SearchNode>();
		for (SearchNode predecessor : predecessors)
		{
			this.predecessors.add(predecessor);
		}
	}

	public void setSuccessors(List<SearchNode> successors) 
	{
		this.successors = new ArrayList<SearchNode>();
		for (SearchNode successor : successors)
		{
			this.successors.add(successor);
		}
	}
}