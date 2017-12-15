/**
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: ManejadorClienteTest.java,v 1.2 2006/12/11 07:31:42 man-muno Exp $
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

import java.io.IOException;
import java.net.Socket;

import edu.jabs.messenger.servidor.IServidorAmigos;
import edu.jabs.messenger.servidor.ManejadorCliente;
import edu.jabs.messenger.servidor.Usuario;
import junit.framework.TestCase;

/**
 * Esta es la clase usada para verificar que los m�todos de la clase ManejadorCliente est�n correctamente implementados
 */
public class ManejadorClienteTest extends TestCase
{
    // -----------------------------------------------------------------
    // Constantes
    // -----------------------------------------------------------------

    /**
     * Indica el tiempo que se va a esperar cuando se realicen acciones en threads diferentes al principal
     */
    public static final long TIEMPO_ESPERA = 200;

    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Es el manejador sobre el que se realizar�n las pruebas
     */
    private ManejadorCliente manejador;

    /**
     * Es el objeto que simula ser un ServidorAmigos
     */
    private SimuladorServidor simuladorServidor;

    /**
     * Es el objeto que simula ser un AdministradorAmigos
     */
    private SimuladorAdministradorAmigos simuladorAdministrador;

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Construye un escenario en el cual un manejador est� conectado a un simulador del servidor
     */
    private void setupEscenario1( )
    {
        try
        {
            // Crear el simulador del servidor
            simuladorServidor = new SimuladorServidor( );
            simuladorAdministrador = ( SimuladorAdministradorAmigos )simuladorServidor.darAministradorAmigos( );

            // Darle tiempo al socket para prepararse
            Thread.sleep( TIEMPO_ESPERA );

            // Conectar el manejador
            Socket socket = simuladorServidor.conectar( );
            manejador = new ManejadorCliente( simuladorServidor, socket );

            // Iniciar el manejador
            manejador.iniciarManejador( );

            // Verificar que se hayan realizado las tareas que hay que hacer cuando se conecta un cliente
            // 1. Verificar que la bd se haya actualizado
            assertTrue( "El usuario no se conect� en la base de datos", simuladorAdministrador.usuarioConectado( ) );

            // 2. Verificar que se le haya notificado el estado a los que lo conocen
            assertTrue( "No se env�o la notificaci�n de conexi�n", simuladorServidor.envioNotificacionConexion( ) );

            // 3. Verificar que haya informado sobre el estado de los 3 amigos
            simuladorServidor.recibirMensaje( );
            String msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", msg.startsWith( IServidorAmigos.ONLINE ) || msg.startsWith( IServidorAmigos.OFFLINE ) );

            simuladorServidor.recibirMensaje( );
            msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", msg.startsWith( IServidorAmigos.ONLINE ) || msg.startsWith( IServidorAmigos.OFFLINE ) );

            simuladorServidor.recibirMensaje( );
            msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", msg.startsWith( IServidorAmigos.ONLINE ) || msg.startsWith( IServidorAmigos.OFFLINE ) );
        }
        catch( Exception e )
        {
            e.printStackTrace( );
            fail( "Se presentaron problemas creando el manejador y el servidor: " + e.getMessage( ) );
        }
    }

    /**
     * Elimina las conexiones y los objetos creados durante la construcci�n del escenario 1
     */
    private void terminarEscenario1( )
    {
        try
        {
            // Enviar un mensaje de LOGOUT
            simuladorServidor.enviarLogout( );
            simuladorServidor.recibirMensaje( );
            simuladorServidor.terminarConexion( );

            // Verificar que se haya enviado el mensaje DESCONEXION al cliente
            assertEquals( "El �ltimo mensaje enviado no fue el esperado", IServidorAmigos.DESCONEXION, simuladorServidor.darUltimoMensaje( ) );

            // Verificar que se haya notificado a los amigos del usuario
            assertTrue( "No se env�o la notificaci�n de desconexi�n", simuladorServidor.envioNotificacionDesconexion( ) );

            // Verificar que en la BD el usuario se haya desconectado
            assertFalse( "El usuario no se desconect� en la base de datos", simuladorAdministrador.usuarioConectado( ) );
        }
        catch( IOException e )
        {
            fail( "Se presentaron problemas terminando las conexiones: " + e.getMessage( ) );
        }
    }

    /**
     * Verifica que la inicializaci�n y finalizaci�n del escenario se realice correctamente
     */
    public void testManejadorCliente( )
    {
        setupEscenario1( );
        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo agregarAmigo realice las modificaciones adecuadas en la base de datos y que se env�e la notificaci�n con el estado del nuevo amigo
     */
    public void testAgregarAmigo( )
    {
        setupEscenario1( );

        try
        {
            // Agregar un amigo
            manejador.agregarAmigo( "Enrique" );

            // Verificar que en la BD se haya agregado el amigo
            assertTrue( "Se agreg� el amigo en la base de datos", simuladorAdministrador.amigoAgregado( ) );

            // Verificar que se haya enviado el estado del amigo
            simuladorServidor.recibirMensaje( );
            String msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", ( msg.startsWith( IServidorAmigos.ONLINE ) || msg.startsWith( IServidorAmigos.OFFLINE ) ) && msg.indexOf( "Enrique" ) != -1 );
        }
        catch( IOException e )
        {
            fail( "Se presentaron problemas agregando el amigo: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }

    /**
     * Verifica el m�todo darNombre
     */
    public void testDarNombre( )
    {
        setupEscenario1( );

        assertEquals( "El nombre est� equivocado", SimuladorServidor.NOMBRE_CLIENTE, manejador.darNombre( ) );

        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo enviarConvesacionAmigo env�e el mensaje correctamente al amigo a trav�s del servidor
     */
    public void testEnviarConversacionAmigo( )
    {
        setupEscenario1( );

        manejador.enviarConversacionAmigo( "Alicia" );

        // Verificar que el mensaje para la conversaci�n se haya mandado al amigo
        assertTrue( "No se envi� el mensaje para iniciar una conversaci�n", simuladorServidor.envioConversacionAmigo( ) );

        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo enviarEstadoAmigo env�e la informaci�no correcamente al cliente
     */
    public void testEnviarEstadoAmigo( )
    {
        setupEscenario1( );

        try
        {
            Usuario alicia = new Usuario( "Alicia" );
            manejador.enviarEstadoAmigo( alicia );

            // Verificar que se haya enviado el estado del amigo
            simuladorServidor.recibirMensaje( );
            String msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", msg.startsWith( IServidorAmigos.OFFLINE ) && msg.indexOf( "Alicia" ) != -1 );

            Usuario carlos = new Usuario( "Carlos", "127.0.0.1", 9998 );
            manejador.enviarEstadoAmigo( carlos );

            // Verificar que se haya enviado el estado del amigo
            simuladorServidor.recibirMensaje( );
            msg = simuladorServidor.darUltimoMensaje( );
            assertTrue( "No se recibi� el mensaje con el estado de un amigo", msg.startsWith( IServidorAmigos.ONLINE ) && msg.indexOf( "Carlos" ) != -1 );

        }
        catch( Exception e )
        {
            fail( "No deber�a haber problemas enviando el estado del amigo: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }

    /**
     * Verifica que el m�todo iniciarConversacion env�e el mensaje (INICIAR_CHARLA) correctamente a trav�s del servidor
     */
    public void testIniciarConversacion( )
    {
        setupEscenario1( );

        try
        {
            manejador.iniciarConversacion( "Alicia", "127.0.0.1", 8000 );

            // Verificar que se haya enviado el mensaje
            simuladorServidor.recibirMensaje( );
            String msg = simuladorServidor.darUltimoMensaje( );
            assertEquals( "No se recibi� el mensaje para iniciar la conversaci�n", IServidorAmigos.INCIAR_CHARLA + ":Alicia;127.0.0.1;8000", msg );
        }
        catch( Exception e )
        {
            fail( "No deber�a haber problemas enviando el estado del amigo: " + e.getMessage( ) );
        }

        terminarEscenario1( );
    }
}
