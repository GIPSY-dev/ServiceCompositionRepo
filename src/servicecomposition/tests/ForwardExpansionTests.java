package servicecomposition.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ForwardExpansion;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.SearchGraph;
import servicecomposition.entities.SearchNode;
import service.Service;
import service.parser.BasicServiceParser;
import service.parser.ConstrainedServiceXMLParser;
import service.parser.ServiceFileParserDecorator;

/**
 * Class for testing the forward expansion process in various scenarios.
 * We populate only the inputs and outputs of test composition requests because 
 * QoS and constraints play no role in (and have no effect on) the forward expansion process.
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
		compositionReq.setInputs(Arrays.asList("int : inputXX", "char : inputYY"));
		compositionReq.setOutputs(Arrays.asList("int : output42", "string : output71"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "boolean : input21", "int : input22", "string : input31", "boolean : input32"));
		compositionReq.setOutputs(Arrays.asList("char : output32", "string : output71"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "char : input12", "float : output11", "string : output12", 
												"boolean : input21", "int : input22", "char : output21", "float : output22",
												"string : input31", "boolean : input32", "int : output31", "char : output32",
												"string : input42", "boolean : output41", "int : output42",
												"char : input51", "float : input52", "string : output51", "boolean : output52",
												"char : input61", "char : output61",
												"string : output71"));
		compositionReq.setOutputs(Arrays.asList("int : output42", "string : output71"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "char : input12", "boolean : input21", "int : input22", "string : input31", "boolean : input32"));
		compositionReq.setOutputs(Arrays.asList("char : output32", "string : output71"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("boolean : input21", "int : input22"));
		compositionReq.setOutputs(Arrays.asList("char : output21", "float : output22"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "char : input12", "boolean : input21", "int : input22", "string : input31", "boolean : input32"));
		compositionReq.setOutputs(Arrays.asList("string : outputXX", "boolean : outputYY"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "boolean : input21", "int : input22", "string : input42", "char : input61"));
		compositionReq.setOutputs(Arrays.asList("int : output42", "char : output61"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		compositionReq.setInputs(Arrays.asList("int : input11", "char : input12", 
												"boolean : input21", "int : input22", 
												"string : input31", "boolean : input32", 
												"string : input42", 
												"char : input51", "float : input52",
												"char : input61"));
		compositionReq.setOutputs(Arrays.asList("int : output42", "char : output61"));
		
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation("testinput/servicerepos/Test_Services_Set_1.xml");
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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