package translation.readers;

import java.util.List;

/**
 * Class for storing composite service input values received from the user.
 * @author Jyotsana Gupta
 */
public class CSInputConfiguration 
{
	private List<String[]> inputDetails;
	
	/**
	 * Parameterized constructor.
	 * @param 	inputDetails	List of records with details of each input of a composite service.
	 * 							Each record consists of the input name, type and value.
	 */
	public CSInputConfiguration(List<String[]> inputDetails)
	{
		this.inputDetails = inputDetails;
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