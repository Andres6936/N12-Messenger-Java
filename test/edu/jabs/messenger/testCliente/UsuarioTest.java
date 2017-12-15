package edu.jabs.messenger.testCliente;

import edu.jabs.messenger.cliente.Usuario;
import junit.framework.TestCase;

/**
 * Esta clase se encarga de verificar la correcta implementación de los métodos
 * de la clase Usuario
 */
public class UsuarioTest extends TestCase
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el objeto donde se harán las pruebas
	 */
	private Usuario usuario;

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Construye un nuevo Usuario desconectado para las pruebas
	 */
	private void setupEscenario1( )
	{
		usuario = new Usuario( "Pedro" );
	}

	/**
	 * Construye un nuevo Usuario desconectado para las pruebas
	 */
	private void setupEscenario2( )
	{
		usuario = new Usuario( "Pedro", "127.0.0.1", 9998 );
	}

	/**
	 * Verifica el constructor con sólo un parámetro
	 */
	public void testUsuario1( )
	{
		setupEscenario1( );

		assertEquals( "El estado del usuario es incorrecto", Usuario.OFFLINE, usuario.darEstado( ) );
		assertEquals( "El nombre del usuario es incorrecto", "Pedro", usuario.darNombre( ) );
	}

	/**
	 * Verifica el constructor con varios parámetros
	 */
	public void testUsuario2( )
	{
		setupEscenario2( );

		assertEquals( "El estado del usuario es incorrecto", Usuario.ONLINE, usuario.darEstado( ) );
		assertEquals( "El nombre del usuario es incorrecto", "Pedro", usuario.darNombre( ) );
		assertEquals( "La dirección IP del usuario es incorrecta", "127.0.0.1", usuario.darDireccionIp( ) );
		assertEquals( "El puerto del usuario es incorrecto", 9998, usuario.darPuerto( ) );
	}

	/**
	 * Verifica el método cambiarEstado()
	 */
	public void testCambiarEstado1( )
	{
		setupEscenario2( );

		usuario.cambiarEstado( );
		assertEquals( "El estado del usuario es incorrecto", Usuario.OFFLINE, usuario.darEstado( ) );
		assertEquals( "El nombre del usuario es incorrecto", "Pedro", usuario.darNombre( ) );
	}

	/**
	 * Verifica el método cambiarEstado(String, int)
	 */
	public void testCambiarEstado2( )
	{
		setupEscenario1( );

		usuario.cambiarEstado( "127.0.0.1", 9998 );
		assertEquals( "El estado del usuario es incorrecto", Usuario.ONLINE, usuario.darEstado( ) );
		assertEquals( "El nombre del usuario es incorrecto", "Pedro", usuario.darNombre( ) );
		assertEquals( "La dirección IP del usuario es incorrecta", "127.0.0.1", usuario.darDireccionIp( ) );
		assertEquals( "El puerto del usuario es incorrecto", 9998, usuario.darPuerto( ) );
	}
}
