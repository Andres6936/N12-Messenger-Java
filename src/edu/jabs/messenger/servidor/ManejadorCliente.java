package edu.jabs.messenger.servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Esta clase se encarga de manejar la comunicación entre el servidor y un
 * cliente. <br>
 * A través de esta clase el servidor puede enviar mensajes a un cliente que
 * esté conectado. <br>
 * Una instancia de la clase ThreadRecibirMensajesCliente es la encargada de
 * recibir los mensajes enviados por el cliente, pero en esta clase es donde se
 * implementa la lógica para ejecutar las acciones que corresponden a los
 * mensajes enviados.
 */
public class ManejadorCliente
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el servidor en el que se encuentra este manejador
	 */
	private IServidorAmigos servidor;

	/**
	 * Referencia al objeto que permite el acceso a la base de datos
	 */
	private IAdministradorAmigos adminAmigos;

	/**
	 * Nombre del usuario al que está asociado este manejador
	 */
	private String nombreUsuario;

	/**
	 * Dirección IP del usuario
	 */
	private String direccionIp;

	/**
	 * Puerto que el cliente estableció para que se lleven a cabo las conversaciones
	 */
	private int puerto;

	/**
	 * El socket que está conectado al cliente
	 */
	private Socket socketCliente;

	/**
	 * Es el stream usuado para leer los mensajes enviados por el cliente
	 */
	private BufferedReader inCliente;

	/**
	 * Es el stream usado para enviar mensajes al cliente
	 */
	private PrintWriter outCliente;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el manejador que se encargará de la comunicación con un cliente.
	 * <br>
	 * Al construir el manejador se preparan también los streams que serán usados
	 * para la comunicación con el cliente.
	 * 
	 * @param servidorAmigos Es el servidor en el que se encuentra este manejador -
	 *            servidorAmigos != null
	 * @param socket Es el socket que está conectado al cliente - socket != null
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el cliente
	 */
	public ManejadorCliente( IServidorAmigos servidorAmigos, Socket socket ) throws IOException
	{
		servidor = servidorAmigos;
		adminAmigos = servidor.darAministradorAmigos( );

		socketCliente = socket;

		outCliente = new PrintWriter( socketCliente.getOutputStream( ), true );
		inCliente = new BufferedReader( new InputStreamReader( socketCliente.getInputStream( ) ) );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Retorna el nombre del usuario asociado a este manejador
	 * 
	 * @return nombreUsuario
	 */
	public String darNombre( )
	{
		return nombreUsuario;
	}

	/**
	 * Inicia el manejador, leyendo el mensaje inicial del cliente, enviandole la
	 * información de sus amigos y luego activando el Thread que va a recibir los
	 * otros mensajes que envíe. <br>
	 * Los pasos de la inicialización son: <br>
	 * 1. Recibir el mensaje inicial del cliente (que tiene la información de
	 * conexión del usuario) <br>
	 * 2. Iniciar el hilo que se encargará de recibir los mensajes enviados por el
	 * cliente (ThreadRecibirMensajesCliente) <br>
	 * 3. Revisar en la base de datos si el usuario ya existe y en caso negativo
	 * crear el nuevo usuario <br>
	 * 4. Cambiar el estado del usuario en la base de datos <br>
	 * 5. Enviar a las personas que conocen al usuario la notificación de que ahora
	 * está online <br>
	 * 6. Enviar al usuario notificaciones con el estado de sus amigos
	 * 
	 * @throws MessengerException Se lanza esta excepción si hay un problema con los
	 *             mensajes del cliente
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void iniciarManejador( ) throws MessengerException, IOException
	{
		try
		{
			// 1. Recibir el mensaje inicial
			String mensajeCliente = inCliente.readLine( );
			if ( !mensajeCliente.startsWith( IServidorAmigos.LOGIN + ":" ) )
				throw new MessengerException( "El formato del mensaje no es el esperado: " + mensajeCliente );

			String strDatosCliente = mensajeCliente.split( ":" )[ 1 ];
			String[ ] datosCliente = strDatosCliente.split( ";" );
			if ( datosCliente.length != 3 )
				throw new MessengerException( "El formato del mensaje no es el esperado: " + mensajeCliente );

			// Sacar del mensaje la información del cliente
			nombreUsuario = datosCliente[ 0 ];
			direccionIp = datosCliente[ 1 ];
			puerto = Integer.parseInt( datosCliente[ 2 ] );

			// 2. Iniciar el hilo que recibirá los siguientes mensajes
			ThreadRecibirMensajesCliente hiloRecibirMensajes = new ThreadRecibirMensajesCliente( this, inCliente );
			hiloRecibirMensajes.start( );

			// 3. Consultar en la base de datos los amigos del usuario.
			// Si el usuario no existe hay que crearlo
			Collection amigos = null;
			Collection conocen = null;

			if ( !adminAmigos.existeUsuario( nombreUsuario ) )
			{
				adminAmigos.crearUsuario( nombreUsuario );
				amigos = new ArrayList( );
				conocen = new ArrayList( );
			}
			else
			{
				amigos = adminAmigos.darAmigos( nombreUsuario );
				conocen = adminAmigos.darPersonasConocen( nombreUsuario );
			}

			// 4. Cambiar el estado del usuario en la base de datos
			adminAmigos.cambiarEstadoUsuario( nombreUsuario, Usuario.STR_ONLINE, direccionIp, puerto );

			// 5. Enviar a las personas que conocen al usuario la notificación de que ahora
			// está online
			enviarNotificacionEstado( conocen, Usuario.ONLINE );

			// 6. Enviar al usuario notificaciones con el estado de sus amigos
			enviarNotificacionEstadoAmigos( amigos );

		}
		catch ( SQLException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Desconecta al usuario, notifica a todas las personas que lo conocen y
	 * destruye el manejador de clientes. <br>
	 * <b>pre: </b> No hay nadie leyendo ni escribiendo en los streams; el thread
	 * que recibe los mensajes del cliente ya va a terminar su ejecución
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas desconectando el
	 *             socket
	 */
	public void desconectarUsuario( ) throws IOException
	{
		try
		{
			PrintWriter tempOut = outCliente;
			outCliente = null;

			BufferedReader tempIn = inCliente;
			inCliente = null;

			// Notificar al servidor de la desconexión del cliente y cambiar el estado del
			// usuario en la base de datos
			adminAmigos.cambiarEstadoUsuario( nombreUsuario, Usuario.STR_OFFLINE, "", 0 );

			servidor.desconectarCliente( nombreUsuario );

			// Enviar la notificacion a las personas que conocen al usuario
			Collection conocen = adminAmigos.darPersonasConocen( nombreUsuario );
			enviarNotificacionEstado( conocen, Usuario.OFFLINE );

			// Enviar al cliente un mensaje avisando de la desconexión
			tempOut.println( IServidorAmigos.DESCONEXION );

			// Desconectar al cliente
			tempOut.close( );
			tempIn.close( );
			socketCliente.close( );

			socketCliente = null;
		}
		catch ( SQLException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Agrega un amigo al usuario. Actualiza al base de datos y envía al cliente el
	 * estado del amigo.
	 * 
	 * @param nombreAmigo El nombre del amigo que se va a agregar - nombreAmigo !=
	 *            null && nombreAmigo != ""
	 */
	public void agregarAmigo( String nombreAmigo )
	{
		try
		{
			// Revisar si el usuario ya es su amigo
			boolean encontroAmigo = adminAmigos.existeAmigo( nombreUsuario, nombreAmigo );

			if ( !encontroAmigo )
			{
				adminAmigos.agregarAmigo( nombreUsuario, nombreAmigo );
				Usuario amigo = adminAmigos.darEstadoUsuario( nombreAmigo );

				if ( amigo != null )
					enviarEstadoAmigo( amigo );
			}
		}
		catch ( SQLException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Envía a un amigo, a través del servidor, un mensaje para iniciar una
	 * conversación
	 * 
	 * @param nombreAmigo El nombre del amigo con el que se va a establecer la
	 *            conversación - nombreAmigo != null && nombreAmigo != ""
	 */
	public void enviarConversacionAmigo( String nombreAmigo )
	{
		servidor.iniciarConversacion( nombreAmigo, nombreUsuario, direccionIp, puerto );
	}

	/**
	 * Envía al usuario un mensaje indicando que un amigo quiere que se inicie una
	 * conversación
	 * 
	 * @param nombreAmigo El nombre del amigo al que se debe conectar el usuario -
	 *            amigo != null
	 * @param direccionIp La direccion ip del amigo con el que se va a establecer la
	 *            conversación - direccionIp != null
	 * @param puerto El puerto al que se debe conectar el usuario
	 */
	public void iniciarConversacion( String nombreAmigo, String direccionIp, int puerto )
	{
		if ( outCliente != null )
		{
			outCliente.println( IServidorAmigos.INCIAR_CHARLA + ":" + nombreAmigo + ";" + direccionIp + ";" + puerto );
		}
	}

	/**
	 * Envía al usuario el estado de uno de sus amigos
	 * 
	 * @param amigo El amigo del que se quiere enviar la información - amigo != null
	 */
	public void enviarEstadoAmigo( Usuario amigo )
	{
		if ( outCliente != null )
		{
			if ( amigo.darEstado( ) == Usuario.ONLINE )
			{
				outCliente.println( IServidorAmigos.ONLINE + ":" + amigo.darNombre( ) + ";" + amigo.darDireccionIp( )
						+ ";" + amigo.darPuerto( ) );
			}
			else
			{
				outCliente.println( IServidorAmigos.OFFLINE + ":" + amigo.darNombre( ) );
			}
		}
	}

	/**
	 * Envía al usuario mensajes indicando el estado de cada uno de sus amigos
	 * 
	 * @param amigos La colección de amigos del usuario - amigos != null y amigos
	 *            contiene objetos de tipo Usuario
	 */
	private void enviarNotificacionEstadoAmigos( Collection amigos )
	{
		for ( Iterator iter = amigos.iterator( ); iter.hasNext( ); )
		{
			Usuario amigo = (Usuario) iter.next( );
			enviarEstadoAmigo( amigo );
		}
	}

	/**
	 * Envía un mensaje a las personas que conocen al usuario indicándole su estado
	 * actual
	 * 
	 * @param personas La colección con las personas a los que se les va a mandar la
	 *            notificación - personas != null y personas contiene objetos de
	 *            tipo Usuario
	 * @param estado El estado que se va a notificar - estado = Usuario.ONLINE ||
	 *            estado = Usuario.OFFLINE
	 */
	private void enviarNotificacionEstado( Collection personas, int estado )
	{
		Usuario usuario = new Usuario( nombreUsuario );
		if ( estado == Usuario.ONLINE )
			usuario.cambiarEstado( direccionIp, puerto );

		for ( Iterator iter = personas.iterator( ); iter.hasNext( ); )
		{
			Usuario amigo = (Usuario) iter.next( );

			// La notificación solamente se envía a los usuarios conectados
			if ( amigo.darEstado( ) == Usuario.ONLINE )
			{
				servidor.enviarNotificacionAmigo( amigo.darNombre( ), usuario );
			}
		}
	}

	/**
	 * Retorna una cadena con el nombre y la dirección IP de los clientes que están
	 * conectados actualmente
	 * 
	 * @return cadena
	 */
	public String toString( )
	{
		return nombreUsuario + " - " + direccionIp + ":" + puerto;
	}
}
