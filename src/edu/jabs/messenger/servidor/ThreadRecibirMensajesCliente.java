package edu.jabs.messenger.servidor;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Esta clase se encarga de esperar los mensajes que un cliente envía al
 * servidor. <br>
 * Cuando un mensaje es recibido, la ejecución de las acciones correspondientes
 * es delegada al ManejadorCliente.
 */
public class ThreadRecibirMensajesCliente extends Thread
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Este el manejador que se encarga del envío de los mensajes del cliente
	 */
	private ManejadorCliente manejador;

	/**
	 * Es el stream a través del cual se reciben los mensajes del cliente
	 */
	private BufferedReader inCliente;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el nuevo hilo y lo deja listo para iniciar
	 * 
	 * @param manejadorCliente Es el manejador del cliente - manejadorCliente !=
	 *            null
	 * @param in Es el stream que esta clase tendrá que revisar - in != null
	 */
	public ThreadRecibirMensajesCliente( ManejadorCliente manejadorCliente, BufferedReader in )
	{
		manejador = manejadorCliente;
		inCliente = in;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Este método se encarga de recibir los mensajes enviados por el cliente al
	 * servidor. <br>
	 * Cada mensaje recibido se revisa y se delega la responsabilidad al
	 * manejadorCliente. <br>
	 * Este método continua su ejecución hasta que se recibe un mensaje de LOGOUT.
	 */
	public void run( )
	{
		String mensaje = "";
		try
		{
			while ( mensaje != null && !mensaje.startsWith( IServidorAmigos.LOGOUT ) )
			{
				mensaje = inCliente.readLine( );

				if ( mensaje != null )
				{
					// Indica que el cliente quiere desconectarse
					if ( mensaje.startsWith( IServidorAmigos.LOGOUT ) )
					{
						manejador.desconectarUsuario( );
					}
					// Indica que el cliente quiere iniciar una conversación con un amigo
					else if ( mensaje.startsWith( IServidorAmigos.CONVERSACION ) )
					{
						String nombreAmigo = mensaje.split( ":" )[ 1 ];

						manejador.enviarConversacionAmigo( nombreAmigo );
					}
					// Indica que el cliente quiere agregar un amigo
					else if ( mensaje.startsWith( IServidorAmigos.AGREGAR_AMIGO ) )
					{
						String nombreAmigo = mensaje.split( ":" )[ 1 ];
						manejador.agregarAmigo( nombreAmigo );
					}
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}

	}
}
