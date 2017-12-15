package edu.jabs.messenger.cliente;

import java.io.IOException;

/**
 * Esta interfaz define los servicios que debe ofrecer el cliente para el
 * messenger
 */
public interface IClienteMessenger
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Mensaje que se envía cuando un cliente se va a conectar
	 */
	static final String LOGIN = "LOGIN";

	/**
	 * Mensaje que se envía cuando un cliente se va a desconectar
	 */
	static final String LOGOUT = "LOGOUT";

	/**
	 * Mensaje que envía el servidor a un cliente cuando lo desconecta
	 */
	static final String DESCONEXION = "DESCONEXION";

	/**
	 * Mensaje que se usa para indicar que un amigo está conectado
	 */
	static final String ONLINE = "ONLINE";

	/**
	 * Mensaje que se usa para indicar que un amigo está desconectado
	 */
	static final String OFFLINE = "OFFLINE";

	/**
	 * Mensaje que se usa para agregar un amigo
	 */
	static final String AGREGAR_AMIGO = "AGREGAR_AMIGO";

	/**
	 * Mensaje que se usa para que el servidor le indique a un cliente que va a
	 * iniciarse una conversación
	 */
	static final String INICIO_CHARLA = "INCIO_CHARLA";

	/**
	 * Mensaje que se usa para que un cliente le indique al servidor que va a
	 * iniciar una charla y que necesita ponerse en contacto con un amigo
	 */
	static final String CONVERSACION = "CONVERSACION";

	/**
	 * Indica si el cliente está conectado al servidor
	 * 
	 * @return Retorna true si el cliente está conectado
	 */
	boolean estaConectado( );

	/**
	 * Retorna el nombre del usuario conectado
	 * 
	 * @return nombreUsuario
	 */
	String darNombreUsuario( );

	/**
	 * Conecta el cliente al servidor y deja la aplicación lista para enviar y
	 * recibir mensajes. <br>
	 * Después de conectarse al servidor se envía la información del usuario (nombre
	 * y dirección ip) y se crea un hilo especial para leer los mensajes
	 * provenientes del servidor usando una instancia de la clase
	 * ThreadRecibirMensajesServidor.
	 * 
	 * @param usuario El nombre del usuario local que se va a conectar al servidor -
	 *            usuario != null && usuario != ""
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación.
	 */
	void conectar( String usuario ) throws IOException;

	/**
	 * Envía un mensaje de LOGOUT al servidor. Este a su vez deberá cerrar la
	 * conexión con el cliente y enviar un mensaje de desconexión (DESCONEXION) para
	 * que, cuando sea recibido por el ThreadRecibirMensajesServidor, se inicie la
	 * desconexión del lado del cliente.
	 */
	void enviarDesconexion( );

	/**
	 * Cierra todas las conversaciones que están abiertas actualmente, limpia la
	 * lista de amigos y desconecta al cliente del servidor. <br>
	 * <b>pre: </b>No hay nadie escribiendo ni leyendo del socket que comunica al
	 * cliente con el servidor.
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas desconectando al
	 *             cliente
	 */
	void desconectar( ) throws IOException;

	/**
	 * Agrega un amigo al usuario
	 * 
	 * @param nombreAmigo El nombre del nuevo amigo - amigo != null
	 */
	void agregarAmigo( String nombreAmigo );

	/**
	 * Actualiza el estado de un amigo. Si no se encontraba en la lista de amigos,
	 * entonces es agregado.
	 * 
	 * @param amigo Los datos del amigo - amigo != null
	 */
	void actualizarEstado( Usuario amigo );

	/**
	 * Este método es usado cuando el usuario local va a iniciar una conversación
	 * con un amigo. <br>
	 * El proceso de crear una conversación se ejecuta en tres etapas principales.
	 * <br>
	 * 1. Se envía al servidor un mensaje solicitando que se le envíe un mensaje al
	 * otro cliente para que entre a hacer parte de la conversación. <br>
	 * 2. Se crea un objeto Conversacion que se encargará de recibir y manejar la
	 * conexión del cliente remoto. <br>
	 * 3. Se retorna el objeto conversación creado para que se abra la ventana
	 * correspondiente en la interfaz.
	 * 
	 * @param amigo El nombre del amigo con el que se va a realizar la conversación
	 *            - amigo != null && amigo hace parte de la lista de amigos
	 * @return Retorna el objeto conversación creado
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	Conversacion crearConversacionLocal( String amigo ) throws IOException;

	/**
	 * Este método es usado cuando se recibe una notificación que indica que se debe
	 * participar en una conversación con un amigo. <br>
	 * El proceso para crear la conversación es el siguiente. <br>
	 * 1. Se crear un objeto Conversacion que se encargará de recibir y manejar la
	 * conexión del cliente remoto. <br>
	 * 2. El objeto Conversacion recién creado establece una comunicación con el
	 * otro cliente. <br>
	 * 3. Se le notifica a la interfaz que se creó una Conversación para que se cree
	 * la ventana correspondiente en la interfaz.
	 * 
	 * @param nombreAmigo El nombre del amigo con el que se va a realizar la
	 *            conversación - nombreAmigo != null
	 * @param ipAmigo La dirección del amigo con el que se va a realizar la
	 *            conversación - ipAmigo != null && ipAmigo != ""
	 * @param puertoAmigo El puerto del amigo al que se debe conectar el cliente
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	void conectarAConversacion( String nombreAmigo, String ipAmigo, int puertoAmigo ) throws IOException;

	/**
	 * Elimina de la colección de conversaciones la conversación indicada
	 * 
	 * @param conv La conversación que se va a eliminar - conv != null
	 */
	void eliminarConversacion( Conversacion conv );

	/**
	 * Método para la extensión 1
	 * 
	 * @return respuesta1
	 */
	String metodo1( );

	/**
	 * Método para la extensión2
	 * 
	 * @return respuesta2
	 */
	String metodo2( );
}
