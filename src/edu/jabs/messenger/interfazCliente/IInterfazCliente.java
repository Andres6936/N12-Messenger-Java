package edu.jabs.messenger.interfazCliente;

import java.util.Collection;

import edu.jabs.messenger.cliente.Conversacion;

/**
 * Esta interfaz define los métodos que ofrece la interfaz del cliente del
 * messenger y que le interesan a la clase ClienteAmigos
 */
public interface IInterfazCliente
{
	/**
	 * Si se había solicitado que se terminara el programa, se cierra la ventana y
	 * la aplicación. <br>
	 * <b>pre: </b>El cliente está desconectado del servidor y no hay conversaciones
	 * en curso.
	 */
	void terminarAplicacion( );

	/**
	 * Actualiza la lista de amigos mostrada
	 * 
	 * @param amigos Una colección con los amigos (Usuario) del usuario - amigos !=
	 *            null
	 */
	void actualizarAmigos( Collection amigos );

	/**
	 * Actualiza la interfaz según el estado de la conexión: <br>
	 * Se actualiza el título de la ventana y se activan o desactivan opciones de la
	 * barra de menú
	 */
	void actualizarEstadoInterfaz( );

	/**
	 * Crea una nueva ventana para una conversación
	 * 
	 * @param conv La conversación asociada a la nueva ventana - conv != null
	 */
	void crearVentanaConversacion( Conversacion conv );
}