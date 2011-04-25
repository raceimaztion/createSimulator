package create.simulator.icons;

import javax.swing.*;

public class IconLoader
{
	private static final IconLoader loader = new IconLoader();
	
	public static Icon getIcon(String name)
	{
		return new ImageIcon(loader.getClass().getResource(name));
	}
}
