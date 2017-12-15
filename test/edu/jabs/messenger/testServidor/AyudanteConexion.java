/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: AyudanteConexion.java,v 1.1 2006/11/09 03:13:57 f-vela Exp $ 
 * Universidad de los Andes (Bogot� - Colombia)
 * Departamento de Ingenier�a de Sistemas y Computaci�n 
 * Todos los derechos reservados 2005 
 *
 * Proyecto Cupi2 
 * Ejercicio: n12_messengerAmigos 
 * Autor: Mario S�nchez - 5/05/2006 
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ 
 */

package edu.jabs.messenger.testServidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Esta clase sirve para crear un socket y esperar una conexi�n desde un thread diferente al principal
 */
public class AyudanteConexion extends Thread
{
    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Es el socket usado para la conexi�n
     */
    private Socket socket;

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Este m�todo crea espera una conexi�n desde un hilo diferente al principal
     */
    public void run( )
    {
        try
        {
            ServerSocket ssocket = new ServerSocket( SimuladorServidor.PUERTO_SERVIDOR );
            socket = ssocket.accept( );
            ssocket.close( );
        }
        catch( IOException e )
        {
            e.printStackTrace( );
        }
    }

    /**
     * Retorna el socket con la conexi�n que se cre�
     * @return socket
     */
    public Socket darSocket( )
    {
        return socket;
    }
}
