package translation.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import org.junit.Test;
import translation.readers.csconfigreaders.CSConfiguration;
import translation.readers.csconfigreaders.FileCSConfigReader;
import translation.readers.csconfigreaders.XMLFileCSConfigReader;
import translation.translators.CompositeServiceTranslator;
import translation.translators.DotGraphCSTranslator;
import utilities.LogUtil;
import utilities.ReadWriteUtil;

/**
 * Class for testing the layered composite service to Dot graph translator.
 * @author Jyotsana Gupta
 */
public class DotTranslatorTests 
{
	/**
	 * Tests that a composite service can be searched for by its name in an XML service repository, 
	 * parsed into a composite service object and translated into Dot and PNG files.
	 * Also tests that input values are not required for translation to Dot.
	 */
	@Test
	public void simpleCSTranslation()
	{		
		String actualLogFileName = "testinput/translationtests/dottranslatortests/simpleCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/dottranslatortests/simpleCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new DotGraphCSTranslator();
		String actualDotFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedDotFileName = "testinput/translationtests/dottranslatortests/simpleCSTranslation/expecteddot.dot";
		
		String actualImageFileName = actualDotFileName.substring(0, actualDotFileName.lastIndexOf(".")) + ".png";
		File actualImageFile = new File(actualImageFileName);
		boolean dotImageGenerated = false;
		if ((actualImageFile.exists()) && (!actualImageFile.isDirectory()))
		{
			dotImageGenerated = true;
		}
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedDotText = ReadWriteUtil.readTextFile(expectedDotFileName);
		String actualDotText = ReadWriteUtil.readTextFile(actualDotFileName);
		assertEquals(expectedDotText, actualDotText);
		assertTrue(dotImageGenerated);
		assertFalse(logGenerated);
	}
	
	/**
	 * Tests correct translation of a complex layered composite service into a Dot graph.
	 */
	@Test
	public void complexCSTranslation()
	{		
		String actualLogFileName = "testinput/translationtests/dottranslatortests/complexCSTranslation/log.txt";
		LogUtil logger = new LogUtil();
		logger.setLogFileName(actualLogFileName);
				
		FileCSConfigReader csConfigReader = new XMLFileCSConfigReader();
		csConfigReader.setConfigFileName("testinput/translationtests/dottranslatortests/complexCSTranslation/CS_Configuration.xml");
		CSConfiguration csConfig = csConfigReader.readCSConfig(logger);
		
		CompositeServiceTranslator csTranslator = new DotGraphCSTranslator();
		String actualDotFileName = csTranslator.generateFormalLangCode(csConfig, logger);
		String expectedDotFileName = "testinput/translationtests/dottranslatortests/complexCSTranslation/expecteddot.dot";
		
		String actualImageFileName = actualDotFileName.substring(0, actualDotFileName.lastIndexOf(".")) + ".png";
		File actualImageFile = new File(actualImageFileName);
		boolean dotImageGenerated = false;
		if ((actualImageFile.exists()) && (!actualImageFile.isDirectory()))
		{
			dotImageGenerated = true;
		}
		
		File actualLogFile = new File(actualLogFileName);
		boolean logGenerated = (!(actualLogFile.length() == 0));
		
		String expectedDotText = ReadWriteUtil.readTextFile(expectedDotFileName);
		String actualDotText = ReadWriteUtil.readTextFile(actualDotFileName);
		assertEquals(expectedDotText, actualDotText);
		assertTrue(dotImageGenerated);
		assertFalse(logGenerated);
	}
}