package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import compositionprocesses.ServiceComposition;
import entities.CompositionRequest;
import entities.ConstraintAwarePlan;

/**
 * Class for testing the complete process of constraint-aware service composition.
 * @author Jyotsana Gupta
 */
public class ServiceCompositionTests 
{
	/**
	 * Tests successful completion of service composition process, including user interaction.
	 * For the following console input, this test should complete successfully:
	 * Comma-separated list of inputs:
	 * DeliveryAddress, ProductName
	 * Comma-separated list of outputs:
	 * ShipmentConfirm
	 * Comma-separated list of QoS features:
	 * COST, AVAILABILITY
	 * Comma-separated list of constraints:
	 * COST < 100, ShipmentConfirm = true, DeliveryAddress = Canada
	 * Please enter the complete file path and name of the service repository XML file:
	 * testinput/Test_Services_Set_3.xml
	 */
	@Test
	public void serviceComposition()
	{
		List<String> actualPlanDetails = new ArrayList<String>();
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition();
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			actualPlanDetails.add(cnstrAwrPlan.toString());
		}
		Collections.sort(actualPlanDetails);
		
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
				
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests failure of service composition process if a valid composition request cannot be created.
	 * For the following console input, this test should complete successfully 
	 * and an error message should be printed on the console:
	 * Comma-separated list of inputs:
	 * 
	 * Comma-separated list of outputs:
	 * ShipmentConfirm
	 * Comma-separated list of QoS features:
	 * COST, AVAILABILITY
	 * Comma-separated list of constraints:
	 * COST < 100, ShipmentConfirm = true, DeliveryAddress = Canada
	 * Please enter the complete file path and name of the service repository XML file:
	 * testinput/Test_Services_Set_3.xml
	 */
	@Test
	public void serviceCompositionAbort()
	{
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition();
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Creation of a composition request when all its components are valid.
	 * 2. Correct use of comma as a separator for splitting requested component lists.
	 * 3. Trimming of leading and trailing spaces from request components and discarding empty components.
	 * 4. Trimming/discarding of all spaces between elements of a constraint.
	 * 5. Correct fetching of constraint operator name based on the given operator symbol.
	 */
	@Test
	public void compReqComponentCreation()
	{
		String inputString = "DeliveryAddress, Product Name, Customer Name, Price";
		String outputString = ", Shipment Confirmation  ,  Invoice , 	 ,  	";
		String qosString = "    		";
		String constraintString = "	 Price < 	100  , 		Invoice		= true , DeliveryAddress = Canada";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		String expectedCompReq = "Inputs: DeliveryAddress, Product Name, Customer Name, Price" 
								+ "\nOutputs: Shipment Confirmation, Invoice" 
								+ "\nQoS: " 
								+ "\nConstraints: (CompositeService) Price LESS_THAN 100, (CompositeService) Invoice EQUALS true, (CompositeService) DeliveryAddress EQUALS Canada";
		
		assertEquals(expectedCompReq, compRequest.toString());
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Format validation failure for constraints with less than or more than 3 elements.
	 * 2. Operator validation failure for constraints with unacceptable operators.
	 * 3. Failure in creation of a composition request if validations fail.
	 */
	@Test
	public void compReqCnstrOpFormatValidation()
	{
		String inputString = "DeliveryAddress, ProductName, CustomerName, Price";
		String outputString = "ShipmentConfirm, Invoice";
		String qosString = "COST";
		String constraintString = "COST < , Invoice <> false, DeliveryAddress = Quebec City";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		assertNull(compRequest);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Missing input error when no inputs are provided in the composition request.
	 * 2. Failure in creation of a composition request if validations fail.
	 */
	@Test
	public void noRequestedInput()
	{
		String inputString = ", , , ";
		String outputString = "ShipmentConfirm, Invoice";
		String qosString = "COST";
		String constraintString = "COST < 100, Invoice = true";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		assertNull(compRequest);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Missing output error when no outputs are requested in the composition request.
	 * 2. Failure in creation of a composition request if validations fail.
	 */
	@Test
	public void noRequestedOutput()
	{
		String inputString = "DeliveryAddress, ProductName, CustomerName, Price";
		String outputString = "";
		String qosString = "COST";
		String constraintString = "COST < 100, DeliveryAddress = Quebec";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		assertNull(compRequest);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Unidentified QoS error when a requested QoS is not from the predefined list of acceptable QoS features.
	 * 2. Failure in creation of a composition request if validations fail.
	 */
	@Test
	public void invalidQOS()
	{
		String inputString = "DeliveryAddress, ProductName, CustomerName, Price";
		String outputString = "ShipmentConfirm, Invoice";
		String qosString = "COST, THROUGHPUT, response_time";
		String constraintString = "COST < 100, DeliveryAddress = Quebec, Invoice = true";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		assertNull(compRequest);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Invalid constraint type error when a requested constraint is applied on features other than the requested inputs, outputs and QoS.
	 * 2. Failure in creation of a composition request if validations fail.
	 */
	@Test
	public void invalidConstraintType()
	{
		String inputString = "DeliveryAddress, ProductName, CustomerName, Price";
		String outputString = "ShipmentConfirm, Invoice";
		String qosString = "COST";
		String constraintString = "AVAILABILITY = 60, DeliveryAddress = Quebec, Invoice = true";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		
		assertNull(compRequest);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Empty service repository error when the given repository has no available services.
	 * 2. Failure of service composition process if validations fail.
	 */
	@Test
	public void emptySvcRepository()
	{
		String inputString = "DeliveryAddress, ProductName";
		String outputString = "ShipmentConfirm";
		String qosString = "COST";
		String constraintString = "COST < 100, DeliveryAddress = Canada";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		String repoFileName = "testinput/Test_Services_Set_4.xml";
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests failure of service composition process if the given composition request cannot be served by the given service repository.
	 */
	@Test
	public void unsolvableProblem()
	{
		String inputString = "DeliveryAddress, ProductName";
		String outputString = "Invoice";
		String qosString = "COST";
		String constraintString = "COST < 100, DeliveryAddress = Canada";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		String repoFileName = "testinput/Test_Services_Set_3.xml";
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests failure of service composition process if the given composition request can be served by a 
	 * single service from the given service repository, and does not require a composition to be created.
	 */
	@Test
	public void noSvcCompositionReqd()
	{
		String inputString = "DeliveryAddress, ProductName";
		String outputString = "ProductNumber";
		String qosString = "COST";
		String constraintString = "COST < 100, DeliveryAddress = Canada";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		String repoFileName = "testinput/Test_Services_Set_3.xml";
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		assertNull(cnstrAwrPlans);
	}
}