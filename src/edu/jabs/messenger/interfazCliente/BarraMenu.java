package edu.jabs.messenger.interfazCliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Esta es la clase donde está definido el menú de la aplicación del cliente
 */
public class BarraMenu extends JMenuBar implements ActionListener
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Es el comando de la opción Conectar
	 */
	private static final String CONECTAR = "Conectar";

	/**
	 * Es el comando de la opción Desconectar
	 */
	private static final String DESCONECTAR = "DesConectar";

	/**
	 * Es el comando de la opción Agregar Amigo
	 */
	private static final String AGREGAR_AMIGO = "AgregarAmigo";

	/**
	 * Es el comando de la opción Salir
	 */
	private static final String SALIR = "salir";

	/**
	 * Es el comando de la opción 1
	 */
	private static final String OPCION_1 = "Opcion1";

	/**
	 * Es el comando de la opción 2
	 */
	private static final String OPCION_2 = "Opcion2";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la clase principal de la interfaz del cliente
	 */
	private InterfazClienteMessengerAmigos principal;

	// -----------------------------------------------------------------
	// Atributos de la Interfaz
	// -----------------------------------------------------------------

	/**
	 * Es el menú Messenger de la Aplicación
	 */
	private JMenu menuMessenger;

	/**
	 * Es la opción Conectar del menú Messenger
	 */
	private JMenuItem itemConectar;

	/**
	 * Es la opción Desconectar del menú Messenger
	 */
	private JMenuItem itemDesconectar;

	/**
	 * Es la opción Agregar Amigo del menú Messenger
	 */
	private JMenuItem itemAgregarAmigo;

	/**
	 * Es la opción Salir del menú Messenger
	 */
	private JMenuItem itemSalir;

	/**
	 * Es el menú Extensiones del Messenger
	 */
	private JMenu menuExtension;

	/**
	 * Es la opción 1
	 */
	private JMenuItem itemOpcion1;

	/**
	 * Es la opción 2
	 */
	private JMenuItem itemOpcion2;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el menú para la aplicación
	 * 
	 * @param padre Es una referencia a la ventana principal del cliente
	 */
	public BarraMenu( InterfazClienteMessengerAmigos padre )
	{
		principal = padre;

		menuMessenger = new JMenu( "Messenger" );
		add( menuMessenger );

		itemConectar = new JMenuItem( "Conectar" );
		itemConectar.setActionCommand( CONECTAR );
		itemConectar.addActionListener( this );
		menuMessenger.add( itemConectar );

		itemDesconectar = new JMenuItem( "Desconectar" );
		itemDesconectar.setActionCommand( DESCONECTAR );
		itemDesconectar.addActionListener( this );
		itemDesconectar.setEnabled( false );
		menuMessenger.add( itemDesconectar );

		menuMessenger.addSeparator( );

		itemAgregarAmigo = new JMenuItem( "Agregar Amigo" );
		itemAgregarAmigo.setActionCommand( AGREGAR_AMIGO );
		itemAgregarAmigo.addActionListener( this );
		itemAgregarAmigo.setEnabled( false );
		menuMessenger.add( itemAgregarAmigo );

		menuMessenger.addSeparator( );

		itemSalir = new JMenuItem( "Salir" );
		itemSalir.setActionCommand( SALIR );
		itemSalir.addActionListener( this );
		menuMessenger.add( itemSalir );

		menuExtension = new JMenu( "Extensión" );
		add( menuExtension );

		itemOpcion1 = new JMenuItem( "Opción 1" );
		itemOpcion1.setActionCommand( OPCION_1 );
		itemOpcion1.addActionListener( this );
		menuExtension.add( itemOpcion1 );

		itemOpcion2 = new JMenuItem( "Opción 2" );
		itemOpcion2.setActionCommand( OPCION_2 );
		itemOpcion2.addActionListener( this );
		menuExtension.add( itemOpcion2 );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Este método activa la opción para conectar y desactiva las opciones para
	 * desconectar y agregar un amigo
	 */
	public void activarConexion( )
	{
		itemConectar.setEnabled( true );
		itemDesconectar.setEnabled( false );
		itemAgregarAmigo.setEnabled( false );
	}

	/**
	 * Este método activa la opción para desconectar y desactiva la opciones para
	 * conectar y agregar un amigo
	 */
	public void activarDesconexion( )
	{
		itemConectar.setEnabled( false );
		itemDesconectar.setEnabled( true );
		itemAgregarAmigo.setEnabled( true );
	}

	/**
	 * Ejecuta una acción según la opción del menú que haya sido seleccionada
	 * 
	 * @param evento El evento de click en una de las opciones del menú
	 */
	public void actionPerformed( ActionEvent evento )
	{
		String comando = evento.getActionCommand( );

		if ( SALIR.equals( comando ) )
		{
			principal.iniciarFinalizacionAplicacion( );
		}
		else if ( CONECTAR.equals( comando ) )
		{
			principal.iniciarConexion( );
		}
		else if ( DESCONECTAR.equals( comando ) )
		{
			principal.terminarConexion( );
		}
		else if ( AGREGAR_AMIGO.equals( comando ) )
		{
			principal.agregarAmigo( );
		}
		else if ( OPCION_1.equals( comando ) )
		{
			principal.reqFuncOpcion1( );
		}
		else if ( OPCION_2.equals( comando ) )
		{
			principal.reqFuncOpcion2( );
		}
	}

}