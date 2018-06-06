package translation.tests;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.entities.ASTNode;
import translation.translationprocesses.ASTBuilder;
import translation.translationprocesses.JavaCodeGenerator;
import translation.translationprocesses.LucidCodeGenVisitor;
import translation.translationprocesses.ObjLucidCodeGenerator;

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
	
	@Test
	public void complexPlanTranslation2()
	{
		String repoFileName = "testinput/complexPlanTranslation/Test_Services_Set_6.xml";
		String inputString = "int : input11, int : input21, string : input31, int : input32, int : input41";
		String outputString = "float : output15, int : output86, char : output91, boolean : output101, string : output111";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		List<String[]> compSvcInputs = new ArrayList<String[]>();
		String[] inpDetails1 = {"input11", "int", "11"};
		String[] inpDetails2 = {"input21", "int", "15"};
		String[] inpDetails3 = {"input31", "string", "xyz12abc"};
		String[] inpDetails4 = {"input32", "int", "45"};
		String[] inpDetails5 = {"input41", "int", "79"};
		compSvcInputs.add(inpDetails1);
		compSvcInputs.add(inpDetails2);
		compSvcInputs.add(inpDetails3);
		compSvcInputs.add(inpDetails4);
		compSvcInputs.add(inpDetails5);
		
		String lucidCode = ObjLucidCodeGenerator.generateObjLucidSegment(cnstrAwrPlans.get(0), compRequest, compSvcInputs);
		String javaCode = JavaCodeGenerator.generateJavaSegment(cnstrAwrPlans.get(0), compRequest);
		System.out.println(javaCode);
	}
}