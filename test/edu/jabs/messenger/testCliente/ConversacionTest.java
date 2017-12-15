package edu.jabs.messenger.testCliente;

import java.util.LinkedList;

import edu.jabs.messenger.cliente.Conversacion;
import edu.jabs.messenger.cliente.Usuario;
import junit.framework.TestCase;

/**
 * Esta es la clase usada para verificar la implementación de los métodos de la
 * clase Conversación. <br>
 * Las pruebas requieren la ayuda de numerosas clases que se encuentran en el
 * paquete uniandes.cupi2.messengerAmigos.test.conversacion <br>
 * Todas las pruebas realizadas se realizan tanto en el caso que la conversación
 * que se está verificando haya esperado una conexión como en el caso de que se
 * haya conectado a otra conversación.
 */
public class ConversacionTest extends TestCase
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Indica el tiempo que se va a esperar cuando se realicen acciones en threads
	 * diferentes al principal
	 */
	public static final long TIEMPO_ESPERA = 100;

	/**
	 * Es el nombre del usuario que se supone establece una conexión activa
	 */
	public static final String USUARIO_ACTIVO = "usuarioActivo";

	/**
	 * Es el nombre del usuario que se supone establece una conversación que espera
	 */
	public static final String USUARIO_ESPERA = "usuarioEspera";

	/**
	 * Es el nombre de la persona que se conecta a la conversación que espera
	 */
	public static final String AMIGO_ACTIVO = "amigoActivo";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es la conversación que inicia una conexión hacia la otra conversación
	 */
	private Conversacion conversacionActiva;

	/**
	 * Es la conversación que espera a que alguien se conecte a ella
	 */
	private Conversacion conversacionEspera;

	/**
	 * Es el objeto que simula ser una conversación que espera a que la conversación
	 * activa se conecte
	 */
	private SimuladorConversacionEspera simuladorEspera;

	/**
	 * Es el objeto que simula ser una conversación que se conecta a una
	 * conversación que espera
	 */
	private SimuladorConversacionActiva simuladorActiva;

	/**
	 * Es el objeto que simula ser el clienteAmigos asociado a una conversación
	 */
	private SimuladorClienteAmigos simuladorClienteAmigos;

	/**
	 * Es el objeto que simula ser la ventana asociada a una conversación
	 */
	private SimuladorVentanaConversacion simuladorVentana;

	/**
	 * Es el objeto que permite que una conversación quede esperando una conexión,
	 * desde un thread diferente al principal
	 */
	private AyudanteConversacionEspera ayudanteConversacionEspera;

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * En este escenario una conversación es creada para que se conecte a un objeto
	 * que simula ser una conversación. <br>
	 * Después de establecer la conexión se verifica que la conversación funcione
	 * correctamente. <br>
	 * La conversación será llamada conversacionActiva.
	 */
	private void setupEscenario1( )
	{
		try
		{
			// Crear el objeto que simulará ser el cliente amigos que necesita la
			// conversación
			simuladorClienteAmigos = new SimuladorClienteAmigos( USUARIO_ACTIVO );

			// Crear el objeto que simulará ser la ventana de la conversación
			simuladorVentana = new SimuladorVentanaConversacion( );

			// Crear el objeto que esperará la conexión de la conversación activa
			simuladorEspera = new SimuladorConversacionEspera( );
			simuladorEspera.start( );

			// Darle tiempo al socket para prepararse
			Thread.sleep( TIEMPO_ESPERA );

			// Crear la conversacion activa
			Usuario amigo = new Usuario( SimuladorConversacionEspera.AMIGO_ESPERA, "localhost",
					SimuladorConversacionEspera.PUERTO_ESPERA );
			conversacionActiva = new Conversacion( simuladorClienteAmigos, amigo );
			conversacionActiva.cambiarVentanaConversacion( simuladorVentana );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción creando la conversación activa: " + e.getMessage( ) );
		}
	}

	/**
	 * En este escenario un objeto que simula ser una conversación se conecta a una
	 * conversación que estaba esperando una conexión. <br>
	 * Después de establecer la conexión se verifica que la conversación funcione
	 * correctamente. <br>
	 * La conversación será llamada conversacionEspera.
	 */
	private void setupEscenario2( )
	{
		try
		{
			// Crear el objeto que simulará ser el cliente amigos que necesita la
			// conversación
			simuladorClienteAmigos = new SimuladorClienteAmigos( USUARIO_ESPERA );

			// Crear el objeto que simulará ser la ventana de la conversación
			simuladorVentana = new SimuladorVentanaConversacion( );

			// Crear el objeto que creará la conversación que esperará a que alguien se
			// conecte
			ayudanteConversacionEspera = new AyudanteConversacionEspera( AMIGO_ACTIVO, simuladorVentana,
					simuladorClienteAmigos );
			ayudanteConversacionEspera.start( );

			// Conectarse a la conversación en espera usando un simulador de una
			// conversacion activa
			simuladorActiva = new SimuladorConversacionActiva( );
			simuladorActiva.conectar( );

			// Darle tiempo al socket para prepararse
			Thread.sleep( TIEMPO_ESPERA );

			// Obtener la conversación en espera
			conversacionEspera = ayudanteConversacionEspera.darConversacion( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción creando la conversación en espera: " + e.getMessage( ) );
		}
	}

	/**
	 * Este método se encarga de deshacer lo construído al preparar el escenario 1
	 * <br>
	 * La conversación activa debe ser terminada, al igual que el simulador de la
	 * conversación en espera. <br>
	 * El éxito de esta tarea depende de que la conversación termine correctamente
	 * cuando reciba el mensaje TERMINAR.
	 */
	private void terminarEscenario1( )
	{
		try
		{
			// Enviar el mensaje TERMINAR
			simuladorEspera.enviarMensajeTerminar( );

			// Esperar el mensaje CONVERSACION_TERMINADA
			simuladorEspera.recibirMensaje( );
			LinkedList mensajes = simuladorEspera.darMensajes( );
			assertTrue( "Debería haber un mensaje de respuesta", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser CONVERSACION_TERMINADA, pero fue " + ultimoMensaje,
					ultimoMensaje.startsWith( Conversacion.CONVERSACION_TERMINADA ) );

			// Darle tiempo a la conversacion para cerrarse
			Thread.sleep( TIEMPO_ESPERA );

			// Verificar que la conversación y su ventana se hayan cerrado
			assertTrue( "La ventana debería estar cerrada", simuladorVentana.estaCerrada( ) );
			assertTrue( "La conversacion debería haber terminado", conversacionActiva.conversacionTerminada( ) );

			// Cerrar el simulador de la conversación en espera
			simuladorEspera.terminarConexion( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción terminando la conversación activa: " + e.getMessage( ) );
		}
	}

	/**
	 * Este método se encarga de deshacer lo construído al preparar el escenario 2
	 * <br>
	 * La conversación que esperaba debe ser terminada, al igual que el simulador de
	 * la conversación activa. <br>
	 * El éxito de esta tarea depende de que la conversación termine correctamente
	 * cuando reciba el mensaje TERMINAR.
	 */
	private void terminarEscenario2( )
	{
		try
		{
			// Enviar el mensaje TERMINAR
			simuladorActiva.enviarMensajeTerminar( );

			// Esperar el mensaje CONVERSACION_TERMINADA
			simuladorActiva.recibirMensaje( );
			LinkedList mensajes = simuladorActiva.darMensajes( );
			assertTrue( "Debería haber un mensaje de respuesta", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser CONVERSACION_TERMINADA pero fue " + ultimoMensaje,
					ultimoMensaje.startsWith( Conversacion.CONVERSACION_TERMINADA ) );

			// Darle tiempo a la conversacion para cerrarse
			Thread.sleep( TIEMPO_ESPERA );

			// Verificar que la conversación y su ventana se hayan cerrado
			assertTrue( "La ventana debería estar cerrada", simuladorVentana.estaCerrada( ) );
			assertTrue( "La conversacion debería haber terminado", conversacionEspera.conversacionTerminada( ) );

			// Cerrar el simulador de la conversación en espera
			simuladorActiva.terminarConexion( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción terminando la conversación en espera: " + e.getMessage( ) );
		}
	}

	/**
	 * Verifica que se haya podido crear una conversación activa (que se conecta a
	 * un objeto que simula ser una conversación en espera). <br>
	 * Además se verifica que la conversación termine correctamente cuando se le
	 * envía el mensaje adecuado (TERMINAR).
	 */
	public void testConversacionActiva( )
	{
		setupEscenario1( );

		assertFalse( "La conversacion NO debería haber terminado", conversacionActiva.conversacionTerminada( ) );

		terminarEscenario1( );
	}

	/**
	 * Verifica que se haya podido crear una conversación en espera (a la que se
	 * conecta a un objeto que simula ser una conversación activa). <br>
	 * Además se verifica que la conversación termine correctamente cuando se le
	 * envía el mensaje adecuado (TERMINAR).
	 */
	public void testConversacionEspera( )
	{
		setupEscenario2( );

		assertFalse( "La conversacion NO debería haber terminado", conversacionEspera.conversacionTerminada( ) );

		terminarEscenario2( );
	}

	/**
	 * Verifica el método darNombreAmigo en el caso de la conversación activa
	 */
	public void testDarNombreAmigo1( )
	{
		setupEscenario1( );

		assertEquals( "El nombre del amigo en la conversación no es correcto", SimuladorConversacionEspera.AMIGO_ESPERA,
				conversacionActiva.darNombreAmigo( ) );

		terminarEscenario1( );
	}

	/**
	 * Verifica el método darNombreAmigo en el caso de la conversación en espera
	 */
	public void testDarNombreAmigo2( )
	{
		setupEscenario2( );

		assertEquals( "El nombre del amigo en la conversación no es correcto", AMIGO_ACTIVO,
				conversacionEspera.darNombreAmigo( ) );

		terminarEscenario2( );
	}

	/**
	 * Verifica el método enviarMensaje en el caso de la conversación activa. <br>
	 * El mensaje debería ser enviado a la conversación remota y debería ser
	 * publicado en la ventana de la conversación.
	 */
	public void testEnviarMensaje1( )
	{
		setupEscenario1( );

		try
		{
			conversacionActiva.enviarMensaje( "prueba mensaje" );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( USUARIO_ACTIVO ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorEspera.recibirMensaje( );
			LinkedList mensajes = simuladorEspera.darMensajes( );
			assertTrue( "Debería estar el mensaje enviado", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'prueba mensaje'", ultimoMensaje.indexOf( "prueba mensaje" ) != -1 );
			mensajes.clear( );

		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario1( );
		}
	}

	/**
	 * Verifica el método enviarMensaje en el caso de la conversación en espera.
	 * <br>
	 * El mensaje debería ser enviado a la conversación remota y debería ser
	 * publicado en la ventana de la conversación.
	 */
	public void testEnviarMensaje2( )
	{
		setupEscenario2( );

		try
		{
			conversacionEspera.enviarMensaje( "prueba mensaje" );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( USUARIO_ESPERA ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorActiva.recibirMensaje( );
			LinkedList mensajes = simuladorActiva.darMensajes( );
			assertTrue( "Debería estar el mensaje enviado", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'prueba mensaje'", ultimoMensaje.indexOf( "prueba mensaje" ) != -1 );
			mensajes.clear( );

		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario2( );
		}
	}

	/**
	 * Verifica que la conversación activa reciba correctamente los mensajes
	 * enviados desde la otra conversación
	 */
	public void testRecibirMensajes1( )
	{
		setupEscenario1( );

		try
		{
			// Enviar el mensaje desde la conversación remota
			simuladorEspera.enviarMensaje( "prueba mensaje" );

			// Darle tiempo a la conversacion para recibir y publicar el mensaje
			Thread.sleep( TIEMPO_ESPERA );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( SimuladorConversacionEspera.AMIGO_ESPERA ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario1( );
		}
	}

	/**
	 * Verifica que la conversación que espera reciba correctamente los mensajes
	 * enviados desde la otra conversación
	 */
	public void testRecibirMensajes2( )
	{
		setupEscenario2( );

		try
		{
			// Enviar el mensaje desde la conversación remota
			simuladorActiva.enviarMensaje( "prueba mensaje" );

			// Darle tiempo a la conversacion para recibir y publicar el mensaje
			Thread.sleep( TIEMPO_ESPERA );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( AMIGO_ACTIVO ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario2( );
		}
	}

	/**
	 * Verifica que el método mostrarMensajeRecibido de la conversación activa
	 * publique los mensajes recibidos en la ventana
	 */
	public void testMostrarMensajeRecibido1( )
	{
		setupEscenario1( );

		try
		{
			// Enviar el mensaje directamente a la conversación
			conversacionActiva.mostrarMensajeRecibido( "prueba mensaje" );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( SimuladorConversacionEspera.AMIGO_ESPERA ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario1( );
		}
	}

	/**
	 * Verifica que el método mostrarMensajeRecibido de la conversación que espera
	 * publique los mensajes recibidos en la ventana
	 */
	public void testMostrarMensajeRecibido2( )
	{
		setupEscenario2( );

		try
		{
			// Enviar el mensaje directamente a la conversación
			conversacionEspera.mostrarMensajeRecibido( "prueba mensaje" );

			// Verificar que el mensaje haya sido publicado en la ventana local
			String mensajePublicado = simuladorVentana.darUltimoMensaje( );
			assertTrue( "El mensaje publicado no tiene el remitente correcto",
					mensajePublicado.startsWith( AMIGO_ACTIVO ) );
			assertTrue( "El mensaje publicado no tiene el mensaje correcto",
					mensajePublicado.indexOf( "prueba mensaje" ) != -1 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario2( );
		}
	}

	/**
	 * Verifica que el método terminar de la conversación activa envíe el mensaje
	 * TERMINAR a la otra conversación
	 */
	public void testTerminar1( )
	{
		setupEscenario1( );

		try
		{
			// Enviar el mensaje TERMINAR
			conversacionActiva.terminar( );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorEspera.recibirMensaje( );
			LinkedList mensajes = simuladorEspera.darMensajes( );
			assertTrue( "Debería estar el mensaje TERMINAR ", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'TERMINAR'", ultimoMensaje.startsWith( Conversacion.TERMINAR ) );
			mensajes.clear( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario1( );
		}
	}

	/**
	 * Verifica que el método terminar de la conversación que espera envíe el
	 * mensaje TERMINAR a la otra conversación
	 */
	public void testTerminar2( )
	{
		setupEscenario2( );

		try
		{
			// Enviar el mensaje TERMINAR
			conversacionEspera.terminar( );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorActiva.recibirMensaje( );
			LinkedList mensajes = simuladorActiva.darMensajes( );
			assertTrue( "Debería estar el mensaje TERMINAR ", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'TERMINAR'", ultimoMensaje.startsWith( Conversacion.TERMINAR ) );
			mensajes.clear( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario2( );
		}
	}

	/**
	 * Verifica que el método enviarConfirmacionTerminarConversacion de la
	 * conversación activa envíe el mensaje CONVERSACION_TERMINADA a la otra
	 * conversación
	 */
	public void testEnviarConfirmacionTerminarConversacion1( )
	{
		setupEscenario1( );

		try
		{
			// Enviar el mensaje CONVERSACION_TERMINADA
			conversacionActiva.enviarConfirmacionTerminarConversacion( );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorEspera.recibirMensaje( );
			LinkedList mensajes = simuladorEspera.darMensajes( );
			assertTrue( "Debería estar el mensaje CONVERSACION_TERMINADA ", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'CONVERSACION_TERMINADA'",
					ultimoMensaje.startsWith( Conversacion.CONVERSACION_TERMINADA ) );
			mensajes.clear( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario1( );
		}
	}

	/**
	 * Verifica que el método enviarConfirmacionTerminarConversacion de la
	 * conversación que espera envíe el mensaje CONVERSACION_TERMINADA a la otra
	 * conversación
	 */
	public void testEnviarConfirmacionTerminarConversacion2( )
	{
		setupEscenario2( );

		try
		{
			// Enviar el mensaje CONVERSACION_TERMINADA
			conversacionEspera.enviarConfirmacionTerminarConversacion( );

			// Verificar que el mensaje haya llegado al otro cliente
			simuladorActiva.recibirMensaje( );
			LinkedList mensajes = simuladorActiva.darMensajes( );
			assertTrue( "Debería estar el mensaje CONVERSACION_TERMINADA ", mensajes.size( ) > 0 );
			String ultimoMensaje = (String) mensajes.getLast( );
			assertTrue( "El mensaje debería ser 'CONVERSACION_TERMINADA'",
					ultimoMensaje.startsWith( Conversacion.CONVERSACION_TERMINADA ) );
			mensajes.clear( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			fail( "No debería producirse una excepción enviando o recibiendo un mensaje: " + e.getMessage( ) );
		}
		finally
		{
			terminarEscenario2( );
		}
	}
}
