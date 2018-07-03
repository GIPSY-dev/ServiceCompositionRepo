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
	
	/**
	 * Parameterized constructor.
	 * @param	compositeService	Composite service object
	 * @param 	inputDetails		List of records with details of each input of the given composite service.
	 * 								Each record consists of the input name, type and value.
	 */
	public CSConfiguration(Service compositeService, List<String[]> inputDetails)
	{
		this.compositeService = compositeService;
		this.inputDetails = inputDetails;
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
}