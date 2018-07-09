package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import translation.readers.csreaders.XMLCSReader;
import translation.translators.CompositeServiceTranslator;
import translation.translators.XMLCSTranslator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for testing the layered composite service to XML translator.
 * @author Jyotsana Gupta
 */
public class XMLTranslatorTests 
{
	/**
	 * Tests that a composite service can be searched for by its name in an XML service repository, 
	 * parsed into a composite service object and translated into an XML file.
	 */
	@Test
	public void simpleCSTranslation()
	{		
		String actualLogFileName = "testinput/translationtests/xmltranslatortests/simpleCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/xmltranslatortests/simpleCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new XMLCSTranslator();
		String actualXMLFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedXMLFileName = "testinput/translationtests/xmltranslatortests/simpleCSTranslation/expectedxml.xml";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedXMLText = ReadWriteUtil.readTextFile(expectedXMLFileName);
		String actualXMLText = ReadWriteUtil.readTextFile(actualXMLFileName);
		assertEquals(expectedXMLText, actualXMLText);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests that the XML translation of a composite service object can be parsed successfully 
	 * to generate the same composite service object again.
	 */
	@Test
	public void csObjXMLEquivalence()
	{
		//Reading a composite service from an XML repository, creating its composite service object
		//and translating the object back to an XML representation
		String actualLogFileName = "testinput/translationtests/xmltranslatortests/csObjXMLEquivalence/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/xmltranslatortests/csObjXMLEquivalence/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new XMLCSTranslator();
		String xmlFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		
		//Reading the XML translation of the composite service generated above and creating its composite service object
		String csName = xmlFileName.substring(xmlFileName.lastIndexOf("CSXML_") + 6, xmlFileName.lastIndexOf(".xml"));  
		
		CompositeServiceReader csReader = new XMLCSReader();
		Service service = csReader.readCompositeService(xmlFileName, csName, logger);
		
		//Checking if the composite service object matches the expected composite service object
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
}