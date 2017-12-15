package edu.jabs.messenger.interfazCliente;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Este es el panel donde se muestran los mensajes recibidos en una conversación
 */
public class PanelMensajesConversacion extends JPanel
{

	// -----------------------------------------------------------------
	// Atributos de la Interfaz
	// -----------------------------------------------------------------

	/**
	 * Es el campo de texto donde se muestran los mensajes de la conversación
	 */
	private JTextArea txtMensajes;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el panel e inicializa sus componentes
	 */
	public PanelMensajesConversacion( )
	{
		setLayout( new BorderLayout( ) );

		txtMensajes = new JTextArea( );
		txtMensajes.setWrapStyleWord( true );
		txtMensajes.setLineWrap( true );
		txtMensajes.setEditable( false );

		JScrollPane scroll = new JScrollPane( );
		scroll.getViewport( ).add( txtMensajes );

		add( scroll );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Agrega un mensaje al final del área de mensajes
	 * 
	 * @param mensaje El mensaje que se va a mostrar - mensaje != null
	 */
	public void agregarMensaje( String mensaje )
	{
		txtMensajes.append( mensaje + "\n" );
		txtMensajes.setCaretPosition( txtMensajes.getText( ).length( ) );
	}
}