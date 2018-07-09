package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import constraint.Constraint;
import service.ConstrainedService;
import service.Service;
import service.composite.layeredcompsvc.LayeredCompositeService;
import translation.readers.csconfigreaders.CSConfiguration;
import translation.readers.csconfigreaders.FileCSConfigReader;
import translation.readers.csconfigreaders.XMLFileCSConfigReader;
import translation.readers.csreaders.CompositeServiceReader;
import translation.readers.csreaders.SerializedCSReader;
import translation.readers.csreaders.XMLCSReader;
import translation.translationprocesses.CompositeServiceTranslator;
import translation.translationprocesses.LucidCSTranslator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for testing the composite service and configuration readers.
 * @author Jyotsana Gupta
 */
public class ReaderTests 
{
	/**
	 * Tests that repositories of invalid types are not parsed and error messages are recorded in the log file for the same.
	 */
	@Test
	public void invalidRepoType()
	{
		String actualLogFileName = "testinput/translationtests/readertests/invalidRepoType/log.txt";
		String expectedLogFileName = "testinput/translationtests/readertests/invalidRepoType/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/readertests/invalidRepoType/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertNull(csConfig);
		assertEquals(expectedLog, actualLog);
	}
	
	/**
	 * Tests that translation is aborted if the given composite service input values could not be validated
	 * and error messages are recorded in the log file for the same.
	 */
	@Test
	public void invalidInput()
	{
		String actualLogFileName = "testinput/translationtests/readertests/invalidInput/log.txt";
		String expectedLogFileName = "testinput/translationtests/readertests/invalidInput/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/readertests/invalidInput/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new LucidCSTranslator();
		String csTranslationFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertNull(csTranslationFileName);
		assertEquals(expectedLog, actualLog);
	}
	
	/**
	 * Tests correct reading of a specific composite service from a serialized Java object repository.
	 */
	@Test
	public void readSerializedCS()
	{
		String csRepoFileName = "testinput/translationtests/readertests/readSerializedCS/Serialized_Repository.txt";
		String csName = "CompSvc_883413469834650";
		String actualLogFileName = "testinput/translationtests/readertests/readSerializedCS/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
		
		CompositeServiceReader csReader = new SerializedCSReader();
		Service service = csReader.readCompositeService(csRepoFileName, csName, logger);
		
		String actualCSName = service.getName();
		List<String> actualCSInputs = service.getInput();
		List<String> actualCSOutputs = service.getOutput();
		List<String> actualCSEffects = ((ConstrainedService)service.getInnerService()).getEffects();
		List<Constraint> actCSConstraints = ((ConstrainedService)service.getInnerService()).getConstraints();
		List<String> actualCSConstraints = new ArrayList<String>();
		for (Constraint constraint : actCSConstraints)
		{
			String cnstrStr = constraint.getType() + " " + constraint.getOperator() + " " + constraint.getLiteralValue();
			actualCSConstraints.add(cnstrStr);
		}
		String actualCSPlan = ((LayeredCompositeService)service).getCompositionPlan().toString();
		
		List<String> expectedCSInputs = new ArrayList<String>();
		expectedCSInputs.add("char : input271");
		List<String> expectedCSOutputs = new ArrayList<String>();
		expectedCSOutputs.add("string : output311");
		String[] expCSEffects = {"string : output311", "int : output281", "float : output291", "char : output301", "boolean : output272", "string : output271"};
		List<String> expectedCSEffects = new ArrayList<String>(Arrays.asList(expCSEffects));
		List<String> expectedCSConstraints = new ArrayList<String>();
		expectedCSConstraints.add("boolean : output272 EQUALS true");
		String expectedCSPlan = "Layer 0: {} [] W27 {W28}\n" + 
								"Layer 1: {W27} [] W28 {W29}\n" + 
								"Layer 2: {W28} [] W29 {W30}\n" + 
								"Layer 3: {W29} [boolean : output272 EQUALS true] W30 {W31}\n" + 
								"Layer 4: {W30} [] W31 {}";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		assertEquals(csName, actualCSName);
		assertEquals(actualCSInputs, expectedCSInputs);
		assertEquals(actualCSOutputs, expectedCSOutputs);
		assertEquals(actualCSEffects, expectedCSEffects);
		assertEquals(actualCSConstraints, expectedCSConstraints);
		assertEquals(actualCSPlan, expectedCSPlan);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests that translation is aborted if the target composite service is not found in the given
	 * serialized Java object repository and error messages are recorded in the log file for the same.
	 */
	@Test
	public void missingSerializedService()
	{
		String actualLogFileName = "testinput/translationtests/readertests/missingSerializedService/log.txt";
		String expectedLogFileName = "testinput/translationtests/readertests/missingSerializedService/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/readertests/missingSerializedService/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertNull(csConfig);
		assertEquals(expectedLog, actualLog);
	}
	
	/**
	 * Tests correct reading of a specific composite service from an XML repository.
	 */
	@Test
	public void readXMLCS()
	{
		String csRepoFileName = "testinput/translationtests/readertests/readXMLCS/XML_Repository.xml";
		String csName = "CompSvc_123";
		String actualLogFileName = "testinput/translationtests/readertests/readXMLCS/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
		
		CompositeServiceReader csReader = new XMLCSReader();
		Service service = csReader.readCompositeService(csRepoFileName, csName, logger);
		
		String actualCSName = service.getName();
		List<String> actualCSInputs = service.getInput();
		List<String> actualCSOutputs = service.getOutput();
		List<String> actualCSEffects = ((ConstrainedService)service.getInnerService()).getEffects();
		List<Constraint> actCSConstraints = ((ConstrainedService)service.getInnerService()).getConstraints();
		List<String> actualCSConstraints = new ArrayList<String>();
		for (Constraint constraint : actCSConstraints)
		{
			String cnstrStr = constraint.getType() + " " + constraint.getOperator() + " " + constraint.getLiteralValue();
			actualCSConstraints.add(cnstrStr);
		}
		String actualCSPlan = ((LayeredCompositeService)service).getCompositionPlan().toString();
		
		List<String> expectedCSInputs = new ArrayList<String>();
		expectedCSInputs.add("string : StudentID");
		List<String> expectedCSOutputs = new ArrayList<String>();
		expectedCSOutputs.add("float : MarksPercentage");
		String[] expCSEffects = {"float : TotalMarks", "int : NumberOfCourses", "float : AverageMarks", "float : MarksPercentage"};
		List<String> expectedCSEffects = new ArrayList<String>(Arrays.asList(expCSEffects));
		String[] expCSConstraints = {"int : NumberOfCourses GREATER_THAN 1", "float : AverageMarks LESS_THAN_OR_EQUAL_TO 100.0"};
		List<String> expectedCSConstraints = new ArrayList<String>(Arrays.asList(expCSConstraints));
		String expectedCSPlan = "Layer 0: {} [] W8 {W9}\n" + 
								"Layer 1: {W8} [int : NumberOfCourses GREATER_THAN 1] W9 {W10}\n" + 
								"Layer 2: {W9} [float : AverageMarks LESS_THAN_OR_EQUAL_TO 100.0] W10 {}";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		assertEquals(csName, actualCSName);
		assertEquals(actualCSInputs, expectedCSInputs);
		assertEquals(actualCSOutputs, expectedCSOutputs);
		assertEquals(actualCSEffects, expectedCSEffects);
		assertEquals(actualCSConstraints, expectedCSConstraints);
		assertEquals(actualCSPlan, expectedCSPlan);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests that translation is aborted if the target composite service is not found in the given
	 * XML repository and error messages are recorded in the log file for the same.
	 */
	@Test
	public void missingXMLService()
	{
		String actualLogFileName = "testinput/translationtests/readertests/missingXMLService/log.txt";
		String expectedLogFileName = "testinput/translationtests/readertests/missingXMLService/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/readertests/missingXMLService/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertNull(csConfig);
		assertEquals(expectedLog, actualLog);
	}
}