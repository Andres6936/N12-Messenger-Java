package edu.jabs.messenger.testCliente;

import java.io.IOException;

import edu.jabs.messenger.cliente.Conversacion;
import edu.jabs.messenger.cliente.IClienteMessenger;
import edu.jabs.messenger.cliente.Usuario;

/**
 * Esta clase cumple con la interfaz IClienteMessenger, aunque no implementa
 * todos los métodos. <br>
 * Su objetivo es ayudar a simplificar las pruebas que se realizan a la clase
 * Conversación.
 */
public class SimuladorClienteAmigos implements IClienteMessenger
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el nombre del usuario que se supone está conectado usando el cliente
	 */
	private String nombreUsuario;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el simulador
	 * 
	 * @param nombre El nombre del usuario que se supone utiliza el cliente
	 */
	public SimuladorClienteAmigos( String nombre )
	{
		nombreUsuario = nombre;
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Retorna el nombre del usuario
	 * 
	 * @return nombreUsuario
	 */
	public String darNombreUsuario( )
	{
		return nombreUsuario;
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @return false
	 */
	public boolean estaConectado( )
	{
		return false;
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param usuario
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void conectar( String usuario ) throws IOException
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 */
	public void enviarDesconexion( )
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void desconectar( ) throws IOException
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param nombreAmigo
	 */
	public void agregarAmigo( String nombreAmigo )
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param amigo
	 */
	public void actualizarEstado( Usuario amigo )
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param amigo
	 * @return null
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public Conversacion crearConversacionLocal( String amigo ) throws IOException
	{
		return null;
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param ipAmigo
	 * @param nombreAmigo
	 * @param puertoAmigo
	 * @throws IOException Se lanza esta excepción si hay problemas en la
	 *             comunicación
	 */
	public void conectarAConversacion( String nombreAmigo, String ipAmigo, int puertoAmigo ) throws IOException
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @param conv
	 */
	public void eliminarConversacion( Conversacion conv )
	{
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @return null
	 */
	public String metodo1( )
	{
		return null;
	}

	/**
	 * Este método no está implementado porque no es necesario para las pruebas
	 * 
	 * @return null
	 */
	public String metodo2( )
	{
		return null;
	}

}