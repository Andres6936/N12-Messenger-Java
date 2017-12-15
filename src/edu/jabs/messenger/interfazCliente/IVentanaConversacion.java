package edu.jabs.messenger.interfazCliente;

/**
 * Esta interfaz define los métodos que ofrece la ventana de una conversación y
 * que le interesan a la clase Conversacion
 */
public interface IVentanaConversacion
{
	/**
	 * Muestra un mensaje en la ventana
	 * 
	 * @param mensaje El mensaje que debe ser mostrado - mensaje != null
	 */
	void publicarMensaje( String mensaje );

	/**
	 * Cierra la conversación y cierra la ventana <br>
	 * Si la ventana es cerrada por el usuario, entonces se cierra la conversación
	 * asociada también.
	 */
	void dispose( );
}
