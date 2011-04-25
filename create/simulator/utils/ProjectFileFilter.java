package create.simulator.utils;

import java.io.*;

import javax.swing.filechooser.FileFilter;

public class ProjectFileFilter extends FileFilter implements FilenameFilter
{
	private static ProjectFileFilter singleton;
	
	public String getDescription()
	{
		return "Create Project folders";
	}
	
	public boolean accept(File f)
	{
		return f.isDirectory() && (new File(f, "src")).exists();
	}
	
	public boolean accept(File dir, String name)
	{
		return accept(new File(dir, name));
	}
	
	public static ProjectFileFilter getFilter()
	{
		if (singleton == null)
			singleton = new ProjectFileFilter();
		
		return singleton;
	}
}
