package edu.jabs.messenger.servidor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;

/**
 * El ServidorAmigos es el que se encarga de recibir las conexiones de los
 * clientes que se conectan al sistema. <br>
 * Después de que se ha establecido una conexión, la responsabilidad de manejar
 * la comunicación con el cliente es de una instancia de la clase
 * ManejadorCliente.
 */
public class ServidorAmigos implements IServidorAmigos
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es la colección de los manejadores de los clientes conectados
	 */
	private Collection clientes;

	/**
	 * Referencia al objeto que permite el acceso a la base de datos para
	 * administrar los amigos
	 */
	private IAdministradorAmigos adminAmigos;

	/**
	 * Es el conjunto de propiedades que contienen la configuración de la aplicación
	 */
	private Properties configuracion;

	/**
	 * Es el socket que recibe las conexiones de los clientes
	 */
	private ServerSocket socket;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el servidor y deja listo el administrador de amigos
	 * 
	 * @param archivo El archivo de propiedades que tiene la configuración del
	 *            servidor - archivo != null
	 * @throws Exception Se lanza esta excepción si hay problemas con el archivo de
	 *             propiedades o hay problemas en la conexión a la base de datos
	 * @throws SQLException Se lanza esta excepción si hay problemas conectando el
	 *             almacén a la base de datos.
	 */
	public ServidorAmigos( String archivo ) throws SQLException, Exception
	{
		clientes = new LinkedList( );

		cargarConfiguracion( archivo );

		adminAmigos = new AdministradorAmigos( configuracion );
		adminAmigos.conectarABD( );
		adminAmigos.inicializarTablas( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Carga la configuración a partir de un archivo de propiedades
	 * 
	 * @param archivo El archivo de propiedades que contiene la configuración que
	 *            requiere el servidor - archivo != null y el archivo debe contener
	 *            la propiedad "servidor.puerto" y las propiedades que requiere el
	 *            administrador de resultados
	 * @throws Exception Se lanza esta excepción si hay problemas cargando el
	 *             archivo de propiedades
	 */
	private void cargarConfiguracion( String archivo ) throws Exception
	{
		FileInputStream fis = new FileInputStream( archivo );
		configuracion = new Properties( );
		configuracion.load( fis );
		fis.close( );
	}

	/**
	 * Retorna el administrador de amigos usado por el servidor
	 * 
	 * @return adminAmigos
	 */
	public IAdministradorAmigos darAministradorAmigos( )
	{
		return adminAmigos;
	}

	/**
	 * Termina la conexión a la base de datos y la cierra
	 * 
	 * @throws SQLException Se lanza esta excepción si hay problemas terminando la
	 *             conexión a la base de datos
	 */
	public void desconectarDB( ) throws SQLException
	{
		adminAmigos.desconectarBD( );
	}

	/**
	 * Este método se encarga de recibir todas las conexiones de los clientes. <br>
	 * Este método debe abrir el socket y para cada conexión entrante construir un
	 * ManejadorCliente asociado.
	 */
	public void recibirConexiones( )
	{
		int puerto = Integer.parseInt( configuracion.getProperty( "servidor.puerto" ) );
		try
		{
			socket = new ServerSocket( puerto );

			while ( true )
			{
				// Esperar una nueva conexión
				Socket socketNuevoCliente = socket.accept( );

				// Crear el manejador de encuentros y delegar la tarea de manejar al nuevo
				// cliente
				ManejadorCliente manejador = new ManejadorCliente( this, socketNuevoCliente );
				clientes.add( manejador );
				try
				{
					manejador.iniciarManejador( );
				}
				catch ( MessengerException e )
				{
					clientes.remove( manejador );
					e.printStackTrace( );
				}
			}
		}
		catch ( IOException e )
		{
			if ( socket != null )
				try
				{
					socket.close( );
				}
				catch ( IOException e1 )
				{
					e1.printStackTrace( );
				}
			e.printStackTrace( );
		}
	}

	/**
	 * Cierra el socket del servidor que espera clientes <br>
	 * <b>pre: </b>No se está esperando una conexión en el socket.
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas cerrando el
	 *             socket
	 */
	public void cerrarConexion( ) throws IOException
	{
		socket.close( );
		socket = null;
	}

	/**
	 * Retorna una colección de Strings con la información de los clientes que están
	 * conectados actualmente
	 * 
	 * @return Collection de Strings
	 */
	public Collection darClientes( )
	{
		Collection listaClientes = new ArrayList( clientes.size( ) );

		for ( Iterator iter = clientes.iterator( ); iter.hasNext( ); )
		{
			ManejadorCliente cliente = (ManejadorCliente) iter.next( );
			listaClientes.add( cliente.toString( ) );
		}

		return listaClientes;
	}

	/**
	 * Envía un mensaje a un usuario indicado el estado de un amigo
	 * 
	 * @param usuario El nombre del usuario al que se le debe enviar el mensaje -
	 *            usuario != null && usuario != ""
	 * @param estadoAmigo El estado del amigo del que se va a enviar la notificación
	 *            - estadoAmigo != null
	 */
	public void enviarNotificacionAmigo( String usuario, Usuario estadoAmigo )
	{
		boolean encontreUsuario = false;

		for ( Iterator iter = clientes.iterator( ); iter.hasNext( ) && !encontreUsuario; )
		{
			ManejadorCliente manejador = (ManejadorCliente) iter.next( );
			if ( manejador.darNombre( ).equals( usuario ) )
			{
				manejador.enviarEstadoAmigo( estadoAmigo );
				encontreUsuario = true;
			}
		}
	}

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
	public void iniciarConversacion( String usuario, String amigo, String direccionIp, int puerto )
	{
		boolean encontreUsuario = false;

		for ( Iterator iter = clientes.iterator( ); iter.hasNext( ) && !encontreUsuario; )
		{
			ManejadorCliente manejador = (ManejadorCliente) iter.next( );
			if ( manejador.darNombre( ).equals( usuario ) )
			{
				manejador.iniciarConversacion( amigo, direccionIp, puerto );
				encontreUsuario = true;
			}
		}
	}

	/**
	 * Elimina el manejador del usuario indicado porque este se está desconectando
	 * 
	 * @param usuario El nombre del usuario que se está desconectado - usuario !=
	 *            null
	 */
	public void desconectarCliente( String usuario )
	{
		boolean encontreUsuario = false;

		for ( Iterator iter = clientes.iterator( ); iter.hasNext( ) && !encontreUsuario; )
		{
			ManejadorCliente manejador = (ManejadorCliente) iter.next( );
			if ( manejador.darNombre( ).equals( usuario ) )
			{
				clientes.remove( manejador );
				encontreUsuario = true;
			}
		}
	}

	// -----------------------------------------------------------------
	// Puntos de Extensión
	// -----------------------------------------------------------------

	/**
	 * Método para la extensión 1
	 * 
	 * @return respuesta1
	 */
	public String metodo1( )
	{
		return "Respuesta 1";
	}

	/**
	 * Método para la extensión2
	 * 
	 * @return respuesta2
	 */
	public String metodo2( )
	{
		return "Respuesta 2";
	}

}
