/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * $Id: SimuladorAdministradorAmigos.java,v 1.2 2006/12/11 07:31:42 man-muno Exp $ 
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import edu.jabs.messenger.servidor.IAdministradorAmigos;
import edu.jabs.messenger.servidor.Usuario;

/**
 * Esta clase simula ser un AdministradorAmigos para facilitar las pruebas de la clase ManejadorCliente
 */
public class SimuladorAdministradorAmigos implements IAdministradorAmigos
{
    // -----------------------------------------------------------------
    // Atributos
    // -----------------------------------------------------------------

    /**
     * Indica que el manejador indic� que el usuario est� conectado
     */
    private boolean usuarioConectado;

    /**
     * Indica que el manejador agreg� un amigo
     */
    private boolean amigoAgregado;

    // -----------------------------------------------------------------
    // M�todos
    // -----------------------------------------------------------------

    /**
     * Indica si el usuario est� conectado
     * @return usuarioConectado
     */
    public boolean usuarioConectado( )
    {
        return usuarioConectado;
    }

    /**
     * Indica si se agreg� un amigo al usuario
     * @return amigoAgregado
     */
    public boolean amigoAgregado( )
    {
        return amigoAgregado;
    }

    /**
     * Este m�todo no es necesario para las pruebas as� que no es implementado
     */
    public void conectarABD( ) throws SQLException, Exception
    {
    }

    /**
     * Este m�todo no es necesario para las pruebas as� que no es implementado
     */
    public void desconectarBD( ) throws SQLException
    {
    }

    /**
     * Este m�todo no es necesario para las pruebas as� que no es implementado
     */
    public void inicializarTablas( ) throws SQLException
    {
    }

    /**
     * Este m�todo no es necesario para las pruebas as� que no es implementado
     */
    public void crearUsuario( String usuario ) throws SQLException
    {
    }

    /**
     * La implementaci�n de este m�todo registra que se cre� un amigo para el usuario
     * @param usuario Es el usuario al que se le va a agregar el amigo
     * @param amigo Es el nombre del amigo
     * @throws SQLException Esta excepci�n no se lanza
     */
    public void agregarAmigo( String usuario, String amigo ) throws SQLException
    {
        amigoAgregado = true;
    }

    /**
     * La implementaci�n de este m�todo registra el cambio en el estado del usuario
     * @param usuario El usuario al que se le est� cambiando el estado
     * @param estado El nuevo estado
     * @param ip La nueva direcci�n ip
     * @param puerto El nuevo puerto
     * @throws SQLException Esta excepci�n no se lanza
     */
    public void cambiarEstadoUsuario( String usuario, String estado, String ip, int puerto ) throws SQLException
    {
        if( estado == "ONLINE" )
            usuarioConectado = true;
        else
            usuarioConectado = false;
    }

    /**
     * La implementaci�n de este m�todo retorna algunos amigos predefinidos para el usuario
     * @param usuario El nombre del usuario
     * @throws SQLException Esta excepci�n no se lanza
     */
    public Collection darAmigos( String usuario ) throws SQLException
    {
        Usuario alicia = new Usuario( "Alicia", "127.0.0.1", 9998 );
        Usuario belisario = new Usuario( "Belisario", "127.0.0.2", 9998 );
        Usuario carlos = new Usuario( "Carlos", "127.0.0.3", 9998 );

        ArrayList amigos = new ArrayList( 3 );
        amigos.add( alicia );
        amigos.add( belisario );
        amigos.add( carlos );

        return amigos;
    }

    /**
     * La implementaci�n de este m�todo retorna algunas personas predefinidas que conocen al usuario
     * @param nombreUsuario El nombre del usuario
     * @throws SQLException Esta excepci�n no se lanza
     */
    public Collection darPersonasConocen( String nombreUsuario ) throws SQLException
    {
        Usuario alicia = new Usuario( "Alicia", "127.0.0.1", 9998 );
        Usuario belisario = new Usuario( "Belisario", "127.0.0.2", 9998 );
        Usuario carlos = new Usuario( "Carlos", "127.0.0.3", 9998 );
        Usuario david = new Usuario( "David", "127.0.0.4", 9998 );

        ArrayList conocen = new ArrayList( 3 );
        conocen.add( alicia );
        conocen.add( belisario );
        conocen.add( carlos );
        conocen.add( david );

        return conocen;
    }

    /**
     * La implementaci�n de este m�todo retorna un estado fijo para el usuario
     * @param nombre El nombre del usuario para el que se quiere conocer el estado
     * @throws SQLException Esta excepci�n no se lanza
     */

    public Usuario darEstadoUsuario( String nombre ) throws SQLException
    {
        return new Usuario( nombre, "127.0.0.1", 9998 );
    }

    /**
     * La implementaci�n de este m�todo verifica que el usuario no sea uno de los amigos o conocidos predefinidos
     * @param usuario El nombre del usuario que se est� buscando
     * @throws SQLException Esta excepci�n no se lanza
     */

    public boolean existeUsuario( String usuario ) throws SQLException
    {
        return usuario.equals( SimuladorServidor.NOMBRE_CLIENTE ) || usuario.equals( "Alicia" ) || usuario.equals( "Belisario" ) || usuario.equals( "Carlos" ) || usuario.equals( "David" );
    }

    /**
     * La implementaci�n de este m�todo siempre retorna false
     * @param usuario El usuario al cual se le va a buscar un amigo
     * @param amigo El nombre del amigo que se va a buscar
     * @return false
     * @throws SQLException Esta excepci�n no se lanza
     */

    public boolean existeAmigo( String usuario, String amigo ) throws SQLException
    {
        return false;
    }

}
