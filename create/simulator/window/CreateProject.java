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
	 * @return If there's a problem, returns a BuildProblem object.
	 */
	public BuildProblem buildSimulatorProject()
	{
		BuildProblem problem = null;
		
		// Compile for local execution:
		problem = compileLocalProject();
		if (problem != null)
			return problem;
		
		return null;
	} // end buildSimulatorProject()
	
	public BuildProblem buildSerialProject()
	{
		BuildProblem problem = null;
		
		// Compile for local execution:
		problem = compileLocalProject();
		if (problem != null)
			return problem;
		
		return null;
	} // end buildSerialProject()
	
	/**
	 * Compiles this CreateProject for local execution.
	 * @return If there's a problem, returns a BuildProblem object.
	 */
	private BuildProblem compileLocalProject()
	{
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
					// This prevents infinite loops:
					if (!module.equals(FileNabber.FILE_MAIN))
						main.printf("#include \"%s\"\n", module);
				}
				CreateUtils.copyFile(FileNabber.FILE_MAIN, main);
				main.flush();
				main.close();
				
				// Copy the headers.
				CreateUtils.copyFile(FileNabber.FILE_HEADER_CM, header1);
				CreateUtils.copyFile(FileNabber.FILE_HEADER_OI, header2);
				
				// Try to compile the files
				String command = String.format("g++ -DMODE_LOCAL -o ..%s%s%s%s %s", File.separator, localBinFolder.getName(), File.separator, getProjectName(), FileNabber.FILE_MAIN);
				
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
	} // end compileLocalProblem()
	
	/**
	 * Compiles the current code for execution on the Command Module.
	 * @return If there's a problem, returns a BuildProblem object.
	 */
	public BuildProblem buildEmbeddedProject()
	{
		BuildProblem problem;
		
		problem = compileEmbeddedProject();
		if (problem != null)
			return problem;
		
		return null;
	} // end buildEmbeddedProject()
	
	private BuildProblem compileEmbeddedProject()
	{
		// If we're running Linux, try compiling with GCC:
		if (MainLauncher.getRuntimePlatform() == Platform.LINUX)
		{
			File mainSource = null;
			File header1 = null, header2 = null;
			
			// Try compiling the project under Linux using AVR-GCC:
			try
			{
				// Copy the source library into the project folder
				String[] modules = getModuleNames();
				
				mainSource = new File(sourceFolder, FileNabber.FILE_MAIN);
				header1 = new File(sourceFolder, FileNabber.FILE_HEADER_CM);
				header2 = new File(sourceFolder, FileNabber.FILE_HEADER_OI);
				
				// Copy the main file,
				PrintStream main = new PrintStream(new FileOutputStream(mainSource));
				main.print("#include \"cm.h\"\n");
				for (String module : modules)
				{
					// This prevents infinite loops:
					if (!module.equals(FileNabber.FILE_MAIN))
						main.printf("#include \"%s\"\n", module);
				}
				CreateUtils.copyFile(FileNabber.FILE_MAIN, main);
				main.flush();
				main.close();
				
				// Copy the headers.
				CreateUtils.copyFile(FileNabber.FILE_HEADER_CM, header1);
				CreateUtils.copyFile(FileNabber.FILE_HEADER_OI, header2);
				
				/*
				 * Compile: Done
				 * avr-g++ -c -mmcu=atmega168 -I. -gdwarf-2 -DF_CPU=18432000UL  -Os -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums -Wall -Wstrict-prototypes -Wa,-adhlns={$project}.lst  -std=gnu99 -MD -MP -MF .dep/{$project}.o.d {$source} -o {$project}.o
				 * 
				 * Link:
				 * avr-g++ -mmcu=atmega168 -I. -gdwarf-2 -DF_CPU=18432000UL  -Os -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums -Wall -Wstrict-prototypes -Wa,-adhlns={$project}.o  -std=gnu99 -MD -MP -MF .dep/{$project}.elf.d {$project}.o --output {$project}.elf -Wl,-Map={$project}.map,--cref -lm
				 * 
				 * Creating load file for Flash:
				 * avr-objcopy -O ihex -R .eeprom {$project}.elf {$project}.hex
				 * 
				 * Creating load file for EEPROM:
				 * avr-objcopy -j .eeprom --set-section-flags=.eeprom="alloc,load" --change-section-lma .eeprom=0 -O ihex {$project}.elf {$project}.eep
				 * 
				 * Creating extended listing:
				 * avr-objdump -h -S {$project}.elf > {$project}.lss
				 * 
				 * Creating symbol table:
				 * avr-nm -n {$project}.elf > {$project}.sym
				 */
				
				// Compile the files
				String command;
				BuildProblem problem = null;
				
//				command = String.format("avr-g++ -DMODE_EMBEDDED -c -mmcu=atmega168 -I. -gdwarf-2 -DF_CPU=18432000UL -Os -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums -Wall -Wa,-adhlns=%s.lst -MD -MP -MF .dep/%s.o.d %s -o %s.o", projectName, projectName, FileNabber.FILE_MAIN, projectName);
				command = String.format("avr-g++ -DMODE_EMBEDDED -c -mmcu=atmega168 -I. -gdwarf-2 -DF_CPU=18432000UL -Os -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums -Wall -Wa,-adhlns=../ebin/%s.lst -MD -MP %s -o ../ebin/%s.o", projectName, FileNabber.FILE_MAIN, projectName);
				System.out.println(command);
				problem = runProgram(command, sourceFolder);
				if (problem != null)
					return problem;
				
				// Link the files:
				command = String.format("avr-g++ -mmcu=atmega168 -I. -gdwarf-2 -DF_CPU=18432000UL  -Os -funsigned-char -funsigned-bitfields -fpack-struct -fshort-enums -Wall -Wstrict-prototypes -Wa,-adhlns=%s.o  -std=gnu99 -MD -MP -MF .dep/%s.elf.d %s.o --output %s.elf -Wl,-Map=%s.map,--cref -lm", projectName, projectName, projectName, projectName, projectName);
				System.out.println(command);
				problem = runProgram(command, embeddedBinFolder);
				if (problem != null)
					return problem;
				
				// Create a load file for Flash ROMs:
				command = String.format("avr-objcopy -O ihex -R .eeprom %s.elf %s.hex", projectName, projectName);
				System.out.println(command);
				problem = runProgram(command, embeddedBinFolder);
				if (problem != null)
					return problem;
				
				// Create a load file for EEPROMs:
				command = String.format("avr-objcopy -j .eeprom --set-section-flags=.eeprom=alloc,load --change-section-lma .eeprom=0 -O ihex %s.elf %s.eep", projectName, projectName);
				System.out.println(command);
				problem = runProgram(command, embeddedBinFolder);
				if (problem != null)
					return problem;
				
				// Create an extended listing:
				command = String.format("avr-objdump -h -S %s.elf", projectName);
				System.out.println(command);
				problem = runProgramDirected(command, embeddedBinFolder, new File(embeddedBinFolder, projectName+".lss"));
				if (problem != null)
					return problem;
				
				// Create the symbol table:
				command = String.format("avr-nm -n %s.elf", projectName);
				System.out.println(command);
				problem = runProgramDirected(command, embeddedBinFolder, new File(embeddedBinFolder, projectName+".sym"));
				if (problem != null)
					return problem;
				
				// If we got here, we succeeded
				return null;
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
//			return new BuildProblem(this, "Compiler failed.", "Unhandled error occurred.", -1);
		}
		else
		{
			// Fail, we don't support other OSes just yet
			return new BuildProblem(this, "Operating System not supported.", "Operating System not supported.", -2);
		}
	} // end compileEmbeddedProject()
	
	private BuildProblem runProgram(String command, File folder) throws IOException
	{
		Process compiler = Runtime.getRuntime().exec(command, null, folder);
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
	} // end runProgram(command, folder)
	
	private BuildProblem runProgramDirected(String command, File folder, File output) throws IOException
	{
		Process compiler = Runtime.getRuntime().exec(new String[]{"/bin/sh", "-c", command}, null, folder);
		
		InputStream stdIn = compiler.getInputStream();
		InputStream errIn = compiler.getErrorStream();
		Integer result = null;
		try {
			result = compiler.waitFor();
		} catch (InterruptedException er) {}
		
		// Pipe the stdIn into the output given
		FileOutputStream out = new FileOutputStream(output);
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = stdIn.read(buffer)) != -1)
		{
			out.write(buffer, 0, bytesRead);
		}
		out.flush();
		out.close();
		
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
	} // end runProgram(command, folder, output)
	
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
			try
			{
				CreateUtils.copyFile(FileNabber.FILE_TEMPLATE, new File(srcFolder, "Main.cpp"));
			}
			catch (IOException er)
			{
				
			}
			
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
	} // end loadModule(String)
}
