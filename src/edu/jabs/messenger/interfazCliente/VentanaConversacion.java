package edu.jabs.messenger.interfazCliente;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;

import edu.jabs.messenger.cliente.Conversacion;

/**
 * Esta es la ventana en la cual se envían y se muestran los mensajes de una
 * conversación
 */
public class VentanaConversacion extends JFrame implements IVentanaConversacion
{
	// -----------------------------------------------------------------
	// Atributos
	// -----------------------------------------------------------------

	/**
	 * Es la conversación asociada a esta ventana
	 */
	private Conversacion conversacion;

	// -----------------------------------------------------------------
	// Atributos de la Interfaz
	// -----------------------------------------------------------------

	/**
	 * Es el panel donde se muestran los mensajes de la conversación
	 */
	private PanelMensajesConversacion panelMensajes;

	/**
	 * Es el panel desde donde se envían los mensajes de la conversación
	 */
	private PanelEnviarMensajes panelEnviarMensajes;

	// -----------------------------------------------------------------
	// Constructores
	// -----------------------------------------------------------------

	/**
	 * Inicializa la ventana
	 * 
	 * @param conv Es la conversación asociada a la ventana - conv != null
	 */
	public VentanaConversacion( Conversacion conv )
	{
		conversacion = conv;
		conv.cambiarVentanaConversacion( this );
		setTitle( "Hablando con " + conv.darNombreAmigo( ) );
		setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

		setLayout( new GridBagLayout( ) );
		GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 0.7, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets( 5, 5, 5, 5 ), 0, 0 );
		panelMensajes = new PanelMensajesConversacion( );
		getContentPane( ).add( panelMensajes, gbc );

		gbc.gridy = 1;
		gbc.weighty = 0.3;
		panelEnviarMensajes = new PanelEnviarMensajes( this );
		getContentPane( ).add( panelEnviarMensajes, gbc );

		setSize( 250, 400 );
	}

	// -----------------------------------------------------------------
	// Métodos
	// -----------------------------------------------------------------

	/**
	 * Muestra un mensaje en la ventana
	 * 
	 * @param mensaje El mensaje que debe ser mostrado - mensaje != null
	 */
	public void publicarMensaje( String mensaje )
	{
		panelMensajes.agregarMensaje( mensaje );
	}

	/**
	 * Envía un mensaje al amigo y lo muestra
	 * 
	 * @param mensaje El mensaje que debe ser enviado y mostrado - mensaje != null
	 */
	public void enviarMensaje( String mensaje )
	{
		conversacion.enviarMensaje( mensaje );
	}

	/**
	 * Cierra la conversación y cierra la ventana <br>
	 * Si la ventana es cerrada por el usuario, entonces se cierra la conversación
	 * asociada también.
	 */
	public void dispose( )
	{
		if ( !conversacion.conversacionTerminada( ) )
			conversacion.terminar( );

		else
			super.dispose( );
	}
}
