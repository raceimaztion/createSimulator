// main.cc

//#include "cm.h"

// For waiting in milliseconds
#ifdef WINDOW
#include <Winbase.h>
#else
#include <unistd.h>
#endif

void padCommand();
void endCommand();

/*
 * This is the main library source file for the Create Simulator.
 */

// Turns the robot on.
void cm_power_on(void)
{
#ifdef MODE_EMBEDDED
	if (!cm_robot_power_status())
	{
		while (!cm_robot_power_status())
		{
			RobotPwrToggleLow;
			cm_wait_ms(500);  // Delay in this state
			RobotPwrToggleHigh;  // Low to high transition to toggle power
			cm_wait_ms(100);  // Delay in this state
			RobotPwrToggleLow;
		}
	}
#endif
#ifdef MODE_LOCAL
	// Tell the repeater to power the robot on
	printf("PowerOn\n");
#endif
}

// Turns the Create/Roomba off.
void cm_power_off(void)
{
#ifdef MODE_EMBEDDED
	if (cm_robot_power_status())
	{
		while (cm_robot_power_status())
		{
			RobotPwrToggleLow;
			cm_wait_ms(500); // Delay in this state
			RobotPwrToggleHigh; // Low to high transition to toggle power
			cm_wait_ms(100); // Delay in this state
			RobotPwrToggleLow;
		}
	}
#endif
#ifdef MODE_LOCAL
	// Tell the repeater to turn the robot off
	printf("PowerOff\n");
#endif
}

// Grants full control of the robot (and all consequences) to your program.
void cm_full_mode(void)
{
	sendByte(CmdFull);
}

/**
 * Conditionally grants full control of the robot to your program.
 * If the robot enters a danger condition, the robot returns to passive mode.
 */
void cm_safe_mode(void)
{
	sendByte(CmdSafe);
}

/**
 * Returns the robot to passive mode, where your program can query sensors,
 *   but not drive the robot.
 * Note: This is the default mode.
 */
void cm_passive_mode(void)
{
	cm_play_demo(255);
}

/**
 * Tells the robot to play the specified demo.
 *   See the DEMO_* constants for more information.
 */
void cm_play_demo(uint8_t demo)
{
	sendByte(CmdDemo);
	padCommand();
	sendByte(demo);
	endCommand();
}

/**
 * Set the rate of data transfer between the robot and the Command Module.
 * Not important for the most part.
 *   See the Baud* codes in oi.h for more information.
 */
void cm_baud_rate(uint8_t baud)
{
	if(baud_code <= 11)
	{
		sendByte(CmdBaud);
#ifdef MODE_EMBEDDED
		UCSR0A |= _BV(TXC0);
#endif
		sendByte(baud_code);
#ifdef MODE_EMBEDDED
		// Wait until transmit is complete
		while(!(UCSR0A & _BV(TXC0))) ;
		
		cli();
		
		// Switch the baud rate register
		if (baud_code == Baud115200)      UBRR0 = Ubrr115200;
		else if (baud_code == Baud57600) UBRR0 = Ubrr57600;
		else if (baud_code == Baud38400) UBRR0 = Ubrr38400;
		else if (baud_code == Baud28800) UBRR0 = Ubrr28800;
		else if (baud_code == Baud19200) UBRR0 = Ubrr19200;
		else if (baud_code == Baud14400) UBRR0 = Ubrr14400;
		else if (baud_code == Baud9600)  UBRR0 = Ubrr9600;
		else if (baud_code == Baud4800)  UBRR0 = Ubrr4800;
		else if (baud_code == Baud2400)  UBRR0 = Ubrr2400;
		else if (baud_code == Baud1200)  UBRR0 = Ubrr1200;
		else if (baud_code == Baud600)   UBRR0 = Ubrr600;
		else if (baud_code == Baud300)   UBRR0 = Ubrr300;
		
		sei();
		
		cm_wait_ms(100);
#endif
	}
} // end cm_baud_rate()

/**
 * Tells the robot to run the "Cover" demo.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_cover(void)
{
	sendByte(CmdClean);
	endCommand();
}

/**
 * Tells the robot to run the "cover" demo and dock with its charging station
 *   when its battery gets low.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_cover_and_dock(void)
{
	sendByte(CmdDock);
	endCommand();
}

/**
 * Tells the robot to run the "spot clean" demo.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_spot(void)
{
	sendByte(CmdSpot);
	endCommand();
}

/**
 * Tells the robot to start driving at the specified speed, with the specified
 * 	driving radius to the left.  Note that MAX_INT16 and MIN_INT16 have it
 *	drive as straight as possible, but it's still not entirely straight.
 */
void cm_drive(int16_t speed, int16_t radius)
{
	sendByte(CmdDrive);
	padCommand();
	sendWord(speed);
	padCommand();
	sendWord(radius);
	endCommand();
}

/**
 * Tells the robot to start driving by specifying the driving speed of each wheel.
 */
void cm_direct_drive(int16_t right_speed, int16_t left_speed)
{
	sendByte(CmdDriveWheels);
	padCommand();
	sendWord(right_speed);
	padCommand();
	sendWord(left_speed);
	endCommand();
}

/**
 * Convenience function to tell the robot to stop driving.
 */
void cm_stop_driving()
{
	cm_direct_drive(0, 0);
}

/**
 * Controls the LEDs on top of the robot.
 *   @param play Turns the Play light on if non-zero.
 *   @param advance Turns the Advance light on if non-zero.
 *   @param power_color Sets the shade of the Power light between green (at 0) and orange (at 255).
 *   @param power_intensity Sets the brightness of the Power light from off (at 0) to full brightness (at 255).
 */
void cm_set_leds(uint8_t play, uint8_t advance, uint8_t power_color, uint8_t power_intensity)
{
	uint8_t b = 0;
	if (play)		b |= LEDPlay;
	if (advance)	b |= LEDAdvance;
	
	sendByte(CmdLeds);
	padCommand();
	sendByte(b);
	padCommand();
	sendByte(power_color);
	padCommand();
	sendByte(power_intensity);
	endCommand();
}

/**
 * Sets the digital outputs on the Create's cargo bay connector.
 *   @param pin*: Turns the pin on if non-zero.
 * Note 1: Not used in simulations.
 * Note 2: Only available on Create robots. Does nothing on Roomba or Scooba robots.
 */
void cm_digital_outputs(uint8_t pin0, uint8_t pin1, uint8_t pin2)
{
	uint8_t b = 0;
	if (pin0)	b |= 0x01;
	if (pin1)	b |= 0x02;
	if (pin2)	b |= 0x04;
	
	sendByte(CmdOutputs);
	padCommand();
	sendByte(b);
	endCommand();
}

/**
 * Sends the provided byte out over IR.
 */
void cm_send_ir(uint8_t data)
{
	sendByte(CmdIRChar);
	padCommand();
	sendByte(data);
	endCommand();
}

/**
 * Stores a song under the given number for later playback.
 */
void cm_store_song(uint8_t song_number, uint8_t song_length, const uint8_t song_notes[], const uint8_t song_lengths[])
{
	sendByte(CmdSong);
	padCommand();
	sendByte(song_number);
	padCommand();
	sendByte(song_length);
	  
	for (uint8_t i=0; i < song_length; i++)
	{
		padCommand();
		cm_send_byte(song_notes[i]);
		padCommand();
		cm_send_byte(song_lengths[i]);
	}
	endCommand();
}

/**
 * Plays the song stored under the given number.
 */
void cm_play_song(uint8_t song_number)
{
	sendByte(CmdPlay);
	padCommand();
	sendByte(song_number);
	endCommand();
}

/* ************************* *
 * Sensor-reading functions: *
 * ************************* */

/**
 * Returns 1 if the left bumper is pressed.
 */
uint8_t cm_read_left_bumper()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_BUMP_DROP);
	endCommand();
	
	if (readByte() & 0x02)
		return 1;
	else
		return 0;
}

/**
 * Returns 1 if the right bumper is pressed.
 */
uint8_t cm_read_right_bumper()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_BUMP_DROP);
	endCommand();
	
	if (readByte() & 0x01)
		return 1;
	else
		return 0;
}

/**
 * Returns 1 if the left wheel is dropped.
 */
uint8_t cm_read_left_wheel_drop()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_BUMP_DROP);
	endCommand();
	
	if (readByte() & 0x08)
		return 1;
	else
		return 0;
}

/**
 * Returns 1 if the right wheel is dropped.
 */
uint8_t cm_read_right_wheel_drop()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_BUMP_DROP);
	endCommand();
	
	if (readByte() & 0x04)
		return 1;
	else
		return 0;
}

/**
 * Returns 1 if the caster wheel is dropped.
 */
uint8_t cm_read_caster_wheel_drop()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_BUMP_DROP);
	endCommand();
	
	if (readByte() & 0x10)
		return 1;
	else
		return 0;
}

/**
 * Returns 1 if the robot currently sees a wall on its right side.
 * Note: There is only one wall sensor on all iRobot robots.
 */
uint8_t cm_read_wall()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_WALL);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if the robot sees a cliff on its far left cliff sensor.
 */
uint8_t cm_read_far_left_cliff()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CLIFF_LEFT);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if the robot sees a cliff on its front left cliff sensor.
 */
uint8_t cm_read_front_left_cliff()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CLIFF_FRONT_LEFT);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if the robot sees a cliff on its front right cliff sensor.
 */
uint8_t cm_read_front_right_cliff()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CLIFF_FRONT_RIGHT);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if the robot sees a cliff on its far right cliff sensor.
 */
uint8_t cm_read_far_right_cliff()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CLIFF_RIGHT);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if the robot currently sees a virtual wall.
 */
uint8_t cm_read_virtual_wall()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_VIRTUAL_WALL);
	endCommand();
	
	return readByte();
}

/*
TODO: Implement something to deal with the "Low Side Driver and Wheel Overcurrents"
	sensor request. code #14.
*/

/**
 * Reads the byte last received from the IR sensor.
 * See the REMOTE_* codes in the cm.h header.
 */
uint8_t cm_read_ir()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_IR_CODE);
	endCommand();
	
	return readByte();
}

/*
TODO: Implement the "Buttons" sensor request. code #18.
*/

/**
 * Reads the distance the robot has moved since the last time it was asked.
 * Note: Units are in millimeters.
 */
int16_t cm_read_distance()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_DISTANCE);
	endCommand();
	
	return readWord();
}

/**
 * Reads the angle the robot has turned since the last time it was asked.
 * Note: Units are in degrees, with positive values to the right and negative
 *   values to the left.
 */
int16_t cm_read_angle()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_ANGLE);
	endCommand();
	
	return readWord();
}}

/**
 * Reads the battery's current charging state.
 * See the BATTERY_* codes in the cm.h header.
 */
uint8_t cm_read_charging_state()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CHARGE_STATE);
	endCommand();
	
	return readByte();
}

/**
 * Reads the battery's current voltage in millivolts.
 */
uint16_t cm_read_battery_voltage()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_VOLTAGE);
	endCommand();
	
	return readWord();
}

/**
 * Reads the amount of current running into (positive values) or out of (negative values)
 *   the robot's battery.
 */
int16_t cm_read_battery_current()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CURRENT);
	endCommand();
	
	return readWord();
}

/**
 * Reads the battery's current temperature in degrees Celsius.
 */
int8_t cm_read_battery_temperature()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_TEMPERATURE);
	endCommand();
	
	return readWord();
}

/**
 * Reads the battery's current charge in milliamp-hours.
 * Note: This value is not accurate if the robot is running off Alkaline batteries.
 */
uint16_t cm_read_battery_charge()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CHARGE);
	endCommand();
	
	return readWord();
}

/**
 * Reads the battery's estimated charge capacity in milliamp-hours.
 * Note: This value is not accurate if the robot is running off Alkaline batteries.
 */
uint16_t cm_read_battery_capacity()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_CAPACITY);
	endCommand();
	
	return readWord();
}

/**
 * Reads the strength of the wall sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_wall_signal()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_WALL_SIGNAL);
	endCommand();
	
	return readWord();
}

/**
 * Reads the strength of the far left cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_left_cliff_signal()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_LEFT_CLIFF_SIGNAL);
	endCommand();
	
	return readWord();
}

/**
 * Reads the strength of the front left cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_front_left_cliff_signal()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_FRONT_LEFT_CLIFF_SIGNAL);
	endCommand();
	
	return readWord();
}

/**
 * Reads the strength of the front right cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_front_right_cliff_signal()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_FRONT_RIGHT_CLIFF_SIGNAL);
	endCommand();
	
	return readWord();
}

/**
 * Reads the strength of the far right cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_right_cliff_signal()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_RIGHT_CLIFF_SIGNAL);
	endCommand();
	
	return readWord();
}

/*
TODO: Implement the "Cargo Bay Digital Inputs" sensor request. code #32, formatted byte.

TODO: Implement the "Cargo Bay Analog Signal" sensor request. code #33, unsigned 10-bit int.

TODO: Implement the "Charging Sources Available" sensor request. code #34, formatted byte.
*/

/**
 * Reads the current Open Interface mode.
 * See the OI_MODE_* codes in the cm.h header.
 */
uint8_t cm_read_oi_mode()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_OI_MODE);
	endCommand();
	
	return readByte();
}

/**
 * Reads the number of the currently-playing song.
 * Range is 0-15.
 */
uint8_t cm_read_current_song_number()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_SONG_NUMBER);
	endCommand();
	
	return readByte();
}

/**
 * Returns 1 if there is a song currently playing.
 */
uint8_t cm_read_is_song_playing()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_SONG_PLAYING);
	endCommand();
	
	return readByte();
}

/**
 * Returns the last-requested speed.
 * Range is -500 to 500, units are in millimeters per second.
 */
uint16_t cm_read_requested_speed()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_SPEED);
	endCommand();
	
	return readWord();
}

/**
 * Returns the last-requested radius.
 * Range is -32768 to 32767, units are in millimeters.
 */
uint16_t cm_read_requested_radius()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_RADIUS);
	endCommand();
	
	return readWord();
}

/**
 * Returns the last-requested speed for the right wheel.
 * Range is -500 to 500, in millimeters per second.
 */
uint16_t cm_read_requested_right_speed()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_RIGHT_WHEEL_SPEED);
	endCommand();
	
	return readWord();
}

/**
 * Returns the last-requested speed for the left wheel.
 * Range is -500 to 500, in millimeters per second.
 */
uint16_t cm_read_requested_left_speed()
{
	sendByte(CmdSensor);
	padCommand();
	sendByte(SEN_LEFT_WHEEL_SPEED);
	endCommand();
	
	return readWord();
}

/**
 * Returns the robot's current power state. 1 for on, 0 for off.
 */
uint8_t cm_robot_power_status()
{
#ifdef MODE_EMBEDDED
	if (RobotIsOn)
		return 1;
	else
		return 0;
#endif
	// TODO: Figure out how to ask for this.
#ifdef MODE_LOCAL
	printf("RobotIsOn\n");
	return readByte();
#endif
}

/**
 * Waits for the specified length of time in milliseconds.
 */
void cm_wait_ms(uint16_t &time)
{
#ifdef MODE_EMBEDDED
	__timer_count = time;
	__timer_on = 1;
	while (__timer_on) ;
#endif
#ifdef MODE_LOCAL
	// TODO: Figure out how to wait in a resolution of milliseconds
#ifdef WINDOWS
	Sleep(time);
#else
	usleep(time);
#endif
#endif
}

/*
 * Generally-useful functions that the main library of code uses to communicate with the robot
 */
void sendByte(const uint8_t &value)
{
#ifdef MODE_EMBEDDED
	while(!(UCSR0A & _BV(UDRE0))) ;
	UDR0 = data;
#else // Typically, MODE_LOCAL is defined if MODE_EMBEDDED isn't
	printf("0x%02X", value);
#endif	
}

void sendWord(const uint16_t &value)
{
#ifdef MODE_EMBEDDED
	sendByte(0xff & (value >> 8));
	sendByte(0xff & value);
#else // Typically, MODE_LOCAL is defined if MODE_EMBEDDED isn't
	printf("0x%04X", value);
#endif
}

uint8_t readByte()
{
#ifdef MODE_EMBEDDED
	while(!(UCSR0A & _BV(RXC0))) ;
	return UDR0;
#else // Typically, MODE_LOCAL is defined if MODE_EMBEDDED isn't
	int value;
	scanf("0x%x", &value);
	return (uint8_t)(0xff & value);
#endif	
}

uint16_t readWord()
{
#ifdef MODE_EMBEDDED
	return TO_UINT16(readByte(), readByte());
#else // Typically, MODE_LOCAL is defined if MODE_EMBEDDED isn't
	int value;
	scanf("0x%x", &value);
	return (uint16_t)(0xffff & value);
#endif
}

// Used in MODE_LOCAL execution only, adds a space between codes so the repeater can translate
//    the codes effectively.
void padCommand()
{
#ifdef MODE_LOCAL
	printf(" ");
#endif
}

// Used in MODE_LOCAL execution only, adds a newline to the end of a command.
void endCommand()
{
#ifdef MODE_LOCAL
	printf("\n");
#endif
}

/*
 * This is the main entry point for the embedded version:
 */
#ifdef MODE_MODE_EMBEDDED
int main(void)
{
	// Do init stuff here
	// Set I/O pins
	DDRB = 0x10;
	PORTB = 0xCF;
	DDRC = 0x00;
	PORTC = 0xFF;
	DDRD = 0xE6;
	PORTD = 0x7D;
	
	// Set up the serial port
	UBRR0 = 19;
	UCSR0B = (_BV(RXCIE0) | _BV(TXEN0) | _BV(RXEN0));
	UCSR0C = (_BV(UCSZ00) | _BV(UCSZ01));
	
	// Set up timer 1 to generate an interrupt every 1 ms
	TCCR1A = 0x00;
	TCCR1B = (_BV(WGM12) | _BV(CS12));
	OCR1A = 71;
	TIMSK1 = _BV(OCIE1A);
	
	// Start the OI:
	sendByte(CmdStart);
}

volatile uint16_t __timer_count = 0;
volatile uint8_t __timer_running = 0;

// This is the timer callback
// Timer 1 interrupt times delays in ms
SIGNAL(SIG_OUTPUT_COMPARE1A)
{
  if (__timer_count)
    __timer_count --;
  else
    __timer_on = 0;
}

#endif
#ifdef MODE_MODE_LOCAL
int main(void)
{
	// TODO: Configure a timer
	
	
	// Start the OI:
	sendByte(CmdStart);
}
#endif

