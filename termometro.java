/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacionaj;

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
import panamahitek.Arduino.PanamaHitek_Arduino;

public class termometro extends javax.swing.JFrame {

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
                //temperatura = aux;                         // la temperatura se pondrá en otra variable
                //lbTemperatura.setText("Temperatura: "+aux+" °C"); //lo imprimimos para mostrar el valor de la temperatura 
                lbTemperatura.setText("Temperatura: 27 °C");
                //System.out.println(aux);                  // se imprime en consola para comprobar que sea el mismo dato recibido y que se enviara despúes
                    //System.out.println("Temperatura: "+temperatura);
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbTemperatura = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        lbHora = new javax.swing.JLabel();
        txtMensaje = new javax.swing.JTextField();
        btnHora = new javax.swing.JButton();
        btnMensaje = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lbCaracteres = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Invernadero");
        setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N

        lbTemperatura.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbTemperatura.setText("Temperatura: ");

        btnEnviar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnEnviar.setText("Enviar Temperatura");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        lbHora.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbHora.setText("Hora Actual:");

        txtMensaje.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        txtMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMensajeActionPerformed(evt);
            }
        });
        txtMensaje.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtMensajeKeyReleased(evt);
            }
        });

        btnHora.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnHora.setText("Hora Actual");
        btnHora.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHoraActionPerformed(evt);
            }
        });

        btnMensaje.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnMensaje.setText("Enviar Mensaje");
        btnMensaje.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMensajeActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setText("Mensaje:");

        lbCaracteres.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        lbCaracteres.setText("Caracteres disponibles:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnHora)
                            .addComponent(lbHora)
                            .addComponent(btnEnviar)
                            .addComponent(lbTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lbCaracteres, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnMensaje))
                        .addContainerGap(223, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbTemperatura, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnEnviar)
                .addGap(27, 27, 27)
                .addComponent(lbHora)
                .addGap(18, 18, 18)
                .addComponent(btnHora)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtMensaje, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbCaracteres)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMensaje)
                .addContainerGap(56, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
       Temperatura(); //cuando se precione el boton se llama a el método Temperatura   
        EnviarDatos(lbTemperatura.getText());   //Se envia el texto obtenido atráves del label Temperatura
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnHoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHoraActionPerformed
       HoraFecha(); //cuando se precione el boton se llama a el método HoraFecha   
       EnviarDatos(lbHora.getText()); //Se envia el texto obtenido atráves del label Hora
    }//GEN-LAST:event_btnHoraActionPerformed

    private void btnMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMensajeActionPerformed
        EnviarDatos(txtMensaje.getText());  //cuando se precione el boton Se envia el texto obtenido atráves del txt Mensaje
        txtMensaje.setText("");             //se limpia el txtMensaje
        letras();                           //se llama al método letras
    }//GEN-LAST:event_btnMensajeActionPerformed

    private void txtMensajeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMensajeActionPerformed
       EnviarDatos(txtMensaje.getText());  //Al hacer enter se envia el mensaje escrito
        txtMensaje.setText("");            //se limpia el txtMensaje
    }//GEN-LAST:event_txtMensajeActionPerformed

    private void txtMensajeKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtMensajeKeyReleased
        letras();       //para hacer referencia al método letras
    }//GEN-LAST:event_txtMensajeKeyReleased

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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnHora;
    private javax.swing.JButton btnMensaje;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lbCaracteres;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbTemperatura;
    private javax.swing.JTextField txtMensaje;
    // End of variables declaration//GEN-END:variables
}
