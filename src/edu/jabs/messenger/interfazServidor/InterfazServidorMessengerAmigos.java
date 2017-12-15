package edu.jabs.messenger.interfazServidor;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import edu.jabs.messenger.servidor.IServidorAmigos;
import edu.jabs.messenger.servidor.ServidorAmigos;

/**
 * Esta es la ventana principal del servidor del Messenger Amigos
 */
public class InterfazServidorMessengerAmigos extends JFrame
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la clase que se encarga de manejar las conexiones
	 * entrantes al servidor
	 */
	private IServidorAmigos servidor;

	// -----------------------------------------------------------------
	// Atributos de la interfaz
	// -----------------------------------------------------------------

	/**
	 * Es el panel donde se muestran los usuarios conectados
	 */
	private PanelClientes panelClientes;

	/**
	 * Panel con las extensiones
	 */
	private PanelExtension panelExtension;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye la ventana principal de la aplicación <br>
	 * 
	 * @param servidorAmigos Es una referencia al servidor sobre el que funciona
	 *            esta interfaz
	 */
	public InterfazServidorMessengerAmigos( IServidorAmigos servidorAmigos )
	{
		servidor = servidorAmigos;
		inicializarVentana( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Inicializa los elementos de la ventana principal
	 */
	private void inicializarVentana( )
	{
		// Construye la forma
		getContentPane( ).setLayout( new BorderLayout( ) );
		setSize( 350, 430 );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		setTitle( "Servidor Messenger Amigos" );

		// Creacion de los paneles aquí
		getContentPane( ).setLayout( new GridBagLayout( ) );

		GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 0.5, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 );
		panelClientes = new PanelClientes( this );
		getContentPane( ).add( panelClientes, gbc );

		panelExtension = new PanelExtension( this );
		gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets( 5, 5, 5, 5 ), 0, 0 );
		getContentPane( ).add( panelExtension, gbc );
	}

	/**
	 * Actualiza la lista de clientes mostrada en el panelClientes
	 */
	public void actualizarClientes( )
	{
		Collection clientes = servidor.darClientes( );
		panelClientes.actualizarClientes( clientes );
	}

	/**
	 * Cierra la ventana y la aplicación
	 */
	public void dispose( )
	{
		super.dispose( );
		try
		{
			servidor.desconectarDB( );
		}
		catch ( SQLException e )
		{
			e.printStackTrace( );
		}
		System.exit( 0 );
	}

	// -----------------------------------------------------------------
	// Puntos de Extensión
	// -----------------------------------------------------------------

	/**
	 * Método para la extensión 1
	 */
	public void reqFuncOpcion1( )
	{
		String resultado = servidor.metodo1( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}

	/**
	 * Método para la extensión 2
	 */
	public void reqFuncOpcion2( )
	{
		String resultado = servidor.metodo2( );
		JOptionPane.showMessageDialog( this, resultado, "Respuesta", JOptionPane.INFORMATION_MESSAGE );
	}

	// -----------------------------------------------------------------
	// Main
	// -----------------------------------------------------------------

	/**
	 * Este método ejecuta la aplicación, creando una nueva interfaz
	 * 
	 * @param args Parámetros de ejecución. No son necesarios.
	 */
	public static void main( String[ ] args )
	{
		try
		{
			String archivoPropiedades = "./data/servidor.properties";
			IServidorAmigos servidor = new ServidorAmigos( archivoPropiedades );

			InterfazServidorMessengerAmigos interfaz = new InterfazServidorMessengerAmigos( servidor );
			interfaz.setVisible( true );

			servidor.recibirConexiones( );
		}
		catch ( Exception e )
		{
			System.out.println( e.getMessage( ) );
			e.printStackTrace( );
		}
	}

}