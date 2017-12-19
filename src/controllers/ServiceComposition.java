package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import entities.CompositionRequest;
import entities.SearchGraph;
import service.Service;

/**
 * Class that controls the service composition process by providing methods for the major sub-processes involved.
 * @author Jyotsana Gupta
 */
public class ServiceComposition 
{	
	/**
	 * Uses forward expansion technique to create a search graph of service search nodes.
	 * @param	compositionReq	Service composition request by the user
	 * @param	serviceRepo		List of all available services (individual and composite) in the service repository
	 * @return	Search graph composed of all possible solution plans for the composition request submitted
	 * 			Null, if no possible solutions exist
	 */
	public SearchGraph forwardExpansion(CompositionRequest compositionReq, ArrayList<Service> serviceRepo)
	{
		SearchGraph searchGraph = new SearchGraph();		
		//Initialize the parameter set of the search graph with the inputs required in the composition request
		Set<String> prdSet = new HashSet<String>(compositionReq.getInputs());
		boolean serviceAdded = false;
		boolean problemSolvable = true;
		
		//Repeatedly loop through the service repository until no more services can be added to the search graph
		do
		{
			//List of services that will be added to the current service layer of the search graph 
			List<Service> addedLayerServices = new ArrayList<Service>();
			serviceAdded = false;
			
			//For each new layer of the search graph, check all the services in the repository
			for (Service service : serviceRepo)
			{
				//All inputs of the service being checked should exist in the current parameter set 
				boolean inputVerified = true;
				for (String serviceInput : service.getInput())
				{
					if (!(prdSet.contains(serviceInput)))
					{
						inputVerified = false;
						break;
					}
				}
				
				if (inputVerified)
				{
					//The outputs of the service being checked should have at least one additional parameter compared to the current parameter set
					boolean outputVerified = false;
					for (String serviceOutput : service.getOutput())
					{
						if (!(prdSet.contains(serviceOutput)))
						{
							outputVerified = true;
							break;
						}
					}
					
					if (outputVerified)
					{
						//Eligible service should be added to the search graph. Non-eligible services should be ignored.
						searchGraph.addService(service);
						
						/*
						 * CheckUserConstraints for verifying quality constraints is not required as of now.
						 * It can be added later. For now, we assume that quality constraints (if any) are always met.
						 */
						
						addedLayerServices.add(service);
						serviceAdded = true;						
					}
				}
			}
			
			//Add the outputs (that are not yet present in the parameter set) of the added services to the current parameter set
			for (Service addedService : addedLayerServices)
			{
				for (String serviceOutput : addedService.getOutput())
				{
					prdSet.add(serviceOutput);
				}
			}			
		} while(serviceAdded);
		
		//If the parameter set of the completed search graph contains all the outputs of the composition request, the request can be solved
		for (String requestOutput : compositionReq.getOutputs())
		{
			if (!(prdSet.contains(requestOutput)))
			{
				problemSolvable = false;
				break;
			}
		}
				
		if (problemSolvable)
		{
			return searchGraph;
		}
		else
		{
			return null;
		}
	}
}