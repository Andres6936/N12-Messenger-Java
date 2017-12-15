package edu.jabs.messenger.interfazCliente;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import edu.jabs.messenger.cliente.Usuario;

/**
 * Es el panel donde se muestran los amigos del usuario
 */
public class PanelAmigos extends JPanel implements ActionListener
{

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Comando para el botón que abre una conversación
	 */
	private final static String CONVERSAR = "CONVERSAR";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la clase principal de la interfaz
	 */
	private InterfazClienteMessengerAmigos ventanaPrincipal;

	// -----------------------------------------------------------------
	// Atributos de la interfaz
	// -----------------------------------------------------------------

	/**
	 * Es la lista donde se muestran los amigos
	 */
	private JList listaAmigos;

	/**
	 * Es el botón en el que se hace click para abrir una conversación
	 */
	private JButton botonConversacion;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el panel
	 * 
	 * @param ventana La ventana dentro de la que se encuentra este panel - ventana
	 *            != null
	 */
	public PanelAmigos( InterfazClienteMessengerAmigos ventana )
	{
		ventanaPrincipal = ventana;
		setLayout( new BorderLayout( ) );

		JScrollPane scroll = new JScrollPane( );
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

		listaAmigos = new JList( );
		listaAmigos.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

		scroll.getViewport( ).add( listaAmigos );
		add( scroll );

		botonConversacion = new JButton( "Abrir Conversación" );
		botonConversacion.setActionCommand( CONVERSAR );
		botonConversacion.addActionListener( this );
		add( botonConversacion, BorderLayout.SOUTH );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Actualiza la lista de amigos mostrada
	 * 
	 * @param amigos La colección de amigos (Usuario) que se deben mostrar - amigos
	 *            != null
	 */
	public void actualizarAmigos( Collection amigos )
	{
		listaAmigos.setListData( amigos.toArray( ) );
	}

	/**
	 * El método que se llama cuando se hace click sobre el botón
	 * 
	 * @param evento El evento del click sobre el botón
	 */
	public void actionPerformed( ActionEvent evento )
	{
		String comando = evento.getActionCommand( );

		if ( CONVERSAR.equals( comando ) )
		{
			Usuario amigo = (Usuario) listaAmigos.getSelectedValue( );
			if ( amigo != null )
			{
				ventanaPrincipal.iniciarConversacion( amigo.darNombre( ) );
			}
		}
	}
}