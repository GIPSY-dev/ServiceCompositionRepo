package translation.translators;

/**
 * Enumeration listing the colors that can be used for Dot graph generation.
 * @author Jyotsana Gupta
 */
public enum DotGraphColor 
{
	COLOR_1("red"),
	COLOR_2("gold"),
	COLOR_3("green"),
	COLOR_4("blue"),
	COLOR_5("brown"),
	COLOR_6("purple"),
	COLOR_7("orange"),
	COLOR_8("deeppink"),
	COLOR_9("darkgreen"),
	COLOR_10("darkgoldenrod"),
	COLOR_11("darkturquoise"),
	COLOR_12("yellow"),
	COLOR_13("chocolate"),
	COLOR_14("salmon"),
	COLOR_15("yellowgreen"),
	COLOR_16("darkkhaki"),
	COLOR_17("maroon"),
	COLOR_18("cornflowerblue"),
	COLOR_19("rosybrown");
	
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