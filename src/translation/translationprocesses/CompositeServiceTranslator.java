package translation.translationprocesses;

import translation.readers.csconfigreaders.CSConfiguration;
import utilities.LogUtil;

/**
 * Interface to be implemented by all concrete layered composite service to formal language translators.
 * @author Jyotsana Gupta
 */
public interface CompositeServiceTranslator 
{
	/**
	 * Method for translating a given layered composite service into a formal language program or representation.
	 * The target language (e.g. Lucid, XML etc.) may vary according to the concrete implementation.
	 * @param 	compSvcConfig	Composite service configuration containing information required for the translation
	 * @param 	logger			Logging utility object for logging error or status messages to a text file
	 * @return	Complete name and path of the file containing the target language program or representation
	 */
	public String generateFormalLangCode(CSConfiguration compSvcConfig, LogUtil logger);
}