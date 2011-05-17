package create.simulator.utils;

import create.simulator.window.*;

import java.io.*;

public class BuildProblem
{
	protected CreateProject project;
	protected String errorData, commentData;
	protected int commandResult;
	
	public BuildProblem(CreateProject project, String comment, String error, int commandResult)
	{
		this.project = project;
		this.commandResult = commandResult;
		
		commentData = comment;
		errorData = error;
	}
	
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
	
	public CreateProject getProject()
	{
		return project;
	}
	
	public int getReturnValue()
	{
		return commandResult;
	}
	
	public String getErrorData()
	{
		return errorData;
	}
	
	public String getCommentData()
	{
		return commentData;
	}
}
