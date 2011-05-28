package create.simulator.utils;

import create.simulator.icons.*;
import include.FileNabber;

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
	
	/**
	 * Copies a file from the given file name to an open PrintStream.
	 * @param source The name of the source file to read from.
	 * @param dest The stream to write the file to.
	 * @throws IOException If there's an error, pass it along.
	 */
	public static void copyFile(String source, PrintStream dest) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(FileNabber.getFile(source)));
		
		String line;
		while ((line = in.readLine()) != null)
		{
			dest.println(line);
		}
		
		in.close();
	}
	
	/**
	 * Copies a file from the given file name to the given File.
	 * @param source The name of the source file to read from.
	 * @param dest The File to write the data to.
	 * @throws IOException If there's an error, pass it along.
	 */
	public static void copyFile(String source, File dest) throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(FileNabber.getFile(source)));
		PrintWriter out = new PrintWriter(new FileWriter(dest));
		
		String line;
		while ((line = in.readLine()) != null)
		{
			out.println(line);
		}
		
		in.close();
		out.close();
	} // end copyFile(String source, File dest)
	
	public static void copyFile(File source, File dest) throws IOException
	{
		FileInputStream in = new FileInputStream(source);
		FileOutputStream out = new FileOutputStream(dest);
		
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) != -1)
		{
			out.write(buffer, 0, length);
		}
		out.flush();
		out.close();
		in.close();
	} // end copyFile(File source, File dest)
}
