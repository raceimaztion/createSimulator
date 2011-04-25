package create.simulator;

/**
 * Some constants used in iRobot's Open Interface, as used by the Create and Roomba robots.
 * @author dvanhumb
 */
public interface OI_Constants
{
	// ---------
	// Commands:
	// ---------
	
	/**
	 * The first command sent to the robot. Initializes the interface.
	 */
	public static final short COMMAND_START = 128;
	/**
	 * Sets the baud rate for communication.
	 * Has one parameter: Baud code (byte)
	 * 	0(300bps)
	 * 	1(600bps)
	 * 	2(1200bps)
	 * 	3(2400bps)
	 * 	4(4800bps)
	 * 	5(9600bps)
	 * 	6(14400bps)
	 * 	7(19200bps)
	 * 	8(28800bps)
	 * 	9(38400bps)
	 * 	10(57600bps)
	 * 	11(115200bps)
	 */
	public static final short COMMAND_BAUD = 129;
	/**
	 * Sets the robot into Safe mode. Gives almost full control, though stops the robot if a danger condition is found.
	 */
	public static final short COMMAND_SAFE_MODE = 131;
	/**
	 * Sets the robot into Full mode. The program is responsible for keeping the robot safe.
	 */
	public static final short COMMAND_FULL_MODE = 132;
	/**
	 * Sets the robot into Passive mode. The program can only ask for and recieve sensor data in this mode.
	 */
	public static final short COMMAND_PASSIVE = 128;
	/**
	 * Starts a specified demo.
	 * Has one parameter: Demo code (byte)
	 */
	public static final short COMMAND_DEMO = 136;
	/**
	 * Starts the Cover demo directly.
	 */
	public static final short COMMAND_DEMO_COVER = 135;
	/**
	 * Starts the Cover and Dock demo directly.
	 */
	public static final short COMMAND_DEMO_COVER_AND_DOCK = 143;
	/**
	 * Starts the Spot demo directly.
	 */
	public static final short COMMAND_DEMO_SPOT = 134;
	/**
	 * Driving directions with speed and curve radius.
	 * Has two parameters: Speed (2 bytes) and Radius (2 bytes)
	 */
	public static final short COMMAND_DRIVE = 137;
	/**
	 * Driving directions with left- and right-wheel speeds.
	 * Has two parameters: Right wheel speed (2 bytes) and Left wheel speed (2 bytes)
	 */
	public static final short COMMAND_DRIVE_DIRECT = 145;
	/**
	 * Controls the LEDs on the robot.
	 * Has three parameters: Advance/Play (byte), Power colour (byte), Power brightness (byte)
	 *    Advance/Play: If bit 3 (mask of 8) is on, the Advance light is on, while if bit 1 is on (mask of 2), the Play light is on.
	 *    Power colour: Ranges from 0 (green) to 255 (red)
	 *    Power brightness: Ranges from 0 (off) to 255 (fully on) 
	 */
	public static final short COMMAND_LEDS = 139;
	/**
	 * Unsupported.
	 */
	public static final short COMMAND_DIGITAL_OUTPUTS = 147;
	/**
	 * Unsupported.
	 */
	public static final short COMMAND_PWM_LOW_SIDE_DRIVERS = 144;
	/**
	 * Unsupported.
	 */
	public static final short COMMAND_LOW_SIDE_DRIVERS = 138;
	/**
	 * Sends a single byte out the IR transmitter.
	 * Has one parameter: Data (byte)
	 */
	public static final short COMMAND_SEND_IR = 151;
	/**
	 * Sound is unsupported in the simulator.
	 */
	public static final short COMMAND_SONG = 140;
	/**
	 * Sound is unsupported in the simulator.
	 */
	public static final short COMMAND_PLAY_SONG = 141;
	/**
	 * Requests information from sensors.
	 */
	public static final short COMMAND_SENSORS = 142;
	
	// ----------------
	// Parameter codes:
	// ----------------
	/**
	 * Aborts the current demo.
	 */
	public static final short DEMO_ABORT = 255;
	/**
	 * The basic "room clean" program.
	 */
	public static final short DEMO_COVER = 0;
	/**
	 * The improved "room clean" program, with automatic docking when the battery runs low.
	 */
	public static final short DEMO_COVER_AND_DOCK = 1;
	/**
	 * Tries to clean a spot by spiraling out and back in.
	 */
	public static final short DEMO_SPOT_COVER = 2;
	/**
	 * Looks for a wall and then follows it.
	 */
	public static final short DEMO_MOUSE = 3;
	/**
	 * Continuously drives in a figure-eight pattern.
	 */
	public static final short DEMO_FIGURE_EIGHT = 4;
	/**
	 * Drives forwards if pushed from behind, and drives away from anything it may bump into.
	 */
	public static final short DEMO_WIMP = 5;
	/**
	 * Requires black electrical tape on the back and sides of the IR sensor.
	 * Homes in on a Virtual Wall until the robot bumps into it.
	 */
	public static final short DEMO_HOME = 6;
	/**
	 * Requires black electrical tape on the back and sides of the IR sensor.
	 * Homes in on a Virtual Wall until the robot bumps into it, then tries to find another one to home in on.
	 */
	public static final short DEMO_TAG = 7;
	/**
	 * Plays the notes of Pachalbel's Canon when cliff sensors are activated in sequence.
	 * (does not work in the simulator)
	 */
	public static final short DEMO_PACHALBEL = 8;
	/**
	 * Plays notes of a chord when cliff sensors are activated, and the chord is changed based on the number of bumpers activated. 
	 * (does not work in the simulator)
	 */
	public static final short DEMO_BANJO = 9;
	
	/**
	 * Drive forward with a slight rightward curve.
	 */
	public static final int DRIVE_FORWARD_RIGHT = 0x7fff;
	/**
	 * Drive forward with a slight leftward curve.
	 */
	public static final int DRIVE_FORWARD_LEFT = 0x8000;
	/**
	 * Spin in place.
	 */
	public static final int DRIVE_SPIN = 0xffff;
}
