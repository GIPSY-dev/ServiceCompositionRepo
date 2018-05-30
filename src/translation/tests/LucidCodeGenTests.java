package translation.tests;

import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.entities.ASTNode;
import translation.translationprocesses.ASTBuilder;
import translation.translationprocesses.LucidCodeGenVisitor;

public class LucidCodeGenTests 
{
	@Test
	public void complexPlanTranslation()
	{
		String repoFileName = "testinput/complexPlanTranslation/Test_Services_Set_5.xml";
		String inputString = "input11, input21, input31, input32, input41";
		String outputString = "output15, output86, output91, output101, output111";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		for (ConstraintAwarePlan cnstrAwrPlan : cnstrAwrPlans)
		{
			ASTNode rootNode = ASTBuilder.cnstrAwrPlanToAST(cnstrAwrPlan, compRequest);
			LucidCodeGenVisitor LCGVisitor = new LucidCodeGenVisitor();
			rootNode.accept(LCGVisitor);
			
			System.out.println("Program:");
			System.out.println(LCGVisitor.getLucidCode());
		}
	}
}