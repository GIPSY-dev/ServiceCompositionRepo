package translation.readers.csconfigreaders;

import java.util.List;
import service.Service;

/**
 * Class for storing details of translation of a composite service to a formal language received from the user.
 * It contains the composite service details and input values, target language and destination folder location.
 * @author Jyotsana Gupta
 */
public class CSConfiguration 
{
	private Service compositeService;
	private List<String[]> inputDetails;
	private String targetLanguage;
	private String destinationFolder;
	
	/**
	 * Parameterized constructor.
	 * @param	compositeService	Composite service object
	 * @param 	inputDetails		List of records with details of each input of the given composite service.
	 * 								Each record consists of the input name, type and value.
	 * @param	targetLanguage		Language to which the composite service needs to be translated
	 * @param	destinationFolder	Complete name and path of the folder where the translation file will be placed
	 */
	public CSConfiguration(Service compositeService, List<String[]> inputDetails, String targetLanguage, String destinationFolder)
	{
		this.compositeService = compositeService;
		this.inputDetails = inputDetails;
		this.targetLanguage = targetLanguage;
		this.destinationFolder = destinationFolder;
	}
	
	/**
	 * Method for fetching the composite service object from this configuration.
	 * @return	Composite service object
	 */
	public Service getCompositeService()
	{
		return compositeService;
	}
	
	/**
	 * Method for fetching the list of input details from this configuration.
	 * @return	Composite service input details
	 */
	public List<String[]> getInputDetails()
	{
		return inputDetails;
	}
	
	/**
	 * Method for fetching the target language name from this configuration.
	 * @return	Target language name
	 */
	public String getTargetLanguage()
	{
		return targetLanguage;
	}
	
	/**
	 * Method for fetching the destination folder name and path from this configuration.
	 * @return	Destination folder name and path
	 */
	public String getDestinationFolder()
	{
		return destinationFolder;
	}
}