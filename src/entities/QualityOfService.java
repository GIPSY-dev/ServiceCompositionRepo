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
	 * Constructor with all data member values accepted as arguments.
	 * @param	featureType		Ontology type of the quality of service feature
	 * @param	literalValue	Value assigned to the quality of service feature
	 */
	public QualityOfService(String featureType, Object literalValue)
	{
		this.featureType = featureType;
		this.literalValue = literalValue;
	}
	
	/**
	 * Accessor method for the Ontology type of the quality of service feature.
	 * @return	QOS feature Ontology type
	 */
	public String getFeatureType()
	{
		return featureType;
	}
	
	/**
	 * Accessor method for the value assigned to the quality of service feature.
	 * @return	QOS feature value
	 */
	public Object getLiteralValue()
	{
		return literalValue;
	}
	
	/**
	 * Mutator method for the Ontology type of the quality of service feature.
	 * @param	featureType	Ontology type to be assigned to this QOS feature
	 */
	public void setFeatureType(String featureType)
	{
		this.featureType = featureType;
	}
	
	/**
	 * Mutator method for the value assigned to the quality of service feature.
	 * @param	literalValue	Value to be assigned to this QOS feature
	 */
	public void setLiteralValue(Object literalValue)
	{
		this.literalValue = literalValue;
	}
}