package edu.jabs.messenger.interfazCliente;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.jabs.messenger.cliente.ClienteAmigos;
import edu.jabs.messenger.cliente.Conversacion;
import edu.jabs.messenger.cliente.IClienteMessenger;

/**
 * Esta es la ventana principal de la aplicación.
 */
public class InterfazClienteMessengerAmigos extends JFrame implements IInterfazCliente
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la clase principal del cliente
	 */
	private IClienteMessenger clienteAmigos;

	/**
	 * Indica que se inició el proceso para salir de la aplicación
	 */
	private boolean saliendo;

	// -----------------------------------------------------------------
	// Atributos de la interfaz
	// -----------------------------------------------------------------

	/**
	 * El panel donde se muestran los amigos del usuario
	 */
	private PanelAmigos panelAmigos;

	/**
	 * Es el menú donde están las opciones para el cliente
	 */
	private BarraMenu barraMenu;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye la ventana e inicializa sus componentes
	 * 
	 * @param archivoConfiguracion Es el nombre del archivo que contiene la
	 *            configuración para el cliente
	 * @throws Exception Se lanza esta excepción si hay problemas con el archivo
	 */
	public InterfazClienteMessengerAmigos( String archivoConfiguracion ) throws Exception
	{
		// Crea la clase principal
		clienteAmigos = new ClienteAmigos( this, archivoConfiguracion );

		// Construye la forma
		getContentPane( ).setLayout( new BorderLayout( ) );
		setSize( 280, 530 );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setTitle( "Messenger Amigos" );

		panelAmigos = new PanelAmigos( this );
		getContentPane( ).add( panelAmigos );

		barraMenu = new BarraMenu( this );
		setJMenuBar( barraMenu );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Inicia la conexión del cliente con el servidor: <br>
	 * pide al usuario que indique su nombre y luego se conecta al servidor.
	 */
	public void iniciarConexion( )
	{
		String nombre = JOptionPane.showInputDialog( this, "Nombre del Usuario" );
		if ( nombre != null && nombre.trim( ).length( ) > 0 )
		{
			try
			{
				clienteAmigos.conectar( nombre );
				actualizarEstadoInterfaz( );
			}
			catch ( IOException e )
			{
				JOptionPane.showMessageDialog( this, "Hubo un error realizando la conexión:" + e.getMessage( ),
						"Error en la Conexión", JOptionPane.ERROR_MESSAGE );
				e.printStackTrace( );
			}
		}
	}

	/**
	 * Cierra la conexión del cliente con el servidor
	 */
	public void terminarConexion( )
	{
		clienteAmigos.enviarDesconexion( );
	}

	/**
	 * Inicia el proceso de finalización de la aplicación. <br>
	 * El cliente se debe desconectar y todas las conversaciones se deben terminar.
	 * Cuando esto se haya hecho entonces se cierra la ventana y la aplicación.<br>
	 * Si el cliente no estaba conectado entonces la aplicación simplemente se
	 * cierra.
	 */
	public void iniciarFinalizacionAplicacion( )
	{
		if ( clienteAmigos.estaConectado( ) )
		{
			saliendo = true;
			clienteAmigos.enviarDesconexion( );
		}
		else
		{
			saliendo = true;
			terminarAplicacion( );
		}
	}

	/**
	 * Si se había solicitado que se terminara el programa, se cierra la ventana y
	 * la aplicación.<br>
	 * <b>pre:</b>El cliente está desconectado del servidor y no hay conversaciones
	 * en curso.
	 */
	public void terminarAplicacion( )
	{
		if ( saliendo )
		{
			super.dispose( );
			System.exit( 0 );
		}
	}

	/**
	 * Cuando se cierra la ventana, se inicia el proceso de finalización de la
	 * aplicación
	 */
	public void dispose( )
	{
		saliendo = true;
		iniciarFinalizacionAplicacion( );
	}

	/**
	 * Actualiza la interfaz según el estado de la conexión: <br>
	 * Se actualiza el título de la ventana y se activan o desactivan opciones de la
	 * barra de menú
	 */
	public void actualizarEstadoInterfaz( )
	{
		if ( clienteAmigos.estaConectado( ) )
		{
			setTitle( "Messenger Amigos: " + clienteAmigos.darNombreUsuario( ) );
			barraMenu.activarDesconexion( );
		}
		else
		{
			setTitle( "Messenger Amigos (desconectado)" );
			barraMenu.activarConexion( );
		}
	}

	/**
	 * Actualiza la lista de amigos mostrada
	 * 
	 * @param amigos Una colección con los amigos (Usuario) del usuario - amigos !=
	 *            null
	 */
	public void actualizarAmigos( Collection amigos )
	{
		panelAmigos.actualizarAmigos( amigos );
		validate( );
	}

	/**
	 * Agrega un amigo al usuario actual. Este método debe pedirle al usuario que
	 * indique el nombre del amigo. <br>
	 * <b>pre: </b>El cliente está conectado
	 */
	public void agregarAmigo( )
	{
		String nombre = JOptionPane.showInputDialog( this, "Nombre del Nuevo Amigo" );
		if ( nombre != null && nombre.trim( ).length( ) > 0 )
		{
			clienteAmigos.agregarAmigo( nombre );
		}
	}

	/**
	 * Crea una nueva ventana para una conversación
	 * 
	 * @param conv La conversación asociada a la nueva ventana - conv != null
	 */
	public void crearVentanaConversacion( Conversacion conv )
	{
		VentanaConversacion ventana = new VentanaConversacion( conv );
		ventana.setLocation( getLocation( ) );
		ventana.setVisible( true );
	}

	/**
	 * Inicia una nueva conversación con un amigo
	 * 
	 * @param nombreAmigo El nombre del amigo con el que se va a iniciar la
	 *            conversación - nombreAmigo != null
	 */
	public void iniciarConversacion( String nombreAmigo )
	{
		try
		{
			Conversacion conversacion = clienteAmigos.crearConversacionLocal( nombreAmigo );
			crearVentanaConversacion( conversacion );
		}
		catch ( IOException e )
		{
			JOptionPane.showMessageDialog( this, "Error creando la conversacion: " + e.getMessage( ) );
		}
	}

	// -----------------------------------------------------------------
	// Puntos de Extensión
	// -----------------------------------------------------------------

	/**
	 * Método para la extensión 1
	 */
	public void reqFuncOpcion1( )
	{
		String resultado = clienteAmigos.metodo1( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}

	/**
	 * Método para la extensión 2
	 */
	public void reqFuncOpcion2( )
	{
		String resultado = clienteAmigos.metodo2( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}

	// -----------------------------------------------------------------
	// Main
	// -----------------------------------------------------------------

	/**
	 * Este método ejecuta la aplicación, creando una nueva interfaz
	 * 
	 * @param args Parámetros de ejecución. No son necesarios, pero si se usan el
	 *            primero debe ser la ruta al archivo de configuración.
	 */
	public static void main( String[ ] args )
	{
		try
		{
			String archivo = "./data/cliente.properties";
			if ( args.length > 0 )
				archivo = args[ 0 ];

			InterfazClienteMessengerAmigos interfaz = new InterfazClienteMessengerAmigos( archivo );
			interfaz.setVisible( true );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}

}