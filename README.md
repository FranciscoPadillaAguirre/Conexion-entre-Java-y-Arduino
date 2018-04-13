# Conexión entre Java y Arduino

### Presenta: Francisco Javier Padilla Aguirre
### Estudiante de Ingeniería en Sistemas Computacionales del Instituto Tecnológico de León
### Materia: Sistemas Programables
***
## Índice
+ [Objetivo](#objetivo)
+ [Descripción](#descripción)
+ [Materiales](#materiales)
+ [Diagrama](#diagrama)
+ [Evidencias](#evidencias)
+ [Código](#código)
+ [Conclusiones](#conclusiones)
***
## Objetivo
> Desarrollar una comunicación entre java y arduino, en el cual atraves de una interfaz en java se puede realizar la comunicación con 
> arduino y este muestre los respectivos mensajes en un LCD
***
## Descripción 
> Se pretende realizar una interfaz en java en la cual el usuario interactue para obtener desde arduino y java mensajes en un LCD donde 
> se muetre la temperatura, la hora y fecha actual, y un mensaje (el que sea que quiera mandar el usuario), todo esto usando la librerpia RXTX 
> para mantener la comunicación de envio y recibo de datos a través del serial y los datos sean leidos en java o arduino conforme corresponda.
> Además de que con botones físicos se obtengan los mismos resultados que con los botones que contiene la interfaz en java.
> De esta manera se demostrará la comunicación entre ambos.
> Como agregado se coloca un LDR el cual a cierta intensidad de luz hará que encienda o se quede apagado un LED y cuando esto suceda se mandara 
> a pantalla del LCD que la Iluminación esta encendida.
> La forma en que se presenta el presente trabajo es realizado sobr una maqueta que simula un invernadero en el cual se mandan los mensajes 
> desplegados en pantalla antes mencionados, además de la iluminación del LED
***
## Materiales
* 1 protoboard
* 1 Sensor de temperaruta LM35
* 1 LCD de 16X2
* 3 pushbotton
* Resistencias de 255 ohms o 330 ohms
* 1 LDR
* 1 LED
* Cable calibre 22
* 1 Arduino UNO
* 1 Laptop 
* IDE de Arduino
* Entorno de desarrollo para JAVA, en este caso se uso Netbeans
***
## Diagrama

***
## Evidencias

***
## Código
> Código Java
~~~
package comunicacionaj;

/*
*Todos estos import lo que hacen es java es importar las librerias para la efectiva ejecución y compilaciónde sintaxis que se usan dentro 
*dentro del programa
*/
import java.io.InputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

public class termometro extends javax.swing.JFrame {  //Inicio de la clase termometro

    private InputStream in = null;              //Variable de entrada de un serial
    private BufferedReader In= null;            //Variable que leera el Buffer de datos
    int temperatura;                            //variable entera
    int aux;                                    //variable entera auxiliar
    Thread timer;                               //variable que se usa para tiempo dentro de ejecución
    int caracteres = 32;                        //Variable entera.
    private OutputStream Output = null;         //Variable que se usara de salida para mandar por serial
    SerialPort serialPort;                      //Variable que servira para obtener el puerto serial
    private final String PORT_NAME = "COM3";    //Variable donde se coloca el número de puerto que se usara
    private static final int TIME_OUT = 2000;   //Variable que contendrá el tiempo de salida
    private static final int DATA_RATE = 9600;  //Variable que inicializa el puerto serial
    private String[] inputLine=new String[5];   //Se hace una variable que es un arreglo el cuál obtendrá los datos de los seriales
    

    public void ArduinoConnection() {           //Método que se usará para crear la conexión con arduino

        CommPortIdentifier portId = null;       //se llama al metodo contenido dentro del jar para hacerlo nulo.
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers(); //se llama al metodo del jar para identificar el numero de puerto que se usara.

        while (portEnum.hasMoreElements()) {    //inicio de ciclo while los elemetos encontras en la identificación del puerto
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement(); //Recorre el elemento que identifica

            if (PORT_NAME.equals(currPortId.getName())) {   //Se identifica que lo obtenido sea dentro del COM3 
                portId = currPortId;                        //Se coloca en porTID el elemento obtenido
                break;                                      //Se rompre el if
            }                                               //fin del if
        }                                                   //fin del while

        if (portId == null) {                               //en caso de que el portID este vacío que no exista una conexión en el puertp 

            System.exit(ERROR);                             //Se manda mensaje de error de que no existe tal conexción 
            return;                                         //retorna la variable
        }                                                   //fin del if

        try {                                               //inicio del try, se intentara realizar las siguientes acciones
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);  //El puerto serial abrira el Id del puerto dentro de esta clase y nombre y tiempo

            serialPort.setSerialPortParams(DATA_RATE,       //El serial toma el serial de comunicaciòn que es el 9600
                    SerialPort.DATABITS_8,                  //Toma los datos en bits
                    SerialPort.STOPBITS_1,                  //detiene los bits
                    SerialPort.PARITY_NONE);                //se enciende la paridad

            Output = serialPort.getOutputStream();          //la salida serà trasmitida por el serial 
            in = serialPort.getInputStream();               //la entrada serà encargada en esa variable

             In= new BufferedReader(new InputStreamReader(serialPort.getInputStream()));    //el buffer leera la entrada en otra interfaz para obtener otra informaciòn 
            timer.resume();                                 //se inicia el timer dentro de este mètodo para cuando sea necesario
        } catch (Exception e) {                             //se termina el try e inicia el catch para hacer válida una excepción

            System.exit(ERROR);                             //Se manda el error en caso de que exista
        }                                                   //fin del catch

    }                                                       //fin del método de conexión
    
    

    private void EnviarDatos(String data) {                 //Inicia el método para enviar datos al arduino

        try {                                               //se inicia un try
            
            Output.write(data.getBytes());                  // se mandara por bytes los bits que se obtengan y serán enviados

        } catch (IOException e) {                           //termina try, inicia catch con su respectiva excepción

            System.exit(ERROR);                             //se manda el error en caso de no ser posible esa transacción
        }                                                   //fin del catch
    }                                                       // fin del método enviar datos
    
    private void Temperatura(){                             //Inicia método que tomará ña temperatura

                try {                                       //inicia el try
                aux = in.read();                            //el puerto nos regresa el valor los sensores
                
                if (aux!=2){                               //en caso de aux no sea diferente de 2 y de -1 
                temperatura = aux;                         // la temperatura se pondrá en otra variable
                lbTemperatura.setText("Temperatura: "+aux+" °C"); //lo imprimimos para mostrar el valor de la temperatura 
                System.out.println(aux);                  // se imprime en consola para comprobar que sea el mismo dato recibido y que se enviara despúes
                   System.out.println("Temperatura: "+temperatura);
            }                                               // fin del if
 
            } catch (Exception e1) {                        // fin del try, inicio de catch con su respectiva excepción
            }                                               //fin del catch
    }                                                       //fin del método temperatura
    
  
    public void HoraFecha(){                                //Inicia método HoraFecha
        Date date = new Date();                             //Se hace la llamada al objeto date
        DateFormat hourdateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");    //se establece el formato que se quiere recibir de fecha y hora
        System.out.println("Hora y fecha: "+hourdateFormat.format(date));           //se imprime en consola el resultado obtenido
        lbHora.setText("Hora-fecha: "+hourdateFormat.format(date));                 //se imprime en un label el resultado obtenido
        
    }                                                        //Fin del método HoraFecha
    
    public void letras() {                                  //Inicia método letras
        caracteres = 32 - txtMensaje.getText().length(); //Indica la cantidad de caracteres disponibles. En el LCD solo se permite imprimir 32 caracteres.

        if (caracteres <= 0) {                          //Si la cantidad de caracteres se ha agotado...
            lbCaracteres.setText("Caracteres disponibles: 0"); //Se imprime que la cantidad de caracteres disponibles es 0
            String cadena = "";                         //Se declara la variable que guardará el //mensaje a enviar
            cadena = txtMensaje.getText();              //Se asigna el //texto del TextField a la variable cadena
            cadena = cadena.substring(0, 32);           //Se evita que por alguna razón la variable contenga más de 32 caracteres, utilizando el substring que crea un string a partir de uno mayor.
            txtMensaje.setText(cadena);                 //se regresa la cadena con 32 caracteres al TextField
        }else {                                         //Si la cantidad de caracteres disponibles es ayor a 0 solamente 
        lbCaracteres.setText("Caracteres disponibles: " + (caracteres)); //se imprimirá la cantidad de caracteres disponibles
}                                                       //fin del else
    }                                                   //fin del método letras
    
    
    public termometro() {                               // inicio del Constructor de la clase termometro
        initComponents();                               // se incializan el método para cargar la interfaz
        setLocationRelativeTo(null);                    //la interfaz se localizará de manera realtiva, o sea cualquier posición
        setResizable(false);                            //que permita colocar algun objeto debajo
        setTitle("Invernadero");                        // se le coloca nombre a la barra superior de la pantalla desplegada

        ((JPanel)getContentPane()).setOpaque(false);    //se crea un panel que no contendra un color de fondo
        ImageIcon uno=new ImageIcon(this.getClass().getResource("/imagenes/Invernaderos.jpg")); // se hace la llamada a la imagen
        JLabel fondo= new JLabel();                     // se crea un objeto para el JLabel
        fondo.setIcon(uno);                             // dentro del label se insertara la imagen que se obtuvo            
        getLayeredPane().add(fondo,JLayeredPane.FRAME_CONTENT_LAYER);   //se posiciona la imagen en todo el fondodel frame 
        fondo.setBounds(0,0,uno.getIconWidth(),uno.getIconHeight());    //la imagen se posiciona en las coordenadas 0,0 
        timer = new Thread(new termometro.ImplementoRunnable());        // el timer será llamado en el método ImplementoRunnable
         timer.start();                                 // se inica el timer
         timer.interrupt();                             // se interrumpe el timer
        letras();                                       // se inicializa el método letras al momento de correr la apliación
        ArduinoConnection();                            //Se inica la conexión del Arduino
    }

    public void  RecibirDatos(){                        //Inicia el método de RecibirDatos
        
        try {                                           //Inicia el try
                String datos;                           //Se crea la variable String datos
                inputLine[0] =In.readLine();            //se llama a inputLine en la posición cero para que almacene el dato que se manda a través del serial
            switch (inputLine[0]) {                     //se crea un switch con el valor que obtenga el arreglo
                case "1":                               //1 es de temperatura
                    datos = inputLine[0];               //datos recibe el valor dentro del arreglo 
                    Temperatura();                      //llamada del método de Temperatura 
                     EnviarDatos("");                   //Se envía un vacío en el método Enviar datos para limpiar el LCD
                    EnviarDatos(lbTemperatura.getText());//Se envia el texto obtenido atráves del label Temperatura
                    System.out.println(datos);          //Se imprime para verificar que dato se recibio
                    break;                              // se rompre el caso
                case "2":                               //2 es la fecha
                    
                    datos = inputLine[0];               //datos recibe el valor dentro del arreglo 
                    HoraFecha();                        //Se llama el método HoraFecha
                    EnviarDatos("");                    //Se envía un vacío en el método Enviar datos para limpiar el LCD
                    EnviarDatos(lbHora.getText());      //Se envia el texto obtenido atráves del label Hora
                    System.out.println(datos);          //Se imprime para verificar que dato se recibio
                    break;                              // se rompre el caso
                case "3":                               //3 es del mensaje que se envia desde teclado
                    datos = inputLine[0];               //datos recibe el valor dentro del arreglo 
                     EnviarDatos("");                   //Se envía un vacío en el método Enviar datos para limpiar el LCD
                    EnviarDatos(txtMensaje.getText());  //Se envia el texto que esta dentro del txt Mensaje
                    txtMensaje.setText("");             //Se limpia el txt
                    letras();                           //se llama al método letras
                    System.out.println(""+datos);       //Se imprime para verificar que dato se recibio
                    break;                              // se rompre el caso
                case "4":                               //4 de luz encencida
                    datos = inputLine[0];               //datos recibe el valor dentro del arreglo 
                     EnviarDatos("");                   //Se envía un vacío en el método Enviar datos para limpiar el LCD
                    EnviarDatos("Iluminacion: Encendida");  //Se envia el texto dentro de los paréntesis
                    System.out.println(""+datos);       //Se imprime para verificar que dato se recibio
                    break;                              // se rompre el caso
                default:                                //en caso de que sea otro número 
                    break;                              // se rompre el caso
            }                                           //fin del switch

            } catch (Exception e) {                     //fin del try e incio del catch con su excepción
                System.err.println(e.toString());       //se imprime el error en caso de que exista
            }                                           //fin del catch
        
    }                                                   //fin del método de RecibirDatos
    
    private class ImplementoRunnable implements Runnable{   //Inicio del método privado ImplementoRunnable 
        public void run() {                                 //este correra cuando corra la aplicación con este método
            while(true){                                    //mientras sea verdadero, un ciclo que siempre existira
                try {                                       //inicio try
                    Thread.sleep(100);                      //inicio del timer y tendrá un tiempo de espera de 100 milisegundos
                RecibirDatos();                             // se correra a la par durante la corrida el método RecibirDatos
    
            } catch (Exception e1) {                        //fin del try e incio del catch con su excepción
            }}}}                                            // fin del catch, fin del while, fin del run, fin del método ImplementoRunnable

    /**
     * Inicio del método donde se hace la llamada a cada componente usado en la interfaz y la posición que toman
     * Fue creado con el asistente de Netbeans 
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        lbTemperatura = new javax.swing.JLabel();     //Label es una etiqueta
        btnEnviar = new javax.swing.JButton();        // se asigna el boton     
        lbHora = new javax.swing.JLabel();            //se asigna otra etiqueta
        txtMensaje = new javax.swing.JTextField();    // se asigna un campo de texto
        btnHora = new javax.swing.JButton();          // se asigna un boton
        btnMensaje = new javax.swing.JButton();       //se asigna un boton
        jLabel1 = new javax.swing.JLabel();           //se asigna una etiqueta
        lbCaracteres = new javax.swing.JLabel();      //se asigna una etiqueta

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); // se cierran todas las operaciones una vez cerrada la ventana
        setTitle("Invernadero");                                             //Titulo que tendra en parte superior la ventana
        setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N                // el tipo de letra asignado y el tamaño de esta

        lbTemperatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N  //tamaño fuente que tiene la etiqueta temperatura
        lbTemperatura.setText("Temperatura: ");                               //texto que se le coloca a la etiqueta

        btnEnviar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N      //tamaño fuente de la letra del boton
        btnEnviar.setText("Enviar Temperatura");                              //texto que contiene el boton
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {     //se dice que tendra una acción de escucha al boton
            public void actionPerformed(java.awt.event.ActionEvent evt) {     // que se le asignara un accción una vez presionado
                btnEnviarActionPerformed(evt);                                //todo eso al botn Enviar
            }                                                                 // fin del action perfomrmed
        });                                                                   //fin del action listener

        lbHora.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N         //tipo de fuenta y tamaño a ala etiqueta
        lbHora.setText("Hora Actual:");                                       //texto contenido en la etiqueta

        txtMensaje.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N  //tipo de fuente y tamaño que contendra el campo de texto
        txtMensaje.addActionListener(new java.awt.event.ActionListener() {      //se crea el metodo de escucha en el cuadro de texto
            public void actionPerformed(java.awt.event.ActionEvent evt) {       //se inicia el metodo de action performed
                txtMensajeActionPerformed(evt);                                 //el evento de acción se asigan al campo de texto
            }                                                                   //fin del action performed
        });                                                                     //fin el action listener
        txtMensaje.addKeyListener(new java.awt.event.KeyAdapter() {             //el campo de texto tendra una llave de escucha que permita que cuando se este en uso haga llamda a algo o un metodo
            public void keyReleased(java.awt.event.KeyEvent evt) {              // se crea el metodo key released
                txtMensajeKeyReleased(evt);                                     // se asigna el evento al campo de texto
            }                                                                   // fin de método key released
        });                                                                     //fin del key listener

        btnHora.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N          //tipo de feunte y tamaño que obtiene el boton
        btnHora.setText("Hora Actual");                                         //texto que contiene el boton
        btnHora.addActionListener(new java.awt.event.ActionListener() {         //se agrega el método de escucha al boton 
            public void actionPerformed(java.awt.event.ActionEvent evt) {       //se agrega una acción
                btnHoraActionPerformed(evt);                                    // la cual se implementa en el boton
            }                                                                   //fin del método action performed
        });                                                                     //fin del action listener

        btnMensaje.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N       //tipo y tamaño fuente del boton Mensaje
        btnMensaje.setText("Enviar Mensaje");                                   //se agrega el texto al boton
        btnMensaje.addActionListener(new java.awt.event.ActionListener() {      //Se agrega el método de escucha al boton
            public void actionPerformed(java.awt.event.ActionEvent evt) {       //Se agrega el método action performed
                btnMensajeActionPerformed(evt);                                //se agrega la acción al boton
            }                                                                   //fin del action performed
        });                                                                     //fin del action listener

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N          //Tipo y tamaño fuente de la etiqueta
        jLabel1.setText("Mensaje:");                                            //texto contenido en la etiqueta

        lbCaracteres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N     //tipo y tamaño fuente de la etiqueta
        lbCaracteres.setText("Caracteres disponibles:");                        //texto contenido en la etiqueta

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane()); //layout que se le agregara al panel
        getContentPane().setLayout(layout);                                             //se agrega el layot
        layout.setHorizontalGroup(                                                      //en el eje horizontal 
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)       //se llama al group Layout
            .addGroup(layout.createSequentialGroup()                                    //se agrega al grupo la secuancia
                .addGap(62, 62, 62)                                                     //las coordenadad que contendra el panel
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //el layout crea la alineación
                    .addGroup(layout.createSequentialGroup()                                    // se agrega al grupo esa alinaeción
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //se vuelve a crear la alineación
                            .addComponent(btnHora)                                    //se agrega el boton
                            .addComponent(lbHora)                                     //se agrega una etiqueta
                            .addComponent(btnEnviar)                                  //se agrega un boton
                            .addComponent(lbTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)) //se agrega el tamaño de la etiqueta
                        .addGap(0, 0, Short.MAX_VALUE))                               // se colocan las coordenadas de la etiqueta
                    .addGroup(layout.createSequentialGroup()                          // el tipo de secuencia que seguira
                        .addGap(2, 2, 2)                                              //las coordenadas
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING) //se crea que el grupo sea paralelo
                            .addComponent(lbCaracteres, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)  //se agrega la etiqueta y el tamaño
                            .addComponent(btnMensaje))                                //se agrega el boton
                        .addContainerGap(223, Short.MAX_VALUE))))                     //medidas del boton
            .addGroup(layout.createSequentialGroup()                                  //se agrega al layout el boton
                .addGap(7, 7, 7)                                                      //en las coordenadas descripta
                .addComponent(jLabel1)                                                //se agrega la etiqueta
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)//la preferencia de dicha etiqueta
                .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE) //se coloca en el tamaño señalado
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) //el contenedor toma el tamaño por default
        );                                                                              //fin de la configuración horizontal del layout
        layout.setVerticalGroup(                                                        //ahora se configurara la parte vertical del layout
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)       //la alineación que tendrá
            .addGroup(layout.createSequentialGroup()                                    // sera de un grupo secuencial
                .addContainerGap()                                                      //el tipo de contenedor que se asigna
                .addComponent(lbTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)  //el tamño establecido para la etiqueta
                .addGap(26, 26, 26)                                                     //las coordenadas donse se coloca
                .addComponent(btnEnviar)                                                //se agrega el boton
                .addGap(27, 27, 27)                                                     //las coordenadas donde se coloca el boton
                .addComponent(lbHora)                                                   //se agrega una etiqueta
                .addGap(18, 18, 18)                                                     //se agregan las coordenadas donse se coloca
                .addComponent(btnHora)                                                  // se agrega el boton
                .addGap(18, 18, 18)                                                     //las coordenadas donde se coloca
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE) //la alineación que tendra el layout en paralelo
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE) //el tamaño agregado para el campo de texto
                    .addComponent(jLabel1))                                             //se agrega una etiqueta
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)  //el tipo de estilo de layout
                .addComponent(lbCaracteres)                                             //se agrega la etiqueta caracteres
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)    //con su respectivo estilo de layout
                .addComponent(btnMensaje)                                               //se agrega el boton mensaje
                .addContainerGap(56, Short.MAX_VALUE))                                  //el contenedor en el que se asignara el tamaño que contiene
        );                                                                              //fin del layout vertical

        pack();                                                                         //el paquete de toda la interfaz
    }// </editor-fold>                                                                  //fin del método initcomponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {  //inicio del método de accion a un boton                                          
       Temperatura();                           //cuando se precione el boton se llama a el método Temperatura   
        EnviarDatos(lbTemperatura.getText());   //Se envia el texto obtenido atráves del label Temperatura
    }                                           //fin del action performed                                   

    private void btnHoraActionPerformed(java.awt.event.ActionEvent evt) {  //inicio del método de accion a un boton                                       
       HoraFecha();                   //cuando se precione el boton se llama a el método HoraFecha   
       EnviarDatos(lbHora.getText()); //Se envia el texto obtenido atráves del label Hora
    }                                 //fin del action performed                                  

    private void btnMensajeActionPerformed(java.awt.event.ActionEvent evt) {    //inicio del método de accion a un boton                                        
        EnviarDatos(txtMensaje.getText());  //cuando se precione el boton Se envia el texto obtenido atráves del txt Mensaje
        txtMensaje.setText("");             //se limpia el txtMensaje
        letras();                           //se llama al método letras
    }                                       //fin del action performed                                       

    private void txtMensajeActionPerformed(java.awt.event.ActionEvent evt) {  //inicio del método de accion al campo de texto                                         
       EnviarDatos(txtMensaje.getText());  //Al hacer enter se envia el mensaje escrito
        txtMensaje.setText("");            //se limpia el txtMensaje
    }                                      //fin del action performed                                  

    private void txtMensajeKeyReleased(java.awt.event.KeyEvent evt) { //método que mientras se escriba en el campo se llamara a lo que este dentro del método                                      
        letras();       //para hacer referencia al método letras
    }                   //fin del Key Released                   

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {        //Inicia el método main
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*
        Toda esta sintaxis dentro del try catch permite la efectiva corrida del programa en momento de ejecución
        */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(termometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(termometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(termometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(termometro.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {    //se invoca un evento que se correra hasta que se interrumpa el programa o cierre la aplicación
            public void run() {                             //se inica el método run
                new termometro().setVisible(true);          //se inica la interfaz termometro y que sea visible
            }                                               //fin del método run
        });                                                 //se cierra el evento con el Runneable
    }                                                       //fin del main

    //las variables de cada componente de la interfaz
    // Variables declaration - do not modify                     
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnHora;
    private javax.swing.JButton btnMensaje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbCaracteres;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbTemperatura;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration                   
}
~~~

>Código Arduino
~~~
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
 if (Leersensor<30){                //Si el valor obtenido es menor de 40 entra a las siguientes instrucicones
 digitalWrite(12,HIGH);             //Enciende el LED
 Serial.println("4");               //Se imprime en el serial el número 4
 }                                  //fin del if
 else digitalWrite(12,LOW);         //En caso que sea mayor a 40 el dato obtenido el LED quedara apagado           
 delay(1000);                       //Se hace una espera de un segundo
    
MESS();                             //Se realiza esta llamada al método MESS dado que java mandara información para imprimir al LCD
}                                   //Fin de método LUZ
~~~
***
## Conclusiones
> En el presente trabajo se aprecio como se realiza la comunicación entre arduino y java, es un trabajo en lo particular que deja gran 
> aprendizaje de como hacer la comunicación entre ambas plataformas y como interacturar con los datos que se transmitan a través de
> puerto serial que es por donde se mantienen funcinando ambos, parece un poco complejo la practica y bastante extensa por la cantidad de
> código presentado, pero si se aprecia bien el código no es tan complejo ya que el mayor trabajo de esto va dentro de la librería, lo que si
> genera complicaciones fue al momento de mandar datos a través de los botones ya que java no reconocia bien los caracteres dado que el serial 
> el caracter que se enviaba al apretar el boton pero además se iba incluido un [] el cual generaba complicaciones con la comunicación, para 
> resolver eso se metio todo dentro de un if con Serial.available() > 0 el cual solo entra si lo que se encuentra son datos que se pueda
> leer, de esta manera se resolvio en parte, aunque en ocasiones se mandaba símbolos raros al LCD, lo cuál era ruido electrico y para mitigar
> ese ruido se colocaron capacitores de ceramica y electroliticos para eliminar esos posibles ruidos.
***
