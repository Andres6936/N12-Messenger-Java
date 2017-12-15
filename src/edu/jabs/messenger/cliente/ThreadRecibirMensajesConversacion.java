package edu.jabs.messenger.cliente;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Esta clase recibe los mensajes de una conversación. <br>
 * Cuando un mensaje es recibido, la ejecución de las acciones correspondientes
 * es delegada a la Conversacion.
 */
public class ThreadRecibirMensajesConversacion extends Thread
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es la conversación para la cual se están escuchando los mensajes
	 */
	private Conversacion conversacion;

	/**
	 * Es el stream a través del cual se reciben los mensajes enviados por el amigo
	 * que participa en la conversación
	 */
	private BufferedReader inAmigo;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el nuevo hilo y lo deja listo para iniciar
	 * 
	 * @param conv La conversación a la cual este hilo debe reportar los mensajes
	 *            recibidos
	 * @param in El stream a través del cual se reciben los mensajes enviados por el
	 *            amigo que participa en la conversación
	 */
	public ThreadRecibirMensajesConversacion( Conversacion conv, BufferedReader in )
	{
		conversacion = conv;
		inAmigo = in;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Recibe los mensajes enviados por el amigo que participa en la conversación,
	 * delega a la conversación las tareas asociadas a los mensajes y espera nuevos
	 * mensajes. <br>
	 * Este hilo debe continuar vivo hasta que se reciba un mensaje de
	 * CONVERSACION_TERMINADA o de TERMINAR
	 */
	public void run( )
	{
		String mensaje = "";
		while ( mensaje != null && !mensaje.startsWith( Conversacion.CONVERSACION_TERMINADA )
				&& !mensaje.startsWith( Conversacion.TERMINAR ) )
		{
			try
			{
				mensaje = inAmigo.readLine( );

				if ( mensaje == null )
				{

				}
				else if ( mensaje.startsWith( Conversacion.MENSAJE ) )
				{
					String mensajeRecibido = mensaje.replaceFirst( Conversacion.MENSAJE + ":", "" );
					conversacion.mostrarMensajeRecibido( mensajeRecibido );
				}
				else if ( mensaje.startsWith( Conversacion.CONVERSACION_TERMINADA ) )
				{
					conversacion.terminarConversacion( );
				}
				else if ( mensaje.startsWith( Conversacion.TERMINAR ) )
				{
					conversacion.enviarConfirmacionTerminarConversacion( );
					conversacion.terminarConversacion( );
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
	}
}
