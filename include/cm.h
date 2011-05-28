/*
 * This is the main library for the code written in the CreateSimulator.
 * Written by Daryl Van Humbeck.
 *
 * Note that when we're compiling for embedded use, MODE_EMBEDDED is defined, while
 *   MODE_LOCAL is defined for when we're running on the local computer.
 */

// Always include the Open Interface library
#include "oi.h"
// Always include the standard C library
#include <stdlib.h>

// If we're compiling for the Command Module, include some AVR libraries
#ifdef MODE_EMBEDDED
#include <avr/interrupt.h>
#include <avr/io.h>
#endif

// If we're compiling for local execution, include some standard C libraries
#ifdef MODE_LOCAL
#include <stdio.h>

typedef unsigned short uint16_t;
typedef signed short int16_t;
#endif

typedef unsigned char uint8_t;
typedef signed char int8_t;

// Roomba IR codes:
// Remote codes
#define REMOTE_NO_BUTTON 255
#define REMOTE_LEFT 129
#define REMOTE_FORWARD 130
#define REMOTE_RIGHT 131
#define REMOTE_SPOT 132
#define REMOTE_MAX 133
#define REMOTE_SMALL 134
#define REMOTE_MEDIUM 135
#define REMOTE_LARGE 136
#define REMOTE_CLEAN 136
#define REMOTE_PAUSE 137
#define REMOTE_POWER 138
#define REMOTE_FORWARD_LEFT 139
#define REMOTE_FORWARD_RIGHT 140
#define REMOTE_STOP_DRIVING 141
// Scheduling remote codes:
#define REMOTE_SEND_ALL 142
#define REMOTE_SEEK_DOCK 143
// Home base codes:
#define HOME_BASE_RESERVED 240
#define HOME_BASE_RED 248
#define HOME_BASE_GREEN 244
#define HOME_BASE_FORCE_FIELD 242
#define HOME_BASE_RED_GREEN 252
#define HOME_BASE_RED_FORCE_FIELD 250
#define HOME_BASE_GREEN_FORCE_FIELD 246
#define HOME_BASE_RED_GREEN_FORCE_FIELD 254

// Demo codes:
#define DEMO_STOP 255
#define DEMO_COVER 0
#define DEMO_COVER_AND_DOCK 1
#define DEMO_SPOT 2
#define DEMO_MOUSE 3
#define DEMO_FIGURE8 4
#define DMEO_WIMP 5
#define DEMO_HOME 6
#define DEMO_TAG 7
#define DEMO_PACHELBEL 8
#define DEMO_BANJO 9

// Sensor codes
#define SEN_SET_0 0
#define SEN_SET_1 1
#define SEN_SET_2 2
#define SEN_SET_3 3
#define SEN_SET_4 4
#define SEN_SET_5 5
#define SEN_SET_6 6
#define SEN_BUMP_DROP 7
#define SEN_WALL 8
#define SEN_FAR_LEFT_CLIFF 9
#define SEN_FRONT_LEFT_CLIFF 10
#define SEN_FRONT_RIGHT_CLIFF 11
#define SEN_FAR_RIGHT_CLIFF 12
#define SEN_VIRTUAL_WALL 13
#define SEN_OVERCURRENT 14
#define SEN_UNUSED1 15
#define SEN_UNUSED2 16
#define SEN_IR_CODE 17
#define SEN_BUTTONS 18
#define SEN_DISTANCE 19
#define SEN_ANGLE 20
#define SEN_CHARGE_STATE 21
#define SEN_VOLTAGE 22
#define SEN_CURRENT 23
#define SEN_TEMPERATURE 24
#define SEN_CHARGE 25
#define SEN_CAPACITY 26
#define SEN_WALL_SIGNAL 27
#define SEN_FAR_LEFT_CLIFF_SIGNAL 28
#define SEN_FRONT_LEFT_CLIFF_SIGNAL 29
#define SEN_FRONT_RIGHT_CLIFF_SIGNAL 30
#define SEN_FAR_RIGHT_CLIFF_SIGNAL 31
#define SEN_DIGITAL_INPUTS 32
#define SEN_ANALOG_INPUTS 33
#define SEN_AVAILABLE_CHARGE_SOURCES 34
#define SEN_OI_MODE 35
#define SEN_SONG_NUMBER 36
#define SEN_SONG_PLAYING 37
#define SEN_NUMBER_STREAM_PACKETS 38
#define SEN_SPEED 39
#define SEN_RADIUS 40
#define SEN_RIGHT_WHEEL_SPEED 41
#define SEN_LEFT_WHEEL_SPEED 42

// Battery charge codes
#define BATTERY_NOT_CHARGING 0
#define BATTERY_RECONDITIONING_CHARGE 1
#define BATTERY_FULL_CHARGE 2
#define BATTERY_TRICKLE_CHARGE 3
#define BATTERY_WAITING 4
#define BATTERY_FAULT 5

// OI mode codes
#define OI_MODE_OFF 0
#define OI_MODE_PASSIVE 1
#define OI_MODE_SAFE 2
#define OI_MODE_FULL 3

// Generally-useful max and min values
#define MAX_UINT8  0xff
#define MIN_UINT8  0
#define MAX_INT8   0x7f
#define MIN_INT8   0x80

#define MAX_UINT16 0xffff
#define MIN_UINT16 0
#define MAX_INT16  0x7fff
#define MIN_INT16  0x8000

// Generally-useful macros
#define TO_UINT16(a,b)  (((uint16_t)(a) << 8) | (b))
#define LED1Toggle      (PORTD ^= LED1)
#define LED2Toggle      (PORTD ^= LED2)

#define ON   1
#define OFF -1
#define UNCHANGED 0

/* ******************************* *
 * CommandModule function headers: *
 * ******************************* */
/**
 * Turns the Create/Roomba on.
 */
void cm_power_on(void);

/**
 * Turns the Create/Roomba off.
 */
void cm_power_off(void);

/**
 * Grants full control of the robot (and all consequences) to your program.
 */
void cm_full_mode(void);

/**
 * Conditionally grants full control of the robot to your program.
 * If the robot enters a danger condition, the robot returns to passive mode.
 */
void cm_safe_mode(void);

/**
 * Returns the robot to passive mode, where your program can query sensors,
 *   but not drive the robot.
 * Note: This is the default mode.
 */
void cm_passive_mode(void);

/**
 * Tells the robot to play the specified demo.
 *   See the DEMO_* constants for more information.
 */
void cm_play_demo(const uint8_t &demo);

/**
 * Set the rate of data transfer between the robot and the Command Module.
 * Not important for the most part.
 *   See the Baud* codes in oi.h for more information.
 */
void cm_baud_rate(const uint8_t &baud);

/**
 * Tells the robot to run the "Cover" demo.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_cover(void);

/**
 * Tells the robot to run the "cover" demo and dock with its charging station
 *   when its battery gets low.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_cover_and_dock(void);

/**
 * Tells the robot to run the "spot clean" demo.
 * Note: If the robot is a Roomba, it will clean the floor.
 */
void cm_demo_spot(void);

/**
 * Tells the robot to start driving at the specified speed, with the specified
 * 	driving radius to the left.  Note that MAX_INT16 and MIN_INT16 have it
 *	drive as straight as possible, but it's still not entirely straight.
 */
void cm_drive(const int16_t &speed, const int16_t &radius);

/**
 * Tells the robot to start driving by specifying the driving speed of each wheel.
 */
void cm_direct_drive(const int16_t &right_speed, const int16_t &left_speed);

/**
 * Convenience function to tell the robot to stop driving.
 */
void cm_stop_driving(void);

/**
 * Controls the LEDs on top of the robot.
 *   @param play Turns the Play light on if non-zero.
 *   @param advance Turns the Advance light on if non-zero.
 *   @param power_color Sets the shade of the Power light between green (at 0) and orange (at 255).
 *   @param power_intensity Sets the brightness of the Power light from off (at 0) to full brightness (at 255).
 */
void cm_set_leds(const uint8_t &play, const uint8_t &advance, const uint8_t &power_color, const uint8_t &power_intensity);

/**
 * Sets the digital outputs on the Create's cargo bay connector.
 *   @param pin*: Turns the pin on if non-zero.
 * Note 1: Not used in simulations.
 * Note 2: Only available on Create robots. Does nothing on Roomba or Scooba robots.
 */
void cm_digital_outputs(const uint8_t &pin0, const uint8_t &pin1, const uint8_t &pin2);

/**
 * Sends the provided byte out over IR.
 */
void cm_send_ir(const uint8_t &data);

/**
 * Stores a song under the given number for later playback.
 */
void cm_store_song(const uint8_t &song_number, const uint8_t &song_length, const uint8_t song_notes[], const uint8_t song_lengths[]);

/**
 * Plays the song stored under the given number.
 */
void cm_play_song(const uint8_t &song_number);

/* ************************* *
 * Sensor-reading functions: *
 * ************************* */

/**
 * Returns 1 if the left bumper is pressed.
 */
uint8_t cm_read_left_bumper(void);

/**
 * Returns 1 if the right bumper is pressed.
 */
uint8_t cm_read_right_bumper(void);

/**
 * Returns 1 if the left wheel is dropped.
 */
uint8_t cm_read_left_wheel_drop(void);

/**
 * Returns 1 if the right wheel is dropped.
 */
uint8_t cm_read_right_wheel_drop(void);

/**
 * Returns 1 if the caster wheel is dropped.
 */
uint8_t cm_read_caster_wheel_drop(void);

/**
 * Returns 1 if the robot currently sees a wall on its right side.
 * Note: There is only one wall sensor on all iRobot robots.
 */
uint8_t cm_read_wall(void);

/**
 * Returns 1 if the robot sees a cliff on its far left cliff sensor.
 */
uint8_t cm_read_far_left_cliff(void);

/**
 * Returns 1 if the robot sees a cliff on its front left cliff sensor.
 */
uint8_t cm_read_front_left_cliff(void);

/**
 * Returns 1 if the robot sees a cliff on its front right cliff sensor.
 */
uint8_t cm_read_front_right_cliff(void);

/**
 * Returns 1 if the robot sees a cliff on its far right cliff sensor.
 */
uint8_t cm_read_far_right_cliff(void);

/**
 * Returns 1 if the robot currently sees a virtual wall.
 */
uint8_t cm_read_virtual_wall(void);

/*
TODO: Implement something to deal with the "Low Side Driver and Wheel Overcurrents"
	sensor request. code #14.
*/

/**
 * Reads the byte last received from the IR sensor.
 * See the REMOTE_* codes in the cm.h header.
 */
uint8_t cm_read_ir(void);

/*
TODO: Implement the "Buttons" sensor request. code #18. !important!
*/

/**
 * Reads the distance the robot has moved since the last time it was asked.
 * Note: Units are in millimeters.
 */
int16_t cm_read_distance(void);

/**
 * Reads the angle the robot has turned since the last time it was asked.
 * Note: Units are in degrees, with positive values to the right and negative
 *   values to the left.
 */
int16_t cm_read_angle(void);

/**
 * Reads the battery's current charging state.
 * See the BATTERY_* codes in the cm.h header.
 */
uint8_t cm_read_charging_state(void);

/**
 * Reads the battery's current voltage in millivolts.
 */
uint16_t cm_read_battery_voltage(void);

/**
 * Reads the amount of current running into (positive values) or out of (negative values)
 *   the robot's battery.
 */
int16_t cm_read_battery_current(void);

/**
 * Reads the battery's current temperature in degrees Celsius.
 */
int8_t cm_read_battery_temperature(void);

/**
 * Reads the battery's current charge in milliamp-hours.
 * Note: This value is not accurate if the robot is running off Alkaline batteries.
 */
uint16_t cm_read_battery_charge(void);

/**
 * Reads the battery's estimated charge capacity in milliamp-hours.
 * Note: This value is not accurate if the robot is running off Alkaline batteries.
 */
uint16_t cm_read_battery_capacity(void);

/**
 * Reads the strength of the wall sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_wall_signal(void);

/**
 * Reads the strength of the far left cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_left_cliff_signal(void);

/**
 * Reads the strength of the front left cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_front_left_cliff_signal(void);

/**
 * Reads the strength of the front right cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_front_right_cliff_signal(void);

/**
 * Reads the strength of the far right cliff sensor's signal.
 * The range is 0 to 4095.
 */
uint16_t cm_read_right_cliff_signal(void);

/*
TODO: Implement the "Cargo Bay Digital Inputs" sensor request. code #32, formatted byte.

TODO: Implement the "Cargo Bay Analog Signal" sensor request. code #33, unsigned 10-bit int.

TODO: Implement the "Charging Sources Available" sensor request. code #34, formatted byte.
*/

/**
 * Reads the current Open Interface mode.
 * See the OI_MODE_* codes in the cm.h header.
 */
uint8_t cm_read_oi_mode(void);

/**
 * Reads the number of the currently-playing song.
 * Range is 0-15.
 */
uint8_t cm_read_current_song_number(void);

/**
 * Returns 1 if there is a song currently playing.
 */
uint8_t cm_read_is_song_playing(void);

/**
 * Returns the last-requested speed.
 * Range is -500 to 500, units are in millimeters per second.
 */
uint16_t cm_read_requested_speed(void);

/**
 * Returns the last-requested radius.
 * Range is -32768 to 32767, units are in millimeters.
 */
uint16_t cm_read_requested_radius(void);

/**
 * Returns the last-requested speed for the right wheel.
 * Range is -500 to 500, in millimeters per second.
 */
uint16_t cm_read_requested_right_speed(void);

/**
 * Returns the last-requested speed for the left wheel.
 * Range is -500 to 500, in millimeters per second.
 */
uint16_t cm_read_requested_left_speed(void);

/**
 * Returns the robot's current power state. 1 for on, 0 for off.
 */
uint8_t cm_robot_power_status(void);

/**
 * Waits for the specified length of time in milliseconds.
 */
void cm_wait_ms(const uint16_t &time);

/* ************************************************ *
 * Functions intended to be private to this module: *
 * ************************************************ */
void sendByte(const uint8_t &value);
void sendWord(const uint16_t &value);
uint8_t readByte(void);
uint16_t readWord(void);

