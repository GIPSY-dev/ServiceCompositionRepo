package tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import compositionprocesses.BackwardSearch;
import compositionprocesses.ForwardExpansion;
import entities.CompositionRequest;
import entities.SearchGraph;
import entities.SearchNode;
import service.Service;
import service.ServiceParser;
import service.ServiceXMLParser;

/**
 * Class for testing the backward search process in various scenarios.
 * We populate only the inputs and outputs of test composition requests because 
 * QoS and constraints play no role in (and have no effect on) the backward search process.
 * @author Jyotsana Gupta
 */
public class BackwardSearchTests 
{
	/**
	 * Tests the following requirements:
	 * 1. Plan sets can be created from search graphs generated by the forward expansion stage.
	 * 2. Valid plan sets produce all the requested outputs (not just partial outputs).
	 */
	@Test
	public void planSetGeneration()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input31", "input32", "input42", "output21"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output22", "output32", "output42"));
		List<List<String>> planSetServiceLists = new ArrayList<List<String>>();
		
		int planSetCount = getActualPlanSetResults(compReqInputs, compReqOutputs, planSetServiceLists);

		int expectedPlanSetCount = 1;
		List<List<String>> expectedServiceLists = new ArrayList<List<String>>();
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname4", "sname9")));
		
		assertEquals(expectedPlanSetCount, planSetCount);
		assertEquals(expectedServiceLists, planSetServiceLists);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. All the layers of the input search graph are processed as starting layers for plan set generation.
	 * 2. Starting layer sets that do not produce any requested output are discarded.
	 * 3. Check 2 is done only for starting layers.
	 * 4. Power set generation and processing of all its elements.
	 * 5. Generation of plan sets that are partially same.
	 * 6. Multiple plan set generation.
	 */
	@Test
	public void startingLayers()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input12", "input31", "input32"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output11"));
		List<List<String>> planSetServiceLists = new ArrayList<List<String>>();
		
		int planSetCount = getActualPlanSetResults(compReqInputs, compReqOutputs, planSetServiceLists);

		int expectedPlanSetCount = 3;
		List<List<String>> expectedServiceLists = new ArrayList<List<String>>();
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname7")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname3", "sname7")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname3", "sname7", "sname8")));
		
		assertEquals(expectedPlanSetCount, planSetCount);
		assertEquals(expectedServiceLists, planSetServiceLists);
	}
	
	/**
	 * Tests that plan sets with just 1 service are discarded.
	 */
	@Test
	public void singleNodePlanSet()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input12", "output31"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output11"));
		List<List<String>> planSetServiceLists = new ArrayList<List<String>>();
		
		int planSetCount = getActualPlanSetResults(compReqInputs, compReqOutputs, planSetServiceLists);

		int expectedPlanSetCount = 2;
		List<List<String>> expectedServiceLists = new ArrayList<List<String>>();
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname7")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname7", "sname8")));
		
		assertEquals(expectedPlanSetCount, planSetCount);
		assertEquals(expectedServiceLists, planSetServiceLists);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Valid plan sets with all services in a single row.
	 * 2. Valid plan sets with services from the first row only.
	 * 3. Excessive services in plan sets, proving complete consideration of all power set elements. 
	 * 	  These will be pruned in plan construction stage. 
	 */
	@Test
	public void layerZeroPlanSets()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input11", "input12", "input31", "input42", "input61", "output22"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output11", "input32"));
		List<List<String>> planSetServiceLists = new ArrayList<List<String>>();
		
		int planSetCount = getActualPlanSetResults(compReqInputs, compReqOutputs, planSetServiceLists);

		int expectedPlanSetCount = 3;
		List<List<String>> expectedServiceLists = new ArrayList<List<String>>();
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname4", "sname8")));
		
		assertEquals(expectedPlanSetCount, planSetCount);
		assertEquals(expectedServiceLists, planSetServiceLists);
	}
	
	/**
	 * Tests plan set generation for a more complex search graph.
	 */
	@Test
	public void complexSearchGraph()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input11", "input12", "input21", "input22", "input31", "input42", "input61", "output22"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output11", "input32"));
		List<List<String>> planSetServiceLists = new ArrayList<List<String>>();
		
		int planSetCount = getActualPlanSetResults(compReqInputs, compReqOutputs, planSetServiceLists);

		int expectedPlanSetCount = 12;
		List<List<String>> expectedServiceLists = new ArrayList<List<String>>();
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname1", "sname2", "sname8")));		
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname3", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname6", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname3", "sname6", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname3", "sname4", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname3", "sname4", "sname6", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname3", "sname4", "sname6", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname2", "sname3", "sname4", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname2", "sname3", "sname4", "sname6", "sname7", "sname8")));
		expectedServiceLists.add(new ArrayList<String>(Arrays.asList("sname10", "sname2", "sname3", "sname4", "sname6", "sname7", "sname8")));
		
		assertEquals(expectedPlanSetCount, planSetCount);
		assertEquals(expectedServiceLists, planSetServiceLists);
	}
	
	/**
	 * Method for constructing plan sets from the search graph generated for a given composition request.
	 * @param 	compReqInputs			Composition request inputs
	 * @param 	compReqOutputs			Composition request outputs
	 * @param 	planSetServiceLists		Lists of service names that constitute valid plan sets
	 * @return	Number of valid plan sets generated for the given composition request
	 */
	private int getActualPlanSetResults(List<String> compReqInputs, List<String> compReqOutputs, List<List<String>> planSetServiceLists)
	{
		//Creating a composition request based on the given inputs and outputs
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(compReqInputs);
		compositionReq.setOutputs(compReqOutputs);
		
		//Reading the service repository
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse("testinput/Test_Services_Set_1.xml");
		
		//Using forward expansion to generate a search graph
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		//Using backward search for constructing plan sets
		List<Set<SearchNode>> planSets = BackwardSearch.backwardSearch(compositionReq, resultingGraph);
		
		//Fetching the number of plan sets generated
		int planSetCount = planSets.size();
		
		//Creating a list of lists of service names that constitute plan sets
		//Service names in each list are sorted alphabetically and the lists are sorted by their size
		for (Set<SearchNode> planSet : planSets)
		{
			List<String> planSetServiceList = new ArrayList<String>();
			for (SearchNode searchNode : planSet)
			{
				planSetServiceList.add(searchNode.getService().getName());
			}
			planSetServiceList.sort(String::compareToIgnoreCase);
			planSetServiceLists.add(planSetServiceList);
		}		
		Collections.sort(planSetServiceLists, new Comparator<List>() 
		{
			public int compare(List list1, List list2) 
			{
				return list1.size() - list2.size();
			}
		});
		
		return planSetCount;
	}
}