/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: ServidorAmigosTest.java,v 1.2 2006/12/11 07:31:42 man-muno Exp $
 * Universidad de los Andes (Bogot� - Colombia)
 * Departamento de Ingenier�a de Sistemas y Computaci�n 
 * Licenciado bajo el esquema Academic Free License version 2.1 
 *
 * Proyecto Cupi2 (http://cupi2.uniandes.edu.co)
 * Ejercicio: n12_messengerAmigos
 * Autor: Mario S�nchez - 21-abr-2006
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package edu.jabs.messenger.testServidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;

import edu.jabs.messenger.servidor.IServidorAmigos;
import edu.jabs.messenger.servidor.ServidorAmigos;
import edu.jabs.messenger.servidor.Usuario;
import junit.framework.TestCase;

/**
 * Esta es la clase usada para verificar que los m�todos de la clase ServidorAmigos est�n correctamente implementados
 */
public class ServidorAmigosTest extends TestCase
{
    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Es la clase donde se har�n las pruebas
     */
    private IServidorAmigos servidorAmigos;

    /**
     * Es el objeto usado para recibir las conexiones entrantes en un thread diferente al principal
     */
    private AyudanteServidor ayudante;

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Construye un servidor y lo inicializa para que empiece a recibir conexiones
     */
    private void setupEscenario1( )
    {
        try
        {
            servidorAmigos = new ServidorAmigos( "./test/data/servidor.properties" );
            ayudante = new AyudanteServidor( servidorAmigos );
            ayudante.start( );

            // Darle tiempo al servidor de empezar
            Thread.sleep( 300 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }
    }

    /**
     * Elimina los objetos y las conexiones creados durante la construcci�n del escenario 1
     */
    private void terminarEscenario1( )
    {
        try
        {
            servidorAmigos.cerrarConexion( );
            ayudante.interrupt( );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }
    }

    /**
     * Verifica que el escenario se construya y destruya correctamente
     */
    public void testServidorAmigos( )
    {
        setupEscenario1( );

        Collection clientes = servidorAmigos.darClientes( );
        assertEquals( "El n�mero de clientes deber�a ser 0", 0, clientes.size( ) );

        terminarEscenario1( );
    }

    /**
     * Verifica el m�todo darClientes
     */
    public void testDarClientes( )
    {
        setupEscenario1( );

        try
        {
            // Crear una conexi�n al servidor para que se cree un cliente
            Socket socketCliente = new Socket( "localhost", 9999 );
            PrintWriter out = new PrintWriter( socketCliente.getOutputStream( ), true );
            out.println( IServidorAmigos.LOGIN + ":cliente;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            Collection clientes = servidorAmigos.darClientes( );
            assertEquals( "El n�mero de clientes deber�a ser 1", 1, clientes.size( ) );
            String nombreCliente = ( String )clientes.iterator( ).next( );
            assertTrue( "El nombre del cliente est� equivocado", nombreCliente.startsWith( "cliente" ) );

            // Crear otra conexi�n al servidor para que se cree un cliente
            Socket socketCliente2 = new Socket( "localhost", 9999 );
            PrintWriter out2 = new PrintWriter( socketCliente2.getOutputStream( ), true );
            out2.println( IServidorAmigos.LOGIN + ":cliente2;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            clientes = servidorAmigos.darClientes( );
            assertEquals( "El n�mero de clientes deber�a ser 2", 2, clientes.size( ) );

            out.println( IServidorAmigos.LOGOUT );
            out.close( );
            socketCliente.close( );

            out2.println( IServidorAmigos.LOGOUT );
            out2.close( );
            socketCliente2.close( );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }

    /**
     * Verifica el m�todo desconectarCliente
     */
    public void testDesconectarCliente( )
    {
        setupEscenario1( );

        try
        {
            // Crear una conexi�n al servidor para que se cree un cliente
            Socket socketCliente = new Socket( "localhost", 9999 );
            PrintWriter out = new PrintWriter( socketCliente.getOutputStream( ), true );
            out.println( IServidorAmigos.LOGIN + ":cliente;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            Collection clientes = servidorAmigos.darClientes( );
            assertEquals( "El n�mero de clientes deber�a ser 1", 1, clientes.size( ) );
            String nombreCliente = ( String )clientes.iterator( ).next( );
            assertTrue( "El nombre del cliente est� equivocado", nombreCliente.startsWith( "cliente" ) );

            // Crear otra conexi�n al servidor para que se cree un cliente
            Socket socketCliente2 = new Socket( "localhost", 9999 );
            PrintWriter out2 = new PrintWriter( socketCliente2.getOutputStream( ), true );
            out2.println( IServidorAmigos.LOGIN + ":cliente2;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            clientes = servidorAmigos.darClientes( );
            assertEquals( "El n�mero de clientes deber�a ser 2", 2, clientes.size( ) );

            out.println( IServidorAmigos.LOGOUT );
            out.close( );
            socketCliente.close( );

            servidorAmigos.desconectarCliente( "cliente" );

            // Verificar que el cliente se haya eliminado
            clientes = servidorAmigos.darClientes( );
            assertEquals( "El n�mero de clientes deber�a ser 1", 1, clientes.size( ) );

            out2.println( IServidorAmigos.LOGOUT );
            out2.close( );
            socketCliente2.close( );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo enviarNotificacionAmigo realmente env�e la notificaci�n
     */
    public void testEnviarNotificacionAmigo( )
    {
        setupEscenario1( );

        try
        {
            // Crear una conexi�n al servidor para que se cree un cliente
            Socket socketCliente = new Socket( "localhost", 9999 );
            PrintWriter out = new PrintWriter( socketCliente.getOutputStream( ), true );
            BufferedReader in = new BufferedReader( new InputStreamReader( socketCliente.getInputStream( ) ) );
            out.println( IServidorAmigos.LOGIN + ":cliente;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            Usuario alicia = new Usuario( "Alicia" );
            servidorAmigos.enviarNotificacionAmigo( "cliente", alicia );

            // Revisar que la notificaci�n haya sido enviada al cliente
            String notificacion = in.readLine( );
            assertEquals( "La notificaci�n enviada est� equivocada", IServidorAmigos.OFFLINE + ":Alicia", notificacion );

            out.println( IServidorAmigos.LOGOUT );
            out.close( );
            socketCliente.close( );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo iniciarConversacion env�e correctamente el mensaje al cliente
     */
    public void testIniciarConversacion( )
    {
        setupEscenario1( );

        try
        {
            // Crear una conexi�n al servidor para que se cree un cliente
            Socket socketCliente = new Socket( "localhost", 9999 );
            PrintWriter out = new PrintWriter( socketCliente.getOutputStream( ), true );
            BufferedReader in = new BufferedReader( new InputStreamReader( socketCliente.getInputStream( ) ) );
            out.println( IServidorAmigos.LOGIN + ":cliente;127.0.0.1;9998" );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );

            servidorAmigos.iniciarConversacion( "cliente", "Alicia", "127.0.0.1", 9997 );

            // Revisar que la notificaci�n haya sido enviada al cliente
            String notificacion = in.readLine( );
            assertEquals( "La notificaci�n enviada est� equivocada", IServidorAmigos.INCIAR_CHARLA + ":Alicia;127.0.0.1;9997", notificacion );

            out.println( IServidorAmigos.LOGOUT );
            out.close( );
            socketCliente.close( );

            // Darle tiempo al servidor de procesar los mensajes
            Thread.sleep( 500 );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "No deber�a haber problemas: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }
}