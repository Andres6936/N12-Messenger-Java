package edu.jabs.messenger.testCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.jabs.messenger.cliente.ClienteAmigos;

/**
 * Esta clase simula el comportamiento de un servidor para las pruebas de la
 * clase ClienteAmigos
 */
public class SimuladorServidor extends Thread
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el socket a través del cual se lleva a cabo la comunicación con el cliente
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
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Espera una conexión para que el cliente se pueda conectar al servidor
	 */
	public void run( )
	{
		try
		{
			ServerSocket ssocket = new ServerSocket( 9999 );
			socketCliente = ssocket.accept( );
			outCliente = new PrintWriter( socketCliente.getOutputStream( ), true );
			inCliente = new BufferedReader( new InputStreamReader( socketCliente.getInputStream( ) ) );

			ssocket.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Cierra la conexión del servidor con el cliente
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void cerrarConexion( ) throws IOException
	{
		inCliente.close( );
		outCliente.close( );
		socketCliente.close( );
	}

	/**
	 * Lee un mensaje enviado por el cliente y lo retorna
	 * 
	 * @return mensaje
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public String recibirMensaje( ) throws IOException
	{
		return inCliente.readLine( );
	}

	/**
	 * Envía al cliente un mensaje que indica que se puede desconectar (DESCONEXION)
	 */
	public void enviarMensajeDesconexion( )
	{
		outCliente.println( ClienteAmigos.DESCONEXION );
	}

}