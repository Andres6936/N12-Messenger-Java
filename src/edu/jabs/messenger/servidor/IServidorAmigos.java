package edu.jabs.messenger.servidor;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * TODO documentar
 */
public interface IServidorAmigos
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Mensaje que se envía cuando un cliente se va a conectar
	 */
	public static final String LOGIN = "LOGIN";

	/**
	 * Mensaje que se envía cuando un cliente se va a desconectar
	 */
	public static final String LOGOUT = "LOGOUT";

	/**
	 * Mensaje que envía el servidor a un cliente cuando lo desconecta
	 */
	public static final String DESCONEXION = "DESCONEXION";

	/**
	 * Mensaje que se usa para indicar que un amigo está conectado
	 */
	public static final String ONLINE = "ONLINE";

	/**
	 * Mensaje que se usa para indicar que un amigo está desconectado
	 */
	public static final String OFFLINE = "OFFLINE";

	/**
	 * Mensaje que se usa para agregar un amigo
	 */
	public static final String AGREGAR_AMIGO = "AGREGAR_AMIGO";

	/**
	 * Mensaje que se usa para que el servidor le indique a un cliente que va a
	 * iniciarse una conversación
	 */
	public static final String INCIAR_CHARLA = "INCIO_CHARLA";

	/**
	 * Mensaje que se usa para que un cliente le indique al servidor que va a
	 * iniciar una charla y que necesita ponerse en contacto con un amigo
	 */
	public static final String CONVERSACION = "CONVERSACION";

	/**
	 * Retorna el administrador de amigos usado por el servidor
	 * 
	 * @return adminAmigos
	 */
	IAdministradorAmigos darAministradorAmigos( );

	/**
	 * Termina la conexión a la base de datos y la cierra
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas terminando la
	 *             conexión a la base de datos
	 */
	void desconectarDB( ) throws SQLException;

	/**
	 * Este método se encarga de recibir todas las conexiones de los clientes. <br>
	 * Este método debe abrir el socket y para cada conexión entrante construir un
	 * ManejadorCliente asociado.
	 */
	void recibirConexiones( );

	/**
	 * Cierra el socket del servidor que espera clientes <br>
	 * <b>pre: </b>No se está esperando una conexión en el socket.
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas cerrando el
	 *             socket
	 */
	void cerrarConexion( ) throws IOException;

	/**
	 * Retorna una colección de Strings con la información de los clientes que están
	 * conectados actualmente
	 * 
	 * @return Collection de Strings
	 */
	Collection darClientes( );

	/**
	 * Envía un mensaje a un usuario indicado el estado de un amigo
	 * 
	 * @param usuario El nombre del usuario al que se le debe enviar el mensaje -
	 *            usuario != null && usuario != ""
	 * @param estadoAmigo El estado del amigo del que se va a enviar la notificación
	 *            - estadoAmigo != null
	 */
	void enviarNotificacionAmigo( String usuario, Usuario estadoAmigo );

	/**
	 * Envía un mensaje a un usuario para que participe en una conversación
	 * 
	 * @param usuario El nombre del usuario al que se le va a enviar el mensaje para
	 *            iniciar una conversación - usuario != null
	 * @param amigo El nombre del usuario con el que se va a establecer una
	 *            conversación - amigo != null
	 * @param direccionIp La direccion ip del usuario con el que se va a establecer
	 *            la conversación - direccionIp != null
	 * @param puerto El puerto al que se debe conectar el usuario
	 */
	void iniciarConversacion( String usuario, String amigo, String direccionIp, int puerto );

	/**
	 * Elimina el manejador del usuario indicado porque este se está desconectando
	 * 
	 * @param usuario El nombre del usuario que se está desconectado - usuario !=
	 *            null
	 */
	void desconectarCliente( String usuario );

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
