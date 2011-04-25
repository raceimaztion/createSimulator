package create.simulator;

/**
 * Provides methods for directing an iRobot Create. Usually subclassed.
 * Interfaces between the core (CreateSimulartorCore) and the controlling program.
 * Shows its own 
 * @author dvanhumb
 */
public abstract class RobotController extends Thread 
{
	private volatile boolean running = false;
	private volatile boolean paused = false;
	
	// ----------------------
	// Robot-control methods:
	// ----------------------
	
	/**
	 * Give speeds to the wheels individually. Negative values indicate driving that wheel backwards.
	 * @param left The speed to drive the left wheel. Range: -500 to 500
	 * @param right The speed to drive the right wheel. Range: -500 to 500
	 */
	public void driveDirect(int left, int right)
	{
		// Cap the speeds
		left = Math.max(-500, Math.min(500, left));
		right = Math.max(-500, Math.min(500, right));
	}
	
	/**
	 * Direct the robot to drive in a circle with a given radius and speed.
	 * @param speed The speed to drive. Negative values indicate driving backwards. Range: -500 to 500
	 * @param radius The radius of the circle. Positive values indicate turning right, while negative values indicate turning left. Range: -2000 to 2000, unless you want straight (0x7fff or 0x8000) or turning in-place (0xffff)
	 */
	public void driveRadius(int speed, int radius)
	{
		// Cap the speed and radius
		speed = Math.max(-500, Math.min(500, speed));
		if (radius != 32768 && radius != 32767 && radius != 0xffff)
			radius = Math.max(-2000, Math.min(2000, radius));
	}
	
	/**
	 * Send a byte out the IR transmitter.
	 * @param code The byte to send.
	 */
	public void sendIRcode(short code)
	{
		
	}
	
	/**
	 * Convenience function to stream a list of bytes out the IR transmitter
	 * @param codes The bytes to send
	 */
	public void sendIRbytes(short[] codes)
	{
		for (short code : codes)
			sendIRcode(code);
	}
	
	/**
	 * Recieve the last IR code sent by any robot.
	 * @return
	 */
	public short getIRcode()
	{
		// For now, return 255, the code for "no code recieved"
		return 255;
	}
	
	// For now, I'm ignoring songs
	
	/**
	 * Tell the Create to wait a specific length of time, up to 255 tenths of a second.
	 * @param delay The amount of time to wait in tenths of a second.
	 */
	public void waitTime(short delay)
	{
		
	}
	
	/**
	 * Tell the Create to wait until it's drive a particular distance.
	 * @param distance The distance to wait.
	 */
	public void waitDistance(int distance)
	{
		
	}
	
	/**
	 * Tells the Create to wait until it's turned a particular angle.
	 * @param angle The angle to wait, in degrees.
	 */
	public void waitAngle(int angle)
	{
		
	}
	
	// ---------------------------------------------
	// Methods for the controlling code to override:
	// ---------------------------------------------
	
	/**
	 * Performs any necessary startup actions.
	 */
	public abstract void init();
	
	/**
	 * Controls the robot's motions. Runs one loop through the robot's code.
	 */
	public abstract void loop();
	
	/**
	 * Starts and runs the controller's code.
	 */
	final public void run()
	{
		running = true;
		init();
		while (running)
		{
			while (paused) yield();
			loop();
		}
	}
	
	// -------------------------------------
	// Robot controller controlling methods:
	// -------------------------------------
	
	/**
	 * Start the robot running.
	 */
	public void startRobot()
	{
		start();
	}
	
	/**
	 * Stop the robot.
	 */
	public void stopRobot()
	{
		running = false;
	}
	
	/**
	 * Returns true if the robot is currently running.
	 * @return Whether the robot is currently running or not.
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Stop the robot from running for a bit.
	 */
	public void pauseRobot()
	{
		paused = true;
	}
	
	/**
	 * Unpause the robot.
	 */
	public void unpauseRobot()
	{
		paused = false;
	}
	
	/**
	 * Returns true if the robot is currently paused.
	 * @return Whether the robot is currently paused or not.
	 */
	public boolean isPaused()
	{
		return paused;
	}
} // end class RobotController
