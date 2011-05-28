package create.simulator.utils;

import create.simulator.window.*;

import java.io.*;

public class BuildProblem extends Throwable
{
	private static final long serialVersionUID = 2904798745L;
	
	protected CreateProject project;
	protected String errorData, commentData;
	protected int commandResult;
	
	/**
	 * Create a new BuildProblem with the CreateProject that the problem happened on, a String comment, a String error
	 *   message, and the return code of the program the error happened with.
	 * @param project The CreateProject where this BuildProblem occurred.
	 * @param comment A comment about the error.
	 * @param error The actual error message.
	 * @param commandResult The error code the program returned.
	 */
	public BuildProblem(CreateProject project, String comment, String error, int commandResult)
	{
		this.project = project;
		this.commandResult = commandResult;
		
		commentData = comment;
		errorData = error;
	}
	
	/**
	 * Create a new BuildProblem directly from the output of the program that caused or encountered the error.
	 * @param project The Create Project where this BuildProblem occurred.
	 * @param stdIn The StandardIn stream of the program.
	 * @param errIn The ErrorIn stream of the program.
	 * @param commandResult The error code the program returned.
	 * @throws IOException In case an error occurred reading data from the program, pass it along.
	 */
	public BuildProblem(CreateProject project, BufferedReader stdIn, BufferedReader errIn, int commandResult) throws IOException
	{
		this.project = project;
		this.commandResult = commandResult;
		
		errorData = "";
		String line = errIn.readLine();
		while (line != null)
		{
			errorData += line + "\n";
			line = errIn.readLine();
		}
		
		commentData = "";
		line = stdIn.readLine();
		while (line != null)
		{
			commentData += line + "\n";
			line = stdIn.readLine();
		}
	}
	
	/**
	 * Returns the CreateProject that generated this BuildProblem.
	 * @return
	 */
	public CreateProject getProject()
	{
		return project;
	}
	
	/**
	 * Returns the error code returned by the program that caused this BuildProblem. 
	 * @return
	 */
	public int getReturnValue()
	{
		return commandResult;
	}
	
	/**
	 * Returns the error message(s) wrapped by this BuildProblem.
	 * @return
	 */
	public String getErrorData()
	{
		return errorData;
	}
	
	/**
	 * Returns any comments wrapped by this BuildProgram.
	 * @return
	 */
	public String getCommentData()
	{
		return commentData;
	}
}
