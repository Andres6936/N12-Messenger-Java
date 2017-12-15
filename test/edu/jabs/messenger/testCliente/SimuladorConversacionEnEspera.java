package edu.jabs.messenger.testCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.jabs.messenger.cliente.Conversacion;
import junit.framework.TestCase;

/**
 * Esta clase sirve para simular que hay una conversación que está esperando a
 * que un cliente se conecte
 */
public class SimuladorConversacionEnEspera extends Thread
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es el puerto en el que se espera la conexión
	 */
	public static final int PUERTO_ESPERA = 9998;

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el socket a través del cual se lleva a cabo la comunicación con la
	 * conversacion
	 */
	private Socket socketConversacion;

	/**
	 * Es el stream usuado para leer los mensajes enviados por la conversacion
	 */
	private BufferedReader inConversacion;

	/**
	 * Es el stream usado para enviar mensajes la conversacion
	 */
	private PrintWriter outConversacion;

	/**
	 * Indica si el socket ya se pudo conectar
	 */
	private boolean conectado;

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Inicia el thread y crea un socket que espera a que una conversación se
	 * conecte.
	 */
	public void run( )
	{
		try
		{
			ServerSocket ssocket = new ServerSocket( PUERTO_ESPERA );

			socketConversacion = ssocket.accept( );
			outConversacion = new PrintWriter( socketConversacion.getOutputStream( ), true );
			inConversacion = new BufferedReader( new InputStreamReader( socketConversacion.getInputStream( ) ) );

			ssocket.close( );

			conectado = true;
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			TestCase.fail( "Hubo un problema esperando la conexión de la conversación: " + e.getMessage( ) );
		}
	}

	/**
	 * Indica si la conversación del simulador ya se conectó
	 * 
	 * @return conectado
	 */
	public boolean estaConectado( )
	{
		return conectado;
	}

	/**
	 * Envía un mensaje para terminar la conversación (TERMINAR)
	 */
	public void enviarMensajeTerminar( )
	{
		outConversacion.println( Conversacion.TERMINAR );
	}

	/**
	 * Recibe un mensaje enviado por el cliente y lo retorna
	 * 
	 * @return mensaje
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public String recibirMensaje( ) throws IOException
	{
		return inConversacion.readLine( );
	}

	/**
	 * Termina la conexión del simulador con la conversación
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void terminarConexion( ) throws IOException
	{
		inConversacion.close( );
		outConversacion.close( );
		socketConversacion.close( );
	}
}