package entities;

/**
 * Enumeration listing the Quality of Service features that can be expected from a composite service.
 * QoS features of all composite services and composition requests are validated against this common list.
 * To alter the list of allowed features, a manual (code) change in this enum will be required.
 * @author Jyotsana Gupta
 */
public enum QualityOfService 
{
	COST("cost"),
	RESPONSE_TIME("response time"),
	RELIABILITY("reliability"),
	AVAILABILITY("availability");
	
	private final String value;
	
	/**
	 * Private constructor to prevent new values from being added from outside this enum.
	 * @param	value	Actual QoS feature name
	 */
	private QualityOfService(String value)
	{
		this.value = value;
	}
	
	/**
	 * Method for fetching the actual QoS feature name.
	 * @return	Actual QoS feature name
	 */
	public String getValue()
	{
		return this.value;
	}
}