package edu.jabs.messenger.testCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import edu.jabs.messenger.cliente.Conversacion;
import junit.framework.TestCase;

/**
 * Esta clase simula ser una convesación en espera de que el cliente remoto se
 * conecte.<br>
 * Esta clase permite enviar y recibir mensajes a la otra conversación a través
 * del socket, y lleva un registro de los mensajes recibidos.<br>
 * El autor de los mensajes enviados por esta clase será AMIGO_ESPERA
 */
public class SimuladorConversacionEspera extends Thread
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es el nombre que se supone tiene el participante simulado
	 */
	public static final String AMIGO_ESPERA = "amigoEspera";

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

	/**
	 * Es la lista donde se mantienen los mensajes recibidos
	 */
	private LinkedList mensajes;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Crea el simulador pero no hace nada todavía con el socket
	 */
	public SimuladorConversacionEspera( )
	{
		mensajes = new LinkedList( );
	}

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
	 * Envía un mensaje a la conversación
	 * 
	 * @param mensaje el mensaje enviado
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void enviarMensaje( String mensaje ) throws IOException
	{
		outConversacion.println( Conversacion.MENSAJE + ":" + mensaje );
	}

	/**
	 * Envía un mensaje para terminar la conversación (TERMINAR)
	 */
	public void enviarMensajeTerminar( )
	{
		outConversacion.println( Conversacion.TERMINAR );
	}

	/**
	 * Recibe un mensaje enviado por la conversación
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void recibirMensaje( ) throws IOException
	{
		String mensajeRecibido = inConversacion.readLine( );
		mensajes.add( mensajeRecibido );
	}

	/**
	 * Retorna la lista de mensajes recibidos por el simulador
	 * 
	 * @return mensajes
	 */
	public LinkedList darMensajes( )
	{
		return mensajes;
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