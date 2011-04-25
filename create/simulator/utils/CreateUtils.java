package create.simulator.utils;

import create.simulator.icons.*;
import java.io.*;
import javax.swing.*;

public class CreateUtils
{
	/**
	 * Loads an entire text file and returns it as a String.
	 * @param folder The File representing the folder that contains the file to read from.
	 * @param filename The name of the file to read from.
	 * @return The text contents of the file.
	 * @throws IOException
	 */
	public static String loadEntireFile(File folder, String filename) throws IOException
	{
		return loadEntireFile(new File(folder, filename));
	}
	
	/**
	 * Loads an entire text file and returns it as a String.
	 * @param filename The name (with or without folder prefixed to it) of the file to read from.
	 * @return The text contents of the file.
	 * @throws IOException
	 */
	public static String loadEntireFile(String filename) throws IOException
	{
		return loadEntireFile(new File(filename));
	}
	
	/**
	 * Loads an entire text file and returns it as a String.
	 * @param file The File to read from.
	 * @return The text contents of the file.
	 * @throws IOException
	 */
	public static String loadEntireFile(File file) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		
		String buffer = in.readLine();
		String line = in.readLine();
		while (line != null)
		{
			buffer += line;
			line = in.readLine();
		}
		
		return buffer;
	}
	
	public static Icon loadIcon(String name)
	{
		return IconLoader.getIcon(name);
	}
}
