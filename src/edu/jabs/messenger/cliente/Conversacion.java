package edu.jabs.messenger.cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.jabs.messenger.interfazCliente.IVentanaConversacion;

/**
 * Esta clase representa una conversación que se está llevando a cabo entre dos
 * usuarios
 */
public class Conversacion
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Mensaje que se usa para indicar el inicio de un mensaje enviado dentro de una
	 * conversación
	 */
	public static final String MENSAJE = "MENSAJE";

	/**
	 * Mensaje que se usa para indicar que se quiere terminar la conversación
	 */
	public static final String TERMINAR = "TERMINAR";

	/**
	 * Mensaje que se usa para indicar que se aceptó el fin de la conversación
	 */
	public static final String CONVERSACION_TERMINADA = "CONVERSACION_TERMINADA";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * El nombre del amigo con el que se va a llevar a cabo la conversación
	 */
	private String nombreAmigo;

	/**
	 * El cliente en el cual se creó esta conversación
	 */
	private IClienteMessenger clienteAmigos;

	/**
	 * Indica si la conversación ya terminó
	 */
	private boolean conversacionTerminada;

	/**
	 * La ventana donde se muestran los mensajes de esta conversación
	 */
	private IVentanaConversacion ventanaConversacion;

	/**
	 * Es el socket a través del cual se lleva a cabo la comunicación con el amigo
	 */
	private Socket socketAmigo;

	/**
	 * Es el stream usuado para leer los mensajes enviados por el amigo
	 */
	private BufferedReader inAmigo;

	/**
	 * Es el stream usado para enviar mensajes al amigo
	 */
	private PrintWriter outAmigo;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Crea una nueva conversación. <br>
	 * El usuario local fue el que inicio la conversación, así que se debe crear un
	 * socket y esperar a que el amigo se conecte.
	 * 
	 * @param cliente Es el cliente en el cual se creó esta conversación - cliente
	 *            != null
	 * @param puerto Es el puerto en el cual se debe esperar la conexión del amigo
	 * @param amigo El nombre del amigo con el que se va a realizar la conversación
	 *            - amigo != null
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	public Conversacion( IClienteMessenger cliente, int puerto, String amigo ) throws IOException
	{
		conversacionTerminada = false;
		clienteAmigos = cliente;
		nombreAmigo = amigo;

		// Esperar la conexión del amigo
		ServerSocket socket = new ServerSocket( puerto );
		socketAmigo = socket.accept( );

		outAmigo = new PrintWriter( socketAmigo.getOutputStream( ), true );
		inAmigo = new BufferedReader( new InputStreamReader( socketAmigo.getInputStream( ) ) );

		socket.close( );

		// Iniciar el hilo que recibirá los mensajes del amigo
		ThreadRecibirMensajesConversacion hilo = new ThreadRecibirMensajesConversacion( this, inAmigo );
		hilo.start( );
	}

	/**
	 * Crea una nueva conversación <br>
	 * El amigo fue el que inició la conversación, así que es necesario abrir una
	 * conexión hasta el cliente del amigo.
	 * 
	 * @param cliente Es el cliente en el cual se creó esta conversación - cliente
	 *            != null
	 * @param amigo Es la información que se tiene sobre el amigo que inició la
	 *            conversación - amigo != null
	 * @throws IOException Se lanza esta excepción si hay problemas estableciendo la
	 *             comunicación con el amigo
	 */
	public Conversacion( IClienteMessenger cliente, Usuario amigo ) throws IOException
	{
		conversacionTerminada = false;
		clienteAmigos = cliente;
		nombreAmigo = amigo.darNombre( );

		// Abrir la conexión
		String direccion = amigo.darDireccionIp( );
		int puerto = amigo.darPuerto( );
		socketAmigo = new Socket( direccion, puerto );

		outAmigo = new PrintWriter( socketAmigo.getOutputStream( ), true );
		inAmigo = new BufferedReader( new InputStreamReader( socketAmigo.getInputStream( ) ) );

		// Iniciar el hilo que recibirá los mensajes del amigo
		ThreadRecibirMensajesConversacion hilo = new ThreadRecibirMensajesConversacion( this, inAmigo );
		hilo.start( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Indica si la conversación está conectada
	 * 
	 * @return true si ya está conectada
	 */
	public boolean estaConectada( )
	{
		return socketAmigo != null;
	}

	/**
	 * Establece la ventana asociada a esta conversación, en la cual se deben
	 * mostrar tanto los mensajes enviados por el usuario local como los mensajes
	 * enviados por el amigo.
	 * 
	 * @param ventana La ventana en la que se publicarán los mensajes - ventana !=
	 *            null
	 */
	public void cambiarVentanaConversacion( IVentanaConversacion ventana )
	{
		ventanaConversacion = ventana;
	}

	/**
	 * Indica si la conversación ya fue marcada como terminada
	 * 
	 * @return conversacionTerminada
	 */
	public boolean conversacionTerminada( )
	{
		return conversacionTerminada;
	}

	/**
	 * Retorna el nombre del amigo con el que se está llevando a cabo la
	 * conversación
	 * 
	 * @return nombreAmigo
	 */
	public String darNombreAmigo( )
	{
		return nombreAmigo;
	}

	/**
	 * Envía al amigo un mensaje y lo muestra en la ventana de la conversación
	 * 
	 * @param mensaje El mensaje que será enviado al amigo - mensaje != null
	 */
	public void enviarMensaje( String mensaje )
	{
		mostrarMensajeEnVentana( clienteAmigos.darNombreUsuario( ), mensaje );
		outAmigo.println( MENSAJE + ":" + mensaje );
	}

	/**
	 * Muestra en la ventana de la conversacion un mensaje enviado por el amigo
	 * 
	 * @param mensaje El mensaje que fue recibido - mensaje != null
	 */
	public void mostrarMensajeRecibido( String mensaje )
	{
		mostrarMensajeEnVentana( nombreAmigo, mensaje );
	}

	/**
	 * Muestra un mensaje en la ventana de la conversación
	 * 
	 * @param autor El autor del mensaje - autor != null
	 * @param mensaje El mensaje que se debe mostrar - mensaje != null
	 */
	private void mostrarMensajeEnVentana( String autor, String mensaje )
	{
		if ( ventanaConversacion != null )
		{
			ventanaConversacion.publicarMensaje( autor + " -> " + mensaje );
		}
	}

	/**
	 * Envia un mensaje al amigo diciendo que se quiere terminar la conversación
	 */
	public void terminar( )
	{
		outAmigo.println( TERMINAR );
	}

	/**
	 * Envía un mensaje al amigo confirmando que la conversación será terminada
	 */
	public void enviarConfirmacionTerminarConversacion( )
	{
		outAmigo.println( CONVERSACION_TERMINADA );
	}

	/**
	 * Termina la conversacion, cerrando la conexión con el amigo y la ventana. <br>
	 * <b>pre: </b> No hay nadie leyendo o escribiendo en el socket
	 */
	public void terminarConversacion( )
	{
		try
		{
			conversacionTerminada = true;

			inAmigo.close( );
			outAmigo.close( );

			socketAmigo.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if ( ventanaConversacion != null )
				ventanaConversacion.dispose( );
		}
	}
}
