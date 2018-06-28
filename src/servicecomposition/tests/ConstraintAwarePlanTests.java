package servicecomposition.tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import servicecomposition.compositionprocesses.BackwardSearch;
import servicecomposition.compositionprocesses.ConstraintAwarePlanConstruction;
import servicecomposition.compositionprocesses.ForwardExpansion;
import servicecomposition.compositionprocesses.PlanConstruction;
import servicecomposition.entities.CompositionPlan;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.entities.SearchGraph;
import servicecomposition.entities.SearchNode;
import servicecomposition.entities.ServiceNode;
import service.BasicService;
import service.ConstrainedService;
import service.Service;
import service.parser.BasicServiceParser;
import service.parser.ConstrainedServiceXMLParser;
import service.parser.ServiceFileParserDecorator;

/**
 * Class for testing construction of and constraint adjustments in constraint-aware service composition plans.
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("int : input11", "float : input12", "string : input21", "boolean : input22"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("string : output51", "boolean : output52"));
		String repoXMLFileName = "testinput/Test_Services_Set_2.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 7;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [boolean : input22 EQUALS false, int : input11 EQUALS 31, int : input11 GREATER_THAN_OR_EQUAL_TO 5, int : input11 LESS_THAN_OR_EQUAL_TO 11, string : input21 EQUALS lit41] sname1 {sname3}"
								+ "\nLayer 1: {sname1} [float : input12 LESS_THAN 32.2] sname3 {sname5}, {} [] sname4 {sname6}" 
								+ "\nLayer 2: {sname3} [float : output31 GREATER_THAN 51.1, float : output31 LESS_THAN 52.2] sname5 {}, {sname4} [char : output41 EQUALS x] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [boolean : input22 EQUALS false, string : input21 EQUALS lit41] sname4 {sname6}" 
								+ "\nLayer 1: {sname4} [char : output41 EQUALS x] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [float : input12 LESS_THAN 32.2, int : input11 EQUALS 31, string : input21 EQUALS lit21, string : input21 EQUALS lit41] sname2 {sname4}" 
								+ "\nLayer 1: {sname2} [boolean : input22 EQUALS false] sname4 {sname6}, {} [] sname3 {sname5}" 
								+ "\nLayer 2: {sname3} [float : output31 GREATER_THAN 51.1, float : output31 LESS_THAN 52.2] sname5 {}, {sname4} [char : output41 EQUALS x] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [float : input12 LESS_THAN 32.2, int : input11 EQUALS 31] sname3 {sname5}" 
								+ "\nLayer 1: {sname3} [float : output31 GREATER_THAN 51.1, float : output31 LESS_THAN 52.2] sname5 {}");
		expectedPlanDetails.add("Layer 0: {} [int : input11 EQUALS 31, int : input11 GREATER_THAN_OR_EQUAL_TO 5, int : input11 LESS_THAN_OR_EQUAL_TO 11, string : input21 EQUALS lit41] sname1 {sname3}, {} [int : input11 EQUALS 31, string : input21 EQUALS lit21, string : input21 EQUALS lit41] sname2 {sname4}" 
								+ "\nLayer 1: {sname1} [float : input12 LESS_THAN 32.2] sname3 {sname5}, {sname2} [boolean : input22 EQUALS false] sname4 {sname6}" 
								+ "\nLayer 2: {sname3} [float : output31 GREATER_THAN 51.1, float : output31 LESS_THAN 52.2] sname5 {}, {sname4} [char : output41 EQUALS x] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [int : input11 EQUALS 31, int : input11 GREATER_THAN_OR_EQUAL_TO 5, int : input11 LESS_THAN_OR_EQUAL_TO 11] sname1 {sname3}" 
								+ "\nLayer 1: {sname1} [float : input12 LESS_THAN 32.2] sname3 {sname5}"
								+ "\nLayer 2: {sname3} [float : output31 GREATER_THAN 51.1, float : output31 LESS_THAN 52.2] sname5 {}");
		expectedPlanDetails.add("Layer 0: {} [string : input21 EQUALS lit21, string : input21 EQUALS lit41] sname2 {sname4}" 
								+ "\nLayer 1: {sname2} [boolean : input22 EQUALS false] sname4 {sname6}"
								+ "\nLayer 2: {sname4} [char : output41 EQUALS x] sname6 {}");
		
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
		ServiceNode serviceNode1 = new ServiceNode(new ConstrainedService(new BasicService("sname1", null, null), null, null), 2);
		ServiceNode serviceNode2 = new ServiceNode(new ConstrainedService(new BasicService("sname2", null, null), null, null), 4);
		ServiceNode serviceNode3 = new ServiceNode(new ConstrainedService(new BasicService("sname3", null, null), null, null), 4);
		ServiceNode serviceNode4 = new ServiceNode(new ConstrainedService(new BasicService("sname4", null, null), null, null), 7);
		ServiceNode serviceNode5 = new ServiceNode(new ConstrainedService(new BasicService("sname5", null, null), null, null), 7);
		ServiceNode serviceNode6 = new ServiceNode(new ConstrainedService(new BasicService("sname6", null, null), null, null), 7);
		ServiceNode serviceNode7 = new ServiceNode(new ConstrainedService(new BasicService("sname7", null, null), null, null), 8);
		ServiceNode serviceNode8 = new ServiceNode(new ConstrainedService(new BasicService("sname8", null, null), null, null), 10);
		ServiceNode serviceNode9 = new ServiceNode(new ConstrainedService(new BasicService("sname9", null, null), null, null), 10);
		
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
		String expectedPlanDetails = "Layer 0: {} [] sname1 {sname2, sname3}"
									+ "\nLayer 1: {sname1} [] sname2 {sname4, sname5}, {sname1} [] sname3 {sname6}"
									+ "\nLayer 2: {sname2} [] sname4 {sname7}, {sname2} [] sname5 {sname8}, {sname3} [] sname6 {sname7, sname9}"
									+ "\nLayer 3: {sname4, sname6} [] sname7 {sname8}"
									+ "\nLayer 4: {sname5, sname7} [] sname8 {}, {sname6} [] sname9 {}";
		
		assertEquals(planLayerCount, expectedPlanLayerCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests correct generation of constraint-aware composition plans even when no services have any constraints.
	 */
	@Test
	public void noConstraints()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("string : StudentID"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("float : MarksPercentage"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [] W8 {W9}"
								+ "\nLayer 1: {W8} [] W9 {W10}"
								+ "\nLayer 2: {W9} [] W10 {}");
		
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests correct constraint allocation to service nodes when there is no constraint adjustment required.
	 */
	@Test
	public void noConstraintAdjReqd()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("int : input111"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("string : output161"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [int : input111 LESS_THAN_OR_EQUAL_TO 111] W11 {W12}"
								+ "\nLayer 1: {W11} [float : output111 GREATER_THAN 121.0, float : output111 LESS_THAN 122.0] W12 {W16}"
								+ "\nLayer 2: {W12} [string : output121 EQUALS lit161, string : output121 EQUALS lit162] W16 {}");
		
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. An adjusted constraint is added to all the successors of the service node that last 
	 * affects the constraint feature irrespective of whether the successor uses the feature or not.
	 * 2. Constraint adjustment is performed for every constraint of every service node in the plan.
	 */
	@Test
	public void cnstrAddedToAllSuccs()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("int : input111"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("char : output151"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [int : input111 LESS_THAN_OR_EQUAL_TO 111] W11 {W12, W13}"
								+ "\nLayer 1: {W11} [float : output111 GREATER_THAN 121.0, float : output111 LESS_THAN 122.0] W12 {W14}, {W11} [float : output111 GREATER_THAN 121.0, float : output111 LESS_THAN 122.0] W13 {W14, W15}"
								+ "\nLayer 2: {W12, W13} [boolean : output131 EQUALS true, int : output132 LESS_THAN 152, string : output121 EQUALS lit141] W14 {W15}"
								+ "\nLayer 3: {W13, W14} [boolean : output131 EQUALS true, float : output141 EQUALS 15.1, int : output132 LESS_THAN 152] W15 {}");
		
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests that in case a service node whose constraint needs to be adjusted has multiple predecessors,
	 * the constraint is added to the successors of only that predecessor that affects the constraint feature.
	 * Successors of other predecessors remain unchanged during this adjustment.
	 */
	@Test
	public void cnstrAdjustedToAffectingPreds()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("int : input111", "boolean : input171"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("string : output231", "int : output251", "float : output261"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [boolean : input171 EQUALS false, int : input111 EQUALS 171] W17 {W20}" 
								+ "\nLayer 1: {W17} [int : output171 EQUALS 231] W20 {W21, W22}" 
								+ "\nLayer 2: {W20} [] W21 {W23, W26}, {W20} [] W22 {W23, W25}" 
								+ "\nLayer 3: {W21, W22} [char : output221 EQUALS l] W23 {}, {W21} [] W26 {}, {W22} [char : output221 EQUALS l] W25 {}");
		
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests that in case a constraint feature is affected by predecessors in multiple layers, the constraint 
	 * will be added to the successors of only the predecessor in the highest layer of all predecessors.
	 */
	@Test
	public void cnstrAdjustedToClosestPreds()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("char : input271"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("string : output311"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [] W27 {W28}" 
								+ "\nLayer 1: {W27} [] W28 {W29}" 
								+ "\nLayer 2: {W28} [] W29 {W30}" 
								+ "\nLayer 3: {W29} [boolean : output272 EQUALS true] W30 {W31}" 
								+ "\nLayer 4: {W30} [] W31 {}");
		
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. A constraint once adjusted is removed from its original service node (except in case 2 stated below).
	 * 2. If the service node whose constraint needs to be adjusted is one of the successors to receive the
	 * constraint during adjustment, the node is left with exactly 1 copy of the constraint after the adjustment.
	 * 3. A constraint on a feature not affected by any services gets added to the beginning of the plan.
	 * 4. Predecessors irrespective of the number of layers between them and a successor node cause the 
	 * successor node to receive an adjusted constraint.
	 */
	@Test
	public void miscCnstrAdjChecks1()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("string : DeliveryAddress", "string : ProductName"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("string : ShipmentConfirm"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 15;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Montreal, string : DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W2 {W3, W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W5 {W3, W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W7 {}, {W1, W2, W5} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W3 {}, {W1, W2, W5} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Montreal, string : DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W2 {W3, W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W7 {}, {W1, W2} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W3 {}, {W1, W2} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Montreal] W1 {W2, W3, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W2 {W3, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W5 {W3, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W7 {}, {W1, W2, W5} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Montreal] W1 {W2, W3, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W2 {W3, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W7 {}, {W1, W2} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Quebec] W1 {W2, W4, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W2 {W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W5 {W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W7 {}, {W1, W2, W5} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada, string : DeliveryAddress EQUALS Quebec] W1 {W2, W4, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W2 {W4, W7}, {W1} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W7 {}, {W1, W2} [string : ProductAddress EQUALS Canada, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada] W1 {W2, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada] W2 {W7}, {W1} [string : ProductAddress EQUALS Canada] W5 {W7}, {W1} [string : ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [string : ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada] W1 {W2, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada] W2 {W7}, {W1} [string : ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [string : ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Canada] W1 {W5, W6, W7}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Canada] W5 {W7}, {W1} [string : ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W5, W6} [string : ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Montreal, string : DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W5}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W2 {W3, W4}, {W1} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W5 {W3, W4}" 
								+ "\nLayer 2: {W1, W2, W5} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W3 {}, {W1, W2, W5} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Montreal, string : DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W2 {W3, W4}" 
								+ "\nLayer 2: {W1, W2} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W3 {}, {W1, W2} [string : ProductAddress EQUALS Montreal, string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Montreal] W1 {W2, W3, W5}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Montreal] W2 {W3}, {W1} [string : ProductAddress EQUALS Montreal] W5 {W3}" 
								+ "\nLayer 2: {W1, W2, W5} [string : ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Montreal] W1 {W2, W3}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Montreal] W2 {W3}" 
								+ "\nLayer 2: {W1, W2} [string : ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Quebec] W1 {W2, W4, W5}" 
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Quebec] W2 {W4}, {W1} [string : ProductAddress EQUALS Quebec] W5 {W4}" 
								+ "\nLayer 2: {W1, W2, W5} [string : ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [string : DeliveryAddress EQUALS Quebec] W1 {W2, W4}"
								+ "\nLayer 1: {W1} [string : ProductAddress EQUALS Quebec] W2 {W4}" 
								+ "\nLayer 2: {W1, W2} [string : ProductAddress EQUALS Quebec] W4 {}");
						
		assertEquals(actualPlanCount, expectedPlanCount);
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Constraints on layer 0 services remain intact after adjustment.
	 * 2. Constraints moved to the beginning of the plan during adjustment are added to all the service nodes in layer 0.
	 * 3. Constraint adjustment of features affected by predecessors of predecessors of the original service node.
	 */
	@Test
	public void miscCnstrAdjChecks2()
	{
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("int : input111", "boolean : input171"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("boolean : output241", "string : output231"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [boolean : input171 EQUALS false, float : input181 LESS_THAN 1.92, int : input111 EQUALS 171] W17 {W20}, {} [float : input181 LESS_THAN 1.92, int : input111 LESS_THAN_OR_EQUAL_TO 111] W11 {W12}" 
								+ "\nLayer 1: {W11} [float : output111 GREATER_THAN 121.0, float : output111 LESS_THAN 122.0] W12 {W19}, {W17} [int : output171 EQUALS 231] W20 {W21, W22}" 
								+ "\nLayer 2: {W12} [string : output121 EQUALS lit191] W19 {W24}, {W20} [] W21 {W23}, {W20} [] W22 {W23}" 
								+ "\nLayer 3: {W19} [] W24 {}, {W21, W22} [char : output221 EQUALS l] W23 {}");
		
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
		ServiceFileParserDecorator serviceParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		serviceParser.setLocation(repoXMLFileName);
		ArrayList<Service> serviceRepo = serviceParser.parse();
		
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
		
		return plans.size();
	}
}