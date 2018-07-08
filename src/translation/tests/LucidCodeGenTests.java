package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import service.Service;
import service.parser.BasicServiceParser;
import service.parser.ConstrainedServiceXMLParser;
import service.parser.ServiceFileParserDecorator;
import service.parser.ServiceSerializedParser;
import service.writer.BasicServiceWriter;
import service.writer.ServiceFileWriterDecorator;
import service.writer.ServiceSerializedWriter;
import servicecomposition.compositionprocesses.ServiceComposition;
import servicecomposition.readers.FileReqConfigReader;
import servicecomposition.readers.RequestConfiguration;
import servicecomposition.readers.XMLFileReqConfigReader;
import translation.readers.csconfigreaders.CSConfiguration;
import translation.readers.csconfigreaders.FileCSConfigReader;
import translation.readers.csconfigreaders.XMLFileCSConfigReader;
import translation.translationprocesses.LucidCSTranslator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

public class LucidCodeGenTests 
{
	/**
	 * Tests that a composite service can be searched for by its name in a service repository of serialized 
	 * Java objects, parsed into a composite service object and translated into a Lucid program.
	 */
	@Test
	public void csTranslation()
	{
		/*
		 * The commented part below needs to be executed only once. Its main purpose is to create a service
		 * repository of serialized Java objects, store a composite service into it and get the name of the
		 * composite service. All this is required for the translation process. 
		 */
		//createSerialCSRepo("testinput/translationtests/csTranslation/");
		
		String actualLogFileName = "testinput/translationtests/csTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/csTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		String actualLucidFileName = LucidCSTranslator.driveServiceTranslation(csConfig, logger);
		String expectedLucidFileName = "testinput/translationtests/csTranslation/expectedlucidprogram.ipl";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
		assertFalse(logGenerated);
	}
	
	@Test
	public void complexCSTranslation()
	{
		/*
		 * The commented part below needs to be executed only once. Its main purpose is to create a service
		 * repository of serialized Java objects, store a composite service into it and get the name of the
		 * composite service. All this is required for the translation process. 
		 */
		//createSerialCSRepo("testinput/translationtests/complexCSTranslation/");
		
		String actualLogFileName = "testinput/translationtests/complexCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/complexCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		String actualLucidFileName = LucidCSTranslator.driveServiceTranslation(csConfig, logger);
		String expectedLucidFileName = "testinput/translationtests/complexCSTranslation/expectedlucidprogram.ipl";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
		assertFalse(logGenerated);
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
		
		String actualLogFileName = "testinput/translationtests/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);	
		
		boolean inpValid = LucidCSTranslator.validateInpValues(inputDetails, logger);
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		assertTrue(inpValid);
		assertFalse(logGenerated);
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
		
		String actualLogFileName1 = "testinput/translationtests/invalidCharInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/invalidCharInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/invalidCharInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/invalidCharInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		boolean inpValid1 = LucidCSTranslator.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = LucidCSTranslator.validateInpValues(inputDetails2, logger2);
		
		String actualLog1 = ReadWriteUtil.readTextFile(actualLogFileName1);
		String expectedLog1 = ReadWriteUtil.readTextFile(expectedLogFileName1);
		String actualLog2 = ReadWriteUtil.readTextFile(actualLogFileName2);
		String expectedLog2 = ReadWriteUtil.readTextFile(expectedLogFileName2);
		
		assertFalse(inpValid1);
		assertFalse(inpValid2);
		assertEquals(expectedLog1, actualLog1);
		assertEquals(expectedLog2, actualLog2);
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
		
		String actualLogFileName1 = "testinput/translationtests/invalidIntInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/invalidIntInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/invalidIntInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/invalidIntInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/invalidIntInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/invalidIntInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		String actualLogFileName4 = "testinput/translationtests/invalidIntInpVal/log4.txt";
		String expectedLogFileName4 = "testinput/translationtests/invalidIntInpVal/expectedlog4.txt";
		LogUtil logger4 = new LogUtil();
		logger4.setLogFileName(actualLogFileName4);
		
		boolean inpValid1 = LucidCSTranslator.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = LucidCSTranslator.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = LucidCSTranslator.validateInpValues(inputDetails3, logger3);
		boolean inpValid4 = LucidCSTranslator.validateInpValues(inputDetails4, logger4);
		
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
		
		String actualLogFileName1 = "testinput/translationtests/invalidFloatInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/invalidFloatInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/invalidFloatInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/invalidFloatInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/invalidFloatInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/invalidFloatInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		boolean inpValid1 = LucidCSTranslator.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = LucidCSTranslator.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = LucidCSTranslator.validateInpValues(inputDetails3, logger3);
		
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
		
		String actualLogFileName1 = "testinput/translationtests/invalidBooleanInpVal/log1.txt";
		String expectedLogFileName1 = "testinput/translationtests/invalidBooleanInpVal/expectedlog1.txt";
		LogUtil logger1 = new LogUtil();
		logger1.setLogFileName(actualLogFileName1);
		
		String actualLogFileName2 = "testinput/translationtests/invalidBooleanInpVal/log2.txt";
		String expectedLogFileName2 = "testinput/translationtests/invalidBooleanInpVal/expectedlog2.txt";
		LogUtil logger2 = new LogUtil();
		logger2.setLogFileName(actualLogFileName2);
		
		String actualLogFileName3 = "testinput/translationtests/invalidBooleanInpVal/log3.txt";
		String expectedLogFileName3 = "testinput/translationtests/invalidBooleanInpVal/expectedlog3.txt";
		LogUtil logger3 = new LogUtil();
		logger3.setLogFileName(actualLogFileName3);
		
		boolean inpValid1 = LucidCSTranslator.validateInpValues(inputDetails1, logger1);
		boolean inpValid2 = LucidCSTranslator.validateInpValues(inputDetails2, logger2);
		boolean inpValid3 = LucidCSTranslator.validateInpValues(inputDetails3, logger3);
		
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
		
		String actualLogFileName = "testinput/translationtests/invalidStringInpVal/log.txt";
		String expectedLogFileName = "testinput/translationtests/invalidStringInpVal/expectedlog.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
		
		boolean inpValid = LucidCSTranslator.validateInpValues(inputDetails, logger);
		
		String actualLog = ReadWriteUtil.readTextFile(actualLogFileName);
		String expectedLog = ReadWriteUtil.readTextFile(expectedLogFileName);
		
		assertFalse(inpValid);
		assertEquals(expectedLog, actualLog);
	}
	
	/**
	 * Method for creating a service repository of serialized Java objects, storing a composite service 
	 * into it and getting the name of the composite service.
	 * This method needs to be executed only once. 
	 * Once the serialized repository is created for a test, this method should not be executed.
	 * @param 	containerFolder		Complete name and path of the folder that will contain all the files related to this operation
	 */
	private void createSerialCSRepo(String containerFolder)
	{
		//Parsing an XML service repository containing no composite services
		String xmlRepoFileName = containerFolder + "XML_Repository.xml";
		ServiceFileParserDecorator xmlSvcParser = new ConstrainedServiceXMLParser(new BasicServiceParser());
		xmlSvcParser.setLocation(xmlRepoFileName);
		ArrayList<Service> services = xmlSvcParser.parse();
		
		//Translating the XML repository into a serialized Java object repository
		String serialRepoFileName = containerFolder + "Serialized_Repository.txt";
		ServiceFileWriterDecorator serialSvcWriter = new ServiceSerializedWriter(new BasicServiceWriter());
		serialSvcWriter.setLocation(serialRepoFileName);
		serialSvcWriter.write(services);
				
		//Creating a composite service based on the serialized repository and storing it back in the same repository
		FileReqConfigReader configReader = new XMLFileReqConfigReader();
		configReader.setConfigFileName(containerFolder + "Request_Configuration.xml");
		RequestConfiguration reqConfig = configReader.readReqConfig();
		
		String actualLogFileName = containerFolder + "log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
		
		ServiceComposition.driveServiceComposition(reqConfig, logger);
		
		//Fetching the name of the composite service from the serialized repository
		ServiceFileParserDecorator serialSvcParser = new ServiceSerializedParser(new BasicServiceParser());
		serialSvcParser.setLocation(serialRepoFileName);
		services = serialSvcParser.parse();
		
		String compSvcName = null;
		for (Service service : services)
		{
			if (service.getName().startsWith("CompSvc_"))
			{
				compSvcName = service.getName();
			}
		}
		System.out.println(compSvcName);
	}
}