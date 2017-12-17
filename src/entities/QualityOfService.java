package entities;

/**
 * Class representing a quality of service feature-value pair.
 * @author Jyotsana Gupta
 */
public class QualityOfService 
{
	private String featureType;
	private Object literalValue;
	
	/**
	 * Default constructor.
	 */
	public QualityOfService()
	{
		featureType = null;
		literalValue = null;
	}
	
	/**
	 * 
	 * @param featureType
	 * @param literalValue
	 */
	public QualityOfService(String featureType, Object literalValue)
	{
		this.featureType = featureType;
		this.literalValue = literalValue;
	}
	
	public String getFeatureType()
	{
		return featureType;
	}
	
	public Object getLiteralValue()
	{
		return literalValue;
	}
	
	public void setFeatureType(String featureType)
	{
		this.featureType = featureType;
	}
	
	public void setLiteralValue(Object literalValue)
	{
		this.literalValue = literalValue;
	}
}