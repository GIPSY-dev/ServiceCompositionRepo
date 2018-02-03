package tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import constraint.Constraint;
import constraint.Operator;
import entities.SearchGraph;
import entities.SearchNode;
import service.Service;

/**
 * Class for testing addition of services (search nodes) to search graphs.
 * @author Jyotsana Gupta
 */
public class SearchGraphTests 
{
	/**
	 * Tests creation of a new search graph with a single service (search node).
	 */
	@Test
	public void addOneService()
	{
		SearchGraph searchGraph = new SearchGraph();
		
		ArrayList<String> inputs = new ArrayList<String>();
		inputs.addAll(Arrays.asList("input11", "input12"));
		ArrayList<String> outputs = new ArrayList<String>();
		outputs.addAll(Arrays.asList("output11", "output12"));
		ArrayList<Constraint> constraints = new ArrayList<Constraint>();
		constraints.add(new Constraint("sname1", "lit11", "type11", Operator.EQUALS));
		constraints.add(new Constraint("sname1", "lit12", "type12", Operator.LESS_THAN));
		ArrayList<String> effects = new ArrayList<String>();
		effects.addAll(Arrays.asList("eff11", "eff12"));
		Service service = new Service("sname1", inputs, outputs, constraints, effects);
		
		searchGraph.addService(service);
		
		String actualOutput = getSearchGraphDetails(searchGraph);
		String expectedOutput = "Layer 0: Node 0 (sname1)";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Tests addition of multiple services (search nodes) to an empty search graph.
	 */
	@Test
	public void addManySvcsInOneLayer()
	{
		SearchGraph searchGraph = getSingleLayerGraph();
		
		String actualOutput = getSearchGraphDetails(searchGraph);
		String expectedOutput = "Layer 0: Node 0 (sname1), Node 1 (sname2), Node 2 (sname3)";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Tests addition of multiple services (search nodes) to an existing layer in a search graph.
	 */
	@Test
	public void addSvcsToExistingLayer()
	{
		SearchGraph searchGraph = getSingleLayerGraph();
		
		ArrayList<String> inputs4 = new ArrayList<String>();
		inputs4.addAll(Arrays.asList("input41", "input42"));
		ArrayList<String> outputs4 = new ArrayList<String>();
		outputs4.addAll(Arrays.asList("output41", "output42"));
		ArrayList<Constraint> constraints4 = new ArrayList<Constraint>();
		constraints4.add(new Constraint("sname4", "lit41", "type41", Operator.EQUALS));
		constraints4.add(new Constraint("sname4", "lit42", "type42", Operator.LESS_THAN));
		ArrayList<String> effects4 = new ArrayList<String>();
		effects4.addAll(Arrays.asList("eff41", "eff42"));
		Service service4 = new Service("sname4", inputs4, outputs4, constraints4, effects4);
		
		ArrayList<String> inputs5 = new ArrayList<String>();
		inputs5.addAll(Arrays.asList("input51", "input52"));
		ArrayList<String> outputs5 = new ArrayList<String>();
		outputs5.addAll(Arrays.asList("output51", "output52"));
		ArrayList<Constraint> constraints5 = new ArrayList<Constraint>();
		constraints5.add(new Constraint("sname5", "lit51", "type51", Operator.EQUALS));
		constraints5.add(new Constraint("sname5", "lit52", "type52", Operator.LESS_THAN));
		ArrayList<String> effects5 = new ArrayList<String>();
		effects5.addAll(Arrays.asList("eff51", "eff52"));
		Service service5 = new Service("sname5", inputs5, outputs5, constraints5, effects5);
		
		searchGraph.addService(service4);
		searchGraph.addService(service5);
		
		String actualOutput = getSearchGraphDetails(searchGraph);
		String expectedOutput = "Layer 0: Node 0 (sname1), Node 1 (sname2), Node 2 (sname3), Node 3 (sname4), Node 4 (sname5)";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Tests creation of a new layer of services (search nodes) in an existing search graph.
	 */
	@Test
	public void addSvcsToNewLayer()
	{
		SearchGraph searchGraph = getSingleLayerGraph();
		
		ArrayList<String> inputs4 = new ArrayList<String>();
		inputs4.addAll(Arrays.asList("output22", "input42"));
		ArrayList<String> outputs4 = new ArrayList<String>();
		outputs4.addAll(Arrays.asList("output41", "output42"));
		ArrayList<Constraint> constraints4 = new ArrayList<Constraint>();
		constraints4.add(new Constraint("sname4", "lit41", "type41", Operator.EQUALS));
		constraints4.add(new Constraint("sname4", "lit42", "type42", Operator.LESS_THAN));
		ArrayList<String> effects4 = new ArrayList<String>();
		effects4.addAll(Arrays.asList("eff41", "eff42"));
		Service service4 = new Service("sname4", inputs4, outputs4, constraints4, effects4);
		
		ArrayList<String> inputs5 = new ArrayList<String>();
		inputs5.addAll(Arrays.asList("output12", "output31"));
		ArrayList<String> outputs5 = new ArrayList<String>();
		outputs5.addAll(Arrays.asList("output51", "output52"));
		ArrayList<Constraint> constraints5 = new ArrayList<Constraint>();
		constraints5.add(new Constraint("sname5", "lit51", "type51", Operator.EQUALS));
		constraints5.add(new Constraint("sname5", "lit52", "type52", Operator.LESS_THAN));
		ArrayList<String> effects5 = new ArrayList<String>();
		effects5.addAll(Arrays.asList("eff51", "eff52"));
		Service service5 = new Service("sname5", inputs5, outputs5, constraints5, effects5);
		
		searchGraph.addService(service4);
		searchGraph.addService(service5);
		
		String actualOutput = getSearchGraphDetails(searchGraph);
		String expectedOutput = "Layer 0: Node 0 (sname1), Node 1 (sname2), Node 2 (sname3)" + "\n"
								+ "Layer 1: Node 0 (sname4), Node 1 (sname5)";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Tests addition of multiple services (search nodes) to existing as well as new layers in a search graph.
	 */
	@Test
	public void addSvcsToMultipleLayers()
	{
		SearchGraph searchGraph = getMultiLayerGraph();
		
		String actualOutput = getSearchGraphDetails(searchGraph);
		String expectedOutput = "Layer 0: Node 0 (sname1), Node 1 (sname2), Node 2 (sname3), Node 3 (sname5)" + "\n"
								+ "Layer 1: Node 0 (sname4), Node 1 (sname7)" + "\n"
								+ "Layer 2: Node 0 (sname6)";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Tests correct assignment of predecessors and successors to search nodes in a search graph.
	 */
	@Test
	public void verifyPredecessorsSuccessors()
	{
		SearchGraph searchGraph = getMultiLayerGraph();
		String actualOutput = "";
		
		//Creating a String with all the nodes and their predecessor-successor details
		List<List<SearchNode>> serviceLayers = searchGraph.getServiceLayers();
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
		System.out.println(actualOutput);
		String expectedOutput = "Layer 0: {} Node 0 (sname1) {}, {} Node 1 (sname2) {sname4, sname6}, {} Node 2 (sname3) {sname7}, {} Node 3 (sname5) {}" + "\n"
								+ "Layer 1: {sname2} Node 0 (sname4) {sname6}, {sname3} Node 1 (sname7) {}" + "\n"
								+ "Layer 2: {sname2, sname4} Node 0 (sname6) {}";
		assertEquals(actualOutput, expectedOutput);
	}
	
	/**
	 * Fetches the service names for each layer in the search graph accepted as input.
	 * @param	searchGraph	The search graph whose details need to be fetched
	 * @return	A String containing all layer numbers, node numbers and node service names from the search graph.
	 */
	private String getSearchGraphDetails(SearchGraph searchGraph)
	{
		String searchGraphDetails = "";
		
		List<List<SearchNode>> serviceLayers = searchGraph.getServiceLayers();
		for (List<SearchNode> serviceLayer : serviceLayers)
		{
			searchGraphDetails = searchGraphDetails + "\nLayer " + serviceLayers.indexOf(serviceLayer) + ": ";
			for (SearchNode searchNode : serviceLayer)
			{
				searchGraphDetails = searchGraphDetails + "Node " + serviceLayer.indexOf(searchNode)
									+ " (" + searchNode.getService().getName() + "), ";
			}
			searchGraphDetails = searchGraphDetails.substring(0, searchGraphDetails.lastIndexOf(","));
		}
		
		searchGraphDetails = searchGraphDetails.trim();
		return searchGraphDetails;
	}
	
	/**
	 * Creates a new search graph with one layer of services (search nodes).
	 * @return	Search graph with a single layer of services
	 */
	private SearchGraph getSingleLayerGraph()
	{
		SearchGraph searchGraph = new SearchGraph();
		
		ArrayList<String> inputs1 = new ArrayList<String>();
		inputs1.addAll(Arrays.asList("input11", "input12"));
		ArrayList<String> outputs1 = new ArrayList<String>();
		outputs1.addAll(Arrays.asList("output11", "output12"));
		ArrayList<Constraint> constraints1 = new ArrayList<Constraint>();
		constraints1.add(new Constraint("sname1", "lit11", "type11", Operator.EQUALS));
		constraints1.add(new Constraint("sname1", "lit12", "type12", Operator.LESS_THAN));
		ArrayList<String> effects1 = new ArrayList<String>();
		effects1.addAll(Arrays.asList("eff11", "eff12"));
		Service service1 = new Service("sname1", inputs1, outputs1, constraints1, effects1);
		
		ArrayList<String> inputs2 = new ArrayList<String>();
		inputs2.addAll(Arrays.asList("input21", "input22"));
		ArrayList<String> outputs2 = new ArrayList<String>();
		outputs2.addAll(Arrays.asList("output21", "output22"));
		ArrayList<Constraint> constraints2 = new ArrayList<Constraint>();
		constraints2.add(new Constraint("sname2", "lit21", "type21", Operator.EQUALS));
		constraints2.add(new Constraint("sname2", "lit22", "type22", Operator.LESS_THAN));
		ArrayList<String> effects2 = new ArrayList<String>();
		effects2.addAll(Arrays.asList("eff21", "eff22"));
		Service service2 = new Service("sname2", inputs2, outputs2, constraints2, effects2);
		
		ArrayList<String> inputs3 = new ArrayList<String>();
		inputs3.addAll(Arrays.asList("input31", "input32"));
		ArrayList<String> outputs3 = new ArrayList<String>();
		outputs3.addAll(Arrays.asList("output31", "output32"));
		ArrayList<Constraint> constraints3 = new ArrayList<Constraint>();
		constraints3.add(new Constraint("sname3", "lit31", "type31", Operator.EQUALS));
		constraints3.add(new Constraint("sname3", "lit32", "type32", Operator.LESS_THAN));
		ArrayList<String> effects3 = new ArrayList<String>();
		effects3.addAll(Arrays.asList("eff31", "eff32"));
		Service service3 = new Service("sname3", inputs3, outputs3, constraints3, effects3);
		
		searchGraph.addService(service1);
		searchGraph.addService(service2);
		searchGraph.addService(service3);
		
		return searchGraph;
	}
	
	/**
	 * Creates a new search graph with multiple layers of services (search nodes).
	 * Adds multiple layers to a single-layer graph.
	 * @return	Search graph with multiple layers of services
	 */
	private SearchGraph getMultiLayerGraph()
	{
		SearchGraph searchGraph = getSingleLayerGraph();
		
		//Service has predecessor in layer 0. It should be added to layer 1.
		ArrayList<String> inputs4 = new ArrayList<String>();
		inputs4.addAll(Arrays.asList("output22", "input42"));
		ArrayList<String> outputs4 = new ArrayList<String>();
		outputs4.addAll(Arrays.asList("output41", "output42"));
		ArrayList<Constraint> constraints4 = new ArrayList<Constraint>();
		constraints4.add(new Constraint("sname4", "lit41", "type41", Operator.EQUALS));
		constraints4.add(new Constraint("sname4", "lit42", "type42", Operator.LESS_THAN));
		ArrayList<String> effects4 = new ArrayList<String>();
		effects4.addAll(Arrays.asList("eff41", "eff42"));
		Service service4 = new Service("sname4", inputs4, outputs4, constraints4, effects4);
		
		//Service has no predecessors. It should be added to layer 0.
		ArrayList<String> inputs5 = new ArrayList<String>();
		inputs5.addAll(Arrays.asList("input51", "input52"));
		ArrayList<String> outputs5 = new ArrayList<String>();
		outputs5.addAll(Arrays.asList("output51", "output52"));
		ArrayList<Constraint> constraints5 = new ArrayList<Constraint>();
		constraints5.add(new Constraint("sname5", "lit51", "type51", Operator.EQUALS));
		constraints5.add(new Constraint("sname5", "lit52", "type52", Operator.LESS_THAN));
		ArrayList<String> effects5 = new ArrayList<String>();
		effects5.addAll(Arrays.asList("eff51", "eff52"));
		Service service5 = new Service("sname5", inputs5, outputs5, constraints5, effects5);
		
		//Service has predecessors in layers 0 and 1. It should be added to layer 2.
		ArrayList<String> inputs6 = new ArrayList<String>();
		inputs6.addAll(Arrays.asList("output41", "input61", "output22", "input11"));
		ArrayList<String> outputs6 = new ArrayList<String>();
		outputs6.addAll(Arrays.asList("output51", "output42", "output61"));
		ArrayList<Constraint> constraints6 = new ArrayList<Constraint>();
		constraints6.add(new Constraint("sname6", "lit61", "type61", Operator.EQUALS));
		constraints6.add(new Constraint("sname6", "lit62", "type62", Operator.LESS_THAN));
		ArrayList<String> effects6 = new ArrayList<String>();
		effects6.addAll(Arrays.asList("eff61", "eff62"));
		Service service6 = new Service("sname6", inputs6, outputs6, constraints6, effects6);
		
		//Service has predecessor in layer 0. It should be added to layer 1.
		ArrayList<String> inputs7 = new ArrayList<String>();
		inputs7.addAll(Arrays.asList("output31"));
		ArrayList<String> outputs7 = new ArrayList<String>();
		outputs7.addAll(Arrays.asList("output71", "output11"));
		ArrayList<Constraint> constraints7 = new ArrayList<Constraint>();
		constraints7.add(new Constraint("sname7", "lit71", "type71", Operator.EQUALS));
		constraints7.add(new Constraint("sname7", "lit72", "type72", Operator.LESS_THAN));
		ArrayList<String> effects7 = new ArrayList<String>();
		effects7.addAll(Arrays.asList("eff71", "eff72"));
		Service service7 = new Service("sname7", inputs7, outputs7, constraints7, effects7);
		
		searchGraph.addService(service4);
		searchGraph.addService(service5);
		searchGraph.addService(service6);
		searchGraph.addService(service7);
		
		return searchGraph;
	}
}