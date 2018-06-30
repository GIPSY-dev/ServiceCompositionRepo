package servicecomposition.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import servicecomposition.readers.FileConfigReader;
import servicecomposition.readers.RequestConfiguration;
import servicecomposition.readers.XMLFileConfigReader;

/**
 * Class for testing the complete process of constraint-aware service composition.
 * @author Jyotsana Gupta
 */
public class ServiceCompositionTests 
{	
	/**
	 * Tests successful completion of service composition process.
	 */
	@Test
	public void serviceComposition()
	{
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/serviceComposition/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		List<String> actualPlanDetails = new ArrayList<String>();
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			actualPlanDetails.add(cnstrAwrPlan.toString());
		}
		Collections.sort(actualPlanDetails);
		
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
				
		assertEquals(actualPlanDetails, expectedPlanDetails);
	}
	
	/**
	 * Tests failure of service composition process if a valid composition request cannot be created.
	 */
	@Test
	public void serviceCompositionAbort()
	{
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/serviceCompositionAbort/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Creation of a composition request when all its components are valid.
	 * 2. Correct use of comma as a separator for splitting requested component lists.
	 * 3. Trimming of leading and trailing spaces from request components and discarding empty components.
	 * 4. Trimming/discarding of all spaces between elements of a constraint.
	 * 5. Correct fetching of constraint operator name based on the given operator symbol.
	 * 6. Correct use of pipe symbol for splitting constraint elements.
	 * 7. Allowing spaces in parameter names.
	 */
	@Test
	public void compReqComponentCreation()
	{
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/compReqComponentCreation/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
		String expectedCompReq = "Inputs: string : Delivery Address, string : Product Name, string : Customer Name, float : Price" 
								+ "\nOutputs: string : Shipment Confirmation, string : Invoice" 
								+ "\nQoS: " 
								+ "\nConstraints: (CompositeService) float : Price LESS_THAN 100, (CompositeService) string : Invoice EQUALS true, (CompositeService) string : Delivery Address EQUALS Canada";
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/compReqCnstrOpFormatValidation/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/noRequestedInput/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/noRequestedOutput/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/invalidQOS/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/invalidConstraintType/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(reqConfig);
		
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
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/emptySvcRepository/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests failure of service composition process if the given composition request cannot be served by the given service repository.
	 */
	@Test
	public void unsolvableProblem()
	{
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/unsolvableProblem/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		
		assertNull(cnstrAwrPlans);
	}
	
	/**
	 * Tests failure of service composition process if the given composition request can be served by a 
	 * single service from the given service repository, and does not require a composition to be created.
	 */
	@Test
	public void noSvcCompositionReqd()
	{
		FileConfigReader configReader = new XMLFileConfigReader();
		configReader.setConfigFileName("testinput/noSvcCompositionReqd/Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.driveServiceComposition(reqConfig);
		
		assertNull(cnstrAwrPlans);
	}
}