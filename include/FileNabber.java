package include;

import java.io.*;

public class FileNabber
{
	private static FileNabber loader = new FileNabber();
	
	public static final String FILE_TEMPLATE = "template.cpp";
	public static final String FILE_MAIN = "main.cpp";
	public static final String FILE_HEADER_OI = "oi.h";
	public static final String FILE_HEADER_CM = "cm.h";
	public static final String FILE_MAKEFILE = "Makefile";
	
	public static InputStream getFile(String name)
	{
		return loader.getClass().getResourceAsStream(name);
	}
}
