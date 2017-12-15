package edu.jabs.messenger.testCliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Collection;

import edu.jabs.messenger.cliente.ClienteAmigos;
import edu.jabs.messenger.cliente.Conversacion;
import edu.jabs.messenger.cliente.Usuario;
import junit.framework.TestCase;

/**
 * Esta clase verifica que los métodos de la clase ClienteAmigos estén
 * correctamente implementados
 */
public class ClienteAmigosTest extends TestCase
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es el nombre del usuario que usa el cliente que se prueba
	 */
	public static final String NOMBRE_USUARIO = "nombreUsuario";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es el objeto sobre el cual se realizan las pruebas
	 */
	private ClienteAmigos clienteAmigos;

	/**
	 * Es el objeto que simula ser un servidor
	 */
	private SimuladorServidor simuladorServidor;

	/**
	 * Es el objeto que simula ser la interfaz de la aplicación
	 */
	private SimuladorInterfaz simuladorInterfaz;

	/**
	 * Es el objeto que permite simular que hay una conversación esperando a que
	 * alguien se conecte
	 */
	private SimuladorConversacionEnEspera simuladorConversacion;

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Construye un escenario en el cual hay un cliente que se conecta a un objeto
	 * que simula ser un servidor
	 */
	private void setupEscenario1( )
	{
		try
		{
			simuladorServidor = new SimuladorServidor( );
			simuladorInterfaz = new SimuladorInterfaz( );

			clienteAmigos = new ClienteAmigos( simuladorInterfaz, "./test/data/cliente.properties" );

			simuladorServidor.start( );
			clienteAmigos.conectar( NOMBRE_USUARIO );

			String mensaje = simuladorServidor.recibirMensaje( );
			String ipLocal = InetAddress.getLocalHost( ).getHostAddress( );
			assertEquals( "El mensaje enviado por el cliente está equivocado",
					ClienteAmigos.LOGIN + ":" + NOMBRE_USUARIO + ";" + ipLocal + ";9998", mensaje );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas construyendo el escenario: " + e.getMessage( ) );
		}
	}

	/**
	 * Elimina los objetos y las conexiones creadas para el escenario 1
	 */
	private void terminarEscenario1( )
	{
		try
		{
			simuladorServidor.enviarMensajeDesconexion( );
			simuladorServidor.cerrarConexion( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas destruyendo el escenario: " + e.getMessage( ) );
		}
	}

	/**
	 * Construye un escenario en el cual hay un cliente que se conecta a un objeto
	 * que simula ser un servidor y un objeto que simula ser una conversación a la
	 * cual se tiene que unir el cliente
	 */
	private void setupEscenario2( )
	{
		try
		{
			simuladorServidor = new SimuladorServidor( );
			simuladorInterfaz = new SimuladorInterfaz( );
			simuladorConversacion = new SimuladorConversacionEnEspera( );

			clienteAmigos = new ClienteAmigos( simuladorInterfaz, "./test/data/cliente.properties" );

			simuladorServidor.start( );
			clienteAmigos.conectar( NOMBRE_USUARIO );
			simuladorConversacion.start( );

			String mensaje = simuladorServidor.recibirMensaje( );
			String ipLocal = InetAddress.getLocalHost( ).getHostAddress( );
			assertEquals( "El mensaje enviado por el cliente está equivocado",
					ClienteAmigos.LOGIN + ":" + NOMBRE_USUARIO + ";" + ipLocal + ";9998", mensaje );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas construyendo el escenario: " + e.getMessage( ) );
		}
	}

	/**
	 * Elimina los objetos y las conexiones creadas para el escenario 2
	 */
	private void terminarEscenario2( )
	{
		try
		{
			simuladorConversacion.enviarMensajeTerminar( );
			simuladorConversacion.recibirMensaje( );
			simuladorConversacion.terminarConexion( );
			simuladorServidor.enviarMensajeDesconexion( );
			simuladorServidor.cerrarConexion( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas destruyendo el escenario: " + e.getMessage( ) );
		}
	}

	/**
	 * Verifica que la conexión del cliente se establezca de forma correcta
	 */
	public void testClienteAmigos( )
	{
		setupEscenario1( );

		assertEquals( "El nombre del usuario es incorrecto", NOMBRE_USUARIO, clienteAmigos.darNombreUsuario( ) );

		terminarEscenario1( );
	}

	/**
	 * Verifica que el método actualizarEstado actualice la interfaz
	 */
	public void testActualizarEstado( )
	{
		setupEscenario1( );

		Usuario alicia = new Usuario( "Alicia" );
		clienteAmigos.actualizarEstado( alicia );

		// Verificar que se haya actualizado la interfaz
		Collection estadoAmigos = simuladorInterfaz.darEstadoAmigos( );
		assertEquals( "Debería haber un amigo", 1, estadoAmigos.size( ) );
		Usuario amigo = (Usuario) estadoAmigos.iterator( ).next( );
		assertEquals( "El estado del usuario está equivocado", Usuario.OFFLINE, amigo.darEstado( ) );
		assertEquals( "El nombre del usuario está equivocado", "Alicia", amigo.darNombre( ) );

		terminarEscenario1( );
	}

	/**
	 * Verifica que el método agregarAmigo actualice envíe el mensaje correcto al
	 * servidor
	 */
	public void testAgregarAmigo( )
	{
		setupEscenario1( );

		try
		{
			clienteAmigos.agregarAmigo( "Alicia" );

			String mensaje = simuladorServidor.recibirMensaje( );
			assertEquals( "El mensaje enviado por el cliente está equivocado", ClienteAmigos.AGREGAR_AMIGO + ":Alicia",
					mensaje );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas: " + e.getMessage( ) );
		}

		terminarEscenario1( );
	}

	/**
	 * Verifica que el método conectarAConversacion se conecte correctamente a una
	 * conversación remota
	 */
	public void testConectarAConversacion( )
	{
		setupEscenario2( );

		try
		{
			clienteAmigos.conectarAConversacion( "amigo", "localhost", SimuladorConversacionEnEspera.PUERTO_ESPERA );

			// Verificar que se le haya notificado a la interfaz de la nueva conversacion
			Conversacion conv = simuladorInterfaz.darConversacion( );
			assertNotNull( "La interfaz no fue notificada de la creación de la conversación", conv );

			// Obtener la conversación creada y revisar que esté efectivamente conectada al
			// socket
			conv.enviarMensaje( "hola" );

			String mensajeConv = simuladorConversacion.recibirMensaje( );
			assertTrue( "El mensaje no fue el esperado",
					mensajeConv.startsWith( Conversacion.MENSAJE ) && mensajeConv.indexOf( "hola" ) != -1 );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas: " + e.getMessage( ) );
		}

		terminarEscenario2( );
	}

	/**
	 * Verifica que el método crearConversacionLocal cree correctamente la
	 * conversación y permita la conexión remota
	 */
	public void testCrearConversacionLocal( )
	{
		setupEscenario1( );

		try
		{
			AyudanteConversacionLocal ayudante = new AyudanteConversacionLocal( clienteAmigos );
			ayudante.start( );

			// Revisar que se haya enviado el mensaje correcto al servidor
			String mensaje = simuladorServidor.recibirMensaje( );
			assertEquals( "El mensaje que se envió al servidor no fue el esperado",
					ClienteAmigos.CONVERSACION + ":Alicia", mensaje );

			// Establecer la conexión con el cliente
			Socket socketConversacion = new Socket( "localhost", 9998 );
			PrintWriter outConversacion = new PrintWriter( socketConversacion.getOutputStream( ), true );
			BufferedReader inConversacion = new BufferedReader(
					new InputStreamReader( socketConversacion.getInputStream( ) ) );

			// Obtener la conversación creada y revisar que esté efectivamente conectada al
			// socket
			Conversacion conv = null;
			while ( conv == null )
				conv = ayudante.darConversacion( );

			conv.enviarMensaje( "hola" );

			String mensajeConv = inConversacion.readLine( );
			assertTrue( "El mensaje no fue el esperado",
					mensajeConv.startsWith( Conversacion.MENSAJE ) && mensajeConv.indexOf( "hola" ) != -1 );

			// Cerrar la conversacion
			outConversacion.println( Conversacion.TERMINAR );
			inConversacion.readLine( ); // Leer el menasje CONVERSACION_TERMINADA

			outConversacion.close( );
			inConversacion.close( );
			socketConversacion.close( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas: " + e.getMessage( ) );
		}

		terminarEscenario1( );
	}

	/**
	 * Verifica que el método enviarDesconexion envíe correctamente el mensaje al
	 * servidor
	 */
	public void testEnviarDesconexion( )
	{
		setupEscenario1( );

		try
		{
			clienteAmigos.enviarDesconexion( );

			String mensaje = simuladorServidor.recibirMensaje( );
			assertEquals( "El mensaje enviado por el cliente está equivocado",
					ClienteAmigos.LOGOUT + ":" + NOMBRE_USUARIO, mensaje );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
			fail( "No debería haber problemas: " + e.getMessage( ) );
		}

		terminarEscenario1( );
	}

}