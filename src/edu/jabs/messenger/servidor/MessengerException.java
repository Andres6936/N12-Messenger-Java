package edu.jabs.messenger.servidor;

/**
 * Esta excepción se utiliza para avisar que se produjeron errores específicos a
 * la aplicación
 */
public class MessengerException extends Exception
{
	/**
	 * Construye la excepción
	 * 
	 * @param mensaje El mensaje que describe el problema que se presentó - mensaje
	 *            != null
	 */
	public MessengerException( String mensaje )
	{
		super( mensaje );
	}
}
