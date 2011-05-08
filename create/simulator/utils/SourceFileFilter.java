package create.simulator.utils;

import java.io.File;
import java.io.FilenameFilter;

import javax.swing.filechooser.FileFilter;

public class SourceFileFilter extends FileFilter implements FilenameFilter
{
	private static SourceFileFilter singleton = null;
	
	public static SourceFileFilter getSingleton()
	{
		if (singleton == null)
			singleton = new SourceFileFilter();
		
		return singleton;
	}
	
	public boolean accept(File f)
	{
		if (!f.isFile())
			return false;
		String name = f.getName();
		int index = name.lastIndexOf('.');
		if (index < 0)
			return false;
		return name.substring(index).equalsIgnoreCase(".cc");
	}
	
	public String getDescription()
	{
		return "Source files";
	}
	
	public boolean accept(File dir, String name)
	{
		return accept(new File(dir, name));
	}
	
}
