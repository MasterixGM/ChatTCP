
# ChatTCP

Una aplicacion de chat hecha en javaFX usando metodos TCP para el envio de mensajes

Aqui una breve Introduccion de las clases y lo que hace cada una:

# Servidor

La clase Servidor es una clase principal la cual es la principal encargada de manejar la logica de entrada de clientes al ChatTCP esta clase contiene

    - HashMap llamado usuarios para mantere el mapeo de usuarios conectados y sus flujos de salida via ObjectOutputStream


    - Main que gestiona la configuracion del puerto asi como el numero de usuarios que se permiten al servidor.


    - Handler la cual gestiona las conexiones de cada clientes a su vez tiene un metodo run que realiza el proceso de inicio de sesion y maneja la comunicacion con el cliente.


    - Tres procesos los cuales son procesoOutputCliente, procesoDesconectar y procesoMensaje.


    - Metodo cerrar que se encarga de cerrar los flujos y el socket adecuadamente.

# Cliente

La clase cliente representa un cliente que se conecta al servidor esta clase contiene

    - Listener la cual es una clase que emplea un hilo que "Escucha" los mensajes del servidor de manera asincrona a su vez cuando detecta un mensaje de desconexion detiene el hilo y se cierra la conexion del cliente.

# Main

La clase principal que gestiona la apertura del cliente y las inicializaciones del escenario que va a ser la parte grafica.

# Controladores

En esta aplicacion tenemos tres controladores los cuales se encargan de gestionar la parte grafica del programa

- ControladorChat

    Esta clase contiene    
    los metodos para poder 
    procesar mensajes asi 
    como enviarlos a la 
    parte grafica


    - enviarMensaje se llama cuando el usuario presiona el boton de enviar mensaje, crea un objeto mensaje y lo envia al servidor a traves del cliente

    - procesarMensaje se llama cuando se recibe un mensaje del servidor y agrega entonces el mensaje al chatBox (VBox) para mostrarlo por pantalla

- ControladorLogin

    Esta clase contiene metodos para poder manejar la entrada de los usuarios a la aplicacion mediante parte grafica


    - inicioSesion se llama cuando el usuario intenta iniciar sesion, procesa la entrada del mismo y cambia la ventada a la del chat en caso de que sea exitosa la conexion.

    - pasoVentanaChat se usa para cambiar a la ventana del chat despues de iniciar sesion correctamente

    - pasoUsuario se usa para obtener los datos del usuario y crear una instancia del cliente para la conexion al servidor

    - login realiza el proceso de inicio de sesion enviando el objeto Usuario al servidor y esperando una respuesta

- ControladorTexto


    Esta Clase solo es un Controlador vacio que se conecta con el mensaje de la parte grafica, se pueden implementar funcionalidades como filtrado de palabras, pasar a minusculas entre otras.

# Otras clases

- Mensaje

    Esta clase es la definicion del objeto mensaje contiene:


    - String Mensaje

    - Usuario

    - Tipo de mensaje

A su vez implementa serializable lo cual permite que los objetos de esta clase se puedan serializar para enviarse por red.

- TipoMensaje

    Esta Clase enum contiene las definiciones de los posibles tipos de mensajes posibles

- Usuario

    Esta clase resume la funcionalidad de la clase usaurio la cual incluye un constructor y los propios getter y setter.


## Instalacion

Tengo el proyecto compilado en dos archivos java para ejecutarlo primero ejecutar

Servidor.java

y despues ejecutar el archivo java

Main.java
