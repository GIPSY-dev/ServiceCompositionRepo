package entities;

import java.util.ArrayList;
import java.util.List;
import constraint.Constraint;
import service.Service;

/**
 * Class for representing a service node of constraint-aware plans created from composition plans.
 * @author Jyotsana Gupta
 */
public class ServiceNode 
{
	private Service service;
	private List<Constraint> constraints;
	private List<ServiceNode> predecessors;
	private List<ServiceNode> successors;
	private int layerIndex;
	
	/**
	 * Parameterized constructor.
	 * It assigns service, layer index and constraints to this service node. 
	 * Blank lists are created for predecessors and successors.
	 * @param 	service		Individual service that this node represents
	 * @param 	layerIndex	Index of the service layer in the constraint-aware plan that contains this node
	 */
	public ServiceNode(Service service, int layerIndex)
	{
		this.service = service;
		this.layerIndex = layerIndex;
		predecessors = new ArrayList<ServiceNode>();
		successors = new ArrayList<ServiceNode>();		
		constraints = new ArrayList<Constraint>();
		if (service.getConstraints() != null) 
		{
			for (Constraint constraint : service.getConstraints())
			{
				//Constraint data members are deep-copied to keep them independent of the service data member
				//This enables constraints to be assigned to different services during constraint-adjustment
				Constraint newConstraint = new Constraint(constraint.getServiceName(), constraint.getLiteralValue(), constraint.getType(), constraint.getOperator());
				constraints.add(newConstraint);
			}
		}
	}
	
	/**
	 * Method for fetching the individual service that this node represents.
	 * @return	Service object of this node
	 */
	public Service getService() 
	{
		return service;
	}
	
	/**
	 * Method for fetching the service nodes that are predecessors to this node.
	 * @return	List of predecessors of this node
	 */
	public List<ServiceNode> getPredecessors() 
	{
		return predecessors;
	}
	
	/**
	 * Method for fetching the service nodes that are successors to this node.
	 * @return	List of successors of this node
	 */
	public List<ServiceNode> getSuccessors() 
	{
		return successors;
	}
	
	/**
	 * Method for fetching the index of the service layer in the constraint-aware plan that contains this node.
	 * @return	Container service layer index
	 */
	public int getLayerIndex()
	{
		return layerIndex;
	}
	
	/**
	 * Method for assigning a container service layer index to this service node.
	 * @param 	layerIndex	Container service layer index to be assigned
	 */
	public void setLayerIndex(int layerIndex)
	{
		this.layerIndex = layerIndex;
	}
	
	/**
	 * Method for adding a predecessor to this node.
	 * @param 	predecessor		Service node to be added as a predecessor to this node
	 */
	public void addPredecessor(ServiceNode predecessor)
	{
		predecessors.add(predecessor);
	}
	
	/**
	 * Method for adding a successor to this node.
	 * @param 	successor		Service node to be added as a successor to this node
	 */
	public void addSuccessor(ServiceNode successor)
	{
		successors.add(successor);
	}
}