/*El presente código tiene como fin establecer la comunicación entre arduino y java (específicamente se manda y recibe información entre uno y otro y viseversa),
 * se mandara la temperatura, hora y fecha, y mensajes a un LCD esl cuál lo mostrará en pantalla.
 * Se ha de aclarar que si bien puede dar problemas las interferencias y ruidos dentro del proto o placa impresa dado que cada componente que se usa en especial Sensor LM35,
 * LCD y arduino son los que provocan esa como coloquialmente se dice "ruido o basura electrica", para ello es recomendable usar disipadores o capacitores para poder mitigar
 * un poco ese ruido provocado.
 * Todo esto representado dinámicamente en un invernadero a escala para así dar un ejemplo de su posible aplicación
 * Nota: Cabe mencionar que las variables y metodos estan escritos o español y con solo principio de comodidad en lo particular, ya que de cierta manera a mi criterio es mejor referenciar
 * a las cosas por su nombre y en ocasiones es mejor usar inglés, un claro ejemplo button en lugar de interruptor, dado que cuando se compra el componente comunmente se pide como pushbutton
 * en lugar de interruptor, e interruptor se puede cofundir como alguna interrupción de el programa y no referencia al componente, algunas otras son abreviadas ya que si se coloca el nombre s
 * sería muy largo y tedioso ver demasiado código.
 */
#include <LiquidCrystal.h>

LiquidCrystal lcd(8, 9, 4, 5, 6, 7);    //Serán los pines del arduino que serán necesarios para mandar a pantalla del LCD los mensajes
String Mensaje="";                      //Mensaje que se va a imprimir
int Leersensor;                          //Pin análogo en espera
byte PIN_SENSOR = A0;                   //La entrada analógica en la cual se recibira la señal del sensor de temperatura
int dato_serial =0;                     // para los datos de entrada seriales
float C;                                //variable flotante en la cuál se realizara una operación para obtener la temperatura
int temp;                               //es otra variable la cual enviaremos a java para señalar la temperatura.

const int buttonPin = 3;                //Entrada digital 3 la cual será usada para un boton para simular uno de la interfaz
const int buttonPin2 = 2;               //Entrada digital 2 la cual será usada para un boton para simular uno de la interfaz
const int buttonPin3= 10;               //Entrada digital 10 la cual será usada para un boton para simular uno de la interfaz
//const int buttonPin4= 11;


int buttonState = 0;                    //Variable que usara para el estado del boton seleccionado
int buttonState2 = 0;                   //Variable que usara para el estado del boton seleccionado
int buttonState3 = 0;                   //Variable que usara para el estado del boton seleccionado
//int buttonState4 = 0;


void setup() {                          //inicio del método setup
pinMode(12,OUTPUT);                     //El pin12 será de salida
lcd.begin(16, 2);                       //Se inicia el LCD
pinMode(buttonPin, INPUT);              //Button que se inicializa que será de entrada
pinMode(buttonPin2, INPUT);             //Button que se inicializa que será de entrada
pinMode(buttonPin3, INPUT);             //Button que se inicializa que será de entrada
//pinMode(buttonPin4, INPUT);
Serial.begin(9600);                     //Se inicia la comunicación serial
}                                       //fin del método setup



void loop() {                           //Inicio del método loop
  buttonState = digitalRead(buttonPin); //igualación de la lectura digital a la variable entera
  buttonState2 = digitalRead(buttonPin2);//igualación de la lectura digital a la variable entera
  buttonState3 = digitalRead(buttonPin3);//igualación de la lectura digital a la variable entera
  
    
  if(buttonState == HIGH) {             //Comparación de si el estado de ese Button es de que le llega una señal, para ejemplificar un 1
    Serial.println('1');                // Se manda al serial el dato 1
     SENS();                            //Se llama al método para que realice lo que se contiene dentro de este.
  }
  else if(buttonState2 == HIGH){        // En caso contrario se compara de si el estado de ese Button es de que le llega una señal, para ejemplificar un 1
    Serial.println('2');                // Se manda al serial el dato 2
    MESS();                             //Se llama al método para que realice lo que se contiene dentro de este.
  }
  else if(buttonState3 == HIGH){       // En caso contrario se compara de si el estado de ese Button es de que le llega una señal, para ejemplificar un 1
    Serial.println('3');              // Se manda al serial el dato 3
    MESS();                           //Se llama al método para que realice lo que se contiene dentro de este.
  }else{                              //En caso de que no se cumpla ninguna de las anteriores entramos en las siguientes sentencias.
     //Serial.println('0');             // se manda al serial el dato 3
    MESS();                           //Se llama al método para que realice lo que se contiene dentro de este. Cabe destacar que ningún metodo dentro de este else entra si recibir alguna señal de la interfaz
    SENS();                           //Se llama al método para que realice lo que se contiene dentro de este. Cabe destacar que ningún metodo dentro de este else entra si recibir alguna señal de la interfaz
    Luz();                            //Se llama al método para que realice lo que contiene dentro.
  }
 
}                                     // fin del método loop

void SENS(){                          //Inicio del método SENS
  if (Serial.available() > 0) {       //Condición que dice que mientras halla caracteres que se puedan leer en el puerto Serie se ejecutara lo que haya dentro de esta condición
  C = (5.0 * analogRead(PIN_SENSOR) * 100.0) / 1023; //Esta operación permite obtener el valor de la temperatura
  temp=C;                             //Se iguala la variable temp a C, aqui el valor obtenido en C se pasa a temp
  Serial.write(temp);               //Se imprime en el serial el valor de temp.
  }                                   //Fin de la condición
delay(100);                           //Se hace una espera de 100 milisegundos por si se vuelve a ejecutar enseguida el mismo método

}                                     //Fin del método SENS


void MESS(){
int caracteres=0;                     //Cantidad de caracteres, esta variable será usada para obtener esa cantidad que se llega de java por el Serial
while (Serial.available()>0){        //Ciclo que se ejecutará mientras halla caracteres que se puedan leer en el puerto Serie
                                     //En la comunicación serial se envían los mensajes caracter a caracter, por lo que tenemos que leerlos 1 a 1
                                    //Serial.available() indica la cantidad de caracteres disponibles. Este while no se romperá hasta que se halla leido todo.
                                    
Mensaje=Mensaje+Decimal_to_ASCII(Serial.read()); //Se lee el caracter de entrada, se transforma desde ASCII. Se acumulan los caracteres en la variable mensaje

}                                     //Fin del while.
caracteres=Mensaje.length();        //Se lee la cantidad de caracteres del mensaje que viene desde Java
if (caracteres>16){                //Si hay mas de 16 caracteres...
if (Mensaje!=""){                 //Si la variable mensaje NO está vacia...
lcd.clear();                      //Se limpia el LCD
lcd.print(Mensaje.substring(0,16)); //Se imprime los primeros 16 caracteres en el renglón 1
lcd.setCursor(0,2);                 //Se salta al rengón 2
lcd.print(Mensaje.substring(16,caracteres)); //Se imprime los caracteres que hallan sobrado del primer renglón
}                                   //Fin del if donde se hace la comparación de que Mensaje no está vacío
}                                   //Fin del if donde se compara que haya más de 16 caracteres.

else{                              //Si la cantidad de caracteres no es mayor a 16...
if (Mensaje!=""){                  //Si la variable mensaje NO está vacia...
lcd.clear();                       //Se limpia el LCD
lcd.print(Mensaje);               //Se imprime todo en el primer renglón
}                                 //Fin del if donde se hace la comparación de que Mensaje no está vacío                                                       
}                                 //Fin del else donde se iniciaba si la cantidad de caracteres no es mayor a 16

delay(1000);                     //Se espera un segundo, en caso de volver a espera del pueto Serial para no saturarlo 

Mensaje="";                     //Se limpia la variable mensaje. Esto evitará que se imprima algo en el LCD si no se ha enviado algún mensaje.
}                               //Fin del método MESS

char Decimal_to_ASCII(int entrada){ //Dentro de este método lo que se hace es obtener el caracter de llegada y transformarlo a lenguaje común para el humano
char salida=' ';                    // el primer caracter es si no hay nada.
switch(entrada){                    // se hace un switch con todas los caracteres ASCII posibles.
case 32:                            // se inicia en el 32 dado que desde este punto en la tabla de caracteres ASCII inician los símbolos, y en especial el espacio inicia en la posicón 32 en la tabla ASCII 
salida=' ';                         //En el 32 el símbolo es espacio.
break;                              //Se rompe el caso.
case 33:                            //Posición 33 de la tabla ASCII y en este caso 33 
salida='!';                         //Se el símbolo ! en esta posición
break;                              //Se rompe el caso.
case 34:                            //Posición 34 de la tabla ASCII                          
salida='"';                         //El símbolo " (comillas)
break;                              //Se rompe el caso.
case 35:                            //Posición 35 de la tabla ASCII
salida='#';                         //Símbolo #
break;                              //Se rompe el caso.
case 36:                            //Posición 36 de la tabla ASCII
salida='$';                         //Símbolo $
break;                              //Se rompe el caso.
case 37:                            //Posición 37 de la tabla ASCII
salida='%';                         //Símbolo %
break;                              //Se rompe el caso.
case 38:                            //Posición 38 de la tabla ASCII
salida='&';                         //Símbolo &
break;                              //Se rompe el caso. 
case 39:                            //Posición 39 de la tabla ASCII
salida=' ';                         //Símbolo de apóstrofo ' '
break;                              //Se rompe el caso.
case 40:                            //Posición 40 de la tabla ASCII
salida='(';                         //Símbolo (
break;                              //Se rompe el caso.
case 41:                            //Posición 41 de la tabla ASCII
salida=')';                         //Símbolo )
break;                              //Se rompe el caso.
case 42:                            //Posición 42 de la tabla ASCII
salida='*';                         //Símbolo *
break;                              //Se rompe el caso.
case 43:                            //Posición 43 de la tabla ASCII
salida='+';                         //Símbolo +
break;                              //Se rompe el caso.
case 44:                            //Posición 44 de la tabla ASCII
salida=',';                         //Símbolo ,
break;                              //Se rompe el caso.
case 45:                            //Posición 45 de la tabla ASCII
salida='-';                         //Símbolo -
break;                              //Se rompe el caso.
case 46:                            //Posición 46 de la tabla ASCII
salida='.';                         //Símbolo .
break;                              //Se rompe el caso.
case 47:                            //Posición 47 de la tabla ASCII
salida='/';                         //Símbolo /
break;                              //Se rompe el caso.
case 48:                            //Posición 48 de la tabla ASCII
salida='0';                         //Símbolo 0
break;                              //Se rompe el caso.
case 49:                            //Posición 49 de la tabla ASCII
salida='1';                         //Símbolo 1
break;                              //Se rompe el caso.
case 50:                            //Posición 50 de la tabla ASCII
salida='2';                         //Símbolo 2 
break;                              //Se rompe el caso.
case 51:                            //Posición 51 de la tabla ASCII
salida='3';                         //Símbolo 3
break;                              //Se rompe el caso.
case 52:                            //Posición 52 de la tabla ASCII
salida='4';                         //Símbolo 4
break;                              //Se rompe el caso.
case 53:                            //Posición 53 de la tabla ASCII
salida='5';                         //Símbolo 5
break;                              //Se rompe el caso.
case 54:                            //Posición 54 de la tabla ASCII
salida='6';                         //Símbolo 6
break;                              //Se rompe el caso.
case 55:                            //Posición 55 de la tabla ASCII
salida='7';                         //Símbolo 7
break;                              //Se rompe el caso.
case 56:                            //Posición 56 de la tabla ASCII
salida='8';                         //Símbolo 8
break;                              //Se rompe el caso.
case 57:                            //Posición 57 de la tabla ASCII
salida='9';                         //Símbolo 9
break;                              //Se rompe el caso.
case 58:                            //Posición 58 de la tabla ASCII
salida=':';                         //Símbolo :
break;                              //Se rompe el caso.
case 59:                            //Posición 59 de la tabla ASCII
salida=';';                         //Símbolo ;
break;                              //Se rompe el caso.
case 60:                            //Posición 60 de la tabla ASCII
salida='<';                         //Símbolo <
break;                              //Se rompe el caso.
case 61:                            //Posicón 61 de la tabla ASCII
salida='=';                         //Símbolo =
break;                              //Se rompe el caso.
case 62:                            //Posición 62 de la tabla ASCII
salida='>';                         //Símbolo >
break;                              //Se rompe el caso.
case 63:                            //Posición 63 de la tabla ASCII
salida='?';                         //Símbolo ?
break;                              //Se rompe el caso.
case 64:                            //Posición 64 de la tabla ASCII
salida='@';                         //Símbolo @
break;                              //Se rompe el caso.
case 65:                            //Posición 65 de la tabla ASCII
salida='A';                         //Símbolo A
break;                              //Se rompe el caso.
case 66:                            //Posición 66 de la tabla ASCII
salida='B';                         //Símbolo B
break;                              //Se rompe el caso.
case 67:                            //Posición 67 de la tabla ASCII
salida='C';                         //Símbolo C
break;                              //Se rompe el caso.
case 68:                            //Posición 68 de la tabla ASCII
salida='D';                         //Símbolo D
break;                              //Se rompe el caso.
case 69:                            //Posición 69 de la tabla ASCII
salida='E';                         //Símbolo E
break;                              //Se rompe el caso.
case 70:                            //Posición 70 de la tabla ASCII
salida='F';                         //Símbolo F
break;                              //Se rompe el caso.
case 71:                            //Posición 71 de la tabla ASCII
salida='G';                         //Símbolo G
break;                              //Se rompe el caso.
case 72:                            //Posición 72 de la tabla ASCII
salida='H';                         //Símbolo H
break;                              //Se rompe el caso.
case 73:                            //Posición 73 de la tabla ASCII
salida='I';                         //Símbolo 73
break;                              //Se rompe el caso.
case 74:                            //Posición 74 de la tabla ASCII
salida='J';                         //Símbolo J
break;                              //Se rompe el caso.
case 75:                            //Posición 75 de la tabla ASCII
salida='K';                         //Símbolo K
break;                              //Se rompe el caso.
case 76:                            //Posición 76 de la tabla ASCII
salida='L';                         //Símbolo L
break;                              //Se rompe el caso.
case 77:                            //Posición 77 de la tabla ASCII
salida='M';                         //Símbolo M
break;                              //Se rompe el caso.
case 78:                            //Posición 78 de la tabla ASCII
salida='N';                         //Símbolo N
break;                              //Se rompe el caso.
case 79:                            //Posición 79 de la tabla ASCII
salida='O';                         //Símbolo O
break;                              //Se rompe el caso.
case 80:                            //Posición 80 de la tabla ASCII
salida='P';                         //Símbolo P
break;                              //Se rompe el caso.
case 81:                            //Posición 81 de la tabla ASCII
salida='Q';                         //Símbolo Q
break;                              //Se rompe el caso.
case 82:                            //Posición 82 de la tabla ASCII
salida='R';                         //Símbolo R
break;                              //Se rompe el caso.
case 83:                            //Posición 83 de la tabla ASCII
salida='S';                         //Símbolo S
break;                              //Se rompe el caso.
case 84:                            //Posición 84 de la tabla ASCII
salida='T';                         //Símbolo T
break;                              //Se rompe el caso.
case 85:                            //Posición 85 de la tabla ASCII
salida='U';                         //Símbolo U
break;                              //Se rompe el caso.
case 86:                            //Posición 86 de la tabla ASCII
salida='V';                         //Símbolo V
break;                              //Se rompe el caso.
case 87:                            //Posición 87 de la tabla ASCII
salida='W';                         //Símbolo W
break;                              //Se rompe el caso.
case 88:                            //Posición 88 de la tabla ASCII
salida='X';                         //Símbolo X
break;                              //Se rompe el caso.
case 89:                            //Posición 89 de la tabla ASCII
salida='Y';                         //Símbolo Y
break;                              //Se rompe el caso.
case 90:                            //Posición 90 de la tabla ASCII
salida='Z';                         //Símbolo Z
break;                              //Se rompe el caso.
case 91:                            //Posición 91 de la tabla ASCII
salida='[';                         //Símbolo [
break;                              //Se rompe el caso.
case 92:                            //Posición 92 de la tabla ASCII
salida=' ';                        //Símbolo \  
break;                             //Se rompe el caso.
case 93:                           //Posición 93 de la tabla ASCII
salida=']';                        //Símbolo ]
break;                             //Se rompe el caso.
case 94:                           //Posición 94 de la tabla ASCII
salida='^';                        //Símbolo ^
break;                             //Se rompe el caso.
case 95:                          //Posición 95 de la tabla ASCII                           
salida='_';                       //Símbolo _
break;                            //Se rompe el caso.
case 96:                          //Posición 96 de la tabla ASCII
salida='`';                       // Símbolo `
break;                            //Se rompe el caso.
case 97:                          //Posición 97 de la tabla ASCII
salida='a';                       //Símbolo a
break;                            //Se rompe el caso.
case 98:                          //Posición 98 de la tabla ASCII
salida='b';                       //Símbolo b
break;                            //Se rompe el caso.
case 99:                          //Posición 99 de la tabla ASCII
salida='c';                       //Símbolo c
break;                            //Se rompe el caso.
case 100:                         //Posición 100 de la tabla ASCII
salida='d';                       //Símbolo d
break;                            //Se rompe el caso.
case 101:                         //Posición 101 de la tabla ASCII
salida='e';                       //Símbolo e
break;                            //Se rompe el caso.
case 102:                         //Posición 102 de la tabla ASCII
salida='f';                       //Símbolo f
break;                            //Se rompe el caso.
case 103:                         //Posición 103 de la tabla ASCII
salida='g';                       //Símbolo g
break;                            //Se rompe el caso.
case 104:                         //Posición 104 de la tabla ASCII
salida='h';                       //Símbolo h
break;                            //Se rompe el caso.
case 105:                         //Posición 105 de la tabla ASCII
salida='i';                       //Símbolo i
break;                            //Se rompe el caso.
case 106:                         //Posición 106 de la tabla ASCII
salida='j';                       //Símbolo j
break;                            //Se rompe el caso.
case 107:                         //Posición 107 de la tabla ASCII
salida='k';                       //Símbolo k
break;                            //Se rompe el caso.
case 108:                         //Posición 108 de la tabla ASCII
salida='l';                       //Símbolo l
break;                            //Se rompe el caso.
case 109:                         //Posición 109 de la tabla ASCII
salida='m';                       //Símbolo m
break;                            //Se rompe el caso.
case 110:                         //Posición 110 de la tabla ASCII
salida='n';                       //Símbolo n
break;                            //Se rompe el caso.
case 111:                         //Posición 111 de la tabla ASCII
salida='o';                       //Símbolo o
break;                            //Se rompe el caso.
case 112:                         //Posición 112 de la tabla ASCII
salida='p';                       //Símbolo p
break;                            //Se rompe el caso.
case 113:                         //Posición 113 de la tabla ASCII
salida='q';                       //Símbolo q
break;                            //Se rompe el caso.
case 114:                         //Posición 114 de la tabla ASCII
salida='r';                       //Símbolo r
break;                            //Se rompe el caso.
case 115:                         //Posición 115 de la tabla ASCII
salida='s';                       //Símbolo s
break;                            //Se rompe el caso.
case 116:                         //Posición 116 de la tabla ASCII
salida='t';                       //Símbolo t
break;                            //Se rompe el caso.
case 117:                         //Posición 117 de la tabla ASCII
salida='u';                       //Símbolo u
break;                            //Se rompe el caso.
case 118:                         //Posición 118 de la tabla ASCII
salida='v';                       //Símbolo v
break;                            //Se rompe el caso.
case 119:                         //Posición 119 de la tabla ASCII
salida='w';                       //Símbolo w
break;                            //Se rompe el caso  .
case 120:                         //Posición 120 de la tabla ASCII
salida='x';                       //Símbolo x
break;                            //Se rompe el caso.
case 121:                         //Posición 121 de la tabla ASCII
salida='y';                       //Símbolo y
break;                            //Se rompe el caso.
case 122:                         //Posición 122 de la tabla ASCII
salida='z';                       //Símbolo z
break;                            //Se rompe el caso.
case 123:                         //Posición 123 de la tabla ASCII
salida='{';                       //Símbolo {
break;                            //Se rompe el caso.
case 124:                         //Posición 124 de la tabla ASCII
salida='|';                       //Símbolo |
break;                            //Se rompe el caso.
case 125:                         //Posición 125 de la tabla ASCII
salida='}';                       //Símbolo }
break;                            //Se rompe el caso.
case 126:                         //Posición 126 de la tabla ASCII
salida='~';                       //Símbolo ~ 
break;                            //Se rompe el caso.
}                                 // se termina el switch
return salida;                    // se regresa la variable salida
}                                 //Se termina el metodo de conversión de caracteres a símbolos ASCII

void Luz(){                         //Inicio del método LUZ
 Leersensor=analogRead(1);          //Instrucción para obtener dato analógico
 if (Leersensor<30)                 //Si el valor obtenido es menor de 40 entra a las siguientes instrucicones
 {
 digitalWrite(12,HIGH);             //Enciende el LED
 Serial.println("4");               //Se imprime en el serial el número 4
 }                                  //fin del if
 else digitalWrite(12,LOW);         //En caso que sea mayor a 40 el dato obtenido el LED quedara apagado           
 delay(1000);                       //Se hace una espera de un segundo
    
MESS();                             //Se realiza esta llamada al método MESS dado que java mandara información para imprimir al LCD
}                                   //Fin de método LUZ

