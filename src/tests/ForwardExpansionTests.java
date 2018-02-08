package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import compositionprocesses.ForwardExpansion;
import entities.CompositionRequest;
import entities.SearchGraph;
import entities.SearchNode;
import service.Service;
import service.ServiceParser;
import service.ServiceXMLParser;

/**
 * Class for testing the forward expansion process in various scenarios.
 * We populate only the inputs and outputs of test composition requests because QoS and constraints play no role in (and have no effect on) the Forward Expansion process.
 * @author Jyotsana Gupta
 */
public class ForwardExpansionTests 
{
	/**
	 * Tests failure of forward expansion when the composition request inputs cannot satisfy the inputs of any repository service. 
	 */
	@Test
	public void inputVerificationFailure()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("inputXX", "inputYY"));
		compositionReq.setOutputs(Arrays.asList("output42", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		assertNull(resultingGraph);
	}
	
	/**
	 * Tests that only those services are included in the search graph that have all their inputs satisfied.
	 */
	@Test
	public void inputVerificationSuccess()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input21", "input22", "input31", "input32"));
		compositionReq.setOutputs(Arrays.asList("output32", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
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
		expectedServiceNames.addAll(Arrays.asList("sname2", "sname3", "sname7"));
		
		assertEquals(resultingServiceNames, expectedServiceNames);
	}
	
	/**
	 * Tests failure of forward expansion when none of the repository services can add a new output parameter to the existing set of parameters.
	 * For this test, all the output parameters of all the repository services have been provided as inputs in the composition request.
	 */
	@Test
	public void outputVerificationFailure()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input12", "output11", "output12", 
												"input21", "input22", "output21", "output22",
												"input31", "input32", "output31", "output32",
												"input42", "output41", "output42",
												"input51", "input52", "output51", "output52",
												"input61", "output61",
												"output71"));
		compositionReq.setOutputs(Arrays.asList("output42", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		assertNull(resultingGraph);
	}
	
	/**
	 * Tests that all (and only) those services are included in the search graph that can add a new output parameter to the existing set of parameters.
	 * This is based on the assumption that other required checks for these services are successful as well. 
	 */
	@Test
	public void outputVerificationSuccess()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input12", "input21", "input22", "input31", "input32"));
		compositionReq.setOutputs(Arrays.asList("output32", "output71"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
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
	
	/**
	 * Tests failure of forward expansion when the search graph is created successfully with just one service node.
	 * Since this implies that the composition request can be served by a single service and does not need a composite service, the request is invalid.
	 */
	@Test
	public void singleServiceSolution()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input21", "input22"));
		compositionReq.setOutputs(Arrays.asList("output21", "output22"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		assertNull(resultingGraph);
	}
	
	/**
	 * Tests failure of forward expansion when the repository services cannot be composed together to provide the requested output parameters.
	 */
	@Test
	public void unsolvableProblem()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input12", "input21", "input22", "input31", "input32"));
		compositionReq.setOutputs(Arrays.asList("outputXX", "outputYY"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		assertNull(resultingGraph);
	}
	
	/**
	 * Tests that services with different names but same input-output requirements are allowed in the same search graph.
	 */
	@Test
	public void duplicateInputOutputServices()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input21", "input22", "input42", "input61"));
		compositionReq.setOutputs(Arrays.asList("output42", "output61"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
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
		expectedServiceNames.addAll(Arrays.asList("sname10", "sname2", "sname4", "sname6"));
		
		assertEquals(resultingServiceNames, expectedServiceNames);
	}
	
	/**
	 * Tests correct assignment of predecessors and successors to search nodes in a search graph.
	 * Also tests that the same service is not repeated in a search graph. For example, sname1 (layer 0) can have sname7 (layer 1) as a predecessor but it is already present and, therefore, not added again.
	 */
	@Test
	public void verifyPredecessorsSuccessors()
	{
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(Arrays.asList("input11", "input12", 
												"input21", "input22", 
												"input31", "input32", 
												"input42", 
												"input51", "input52",
												"input61"));
		compositionReq.setOutputs(Arrays.asList("output42", "output61"));
		
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		//Creating a String with all the nodes and their predecessor-successor details
		List<List<SearchNode>> serviceLayers = resultingGraph.getServiceLayers();
		String actualOutput = "";
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			actualOutput = actualOutput + "\nLayer " + serviceLayers.indexOf(serviceLayer) + ": ";
			for (SearchNode searchNode : serviceLayer)
			{
				boolean hasPredecessor = false, hasSuccessor = false;
				
				//Adding predecessor service names for the current node to the output
				actualOutput = actualOutput + "{";
				for (SearchNode predecessor : searchNode.getPredecessors())
				{
					actualOutput = actualOutput + predecessor.getService().getName() + ", ";
					hasPredecessor = true;
				}
				if (hasPredecessor)
				{
					actualOutput = actualOutput.substring(0, actualOutput.lastIndexOf(","));
				}
				
				//Adding name of the current node to the output
				actualOutput = actualOutput + "} Node " + serviceLayer.indexOf(searchNode)
								+ " (" + searchNode.getService().getName() + ") {";
				
				//Adding successor service names for the current node to the output
				for (SearchNode successor : searchNode.getSuccessors())
				{
					actualOutput = actualOutput + successor.getService().getName() + ", ";
					hasSuccessor = true;
				}
				if (hasSuccessor)
				{
					actualOutput = actualOutput.substring(0, actualOutput.lastIndexOf(","));
				}
				actualOutput = actualOutput + "}, ";
			}
			actualOutput = actualOutput.substring(0, actualOutput.lastIndexOf(","));
		}
		
		actualOutput = actualOutput.trim();
		String expectedOutput = "Layer 0: {} Node 0 (sname1) {}, {} Node 1 (sname2) {sname4, sname6, sname10}, {} Node 2 (sname3) {sname7}, {} Node 3 (sname5) {}" + "\n"
								+ "Layer 1: {sname2} Node 0 (sname4) {sname6, sname10}, {sname3} Node 1 (sname7) {sname6, sname10}" + "\n"
								+ "Layer 2: {sname2, sname4, sname7} Node 0 (sname6) {}, {sname2, sname4, sname7} Node 1 (sname10) {}";
		assertEquals(actualOutput, expectedOutput);
	}
}