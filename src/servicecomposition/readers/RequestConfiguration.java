package servicecomposition.readers;

/**
 * Class for storing the composition request and source service repository details received from the user.
 * @author Jyotsana Gupta
 */
public class RequestConfiguration 
{
	private String inputs;
	private String outputs;
	private String qos;
	private String constraints;
	private String repoFileName;
	
	/**
	 * Parameterized constructor.
	 * @param 	inputs			Comma-separated list of composition request inputs
	 * @param 	outputs			Comma-separated list of composition request outputs
	 * @param 	qos				Comma-separated list of composition request QoS features
	 * @param 	constraints		Comma-separated list of composition request constraints
	 * @param 	repoFileName	Complete name and path of source service repository
	 */
	public RequestConfiguration(String inputs, String outputs, String qos, String constraints, String repoFileName)
	{
		this.inputs = inputs;
		this.outputs = outputs;
		this.qos = qos;
		this.constraints = constraints;
		this.repoFileName = repoFileName;
	}
	
	/**
	 * Method for fetching the list of composition request inputs from this configuration.
	 * @return	Composition request inputs
	 */
	public String getInputs()
	{
		return inputs;
	}
	
	/**
	 * Method for fetching the list of composition request outputs from this configuration.
	 * @return	Composition request outputs
	 */
	public String getOutputs()
	{
		return outputs;
	}
	
	/**
	 * Method for fetching the list of composition request QoS features from this configuration.
	 * @return	Composition request QoS features
	 */
	public String getQos()
	{
		return qos;
	}
	
	/**
	 * Method for fetching the list of composition request constraints from this configuration.
	 * @return	Composition request constraints
	 */
	public String getConstraints()
	{
		return constraints;
	}
	
	/**
	 * Method for fetching the source service repository name and path from this configuration.
	 * @return	Source service repository name and path
	 */
	public String getRepoFileName()
	{
		return repoFileName;
	}
}