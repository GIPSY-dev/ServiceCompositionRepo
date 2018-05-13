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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input11", "input12", "input21", "input22"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output51", "output52"));
		String repoXMLFileName = "testinput/Test_Services_Set_2.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 7;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [type11 EQUALS lit11, type12 LESS_THAN lit12, type31 EQUALS lit31, type32 LESS_THAN lit32, type41 EQUALS lit41, type42 LESS_THAN lit42, type51 EQUALS lit51, type52 LESS_THAN lit52, type61 EQUALS lit61, type62 LESS_THAN lit62] sname1 {sname3}"
								+ "\nLayer 1: {sname1} [] sname3 {sname5}, {} [] sname4 {sname6}" 
								+ "\nLayer 2: {sname3} [] sname5 {}, {sname4} [] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [type11 EQUALS lit11, type12 LESS_THAN lit12, type31 EQUALS lit31, type32 LESS_THAN lit32, type41 EQUALS lit41, type42 LESS_THAN lit42, type51 EQUALS lit51, type52 LESS_THAN lit52, type61 EQUALS lit61, type62 LESS_THAN lit62] sname1 {sname3}, {} [type21 EQUALS lit21, type22 LESS_THAN lit22, type31 EQUALS lit31, type32 LESS_THAN lit32, type41 EQUALS lit41, type42 LESS_THAN lit42, type51 EQUALS lit51, type52 LESS_THAN lit52, type61 EQUALS lit61, type62 LESS_THAN lit62] sname2 {sname4}" 
								+ "\nLayer 1: {sname1} [] sname3 {sname5}, {sname2} [] sname4 {sname6}" 
								+ "\nLayer 2: {sname3} [] sname5 {}, {sname4} [] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [type11 EQUALS lit11, type12 LESS_THAN lit12, type31 EQUALS lit31, type32 LESS_THAN lit32, type51 EQUALS lit51, type52 LESS_THAN lit52] sname1 {sname3}" 
								+ "\nLayer 1: {sname1} [] sname3 {sname5}" 
								+ "\nLayer 2: {sname3} [] sname5 {}");
		expectedPlanDetails.add("Layer 0: {} [type21 EQUALS lit21, type22 LESS_THAN lit22, type31 EQUALS lit31, type32 LESS_THAN lit32, type41 EQUALS lit41, type42 LESS_THAN lit42, type51 EQUALS lit51, type52 LESS_THAN lit52, type61 EQUALS lit61, type62 LESS_THAN lit62] sname2 {sname4}" 
								+ "\nLayer 1: {sname2} [] sname4 {sname6}, {} [] sname3 {sname5}" 
								+ "\nLayer 2: {sname3} [] sname5 {}, {sname4} [] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [type21 EQUALS lit21, type22 LESS_THAN lit22, type41 EQUALS lit41, type42 LESS_THAN lit42, type61 EQUALS lit61, type62 LESS_THAN lit62] sname2 {sname4}" 
								+ "\nLayer 1: {sname2} [] sname4 {sname6}" 
								+ "\nLayer 2: {sname4} [] sname6 {}");
		expectedPlanDetails.add("Layer 0: {} [type31 EQUALS lit31, type32 LESS_THAN lit32, type51 EQUALS lit51, type52 LESS_THAN lit52] sname3 {sname5}" 
								+ "\nLayer 1: {sname3} [] sname5 {}");
		expectedPlanDetails.add("Layer 0: {} [type41 EQUALS lit41, type42 LESS_THAN lit42, type61 EQUALS lit61, type62 LESS_THAN lit62] sname4 {sname6}" 
								+ "\nLayer 1: {sname4} [] sname6 {}");
		
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("StudentID"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("MarksPercentage"));
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input111"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output161"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [input111 EQUALS lit111] W11 {W12}"
								+ "\nLayer 1: {W11} [output111 EQUALS lit121, output111 LESS_THAN lit122] W12 {W16}"
								+ "\nLayer 2: {W12} [output121 EQUALS lit161, output121 LESS_THAN lit162] W16 {}");
		
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input111"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output151"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [input111 EQUALS lit111] W11 {W12, W13}"
								+ "\nLayer 1: {W11} [output111 EQUALS lit121, output111 LESS_THAN lit122] W12 {W14}, {W11} [output111 EQUALS lit121, output111 LESS_THAN lit122] W13 {W14, W15}"
								+ "\nLayer 2: {W12, W13} [output121 EQUALS lit141, output131 LESS_THAN lit142, output132 LESS_THAN lit152] W14 {W15}"
								+ "\nLayer 3: {W13, W14} [output131 LESS_THAN lit142, output132 LESS_THAN lit152, output141 EQUALS lit151] W15 {}");
		
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input111", "input171"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output231", "output251", "output261"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [input111 EQUALS lit171, input171 LESS_THAN lit172] W17 {W20}" 
								+ "\nLayer 1: {W17} [output171 EQUALS lit231] W20 {W21, W22}" 
								+ "\nLayer 2: {W20} [] W21 {W23, W26}, {W20} [] W22 {W23, W25}" 
								+ "\nLayer 3: {W21, W22} [output221 EQUALS lit232] W23 {}, {W21} [] W26 {}, {W22} [output221 EQUALS lit232] W25 {}");
		
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input271"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output311"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [] W27 {W28}" 
								+ "\nLayer 1: {W27} [] W28 {W29}" 
								+ "\nLayer 2: {W28} [] W29 {W30}" 
								+ "\nLayer 3: {W29} [output272 EQUALS lit311] W30 {W31}" 
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("DeliveryAddress", "ProductName"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("ShipmentConfirm"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 15;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Montreal, DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W2 {W3, W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W5 {W3, W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W7 {}, {W1, W2, W5} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W3 {}, {W1, W2, W5} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Montreal, DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W2 {W3, W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W7 {}, {W1, W2} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W3 {}, {W1, W2} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Montreal] W1 {W2, W3, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W2 {W3, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W5 {W3, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W7 {}, {W1, W2, W5} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Montreal] W1 {W2, W3, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W2 {W3, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W7 {}, {W1, W2} [ProductAddress EQUALS Canada, ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Quebec] W1 {W2, W4, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W2 {W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W5 {W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W7 {}, {W1, W2, W5} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada, DeliveryAddress EQUALS Quebec] W1 {W2, W4, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W2 {W4, W7}, {W1} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W7 {}, {W1, W2} [ProductAddress EQUALS Canada, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada] W1 {W2, W5, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada] W2 {W7}, {W1} [ProductAddress EQUALS Canada] W5 {W7}, {W1} [ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W5, W6} [ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada] W1 {W2, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada] W2 {W7}, {W1} [ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W2, W6} [ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Canada] W1 {W5, W6, W7}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Canada] W5 {W7}, {W1} [ProductAddress EQUALS Canada] W6 {W7}" 
								+ "\nLayer 2: {W1, W5, W6} [ProductAddress EQUALS Canada] W7 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Montreal, DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4, W5}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W2 {W3, W4}, {W1} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W5 {W3, W4}" 
								+ "\nLayer 2: {W1, W2, W5} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W3 {}, {W1, W2, W5} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Montreal, DeliveryAddress EQUALS Quebec] W1 {W2, W3, W4}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W2 {W3, W4}" 
								+ "\nLayer 2: {W1, W2} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W3 {}, {W1, W2} [ProductAddress EQUALS Montreal, ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Montreal] W1 {W2, W3, W5}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Montreal] W2 {W3}, {W1} [ProductAddress EQUALS Montreal] W5 {W3}" 
								+ "\nLayer 2: {W1, W2, W5} [ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Montreal] W1 {W2, W3}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Montreal] W2 {W3}" 
								+ "\nLayer 2: {W1, W2} [ProductAddress EQUALS Montreal] W3 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Quebec] W1 {W2, W4, W5}" 
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Quebec] W2 {W4}, {W1} [ProductAddress EQUALS Quebec] W5 {W4}" 
								+ "\nLayer 2: {W1, W2, W5} [ProductAddress EQUALS Quebec] W4 {}");
		expectedPlanDetails.add("Layer 0: {} [DeliveryAddress EQUALS Quebec] W1 {W2, W4}"
								+ "\nLayer 1: {W1} [ProductAddress EQUALS Quebec] W2 {W4}" 
								+ "\nLayer 2: {W1, W2} [ProductAddress EQUALS Quebec] W4 {}");
				
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
		List<String> compReqInputs = new ArrayList<String>(Arrays.asList("input111", "input171"));
		List<String> compReqOutputs = new ArrayList<String>(Arrays.asList("output241", "output231"));
		String repoXMLFileName = "testinput/Test_Services_Set_3.xml";
		List<String> actualPlanDetails = new ArrayList<String>();
		
		int actualPlanCount = getActualPlanResults(compReqInputs, compReqOutputs, repoXMLFileName, actualPlanDetails);
		
		int expectedPlanCount = 1;
		List<String> expectedPlanDetails = new ArrayList<String>();
		expectedPlanDetails.add("Layer 0: {} [input111 EQUALS lit111, input181 LESS_THAN lit192] W11 {W12}, {} [input111 EQUALS lit171, input171 LESS_THAN lit172, input181 LESS_THAN lit192] W17 {W20}" 
								+ "\nLayer 1: {W11} [output111 EQUALS lit121, output111 LESS_THAN lit122] W12 {W19}, {W17} [output171 EQUALS lit231] W20 {W21, W22}" 
								+ "\nLayer 2: {W12} [output121 EQUALS lit191] W19 {W24}, {W20} [] W21 {W23}, {W20} [] W22 {W23}" 
								+ "\nLayer 3: {W19} [] W24 {}, {W21, W22} [output221 EQUALS lit232] W23 {}");
		
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
		
		return plans.size();
	}
}