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
		expectedPlanDetails.add("Layer 0: "
								+ "\nLayer 1: {} sname3 {sname5}"
								+ "\nLayer 2: {sname3} sname5 {}");
		expectedPlanDetails.add("Layer 0: "
								+ "\nLayer 1: {} sname4 {sname6}"
								+ "\nLayer 2: {sname4} sname6 {}");
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
		
		assertEquals(actualPlanCount, expectedPlanCount);
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