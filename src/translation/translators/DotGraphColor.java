package translation.translators;

/**
 * Enumeration listing the colors that can be used for dot graph generation.
 * @author Jyotsana Gupta
 */
public enum DotGraphColor 
{
	COLOR_1("red"),
	COLOR_2("gold"),
	COLOR_3("blue"),
	COLOR_4("green"),
	COLOR_5("brown"),
	COLOR_6("purple"),
	COLOR_7("orange"),
	COLOR_8("deeppink"),
	COLOR_9("salmon"),
	COLOR_10("darkgreen"),
	COLOR_11("darkgoldenrod");
	
	private final String value;
	
	/**
	 * Private constructor to prevent new values from being added from outside this enum.
	 * @param	value	Actual color name
	 */
	private DotGraphColor(String value)
	{
		this.value = value;
	}
	
	/**
	 * Method for fetching the actual color name.
	 * @return	Actual color name
	 */
	public String getValue()
	{
		return this.value;
	}
}