package translation.readers;

import java.util.List;
import service.Service;

/**
 * Class for storing composite service input values received from the user.
 * @author Jyotsana Gupta
 */
public class CSConfiguration 
{
	private Service compositeService;
	private List<String[]> inputDetails;
	private String destinationFolder;
	
	/**
	 * Parameterized constructor.
	 * @param	compositeService	Composite service object
	 * @param 	inputDetails		List of records with details of each input of the given composite service.
	 * 								Each record consists of the input name, type and value.
	 * @param	destinationFolder	Complete name and path of the folder where the Lucid translation file will be placed
	 */
	public CSConfiguration(Service compositeService, List<String[]> inputDetails, String destinationFolder)
	{
		this.compositeService = compositeService;
		this.inputDetails = inputDetails;
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
	 * Method for fetching the destination folder name and path from this configuration.
	 * @return	Destination folder name and path
	 */
	public String getDestinationFolder()
	{
		return destinationFolder;
	}
}