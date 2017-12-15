package edu.jabs.messenger.testCliente;

import edu.jabs.messenger.interfazCliente.IVentanaConversacion;

/**
 * Esta clase cumple con la interfaz IVentanaConversacion.<br>
 * Su objetivo es ayudar a simplificar las pruebas que se realizan a la clase
 * Conversación.s
 */
public class SimuladorVentanaConversacion implements IVentanaConversacion
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * El último mensaje publicado en la ventana
	 */
	private String ultimoMensaje;

	/**
	 * Indica si la ventana ya fue cerrada
	 */
	private boolean cerrada;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye la ventana.
	 */
	public SimuladorVentanaConversacion( )
	{
		cerrada = false;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Se publica un mensaje. Este método, junto con darUltimoMensaje(), se usará
	 * para verificar que la conversación efectivamente haya publicado el mensaje.
	 * 
	 * @param mensaje El mensaje publicado
	 */
	public void publicarMensaje( String mensaje )
	{
		ultimoMensaje = mensaje;
	}

	/**
	 * Cierra la ventana
	 */
	public void dispose( )
	{
		cerrada = true;
	}

	/**
	 * Retorna el último mensaje recibido
	 * 
	 * @return ultimoMensaje
	 */
	public String darUltimoMensaje( )
	{
		return ultimoMensaje;
	}

	/**
	 * Indica si la ventana ya fue cerrada
	 * 
	 * @return cerrada
	 */
	public boolean estaCerrada( )
	{
		return cerrada;
	}
}