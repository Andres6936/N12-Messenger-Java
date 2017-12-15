package edu.jabs.messenger.testCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;

import edu.jabs.messenger.cliente.Conversacion;

/**
 * Esta clase sirve para simular el comportamiento de una conversación activa
 * que se conecta a una conversación que espera
 */
public class SimuladorConversacionActiva
{
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
	 * Es la lista donde se mantienen los mensajes recibidos
	 */
	private LinkedList mensajes;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el simulador pero no hace nada con la conversación por el momento
	 */
	public SimuladorConversacionActiva( )
	{
		mensajes = new LinkedList( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Establece una conexión con una conversación que esperaba a que alguien se
	 * conectara
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void conectar( ) throws IOException
	{
		socketConversacion = new Socket( "localhost", AyudanteConversacionEspera.PUERTO_ESPERA );
		outConversacion = new PrintWriter( socketConversacion.getOutputStream( ), true );
		inConversacion = new BufferedReader( new InputStreamReader( socketConversacion.getInputStream( ) ) );
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