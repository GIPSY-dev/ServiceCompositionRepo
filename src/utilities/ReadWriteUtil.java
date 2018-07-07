package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Utility class for reading from and writing to different sources/destinations.
 * @author Jyotsana Gupta
 */
public class ReadWriteUtil 
{
	/**
	 * Method for reading all the contents of a text file.
	 * @param	fileName	Complete file path, name and extension of the source file
	 * @return	The entire contents of the input file as a String object
	 */
	public static String readTextFile(String fileName)
	{
		FileReader inputStream = null;
		String input = "";
		int nextChar;
		
		try
		{
			inputStream = new FileReader(fileName);
			
			//Reading until the end of the file is reached
			while ((nextChar = inputStream.read()) != -1)
			{
				char character = (char)nextChar;
				input = input + Character.toString(character);
			}
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Exception while opening the file: " + fnfe.getMessage());
		}
		catch(IOException ioe)
		{
			System.out.println("Exception while reading the file: " + ioe.getMessage());
		}
		finally
		{
			if (inputStream != null)
			{
				try
				{
					inputStream.close();
				}
				catch(IOException ioe)
				{
					System.out.println("Exception while closing the file: " + ioe.getMessage());
				}
			}
		}
		
		return input;		
	}
	
	/**
	 * Method for writing/appending text to a text file.
	 * If the file does not already exist, a new file is created. Otherwise, the text is appended to the existing file.
	 * @param	fileName	Complete file path, name and extension of the source file
	 * @param	text		The text to be written to the file
	 */
	public static void writeToTextFile(String fileName, String text)
	{
		PrintWriter outputStream = null;
		
		try
		{
			outputStream = new PrintWriter(new FileOutputStream(fileName, true));
			outputStream.print(text);
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("Exception while opening the file: " + fnfe.getMessage());
		}
		finally
		{
			if (outputStream != null)
			{
				outputStream.close();
			}
		}
	}
	
	/**
	 * Method for creating an XML document for an XML file.
	 * @param	xmlFileName		Complete name and location of the XML file
	 * @return	XML document for the XML file
	 */
	public static Document getXmlDocument(String xmlFileName)
	{
		File xmlFile = new File(xmlFileName);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		try 
		{
			dBuilder = dbFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException pce) 
		{
			System.out.println("Exception while parsing XML file: " + pce.getMessage());
		}
		
		Document doc = null;
		try 
		{
			doc = dBuilder.parse(xmlFile);
		}
		catch (SAXException se) 
		{
			System.out.println("Exception while parsing XML file: " + se.getMessage());
		} 
		catch (IOException ioe) 
		{
			System.out.println("Exception while parsing XML file: " + ioe.getMessage());
		}
		
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	/**
	 * Method for fetching the value of the attribute of an XML tag.
	 * @param 	tagName			Name of the tag element
	 * @param	attributeName	Name of the target attribute
	 * @param 	doc				Container XML document. This should be null if sub-element of another element needs to be parsed.
	 * @param 	elem			Container XML element. This should be null if sub-element of a document needs to be parsed.
	 * @return	Value of the target attribute
	 */
	public static String getXMLTagValue(String tagName, String attributeName, Document doc, Element elem)
	{
		String tagValue = null;
		
		Node targetNode = null;
		if (doc != null)
		{
			targetNode = doc.getElementsByTagName(tagName).item(0);
		}
		else if (elem != null)
		{
			targetNode = elem.getElementsByTagName(tagName).item(0);
		}
		
		if (targetNode.getNodeType() == Node.ELEMENT_NODE)
		{
			Element targetElement = (Element) targetNode;
			tagValue = targetElement.getAttribute(attributeName);
		}
		
		return tagValue;
	}
}