package edu.jabs.messenger.interfazCliente;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Este es el panel desde donde se envían los mensajes de una conversación
 */
public class PanelEnviarMensajes extends JPanel implements ActionListener
{

	// -----------------------------------------------------------------
	// Constantes
	// -----------------------------------------------------------------

	/**
	 * Comando para el botón de Enviar un mensaje
	 */
	private final static String ENVIAR = "ENVIAR";

	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es una referencia a la ventana de la cual hace parte este panel
	 */
	private VentanaConversacion ventana;

	// -----------------------------------------------------------------
	// Atributos de la Interfaz
	// -----------------------------------------------------------------

	/**
	 * Es la zona donde se escriben los mensajes para enviar
	 */
	private JTextArea txtMensaje;

	/**
	 * Es el botón usado para enviar un mensaje
	 */
	private JButton botonEnviar;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Construye el panel e inicializa sus componentes
	 * 
	 * @param ventanaConv Es la ventana a la que pertenece este panel - ventanaConv
	 *            != null
	 */
	public PanelEnviarMensajes( VentanaConversacion ventanaConv )
	{
		ventana = ventanaConv;

		setLayout( new BorderLayout( ) );
		JScrollPane scroll = new JScrollPane( );
		txtMensaje = new JTextArea( );
		txtMensaje.setWrapStyleWord( true );
		txtMensaje.setLineWrap( true );
		scroll.getViewport( ).add( txtMensaje );

		add( scroll );

		botonEnviar = new JButton( "Enviar" );
		botonEnviar.setActionCommand( ENVIAR );
		botonEnviar.addActionListener( this );
		add( botonEnviar, BorderLayout.EAST );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Este método se ejecuta cuando se hace click en el botón para enviar un
	 * mensaje
	 * 
	 * @param evento El evento de click sobre el botón
	 */
	public void actionPerformed( ActionEvent evento )
	{
		String comando = evento.getActionCommand( );

		if ( ENVIAR.equals( comando ) )
		{
			String mensaje = txtMensaje.getText( ).trim( );
			if ( mensaje.length( ) > 0 )
			{
				ventana.enviarMensaje( mensaje );
				txtMensaje.setText( "" );
			}
		}
	}

}
