package create.simulator;

import java.io.*;
import javax.comm.*;

/**
 * An interface to a locally-executing controller.
 * @author dvanhumb
 */
public class RobotRepeater
{
	protected Process masterProcess;
	
	// Buffers to communicate with the controlling process:
	protected BufferedReader coreIn;
	protected PrintStream coreOut;
	
	// Buffers to communicate with the virtual and real robots:
	/**
	 * If this is not null, control the simulator's robot.
	 */
	protected SimulatedRobot simulatedRobot;
	/**
	 * If this is not null and simulatedRobot is null, control the real robot.
	 */
	protected SerialPort realRobot;
	protected BufferedInputStream realIn;
	protected OutputStream realOut;
	
	public RobotRepeater(Process master)
	{
		masterProcess = master;
		
		// Connect to the Process's IO ports 
		coreIn = new BufferedReader(new InputStreamReader(master.getInputStream()));
		coreOut = new PrintStream(master.getOutputStream());
	}
	
	public RobotRepeater(Process master, SimulatedRobot robot)
	{
		this(master);
		
		simulatedRobot = robot;
	}
	
	public RobotRepeater(Process master, SerialPort port) throws IOException
	{
		this(master);
		
		realRobot = port;
		realIn = new BufferedInputStream(port.getInputStream());
		realOut = port.getOutputStream();
	}
	
	public void run()
	{
		// Accept a command from the controller
		try
		{
			String line = coreIn.readLine();
			String[] parsed = line.split(" +");
			if (parsed.length == 0)
				return;
			
			if (simulatedRobot != null)
				controlSimulated(parsed);
			else if (realRobot != null)
				controlReal(parsed);
			// else we have nothing to control
		}
		catch (IOException er)
		{
			
		}
	} // end run()
	
	private void controlSimulated(String[] command)
	{
		
	}
	
	private void controlReal(String[] command)
	{
		
	}
}
