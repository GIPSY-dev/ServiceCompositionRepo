package translation.readers;

import servicecomposition.entities.CompositionRequest;

/**
 * Interface to be implemented by all concrete composite service input configuration readers.
 * @author Jyotsana Gupta
 */
public interface CSInputConfigReader 
{
	/**
	 * Method for reading composite service input values from the user.
	 * Mode of input (e.g. console, XML file etc.) may vary according to the concrete implementation.
	 * @param	compRequest		Composition request containing the list of inputs to be read 
	 * @return	Input configuration object containing the input values read from the user
	 */
	public CSInputConfiguration readCSInpConfig(CompositionRequest compRequest);
}