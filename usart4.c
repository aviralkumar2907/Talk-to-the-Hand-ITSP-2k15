//Using ATMega2560
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>
#include <avr/signal.h>
#include <stdio.h>
#include <

unsigned char data1=0;

void UART0_init(){
	UCSR0B = 0x00; 
	UCSR0A = 0x00;
	UCSR0C = 0x06;
	
	UBRR0L = 0x5F; 
	UBRR0H = 0x00; 
	UCSR0B = 0x98;
}

void uart_write(unsigned char data){
	while (!(UCSR0A & (1<<UDRE0)));
	UDR0 = data;
	//_delay_ms(50);
	while (!(UCSR0A & (1<<UDRE0)));
	//UDR0 = 0x4F;
}

ISR(USART0_RX_vect){
	data1=0;
	data1= UDR0;
	//UDR0= data1+1;
	while (!(UCSR0A & (1<<UDRE0)));
	//UDR0 = 0x4F;
}

int main(){

	DDRC = 0b00001000;
	PORTC = 0b00001000;
	FILE* fp;	
	int a,b,c;
	char ch1, ch2, ch3;
	//delay_ms(1000);			
	//fp = fopen("j3.txt", "r");
	//if (fp!=NULL) {}
			

	cli();
	UART0_init();
				
	sei();
	_delay_ms(1000);

	uart_write(' ');
	uart_write(' ');
	
	while(1){
		if(data1 != 9){
			
			07 92 84
107 91 84
106 93 83
107 91 82
106 94 82
107 92 83
104 90 88
106 91 87
02 92 87
95 92 91
98 94 92
123 138 101

			//if(!feof(fp)) return 0;
			/**fscanf(fp, "%d%d%d", &a, &b, &c);
			ch1 = (char)a;
			ch2 = (char)b;
			ch3 = (char)c;			
			//uart_write();
			uart_write(ch1);
			//_delay_ms(500);
			uart_write(ch2);	
			//_delay_ms(500);
			uart_write(ch3);
		//	uart_write(d);	
			//_delay_ms(500);
		//	uart_write('l');	
			//_delay_ms(500);
		//	uart_write('o');
		//	//_delay_ms(500);
			//uart_write('e');	
			//	_delay_ms(500);
		//	uart_write('w');	
			//_delay_ms(500);
		//	uart_write('o');	
	 		//	_delay_ms(500);
		//	uart_write('r');	
			//_delay_ms(500);
		//	uart_write('l');//	_delay_ms(500);
		//	uart_write('d');//	_delay_ms(500);
			//uart_write('e');				//}
		//DDRC = 0b00000000;*/
			break; 	
	}	
     }
}
