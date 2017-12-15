/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: AyudanteServidor.java,v 1.1 2006/11/09 03:13:57 f-vela Exp $ 
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

import edu.jabs.messenger.servidor.IServidorAmigos;

/**
 * Esta clase le permite al servidor recibir conexiones en un thread diferente al principal
 */
public class AyudanteServidor extends Thread
{
    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Es el servidor sobre el que se est�n realizando las pruebas
     */
    private IServidorAmigos servidor;

    // -----------------------------------------------------------------
    // Cosntructores
    // -----------------------------------------------------------------

    /**
     * Construye el ayudante, pero no inicia al servidor
     * @param servidorAmigos El servidor sobre el que se van a realizar las pruebas
     */
    public AyudanteServidor( IServidorAmigos servidorAmigos )
    {
        servidor = servidorAmigos;
    }

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Inicia la recepci�n de conexiones en el servidor
     */
    public void run( )
    {
        servidor.recibirConexiones( );
    }
}
