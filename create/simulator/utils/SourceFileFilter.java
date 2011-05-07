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
		// TODO Auto-generated method stub
		return false;
	}
	
	public String getDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	public boolean accept(File dir, String name)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}
