package tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import compositionprocesses.BackwardSearch;
import compositionprocesses.ConstraintAwarePlanConstruction;
import compositionprocesses.ForwardExpansion;
import compositionprocesses.PlanConstruction;
import entities.CompositionPlan;
import entities.CompositionRequest;
import entities.ConstraintAwarePlan;
import entities.SearchGraph;
import entities.SearchNode;
import entities.ServiceNode;
import service.Service;
import service.ServiceParser;
import service.ServiceXMLParser;

/**
 * Class for testing construction of constraint-aware service composition plans.
 * It also contains tests for constraint adjustments. 
 * @author Jyotsana Gupta
 */
public class ConstraintAwarePlanTests 
{
	/**
	 * Tests that service nodes are assigned only those predecessors and successors that are present in their plan.
	 * Other irrelevant predecessors and successors should be removed from their lists for that plan.
	 * Also tests that empty layer removal method is correctly executed during constraint-aware plan construction. 
	 */
	@Test
	public void excessPredSuccRemoval()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input11", "input12", "input21", "input22"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output51", "output52"));
		String repoXMLFileName = "testinput/Test_Services_Set_2.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 7;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} sname1 {sname3}"
								+ "\nLayer 1: {sname1} sname3 {sname5}"
								+ "\nLayer 2: {sname3} sname5 {}");
		expectedPlanDetails.add("Layer 0: {} sname1 {sname3}"
								+ "\nLayer 1: {sname1} sname3 {sname5}, {} sname4 {sname6}"
								+ "\nLayer 2: {sname3} sname5 {}, {sname4} sname6 {}");
		expectedPlanDetails.add("Layer 0: {} sname1 {sname3}, {} sname2 {sname4}"
								+ "\nLayer 1: {sname1} sname3 {sname5}, {sname2} sname4 {sname6}"
								+ "\nLayer 2: {sname3} sname5 {}, {sname4} sname6 {}");
		expectedPlanDetails.add("Layer 0: {} sname2 {sname4}"
								+ "\nLayer 1: {sname2} sname4 {sname6}"
								+ "\nLayer 2: {sname4} sname6 {}");
		expectedPlanDetails.add("Layer 0: {} sname2 {sname4}" 
								+ "\nLayer 1: {sname2} sname4 {sname6}, {} sname3 {sname5}" 
								+ "\nLayer 2: {sname3} sname5 {}, {sname4} sname6 {}");
		expectedPlanDetails.add("Layer 0: {} sname3 {sname5}"
								+ "\nLayer 1: {sname3} sname5 {}");
		expectedPlanDetails.add("Layer 0: {} sname4 {sname6}"
								+ "\nLayer 1: {sname4} sname6 {}");
				
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests correct removal of empty service layers from a constraint-aware plan
	 * irrespective of the number and position of the empty layers in the plan. 
	 */
	@Test
	public void emptyLayerRemoval()
	{
		//Creating service nodes
		ServiceNode serviceNode1 = new ServiceNode(new Service("sname1", null, null, null, null), 2);
		ServiceNode serviceNode2 = new ServiceNode(new Service("sname2", null, null, null, null), 4);
		ServiceNode serviceNode3 = new ServiceNode(new Service("sname3", null, null, null, null), 4);
		ServiceNode serviceNode4 = new ServiceNode(new Service("sname4", null, null, null, null), 7);
		ServiceNode serviceNode5 = new ServiceNode(new Service("sname5", null, null, null, null), 7);
		ServiceNode serviceNode6 = new ServiceNode(new Service("sname6", null, null, null, null), 7);
		ServiceNode serviceNode7 = new ServiceNode(new Service("sname7", null, null, null, null), 8);
		ServiceNode serviceNode8 = new ServiceNode(new Service("sname8", null, null, null, null), 10);
		ServiceNode serviceNode9 = new ServiceNode(new Service("sname9", null, null, null, null), 10);
		
		//Assigning predecessors and successors to the service nodes
		serviceNode1.addSuccessor(serviceNode2);
		serviceNode1.addSuccessor(serviceNode3);
		serviceNode2.addPredecessor(serviceNode1);
		serviceNode2.addSuccessor(serviceNode4);
		serviceNode2.addSuccessor(serviceNode5);
		serviceNode3.addPredecessor(serviceNode1);
		serviceNode3.addSuccessor(serviceNode6);
		serviceNode4.addPredecessor(serviceNode2);
		serviceNode4.addSuccessor(serviceNode7);
		serviceNode5.addPredecessor(serviceNode2);
		serviceNode5.addSuccessor(serviceNode8);
		serviceNode6.addPredecessor(serviceNode3);
		serviceNode6.addSuccessor(serviceNode7);
		serviceNode6.addSuccessor(serviceNode9);
		serviceNode7.addPredecessor(serviceNode4);
		serviceNode7.addPredecessor(serviceNode6);
		serviceNode7.addSuccessor(serviceNode8);
		serviceNode8.addPredecessor(serviceNode5);
		serviceNode8.addPredecessor(serviceNode7);
		serviceNode9.addPredecessor(serviceNode6);
				
		//Creating a constraint-aware composition plan with the service nodes
		ConstraintAwarePlan cnstrAwrPlan = new ConstraintAwarePlan(13);
		cnstrAwrPlan.addServiceNode(serviceNode1);
		cnstrAwrPlan.addServiceNode(serviceNode2);
		cnstrAwrPlan.addServiceNode(serviceNode3);
		cnstrAwrPlan.addServiceNode(serviceNode4);
		cnstrAwrPlan.addServiceNode(serviceNode5);
		cnstrAwrPlan.addServiceNode(serviceNode6);
		cnstrAwrPlan.addServiceNode(serviceNode7);
		cnstrAwrPlan.addServiceNode(serviceNode8);
		cnstrAwrPlan.addServiceNode(serviceNode9);
		
		//Removing the empty layers from the plan
		cnstrAwrPlan.removeEmptyLayers();
		
		int planLayerCount = cnstrAwrPlan.getServiceLayerCount();
		int expectedPlanLayerCount = 5;
		
		String actualPlanDetails = cnstrAwrPlan.toString();
		String expectedPlanDetails = "Layer 0: {} sname1 {sname2, sname3}"
									+ "\nLayer 1: {sname1} sname2 {sname4, sname5}, {sname1} sname3 {sname6}"
									+ "\nLayer 2: {sname2} sname4 {sname7}, {sname2} sname5 {sname8}, {sname3} sname6 {sname7, sname9}"
									+ "\nLayer 3: {sname4, sname6} sname7 {sname8}"
									+ "\nLayer 4: {sname5, sname7} sname8 {}, {sname6} sname9 {}";
		
		assertEquals(planLayerCount, expectedPlanLayerCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Method for constructing constraint-aware plans from composition plans generated for a given composition request.
	 * @param 	compReqInputs		Composition request inputs
	 * @param 	compReqOutputs		Composition request outputs
	 * @param 	repoXMLFileName		Complete name of the XML file containing the service repository
	 * @param 	actualPlanDetails	List of strings containing layer-wise service names from the generated plans
	 * @return	Number of constraint-aware plans generated for the given composition request
	 */
	private int getActualPlanResults(List<String> compReqInputs, List<String> compReqOutputs, String repoXMLFileName, List<String> actualPlanDetails)
	{
		//Creating a composition request based on the given inputs and outputs
		CompositionRequest compositionReq = new CompositionRequest();
		compositionReq.setInputs(compReqInputs);
		compositionReq.setOutputs(compReqOutputs);
		
		//Reading the service repository
		ServiceParser serviceParser = new ServiceXMLParser();
		ArrayList<Service> serviceRepo = serviceParser.parse(repoXMLFileName);
		
		//Using forward expansion to generate a search graph
		SearchGraph resultingGraph = ForwardExpansion.forwardExpansion(compositionReq, serviceRepo);
		
		//Using backward search for constructing plan sets
		List<Set<SearchNode>> planSets = BackwardSearch.backwardSearch(compositionReq, resultingGraph);
		
		//Constructing pruned and validated composition plans from the plan sets
		List<CompositionPlan> plans = PlanConstruction.constructPlans(compositionReq, planSets);
		
		//Constructing constraint aware plans from composition plans
		List<ConstraintAwarePlan> cnstrAwrPlans = ConstraintAwarePlanConstruction.constructCAPlans(plans);
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			actualPlanDetails.add(cnstrAwrPlan.toString());
		}
		Collections.sort(actualPlanDetails);
		
		//TODO remove later
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			System.out.println("Plan:\n" + cnstrAwrPlan);
		}
		
		return plans.size();
	}
}