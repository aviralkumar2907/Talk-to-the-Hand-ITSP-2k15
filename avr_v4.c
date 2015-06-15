
#include <avr/io.h>
#include <avr/interrupt.h>
#include <avr/signal.h>
#include <util/delay.h>
#include <avr/pgmspace.h>

//volatile unsigned int ADC;

struct acc{
	  unsigned int  a, b, c;
};

struct letter {
	  unsigned int a[6];
};

struct contact{
	char ch[8];
};

void PORTinit(void){
	DDRJ = ~0x2A;
	PORTJ = 0x2A;
	DDRG = ~0x04;
	PORTG = 0x04;
	DDRA = 0b10100011;
	PORTA = 0b01011100;
}

void ADC_pinconfig(void){
	DDRF = 0x00;
	PORTF=0x00;
	DDRK=0x00;
	PORTK=0x00;
}


unsigned int  ReadADC(unsigned char ch)
{
	unsigned char a=0, b=0;
    if(ch>7){
   		ADCSRB = 0x08;
    }
    ch=ch & 0x07;   // bit wise multipliucation
    ADMUX= 0x00| ch;          // or operator selectively manipulates certain bits of admux register
	ADCSRA = ADCSRA | 0x40; //Set start conversion bit
	while((ADCSRA&0x10)==0); //Wait for conversion to complete
	//a=ADCH;
	b=ADCL;
	a=ADCH;
	             //Compiler help to stor the data of both  ADCH and ADCL together in the unsigned integer
	ADCSRA = ADCSRA|0x10; //clear ADIF (ADC Interrupt Flag) by writing 1 to it
	ADCSRB = 0x00;
	return (256*a+b);
}


unsigned char data1=0;

void UART0_init(){
	UCSR0B = 0x00; //disable while setting baud rate
	UCSR0A = 0x00;
	UCSR0C = 0x06;
	// UBRR0L = 0x47; //11059200 Hz
	UBRR0L = 0x5F; // 14745600 Hzset baud rate lo
	UBRR0H = 0x00; //set baud rate hi
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


 const static struct letter miin[] ={
 {460,410,492,461,240,0},   //A
{625,583,613,601,270,0},    //B
 {550,500,530,560,260,0},   //C
 {630,450,530,530,225,0},   //D
 {460,425,500,490,200,0},   //E
  {500,578,630,630,320,0},  //F
{600,418,485,460,295,0},    //G
{610,575,500,470,245,0},    //H
 {450,390,465,525,225,0},   //I
 {450,390,465,525,225,0},   //J
  {630,490,490,460,225,0},  //K
   {610,400,450,430,300,0}, //L
{479,416,491,481,240,0},    //M
{459,431,497,466,220,0},    //N
 {503,450,516,540,225,0},   //O
  {645,480,480,500,240,438},//P
  {600,418,485,470,250,438},//Q
   {580,550,507,490,210,0},//R
{445,400,478,483,220,0},    //S
 {568,420,490,470,230,0},   //T
 {629,570,507,500,220,0},   //U
 {640,580,500,490,210,0},   //V
 {648,590,620,510,210,0},   //W
  {506,395,485,476,207,0},  //X
{467,410,487,582,300,0},    //Y
{630,402,495,495,205,0}     //Z
    };

 const static struct letter maax[] ={
 {520,474,546,526,320,1024},    //A
 {732,691,689,693,330,1024},    //B
 {630,580,610,630,325,1024},    //C
 {700,520,590,595,310,1024},    //D
 {535,500,565,550,275,1024},    //E
  {565,653,694,707,370,1024},   //F
{675,474,545,530,330,430},      //G
 {680,630,570,530,300,1024},    //H
  {530,460,525,620,280,1024},   //I
  {530,460,525,620,280,1024},   //J
   {700,565,575,520,300,430},   //K
    {686,460,550,557,375,1024}, //L
{568,480,570,538,287,1024},     //M
 {553,514,579,547,267,1024},    //N
  {588,521,587,596,295,1024},   //O
   {710,545,555,560,287,1024},  //P
    {655,474,545,538,300,1024}, //Q
     {670,610,560,540,270,1024},//R
{510,460,552,535,280,1024},     //S
 {617,467,540,533,278,1024},    //T
 {700,630,565,562,287,1024},    //U
 {705 ,650,560,560,280,1024},   //V
  {700,655,697,580,270,1024},   //W
{564,457,545,535,259,1024},     //X
{532,470,541,642,360,1024},     //Y
{677,445,544,543,254,1024}      //Z
        };

 const static struct contact tmp[]  = { {0,1,1,1,1,1,1,1}, {0,1,1,1,1,1,1,1}, {0,1,1,1,1,1,1,1}, {1,1,0,1,1,1,1,1}, {0,1,0, 1,1,1,1,0}, {1,1,1,1,1,1,1,0}, {1,1,1,1,1,1,1,1}, {0,1,1,1,0,1,1,1}, {0,1,1,1,1,1,1,1},
      {0,1,1,1,1,1,1,1}, {1,0,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {0,1,1,1,1,0,1,1}, {0,1,1,1,0,1,1,1}, {0,1,0,1,1,1,1,1}, {1,0,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {1,1,1,1,1,1,0,1}, {0,1,1,0,1,1,0,1},
      {0,0,1,1,1,1,1,0}, {0,1,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {1,1,1,1,1,1,1,1}, {1,1,1,0,1,1,1,1}, {0,1,1,1,1,1,1,1}, {1,1,1,0,1,1,1,1}};



int main(){

	cli();
	PORTinit();
	UART0_init();
	ADC_pinconfig();
	sei();

	 struct letter temp;
	//int pos[2];
	int pos;
	 struct contact ctl;
	//struct acc tempo;
	int flag=0;
	int p=0, k=0;
	 int count[27]; // array to count the occurences of a particular letter where the index denotes the letter and entry denotes the frequency
	long int time1=0;
    int i=0,j=0;
	struct acc reading[30];
    for(i=0; i<27; i++)count[i]=0;
		ADCSRA = 0x00;
		ADCSRB = 0x00;		//MUX5 = 0
		ADMUX = 0x00;		//Vref=5V external --- ADLAR=1 --- MUX4:0 = 0000
		ACSR = 0x80;
		ADCSRA = 0x86;
			//

   _delay_ms(2000);
	while(1){
		//ADC part here
		flag=0;
        int maxim=0;
            /***Specify the ADC ports here***/
		if(time1%50==0 && time1%100!=0){

			temp.a[0]= ReadADC(4);
			temp.a[1]= ReadADC(6);
			temp.a[2]= ReadADC(8);
			temp.a[3]= ReadADC(10);
			temp.a[4]= ReadADC(12);
			temp.a[5]= ReadADC(14);
            //uart_write(temp.a[0]-500);

            ctl.ch[0]= (PINA&(1<<PA6))/64;   //fine
			ctl.ch[1]= (PINA&(1<<PA4))/16;  //fine
			ctl.ch[2] = (PING&(1<<PG2))/4;  //fine
			ctl.ch[3]=(PINJ&(1<<PJ5))/32;   //fine
			ctl.ch[4]=(PINJ&(1<<PJ3))/8;    //fine
			ctl.ch[5] = (PINJ&(1<<PJ1))/2;  //fine
			ctl.ch[6]=(PINA&(1<<PA3))/8;    //fine
			ctl.ch[7]= (PINA&(1<<PA2))/4;   //finr
			//for(int i=0;i<7;i++){uart_write(ctl.ch[i]);uart_write(' ');_delay_ms(50);}
            //uart_write(flag+'0');uart_write(' ');uart_write(' ');
			for(i=0; i<26; i++){
			flag=0;
    			for(j=0; j<6; j++){
      				if ((miin[i].a[j]>temp.a[j]) || (temp.a[j]>maax[i].a[j])){
                    //uart_write(miin[i].a[0]/100+'0');uart_write((miin[i].a[0]%100)/10+'0');uart_write(miin[i].a[0]%10+'0'); uart_write(32);
                    //_delay_ms(2000);      					//flag=1;
      				flag=1;
      				}

  				}
  				//uart_write(flag+'0');
  				//uart_write(32);
  				if (flag==0) {
  					 for(int j=0;j<8;j++){
    					if(ctl.ch[j]!=tmp[i].ch[j]){
        					flag=1;
    					}
					}
				}
				if(flag==0) {
					count[i]++;

				}
				else{
                    //uart_write('A');
					flag=0;
					//uart_write(flag+'0');
				}
			}
			 time1+=50;
           /*uart_write(temp.a[0]/100+'0');uart_write((temp.a[0]%100)/10+'0');uart_write(temp.a[0]%10+'0'); uart_write(32);
			uart_write(temp.a[1]/100+'0');uart_write((temp.a[1]%100)/10+'0');uart_write(temp.a[1]%10+'0'); uart_write(32);
            uart_write(temp.a[2]/100+'0');uart_write((temp.a[2]%100)/10+'0');uart_write(temp.a[2]%10+'0'); uart_write(32);
            uart_write(temp.a[3]/100+'0');uart_write((temp.a[3]%100)/10+'0');uart_write(temp.a[3]%10+'0'); uart_write(32);
            uart_write(temp.a[4]/100+'0');uart_write((temp.a[4]%100)/10+'0');uart_write(temp.a[4]%10+'0'); uart_write(32);
            uart_write(temp.a[5]/100+'0');uart_write((temp.a[5]%100)/10+'0');uart_write(temp.a[5]%10+'0'); uart_write(32);
            uart_write(ctl.ch[0]+'0');uart_write(32);
            uart_write(ctl.ch[1]+'0');uart_write(32);
            uart_write(ctl.ch[2]+'0');uart_write(32);
            uart_write(ctl.ch[3]+'0');uart_write(32);
            uart_write(ctl.ch[4]+'0');uart_write(32);
            uart_write(ctl.ch[5]+'0');uart_write(32);
            uart_write(ctl.ch[6]+'0');uart_write(32);
            uart_write(ctl.ch[7]+'0');uart_write(32);
            uart_write('\n');*/

			_delay_ms(50);
		}

		else {
			reading[p].a=ReadADC(0);
			reading[p].b=ReadADC(2);
			reading[p].c=ReadADC(5);
			p++;
			time1+=50;
			_delay_ms(50);
		}

		if(time1==2000){
			for(i=0; i<27; i++){
				if(maxim<count[i]) {
					maxim = count[i];
					pos=i;
				}
				if(maxim==count[i]){
					pos=i;
				}
			}
			if(pos==9 || pos==8){
				uart_write(' ');
				//uart_write(' ');
				for(k=0; k<p; k++){
					uart_write(reading[k].a/4);
					uart_write(reading[k].b/4);
					uart_write(reading[k].c/4);

				}
				uart_write('&');
				_delay_ms(2000);
			}
			else {//uart_write(temp.a[3]/100+'0');uart_write((temp.a[3]%100)/10+'0');uart_write(temp.a[3]%10+'0'); uart_write(32);}

              /*  for(i=0; i<26; i++){
                    uart_write(count[i]/10+'0');
                    uart_write(count[i]%10+'0');
                    uart_write(' ');
                }*/
			/*for(k=0; k<p; k++){
					uart_write(reading[k].a/100+'0');
					uart_write((reading[k].a%100)/10+'0');
					uart_write(reading[k].a%10+'0');
					uart_write(32);
					uart_write(reading[k].b/100+'0');
					uart_write((reading[k].b%100)/10+'0');
					uart_write(reading[k].b%10+'0');
					//uart_write(reading[k].b);
					uart_write(32);
					uart_write(reading[k].c/100+'0');
					uart_write((reading[k].c%100)/10+'0');
					uart_write(reading[k].c%10+'0');
					//uart_write(reading[k].c);
					uart_write('\n');
                    */
                    if(pos != 26){
                    uart_write('a'+pos);
                    }
                    _delay_ms(3000);

				//uart_write('\n');
            }
			time1=0;
			for(i=0; i<27; i++)count[i]=0;
			p=0;

			//_delay_ms(500);

		}

	//_delay_ms(2000);
    }
}
