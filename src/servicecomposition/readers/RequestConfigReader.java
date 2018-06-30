package servicecomposition.readers;

/**
 * Interface to be implemented by all concrete composition request configuration readers.
 * @author Jyotsana Gupta
 */
public interface RequestConfigReader 
{
	/**
	 * Method for reading composition request and service repository details from the user.
	 * Mode of input (e.g. console, XML file etc.) may vary according to the concrete implementation.
	 * @return	Request configuration object containing the input request and repository details
	 */
	public RequestConfiguration readReqConfig();
}