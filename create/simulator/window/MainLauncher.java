package create.simulator.window;

import create.simulator.utils.*;
import java.io.*;
import javax.swing.Action;

import org.fife.ui.rtextarea.RTextArea;

public class MainLauncher
{
	protected static File SKETCHBOOK_FOLDER;
	protected static String LAUNCHER_SCRIPT;
	protected static Platform RUNTIME_PLATFORM;
	
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.err.println("Warning: No command-line arguments given, large chance of not finding the Sketchbook Folder!");
		}
		
		// Check what our runtime platform is:
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows"))
			RUNTIME_PLATFORM = Platform.WINDOWS;
		else if (osName.contains("mac os"))
			RUNTIME_PLATFORM = Platform.MAC_OS;
		else if (osName.contains("linux"))
			RUNTIME_PLATFORM = Platform.LINUX;
		else
			RUNTIME_PLATFORM = Platform.OTHER;
		
		for (String arg : args)
		{
			// The "sketchbook" folder that stores all the projects.
			if (arg.startsWith("-s="))
				SKETCHBOOK_FOLDER = new File(arg.substring(3));
			// The name of the script that launched us.
			if (arg.startsWith("-l="))
				LAUNCHER_SCRIPT = arg.substring(3);
		}
		
		// If we still don't have a folder name for the sketchbook, make one up:
		// NOTE: Very platform dependent right now
		if (SKETCHBOOK_FOLDER == null)
		{
			if (RUNTIME_PLATFORM == Platform.WINDOWS)
			{
				// TODO: Put the code to determine the best place the Sketchbook folder should go in Windows
			}
			else if (RUNTIME_PLATFORM == Platform.MAC_OS)
			{
				// TODO: Put the code to determine the best place the Sketchbook folder should go on a Mac
			}
			else if (RUNTIME_PLATFORM == Platform.LINUX)
			{
				// TODO: Put the code to determine the best place the Sketchbook folder should go under Linux
				SKETCHBOOK_FOLDER = new File(System.getProperty("user.home")+"/.config/createSimulator/createSketches/", "createSketches");
			}
			else // if (RUNTIME_PLATFORM == Platform.OTHER)
			{
				// This is the (relatively safe) fallback folder:
				SKETCHBOOK_FOLDER = new File(System.getProperty("user.home"), "createSketches");
			}
			
			// Make sure the folder(s) actually exist:
			SKETCHBOOK_FOLDER.mkdirs();
			
			// Save this as the new default
			if (LAUNCHER_SCRIPT != null)
			{
				try
				{
					// Read in the old launcher script
					String script = CreateUtils.loadEntireFile(LAUNCHER_SCRIPT);
					
					// Update the Sketchbook folder defaults
					script = script.replaceAll("^((set|export) SKETCHBOOK[= ]).*$", "\1 "+SKETCHBOOK_FOLDER);
					
					// Write out the new launcher script 
					FileWriter out = new FileWriter(LAUNCHER_SCRIPT);
					out.write(script);
					out.flush();
					out.close();
				}
				catch (IOException er)
				{
					er.printStackTrace();
				}
			}
		} // end if (we don't have a sketchbook folder)
		
		if (!SKETCHBOOK_FOLDER.exists())
			SKETCHBOOK_FOLDER.mkdirs();
		
		// Debugging:
		System.out.printf("Sketchbook folder: %s\n", SKETCHBOOK_FOLDER);
		
		// Set up the RTextArea Action parameters:
		{
			@SuppressWarnings("unused")
			RTextArea textArea = new RTextArea();
			
			Action cut = RTextArea.getAction(RTextArea.CUT_ACTION);
			cut.putValue(Action.NAME, "Cut");
			cut.putValue(Action.SHORT_DESCRIPTION, null);
			Action copy = RTextArea.getAction(RTextArea.COPY_ACTION);
			copy.putValue(Action.NAME, "Copy");
			copy.putValue(Action.SHORT_DESCRIPTION, null);
			Action paste = RTextArea.getAction(RTextArea.PASTE_ACTION);
			paste.putValue(Action.NAME, "Paste");
			paste.putValue(Action.SHORT_DESCRIPTION, null);
		}
		
		// Launch the EditorWindow:
		EditorWindow window = new EditorWindow();
		window.show();
	}
	
	/**
	 * Returns a reference to the folder that stores all the projects. 
	 * @return
	 */
	public static File getSketchbookFolder()
	{
		return SKETCHBOOK_FOLDER;
	}
	
	/**
	 * Returns a list of the names of all the currently-existing projects.
	 * @return
	 */
	public static String[] getSketchNames()
	{
		FilenameFilter filter = ProjectFileFilter.getFilter();
		
		if (filter == null || SKETCHBOOK_FOLDER == null)
		{
			System.err.println("MainLauncher.getSketchNames(): Fatal error, a null pointer.");
			System.exit(0);
		}
		
		assert(filter != null);
		assert(SKETCHBOOK_FOLDER != null);
		
		return SKETCHBOOK_FOLDER.list(filter);
	}
}