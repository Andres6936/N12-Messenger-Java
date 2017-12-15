package edu.jabs.messenger.testCliente;

import java.io.IOException;

import edu.jabs.messenger.cliente.ClienteAmigos;
import edu.jabs.messenger.cliente.Conversacion;

/**
 * Esta clase sirve para facilitar las pruebas de la clase ClienteAmigos.<br>
 * Esta clase permite crear una conversación en un thread diferente al principal
 * y hacer que esta espere una conexión.
 */
public class AyudanteConversacionLocal extends Thread
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el cliente amigos sobre el que se están realizando las pruebas
	 */
	private ClienteAmigos clienteAmigos;

	/**
	 * Es la conversación local que el cliente va a crear
	 */
	private Conversacion conversacion;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Crea el ayudante pero no crea todavía la conversación
	 * 
	 * @param cliente Es el cliente sobre el que se están haciendo las pruebas
	 */
	public AyudanteConversacionLocal( ClienteAmigos cliente )
	{
		clienteAmigos = cliente;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Crea la conversación
	 */
	public void run( )
	{
		try
		{
			conversacion = clienteAmigos.crearConversacionLocal( "Alicia" );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Retorna la conversación creada
	 * 
	 * @return conversacion
	 */
	public Conversacion darConversacion( )
	{
		return conversacion;
	}

}