package create.simulator.window;

import java.io.*;
import java.util.*;

import create.simulator.utils.*;

import org.fife.ui.rsyntaxtextarea.*;

/**
 * Contains all the information about a project required to build it, as well as starting a simulator/etc.
 * @author dvanhumb
 *
 */
public class CreateProject
{
	protected String projectName;
	protected File projectFolder;
	
	protected File sourceFolder;
	protected File localBinFolder;
	protected File embeddedBinFolder;
	
	/**
	 * These represent all the modules that make up this CreateProject.
	 */
	protected Vector<String> moduleNames;
	
	protected CreateProject(File projectFolder)
	{
		projectName = projectFolder.getName();
		this.projectFolder = projectFolder;
		
		sourceFolder = new File(projectFolder, "src");
		localBinFolder = new File(projectFolder, "lbin");
		embeddedBinFolder = new File(projectFolder, "ebin");
		
		sourceFolder.mkdirs();
		localBinFolder.mkdirs();
		embeddedBinFolder.mkdirs();
		
		// Load information about each module
		moduleNames = new Vector<String>();
		for (String module : sourceFolder.list(SourceFileFilter.getSingleton()))
			moduleNames.add(module);
	}
	
	/**
	 * Compiles the current code for execution on this computer.
	 */
	public void buildSimulatorProject()
	{
		// TODO: Build the project for local simulation
	}
	
	/**
	 * Compiles the current code for execution on the Command Module.
	 */
	public void buildEmbeddedProject()
	{
		// TODO: Build the project for embedded operation.
	}
	
	/**
	 * Returns a properly-loaded CreateProject, if it exists.
	 * @param name The name of the project to load.
	 * @return The newly-loaded CreateProject.
	 */
	public static CreateProject loadProject(String name)
	{
		File projectFolder = new File(MainLauncher.getSketchbookFolder(), name);
		if (projectFolder.exists() && projectFolder.isDirectory())
		{
			return new CreateProject(projectFolder);
		}
		return null;
	}
	
	/**
	 * Creates a new project with the given name.
	 * @param name The name to give the new project.
	 * @return A copy of the newly-created project.
	 */
	public static CreateProject newProject(String name)
	{
		File projectFolder = new File(MainLauncher.getSketchbookFolder(), name);
		
		if (projectFolder.exists())
		{
			// This folder (and therefore project) already exists in some manner, so just load it.
			return loadProject(name);
		}
		else
		{
			// Create all the necessary folders
			projectFolder.mkdirs();
			
			// Create the source folder
			File srcFolder = new File(projectFolder, "src");
			srcFolder.mkdir();
			
			// Add a bare minimum source file to the source folder
			// TODO: Add a template source file
			
			// Return the new project
			return new CreateProject(projectFolder);
		}
	} // end newProject(String name)
	
	public String getProjectName()
	{
		return projectName;
	}
	
	/**
	 * Returns a list of all the modules included in this project.
	 * @return
	 */
	public String[] getModuleNames()
	{
		return sourceFolder.list(SourceFileFilter.getSingleton());
	}
	
	/**
	 * Loads a module as a TextEditorPane.
	 * @param moduleName
	 * @return
	 */
	public TextEditorPane loadModule(String moduleName)
	{
		if (moduleNames.contains(moduleName))
		{
			try
			{
				TextEditorPane editor = new TextEditorPane(TextEditorPane.INSERT_MODE, false, FileLocation.create(new File(sourceFolder, moduleName)));
				editor.setHighlightCurrentLine(true);
				editor.setBracketMatchingEnabled(true);
				editor.setTextAntiAliasHint("VALUE_TEXT_ANTIALIAS_ON");
				editor.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
				return editor;
			}
			catch (IOException er)
			{
				return null;
			}
		}
		return null;
	}
}
