package edu.jabs.messenger.interfazServidor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

/**
 * Es el panel donde se muestran los clientes que hay conectados actualmente al
 * servidor
 */
public class PanelClientes extends JPanel implements ActionListener
{
	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Comando para el botón Refrescar
	 */
	private static final String REFRESCAR = "Refrescar";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la ventana principal de la aplicación del servidor
	 */
	private InterfazServidorMessengerAmigos principal;

	// -----------------------------------------------------------------
	// Atributos de la Interfaz
	// -----------------------------------------------------------------

	/**
	 * Es la lista donde se muestran los clientes
	 */
	private JList listaClientes;

	/**
	 * Es el botón que se usa para refrescar la lista de clientes
	 */
	private JButton botonRefrescar;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Inicializa el panel
	 * 
	 * @param padre Es una referencia a la ventana principal del servidor
	 */
	public PanelClientes( InterfazServidorMessengerAmigos padre )
	{
		principal = padre;
		inicializarPanel( );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Inicializa los elementos dentro del panel
	 */
	private void inicializarPanel( )
	{
		setLayout( new BorderLayout( ) );

		JScrollPane scroll = new JScrollPane( );
		listaClientes = new JList( );
		scroll.getViewport( ).add( listaClientes );
		add( scroll, BorderLayout.CENTER );

		botonRefrescar = new JButton( "Refrescar" );
		botonRefrescar.addActionListener( this );
		botonRefrescar.setActionCommand( REFRESCAR );
		add( botonRefrescar, BorderLayout.EAST );

		setBorder( new TitledBorder( "Clientes en línea" ) );
	}

	/**
	 * Actualiza la lista mostrada de clientes
	 * 
	 * @param clientes Es una colección con la información (String) de los clientes
	 *            que hay conectados actualmente
	 */
	public void actualizarClientes( Collection clientes )
	{
		listaClientes.setListData( clientes.toArray( ) );
	}

	/**
	 * Es el método llamado cuando se hace click sobre el botón refrescar
	 * 
	 * @param evento Es el evento de click sobre el botón
	 */
	public void actionPerformed( ActionEvent evento )
	{
		String comando = evento.getActionCommand( );

		if ( REFRESCAR.equals( comando ) )
		{
			principal.actualizarClientes( );
		}
	}

}