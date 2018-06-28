package translation.utilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

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
}