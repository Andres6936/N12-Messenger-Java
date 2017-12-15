package edu.jabs.messenger.testCliente;

import java.io.IOException;

import edu.jabs.messenger.cliente.Conversacion;

/**
 * Esta clase se usa para construir una conversación que esperará a que alguien
 * se conecte a ella.<br>
 * La conversación esperará en un thread diferente al principal.
 */
public class AyudanteConversacionEspera extends Thread
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es el nombre del usuario asociado a la conversación
	 */
	public static final String USUARIO_ESPERA = "usuarioEspera";

	/**
	 * Es el puerto en el cual se esperá la conexión
	 */
	public static final int PUERTO_ESPERA = 9998;

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es la conversación que espera
	 */
	private Conversacion conversacion;

	/**
	 * Es el cliente asociado a la conversación
	 */
	private SimuladorClienteAmigos simuladorCliente;

	/**
	 * Es la ventana asociada a la conversación
	 */
	private SimuladorVentanaConversacion simuladorVentana;

	/**
	 * Es el nombre del amigo con el cual se supone que se está sosteniendo la
	 * conversación
	 */
	private String nombreAmigoActivo;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Crea el ayudante, pero no hace nada aún con la conversación
	 * 
	 * @param nombreAmigo El nombre del amigo con el que se realizará la
	 *            conversación
	 * @param ventana La ventana que estará asociada a la conversación
	 * @param cliente El cliente que estará asociado a la conversación
	 */
	public AyudanteConversacionEspera( String nombreAmigo, SimuladorVentanaConversacion ventana,
			SimuladorClienteAmigos cliente )
	{
		nombreAmigoActivo = nombreAmigo;
		simuladorVentana = ventana;
		simuladorCliente = cliente;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Inicia el thread y crea la conversación que espera a que alguien se conecte
	 */
	public void run( )
	{
		try
		{
			conversacion = new Conversacion( simuladorCliente, PUERTO_ESPERA, nombreAmigoActivo );
			conversacion.cambiarVentanaConversacion( simuladorVentana );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * Retorna la conversación que espera
	 * 
	 * @return conversacion
	 */
	public Conversacion darConversacion( )
	{
		return conversacion;
	}

}