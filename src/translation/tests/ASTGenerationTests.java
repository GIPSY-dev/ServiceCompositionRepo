package translation.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.entities.ASTNode;
import translation.translationprocesses.ASTBuilder;
import translation.utilities.ASTUtil;

public class ASTGenerationTests 
{
	@Test
	public void complexPlanTranslation()
	{
		String repoFileName = "testinput/Test_Services_Set_5.xml";
		String inputString = "input11, input21, input31, input32, input41";
		String outputString = "output15, output86, output91, output101, output111";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		List<String> astStrings = new ArrayList<String>();
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			ASTNode rootNode = ASTBuilder.cnstrAwrPlanToAST(cnstrAwrPlan, compRequest);
			String astString = ASTUtil.getASTString(rootNode);
			astStrings.add(astString);
			
			System.out.println("AST\n" + astString);
		}
		
		//TODO 
		//write expected ast output to files, read from there and compare with actual output
		String ast = "";
	}
	
	@Test
	public void simplePlanTranslation()
	{
		//TODO
		//prepare case for testing root node with just 1 child - simple plan
	}
}