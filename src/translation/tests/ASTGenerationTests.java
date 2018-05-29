package translation.tests;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.entities.ASTNode;
import translation.translationprocesses.ASTBuilder;
import translation.utilities.ASTUtil;
import translation.utilities.ReadWriteUtil;

/**
 * Class for testing translation of constraint-aware composition plans into abstract syntax trees.
 * @author Jyotsana Gupta
 */
public class ASTGenerationTests 
{
	/**
	 * Tests the following requirements:
	 * 1. Translation of a complex composition plan into an AST.
	 * 2. Output accumulator node can have multiple sequential execution operators as children.
	 * 3. Every sequential execution operator has 2 children (in order): parallel execution operator and service node.
	 * 4. Parallel execution operator can have 1 child (data node/sequential execution operator).
	 * 5. Parallel execution operator can have multiple children (any combination of data nodes and sequential execution operators).
	 * 6. Service node cannot have a child.
	 * 7. Data node cannot have a child.
	 * 8. Correct names, types and hierarchy of nodes in the AST.
	 */
	@Test
	public void complexPlanTranslation()
	{
		//Creating composition request and generating constraint-aware plans
		String repoFileName = "testinput/complexPlanTranslation/Test_Services_Set_5.xml";
		String inputString = "input11, input21, input31, input32, input41";
		String outputString = "output15, output86, output91, output101, output111";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		//Translating plans into ASTs
		List<String> astStrings = new ArrayList<String>();
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			ASTNode rootNode = ASTBuilder.cnstrAwrPlanToAST(cnstrAwrPlan, compRequest);
			String astString = ASTUtil.getASTString(rootNode);
			astStrings.add(astString);
		}
		Collections.sort(astStrings);
		
		//Reading expected AST Strings from text files
		String expectedASTFileName1 = "testinput/complexPlanTranslation/expectedAST1.txt";
		String expectedASTFileName2 = "testinput/complexPlanTranslation/expectedAST2.txt";
		String expectedAST1 = ReadWriteUtil.readTextFile(expectedASTFileName1);
		String expectedAST2 = ReadWriteUtil.readTextFile(expectedASTFileName2);
		
		//Comparing actual and expected results				
		assertEquals(expectedAST1.trim(), astStrings.get(0).trim());
		assertEquals(expectedAST2.trim(), astStrings.get(1).trim());
	}
	
	/**
	 * Tests the following requirements:
	 * 1. Translation of a simple composition plan into an AST.
	 * 2. Output accumulator node can have 1 sequential execution operator as a child.
	 */
	@Test
	public void simplePlanTranslation()
	{
		//Creating composition request and generating constraint-aware plan
		String repoFileName = "testinput/simplePlanTranslation/Test_Services_Set_3.xml";
		String inputString = "input271";
		String outputString = "output311";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		//Translating plan into AST
		ASTNode rootNode = ASTBuilder.cnstrAwrPlanToAST(cnstrAwrPlans.get(0), compRequest);
		String astString = ASTUtil.getASTString(rootNode);
		
		//Reading expected AST String from text file
		String expectedASTFileName = "testinput/simplePlanTranslation/expectedAST.txt";
		String expectedAST = ReadWriteUtil.readTextFile(expectedASTFileName);
		
		//Comparing actual and expected results				
		assertEquals(expectedAST.trim(), astString.trim());
	}
}