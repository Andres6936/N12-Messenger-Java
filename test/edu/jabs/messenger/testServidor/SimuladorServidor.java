/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: SimuladorServidor.java,v 1.2 2006/12/11 07:31:42 man-muno Exp $ 
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Collection;

import edu.jabs.messenger.servidor.IAdministradorAmigos;
import edu.jabs.messenger.servidor.IServidorAmigos;
import edu.jabs.messenger.servidor.Usuario;

/**
 * Esta clase simula ser un servidor para facilitar las pruebas a los m�todos de la clase ManejadorCliente
 */
public class SimuladorServidor implements IServidorAmigos
{
    // -----------------------------------------------------------------
    // Constantes
    // -----------------------------------------------------------------

    /**
     * Es el puerto en el que el servidor recibe las conexiones
     */
    public static final int PUERTO_SERVIDOR = 9999;

    /**
     * Es el nombre del cliente
     */
    public static final String NOMBRE_CLIENTE = "Alicia";

    /**
     * Es la direcci�n ip del cliente
     */
    public static final String IP_CLIENTE = "127.0.0.1";

    /**
     * Es el puerto en el que el cliente recibe las conexiones entrantes
     */
    public static final int PUERTO_CLIENTE = 9997;

    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Es el objeto que simula ser el AdministradorAmigos
     */
    private SimuladorAdministradorAmigos simuladorAdministrador;

    /**
     * Es el objeto que sirve para crear una conexi�n en un thread diferente al principal
     */
    private AyudanteConexion ayudante;

    /**
     * Es el socket que se supone est� del lado del servidor y que ser� usado por el manejadorCliente
     */
    private Socket socketServidor;

    /**
     * Es el socket que comunica al servidor con el cliente
     */
    private Socket socketCliente;

    /**
     * Es el stream usuado para leer los mensajes enviados al cliente
     */
    private BufferedReader inCliente;

    /**
     * Es el stream usado para enviar mensajes al servidor desde el supuesto cliente
     */
    private PrintWriter outCliente;

    /**
     * Es el �ltimo mensaje que fue recibido por el cliente
     */
    private String ultimoMensaje;

    /**
     * Indica si se envi� una notificaci�n de desconexi�n a los que conocen al usuario
     */
    private boolean enviarNotificacionDesconexion;

    /**
     * Indica si se envi� una notificaci�n de conexi�n a los que conocen al usuario
     */
    private boolean enviarNotificacionConexion;

    /**
     * Indica si se envi� una notificaci�n para iniciar una conversaci�n
     */
    private boolean enviarConversacionAmigo;

    // -----------------------------------------------------------------
    // Constructores
    // -----------------------------------------------------------------

    /**
     * Crea el servidor simulado y lo deja esperando una conexi�n en un thread aparte
     */
    public SimuladorServidor( )
    {
        simuladorAdministrador = new SimuladorAdministradorAmigos( );
        ayudante = new AyudanteConexion( );
        ayudante.start( );
    }

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Conecta el socketCliente al servidor
     * @return socketServidor
     * @throws IOException Se lanza esta excepci�n si hay problemas en la comunicaci�n
     */
    public Socket conectar( ) throws IOException
    {
        socketCliente = new Socket( "localhost", PUERTO_SERVIDOR );
        outCliente = new PrintWriter( socketCliente.getOutputStream( ), true );
        inCliente = new BufferedReader( new InputStreamReader( socketCliente.getInputStream( ) ) );

        while( socketServidor == null )
            socketServidor = ayudante.darSocket( );

        outCliente.println( IServidorAmigos.LOGIN + ":" + NOMBRE_CLIENTE + ";" + IP_CLIENTE + ";" + PUERTO_CLIENTE );

        return socketServidor;
    }

    /**
     * Cierra la conexi�n desde el lado del cliente
     * @throws IOException Se lanza esta excepci�n si hay problemas en la comunicaci�n
     */
    public void terminarConexion( ) throws IOException
    {
        outCliente.close( );
        inCliente.close( );
        socketCliente.close( );
    }

    /**
     * Simula el env�o desde el cliente de un mensaje de LOGOUT
     */
    public void enviarLogout( )
    {
        outCliente.println( IServidorAmigos.LOGOUT );
    }

    /**
     * Lee un mensaje enviado hacia el cliente
     * @throws IOException Se lanza esta excepci�n si hay problemas en la comunicaci�n
     */
    public void recibirMensaje( ) throws IOException
    {
        ultimoMensaje = inCliente.readLine( );
    }

    /**
     * Retorna el �ltimo mensaje recibido
     * @return ultimoMensaje
     */
    public String darUltimoMensaje( )
    {
        return ultimoMensaje;
    }

    /**
     * Retorna el objeto que simula ser el AdministradorAmigos
     */
    public IAdministradorAmigos darAministradorAmigos( )
    {
        return simuladorAdministrador;
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public void desconectarDB( ) throws SQLException
    {
    }

    /**
     * Inicia la recepci�n de conexiones
     */
    public void recibirConexiones( )
    {
        ayudante.start( );
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public void cerrarConexion( ) throws IOException
    {
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public Collection darClientes( )
    {
        return null;
    }

    /**
     * La implementaci�n de este m�todo registra que se envi� una notificaci�n con el estado del usuario
     * @param usuario El nombre del usuario al que se le va a enviar la notificaci�n
     * @param estadoAmigo El estado que va a ser informado
     */
    public void enviarNotificacionAmigo( String usuario, Usuario estadoAmigo )
    {
        if( estadoAmigo.darEstado( ) == Usuario.ONLINE )
            enviarNotificacionConexion = true;
        else
            enviarNotificacionDesconexion = true;
    }

    /**
     * Indica si se envi� la notificaci�n de desconexi�n
     * @return enviarNotificacionDesconexion
     */
    public boolean envioNotificacionDesconexion( )
    {
        return enviarNotificacionDesconexion;
    }

    /**
     * Indica si se envi� la notificaci�n de conexi�n
     * @return enviarNotificacionConexion
     */
    public boolean envioNotificacionConexion( )
    {
        return enviarNotificacionConexion;
    }

    /**
     * La implementaci�n de este m�todo verifica que se haya enviado al amigo una notificaci�n para crear una conversaci�n
     * @param usuario El nombre del usuario al que se le va a enviar la notificaci�n
     * @param amigo El nombre del amigo que invit� al usuario a la conversaci�n
     * @param direccionIp La direcci�n ip a la que deber�a conectarse el usuario
     * @param puerto El puerto al que deber�a conectarse el usuario
     */
    public void iniciarConversacion( String usuario, String amigo, String direccionIp, int puerto )
    {
        if( amigo.equals( SimuladorServidor.NOMBRE_CLIENTE ) && usuario.equals( "Alicia" ) )
            enviarConversacionAmigo = true;
    }

    /**
     * Indica si se envi� el mensaje al amigo para iniciar la convesaci�n
     * @return enviarConversacionAmigo
     */
    public boolean envioConversacionAmigo( )
    {
        return enviarConversacionAmigo;
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public void desconectarCliente( String usuario )
    {
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public String metodo1( )
    {
        return null;
    }

    /**
     * Este m�todo no es relevante para las pruebas as� que no se implementa
     */
    public String metodo2( )
    {
        return null;
    }

}
