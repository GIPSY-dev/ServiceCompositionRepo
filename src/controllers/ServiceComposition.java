package controllers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import entities.CompositionRequest;
import entities.SearchGraph;
import service.Service;

public class ServiceComposition 
{	
	public SearchGraph forwardExpansion(CompositionRequest compositionReq, ArrayList<Service> serviceRepo)
	{
		SearchGraph searchGraph = new SearchGraph();
		Set<String> prdSet = new HashSet<String>(compositionReq.getInputs());
		boolean serviceAdded = false;
		boolean problemSolvable = true;
		
		do
		{
			serviceAdded = false;
			
			for (Service service : serviceRepo)
			{
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
						searchGraph.addService(service);
						
						/*
						 * CheckUserConstraints for verifying quality constraints is not required as of now.
						 * It can be added later. For now, we assume that quality constraints (if any) are always met.
						 */
						
						for (String serviceOutput : service.getOutput())
						{
							prdSet.add(serviceOutput);
						}			
						
						serviceAdded = true;						
					}
				}
			}
		} while(serviceAdded);
		
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