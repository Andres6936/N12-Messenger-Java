package edu.jabs.messenger.cliente;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Esta clase se encarga de recibir los mensajes del servidor. <br>
 * Cuando un mensaje es recibido, la ejecución de las acciones correspondientes
 * es delegada al ClienteAmigos.
 */
public class ThreadRecibirMensajesServidor extends Thread
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el objeto que implementa las acciones que se deben realizar cuando se
	 * recibe un mensaje
	 */
	private IClienteMessenger clienteAmigos;

	/**
	 * Es el stream utilizado para recibir los mensajes del servidor
	 */
	private BufferedReader inServidor;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el nuevo hilo y lo deja listo para iniciar
	 * 
	 * @param cliente El cliente al cual este hilo debe reportar los mensajes
	 *            recibidos
	 * @param in El stream a través del cual se reciben los mensajes enviados por el
	 *            servidor
	 */
	public ThreadRecibirMensajesServidor( IClienteMessenger cliente, BufferedReader in )
	{
		clienteAmigos = cliente;
		inServidor = in;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Recibe los mensajes enviados por el servidor, delega al cliente las tareas
	 * asociadas a los mensajes y espera nuevos mensajes. <br>
	 * Este hilo debe continuar vivo hasta que se reciba un mensaje de DESCONEXION
	 */
	public void run( )
	{
		String mensaje = "";
		try
		{
			while ( !mensaje.startsWith( IClienteMessenger.DESCONEXION ) )
			{
				mensaje = inServidor.readLine( );
				if ( mensaje.startsWith( IClienteMessenger.ONLINE ) )
				{
					// Sacar los datos del mensaje
					String[ ] datosAmigo = mensaje.split( ":" )[ 1 ].split( ";" );
					String nombreAmigo = datosAmigo[ 0 ];
					String ipAmigo = datosAmigo[ 1 ];
					int puertoAmigo = Integer.parseInt( datosAmigo[ 2 ] );

					Usuario amigo = new Usuario( nombreAmigo, ipAmigo, puertoAmigo );
					clienteAmigos.actualizarEstado( amigo );
				}
				else if ( mensaje.startsWith( IClienteMessenger.OFFLINE ) )
				{
					// Sacar los datos del mensaje
					String nombreAmigo = mensaje.split( ":" )[ 1 ];

					Usuario amigo = new Usuario( nombreAmigo );
					clienteAmigos.actualizarEstado( amigo );
				}
				else if ( mensaje.startsWith( IClienteMessenger.INICIO_CHARLA ) )
				{
					// Sacar los datos del mensaje
					String[ ] datosAmigo = mensaje.split( ":" )[ 1 ].split( ";" );
					String nombreAmigo = datosAmigo[ 0 ];
					String ipAmigo = datosAmigo[ 1 ];
					int puertoAmigo = Integer.parseInt( datosAmigo[ 2 ] );

					clienteAmigos.conectarAConversacion( nombreAmigo, ipAmigo, puertoAmigo );
				}
				else if ( mensaje.startsWith( IClienteMessenger.DESCONEXION ) )
				{
					inServidor = null;
					clienteAmigos.desconectar( );
				}
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}
}