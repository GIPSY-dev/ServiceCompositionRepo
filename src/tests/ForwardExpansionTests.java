package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import controllers.ServiceComposition;
import entities.CompositionRequest;
import entities.SearchGraph;
import entities.SearchNode;
import service.Service;
import service.ServiceParser;
import service.ServiceXMLParser;

public class ForwardExpansionTests 
{
	@Test
	public void inputVerificationFailure()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("inputXX", "inputYY"));
		compositionReq.setOutputs(Arrays.asList("output42", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ServiceComposition.forwardExpansion(compositionReq, serviceRepo);
		
		assertNull(resultingGraph);
	}
	
	@Test
	public void outputVerificationFailure()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input12", "input21", "input22", "input31", "input32"));
		compositionReq.setOutputs(Arrays.asList("output32", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ServiceComposition.forwardExpansion(compositionReq, serviceRepo);
		
		List<String> resultingServiceNames = new ArrayList<String>();
		if (resultingGraph != null)
		{
			for (List<SearchNode> serviceLayer: resultingGraph.getServiceLayers())
			{
				for (SearchNode serviceNode : serviceLayer)
				{
					resultingServiceNames.add(serviceNode.getService().getName());
				}
			}
		}
		resultingServiceNames.sort(String::compareToIgnoreCase);
		
		List<String> expectedServiceNames = new ArrayList<String>();
		expectedServiceNames.addAll(Arrays.asList("sname1", "sname2", "sname3", "sname7"));
		
		assertEquals(resultingServiceNames, expectedServiceNames);
	}
}