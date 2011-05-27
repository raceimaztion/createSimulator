package create.simulator.window;

import include.FileNabber;

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
	public BuildProblem buildSimulatorProject()
	{
		// TODO: Build the project for local simulation
		// If we're running Linux, try compiling with GCC:
		if (MainLauncher.getRuntimePlatform() == Platform.LINUX)
		{
			File mainSource = null;
			File header1 = null, header2 = null;
			
			// Try compiling the project under Linux using GCC:
			try
			{
				Runtime runtime = Runtime.getRuntime();
				
				// Copy the source library into the project folder
				String[] modules = getModuleNames();
				
				mainSource = new File(sourceFolder, FileNabber.FILE_MAIN);
				header1 = new File(sourceFolder, FileNabber.FILE_HEADER_CM);
				header2 = new File(sourceFolder, FileNabber.FILE_HEADER_OI);
				
				// Copy the main file,
				PrintStream main = new PrintStream(new FileOutputStream(mainSource));
				main.println("#include \"cm.h\"");
				for (String module : modules)
				{
					main.printf("#include \"%s\"\n", module);
					System.out.println(module);
				}
				CreateUtils.copyFile(FileNabber.FILE_MAIN, main);
				main.flush();
				main.close();
				
				// Copy the headers.
				CreateUtils.copyFile(FileNabber.FILE_HEADER_CM, header1);
				CreateUtils.copyFile(FileNabber.FILE_HEADER_OI, header2);
				
				// Try to compile the files
				String command = String.format("g++ -DMODE_LOCAL -o \"%s%s%s\" %s", localBinFolder.getPath(), File.separator, getProjectName(), FileNabber.FILE_MAIN);
				System.out.println(command);
				
				Process compiler = runtime.exec(command, null, sourceFolder);
				InputStream stdIn = compiler.getInputStream();
				InputStream errIn = compiler.getErrorStream();
				Integer result = null;
				try {
					result = compiler.waitFor();
				} catch (InterruptedException er) { }
				
				if (result == 0)
				{
					// We succeeded
					return null;
				}
				else
				{
					// Something failed
					return new BuildProblem(this, new BufferedReader(new InputStreamReader(stdIn)), new BufferedReader(new InputStreamReader(errIn)), result);
				}
			}
			catch (IOException er)
			{
				return new BuildProblem(this, "File I/O error.", er.getMessage(), -2);
			}
			finally
			{
				// Delete the files we added:
				if (mainSource != null) mainSource.delete();
				if (header1 != null) header1.delete();
				if (header2 != null) header2.delete();
			}
//			return new BuildProblem(this, "Compiler failed.", "Unhandled error ocurred.", -1);
		}
		else
		{
			// Fail, we don't support other OSes just yet
			return new BuildProblem(this, "Operating System not supported.", "Operating System not supported.", -2);
		}
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
