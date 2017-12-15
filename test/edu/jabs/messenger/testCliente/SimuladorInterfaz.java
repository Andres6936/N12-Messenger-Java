package edu.jabs.messenger.testCliente;

import java.util.Collection;

import edu.jabs.messenger.cliente.Conversacion;
import edu.jabs.messenger.interfazCliente.IInterfazCliente;

/**
 * Esta clase sirve para simular la interfaz de la aplicación, a la cual el
 * cliente tiene que informarle de varios sucesos durante la ejecución de la
 * aplicación
 */
public class SimuladorInterfaz implements IInterfazCliente
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es la información sobre el estado de los amigos que envía el cliente a la
	 * interfaz
	 */
	private Collection estadoAmigos;

	/**
	 * Es la conversación que el cliente crea y para la cual la interfaz debería
	 * crear una ventana
	 */
	private Conversacion conversacion;

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * La implementación de este método permite conocer la información que el
	 * cliente le envió a la interfaz para actualizar el estado de los amigos.
	 * 
	 * @param amigos Es la información sobre el estado de los amigos del usuario
	 */
	public void actualizarAmigos( Collection amigos )
	{
		estadoAmigos = amigos;
	}

	/**
	 * Retorna la información que el cliente envió a la interfaz sobre el estado de
	 * los amigos
	 * 
	 * @return estadoAmigos
	 */
	public Collection darEstadoAmigos( )
	{
		return estadoAmigos;
	}

	/**
	 * La implementación de este método permite saber si el cliente le notificó a la
	 * interfaz la necesidad de crear una ventana para una nueva conversación
	 */
	public void crearVentanaConversacion( Conversacion conv )
	{
		conversacion = conv;
	}

	/**
	 * Retorna la conversación que fue enviada por el cliente
	 * 
	 * @return conversacion
	 */
	public Conversacion darConversacion( )
	{
		return conversacion;
	}

	/**
	 * Este método no es necesario para las pruebas así que no es implementado
	 */
	public void terminarAplicacion( )
	{
	}

	/**
	 * Este método no es necesario para las pruebas así que no es implementado
	 */
	public void actualizarEstadoInterfaz( )
	{
	}
}