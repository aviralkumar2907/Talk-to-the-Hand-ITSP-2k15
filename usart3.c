#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <avr/signal.h>

unsigned char data1=0;


void UART_init (void){
	
	UCSRB = 0x00;
	UCSRA = 0x00;
	UCSRC = 0x06;
	UBRRL = 0x0C;
	UBRRH = 0x00;
	UCSRB = 0x98;
}

void uart_write(char data){

	while (!(UCSRA & (1<<UDRE)));
	UDR = data;
	//while (!(UCSRA & (1<<UDRE)));
	//UDR = 0x0D;
}

ISR(USART_RXC_vect){

	data1= 0;
	data1 = UDR;
	UDR = data1+1;
	while (!(UCSRA & (1<<UDRE)));
	UDR = 0x0D;
}

int main(void)
{	
	DDRB = 0x01;
	PORTB = 0x00;
	cli();
	UART_init();	
	_delay_ms(1000);
	sei();	
	uart_write('2');
	
	while(1);
	/*_ms(1000);
	while(1){
		
		if (flag==1)
		{
			if (data == 'a')
			{
				//DDRB = 0x01;
				PORTB = 0x01;
				_delay_ms(2000);
			}
			PORTB = 0x00;
			flag=0;		
		}
	}*/
}




