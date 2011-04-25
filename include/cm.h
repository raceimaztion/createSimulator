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
#endif

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
#define SEN_LEFT_CLIFF 9
#define SEN_FRONT_LEFT_CLIFF 10
#define SEN_FRONT_RIGHT_CLIFF 11
#define SEN_RIGHT_CLIFF 12
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
#define SEN_LEFT_CLIFF_SIGNAL 28
#define SEN_FRONT_LEFT_CLIFF_SIGNAL 29
#define SEN_FRONT_RIGHT_CLIFF_SIGNAL 30
#define SEN_RIGHT_CLIFF_SIGNAL 31
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
 * Initializes the CommandModule interface.
 * Called automatically by the internal startup function.
 */
void cm_init(void);

/**
 * Turns the Create/Roomba on.
 */
void cm_power_on(void);

/**
 * Turns the Create/Roomba off.
 */
void cm_power_off(void);

/**
 * Initializes the Create/Roomba's serial port.
 * Called automatically by the internal startup function.
 */
void cm_start(void);

/**
 * Grants full control of the robot (and all consequences) to your program.
 */
void cm_full_mode(void);

/**
 * 
 */
void cm_safe_mode(void);

/**
 * 
 */
void cm_passive_mode(void);

/**
 * 
 */
void cm_play_demo(uint8_t demo);

/**
 * 
 */
void cm_baud_rate(uint8_t baud);

/**
 * 
 */
void cm_demo_cover(void);

/**
 * 
 */
void cm_demo_cover_and_dock(void);

/**
 * 
 */
void cm_demo_spot(void);

/**
 * 
 */
void cm_drive(int16_t speed, int16_t radius);

/**
 * 
 */
void cm_direct_drive(int16_t right_speed, int16_t left_speed);

/**
 * 
 */
void cm_set_leds(uint8_t play, uint8_t advance, uint8_t power_color, uint8_t power_intensity);

/**
 * 
 */
void cm_digital_outputs(uint8_t pin0, uint8_t pin1, uint8_t pin2);

/**
 * 
 */
void cm_send_ir(uint8_t data);

/**
 * 
 */
void cm_store_song(uint8_t song_number, uint8_t song_length, const uint8_t song_notes[], const uint8_t song_lengths[]);

/**
 * 
 */
void cm_play_song(uint8_t song_number);

