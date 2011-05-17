// main.cc

#include "cm.h"

/*
 * This is the main library source file for the Create Simulator.
 */

void cm_power_on()
{
	// Turn the robot on
}

void sendByte(const uint8_t &value)
{
#ifdef EMBEDDED
	while(!(UCSR0A & _BV(UDRE0))) ;
	UDR0 = data;
#else // Typically, LOCAL is defined if EMBEDDED isn't
	printf("0x%02X\n", value);
#endif	
}

void sendWord(const uint16_t &value)
{
#ifdef EMBEDDED
	sendByte(0xff & (value >> 8));
	sendByte(0xff & value);
#else // Typically, LOCAL is defined if EMBEDDED isn't
	printf("0x%04X\n", value);
#endif
}

uint8_t readByte()
{
#ifdef EMBEDDED
	while(!(UCSR0A & _BV(RXC0))) ;
	return UDR0;
#else // Typically, LOCAL is defined if EMBEDDED isn't
	int value;
	scanf("0x%x", &value);
	return (uint8_t)(0xff & value);
#endif	
}

uint16_t readWord()
{
#ifdef EMBEDDED
	return TO_UINT16(readByte(), readByte());
#else // Typically, LOCAL is defined if EMBEDDED isn't
	int value;
	scanf("0x%x", &value);
	return (uint16_t)(0xffff & value);
#endif
}

/*
 * This is the main entry point for the embedded version:
 */
#ifdef MODE_EMBEDDED
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

void cm_wait_ms(uint16_t &time)
{
	__timer_count = time;
	__timer_on = 1;
	while (__timer_on) ;
}

uint8_t cm_robot_power_status()
{
	if (RobotIsOn)
		return 1;
	else
		return 0;
}
#endif
#ifdef MODE_LOCAL
int main(void)
{
	// TODO: Configure a timer
	
	
	// Start the OI:
	sendByte(CmdStart);
}

void cm_wait_ms(uint16_t &time)
{
	// TODO: Figure out how to wait for this.
}

uint8_t cm_robot_power_status()
{
	// TODO: Figure out how to ask for this.
}
#endif

