package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import translation.translators.TranslatorUtil;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for testing functionality that relates to multiple composite service translators.
 * @author Jyotsana Gupta
 */
public class GeneralTranslatorTests 
{
	/**
	 * Method for testing successful validation of all composite service input values.
	 */
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
		
		String actualLogFileName = "testinput/translationtests/generaltranslatortests/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);	
		
		boolean inpValid = TranslatorUtil.validateInpValues(inputDetails, logger);
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		assertTrue(inpValid);
		assertFalse(logGenerated);
	}
	
	/**
	 * Method for testing validation failure scenarios for char type composite service input values.
	 */
	@Test
	public void invalidCharInpVal()
	{
		List<String[]> inputDetails1 = new ArrayList<String[]>();
		List<String[]> inputDetails2 = new ArrayList<String[]>();
		String[] input1 = {"charInput", "char", ""};
		String[] input2 = {"charInput", "char", "abc"};
		inputDetails1.add(input1);
		inputDetails2.add(input2);
		
		String actualLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidCharInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidCharInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidCharInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidCharInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		boolean inpValid1 = TranslatorUtil.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = TranslatorUtil.validateInpValues(inputDetails2, logger2);
		
		String actualLog1 = ReadWriteUtil.readTextFile(actualLogFileName1);
		String expectedLog1 = ReadWriteUtil.readTextFile(expectedLogFileName1);
		String actualLog2 = ReadWriteUtil.readTextFile(actualLogFileName2);
		String expectedLog2 = ReadWriteUtil.readTextFile(expectedLogFileName2);
		
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertEquals(expectedLog1, actualLog1);
		assertEquals(expectedLog2, actualLog2);
	}
	
	/**
	 * Method for testing validation failure scenarios for integer type composite service input values.
	 */
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
		
		String actualLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		String actualLogFileName4 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/log4.txt";
		String expectedLogFileName4 = "testinput/translationtests/generaltranslatortests/invalidIntInpVal/expectedlog4.txt";
		LogUtil logger4 = new LogUtil();
		logger4.setLogFileName(actualLogFileName4);
		
		boolean inpValid1 = TranslatorUtil.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = TranslatorUtil.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = TranslatorUtil.validateInpValues(inputDetails3, logger3);
		boolean inpValid4 = TranslatorUtil.validateInpValues(inputDetails4, logger4);
		
		String actualLog1 = ReadWriteUtil.readTextFile(actualLogFileName1);
		String expectedLog1 = ReadWriteUtil.readTextFile(expectedLogFileName1);
		String actualLog2 = ReadWriteUtil.readTextFile(actualLogFileName2);
		String expectedLog2 = ReadWriteUtil.readTextFile(expectedLogFileName2);
		String actualLog3 = ReadWriteUtil.readTextFile(actualLogFileName3);
		String expectedLog3 = ReadWriteUtil.readTextFile(expectedLogFileName3);
		String actualLog4 = ReadWriteUtil.readTextFile(actualLogFileName4);
		String expectedLog4 = ReadWriteUtil.readTextFile(expectedLogFileName4);
		
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
		assertFalse(inpValid4);
		assertEquals(expectedLog1, actualLog1);
		assertEquals(expectedLog2, actualLog2);
		assertEquals(expectedLog3, actualLog3);
		assertEquals(expectedLog4, actualLog4);		
	}
	
	/**
	 * Method for testing validation failure scenarios for floating-point type composite service input values.
	 */
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
		
		String actualLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidFloatInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		boolean inpValid1 = TranslatorUtil.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = TranslatorUtil.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = TranslatorUtil.validateInpValues(inputDetails3, logger3);
		
		String actualLog1 = ReadWriteUtil.readTextFile(actualLogFileName1);
		String expectedLog1 = ReadWriteUtil.readTextFile(expectedLogFileName1);
		String actualLog2 = ReadWriteUtil.readTextFile(actualLogFileName2);
		String expectedLog2 = ReadWriteUtil.readTextFile(expectedLogFileName2);
		String actualLog3 = ReadWriteUtil.readTextFile(actualLogFileName3);
		String expectedLog3 = ReadWriteUtil.readTextFile(expectedLogFileName3);
		
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
		assertEquals(expectedLog1, actualLog1);
		assertEquals(expectedLog2, actualLog2);
		assertEquals(expectedLog3, actualLog3);
	}
	
	/**
	 * Method for testing validation failure scenarios for boolean type composite service input values.
	 */
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
		
		String actualLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/generaltranslatortests/invalidBooleanInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		boolean inpValid1 = TranslatorUtil.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = TranslatorUtil.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = TranslatorUtil.validateInpValues(inputDetails3, logger3);
		
		String actualLog1 = ReadWriteUtil.readTextFile(actualLogFileName1);
		String expectedLog1 = ReadWriteUtil.readTextFile(expectedLogFileName1);
		String actualLog2 = ReadWriteUtil.readTextFile(actualLogFileName2);
		String expectedLog2 = ReadWriteUtil.readTextFile(expectedLogFileName2);
		String actualLog3 = ReadWriteUtil.readTextFile(actualLogFileName3);
		String expectedLog3 = ReadWriteUtil.readTextFile(expectedLogFileName3);
		
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertFalse(inpValid3);
		assertEquals(expectedLog1, actualLog1);
		assertEquals(expectedLog2, actualLog2);
		assertEquals(expectedLog3, actualLog3);
	}
	
	/**
	 * Method for testing validation failure scenarios for string type composite service input values.
	 */
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
		
		String actualLogFileName = "testinput/translationtests/generaltranslatortests/invalidStringInpVal/log.txt";
		String expectedLogFileName = "testinput/translationtests/generaltranslatortests/invalidStringInpVal/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
		
		boolean inpValid = TranslatorUtil.validateInpValues(inputDetails, logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertFalse(inpValid);
		assertEquals(expectedLog, actualLog);
	}
}