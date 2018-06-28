package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.entities.CompositionRequest;
import servicecomposition.entities.ConstraintAwarePlan;
import translation.translationprocesses.LucidTranslationDriver;
import translation.utilities.ReadWriteUtil;

public class LucidCodeGenTests 
{
	@Test
	public void translationCLI()
	{
		String actualLucidFileName = "testinput/translationCLI/testobjlucidprogram.ipl";
		String expectedLucidFileName = "testinput/translationCLI/expectedtestobjlucidprogram.ipl";
		String repoFileName = "testinput/translationCLI/Test_Services_Set_3.xml";
		String inputString = "char : input271";
		String outputString = "string : output311";
		String qosString = "";
		String constraintString = "";
		CompositionRequest compRequest = ServiceComposition.constructCompositionRequest(inputString, outputString, qosString, constraintString);
		List<ConstraintAwarePlan> cnstrAwrPlans = ServiceComposition.buildServiceCompositions(compRequest, repoFileName);
		
		LucidTranslationDriver.driveTranslation(cnstrAwrPlans.get(0), compRequest, actualLucidFileName);
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
	}
	
	@Test
	public void complexPlanTranslation()
	{
		String actualLucidFileName = "testinput/complexPlanTranslation/testobjlucidprogram.ipl";
		String expectedLucidFileName = "testinput/complexPlanTranslation/expectedtestobjlucidprogram.ipl";
		String repoFileName = "testinput/complexPlanTranslation/Test_Services_Set_5.xml";
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
		
		LucidTranslationDriver.compPlanToObjLucid(cnstrAwrPlans.get(0), compRequest, compSvcInputs, actualLucidFileName);
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
	}
	
	@Test
	public void validInputValues()
	{
		List<String[]> inputDetails = new ArrayList<String[]>();
		String[] input1 = {"floatInput", "float", " 12.34 "};
		String[] input2 = {"stringInput", "string", "abc_12 xyz*34"};
		String[] input3 = {"intInput", "int", "	-5678	"};
		String[] input4 = {"booleanInput", "boolean", "false  "};
		String[] input5 = {"charInput", "char", "x"};
		inputDetails.add(input1);
		inputDetails.add(input2);
		inputDetails.add(input3);
		inputDetails.add(input4);
		inputDetails.add(input5);
		
		boolean inpValid = LucidTranslationDriver.validateInpValues(inputDetails);
		assertTrue(inpValid);
	}
	
	@Test
	public void invalidCharInpVal()
	{
		List<String[]> inputDetails1 = new ArrayList<String[]>();
		List<String[]> inputDetails2 = new ArrayList<String[]>();
		String[] input1 = {"charInput", "char", ""};
		String[] input2 = {"charInput", "char", "abc"};
		inputDetails1.add(input1);
		inputDetails2.add(input2);
		
		boolean inpValid1 = LucidTranslationDriver.validateInpValues(inputDetails1);
		boolean inpValid2 = LucidTranslationDriver.validateInpValues(inputDetails2);
		assertFalse(inpValid1);
		assertFalse(inpValid2);
	}
	
	@Test
	public void invalidIntInpVal()
	{
		List<String[]> inputDetails1 = new ArrayList<String[]>();
		List<String[]> inputDetails2 = new ArrayList<String[]>();
		List<String[]> inputDetails3 = new ArrayList<String[]>();
		List<String[]> inputDetails4 = new ArrayList<String[]>();
		String[] input1 = {"intInput", "int", ""};
		String[] input2 = {"intInput", "int", "abc"};
		String[] input3 = {"intInput", "int", "-"};
		String[] input4 = {"intInput", "int", "12.34"};
		inputDetails1.add(input1);
		inputDetails2.add(input2);
		inputDetails3.add(input3);
		inputDetails4.add(input4);
		
		boolean inpValid1 = LucidTranslationDriver.validateInpValues(inputDetails1);
		boolean inpValid2 = LucidTranslationDriver.validateInpValues(inputDetails2);
		boolean inpValid3 = LucidTranslationDriver.validateInpValues(inputDetails3);
		boolean inpValid4 = LucidTranslationDriver.validateInpValues(inputDetails4);
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
		assertFalse(inpValid4);
	}
	
	@Test
	public void invalidFloatInpVal()
	{
		List<String[]> inputDetails1 = new ArrayList<String[]>();
		List<String[]> inputDetails2 = new ArrayList<String[]>();
		List<String[]> inputDetails3 = new ArrayList<String[]>();
		String[] input1 = {"floatInput", "float", ""};
		String[] input2 = {"floatInput", "float", "abc"};
		String[] input3 = {"floatInput", "float", "-"};
		inputDetails1.add(input1);
		inputDetails2.add(input2);
		inputDetails3.add(input3);
		
		boolean inpValid1 = LucidTranslationDriver.validateInpValues(inputDetails1);
		boolean inpValid2 = LucidTranslationDriver.validateInpValues(inputDetails2);
		boolean inpValid3 = LucidTranslationDriver.validateInpValues(inputDetails3);
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
	}
	
	@Test
	public void invalidBooleanInpVal()
	{
		List<String[]> inputDetails1 = new ArrayList<String[]>();
		List<String[]> inputDetails2 = new ArrayList<String[]>();
		List<String[]> inputDetails3 = new ArrayList<String[]>();
		String[] input1 = {"booleanInput", "boolean", ""};
		String[] input2 = {"booleanInput", "boolean", "	  "};
		String[] input3 = {"booleanInput", "boolean", "TRUE"};
		inputDetails1.add(input1);
		inputDetails2.add(input2);
		inputDetails3.add(input3);
		
		boolean inpValid1 = LucidTranslationDriver.validateInpValues(inputDetails1);
		boolean inpValid2 = LucidTranslationDriver.validateInpValues(inputDetails2);
		boolean inpValid3 = LucidTranslationDriver.validateInpValues(inputDetails3);
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
	}
	
	@Test
	public void invalidStringInpVal()
	{
		List<String[]> inputDetails = new ArrayList<String[]>();
		String[] input1 = {"booleanInput", "boolean", "true"};
		String[] input2 = {"stringInput", "string", ""};
		String[] input3 = {"intInput", "int", "1234"};
		inputDetails.add(input1);
		inputDetails.add(input2);
		inputDetails.add(input3);
		
		boolean inpValid1 = LucidTranslationDriver.validateInpValues(inputDetails);
		assertFalse(inpValid1);
	}
}