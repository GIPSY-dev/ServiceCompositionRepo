package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.io.File;
import java.util.ArrayList;
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
import translation.translators.CompositeServiceTranslator;
import translation.translators.LucidCSTranslator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for testing the layered composite service to Objective Lucid translator.
 * @author Jyotsana Gupta
 */
public class LucidTranslatorTests 
{
	/**
	 * Tests that a composite service can be searched for by its name in a service repository of serialized 
	 * Java objects, parsed into a composite service object and translated into an Objective Lucid program.
	 */
	@Test
	public void simpleCSTranslation()
	{
		/*
		 * The commented part below needs to be executed only once. Its main purpose is to create a service
		 * repository of serialized Java objects, store a composite service into it and get the name of the
		 * composite service. All this is required for the translation process. 
		 */
		//createSerialCSRepo("testinput/translationtests/lucidtranslatortests/simpleCSTranslation/");
		
		String actualLogFileName = "testinput/translationtests/lucidtranslatortests/simpleCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/lucidtranslatortests/simpleCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new LucidCSTranslator();
		String actualLucidFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedLucidFileName = "testinput/translationtests/lucidtranslatortests/simpleCSTranslation/expectedlucidprogram.ipl";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests correct translation of a complex layered composite service into Objective Lucid.
	 */
	@Test
	public void complexCSTranslation()
	{
		/*
		 * The commented part below needs to be executed only once. Its main purpose is to create a service
		 * repository of serialized Java objects, store a composite service into it and get the name of the
		 * composite service. All this is required for the translation process. 
		 */
		//createSerialCSRepo("testinput/translationtests/lucidtranslatortests/complexCSTranslation/");
		
		String actualLogFileName = "testinput/translationtests/lucidtranslatortests/complexCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/lucidtranslatortests/complexCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new LucidCSTranslator();
		String actualLucidFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedLucidFileName = "testinput/translationtests/lucidtranslatortests/complexCSTranslation/expectedlucidprogram.ipl";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests correct translation of a layered composite service read from an XML file into Objective Lucid.
	 */
	@Test
	public void xmlToLucidTranslation()
	{
		String actualLogFileName = "testinput/translationtests/lucidtranslatortests/xmlToLucidTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);		
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/lucidtranslatortests/xmlToLucidTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new LucidCSTranslator();
		String actualLucidFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedLucidFileName = "testinput/translationtests/lucidtranslatortests/xmlToLucidTranslation/expectedlucidprogram.ipl";
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedProgram = ReadWriteUtil.readTextFile(expectedLucidFileName);
		String actualProgram = ReadWriteUtil.readTextFile(actualLucidFileName);
		assertEquals(expectedProgram, actualProgram);
		assertFalse(logGenerated);
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